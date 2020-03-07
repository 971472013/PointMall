<template>
  <div class="navbar">
    <hamburger :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

    <breadcrumb class="breadcrumb-container" />

    <div style="position: absolute;right: 18%;top: 5px;">
      <span>当前商城: </span>
      <el-select v-model="value" placeholder="请选择" @change="change">
        <el-option
          v-for="item in mallList"
          :key="item.id"
          :label="item.title"
          :value="item.id"
        />
      </el-select>
      <el-button style="margin-left:10px" type="primary" icon="el-icon-plus" @click="handleAdd()">
        新建商城
      </el-button>
    </div>

    <div class="right-menu">
      <el-dropdown class="avatar-container" trigger="click">
        <div class="avatar-wrapper">
          <img :src="avatar+'?imageView2/1/w/80/h/80'" class="user-avatar">
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown" class="user-dropdown">
          <router-link to="/">
            <el-dropdown-item>
              主页
            </el-dropdown-item>
          </router-link>
          <!-- <a target="_blank" href="https://github.com/PanJiaChen/vue-admin-template/">
            <el-dropdown-item>Github</el-dropdown-item>
          </a>
          <a target="_blank" href="https://panjiachen.github.io/vue-element-admin-site/#/">
            <el-dropdown-item>Docs</el-dropdown-item>
          </a> -->
          <el-dropdown-item divided @click.native="logout">
            <span style="display:block;">登出</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>

    <el-dialog title="新建商城" :visible.sync="dialogFormVisible">
      <el-form ref="temp" :model="temp" :rules="rules" label-width="100px">
        <el-form-item label="商城标题" prop="title">
          <el-input v-model="temp.title" auto-complete="off" style="width:300px" />
        </el-form-item>
        <el-form-item label="商城Logo" prop="image">
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
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmAdd()">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'

export default {
  components: {
    Breadcrumb,
    Hamburger
  },
  data() {
    var validateImage = (rule, value, callback) => {
      if (this.temp.logo.url === undefined) {
        callback(new Error('logo图片为必选项'))
      } else {
        callback()
      }
    }
    return {
      value: '',
      rules: {
        title: [{ required: true, message: '商城名为必填项', trigger: 'blur' }],
        image: [{ required: true, validator: validateImage, trigger: 'blur' }]
      },
      dialogFormVisible: false,
      temp: {
        title: '',
        logo: {}
      }
    }
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar'
    ]),
    mallList() {
      return this.$store.getters.mallList
    }
  },
  mounted() {
    this.value = this.$store.getters.curMallID
  },
  methods: {
    handleAdd() {
      this.dialogFormVisible = true
      this.temp = {
        title: '',
        logo: {}
      }
      this.$nextTick(() => {
        this.$refs['temp'].clearValidate()
      })
    },
    confirmAdd() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          this.$store.dispatch('mall/createMall', this.temp).then((data) => {
            this.$router.push({ path: '/' })
            // this.value = data.id
            this.$store.dispatch('point/setPointInfo', data.point).then(() => {
              this.$notify({
                title: 'Success',
                message: '商城 ' + this.temp.title + ' 创建成功',
                type: 'success',
                duration: 2000
              })
              this.dialogFormVisible = false
            })
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    handleSuccess(res, file) {
      const { data } = res
      this.temp.logo = data
    },
    beforeUpload() {

    },
    change(v) {
      this.$store.dispatch('mall/setCurMall', v).then((point) => {
        this.$store.dispatch('point/setPointInfo', point).then(() => {
          const index = this.mallList.findIndex((each, i, arr) => {
            if (each.id === v) {
              return true
            }
          })
          // console.log(v, this.$store)
          // this.value = v
          this.$router.push({ path: '/redirect' })
          this.$notify({
            title: 'Success',
            message: '当前商城已切换为' + this.mallList[index].title,
            type: 'success',
            duration: 2000
          })
        })
      })
    },
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async logout() {
      await this.$store.dispatch('user/logout')
      this.$router.push(`/login?redirect=${this.$route.fullPath}`)
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

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color:transparent;

    &:hover {
      background: rgba(0, 0, 0, .025)
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s;

        &:hover {
          background: rgba(0, 0, 0, .025)
        }
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
