import { updateCartGoods, getAllGoodsByMallID, subjectGoods, entranceGoods, search } from '@/api'
import Vue from 'vue'

const state = {
  goods2order: [],
  goodsList: [],
  cartGoodsList: [],
  curGoods: undefined,
  searchTitle: '',
  searchList: [],
  url: ''
}

const mutations = {
  SET_CUR_GOODS: (state, data) => {
    state.curGoods = data
    Vue.set(state, 'curGoods', data)
  },
  SET_GOODS_LIST: (state, data) => {
    state.goodsList = data
    Vue.set(state, 'goodsList', data)
  },
  SET_CART_GOODS_LIST: (state, data) => {
    state.cartGoodsList = data
    Vue.set(state, 'cartGoodsList', data)
  },
  SET_GOODS2ORDER: (state, data) => {
    state.goods2order = data
    Vue.set(state, 'goods2order', data)
  },
  CLEAR: (state) => {
    Vue.set(state, 'goods2order', [])
    Vue.set(state, 'goodsList', [])
    Vue.set(state, 'cartGoodsList', [])
  },
  CLEAR_SEARCH: (state) => {
    Vue.set(state, 'searchList', [])
    Vue.set(state, 'searTitle', '')
    Vue.set(state, 'url', '')
  },
  SET_SEARCH: (state, data) => {
    Vue.set(state, 'searchList', data.list)
    Vue.set(state, 'searchTitle', data.title)
    Vue.set(state, 'url', data.url)
  }
}

const actions = {
  subjectGoods({ commit, rootGetters }, data) {
    const title = data.title
    const url = data.url
    return new Promise((resolve, reject) => {
      subjectGoods({ mallID: rootGetters.mallID, subjectID: data.subjectID }).then((resp) => {
        const { data } = resp
        commit('SET_SEARCH', { list: data, title: title, url: url })
        resolve()
      })
    })
  },
  entranceGoods({ commit, rootGetters }, data) {
    const title = data.title
    return new Promise((resolve, reject) => {
      entranceGoods({ mallID: rootGetters.mallID, rxg: data.rxg }).then((resp) => {
        const { data } = resp
        commit('SET_SEARCH', { list: data, title: title, url: '' })
        resolve()
      })
    })
  },
  search({ commit, rootGetters }, data) {
    const title = data.title
    return new Promise((resolve, reject) => {
      search({ mallID: rootGetters.mallID, rxg: data.rxg }).then((resp) => {
        const { data } = resp
        commit('SET_SEARCH', { list: data, title: title, url: '' })
        resolve()
      })
    })
  },
  clearSearch({ commit }) {
    return new Promise((resolve, reject) => {
      commit('CLEAR_SEARCH')
      resolve()
    })
  },
  clear({ commit }) {
    return new Promise((resolve, reject) => {
      commit('CLEAR')
      resolve()
    })
  },
  setCurGoods({ commit, rootGetters }, data) {
    return new Promise((resolve, reject) => {
      commit('SET_CUR_GOODS', data)
      resolve()
    })
  },
  getAllGoodsByMallID({ commit, rootGetters }) {
    return new Promise((resolve, reject) => {
      getAllGoodsByMallID({ mallID: rootGetters.mallID }).then((resp) => {
        const { data } = resp
        commit('SET_GOODS_LIST', data)
        resolve()
      })
    })
  },
  setGoods2order({ commit }, data) {
    return new Promise((resolve, reject) => {
      commit('SET_GOODS2ORDER', data)
      resolve()
    }).catch(() => {})
  },
  setGoodsList({ commit }, data) {
    return new Promise((resolve, reject) => {
      commit('SET_GOODS_LIST', data)
      resolve()
    }).catch(() => {})
  },
  setCartGoodsList({ commit }, data) {
    return new Promise((resolve, reject) => {
      commit('SET_CART_GOODS_LIST', data)
      resolve()
    }).catch(() => { })
  },
  updateCartGoods({ commit, rootGetters }) {
    // console.log(rootGetters)
    const id = rootGetters.customer.id
    const goodsList = rootGetters.cartGoodsList
    var dataList = []
    for (var i = 0; i < goodsList.length; i++) {
      dataList.push({ key: goodsList[i].id, value: goodsList[i].num })
    }
    const data = {
      id: id,
      goodsList: dataList
    }
    updateCartGoods(data)
  },
  afterBuy({ commit, rootGetters }, data) {
    return new Promise((resolve, reject) => {
      const goodsList = rootGetters.cartGoodsList
      for (var i = 0; i < data.length; i++) {
        const index = goodsList.findIndex((v, i, arr) => {
          if (v.id === data[i]) {
            return true
          }
        })
        goodsList.splice(index, 1)
      }
      commit('SET_CART_GOODS_LIST', goodsList)
      resolve()
    }).catch(() => { })
  }
}

export default {
  namespaced: true,
  state,
  actions,
  mutations
}
