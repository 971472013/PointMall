<template>
  <div class="user">
    <br>
    <br>
    <van-cell title="登录手机号" :value="hidePhone" />
    <van-cell title="收货地址" is-link to="addressList" />
    <br>
    <br>
    <van-button size="large" block plain hairline type="info" @click="onLogout">退出当前账户</van-button>
  </div>
</template>
<script>
import {
  Cell,
  CellGroup,
  Button
} from 'vant'
import { clean } from '@/store'
export default {

  components: {
    [Cell.name]: Cell,
    [CellGroup.name]: CellGroup,
    [Button.name]: Button
  },

  data() {
    return {
    }
  },
  computed: {
    phone() {
      return this.$store.getters.customer.phone
    },
    hidePhone() {
      return this.phone.substr(0, 3) + '****' + this.phone.substr(7)
    }
  },
  methods: {
    onLogout() {
      clean()
      this.$router.push('index')
    //   this.$store.dispatch('user/logout', {})
    }
  }
}

</script>

<style lang="less">
.user{
    .van-button{
        font-size: 25px
    }
}
</style>
