<template>
  <div class="app-container">
    <div v-if="$store.getters.point === null">
      <h3>当前商城尚未配置积分</h3>
    </div>
    <div v-if="$store.getters.point !== null">
      <el-tabs v-model="activeName">
        <el-tab-pane label="积分发放" name="first">
          <div style="margin:0 0 0 30px">
            <el-tabs tab-position="left" style="height: 200px;margin-top:40px">
              <el-tab-pane label="单个发放">
                <el-form ref="form" label-position="right" :model="disForm" label-width="130px">
                  <el-form-item label="输入用户手机号:">
                    <el-input v-model.number="disForm.phone" style="width:280px" />
                  </el-form-item>
                  <el-form-item label="积分发放数量:">
                    <el-input-number v-model="disForm.num" style="width:180px" controls-position="right" :min="1" label="描述文字" />
                    <span>{{ $store.getters.point.name }} (支持小数位发放)</span>
                  </el-form-item>
                  <el-form-item>
                    <el-button :disabled="disForm.phone===''" type="primary" @click="distributePointOne()">确认发放</el-button>
                  </el-form-item>
                </el-form>
              </el-tab-pane>
              <el-tab-pane label="批量发放">
                <div style="margin-left:40px">
                  <el-row style="margin: 10px 0px 0px 22px;">
                    <el-link type="primary" class="download" href="demo.txt" download="" title="下载">批量发放模板下载</el-link>
                  </el-row>
                  <el-row style="margin-top:40px;max-width:180px">
                    <el-upload
                      class="upload-demo"
                      action="/api/admin/uploadFile"
                      :limit="3"
                      :file-list="fileList"
                      :data="{mallID:$store.getters.curMallID}"
                      accept="text/plain"
                      :on-success="onSuccess"
                    >
                      <el-button type="primary">上传模板并完成发放</el-button>
                      <div slot="tip" class="el-upload__tip">一次只能上传一个txt文件</div>
                    </el-upload>
                  </el-row>
                </div>
              </el-tab-pane>
            </el-tabs>
            <div style="margin:30px 0 0 0;">
              <h2>发放历史</h2>
            </div>
            <el-table
              :data="recordTable"
              max-height="500"
              border
              style="width: 900px;margin:20px 0 0 0"
            >
              <el-table-column
                align="center"
                prop="id"
                label="批次号"
                width="215"
              />
              <el-table-column
                align="center"
                prop="createTime"
                label="发放时间"
                width="200"
              >
                <template slot-scope="scope">
                  {{ new Date(scope.row.createTime) | parseTime }}
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                label="用户手机号"
                width="130"
              >
                <template slot-scope="scope">
                  <div v-if="scope.row.num === 1">
                    <span>{{ scope.row.distributeList[0].key }}</span>
                  </div>
                  <!--  -->
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="total"
                label="总发放数量"
              />
              <el-table-column
                align="center"
                prop="num"
                label="总发放人数"
              />
              <el-table-column
                align="center"
                prop="operator"
                label="操作人"
              />
            </el-table>
          </div>
        </el-tab-pane>
        <el-tab-pane label="积分查询" name="second">
          <div style="margin:30px 0 0 40px">
            <el-form :inline="true" :model="queryForm" class="demo-form-inline">
              <el-form-item label="手机号:">
                <el-input v-model.number="queryForm.phone" placeholder="请输入正确手机号" />
              </el-form-item>
              <!-- <el-form-item label="姓名">
              <el-select v-model="formInline.region" placeholder="活动区域">
                <el-option label="区域一" value="shanghai" />
                <el-option label="区域二" value="beijing" />
              </el-select>
            </el-form-item> -->
              <el-form-item>
                <el-button :disabled="queryForm.phone === ''" type="primary" @click="onQuery()">查询</el-button>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="onExport()">导出全部用户数据</el-button>
              </el-form-item>
              <el-table
                :data="queryAns"
                max-height="500"
                border
                style="width: 600px"
              >
                <el-table-column
                  align="center"
                  prop="id"
                  label="用户ID"
                  width="230"
                />
                <el-table-column
                  align="center"
                  prop="phone"
                  label="用户手机号"
                  width="180"
                />
                <el-table-column
                  align="center"
                  prop="remain"
                  label="剩余积分数量"
                />
              </el-table>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>

      <!-- <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" /> -->
    </div>
    <el-dialog
      title="提示"
      :visible.sync="dialogVisible"
      width="40%"
    >
      <span>该用户为全新用户，即将创建并发放积分，是否继续？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="distributeConfirm()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
// import { fetchList, fetchPv, createArticle, updateArticle } from '@/api/article'
// import waves from '@/directive/waves' // waves directive
// import Pagination from '@/components/Pagination' // secondary package based on el-pagination
export default {
  name: 'DistributePoint',
  // components: { Pagination },
  // directives: { waves },
  data() {
    return {
      activeName: 'first',
      disForm: {
        phone: '',
        num: 1
      },
      queryForm: {
        phone: ''
      },
      fileList: [],
      queryAns: [],
      dialogVisible: false
    }
  },
  computed: {
    recordTable() {
      return this.$store.getters.recordTable
    },
    queryTable() {
      return this.$store.getters.queryTable
    }
  },
  created() {
    this.$store.dispatch('point/loadDistributeRecord')
    this.$store.dispatch('point/queryAllPoint').then(() => {
      this.queryAns = Object.assign([], this.queryTable)
    })
  },
  methods: {
    onSuccess(response, file, fileList) {
      const { data } = response
      this.$store.dispatch('point/setDistributeRecord', data).then(() => {
        this.$notify({
          title: 'Success',
          message: '积分发放完成',
          type: 'success',
          duration: 2000
        })
      })
    },
    distributeConfirm() {
      this.$store.dispatch('point/newCustomerDistributePointOne',
        { phone: this.disForm.phone, quantity: this.disForm.num.toFixed(2) }).then(() => {
        this.$notify({
          title: 'Success',
          dangerouslyUseHTMLString: true,
          message: '<div>新增用户: ' + this.disForm.phone + ' 成功</div><div>新增积分余额: ' + this.disForm.num + '</div>',
          type: 'success',
          duration: 2000
        })
        this.dialogVisible = false
      })
    },
    distributePointOne() {
      var phone = this.disForm.phone
      if (!(/^1[3456789]\d{9}$/.test(phone))) {
        this.$notify({
          title: 'Fail',
          message: '手机号有误,其检查后再提交',
          type: 'error',
          duration: 2000
        })
      } else {
        this.$store.dispatch('point/distributePointOne',
          { phone: this.disForm.phone, quantity: this.disForm.num.toFixed(2) }).then(() => {
          this.$notify({
            title: 'Success',
            dangerouslyUseHTMLString: true,
            message: '<div>用户: ' + this.disForm.phone + '</div><div>新增积分余额: ' + this.disForm.num + '</div>',
            type: 'success',
            duration: 2000
          })
        }).catch(() => {
          this.dialogVisible = true
        })
      }
    },
    onQuery() {
      this.$store.dispatch('point/queryPoint', this.queryForm).then(() => {
        this.queryAns = Object.assign([], this.queryTable)
        this.$notify({
          title: 'Success',
          message: '积分查询成功',
          type: 'success',
          duration: 2000
        })
      })
    },
    onExport() {

    }
  }
}
</script>
