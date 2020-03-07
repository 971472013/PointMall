<template>
  <div class="login">
    <van-field
      v-model="phone"
      label="手机号"
      type="tel"
      placeholder=""
      blur="phoneBlur()"
      focus="phoneFocus()"
      class="login-item login-input"
    />
    <div v-if="phoneError !== ''" class="invalid-feedback">{{ phoneError }}</div>
    <van-field
      v-model="sms"
      center
      clearable
      label="短信验证码"
      placeholder=""
      focus="smsFocus()"
      blur="smsBlur()"
      class="login-item login-input"
    >
      <van-button slot="button" :disabled="disabled" size="small" type="primary" @click="getSms()">{{ btnTitle }}</van-button>
    </van-field>
    <div v-if="smsError" class="invalid-feedback">{{ smsError }}</div>
    <div class="login_btn login-item">
      <van-button type="primary" block @click="handleLogin">登录</van-button>
    </div>
  </div>
</template>
<script>
import {
  Field,
  Button,
  Toast
} from 'vant'
import { initInfo } from '@/store'
export default {

  components: {
    [Field.name]: Field,
    [Button.name]: Button,
    [Toast.name]: Toast
  },

  data() {
    return {
      phone: '', // 手机号
      sms: '', // 验证码
      disabled: false,
      phoneError: '',
      btnTitle: '获取验证码',
      smsError: ''
    }
  },
  methods: {
    phoneBlur() {
      if (this.phone === '') {
        this.phoneError = '手机号不能为空'
      }
    },
    phoneFocus() {
      this.phoneError = ''
    },
    smsBlur() {
      if (this.sms === '') {
        this.smsError = '验证码不能为空'
      }
    },
    smsFocus() {
      this.smsError = ''
    },
    getSms() {
      this.$store.dispatch('customer/sms', this.phone).then(() => {
        this.validateBtn()
      }).catch(() => {
      })
    },
    validateBtn() {
      // 倒计时
      let time = 60
      const timer = setInterval(() => {
        if (time === 0) {
          clearInterval(timer)
          this.disabled = false
          this.btnTitle = '获取验证码'
        } else {
          this.btnTitle = time + '秒后重试'
          this.disabled = true
          time--
        }
      }, 1000)
    },
    handleLogin() {
      const that = this
      this.$store.dispatch('customer/login', { phone: this.phone, sms: this.sms }).then(() => {
        this.$router.push('index')
        Toast.loading({
          duration: 0, // 持续展示 toast
          forbidClick: true,
          message: '数据加载中...'
        })
        initInfo(that.phone).then(() => {
          Toast.clear()
        })
      }).catch(() => {
      })
    }
  }
}

</script>

<style lang="less">
.login{
    padding: 6.0625rem 2.375rem 0;
    background: #fff;
    height: 100%;
    box-sizing: border-box;
    position: absolute;
    bottom: 0;
    max-width: 640px;
    width: 100%;

    &-item{
        margin: 20px 20px;

    }

    &-input{
        border: 1px solid rgba(0,0,0,.15);
        border-radius: .25rem;
    }
    .van-cell{
    width: auto;
  }
}
</style>
