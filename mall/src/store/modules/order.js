import { updateOrderStatus, createOneOrder, getAllOrderByCustomerID, backOrderGoods } from '@/api'

import Vue from 'vue'

const state = {
  orderList: []
}

const mutations = {
  ADD_ORDER: (state, data) => {
    state.orderList.unshift(data)
  },
  SET_ORDER_LIST: (state, data) => {
    Vue.set(state, 'orderList', data)
    // state.orderList = data
  }
}

const actions = {
  addOrder({ commit, rootGetters }, data) {
    // console.log(23322)
    return new Promise((resolve, rejcet) => {
      createOneOrder(data).then((resp) => {
        const { data } = resp
        commit('ADD_ORDER', data)
        resolve()
      })
    }).catch(() => {})
  },
  setOrderList({ commit }, data) {
    return new Promise((resolve, rejcet) => {
      commit('SET_ORDER_LIST', data)
      resolve()
    }).catch(() => {})
  },
  getOrderList({ commit, rootGetters }) {
    return new Promise((resolve, rejcet) => {
      getAllOrderByCustomerID({ customerID: rootGetters.customer.id }).then((resp) => {
        const { data } = resp
        commit('SET_ORDER_LIST', data)
        resolve()
      })
    }).catch(() => { })
  },
  updateOrderStatus({ commit, rootGetters }, data) {
    return new Promise((resolve, rejcet) => {
      updateOrderStatus(data).then((resp) => {
        // const { data } = resp
        // commit('SET_ORDER_LIST', data)
        resolve()
      })
    }).catch(() => { })
  },
  backOrderGoods({ commit, rootGetters }, data) {
    return new Promise((resolve, rejcet) => {
      backOrderGoods(data).then((resp) => {
        // const { data } = resp
        // commit('SET_ORDER_LIST', data)
        resolve()
      })
    }).catch(() => { })
  }
}

export default {
  namespaced: true,
  state,
  actions,
  mutations
}
