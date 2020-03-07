<template>
  <div class="addressList">
    <van-address-list
      v-model="chosenAddressId"
      :list="list"
      :disabled-list="disabledList"
      disabled-text="以下地址超出配送范围"
      default-tag-text="默认"
      @add="onAdd"
      @edit="onEdit"
      @select="select"
    />
  </div>
</template>

<script>
import { Toast, AddressList } from 'vant'

export default {
  components: {
    [Toast.name]: Toast,
    [AddressList.name]: AddressList
  },
  data() {
    return {
      chosenAddressId: '1',
      list: [

      ],
      disabledList: [
        {
          id: '3',
          name: '王五',
          tel: '1320000000',
          address: '浙江省杭州市滨江区江南大道 15 号'
        },
        {
          id: '3',
          name: '王五',
          tel: '1320000000',
          address: '浙江省杭州市滨江区江南大道 15 号'
        }
      ]
    }
  },
  created() {
    this.list = this.$store.getters.addressList
  },
  methods: {
    onAdd() {
    //   this.$router.push('addressEdit')
    //   Toast('新增地址')
      this.$store.dispatch('address/setTempAddress', null).then(() => {
        this.$router.push('addressEdit')
        Toast('新增地址')
      }).catch(() => {})
    },
    onEdit(item, index) {
      this.$store.dispatch('address/setTempAddress', item).then(() => {
        this.$router.push('addressEdit')
      }).catch(() => {})
    },
    select(item, index) {
      if (document.from !== 'user') {
        this.$store.dispatch('address/setUseAddress', item).then(() => {
          this.$router.go(-1)
        }).catch(() => {})
      }
    }
  }
}
</script>
<style lang="less">
.addressList{
    .van-address-item--disabled:nth-of-type(1) {
        margin-bottom: 120px!important;
    }
    .van-address-list__bottom{
        max-width: 640px;
        margin: auto;
        left: 0;
        right: 0;
    }
    .van-radio__icon{
        visibility: hidden;
    }
}
</style>
