const getters = {
  cartNum: state => {
    return state.goods.cartGoodsList.reduce((total, item) => total + item.num, 0)
  },
  token: state => state.customer.token,
  goodsList: state => state.goods.goodsList,
  cartGoodsList: state => state.goods.cartGoodsList,
  orderList: state => state.order.orderList,
  customer: state => state.customer.customer,
  goods2order: state => state.goods.goods2order,
  curGoods: state => state.goods.curGoods,
  addressList: state => state.address.addressList,
  tempAddress: state => state.address.tempAddress,
  useAddress: state => state.address.useAddress,
  mall: state => state.setting.mall,
  point: state => state.setting.point,
  mallID: state => state.setting.mallID,
  searchTitle: state => state.goods.searchTitle,
  searchList: state => state.goods.searchList,
  url: state => state.goods.url
}
export default getters
