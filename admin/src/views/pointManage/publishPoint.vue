<template>
  <div class="app-container">
    <!-- <div class="filter-container" style="margin-bottom: 40px;">
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-plus" @click="handleCreate">
        新增
      </el-button>
    </div> -->
    <div v-if="point === undefined || point === null" style="margin-left:10px">
      <h3>当前商城尚未配置积分(需配置积分才能进行后续操作)</h3>
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-plus" @click="handleCreate">
        新增
      </el-button>
    </div>
    <div v-if="point !== undefined && point !== null" style="width:470px;margin-left:10px">
      <h3>当前商城货币：</h3>
      <el-card :body-style="{ padding: '20px',width:'470px',height:'140px'}" @click.native="select()">
        <el-col :span="9">
          <img :src="point.logo.url" style="width:100px;height:100px;object-fit:cover;" class="image">
        </el-col>
        <div style="padding-top: 4px;">
          <div style="font-size:50px;font-weight:10px">{{ point.name }}</div>
          <div style="margin-top:20px;color: #6d6b6b;font-weight: 450;">{{ point.name }} : 人民币 = {{ point.exchangeRate }} : 1</div>
        </div>
      </el-card>
      <el-dialog title="删除确认" :visible.sync="popVisible">
        <p>你确定要删除这个商城货币吗?</p>
        <div style="text-align: right; margin: 0">
          <el-button size="mini" type="text" @click="popVisible = false">取消</el-button>
          <el-button type="primary" size="mini" @click="deleteConfirm()">确定</el-button>
        </div>
      </el-dialog>
    </div>
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="ruleForm" :rules="rules" :model="ruleForm" label-position="right" label-width="130px" style="width: 400px; margin-left:50px;">
        <el-form-item label="积分名称:" prop="name">
          <el-input v-model="ruleForm.name" />
        </el-form-item>
        <el-form-item label="积分汇率:" prop="exchangeRate">
          <span>{{ ruleForm.name }} : 人民币 = </span>
          <el-input v-model.number="ruleForm.exchangeRate" type="number" :disabled="ruleForm.fiexdRate" style="width:100px" />
          <span> = 1</span>
        </el-form-item>
        <el-form-item label="固定积分汇率:">
          <span>{{ ruleForm.fiexdRate === true ? "是":"否" }}</span>
        </el-form-item>
        <el-form-item label="积分logo:" prop="logo">
          <el-upload
            action="/api/admin/uploadImage"
            class="avatar-uploader"
            :headers="{token:$store.getters.token}"
            accept="image/png, image/jpeg"
            :on-success="uploadSuccess"
            :data="{dir:'pointImage'}"
            :show-file-list="false"
          >
            <img v-if="ruleForm.logo.url" :src="ruleForm.logo.url" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon" />
            <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过1M</div>
          </el-upload>
          <el-dialog :visible.sync="dialogVisible" size="tiny">
            <img width="100%" :src="dialogImageUrl" alt="">
          </el-dialog>
        </el-form-item>
        <el-form-item label="创建时间:">
          <span>{{ ruleForm.createTime | parseTime }}</span>
        </el-form-item>
        <el-form-item label="有效期至:">
          <el-radio v-model="ruleForm.ddl" label="无">永久有效</el-radio>
          <!-- <span>{{ ruleForm.ddl === "无" ? "永久有效": ruleForm.ddl }}</span> -->
        </el-form-item>
        <el-form-item label="积分总量:">
          <el-radio v-model="ruleForm.total" label="无">无上限</el-radio>
          <!-- <span>{{ ruleForm.total === "无" ? "无上限" : ruleForm.total }}</span> -->
        </el-form-item>
        <el-form-item label="积分发行方:">
          <span>{{ ruleForm.publisher }}</span>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-if=" dialogStatus==='create'" @click="dialogFormVisible = false">
          取消
        </el-button>
        <!-- <el-button v-if=" dialogStatus==='update'" type="danger" @click="onCickDelete()">
          删除
        </el-button> -->
        <el-button type="primary" @click="dialogStatus==='create'?creatPoint():updatePoint()">
          确认
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// import { fetchList, fetchPv, createArticle, updateArticle } from '@/api/article'
// import waves from '@/directive/waves' // waves directive
// import Pagination from '@/components/Pagination' // secondary package based on el-pagination

export default {
  name: 'PublishPoint',
  // components: { Pagination },
  // directives: { waves },
  data() {
    return {
      dialogImageUrl: '',
      dialogVisible: false,
      listLoading: true,
      ruleForm: {
        // id: '提交后自动生成',
        name: '',
        exchangeRate: 1,
        fixedRate: true,
        logo: '',
        createTime: '提交后自动生成',
        ddl: '无',
        total: '无',
        publisher: '提交后自动生成',
        image: {}
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '新增'
      },
      rules: {
        name: [{ required: true, message: '积分名称为必填项', trigger: 'blur' }],
        exchangeRate: [{ required: true, message: '积分名称为必填项', trigger: 'blur' }],
        logo: [{ required: true, message: 'logo为必填项', trigger: 'blur' }]
      },
      popVisible: false
    }
  },
  computed: {
    point() {
      return this.$store.getters.point
    }
  },
  created() {
    this.resetRuleForm()
  },
  methods: {
    uploadSuccess(resp) {
      const { data } = resp
      this.ruleForm.logo = data
    },
    resetRuleForm() {
      this.ruleForm = {
        // id: '提交后自动生成',
        name: '',
        exchangeRate: 1,
        fixedRate: true,
        logo: '',
        createTime: '提交后自动生成',
        ddl: '无',
        total: '无',
        publisher: '提交后自动生成',
        image: {}
      }
    },
    select() {
      this.ruleForm = Object.assign({}, this.point)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true

      this.$nextTick(() => {
        this.$refs['ruleForm'].clearValidate()
      })
    },
    handleCreate() {
      this.resetRuleForm()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['ruleForm'].clearValidate()
      })
    },
    creatPoint() {
      this.$refs['ruleForm'].validate((valid) => {
        if (valid) {
          this.$store.dispatch('point/updatePoint', this.ruleForm).then(() => {
            this.dialogFormVisible = false
            this.$notify({
              title: 'Success',
              message: 'Created Successfully',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
    },
    updatePoint() {
      const that = this
      this.$refs['ruleForm'].validate((valid) => {
        if (valid) {
          that.dialogFormVisible = false
          that.$store.dispatch('point/updatePoint', that.ruleForm).then(() => {
            that.dialogFormVisible = false
            that.$notify({
              title: 'Success',
              message: 'Update Successfully',
              type: 'success',
              duration: 2000
            })
            console.log(this.$store.getters.point, this.$store)
          }).catch(() => {})
        }
      })
    },
    onCickDelete() {
      this.popVisible = true
    },
    deleteConfirm() {
      const that = this
      this.$store.dispatch('point/deletePoint').then(() => {
        that.popVisible = false
        that.dialogFormVisible = false
        that.$notify({
          title: 'Success',
          message: 'Delete Successfully',
          type: 'success',
          duration: 2000
        })
      }).catch(() => {})
    }
  }
}
</script>

<style>
  .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
    object-fit:cover;
  }
</style>
