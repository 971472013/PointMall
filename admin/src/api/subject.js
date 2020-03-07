import request from '@/utils/request'

export function createSubject(data) {
  return request({
    url: '/api/admin/createSubject',
    method: 'post',
    data
  })
}

export function getSubjectByMallID(data) {
  return request({
    url: '/api/admin/getSubjectByMallID',
    method: 'post',
    data
  })
}

export function filterGoodsInSubject(data) {
  return request({
    url: '/api/admin/filterGoodsInSubject',
    method: 'post',
    data
  })
}

export function filterSubject(data) {
  return request({
    url: '/api/admin/filterSubject',
    method: 'post',
    data
  })
}

export function deleteSubject(data) {
  return request({
    url: '/api/admin/deleteSubject',
    method: 'post',
    data
  })
}

export function updateSubject(data) {
  return request({
    url: '/api/admin/updateSubject',
    method: 'post',
    data
  })
}

export function addGoodsToSubject(data) {
  return request({
    url: '/api/admin/addGoodsToSubject',
    method: 'post',
    data
  })
}

export function goodsMove(data) {
  return request({
    url: '/api/admin/goodsMove',
    method: 'post',
    data
  })
}

export function moveOut(data) {
  return request({
    url: '/api/admin/moveOut',
    method: 'post',
    data
  })
}

export function getMallGoodsWithSubject(data) {
  return request({
    url: '/api/admin/getMallGoodsWithSubject',
    method: 'post',
    data
  })
}
