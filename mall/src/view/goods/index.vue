<template>
  <div v-if="goods.id !== undefined" class="goods">
    <van-swipe class="goods-swipe" :autoplay="3000">
      <van-swipe-item v-for="thumb in goods.imageList" :key="thumb.url">
        <img :src="thumb.url">
      </van-swipe-item>
    </van-swipe>

    <van-cell-group>
      <van-cell>
        <div class="goods-title">{{ goods.name }}</div>
        <div class="goods-price">
          <span class="pointPrice">
            {{ goods.pointPrice.toFixed(2) }}
          </span>
          <span>
            {{ goods.pointType }}
          </span>
          <span class="marketPrice">
            <!-- {{ goods.marketPrice }} -->
            {{ formatPrice(goods.marketPrice) }}
          </span>
        </div>
      </van-cell>
      <van-cell class="goods-express">
        <van-col span="16">运费：满{{ $store.getters.mall.over }}元包邮,不满加收运费{{ $store.getters.mall.lest }}元</van-col>
        <van-col span="8">剩余：{{ goods.stockCount }}</van-col>
      </van-cell>
      <van-cell v-if="goods.source === '京东'" class="goods-express" is-link @click="showPopup(2)">
        <template slot="title">
          <van-image
            width="50"
            height="50"
            src="jd.png"
          />
          <span style="position:relative;left:10px;top:-10px;">京东发货&售后</span>
        </template>
      </van-cell>
    </van-cell-group>

    <van-cell-group class="goods-cell-group">
      <van-cell class="describe" use-label-slot center	title-width="100%">
        <template slot="title">
          <span style="font-size:20px;font-weight: bold;position: relative;left: 15px;">商品详细说明</span>
        </template>
        <template slot="label">
          <!-- <div v-for="(item,index) in goods.desImages" :key="index">
            <van-image :src="item" />
          </div> -->
          <div v-html="goods.describe" />
        </template>
      </van-cell>
    </van-cell-group>

    <!-- <van-cell-group class="goods-cell-group">
      <van-cell value="进入店铺" icon="shop-o" is-link @click="sorry">
        <template slot="title">
          <span class="van-cell-text">有赞的店</span>
          <van-tag class="goods-tag" type="danger">官方</van-tag>
        </template>
      </van-cell>
      <van-cell title="线下门店" icon="location-o" is-link @click="sorry" />
    </van-cell-group> -->

    <!-- <van-cell-group class="goods-cell-group">
      <van-cell title="送至" is-link @click="showPopup(1)" />
    </van-cell-group> -->
    <van-popup v-model="supplier" round position="bottom" safe-area-inset-bottom style="height:300px">
      <van-row style="width: 100%;margin:20px 0; text-align: center">
        <span style="font-size:40px">购买须知</span>
      </van-row>
      <van-row style="margin:0 0 10px 20px">
        <van-image
          width="50"
          height="50"
          src="jd.png"
        />
        <span style="top: -15px;position: relative;left: 10px;">京东发货&售后</span>
      </van-row>
      <van-row style="margin:0 0 10px 20px">
        <span>1. 该商品由京东商城负责发货和提供售后服务。</span>
      </van-row></van-popup>

    <van-popup v-model="areaShow" round position="bottom" safe-area-inset-bottom>
      <van-area :area-list="areaList" title="配送至" @cancel="cancel()" @confirm="confirm" />
    </van-popup>

    <van-goods-action>
      <!-- <van-goods-action-icon icon="chat-o" @click="sorry">
        客服
      </van-goods-action-icon> -->
      <van-goods-action-icon icon="cart-o" style="margin:0 40px 0 10px" :info="$store.getters.cartNum" @click="onClickCart">
        购物车
      </van-goods-action-icon>
      <van-goods-action-button type="warning" @click="addToCart">
        加入购物车
      </van-goods-action-button>
      <van-goods-action-button type="danger" @click="skuShow = true">
        立即购买
      </van-goods-action-button>
    </van-goods-action>

    <van-popup v-model="skuShow" round position="bottom" safe-area-inset-bottom>
      <van-row class="sku-row">
        <van-col span="8">
          <span>商品名称</span>
        </van-col>
        <van-col span="16">
          <span style="float: right;">{{ goods.name }}</span>
        </van-col>
      </van-row>
      <van-row class="sku-row">
        <span>数量</span>
        <span style="float: right;margin-right: 50px;"><van-stepper v-model="goods.num" button-size="36px" input-width="40px" /></span>
      </van-row>
      <van-row>
        <van-col span="12"><van-button type="default" block @click="skuShow = false">取消</van-button></van-col>
        <van-col span="12"><van-button type="info" block @click="confirmBuy()">确认</van-button></van-col>
      </van-row>
    </van-popup>

  </div>
