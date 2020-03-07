import request from '@/utils/request'

export function getAllGoods(data) {
  return request({
    url: '/api/admin/getAllGoods',
    method: 'post',
    data
  })
}

export function filterGoods(data) {
  return request({
    url: '/api/admin/filterGoods',
    method: 'post',
    data
  })
}

export function getMallGoods(data) {
  return request({
    url: '/api/admin/getMallGoods',
    method: 'post',
    data
  })
}

export function toggle(data) {
  return request({
    url: '/api/admin/toggleGoodsStatus',
    method: 'post',
    data
  })
}

export function deleteMallGoods(data) {
  return request({
    url: '/api/admin/deleteMallGoods',
    method: 'post',
    data
  })
}

export function updatePrice(data) {
  return request({
    url: '/api/admin/updatePrice',
    method: 'post',
    data
  })
}

export function addToMall(data) {
  return request({
    url: '/api/admin/addToMall',
    method: 'post',
    data
  })
}

export function updateGoods(data) {
  return request({
    url: '/api/admin/updateGoods',
    method: 'post',
    data
  })
}

export function createGoods(data) {
  return request({
    url: '/api/admin/createGoods',
    method: 'post',
    data
  })
}
