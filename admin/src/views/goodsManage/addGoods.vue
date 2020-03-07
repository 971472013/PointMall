<template>
  <div class="app-container">
    <el-row>
      <el-menu :default-active="activeIndex" style="margin-bottom: 20px;" class="el-menu-demo" mode="horizontal">
        <el-menu-item index="1"><a href="/a">自有商品</a></el-menu-item>
      </el-menu>
    </el-row>
    <el-button class="filter-item" style="margin:0 0 20px 10px;" type="primary" icon="el-icon-plus" @click="handleCreate()">
      新增自有商品
    </el-button>
    <el-form :inline="true" :model="searchForm" class="demo-form-inline">
      <el-row>
        <el-form-item style="margin-bottom: 40px;" label="商品类型:">
          <el-select v-model="searchForm.goodsType" style="width: 160px" class="filter-item">
            <el-option v-for="item in goodsTypeList" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item style="margin-bottom: 40px;" label="商品状态:">
          <el-select v-model="searchForm.goodsStatus" style="width: 100px" class="filter-item">
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
      style="width: 100%;"
    >
      <el-table-column label="商品id" align="center" width="150">
        <template slot-scope="{row}">
          <span>{{ row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品名称" align="center" width="180">
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
      <el-table-column label="skuid" min-width="100px" align="center">
        <!-- <template slot-scope="{row}">
          <span>{{ row.skuid }}</span>
        </template> -->
      </el-table-column>
      <el-table-column label="市场价(元)" width="100px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.marketPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="供货价(元)" width="100px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.supplyPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="利润率" width="100px" align="center">
        <template slot-scope="{row}">
          <span>{{ row | profitMargin }}</span>
        </template>
      </el-table-column>
      <el-table-column label="库存数量" width="100px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.stockCount }}</span>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" align="center" width="153px" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <el-button :disabled="row.inMall" type="success" size="mini" @click="handleAdd(row)">
            添加
          </el-button>
          <el-button type="primary" size="mini" @click="handleEdit(row)">
            编辑
          </el-button>
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
      <el-form ref="temp" :model="temp" label-position="right" label-width="100px" :rules="rules">
        <el-form-item label="商品品类" prop="goodsType">
          <el-select v-model="temp.goodsType" style="width: 100px" class="filter-item">
            <el-option v-for="item in goodsTypeList" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="temp.name" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="商品编码">
          {{ temp.id }}
        </el-form-item>
        <el-form-item label="展示图片" prop="imageList">
          <!-- <el-image
            style="width: 100px; height: 100px"
            :src="temp.imageList[0]"
            fit="cover"
          /> -->

          <el-upload
            action="/api/admin/uploadImage"
            list-type="picture-card"
            :on-preview="handlePictureCardPreview"
            :on-remove="handleRemove"
            :headers="{token:$store.getters.token}"
            accept="image/png, image/jpeg"
            :file-list="temp.imageList"
            :on-success="uploadSuccess"
            :data="{dir:'goodsImage'}"
          >
            <i class="el-icon-plus" />
          </el-upload>
          <el-dialog :visible.sync="dialogVisible" size="tiny">
            <img width="100%" :src="dialogImageUrl" alt="">
          </el-dialog>
        </el-form-item>
        <el-form-item label="详细说明">
          <editor v-model="temp.describe" :is-clear="isClear" @change="describeChange" />
        </el-form-item>
        <el-form-item label="市场价格" prop="marketPrice">
          <el-input v-model.number="temp.marketPrice" style="width: 200px;" /> 元
        </el-form-item>
        <el-form-item label="供货价格" prop="supplyPrice">
          <el-input v-model.number="temp.supplyPrice" style="width: 200px;" /> 元
        </el-form-item>
        <el-form-item label="商品库存" prop="stockCount">
          <el-input-number v-model="temp.stockCount" />件
        </el-form-item>
        <el-form-item label="是否需要配送">
          <el-radio v-model="temp.needDistribution" label="是">是</el-radio>
          <el-radio v-model="temp.needDistribution" label="否">否</el-radio>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createConfirm():editConfirm()">
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
// import Pagination from '@/components/Pagination' // secondary package based on el-pagination
import * as v from './variable'
import Editor from './editor'
// import request from '@/utils/request'

export default {
  name: 'AddGoods',
  // components: { Pagination },
  components: { Editor },
  // directives: { waves },
  data() {
    var validateImage = (rule, value, callback) => {
      if (this.uploadList.length === 0) {
        callback(new Error('展示图片为必填项'))
      } else {
        callback()
      }
    }
    return {
      dialogImageUrl: '',
      dialogVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '新增'
      },

      activeIndex: '1',
      tableKey: 0,

      currentPage: 1,
      pageSize: 10,

      listLoading: true,

      activeNames: ['1', '2'],
      temp: {
        id: '',
        name: '',
        skuid: '',
        imageList: [],
        needDistribution: '',
        goodsType: '',
        supplyPrice: 0,
        marketPrice: 0,
        source: '',
        stockCount: '',
        supplier: '',
        describe: ''
      },
      rules: {
        name: [{ required: true, message: '商品名称为必填项', trigger: 'blur' }],
        imageList: [{ required: true, validator: validateImage, trigger: 'blur' }],
        goodsType: [{ required: true, message: '商品种类为必填项', trigger: 'blur' }],
        supplyPrice: [{ required: true, message: '商品供应价为必填项', trigger: 'blur' }],
        marketPrice: [{ required: true, message: '商品市场价为必填项', trigger: 'blur' }],
        stockCount: [{ required: true, message: '商品库存为必填项', trigger: 'blur' }]
      },
      editVisible: false,
      deliveryRules: {
        deliveryCompany: [{ required: true, message: '快递公司为必选项', trigger: 'blur' }],
        deliveryID: [{ required: true, message: '快递单号为必选项', trigger: 'blur' }]
      },

      isClear: false,

      searchForm: {
        goodsType: v.goodsTypeList[0],
        goodsStatus: v.goodsStatusList[0],
        goodsName: '',
        supplier: v.supplierList[0],
        needDistribution: v.distributeStatusList[0],
        stockStatus: v.stockStatusList[0]
      },
      goodsTypeList: v.goodsTypeList,
      goodsStatusList: v.goodsStatusList,
      distributeStatusList: v.distributeStatusList,
      supplierList: v.supplierList,
      stockStatusList: v.stockStatusList,

      uploadList: [],
      list: []
    }
  },
  computed: {
    allList() {
      return this.$store.getters.goodsList
    },
    total: {
      get: function() {
        return this.allList.length
      },
      set: function(val) {}
    }
  },
  created() {
    this.$store.dispatch('goods/getAllGoods').then(() => {
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
    describeChange(info) {
      this.temp.describe = info
    },
    uploadSuccess(resp) {
      const { data } = resp
      this.uploadList.push(data)
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    },
    handleRemove(file, fileList) {
      const index = this.uploadList.findIndex((v, i, arr) => {
        if (v.url === file.url) {
          return true
        }
      })
      this.uploadList.splice(index, 1)
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
      })
      this.currentPage = 1
      this.list = this.allList.slice(0, this.pageSize)
    },
    resetTemp() {
      this.temp = {
        id: '提交后自动生成',
        name: '',
        skuid: '',
        imageList: [],
        needDistribution: '是',
        goodsType: '',
        supplyPrice: '',
        marketPrice: '',
        source: '提交后自动生成',
        stockCount: '',
        supplier: '',
        describe: ''
      }
    },
    handleCreate() {
      this.dialogStatus = 'create'
      this.resetTemp()
      this.editVisible = true
      this.uploadList = []
      this.$nextTick(() => {
        this.$refs['temp'].clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogStatus = 'update'
      this.temp = Object.assign({}, row) // copy obj
      this.editVisible = true
      this.uploadList = this.$deepCopy(this.temp.imageList)
      this.$nextTick(() => {
        this.$refs['temp'].clearValidate()
      })
    },
    handleAdd(row) {
      this.$store.dispatch('goods/addToMall', { goodsID: row.id, supplyPrice: row.supplyPrice, marketPrice: row.marketPrice }).then(() => {
        row.inMall = true
        this.$notify({
          title: 'Success',
          message: '商品已加入当前商城中',
          type: 'success',
          duration: 2000
        })
      })
    },
    editConfirm() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          const data = Object.assign({}, this.temp)
          data.imageList = this.$deepCopy(this.uploadList)
          const index = this.allList.findIndex((v, i, arr) => {
            if (v.id === data.id) {
              return true
            }
          })
          this.$store.dispatch('goods/updateGoods', { index: index, data: data }).then(() => {
            this.$notify({
              title: 'Success',
              message: '商品更新成功',
              type: 'success',
              duration: 2000
            })
            this.list = this.allList.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
            this.editVisible = false
          })
        }
      })
    },
    createConfirm() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          const data = Object.assign({}, this.temp)
          data.imageList = this.$deepCopy(this.uploadList)
          this.$store.dispatch('goods/createGoods', data).then(() => {
            this.$notify({
              title: 'Success',
              message: '新建商品成功',
              type: 'success',
              duration: 2000
            })
            this.list = this.allList.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
            this.editVisible = false
          })
        }
      })
    }
  }
}
</script>
