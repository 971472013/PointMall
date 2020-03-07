import request from '@/utils/request'

export function getMallInfo(data) {
  return request({
    url: '/api/admin/getMallInfo',
    method: 'post',
    data
  })
}

export function updateMallFitment(data) {
  return request({
    url: '/api/admin/updateMallFitment',
    method: 'post',
    data
  })
}

export function updateMallBasic(data) {
  return request({
    url: '/api/admin/updateMallBasic',
    method: 'post',
    data
  })
}

export function getTodayCustomerNum(data) {
  return request({
    url: '/api/admin/getTodayCustomerNum',
    method: 'post',
    data
  })
}

export function createMall(data) {
  return request({
    url: '/api/admin/createMall',
    method: 'post',
    data
  })
}
