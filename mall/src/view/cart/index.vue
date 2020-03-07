<template>
  <div class="cart">
    <div class="mainCart">
      <van-checkbox-group ref="checkboxGroup" v-model="checkedGoods" class="card-goods">
        <van-swipe-cell
          v-for="(item,index) in goodsList"
          :key="item.id"
          class="card-goods__item"
          :name="item.id"
        >
          <van-checkbox :name="item.id">
            <!-- <div style="width:60%" @click.stop="clickGoodsImg(item.id)"> -->
            <van-card
              :title="item.name"
              :thumb="item.image.url"
              @click.stop="clickGoodsImg(item.id)"
            >
              <div slot="price">
                <span class="mPrice">{{ formatPrice(item.marketPrice) }}</span>
                <span class="pPrice">{{ item.pointPrice.toFixed(2) }}</span>
                <span class="pType">{{ item.pointType }}</span>
              </div>
              <div slot="num" @click.stop>
                <van-stepper v-model="item.num" min="1" @overlimit="overlimit()" />
              </div>
            </van-card>
            <!-- </div> -->
          </van-checkbox>
          <van-button
            slot="right"
            square
            text="删除"
            type="danger"
            style="height: 100%;"
            @click="deleteItem(index)"
          />
        </van-swipe-cell>
      </van-checkbox-group>
    </div>
    <van-submit-bar
      :disabled="!checkedGoods.length"
      :button-text="submitBarText"
      safe-area-inset-bottom
      @submit="onSubmit"
    >
      <van-checkbox v-model="checkedAll" style="position:relative" @click="toggleAll">全选</van-checkbox>
      <div class="totalPrice">
        <span class="pType">合计: </span>
        <span class="mPrice total">{{ formatPrice(totalMarketPrice) }}</span>
        <span class="pPrice total">{{ totalPointPrice }}</span>
        <span class="pType">{{ totalPType }}</span>
      </div>
    </van-submit-bar>
  </div>
</template>

<script>
import { Checkbox, CheckboxGroup, Card, SubmitBar, Toast, Stepper, SwipeCell, Button, Dialog } from 'vant'

export default {
  components: {
    [Card.name]: Card,
    [Checkbox.name]: Checkbox,
    [SubmitBar.name]: SubmitBar,
    [CheckboxGroup.name]: CheckboxGroup,
    [Stepper.name]: Stepper,
    [Toast.name]: Toast,
    [Button.name]: Button,
    [SwipeCell.name]: SwipeCell,
    [Dialog.name]: Dialog
  },

  data() {
    return {
      checkedGoods: [],
      checkedAll: false
      // goodsList: this.$store.getters.goodsList
    }
  },

  computed: {
    goodsList() {
      return this.$store.getters.cartGoodsList
    },
    submitBarText() {
      const count = this.goodsList.reduce((total, item) => total + (this.checkedGoods.indexOf(item.id) !== -1 ? (item.num) : 0), 0)
      return '结算' + (count ? `(${count})` : '')
    },

    totalMarketPrice() {
      return this.goodsList.reduce((total, item) => total + (this.checkedGoods.indexOf(item.id) !== -1 ? (item.marketPrice * item.num) : 0), 0)
    },

    totalPointPrice() {
      return this.goodsList.reduce((total, item) => total + (this.checkedGoods.indexOf(item.id) !== -1 ? (item.pointPrice * item.num) : 0), 0).toFixed(2)
    },

    totalPType() {
      return this.$store.getters.pointType
    }
  },

  destroyed() {
    this.$store.dispatch('goods/updateCartGoods')
  },

  methods: {
    formatPrice(marketPrice) {
      return '￥' + (marketPrice).toFixed(2)
    },

    clickGoodsImg(id) {
      this.$store.dispatch('goods/setCurGoods', id).then(() => {
        this.$router.push('goods')
      })
    },

    onSubmit() {
      var dataList = []
      for (var i = 0; i < this.goodsList.length; i++) {
        const item = this.goodsList[i]
        if (this.checkedGoods.indexOf(item.id) !== -1) {
          // const data = {
          //   id: item.id,
          //   name: item.name,
          //   describe: '约250g，2根',
          //   marketPrice: item.marketPrice,
          //   pointPrice: item.pointPrice,
          //   pointType: item.pointType,
          //   num: item.num,
          //   image: item.image
          // }
          const data = this.$deepCopy(item)
          dataList.push(data)
        }
      }
      this.$store.dispatch('goods/setGoods2order', dataList).then(() => {
        this.$router.push('orderConfirm')
      }).catch(() => {})
    },

    deleteItem(index) {
      Dialog.confirm({
        title: '确认删除？',
        message: '确定要从购物车中移除此物品么？'
      }).then(() => {
        // on confirm
        this.goodsList.splice(index, 1)
      }).catch(() => {
        // on cancel
      })
    },

    overlimit() {
      Toast('物品不能再减少了哦')
    },

    toggleAll() {
      if (this.checkedAll === false) {
        this.$refs.checkboxGroup.toggleAll(true)
      } else {
        this.$refs.checkboxGroup.toggleAll()
      }
    }
  }
}
</script>

<style lang="less">
.cart{
  margin-bottom: 50px;

  .mainCart{
    background-color: #fff;
    height: 100%;
  }
  .van-submit-bar{
    width: 100%;
    height: 50px;
    max-width: 640px;
    left: auto;
  }
  // .mPrice.total{
  //   margin: 0 10px 0 0px;
  // }
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
  .totalPrice{
    -webkit-box-flex: 1;
      -webkit-flex: 1;
      flex: 1;
      padding-right: 12px;
      color: #323233;
      text-align: right;
  }

  .van-card {
    padding: 8px 16px 8px 0;
    margin-left:30px
  }

  .card-goods {
    padding: 10px 0;
    background-color: #fff;

    &__item {
      position: relative;
      background-color: #fafafa;

      .van-checkbox__label {
        width: 100%;
        margin-left: 11px;
        height: auto; // temp
        padding: 0 10px 0 15px;
        box-sizing: border-box;
      }

      .van-checkbox__icon {
        top: 50%;
        left: 16px;
        z-index: 1;
        position: absolute;
        margin-top: -10px;
      }

      // .van-card__marketPrice {
      //   color: #f44;
      // }
    }
  }
}

</style>
