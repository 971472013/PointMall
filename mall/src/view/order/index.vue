<template>
  <div class="order">
    <van-tabs v-model="active" @click="onClick">
      <van-tab v-for="(item,index) in typeList" :key="index" :title="item" :name="item">
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <van-panel v-for="(order,oIndex) in orderList" :key="oIndex" class="orderGroup">
            <div slot="default">
              <van-card
                v-for="(pItem,pIndex) in order.orderGoodsList"
                :key="pIndex"
                :num="pItem.num"
                :title="pItem.name"
                :thumb="pItem.image.url"
                :tag="pItem.orderStatus"
                @click="showPopup(order,pItem,oIndex,pIndex)"
              >
                <div slot="price">
                  <span class="mPrice">{{ formatPrice(pItem.marketPrice) }}</span>
                  <span class="pPrice">{{ pItem.pointPrice.toFixed(2) }}</span>
                  <span class="pType">{{ pItem.pointType }}</span>
                </div>
              </van-card>
              <div style="float:right;margin:10px 10px 10px 0">
                共{{ totalNum(order) }}件商品,合计<span class="mPrice">{{ formatPrice(totalMarketPrice(order)) }}</span>
                <span class="pPrice">{{ totalPointPrice(order) }}</span>
                <span class="pType">{{ $store.getters.point.name }}</span>
              </div>
            </div>
          </van-panel>
        </van-list>
      </van-tab>
    </van-tabs>
    <van-popup v-model="backShow" style="width: 80%;padding:30px 20px 30px 20px" round>
      <van-form validate-first @submit="onSubmit">
        <van-radio-group v-model="radio">
          <van-radio style="margin-bottom:10px" name="1">仅退款</van-radio>
          <van-radio name="2">退货同时退款</van-radio>
        </van-radio-group>
        <div style="height:140px">
          <van-field
            v-show="radio === '2'"
            v-model="backExpressCompany"
            name="backExpressCompany"
            label="快递公司"
            placeholder="请填写退货物流公司名称"
            required
            :rules="[{ validator:validator1, message: '必填项' }]"
          />
          <van-field
            v-show="radio === '2'"
            v-model="backExpressID"
            name="backExpressID"
            label="快递单号"
            placeholder="请填写退货物流单号"
            required
            :rules="[{ validator:validator2, message: '必填项' }]"
          />
        </div>
        <div style="margin: 16px;">
          <van-button round block type="info" native-type="submit">
            提交
          </van-button>
        </div>
      </van-form>
    </van-popup>
    <van-popup v-model="show" style="width: 80%;padding:30px 20px 30px 20px" round>
      <van-card
        v-if="show"
        :num="temp.num"
        :title="temp.name"
        :thumb="temp.image.url"
        :tag="temp.orderStatus"
      >
        <div slot="price">
          <span class="mPrice">{{ formatPrice(temp.marketPrice) }}</span>
          <span class="pPrice">{{ temp.pointPrice.toFixed(2) }}</span>
          <span class="pType">{{ temp.pointType }}</span>
        </div>
      </van-card>
      <van-collapse v-if="show" v-model="activeNames">
        <van-collapse-item title="订单信息" name="1">
          <van-cell-group>
            <van-cell title="订单创建时间">
              <template slot="label">
                {{ tOrder.createTime | parseTime }}
              </template>
            </van-cell>
            <van-cell title="运费">
              <template slot="label">
                {{ temp.express.toFixed(2) }} 元 = {{ temp.pointExpress.toFixed(2) }} {{ $store.getters.point.name }}
              </template>
            </van-cell>
            <van-cell title="总价">
              <template slot="label">
                <span class="mPrice">{{ formatPrice(temp.marketPrice*temp.num+temp.express) }}</span>
                <span class="pPrice">{{ (temp.pointPrice*temp.num+temp.pointExpress).toFixed(2) }}</span>
                <span style="color:#323233;" class="pType">{{ temp.pointType }}</span>
              </template>
            </van-cell>
            <van-cell title="收货人姓名">
              <template slot="label">
                {{ tOrder.address.name }}
              </template>
            </van-cell>
            <van-cell title="收货人联系方式">
              <template slot="label">
                {{ tOrder.address.tel }}
              </template>
            </van-cell>
            <van-cell title="收货地址">
              <template slot="label">
                {{ tOrder.address.address }}
              </template>
            </van-cell>
            <van-cell title="快递公司">
              <template slot="label">
                {{ temp.expressCompany }}
              </template>
            </van-cell>
            <van-cell title="快递单号">
              <template slot="label">
                {{ temp.expressID }}
              </template>
            </van-cell>
          </van-cell-group>
        </van-collapse-item>
      </van-collapse>
      <div style="float: right;margin: 40px 0 0 0;">
        <van-button size="small" @click="show = false">关闭</van-button>
        <van-button v-if="temp.orderStatus === '待收货'" size="small" type="primary" @click="onConfirm()">确认收货</van-button>
        <van-button v-if="temp.orderStatus === '已完成'" size="small" type="danger" @click="onBack()">申请退款</van-button>
      </div>
    </van-popup>
    <div v-if="orderList.length === 0" class="empty-div">
      <!-- <em class="empty" /> -->
      <van-row>
        <van-image
          style="margin-left:28px"
          fit="cover"
          src="empty.png"
        />
      </van-row>
      <van-row>
        <span style="font-size: 18px;color: #969696;margin:0 auto;">暂无订单</span>
      </van-row>
    </div>
  </div>
