import { getMallInfo } from '@/api'
import Vue from 'vue'

const state = {
  mall: {},
  point: undefined,
  mallID: undefined
}
const mutations = {
  SET_MALL: (state, data) => {
    state.mall = data
    state.mallID = data.id
    state.point = data.point
    Vue.set(state, 'mall', data)
    Vue.set(state, 'mallID', data.id)
    Vue.set(state, 'point', data.point)
  }
}

const actions = {
  setMall({ commit }, id) {
    return new Promise((res) => {
      getMallInfo({ mallID: id }).then((resp) => {
        const { data } = resp
        commit('SET_MALL', data)
        res()
      })
    })
  }
}

export default {
  namespaced: true,
  state,
  actions,
  mutations
}
