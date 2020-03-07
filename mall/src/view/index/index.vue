<template>
  <div v-if="mall.id" class="index">
    <van-sticky :offset-top="46">
      <van-row style="height:54px" type="flex" justify="center">
        <van-col span="4"><van-goods-action-icon icon="user-circle-o" text="个人中心" size="40px" @click="onClickUser" /></van-col>
        <van-col span="16"><van-search v-model="value" show-action placeholder="请输入搜索关键词" @search="onSearch" @cancel="value=''" /></van-col>
        <van-col span="4"><van-goods-action-icon icon="cart-o" text="购物车" size="40px" @click="onClickCart" /></van-col>
      </van-row>
      <van-row style="height:54px;background-color: #fff;" type="flex" justify="space-between">
        <van-col class="col2" span="12">
          <van-icon style="padding-top:10px" name="points" size="30px" />
          <span class="sticky-span">
            <span>{{ mall.point.pointType }}</span>
            <span v-if="$store.getters.customer !== undefined && $store.getters.customer !== null" style="color:#10a0e9;margin-left:10px">{{ remain }}</span>
            <span v-if="$store.getters.customer === undefined || $store.getters.customer === null" style="color:#10a0e9;margin-left:10px" @click="$router.push('login')">未登录</span>
          </span>
        </van-col>
        <van-col class="col2" span="12" @click="onClickOrder">
          <van-icon style="padding-top:10px" name="orders-o" size="30px" />
          <span class="sticky-span">我的订单</span>
        </van-col>
      </van-row>
    </van-sticky>
    <!-- <van-goods-action-icon icon="cart-o" /> -->
    <div style="width:100%;background: #fff;max-width:640px">
      <van-swipe class="index-swipe" :autoplay="mall.interval*1000">
        <van-swipe-item v-for="(item, index) in mall.advertise" :key="index">
          <img v-lazy="item.image.url" style="width:100%" @click="linkTo(item)">
        </van-swipe-item>
      </van-swipe>
    </div>
    <van-tabs class="index-tabs" line-height="0px">
      <van-tab v-for="(item,index) in mall.entrance" :key="index">
        <div slot="title">
          <van-goods-action-icon :text=" item.name" @click="clickEntrance(item)">
            <div slot="icon">
              <div class="entrance" :style="{backgroundImage:'url(' + item.image.url + '), linear-gradient('+mall.color+', '+mall.color+')'}" />
            </div>
          </van-goods-action-icon>
        </div>
      </van-tab>
    </van-tabs>
    <div v-for="(item,index) in mall.subjectOrder" :key="index" style="margin:20px 0 20px 0;text-align:center">
      <van-image
        fit="cover"
        lazy-load
        :src="mall.subjectList[item].image.url"
        radius="8px"
        width="95%"
        @click="clickSubject(mall.subjectList[item])"
      />
      <van-tabs v-if="mall.subjectList[item].showGoodsList.length!== 0" swipe-threshold="2" class="subject-tabs" line-height="0px" title-active-color="#646566" title-inactive-color="#646566">
        <van-tab v-for="(pItem,pIndex) in mall.subjectList[item].showGoodsList" :key="pIndex">
          <div slot="title" align="left">
            <div class="item-tab" @click="clickGoodsImg(pItem.id)">
              <van-row style="width:100%">
                <img v-lazy="pItem.imageList[0].url" class="subjectGoods">
              </van-row>
              <div style="line-height:25px"><p class="goodsName">{{ pItem.name }}</p></div>
              <div style="font-size:18px;line-height:25px"><span class="p-price">{{ pItem.pointPrice.toFixed(2) }}</span><span>{{ pItem.pointType }}</span></div>
            </div>
          </div>
        </van-tab>
      </van-tabs>
    </div>
    <div v-if="mall.showList">
      <section class="ft18"><em /><span style="width:84px">&nbsp;为你推荐&nbsp;</span><em /></section>
      <van-list
        v-if="ready"
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
  </div>
</template>

<script>
import {
  Icon,
  Swipe,
  Toast,
  SwipeItem,
  GoodsActionIcon,
  Sticky,
  Search,
  Tab,
  Tabs,
  Lazyload,
  Row,
  Col,
  Image,
  List
} from 'vant'
import Vue from 'vue'
import '@/resource/fastEntranceIcon/iconfont.css'
import { initInfo } from '@/store'

Vue.use(Lazyload)

