import request from '@/utils/request'

export function getPointInfo(data) {
  return request({
    url: '/api/admin/getPointInfo',
    method: 'post',
    data
  })
}

export function updatePoint(data) {
  return request({
    url: '/api/admin/updatePoint',
    method: 'post',
    data
  })
}

export function deletePoint(data) {
  return request({
    url: '/api/admin/updatePoint',
    method: 'post',
    data
  })
}

export function loadDistributeRecord(data) {
  return request({
    url: '/api/admin/loadDistributeRecord',
    method: 'post',
    data
  })
}

export function distributePointOne(data) {
  return request({
    url: '/api/admin/distributePointOne',
    method: 'post',
    data
  })
}

export function newCustomerDistributePointOne(data) {
  return request({
    url: '/api/admin/newCustomerDistributePointOne',
    method: 'post',
    data
  })
}

export function queryPoint(data) {
  return request({
    url: '/api/admin/queryPoint',
    method: 'post',
    data
  })
}

export function queryAllPoint(data) {
  return request({
    url: '/api/admin/queryAllPoint',
    method: 'post',
    data
  })
}

