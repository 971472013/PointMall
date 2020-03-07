import request from '@/utils/request'

// 发送验证码
export function sms(data) {
  return request({
    url: '/api/mall/sms',
    method: 'post',
    data
  })
}

// 登录验证
export function login(data) {
  return request({
    url: '/api/mall/login',
    method: 'post',
    data
  })
}

// 更改地址信息
export function updateAddress(data) {
  return request({
    url: '/api/mall/updateAddress',
    method: 'post',
    data
  })
}

// 更改购物车信息
export function updateCartGoods(data) {
  return request({
    url: '/api/mall/updateCartGoods',
    method: 'post',
    data
  })
}

// 修改一个订单
export function updateOrderStatus(data) {
  return request({
    url: '/api/mall/updateOrderStatus',
    method: 'post',
    data
  })
}

export function backOrderGoods(data) {
  return request({
    url: '/api/mall/backOrderGoods',
    method: 'post',
    data
  })
}

// 创建一个订单
export function createOneOrder(data) {
  return request({
    url: '/api/mall/createOneOrder',
    method: 'post',
    data
  })
}

// 首页 为你推荐用
export function getAllGoodsByMallID(data) {
  return request({
    url: '/api/mall/getAllGoodsByMallID',
    method: 'post',
    data
  })
}

// 加载商城信息
export function getMallInfo(data) {
  return request({
    url: '/api/mall/getMallInfo',
    method: 'post',
    data
  })
}

// 加载用户信息
export function loadInfo(data) {
  return request({
    url: '/api/mall/loadInfo',
    method: 'post',
    data
  })
}

// 加载用户信息
export function getGoodsInfoByID(data) {
  return request({
    url: '/api/mall/getGoodsInfoByID',
    method: 'post',
    data
  })
}

export function getAllOrderByCustomerID(data) {
  return request({
    url: '/api/mall/getAllOrderByCustomerID',
    method: 'post',
    data
  })
}

export function search(data) {
  return request({
    url: '/api/mall/search',
    method: 'post',
    data
  })
}

export function entranceGoods(data) {
  return request({
    url: '/api/mall/entranceGoods',
    method: 'post',
    data
  })
}

export function subjectGoods(data) {
  return request({
    url: '/api/mall/subjectGoods',
    method: 'post',
    data
  })
}