export default {
  components: {
    [Icon.name]: Icon,
    [Swipe.name]: Swipe,
    [SwipeItem.name]: SwipeItem,
    [GoodsActionIcon.name]: GoodsActionIcon,
    [Sticky.name]: Sticky,
    [Search.name]: Search,
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
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
      goodsList: [],
      value: '',
      list: [],
      ready: false
    }
  },
  computed: {
    remain() {
      if (this.$store.getters.customer !== undefined && this.$store.getters.customer.remain !== undefined) {
        return this.$store.getters.customer.remain.toFixed(2)
      }
      return '未登录'
    },
    mall() {
      return this.$store.getters.mall
    }
  },
  beforeCreate() {
    const mallID = this.$route.query.mallID
    if (mallID !== undefined) {
      // if (mallID !== undefined && this.$store.getters.mallID === undefined) {
      Toast.loading({
        duration: 0, // 持续展示 toast
        forbidClick: true,
        message: '数据加载中...'
      })
      this.$store.dispatch('setting/setMall', mallID).then(() => {
        Toast.clear()
        this.ready = true
      })
    } else if (this.$store.getters.mallID !== undefined) {
      Toast.loading({
        duration: 0, // 持续展示 toast
        forbidClick: true,
        message: '数据加载中...'
      })
      this.$store.dispatch('setting/setMall', this.$store.getters.mallID).then(() => {
        Toast.clear()
        this.ready = true
      })
    }
    if (this.$store.getters.customer !== undefined && this.$store.getters.customer.remain !== undefined) {
      initInfo(this.$store.getters.customer.phone)
    }
  },
  methods: {
    clickSubject(item) {
      this.$store.dispatch('goods/subjectGoods', { subjectID: item.eid, title: '专题-' + item.name, url: item.image.url }).then(() => {
        this.$router.push('search')
      })
    },

    clickEntrance(item) {
      this.$store.dispatch('goods/entranceGoods', { rxg: item.goodsType, title: '分类-' + item.name }).then(() => {
        this.$router.push('search')
      })
    },

    onSearch() {
      this.$store.dispatch('goods/search', { rxg: this.value, title: '搜索-' + this.value }).then(() => {
        this.$router.push('search')
      })
    },

    clickGoodsImg(id) {
      this.$store.dispatch('goods/setCurGoods', id).then(() => {
        this.$router.push('goods')
      })
    },

    linkTo(item) {
      location.href = item.link
    },

    onClickCart() {
      this.$router.push('cart')
    },

    onClickUser() {
      this.$router.push('user')
    },

    onClickOrder() {
      this.$router.push('order')
    },

    sorry() {
      Toast('暂无后续逻辑~')
    },

    onLoad() {
      this.$store.dispatch('goods/getAllGoodsByMallID').then(() => {
        this.list = this.$store.getters.goodsList
        this.loading = false
        this.finished = true
      })
    }
  }
}
</script>

<style lang="less">
.index {
  padding-bottom: 50px;
  @max_width:640px;

.subjectGoods{
  object-fit: cover;
  width: @max_width * 0.20;
  height: @max_width * 0.20;
}
.entrance {
  width: 40px;
  height: 40px;
  background-blend-mode: lighten;
  background-size: cover;
  border-width:0;
  border:0
}

.goodsName{
  width: 100%;
  font-size: 18px;
  text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    margin-block-start:0
}
// .van-hairline--top-bottom::after{
//     border-width:0 0
// }

  &-swipe {
    img {
      width: 100%;
      display: block;
      object-fit: cover;
      border-radius: 8px
    }
  }

  &-tabs{
    @height:130px;
    height: @height;
    .van-tabs__nav--line {
      height: @height - 40px;
    }
    .van-goods-action-icon__icon{
      width: auto;
      margin: 0 auto 16px;
    }
    .van-tab {
    font-weight: 600;
    width: 120px;
    flex-basis: auto!important;
    margin-left: .9375rem;
    }
  }
  .subject-tabs{
    @height:250px;
    height: @height;
    .van-tabs__nav--line {
      height: @height - 40px;
    }
    .van-tab {
    font-weight: 400;
    max-width: 160px;
    min-width:160px;
    flex-basis: auto!important;
    line-height: 37px;
    margin:0 10px 0 10px;
    }
  }

  .p-price {
    color: #ff763d;
    margin-right: 10px;
  }

  &-express {
    color: #999;
    font-size: 12px;
    padding: 5px 15px;
  }

  &-cell-group {
    margin: 15px 0;

    .van-cell__value {
      color: #999;
    }
  }

  &-tag {
    margin-left: 5px;
  }

  .col2{
  padding-left: 10%;
}
.sticky-span{
  font-size: 15px;
  text-align: center;
  position: absolute;
  margin:16px 10px;
}
.van-tabs--line .van-tabs__wrap{
  height: 100%!important;
}
.van-tabs__nav--line {
    box-sizing: content-box;
    // height: 100%;
    padding-top: 20px;
    padding-bottom: 20px;
}
.item-tab{
  width: @max_width / 4 !important;
  text-align: center;
}

.van-sticky{
  width: 100%;
  background-color: #fff;
}
 .van-sticky--fixed{
transform: translateX(-50%);
    max-width: 640px;
    left: 50%;
 }
 .ft18{
    display: -webkit-box;
    display: -webkit-flex;
    display: flex;
    // height: 3.125rem;
    height: 85px;
    line-height: 3.125rem;
    color: #444;
    text-align: center;
    padding: 0 .9375rem;
    -webkit-box-align: center;
    -webkit-align-items: center;
    align-items: center;
    -webkit-box-pack: center;
    -webkit-justify-content: center;
    justify-content: center;
    font-size: 18px;

    em{
      width: 10px;
      height: 2px;
      background: #999;
      transform: rotate(-60deg);
    }
 }
 .p-item{
   width: 100%;
  //  height: 100%;
   background: #fff;
    padding-top: .9375rem;
    padding-bottom: .9375rem;
    text-align: center;
 }
}
</style>