</template>
<script>
import {
  Tabs,
  Tab,
  Image,
  Row,
  List,
  Card,
  CellGroup,
  Panel,
  Popup,
  Button,
  Collapse,
  CollapseItem,
  Cell,
  Toast,
  Radio,
  RadioGroup,
  Form,
  Field
} from 'vant'
// import { mapGetters } from 'vuex'
export default {

  components: {
    [Tabs.name]: Tabs,
    [Tab.name]: Tab,
    [Image.name]: Image,
    [Row.name]: Row,
    [List.name]: List,
    [Card.name]: Card,
    [CellGroup.name]: CellGroup,
    [Cell.name]: Cell,
    [Panel.name]: Panel,
    [Popup.name]: Popup,
    [Button.name]: Button,
    [Collapse.name]: Collapse,
    [CollapseItem.name]: CollapseItem,
    [Toast.name]: Toast,
    [Radio.name]: Radio,
    [RadioGroup.name]: RadioGroup,
    [Form.name]: Form,
    [Field.name]: Field
  },
  data() {
    return {
      typeList: ['全部', '待发货', '待收货', '已完成', '售后中'],
      active: '全部',
      orderList: [],
      loading: false,
      finished: false,
      show: false,
      backShow: false,
      temp: {},
      tOrder: {},
      activeNames: ['1'],
      i: -1,
      j: -1,
      radio: '1',
      backExpressID: '',
      backExpressCompany: ''
    }
  },

  computed: {
    list() {
      return this.$store.getters.orderList
    }
  },
  created() {
    Toast.loading({
      duration: 0, // 持续展示 toast
      forbidClick: true,
      message: '数据加载中...'
    })
    this.$store.dispatch('order/getOrderList').then(() => [
      Toast.clear()
    ])
  },
  mounted() {
    this.orderList = this.list
  },
  methods: {
    validator1(val) {
      if (this.radio === '1') {
        return true
      } else {
        return val !== ''
      }
    },
    validator2(val) {
      if (this.radio === '1') {
        return true
      } else {
        return val !== ''
      }
    },
    totalMarketPrice(item) {
      var ans = 0
      for (var i in item.orderGoodsList) {
        ans += (item.orderGoodsList[i].marketPrice * item.orderGoodsList[i].num + item.orderGoodsList[i].express)
      }
      return ans
    },
    totalPointPrice(item) {
      var ans = 0
      for (var i in item.orderGoodsList) {
        ans += (item.orderGoodsList[i].pointPrice * item.orderGoodsList[i].num + item.orderGoodsList[i].pointExpress)
      }
      return ans.toFixed(2)
    },
    totalNum(item) {
      var ans = 0
      for (var i in item.orderGoodsList) {
        ans += item.orderGoodsList[i].num
      }
      return ans
    },
    formatPrice(marketPrice) {
      return '￥' + (marketPrice).toFixed(2)
    },
    showPopup(tOrder, temp, i, j) {
      this.tOrder = tOrder
      this.temp = temp
      this.show = true
      this.i = i
      this.j = j
      console.log(this.tOrder)
      console.log(this.temp)
    },
    onLoad() {
      this.finished = true
    },
    onClick(name, title) {
      if (title === '全部') {
        this.orderList = this.list
      } else {
        var newList = []
        for (var i = 0; i < this.list.length; i++) {
          const orderGoodsList = this.list[i].orderGoodsList
          var each = []
          for (var j in orderGoodsList) {
            if (orderGoodsList[j].orderStatus === title || (orderGoodsList[j].orderStatus === '退款失败' && title === '已完成')) {
              each.push(this.list[i].orderGoodsList[j])
            }
          }
          if (each.length !== 0) {
            var temp = this.$deepCopy(this.list[i])
            temp.orderGoodsList = each
            newList.push(temp)
          }
        }
        this.orderList = newList
      }
    },
    onConfirm(to) {
      this.$store.dispatch('order/updateOrderStatus', { orderID: this.tOrder.id, goodsID: this.temp.goodsID, to: '已完成' }).then(() => {
        this.orderList[this.i].orderGoodsList[this.j].orderStatus = '已完成'
        if (this.active !== '全部') {
          var newList = {}
          for (var each in this.orderList[this.i].orderGoodsList) {
            if (Number(each) !== this.j) {
              newList[this.orderList[this.i].orderGoodsList[each].goodsID] = this.orderList[this.i].orderGoodsList[each]
            }
          }
          if (Object.keys(newList).length === 0) {
            this.orderList.splice(this.i, 1)
          } else {
            this.orderList[this.i].orderGoodsList = newList
          }
        }
        this.show = false
        this.$forceUpdate()
        Toast.success('确认收货成功')
      })
    },
    onBack() {
      this.backExpressID = ''
      this.backExpressCompany = ''
      this.backShow = true
    },
    onSubmit(values) {
      this.$store.dispatch('order/backOrderGoods', { orderID: this.tOrder.id, goodsID: this.temp.goodsID, ...values }).then(() => {
        this.orderList[this.i].orderGoodsList[this.j].orderStatus = '售后中'
        if (this.active !== '全部') {
          var newList = {}
          for (var each in this.orderList[this.i].orderGoodsList) {
            if (Number(each) !== this.j) {
              newList[this.orderList[this.i].orderGoodsList[each].goodsID] = this.orderList[this.i].orderGoodsList[each]
            }
          }
          if (Object.keys(newList).length === 0) {
            this.orderList.splice(this.i, 1)
          } else {
            this.orderList[this.i].orderGoodsList = newList
          }
        }
        this.show = false
        this.backShow = false
        this.$forceUpdate()
        Toast.success('申请退款成功')
      })
    }
  }
}

