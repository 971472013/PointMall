<template>
  <div class="app-container">
    <el-form :inline="true" :model="searchForm" class="demo-form-inline">
      <el-form-item style="margin-bottom: 40px;" label="商品类型:">
        <el-select v-model="searchForm.goodsType" style="width: 160px" class="filter-item">
          <el-option v-for="item in goodsTypeList" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item style="margin-bottom: 40px;" label="商品名称:">
        <el-input v-model="searchForm.goodsName" style="width: 200px;" />
      </el-form-item>
      <el-form-item style="margin-bottom: 40px;" label="供应商:">
        <el-select v-model="searchForm.supplier" style="width: 100px" class="filter-item">
          <el-option v-for="item in supplierList" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="filter()">
        筛选
      </el-button>
      <el-button class="filter-item" type="primary" icon="el-icon-plus" @click="handleAdd()">
        添加商品
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
      <el-table-column label="序号" align="center" width="80">
        <template slot-scope="{$index}">
          <span>{{ ((currentPage-1)*pageSize)+$index }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品id" align="center" width="150">
        <template slot-scope="{row}">
          <span>{{ row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品名称" align="center" width="150">
        <template slot-scope="{row}">
          <span>{{ row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品图片" width="130px" align="center">
        <template slot-scope="{row}">
          <el-image
            style="width: 100px; height: 100px"
            :src="row.imageList[0].url"
            fit="cover"
          />
        </template>
      </el-table-column>
      <el-table-column label="积分价格" width="130px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.pointPrice }} {{ $store.getters.point.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品供应商" width="100px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.supplier }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品来源" width="100px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.source }}</span>
        </template>
      </el-table-column>
      <el-table-column label="库存数量" width="100px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.stockCount }}</span>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" align="center" width="153px" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <el-row>
            <el-button type="danger" size="mini" @click="moveOut(row)">
              移出专题
            </el-button>
          </el-row>
          <el-row style="margin-top:20px">
            <el-button v-if="allList.length!==0" :disabled="row.id === allList[0].id" type="primary" size="mini" @click="move(row,true)">
              上移
            </el-button>
            <el-button v-if="allList.length!==0" :disabled="row.id === allList[allList.length-1].id" type="primary" size="mini" @click="move(row,false)">
              下移
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

  </div>
</template>

<script>
// import waves from '@/directive/waves' // waves directive
import * as v from './variable'
// import Pagination from '@/components/Pagination' // secondary package based on el-pagination

export default {
  name: 'SubjectGoods',
  // components: { Pagination },
  // directives: { waves },
  data() {
    return {
      tableKey: 0,
      listLoading: true,

      currentPage: 1,
      pageSize: 10,

      activeNames: ['1', '2'],

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

      searchForm: {
        goodsType: v.goodsTypeList[0],
        goodsName: '',
        supplier: v.supplierList[0]
      },

      supplierList: v.supplierList,
      goodsTypeList: v.goodsTypeList,
      list: []
    }
  },
  computed: {
    allList() {
      return this.$store.getters.subjectGoodsList
    },
    total: {
      get: function() {
        return this.allList.length
      },
      set: function(val) {}
    }
  },
  created() {
    this.$store.dispatch('subject/getMallGoodsWithSubject', { inSubject: true }).then(() => {
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
    filter() {
      this.listLoading = true
      this.$store.dispatch('subject/filterGoodsInSubject', { inSubject: true, ...this.searchForm }).then(() => {
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
    },
    moveOut(row) {
      this.$store.dispatch('subject/moveOut', { goodsID: row.id }).then(() => {
        this.$notify({
          title: 'Success',
          message: '商品已移出专题',
          type: 'success',
          duration: 2000
        })
        this.list = this.allList.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
      })
    },
    move(row, up) {
      const index = this.allList.findIndex((v, i, arr) => {
        if (row.id === v.id) {
          return true
        }
      })
      this.$store.dispatch('subject/goodsMove', { goodsID: row.id, index: index, up: up }).then(() => {
        this.$notify({
          title: 'Success',
          message: (up === true ? '上移' : '下移') + '成功',
          type: 'success',
          duration: 2000
        })
        this.list = this.allList.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
      })
    },
    handleAdd() {
      this.$router.push('/goodsManage/subjectGoodsAdd')
    }
  }
}
</script>
