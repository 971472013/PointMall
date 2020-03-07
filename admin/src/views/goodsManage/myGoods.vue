<template>
  <div class="app-container">
    <el-row>
      <el-button class="filter-item" style="margin-bottom: 40px;" type="primary" icon="el-icon-plus" @click="handleAdd">
        添加商品
      </el-button>
    </el-row>
    <el-form :inline="true" :model="searchForm" class="demo-form-inline">
      <el-row>
        <el-form-item style="margin-bottom: 40px;" label="商品类型:">
          <el-select v-model="searchForm.goodsType" style="width: 160px" class="filter-item">
            <el-option v-for="item in goodsTypeList" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item style="margin-bottom: 40px;" label="商品状态:">
          <el-select v-model="searchForm.goodsStatus " style="width: 100px" class="filter-item">
            <el-option v-for="item in goodsStatusList" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item style="margin-bottom: 40px;" label="商品名称:">
          <el-input v-model="searchForm.goodsName" style="width: 200px;" />
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item style="margin-bottom: 40px;" label="是否配送:">
          <el-select v-model="searchForm.needDistribution" style="width: 90px" class="filter-item">
            <el-option v-for="item in distributeStatusList" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item style="margin-bottom: 40px;" label="供应商:">
          <el-select v-model="searchForm.supplier" style="width: 100px" class="filter-item">
            <el-option v-for="item in supplierList" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item style="margin-bottom: 40px;" label="有无库存:">
          <el-select v-model="searchForm.stockStatus" style="width: 100px" class="filter-item">
            <el-option v-for="item in stockStatusList" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-button class="filter-item" type="primary" icon="el-icon-search" @click="filterGoods()">
          筛选
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
      style="width: 980;"
    >
      <el-table-column label="商品编号" width="80" align="center">
        <template slot-scope="{row}">
          <span>{{ row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品名称" align="center" width="80">
        <template slot-scope="{row}">
          <span>{{ row.name }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="是否配送" min-width="100px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.needDistribution }}</span>
        </template>
      </el-table-column> -->
      <el-table-column label="商品状态" width="80" align="center">
        <template slot-scope="{row}">
          <span>{{ row.goodsStatus }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品品类" width="80" align="center">
        <template slot-scope="{row}">
          <span>{{ row.goodsType }}</span>
        </template>
      </el-table-column>
      <el-table-column label="积分价" width="70" align="center">
        <template slot-scope="{row}">
          <span>{{ row.pointPrice }} {{ $store.getters.point.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="成本价/供货价(元)" width="70" align="center">
        <template slot-scope="{row}">
          <span>{{ row.supplyPrice | formatPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="市场价(元)" width="70" align="center">
        <template slot-scope="{row}">
          <span>{{ row.marketPrice | formatPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="利润率" width="70" align="center">
        <template slot-scope="{row}">
          <span>{{ row | profitMargin }}</span>
        </template>
      </el-table-column>
      <el-table-column label="供应商" width="70" align="center">
        <template slot-scope="{row}">
          <span>{{ row.supplier }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品来源" width="80" align="center">
        <template slot-scope="{row}">
          <span>{{ row.source }}</span>
        </template>
      </el-table-column>
      <el-table-column label="库存状态" width="80" align="center">
        <template slot-scope="{row}">
          <span>{{ row.stockCount > 0 ? '有货':'缺货' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="153" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <el-row>
            <el-button type="primary" size="mini" @click="toggle(row)">
              {{ row.goodsStatus === "上架" ? "下架":"上架" }}
            </el-button>
          </el-row>
          <el-row style="margin-top:20px">
            <el-button type="primary" size="mini" @click="handleEdit(row)">
              编辑
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

    <el-dialog title="设置价格" :visible.sync="editVisible" width="650px">
      <el-form label-position="right" label-width="100px">
        <el-form-item label="名称:">
          {{ tempRow.name }}
        </el-form-item>
        <el-form-item label="市场价格:">
          {{ tempRow.marketPrice | formatPrice }}元
        </el-form-item>
        <el-form-item label="供货价格:">
          {{ tempRow.supplyPrice | formatPrice }}元
        </el-form-item>
        <el-form-item label="允许支付方式:">
          <el-radio-group v-model="tempRow.payMethod">
            <el-radio style="margin-top:14px" :label="1">优先使用{{ $store.getters.point.name }}支付,如{{ $store.getters.point.name }}不足，允许补差额</el-radio>
            <el-radio style="margin-top:20px" :label="2">仅能使用{{ $store.getters.point.name }}支付,如{{ $store.getters.point.name }}不足则无法购买</el-radio>
            <el-radio style="margin-top:20px" :label="3">仅能使用现金支付,如现金不足则无法购买</el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- <el-form-item> -->
        <el-radio-group v-model="priceChoice" style="margin:20px 0 0 20px">
          <el-radio :label="1">
            <span>等于：</span>
            <el-input-number v-model="priceNum1" :min="0" />
            <span>{{ $store.getters.point.name }}</span>
          </el-radio>
          <el-radio :label="2" style="margin-top:20px">
            <span>等于：</span>
            <el-select v-model="priceSelect" disabled style="width:140px">
              <el-option
                v-for="item in priceList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
            <span> * {{ $store.getters.point.exchangeRate }}(积分汇率) * </span>
            <el-input-number v-model="priceNum2" :min="0" style="width:130px" />
            <span> = {{ calPrice }} {{ $store.getters.point.name }}</span>
          </el-radio>
        </el-radio-group>
        <!-- </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="editConfirm()">
          确认
        </el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
// import waves from '@/directive/waves' // waves directive
// eslint-disable-next-line no-unused-vars
import { parseTime, profitMargin } from '@/utils'
import * as v from './variable'
// import { goodsTypeList } from './variable'
// import Pagination from '@/components/Pagination' // secondary package based on el-pagination

export default {
  name: 'Mygoodss',
  // components: { Pagination },
  // directives: { waves },
  data() {
    return {
      visiblePop: false,
      tableKey: 0,

      currentPage: 1,
      pageSize: 10,

      listLoading: true,
      activeNames: ['1', '2'],

      tempRow: {
        goodsName: '',
        id: '',
        needDistribution: '',
        goodsState: '',
        goodsType: '',
        pointPrice: 0,
        supplyPrice: 0,
        marketPrice: 0,
        supplier: '',
        supplierID: '',
        goodsSource: '',
        stockState: '',
        payMethod: ''
      },
      editVisible: false,
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
      downloadLoading: false,

      goodsTypeList: v.goodsTypeList,
      goodsStatusList: v.goodsStatusList,
      distributeStatusList: v.distributeStatusList,
      supplierList: v.supplierList,
      priceList: v.priceList,
      stockStatusList: v.stockStatusList,

      priceChoice: 1,
      priceSelect: v.priceList[0],
      priceNum1: undefined,
      priceNum2: 1,

      searchForm: {
        goodsType: v.goodsTypeList[0],
        goodsStatus: v.goodsStatusList[0],
        goodsName: '',
        needDistribution: v.distributeStatusList[0],
        supplier: v.supplierList[0],
        stockStatus: v.stockStatusList[0]
      },
      list: []
    }
  },
  computed: {
    allList() {
      return this.$store.getters.goodsList
    },
    calPrice() {
      // 基准为市场价
      var ans = 0
      if (this.priceSelect === this.priceList[0]) {
        ans = this.tempRow.marketPrice
      }
      ans = ans * this.$store.getters.point.exchangeRate * this.priceNum2
      return ans
    },
    total: {
      get: function() {
        return this.allList.length
      },
      set: function(val) {}
    }
  },
  created() {
    this.$store.dispatch('goods/getMallGoods').then(() => {
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
    handleAdd() {
      this.$router.push({ path: '/goodsManage/addgoods' })
    },
    filterGoods() {
      this.listLoading = true
      this.$store.dispatch('goods/filterGoods', this.searchForm).then(() => {
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
    handleEdit(row) {
      this.tempRow = Object.assign({}, row) // copy obj
      this.priceNum1 = this.tempRow.pointPrice
      this.priceNum2 = 1
      this.priceChoice = 1
      this.editVisible = true
    },
    editConfirm() {
      var price = 0
      if (this.priceChoice === 1) {
        price = this.priceNum1
      } else if (this.priceChoice === 2) {
        price = this.calPrice
      }
      const index = this.allList.findIndex((v, i, arr) => {
        if (this.tempRow.id === v.id) {
          return true
        }
      })
      this.$store.dispatch('goods/updatePrice', { goodsID: this.tempRow.id, pointPrice: price, index: index,
        payMethod: this.tempRow.payMethod }).then(() => {
        this.editVisible = false
        this.$notify({
          title: 'Success',
          message: '价格更新成功',
          type: 'success',
          duration: 2000
        })
        this.list = this.allList.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
      })
    },
    toggle(item) {
      const index = this.allList.findIndex((v, i, arr) => {
        if (item.id === v.id) {
          return true
        }
      })
      this.$store.dispatch('goods/toggle', { index: index, id: item.id, to: item.goodsStatus === '上架' ? '下架' : '上架' }).then(() => {
        this.$notify({
          title: 'Success',
          // message: (item.goodsStatus === '上架' ? '下架' : '上架') + '成功',
          message: item.goodsStatus + '成功',
          type: 'success',
          duration: 2000
        })
        this.list = this.allList.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
      })
    },
    handleDelete(row) {
      const index = this.allList.findIndex((v, i, arr) => {
        if (row.id === v.id) {
          return true
        }
      })
      this.$store.dispatch('goods/deleteMallGoods', { index: index, id: row.id }).then(() => {
        this.$notify({
          title: 'Success',
          message: '删除成功',
          type: 'success',
          duration: 2000
        })
        this.list = this.allList.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
      })
    }
  }
}
</script>
