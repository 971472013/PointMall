<template>
  <div class="app-container">
    <el-form :inline="true" :model="searchForm" class="demo-form-inline">
      <el-form-item style="margin-bottom: 40px;" label="订单号:">
        <el-input v-model="searchForm.orderID" style="width: 200px;" />
      </el-form-item>
      <el-form-item style="margin-bottom: 40px;" label="订单时间:">
        <el-date-picker v-model="searchForm.searchTime" type="date" format="yyyy-MM-dd" placeholder="选择日期" />
      </el-form-item>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleSearch">
        搜索
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
      <el-table-column label="商品名称" align="center" width="100">
        <template slot-scope="{row}">
          <span>{{ row.productName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品编号" width="100px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.productID }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单号" min-width="100px">
        <template slot-scope="{row}">
          <span>{{ row.orderID }}</span>
        </template>
      </el-table-column>
      <el-table-column label="成本价" width="80px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.supplyPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="积分价" width="80px">
        <template slot-scope="{row}">
          <span>{{ row.pointPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品数量" width="80px">
        <template slot-scope="{row}">
          <span>{{ row.productCount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="供应商" width="100">
        <template slot-scope="{row}">
          <span>{{ row.supplier }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品来源" width="100">
        <template slot-scope="{row}">
          <span>{{ row.productSource }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单创建时间" width="120">
        <template slot-scope="{row}">
          <span>{{ row.orderCreateTime | parseTime('{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单状态" width="120">
        <template slot-scope="{row}">
          <span>{{ row.orderState }}</span>
        </template>
      </el-table-column>
      <el-table-column label="用户ID" width="120">
        <template slot-scope="{row}">
          <span>{{ row.userID }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="230" class-name="small-padding fixed-width">
        <template slot-scope="{row,$index}">
          <el-button type="primary" size="mini" @click="handleDetail(row)">
            详情
          </el-button>
          <el-button v-if="isUnDelivery(row)" size="mini" type="danger" @click="handleDelivery(row,$index)">
            发货
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <el-dialog title="查看详情" :visible.sync="detailVisible">
      <el-collapse v-model="activeNames">
        <el-collapse-item title="商品信息" name="1">
          <div>商品名称: {{ temp.productName }}</div>
          <div>商品编号: {{ temp.productID }}</div>
          <div>商品类别: {{ temp.productType }}</div>
          <div>商品类别: {{ temp.productSource }}</div>
          <div>所属供应商: {{ temp.supplier }}</div>
          <div>所属供应商ID: {{ temp.supplierID }}</div>
          <div>是否需要配送: {{ temp.needDistribution }}</div>
          <div>商品市场价: {{ temp.marketPrice }}</div>
          <div>商品供货价: {{ temp.supplyPrice }}</div>
          <div>积分定价: {{ temp.pointPrice }}</div>
          <div>现金定价: {{ temp.cashPrice }}</div>
        </el-collapse-item>
        <el-collapse-item title="订单信息" name="2">
          <div>订单号: {{ temp.orderID }}</div>
          <div>商品数量: {{ temp.productCount }}</div>
          <div>订单金额: {{ temp.orderPrice }}</div>
          <div>订单状态: {{ temp.orderState }}</div>
          <div>订单创建时间: {{ temp.orderCreateTime | parseTime('{y}-{m}-{d} {h}:{i}') }}</div>
          <div>订单支付时间: {{ temp.orderPayTime | parseTime('{y}-{m}-{d} {h}:{i}') }}</div>
          <div>订单发货时间: {{ temp.orderDeliveryTime | parseTime('{y}-{m}-{d} {h}:{i}') }}</div>
          <div>客户收货时间: {{ temp.orderReceiveTime | parseTime('{y}-{m}-{d} {h}:{i}') }}</div>
          <div>订单完成时间: {{ temp.orderDoneTime | parseTime('{y}-{m}-{d} {h}:{i}') }}</div>
          <div>收件人姓名: {{ temp.receiverName }}</div>
          <div>收件人地址: {{ temp.receiverAddress }}</div>
          <div>收件人联系电话: {{ temp.receiverPhone }}</div>
          <div>快递公司: {{ temp.deliveryCompany }}</div>
          <div>快递单号: {{ temp.deliveryID }}</div>
          <div>用户ID: {{ temp.userID }}</div>
        </el-collapse-item>
      </el-collapse>
    </el-dialog>

    <el-dialog :visible.sync="deliveryVisible" title="请补充物流信息">
      <el-form :rules="deliveryRules" class="form-container">
        <el-row>
          <div>订单号 ： {{ temp.orderID }}</div>
        </el-row>
        <el-row>
          <el-form-item style="margin-bottom: 40px;" prop="deliveryCompany" label="快递公司">
            <el-select v-model="temp.deliveryCompany" style="width: 90px" class="filter-item">
              <el-option v-for="item in DeliveryCompanyOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
        </el-row>
        <el-row>
          <el-form-item style="margin-bottom: 40px;" prop="deliveryID" label="快递单号">
            <el-input v-model="temp.deliveryID" style="width: 200px;" />
          </el-form-item>
        </el-row>
        <el-row>
          <el-button @click="cancel()">取消</el-button>
          <el-button type="primary" @click="confirm()">确认</el-button>
        </el-row>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
// import { fetchList, fetchPv, createArticle, updateArticle } from '@/api/article'
// import waves from '@/directive/waves' // waves directive
import { parseTime } from '@/utils'
import Pagination from '@/components/Pagination' // secondary package based on el-pagination

export default {
  name: 'AllOrder',
  components: { Pagination },
  // directives: { waves },
  data() {
    return {
      tableKey: 0,
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        importance: undefined,
        title: undefined,
        type: undefined,
        sort: '+id'
      },
      temIndex: -1,
      activeNames: ['1', '2'],
      searchForm: {
        orderID: '',
        orderState: '全部',
        productID: '',
        userID: '',
        searchTime: ''
      },
      OrderStateOptions: ['全部', '待支付', '待发货', '待收货', '已取消', '已完成', '订单失败', '商户支付失败',
        '支付成功通知失败', '售后中', '订单关闭'],
      DeliveryCompanyOptions: ['京东', '自营'],
      temp: {
        productName: '',
        productID: '',
        productType: '',
        productSource: '',
        supplier: '',
        supplierID: '',
        needDistribution: '',
        marketPrice: '',
        supplyPrice: '',
        pointPrice: '',
        cashPrice: '',

        orderID: '',
        productCount: '',
        orderPrice: '',
        orderState: '',
        orderCreateTime: '',
        orderPayTime: '',
        orderDeliveryTime: '',
        orderReceiveTime: '',
        orderDoneTime: '',
        receiverName: '',
        receiverAddress: '',
        receiverPhone: '',
        deliveryCompany: '',
        deliveryID: '',
        userID: ''
      },
      detailVisible: false,
      deliveryVisible: false,
      textMap: {
        update: 'Edit',
        create: 'Create'
      },
      dialogPvVisible: false,
      pvData: [],
      deliveryRules: {
        deliveryCompany: [{ required: true, message: '快递公司为必选项', trigger: 'blur' }],
        deliveryID: [{ required: true, message: '快递单号为必选项', trigger: 'blur' }]
      },
      downloadLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      this.list = [
        {
          productName: '123',
          productID: 'ab123',
          productType: '飞机',
          productSource: '京东',
          supplier: 'xxx公司',
          supplierID: 'xxx123',
          needDistribution: '是',
          marketPrice: 100,
          supplyPrice: 90,
          pointPrice: '400E豆',
          cashPrice: 99,

          orderID: '321',
          productCount: 4,
          orderPrice: 400,
          orderState: '待发货',
          orderCreateTime: new Date(),
          orderPayTime: new Date(),
          orderDeliveryTime: new Date(),
          orderReceiveTime: new Date(),
          orderDoneTime: new Date(),
          receiverName: 'zgq',
          receiverAddress: '云南',
          receiverPhone: '123456789',
          deliveryCompany: '顺丰',
          deliveryID: '22222',
          userID: '1'
        }
      ]
      // fetchList(this.listQuery).then(response => {
      //   this.list = response.data.items
      //   this.total = response.data.total

      // Just to simulate the time of the request
      setTimeout(() => {
        this.listLoading = false
      }, 1.5 * 1000)
    },
    resetTemp() {
      this.temp = {
        productName: '',
        productID: '',
        prodcutType: '',
        supplier: '',
        supplierID: '',
        needDistribution: '',
        marketPrice: '',
        supplyPrice: '',
        pointPrice: '',
        cashPrice: '',

        orderID: '',
        productCount: '',
        orderPrice: '',
        orderState: '',
        orderCreateTime: '',
        orderPayTime: '',
        orderDeliveryTime: '',
        orderReceiveTime: '',
        orderDoneTime: '',
        receiverName: '',
        receiverAddress: '',
        receiverPhone: '',
        deliveryCompany: '',
        deliveryID: '',
        userID: ''
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          console.log(this.temp)
          // this.temp.id = parseInt(Math.random() * 100) + 1024 // mock a id
          // this.temp.author = 'vue-element-admin'
          // createArticle(this.temp).then(() => {
          //   this.list.unshift(this.temp)
          //   this.dialogFormVisible = false
          //   this.$notify({
          //     title: 'Success',
          //     message: 'Created Successfully',
          //     type: 'success',
          //     duration: 2000
          //   })
          // })
        }
      })
    },
    isUnDelivery(row) {
      return row.orderState === '待发货'
    },
    handleDetail(row) {
      this.temp = Object.assign({}, row) // copy obj
      this.detailVisible = true
    },
    handleDelivery(row, index) {
      this.temp = Object.assign({}, row) // copy obj
      this.deliveryVisible = true
      this.temIndex = index
    },
    cancel() {
      this.deliveryVisible = false
    },
    confirm() {
      this.list[this.temIndex].deliveryCompany = this.temp.deliveryCompany
      this.list[this.temIndex].deliveryID = this.temp.deliveryID
      this.deliveryVisible = false
      console.log(this.list[this.temIndex])
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp)
          console.log(tempData, this.temp)
          // tempData.timestamp = +new Date(tempData.timestamp) // change Thu Nov 30 2017 16:41:05 GMT+0800 (CST) to 1512031311464
          // updateArticle(tempData).then(() => {
          //   const index = this.list.findIndex(v => v.id === this.temp.id)
          //   this.list.splice(index, 1, this.temp)
          //   this.dialogFormVisible = false
          //   this.$notify({
          //     title: 'Success',
          //     message: 'Update Successfully',
          //     type: 'success',
          //     duration: 2000
          //   })
          // })
        }
      })
    },
    handleDelete(row, index) {
      this.$notify({
        title: 'Success',
        message: 'Delete Successfully',
        type: 'success',
        duration: 2000
      })
      this.list.splice(index, 1)
    },
    handleSearch() {
      console.log(this.searchForm)
    },
    formatJson(filterVal) {
      return this.list.map(v => filterVal.map(j => {
        if (j === 'timestamp') {
          return parseTime(v[j])
        } else {
          return v[j]
        }
      }))
    }
  }
}
</script>
