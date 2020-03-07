<template>
  <div class="editAddress">
    <van-address-edit
      :area-list="areaList"
      show-postal
      show-delete
      show-set-default
      show-search-result
      :address-info="addressInfo"
      :search-result="searchResult"
      :area-columns-placeholder="['请选择', '请选择', '请选择']"
      @save="onSave"
      @delete="onDelete"
    />
  </div>
</template>

<script>
import { Toast, AddressEdit } from 'vant'
import areaList from '@/area.js'

export default {
  components: {
    [Toast.name]: Toast,
    [AddressEdit.name]: AddressEdit
  },
  data() {
    return {
      areaList: areaList,
      searchResult: [],
      addressInfo: {}
    }
  },
  created() {
    const address = this.$store.getters.tempAddress
    if (address !== null) {
      this.addressInfo = address
    }
  },
  methods: {
    onSave(content) {
      var code
      if (this.$store.getters.tempAddress === null) {
        code = 'address/addAddress'
        content.id = this.$store.getters.addressList.length
      } else {
        code = 'address/editAddress'
      }
      content.address = ((content.province === content.city) ? (content.province)
        : (content.province + content.city)) + content.county + content.addressDetail
      this.$store.dispatch(code, content).then(() => {
        this.$router.go(-1)
      }).catch(() => {})
    },
    onDelete(content) {
      if (this.$store.getters.tempAddress === null) {
        this.$router.go(-1)
      } else {
        this.$store.dispatch('address/deleteAddress', content.id).then(() => {
          this.$router.go(-1)
        })
      }

      Toast('delete')
    }
    // onChangeDetail(val) {
    //   if (val) {
    //     this.searchResult = [{
    //       name: '黄龙万科中心',
    //       address: '杭州市西湖区'
    //     }]
    //   } else {
    //     this.searchResult = []
    //   }
    // }
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
}
</style>
