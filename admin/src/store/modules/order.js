import { getAllOrderByMallID, updateOrder, back, filterOrder, rejectBack } from '@/api/order'
import Vue from 'vue'
const state = {
  orderList: []
}

const mutations = {
  SET_ORDER_LIST: (state, data) => {
    state.orderList = data
    Vue.set(state, 'orderList', state.orderList)
  },
  UPDATE: (state, data) => {
    const index = state.orderList.findIndex((v, i, arr) => {
      if (v.id === data.id) {
        return true
      }
    })
    state.orderList.splice(index, 1, data)
    Vue.set(state, 'orderList', state.orderList)
  }
}

const actions = {
  getAllOrderByMallID({ commit, rootGetters }) {
    return new Promise((resolve, reject) => {
      getAllOrderByMallID({ mallID: rootGetters.curMallID }).then(resp => {
        const { data } = resp
        commit('SET_ORDER_LIST', data)
        resolve()
      })
    })
  },
  updateOrder({ commit, rootGetters }, data) {
    return new Promise((resolve, reject) => {
      updateOrder(data).then(resp => {
        commit('UPDATE', data)
        resolve()
      })
    })
  },
  back({ commit, rootGetters }, data) {
    return new Promise((resolve, reject) => {
      back(data).then(resp => {
        resolve()
      })
    })
  },
  rejectBack({ commit, rootGetters }, data) {
    return new Promise((resolve, reject) => {
      rejectBack(data).then(resp => {
        resolve()
      })
    })
  },
  filterOrder({ commit, rootGetters }, data) {
    return new Promise((resolve, reject) => {
      filterOrder({ ...data, mallID: rootGetters.curMallID }).then(resp => {
        const { data } = resp
        commit('SET_ORDER_LIST', data)
        resolve()
      })
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