</script>

<style lang="less">
.order{
    height: 100%;
    // box-sizing: border-box;
    // position: absolute;
    max-width: 640px;
    width: 100%;

    .orderGroup{
      margin:20px 20px;
      border-radius: 20px;
      padding-bottom: 30px
    }

    .van-panel__content{
      padding: 30px 20px;
    }
    .van-cell.van-panel__header{
      border-radius: 20px 20px 0 0
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
      margin-left: 3px
    }

    // .van-card:first-child {
    //   border-radius: 20px 20px 0 0
    // }
    // .van-card:last-child {
    //   border-radius: 0px 0px 20px 20px
    // }
    .van-card:not(:first-child) {
      margin-top: 8px;
    }

    .van-card{
      background-color: #ffffff;
    }

    .empty{
    width: 40%;
    height: 37%;
    margin-left: 30%;
    margin-top: 20%;
    // margin-bottom: 1.125rem;
    display: block;
    background-image: url(data:image/png;base64,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);
    background-size: 100% 100%;
    background-repeat: no-repeat
    }
    .empty-div{
        width: 100%;
        height: 100%;
        text-align: center;
        // position: absolute;
        // top:0;
        // left: 0;
    }
    // .van-tabs__content{
    //     height: 100%;
    //     box-sizing: border-box;
    //     position: absolute;
    //     max-width: 640px;
    //     width: 100%;

    // }
}
</style>
