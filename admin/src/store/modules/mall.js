// import Cookies from 'js-cookie'
import { getMallInfo, updateMallFitment, updateMallBasic, createMall } from '@/api/mall'
import Vue from 'vue'

const state = {
  curMallID: undefined,
  curMall: undefined,
  mallList: [],
  refresh: false
}
const mutations = {
  SET_REFRESH: (state, data) => {
    Vue.set(state, 'refresh', data)
  },
  SET_CUR_MALLID: (state, id) => {
    // state.curMallID = id
    Vue.set(state, 'curMallID', id)
  },
  SET_CUR_MALL: (state, data) => {
    // state.curMall = data
    Vue.set(state, 'curMall', data)
  },
  SET_MALL_LIST: (state, data) => {
    // state.mallList = data
    Vue.set(state, 'mallList', data)
  },
  UPDATE_MALL_LIST: (state, data) => {
    const index = state.mallList.findIndex((v, i, arr) => {
      if (v.id === data.id) {
        return true
      }
    })
    state.mallList.splice(index, 1, { id: data.id, title: data.title })
  },
  ADD_MALL: (state, data) => {
    state.mallList.push(data)
    Vue.set(state, 'mallList', state.mallList)
  }
}

const actions = {
  refresh({ commit }, data) {
    return new Promise((res) => {
      commit('SET_REFRESH', data)
      res()
    })
  },
  setCurMall({ commit }, id) {
    return new Promise((res) => {
      getMallInfo({ mallID: id }).then((resp) => {
        const { data } = resp
        commit('SET_CUR_MALLID', data.id)
        commit('SET_CUR_MALL', data)
        res(data.point)
      })
    })
  },
  createMall({ commit, rootGetters }, data) {
    return new Promise((res) => {
      createMall({ ...data, userID: rootGetters.userID }).then((resp) => {
        const { data } = resp
        commit('SET_CUR_MALLID', data.id)
        commit('SET_CUR_MALL', data)
        commit('ADD_MALL', { id: data.id, title: data.title })
        res(data)
      })
    })
  },
  setMallList({ commit }, data) {
    return new Promise((res) => {
      commit('SET_MALL_LIST', data)
      res()
    })
  },
  updateMallFitment({ commit }, data) {
    return new Promise((res) => {
      updateMallFitment(data).then((resp) => {
        commit('SET_CUR_MALL', data)
        res()
      })
    })
  },
  updateBasic({ commit, rootGetters }, data) {
    return new Promise((res) => {
      updateMallBasic({ ...data, userID: rootGetters.userID }).then((resp) => {
        commit('SET_CUR_MALL', data)
        commit('UPDATE_MALL_LIST', data)
        res()
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
