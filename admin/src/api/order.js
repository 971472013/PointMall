import request from '@/utils/request'

export function getAllOrderByMallID(data) {
  return request({
    url: '/api/admin/getAllOrderByMallID',
    method: 'post',
    data
  })
}

export function updateOrder(data) {
  return request({
    url: '/api/admin/updateOrder',
    method: 'post',
    data
  })
}

export function getNearMonthOrder(data) {
  return request({
    url: '/api/admin/getNearMonthOrder',
    method: 'post',
    data
  })
}

export function back(data) {
  return request({
    url: '/api/admin/backConfirm',
    method: 'post',
    data
  })
}

export function rejectBack(data) {
  return request({
    url: '/api/admin/rejectBack',
    method: 'post',
    data
  })
}

export function filterOrder(data) {
  return request({
    url: '/api/admin/filterOrder',
    method: 'post',
    data
  })
}
