<template>
  <div class="orderConfirm">
    <br>
    <van-cell style="height:110px" center to="addressList">
      <div v-if="useAddress === null" style="text-align: center;font-size: 20px;">+添加收货地址</div>
      <div v-if="useAddress !== null">
        <van-row style="color: rgb(0, 0, 0);font-size:18px;margin:0 0 0 10px">
          <span>
            {{ useAddress.name }}
          </span>
          <span style="margin-left:30px">
            {{ useAddress.tel }}
          </span>
        </van-row>
        <van-row style="color:#3f3f3f;margin:10px 0 0 10px">
          <span> {{ useAddress.address }}</span>
        </van-row>
      </div>
    </van-cell>
    <br>
    <van-cell-group>
      <van-cell v-for="(item,index) in goodsList" :key="index">
        <div slot="default">
          <van-card :num="item.num" :title="item.name" :thumb="item.image.url">
            <div slot="price">
              <span class="mPrice">{{ formatPrice(item.marketPrice) }}</span>
              <span class="pPrice">{{ item.pointPrice.toFixed(2) }}</span>
              <span class="pType">{{ item.pointType }}</span>
            </div>
          </van-card>
        </div>
      </van-cell>
      <van-cell title="商品合计">
        <div slot="default">
          <span class="mPrice">{{ formatPrice(allMarketPrice) }}</span>
          <span class="pPrice">{{ allPointPrice.toFixed(2) }}</span>
          <span class="pType">{{ $store.getters.point.name }}</span>
        </div>
      </van-cell>
      <van-cell title="运费">
        <div slot="default">
          <span class="pType">{{ express.toFixed(2) }} 元 = {{ pointExpress.toFixed(2) }} {{ $store.getters.point.name }}</span>
        </div>
      </van-cell>
    </van-cell-group>
    <van-submit-bar
      :disabled="goodsList.length === 0"
      :button-text="submitBarText"
      safe-area-inset-bottom
      @submit="onSubmit"
    >
      <div class="totalPrice">
        <span class="pType">仍需剩余支付: </span>
        <span class="mPrice total">{{ formatPrice(totalMarketPrice) }}</span>
        <span class="pPrice total">{{ totalPointPrice.toFixed(2) }}</span>
        <span class="pType">{{ goodsList[0].pointType }}</span>
      </div>
    </van-submit-bar>
    <!-- <van-row class="">
      <van-col span="16">仍需剩余支付: <span>2222</span></van-col>
      <van-col span="8">
        <van-button block plain hairline type="info" @click="onLogout">退出当前账户</van-button>
      </van-col>
    </van-row> -->
    <!-- <van-cell-group title="1">
      <van-cell title="单元格" value="内容" />
    </van-cell-group>
    <van-cell-group title=" ">
      <van-cell title="单元格" value="内容" />
    </van-cell-group> -->
  </div>
</template>
<script>
import {
  Cell,
  CellGroup,
  Button,
  Card,
  Row,
  Col,
  SubmitBar,
  Toast
} from 'vant'
export default {

  components: {
    [Cell.name]: Cell,
    [CellGroup.name]: CellGroup,
    [Button.name]: Button,
    [Card.name]: Card,
    [Row.name]: Row,
    [Col.name]: Col,
    [SubmitBar.name]: SubmitBar,
    [Toast.name]: Toast
  },
  data() {
    return {
      goodsList: this.$store.getters.goods2order,
      useAddress: this.$store.getters.useAddress
    }
  },
  computed: {
    submitBarText() {
      const count = this.goodsList.reduce((total, item) => total + item.num, 0)
      return '提交订单' + (count ? `(${count})` : '')
    },

    totalMarketPrice() {
      return this.allMarketPrice + this.express
    },

    allMarketPrice() {
      return this.goodsList.reduce((total, item) => total + (item.marketPrice * item.num), 0)
    },

    allPointPrice() {
      return this.goodsList.reduce((total, item) => total + (item.pointPrice * item.num), 0)
    },

    totalPointPrice() {
      return this.allPointPrice + this.pointExpress
    },

    express() {
      const lest = this.$store.getters.mall.lest
      const over = this.$store.getters.mall.over
      return (this.allMarketPrice >= over ? 0 : lest)
    },

    pointExpress() {
      return (this.express * this.$store.getters.point.exchangeRate)
    }
  },
  methods: {
    formatPrice(marketPrice) {
      return '￥' + (marketPrice).toFixed(2)
    },
    onSubmit() {
      if (this.useAddress === null) {
        Toast.fail('请先添加收货地址')
      } else if (this.$store.getters.customer.remain < this.totalPointPrice) {
        Toast.fail('余额不足,无法购买')
      } else {
        var order = {
          'id': '无',
          'customerID': this.$store.getters.customer.id,
          'customerPhone': this.$store.getters.customer.phone,
          'mallID': this.$store.getters.mallID,
          'userID': '',
          'address': this.$store.getters.useAddress,
          'createTime': this.$parseTime(new Date()),
          'payMethod': 'ONLY_POINT',
          'orderGoodsList': {},
          'totalMarketPrice': this.totalMarketPrice,
          'totalPointPrice': this.totalPointPrice,
          'totalNum': this.goodsList.reduce((total, item) => total + item.num, 0),
          'pointType': this.$store.getters.point.name,
          'express': this.express,
          'pointExpress': this.pointExpress
        }
        var buyList = []
        for (var i = 0; i < this.goodsList.length; i++) {
          const item = this.goodsList[i]
          const orderGoods = {
            'goodsID': item.id,
            'name': item.name,
            'goodsType': item.goodsType,
            'describe': item.describe,
            'supplier': item.supplier,
            'image': item.image,
            'marketPrice': item.marketPrice,
            'pointPrice': item.pointPrice,
            'supplyPrice': item.supplyPrice,
            'pointType': item.pointType,
            'num': item.num,
            'payTime': this.$parseTime(new Date()),
            'outTime': null,
            'receiveTime': null,
            'doneTime': null,
            'source': item.source,
            'needDistribution': item.needDistribution,
            'expressCompany': '',
            'expressID': '',
            'backExpressCompany': '',
            'backExpressID': '',
            'orderStatus': '待发货',
            'express': this.express * ((item.pointPrice * item.num) / this.allPointPrice),
            'pointExpress': this.pointExpress * ((item.pointPrice * item.num) / this.allPointPrice)
          }
          order.orderGoodsList[item.id] = orderGoods
          buyList.push(item.id)
        }
        this.$store.dispatch('order/addOrder', order).then(() => {
          this.$store.dispatch('goods/afterBuy', buyList).then(() => {
            var c = this.$store.getters.customer
            c.remain = c.remain - this.totalPointPrice
            this.$store.dispatch('customer/setCustomer', c)
            this.$store.dispatch('goods/updateCartGoods')
            this.$router.push('order')
            Toast.success('购买成功')
          })
        })
      }
    }
  }
}

</script>

<style lang="less">
.orderConfirm{
    .van-submit-bar{
        width: 100%;
        height: 50px;
        max-width: 640px;
        left: auto;

        .van-button{
            width: auto;
        }
    }
    .mPrice{
        margin: 0 10px 0 3px;
        text-decoration: line-through;
        color: #666;
    }
    .pPrice{
        color: rgb(253, 114, 62);
        font-size: 20px;
    }
    .pType{
        font-size: 17px;
        margin-left: 3px;
        color: #323233;
    }
    .totalPrice{
        -webkit-box-flex: 1;
        -webkit-flex: 1;
        flex: 1;
        padding-left: 12px;
        color: #323233;
        text-align: left;
  }
}
</style>
