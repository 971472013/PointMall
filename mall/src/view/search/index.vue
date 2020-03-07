<template>
  <div class="search">
    <div v-if="$store.getters.url !== ''" style="margin:20px 0 20px 0;text-align:center">
      <van-image
        fit="cover"
        lazy-load
        :src="$store.getters.url"
        radius="8px"
        width="95%"
      />
    </div>
    <div v-if="$store.getters.url === ''">
      <br>
      <br>
    </div>
    <van-list
      v-model="loading"
      :finished="finished"
      finished-text="没有更多了"
      @load="onLoad"
    >
      <van-row
        style="margin-left: -7.5px;
          margin-right: -7.5px;
          padding: 0 25.6px;
          padding-bottom: 25.6px"
      >
        <van-col
          v-for="(item,index) in list"
          :key="index"
          span="12"
          style="padding-left: 7.5px; padding-right: 7.5px; margin-bottom: 20px;"
        >
          <div class="p-item" @click="clickGoodsImg(item.id)">
            <van-row style="width:100%;height:0;padding-top: 100%;position: relative;">
              <van-image
                fit="cover"
                lazy-load
                :src="item.imageList[0].url"
                width="100%"
                height="100%"
                style="left: 0;top: 0;position: absolute;"
              />
            </van-row>
            <div style="text-align:center">
              <p class="goodsName">{{ item.name }}</p>
            </div>
            <div style="text-align:center;font-size:18px;">
              <span class="p-price">{{ item.pointPrice.toFixed(2) }} </span><span>{{ item.pointType }}</span>
            </div>
          </div>
        </van-col>
      </van-row>
    </van-list>
  </div>
</template>

<script>

import Vue from 'vue'
import { Row, Col, List, Lazyload, Toast, Image } from 'vant'
Vue.use(Lazyload)
export default {
  components: {
    [Row.name]: Row,
    [Col.name]: Col,
    [Image.name]: Image,
    [List.name]: List,
    [Toast.name]: Toast
  },
  data() {
    return {
      loading: false,
      finished: false,
      list: []
    }
  },
  computed: {
    searchList() {
      return this.$store.getters.searchList
    }
  },
  methods: {
    clickGoodsImg(id) {
      this.$store.dispatch('goods/setCurGoods', id).then(() => {
        this.$router.push('goods')
      })
    },
    onLoad() {
      this.list = this.searchList
      this.loading = false
      this.finished = true
    }
  }
}
</script>

<style  lang="less">
.search{
    .p-item{
   width: 100%;
  //  height: 100%;
   background: #fff;
    padding-top: .9375rem;
    padding-bottom: .9375rem;
    text-align: center;
 }
 .goodsName{
  width: 100%;
  font-size: 18px;
  text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    margin-block-start:0
}
.p-price {
    color: #ff763d;
    margin-right: 10px;
  }
}
</style>
