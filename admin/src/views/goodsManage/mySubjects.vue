<template>
  <div class="app-container">
    <el-form :inline="true" :model="searchForm" class="demo-form-inline">
      <el-form-item style="margin-bottom: 40px;" label="专题名称:">
        <el-input v-model="searchForm.subjectName" style="width: 200px;" />
      </el-form-item>
      <el-form-item style="margin-bottom: 40px;" label="创建时间:">
        <el-date-picker
          v-model="searchForm.time"
          type="daterange"
          align="right"
          unlink-panels
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :picker-options="pickerOptions"
        />
      </el-form-item>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="filterSubject()">
        筛选
      </el-button>
      <el-button class="filter-item" type="primary" icon="el-icon-plus" @click="handleAdd()">
        新增专题
      </el-button>
    </el-form>

    <el-table
      :key="tableKey"
      v-loading="listLoading"
      :data="list"
      border
      fit
      highlight-current-row
      style="width: 100%;"
    >
      <el-table-column label="专题名称" align="center" width="100">
        <template slot-scope="{row}">
          <span>{{ row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="专题图片" width="100px" align="center">
        <template slot-scope="{row}">
          <el-image
            :src="row.image.url"
            fit="scale-down"
          />
        </template>
      </el-table-column>
      <el-table-column label="专题类型" min-width="100px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.type }}</span>
        </template>
      </el-table-column>
      <el-table-column label="使用状态" width="100px" align="center">
        <template slot-scope="{row}">
          <el-tag :type="row.status === '上架' ? 'success' : 'warning'">
            {{ row.status === '上架' ? '使用中' : '未使用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="专题描述" width="150px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.describe }}</span>
        </template>
      </el-table-column>
      <el-table-column label="含商品数" width="80px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.goodsList.length }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="100" align="center">
        <template slot-scope="{row}">
          <span>{{ row.createTime | parseTime() }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="230" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <el-row>
            <el-button type="primary" size="mini" @click="handleToggle(row)">
              {{ row.status === "上架"?"下架":"上架" }}
            </el-button>
            <el-button type="primary" size="mini" @click="handleEdit(row)">
              编辑
            </el-button>
          </el-row>
          <el-row style="margin-top:20px">
            <el-button size="mini" type="success" @click="manageGoods(row)">
              管理商品
            </el-button>
            <el-button size="mini" type="danger" @click="handleDelete(row)">
              删除
            </el-button>
          </el-row>
        </template>
      </el-table-column>
    </el-table>

    <div style="float: right;margin: 30px 20px 0 0;">
      <el-pagination
        background
        :current-page="currentPage"
        :page-sizes="[10, 20, 30, 40]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="editVisible">
      <el-form ref="temp" :model="temp" label-position="right" label-width="120px" :rules="editRules">
        <el-form-item label="专题名称" prop="name">
          <el-input v-model="temp.name" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="专题描述" prop="describe">
          <el-input v-model="temp.describe" type="textarea" />
        </el-form-item>
        <el-form-item label="专题图片:" prop="image">
          <el-upload
            class="subject-uploader"
            action="/api/admin/uploadImage"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :headers="{token:$store.getters.token}"
            accept="image/png, image/jpeg"
            :data="{dir:'subjectImage'}"
            :on-success="handleSuccess"
          >
            <img v-if="hasImage" :src="temp.image.url" class="subject">
            <i v-else class="el-icon-plus subject-uploader-icon" />
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="dialogStatus==='create'?creatConfirm():editConfirm()">
          确认
        </el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
// import waves from '@/directive/waves' // waves directive
// import Pagination from '@/components/Pagination' // secondary package based on el-pagination

export default {
  name: 'MySubjects',
  // components: { Pagination },
  // directives: { waves },
  data() {
    var validateImage = (rule, value, callback) => {
      if (this.temp.image.url === undefined) {
        callback(new Error('专题图片为必选项'))
      } else {
        callback()
      }
    }
    return {
      tableKey: 0,
      listLoading: true,

      currentPage: 1,
      pageSize: 10,

      activeNames: ['1', '2'],
      searchForm: {
        subjectName: '',
        time: null
      },
      dialogStatus: '',
      temp: {
        id: '',
        name: '',
        image: '',
        type: '',
        status: '',
        describe: '',
        goodsList: [],
        createTime: '',
        lastUpdateTime: ''
      },
      editVisible: false,
      textMap: {
        update: '编辑',
        create: '新增'
      },
      hasImage: false,
      editRules: {
        name: [{ required: true, message: '专题名称为必选项', trigger: 'blur' }],
        describe: [{ required: true, message: '专题描述为必选项', trigger: 'blur' }],
        image: [{ required: true, validator: validateImage, trigger: 'blur' }]
      },
      downloadLoading: false,
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            picker.$emit('pick', [start, end])
          }
        }]
      },
      list: []
    }
  },
  computed: {
    allList() {
      return this.$store.getters.subjectList
    },
    total: {
      get: function() {
        return this.allList.length
      },
      set: function(val) {}
    }
  },
  created() {
    this.$store.dispatch('subject/getSubjectByMallID').then(() => {
      this.listLoading = false
      this.list = this.allList.slice(0, this.pageSize)
      this.total = this.allList.length
    })
  },
  methods: {
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.list = this.allList.slice(0, val)
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.list = this.allList.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
    },
    handleSuccess(res, file) {
      const { data } = res
      this.temp.image = data
      this.hasImage = true
    },
    beforeUpload() {

    },
    filterSubject() {
      var form = this.$deepCopy(this.searchForm)
      // console.log(this.searchForm.time)
      // console.log(form.time)
      if (this.searchForm.time !== null) {
        var t0 = new Date()
        t0.setTime(this.searchForm.time[0].getTime())
        var t1 = new Date()
        t1.setTime(this.searchForm.time[1].getTime())
        t0.setUTCHours(23)
        t0.setUTCMinutes(59)
        t0.setUTCSeconds(59)
        t0.setUTCMilliseconds(999)

        t1.setTime(t1.getTime() + 3600 * 1000 * 24)
        t1.setUTCHours(23)
        t1.setUTCMinutes(59)
        t1.setUTCSeconds(59)
        t1.setUTCMilliseconds(999)
        form.time[0] = t0
        form.time[1] = t1
      }
      this.listLoading = true
      this.$store.dispatch('subject/filterSubject', form).then(() => {
        this.listLoading = false
        this.$notify({
          title: 'Success',
          message: '查询成功',
          type: 'success',
          duration: 2000
        })
        this.currentPage = 1
        this.list = this.allList.slice(0, this.pageSize)
      })
    },
    resetTemp() {
      this.temp = {
        id: '提交后自动生成',
        name: '',
        image: '',
        type: '商品专题',
        status: '上架',
        describe: '',
        goodsList: [],
        createTime: '提交后自动生成',
        lastUpdateTime: '提交后自动生成'
      }
    },
    manageGoods(row) {
      this.$store.dispatch('subject/setCurSubjectID', row.eid).then(() => {
        this.$router.push({ path: '/goodsManage/subjectGoods' })
      })
    },
    creatConfirm() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          this.$store.dispatch('subject/createSubject', this.temp).then(() => {
            this.$notify({
              title: 'Success',
              message: '新增专题成功',
              type: 'success',
              duration: 2000
            })
            this.editVisible = false
            this.list = this.allList.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    handleAdd() {
      this.dialogStatus = 'create'
      this.resetTemp()
      this.editVisible = true
      this.hasImage = false
      this.$nextTick(() => {
        this.$refs['temp'].clearValidate()
      })
    },
    handleToggle(row) {
      console.log(row)
      this.$store.dispatch('subject/setCurSubjectID', row.eid).then(() => {
        console.log(this.$store)
        row.status = row.status === '上架' ? '下架' : '上架'
        this.$store.dispatch('subject/updateSubject', row).then(() => {
        // row.status === '下架'
          this.$notify({
            title: 'Success',
            message: row.status + '成功',
            type: 'success',
            duration: 2000
          })
          this.list = this.allList.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
        })
      })
    },
    handleEdit(row) {
      this.$store.dispatch('subject/setCurSubjectID', row.eid).then(() => {
        this.dialogStatus = 'update'
        this.temp = Object.assign({}, row) // copy obj
        this.editVisible = true
        this.hasImage = true
        this.$nextTick(() => {
          this.$refs['temp'].clearValidate()
        })
      })
    },
    editConfirm() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          this.$store.dispatch('subject/updateSubject', this.temp).then(() => {
            const tempData = Object.assign({}, this.temp)
            const index = this.allList.findIndex((v, i, arr) => {
              if (this.temp.eid === v.eid) {
                return true
              }
            })
            this.allList.splice(index, 1, tempData)
            this.editVisible = false
            this.$notify({
              title: 'Success',
              message: '主题更新成功',
              type: 'success',
              duration: 2000
            })
            this.list = this.allList.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    handleDelete(row) {
      const index = this.allList.findIndex((v, i, arr) => {
        if (row.eid === v.eid) {
          return true
        }
      })
      this.$store.dispatch('subject/setCurSubjectID', row.eid).then(() => {
        this.$store.dispatch('subject/deleteSubject', { index: index }).then(() => {
          this.$notify({
            title: 'Success',
            message: '主题删除成功',
            type: 'success',
            duration: 2000
          })
          this.list = this.allList.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
        })
      })
    }
  }
}
</script>

<style>
.subject-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .subject-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .subject-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 370px;
    height: 210px;
    line-height: 178px;
    text-align: center;
  }
  .subject {
    width: 370px;
    height: 210px;
    display: block;
  }
</style>
