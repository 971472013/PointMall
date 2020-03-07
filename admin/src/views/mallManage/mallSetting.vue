<template>
  <div class="app-container">
    <el-form ref="temp" :model="temp" :rules="rules" label-width="100px" class="demo-ruleForm">
      <el-form-item label="商城标题" prop="title">
        <el-input v-model="temp.title" auto-complete="off" style="width:400px" />
      </el-form-item>
      <el-form-item label="商城Logo:" prop="image">
        <el-upload
          class="logo-uploader"
          action="/api/admin/uploadImage"
          :show-file-list="false"
          :before-upload="beforeUpload"
          :headers="{token:$store.getters.token}"
          accept="image/png, image/jpeg, image/jpg"
          :data="{dir:'logoImage'}"
          :on-success="handleSuccess"
        >
          <img v-if="temp.logo.url" :src="temp.logo.url" class="logo">
          <i v-else class="el-icon-plus logo-uploader-icon" />
        </el-upload>
      </el-form-item>
      <el-form-item label="商城地址">
        <div>http://www.pointxmall.com/mall/#/index?mallID={{ temp.id }}</div>
      </el-form-item>
      <el-form-item label="运费设置">
        <div>
          运费按照满<el-input-number v-model="temp.over" style="margin:0 5px 0 5px" :min="0" />元包邮
        </div>
        <div style="margin:30px 0 0 0">
          不满则收取<el-input-number v-model="temp.lest" style="margin:0 5px 0 5px" :min="0" />元运费
        </div>
      </el-form-item>
      <el-form-item label="客服电话">
        <el-input v-model="temp.servePhone" style="width:400px" auto-complete="off" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit()">提交</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'MallSetting',
  data() {
    var validateImage = (rule, value, callback) => {
      if (this.temp.logo.url === undefined) {
        callback(new Error('logo图片为必选项'))
      } else {
        callback()
      }
    }
    return {
      rules: {
        title: [{ required: true, message: '商城名为必填项', trigger: 'blur' }],
        image: [{ required: true, validator: validateImage, trigger: 'blur' }]
      },
      temp: {}
    }
  },
  computed: {
    curMall() {
      return this.$store.getters.curMall
    }
  },
  created() {
    this.temp = Object.assign({}, this.curMall)
  },
  methods: {
    handleSuccess(res, file) {
      const { data } = res
      console.log(data)
      this.temp.logo = data
    },
    beforeUpload() {

    },
    submit() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          this.$store.dispatch('mall/updateBasic', this.temp).then(() => {
            this.$notify({
              title: 'Success',
              message: '商城设置保存成功',
              type: 'success',
              duration: 2000
            })
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
      this.$nextTick(() => {
        this.$refs['temp'].clearValidate()
      })
    }
  }
}
</script>

<style>
.logo-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .logo-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .logo-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 160px;
    height: 160px;
    line-height: 178px;
    text-align: center;
  }
  .logo {
    width: 160px;
    height: 160px;
    display: block;
  }
</style>
