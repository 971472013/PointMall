const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  userID: state => state.user.userID,
  name: state => state.user.name,

  visitedViews: state => state.tagsView.visitedViews,
  cachedViews: state => state.tagsView.cachedViews,

  point: state => state.point.point,
  curMallID: state => state.mall.curMallID,
  curMall: state => state.mall.curMall,
  recordTable: state => state.point.recordTable,
  queryTable: state => state.point.queryTable,
  goodsList: state => state.goods.goodsList,

  subjectList: state => state.subject.subjectList,
  subjectGoodsList: state => state.subject.subjectGoodsList,
  curSubjectID: state => state.subject.curSubjectID,

  orderList: state => state.order.orderList,
  mallList: state => state.mall.mallList,
  refresh: state => state.mall.refresh
}
export default getters
