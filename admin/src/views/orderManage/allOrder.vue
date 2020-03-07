<template>
  <div class="app-container">
    <el-form :inline="true" :model="searchForm" class="demo-form-inline">
      <el-row>
        <el-form-item style="margin-bottom: 40px;" label="订单号:">
          <el-input v-model="searchForm.orderID" style="width: 200px;" />
        </el-form-item>
        <el-form-item style="margin-bottom: 40px;" label="订单状态:">
          <el-select v-model="searchForm.orderStatus" style="width: 150px" class="filter-item">
            <el-option v-for="item in orderStatusList" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item style="margin-bottom: 40px;" label="商品编号:">
          <el-input v-model="searchForm.goodsID" style="width: 200px;" />
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item style="margin-bottom: 40px;" label="用户ID:">
          <el-input v-model="searchForm.customerID" style="width: 200px;" />
        </el-form-item>
        <el-form-item style="margin-bottom: 40px;" label="订单时间:">
          <el-date-picker v-model="searchForm.createTime" type="date" format="yyyy-MM-dd" placeholder="选择日期" />
        </el-form-item>
        <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleSearch()">
          搜索
        </el-button>
      </el-row>
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
      <el-table-column label="序号" align="center" width="100">
        <template slot-scope="{$index}">
          <span>{{ ((currentPage-1)*pageSize)+$index }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品名称" align="center" width="100">
        <template slot-scope="{row}">
          <span>{{ row.orderGoods.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品编号" width="100px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.orderGoods.goodsID }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单号" min-width="100px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column label="供货/成本价" width="80px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.orderGoods.supplyPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="积分价" width="80px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.orderGoods.pointPrice }} {{ row.orderGoods.pointType }}</span>
        </template>
      </el-table-column>
      <el-table-column label="运费" width="150px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.orderGoods.express.toFixed(2) }} 元 = {{ row.orderGoods.pointExpress.toFixed(2) }} {{ row.orderGoods.pointType }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品数量" width="80px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.orderGoods.num }}</span>
        </template>
      </el-table-column>
      <el-table-column label="供应商" width="100" align="center">
        <template slot-scope="{row}">
          <span>{{ row.orderGoods.supplier }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品来源" width="100" align="center">
        <template slot-scope="{row}">
          <span>{{ row.orderGoods.source }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单创建时间" width="120" align="center">
        <template slot-scope="{row}">
          <span>{{ row.createTime | parseTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单状态" width="120" align="center">
        <template slot-scope="{row}">
          <span>{{ row.orderGoods.orderStatus }}</span>
        </template>
      </el-table-column>
      <el-table-column label="用户手机号" width="120" align="center">
        <template slot-scope="{row}">
          <span>{{ row.customerPhone }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width" fixed="right">
        <template slot-scope="{row}">
          <div>
            <el-button type="primary" size="mini" @click="handleDetail(row)">
              详情
            </el-button>
            <el-button v-if="row.orderGoods.orderStatus === '待发货'" size="mini" type="success" @click="handleDelivery(row)">
              发货
            </el-button>
            <el-button v-if="row.orderGoods.orderStatus === '售后中'" size="mini" type="warning" @click="setNext(row,'退款通过')">
              退款通过
            </el-button>
          </div>
          <div style="margin-top:10px">
            <el-button v-if="row.orderGoods.orderStatus === '售后中'" size="mini" type="danger" @click="setNext(row,'拒绝退款')">
              拒绝退款
            </el-button>
          </div>
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

    <el-dialog
      title="提示"
      :visible.sync="check"
      width="30%"
    >
      <span>确定 {{ next }} ?</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="check = false">取 消</el-button>
        <el-button type="primary" @click="next === 退款通过 ? handleBack():rejectBack()">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog title="查看详情" :visible.sync="detailVisible">
      <el-collapse v-model="activeNames">
        <el-collapse-item title="商品信息" name="1">
          <el-form label-position="right">
            <el-form-item label="商品名称:">
              <div>{{ temp.orderGoods.name }}</div>
            </el-form-item>
            <el-form-item label="商品名称:">
              <div>{{ temp.orderGoods.name }}</div>
            </el-form-item>
            <el-form-item label="商品编号:">
              <div>{{ temp.orderGoods.goodsID }}</div>
            </el-form-item>
            <el-form-item label="商品类别:">
              <div>{{ temp.orderGoods.type }}</div>
            </el-form-item>
            <el-form-item label="所属供应商:">
              <div>{{ temp.orderGoods.supplier }}</div>
            </el-form-item>
            <!-- <div>所属供应商ID: {{ temp.supplierID }}</div> -->
            <el-form-item label="是否需要配送:">
              <div>{{ temp.orderGoods.needDistribution }}</div>
            </el-form-item>
            <el-form-item label="商品市场价:">
              <div>{{ temp.orderGoods.marketPrice }}</div>
            </el-form-item>
            <el-form-item label="商品供货价">
              <div>{{ temp.orderGoods.supplyPrice }}</div>
            </el-form-item>
            <el-form-item label="积分定价:">
              <div>{{ temp.orderGoods.pointPrice }} {{ temp.orderGoods.pointType }}</div>
            </el-form-item>
          <!-- <div>现金定价: {{ temp.cashPrice }}</div> -->
          </el-form>
        </el-collapse-item>
        <el-collapse-item title="订单信息" name="2">
          <el-form label-position="right">
            <el-form-item label="订单号:">
              <div>{{ temp.id }}</div>
            </el-form-item>
            <el-form-item label="商品数量:">
              <div>{{ temp.orderGoods.num }}</div>
            </el-form-item>
            <el-form-item label="订单金额:">
              <div>{{ getTotal }}</div>
            </el-form-item>
            <el-form-item label="订单状态:">
              <div>{{ temp.orderGoods.orderStatus }}</div>
            </el-form-item>
            <el-form-item label="订单创建时间:">
              <div>{{ temp.createTime | parseTime }}</div>
            </el-form-item>
            <el-form-item label="订单支付时间:">
              <div>{{ temp.orderGoods.payTime | parseTime }}</div>
            </el-form-item>
            <el-form-item label="订单发货时间:">
              <div>{{ temp.orderGoods.outTime | parseTime }}</div>
            </el-form-item>
            <el-form-item label="客户收货时间:">
              <div>{{ temp.orderGoods.receiveTime | parseTime }}</div>
            </el-form-item>
            <el-form-item label="订单完成时间:">
              <div>{{ temp.orderGoods.doneTime | parseTime }}</div>
            </el-form-item>
            <el-form-item label="收件人姓名:">
              <div>{{ temp.address.name }}</div>
            </el-form-item>
            <el-form-item label="收件人地址:">
              <div>{{ temp.address.address }}</div>
            </el-form-item>
            <el-form-item label="收件人联系电话:">
              <div>{{ temp.address.tel }}</div>
            </el-form-item>
            <el-form-item label="快递公司:">
              <div>{{ temp.orderGoods.expressCompany }}</div>
            </el-form-item>
            <el-form-item label="快递单号:">
              <div>{{ temp.orderGoods.expressID }}</div>
            </el-form-item>
            <el-form-item label="退货快递公司:">
              <div>{{ temp.orderGoods.backExpressCompany }}</div>
            </el-form-item>
            <el-form-item label="退货快递单号:">
              <div>{{ temp.orderGoods.backExpressID }}</div>
            </el-form-item>
            <el-form-item label="退款方式">
              <div>{{ temp.orderGoods.orderStatus === '售后中' ? (temp.orderGoods.backExpressID === '' ? '仅退款' : '退款退货') : '未退款' }}</div>
            </el-form-item>
            <el-form-item label="用户ID:">
              <div>{{ temp.customerID }}</div>
            </el-form-item>
          </el-form>
        </el-collapse-item>
      </el-collapse>
    </el-dialog>

    <el-dialog :visible.sync="deliveryVisible" title="请补充物流信息">
      <el-form ref="ruleForm" :model="ruleForm" :rules="deliveryRules" class="form-container" label-position="right">
        <el-form-item>
          <div>订单号 ： {{ temp.id }}</div>
        </el-form-item>
        <el-form-item style="margin-bottom: 40px;" prop="expressCompany" label="快递公司">
          <el-select v-model="ruleForm.expressCompany" style="width: 90px" class="filter-item">
            <el-option v-for="item in expressCompanyList" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item style="margin-bottom: 40px;" prop="expressID" label="快递单号">
          <el-input v-model="ruleForm.expressID" style="width: 200px;" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="deliveryVisible = false">取消</el-button>
        <el-button type="primary" @click="confirm()">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// import { fetchList, fetchPv, createArticle, updateArticle } from '@/api/article'
// import waves from '@/directive/waves' // waves directive
// import Pagination from '@/components/Pagination' // secondary package based on el-pagination
import * as v from './variable'

export default {
  name: 'AllOrder',
  // components: { Pagination },
  // directives: { waves },
  data() {
    return {
      tableKey: 0,

      currentPage: 1,
      pageSize: 10,

      listLoading: true,
      ruleForm: {
        expressCompany: '',
        expressID: ''
      },
      activeNames: ['1', '2'],
      searchForm: {
        orderID: '',
        orderStatus: v.orderStatusList[0],
        goodsID: '',
        customerID: '',
        createTime: null
      },
      temp: {
        totalMarketPrice: 0,
        address: {},
        mallID: '',
        orderStatus: '',
        orderGoods: {},
        totalPointPrice: 0,
        pointType: '',
        userID: '',
        customerPhone: '',
        createTime: '',
        payMethod: '',
        customerID: '',
        id: '',
        lastUpdateTime: ''
      },
      detailVisible: false,
      deliveryVisible: false,
      deliveryRules: {
        expressCompany: [{ required: true, message: '快递公司为必选项', trigger: 'blur' }],
        expressID: [{ required: true, message: '快递单号为必选项', trigger: 'blur' }]
      },
      // downloadLoading: false,
      orderStatusList: v.orderStatusList,
      expressCompanyList: v.expressCompanyList,
      list: [],
      check: false,
      next: '',
      trow: {}
    }
  },
  computed: {
    getTotal() {
      return (this.temp.orderGoods.pointPrice * this.temp.orderGoods.num) + ' ' + this.temp.orderGoods.pointPrice
    },
    allList() {
      return this.$store.getters.orderList
    },
    total: {
      get: function() {
        return this.allList.length
      },
      set: function(val) {}
    }
  },
  created() {
    this.$store.dispatch('order/getAllOrderByMallID').then(() => {
      this.listLoading = false
      this.list = this.allList.slice(0, this.pageSize)
      this.total = this.allList.length
    })
  },
  methods: {
    setNext(row, next) {
      this.trow = row
      this.next = next
      this.check = true
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.list = this.allList.slice(0, val)
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.list = this.allList.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
    },
    resetTemp() {
      this.temp = {
        totalMarketPrice: 0,
        address: {},
        mallID: '',
        orderStatus: '',
        orderGoods: {},
        totalPointPrice: 0,
        pointType: '',
        userID: '',
        customerPhone: '',
        createTime: '',
        payMethod: '',
        customerID: '',
        id: '',
        lastUpdateTime: ''
      }
    },
    handleDetail(row) {
      this.resetTemp()
      this.temp = row
      this.detailVisible = true
    },
    handleDelivery(row) {
      this.resetTemp()
      this.temp = Object.assign({}, row)
      this.deliveryVisible = true
      this.$nextTick(() => {
        this.$refs['ruleForm'].clearValidate()
      })
    },
    handleBack() {
      this.check = false
      this.$store.dispatch('order/back', { orderID: this.trow.id, goodsID: this.trow.orderGoods.goodsID }).then(() => {
        this.trow.orderGoods.orderStatus = '订单关闭'
        this.$notify({
          title: 'Success',
          message: '退款成功',
          type: 'success',
          duration: 2000
        })
      })
    },
    rejectBack() {
      this.check = false
      this.$store.dispatch('order/rejectBack', { orderID: this.trow.id, goodsID: this.trow.orderGoods.goodsID }).then(() => {
        this.trow.orderGoods.orderStatus = '退款失败'
        this.$notify({
          title: 'Success',
          message: '拒绝退款成功',
          type: 'success',
          duration: 2000
        })
      })
    },
    confirm() {
      const that = this
      this.$refs['ruleForm'].validate((valid) => {
        if (valid) {
          that.temp.orderGoods.expressCompany = that.ruleForm.expressCompany
          that.temp.orderGoods.expressID = that.ruleForm.expressID
          that.temp.orderGoods.orderStatus = '待收货'
          that.$store.dispatch('order/updateOrder', this.temp).then(() => {
            that.$notify({
              title: 'Success',
              message: '发货信息添加成功',
              type: 'success',
              duration: 2000
            })
            that.deliveryVisible = false
          })
        } else {
          return false
        }
      })
    },
    handleSearch() {
      this.listLoading = true
      var form = this.$deepCopy(this.searchForm)
      if (this.searchForm.createTime !== null) {
        var t0 = new Date()
        t0.setTime(this.searchForm.createTime.getTime() + 3600 * 1000 * 24)
        form.createTime = t0
      }
      this.$store.dispatch('order/filterOrder', form).then(() => {
        this.$notify({
          title: 'Success',
          message: '查询成功',
          type: 'success',
          duration: 2000
        })
        this.listLoading = false
        this.currentPage = 1
        this.list = this.allList.slice(0, this.pageSize)
      })
    }
  }
}
</script>
