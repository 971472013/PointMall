import { getAllGoods, getMallGoods, toggle, deleteMallGoods, updatePrice,
  addToMall, updateGoods, filterGoods, createGoods } from '@/api/goods'
import Vue from 'vue'

const state = {
  goodsList: []
}

const mutations = {
  SET_GOODS_LIST: (state, data) => {
    state.goodsList = data
    Vue.set(state, 'goodsList', state.goodsList)
  },
  TOGGLE: (state, data) => {
    state.goodsList[data.index].goodsStatus = data.to
    Vue.set(state, 'goodsList', state.goodsList)
  },
  DELETE_IN_MALL: (state, index) => {
    state.goodsList.splice(index, 1)
    Vue.set(state, 'goodsList', state.goodsList)
  },
  UPDATE_PRICE: (state, data) => {
    state.goodsList[data.index].pointPrice = data.pointPrice
    state.goodsList[data.index].payMethod = data.payMethod
    Vue.set(state, 'goodsList', state.goodsList)
  },
  UPDATE_GOODS: (state, data) => {
    state.goodsList.splice(data.index, 1, data.data)
    Vue.set(state, 'goodsList', state.goodsList)
  },
  CREATE_GOODS: (state, data) => {
    state.goodsList.unshift(data)
    Vue.set(state, 'goodsList', state.goodsList)
  }
}

const actions = {
  getAllGoods({ commit, rootGetters }) {
    return new Promise((resolve, reject) => {
      getAllGoods({ userID: rootGetters.userID, mallID: rootGetters.curMallID }).then((resp) => {
        const { data } = resp
        console.log(rootGetters.curMallID)
        commit('SET_GOODS_LIST', data)
        resolve()
      })
    })
  },
  filterGoods({ commit, rootGetters }, data) {
    return new Promise((resolve, reject) => {
      filterGoods({ userID: rootGetters.userID, mallID: rootGetters.curMallID, ...data }).then((resp) => {
        const { data } = resp
        commit('SET_GOODS_LIST', data)
        resolve()
      })
    })
  },
  getMallGoods({ commit, rootGetters }) {
    return new Promise((resolve, reject) => {
      getMallGoods({ userID: rootGetters.userID, mallID: rootGetters.curMallID }).then((resp) => {
        const { data } = resp
        commit('SET_GOODS_LIST', data)
        resolve()
      })
    })
  },
  addToMall({ commit, rootGetters }, data) {
    return new Promise((resolve, reject) => {
      addToMall({ ...data, mallID: rootGetters.curMallID }).then((resp) => {
        resolve()
      })
    })
  },
  toggle({ commit, rootGetters }, data) {
    return new Promise((resolve, reject) => {
      toggle({ mallID: rootGetters.curMallID, goodsID: data.id, to: data.to }).then(() => {
        commit('TOGGLE', data)
        resolve()
      })
    })
  },
  deleteMallGoods({ commit, rootGetters }, data) {
    return new Promise((resolve, reject) => {
      deleteMallGoods({ mallID: rootGetters.curMallID, goodsID: data.id }).then(() => {
        commit('DELETE_IN_MALL', data.index)
        resolve()
      })
    })
  },
  updatePrice({ commit, rootGetters }, data) {
    return new Promise((resolve, reject) => {
      updatePrice({ mallID: rootGetters.curMallID, ...data }).then(() => {
        commit('UPDATE_PRICE', data)
        resolve()
      })
    })
  },
  updateGoods({ commit }, data) {
    return new Promise((resolve, reject) => {
      updateGoods(data.data).then(() => {
        commit('UPDATE_GOODS', data)
        resolve()
      })
    })
  },
  createGoods({ commit, rootGetters }, data) {
    return new Promise((resolve, reject) => {
      createGoods({ ...data, belongUser: rootGetters.userID }).then(resp => {
        const { data } = resp
        commit('CREATE_GOODS', data)
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