</template>

<script>
import {
  // Tag,
  Col,
  Row,
  Icon,
  Cell,
  CellGroup,
  Swipe,
  Toast,
  SwipeItem,
  GoodsAction,
  GoodsActionIcon,
  GoodsActionButton,
  Area,
  Popup,
  Image,
  Sku,
  Stepper,
  Button
} from 'vant'

import areaList from '@/area.js'
import { getGoodsInfoByID } from '@/api'

export default {
  components: {
    // [Tag.name]: Tag,
    [Col.name]: Col,
    [Row.name]: Row,
    [Icon.name]: Icon,
    [Cell.name]: Cell,
    [CellGroup.name]: CellGroup,
    [Swipe.name]: Swipe,
    [SwipeItem.name]: SwipeItem,
    [GoodsAction.name]: GoodsAction,
    [GoodsActionIcon.name]: GoodsActionIcon,
    [GoodsActionButton.name]: GoodsActionButton,
    [Area.name]: Area,
    [Popup.name]: Popup,
    [Image.name]: Image,
    [Sku.name]: Sku,
    [Stepper.name]: Stepper,
    [Button.name]: Button
  },
  data() {
    return {
      areaShow: false,
      supplier: false,
      areaList: areaList,
      goods: {},
      skuShow: false
    }
  },
  created() {
    const id = this.$store.getters.curGoods
    getGoodsInfoByID({ id: id, mallID: this.$store.getters.mallID }).then(resp => {
      const { data } = resp
      this.goods = data
    })
  },
  methods: {
    showPopup(v) {
      if (v === 1) {
        this.areaShow = true
      } else if (v === 2) {
        this.supplier = true
      }
    },

    cancel() {
      this.areaShow = false
    },

    confirm(v) {

    },

    formatPrice(price) {
      return '¥' + (price).toFixed(2)
    },

    onClickCart() {
      this.$router.push('cart')
    },

    addToCart() {
      if (sessionStorage.getItem('token') === null) {
        this.$router.push({ path: 'login' })
      } else {
        const that = this
        const index = this.$store.getters.cartGoodsList.findIndex(function(value, index, arr) {
          return value.id === that.goods.id
        })

        if (index === -1) {
        // const data = {
        //   id: this.goods.id,
        //   name: this.goods.name,
        //   describe: this.goods.describe,
        //   marketPrice: this.goods.marketPrice,
        //   pointPrice: this.goods.pointPrice,
        //   pointType: this.goods.pointType,
        //   num: 1,
        //   image: this.goods.imageList[0]
        // }
          const data = this.$deepCopy(this.goods)
          data.num = 1
          data.image = this.goods.imageList[0]
          this.$store.getters.cartGoodsList.unshift(data)
        } else {
          this.$store.getters.cartGoodsList[index].num++
        }
        Toast.success('添加成功')
      }
    },

    confirmBuy() {
      const data = Object.assign({}, this.goods)
      data.image = this.goods.imageList[0]
      this.$store.dispatch('goods/setGoods2order', [data]).then(() => {
        this.$router.push('orderConfirm')
      }).catch(() => {})
    },

    sorry() {
      Toast('暂无后续逻辑~')
    }
  }
}
</script>

<style lang="less">
.goods {
  padding-bottom: 50px;

  &-swipe {
    img {
      width: 100%;
      display: block;
    }
  }

  &-title {
    font-size: 16px;
  }

  // &-price {
  //   color: #f44;
  // }
  .pointPrice{
    color: rgb(253, 114, 62);
    font-size: 20px;
  }

  .marketPrice{
    margin-left: .9375rem;
    text-decoration: line-through;
    color: #666;
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

  .sku-row{
    padding: 20px 20px;
    font-size: 26px
  }

  .van-popup--bottom {
    bottom: 0;
    left: auto;
    width: 100%;
    max-width: 640px;
  }

  .van-goods-action{
    margin: 0 auto;
    max-width: 640px;
    width: 100%;
  }
  .describe{
    .van-cell__title{
      width:100%;
    }
  }
}
</style>
