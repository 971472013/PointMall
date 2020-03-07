import { sms, login } from '@/api'
import Vue from 'vue'

const state = {
  customer: undefined,
  token: sessionStorage.getItem('token')
}

const mutations = {
  SET_CUSTOMER: (state, data) => {
    Vue.set(state, 'customer', data)
    // state.customer = data
  },
  SET_TOKEN: (state, data) => {
    if (data === null) {
      sessionStorage.removeItem('token')
      state.token = null
    } else {
      state.token = data
      sessionStorage.setItem('token', state.token)
    }
  }
}

const actions = {
  sms({ commit, rootGetters }, phone) {
    return new Promise((resolve, reject) => {
      sms({ phone, mallID: rootGetters.mallID }).then(resp => {
        resolve()
        // const { data } = resp
      }).catch(error => {
        reject(error)
      })
    })
  },
  login({ commit, rootGetters }, data) {
    return new Promise((resolve, reject) => {
      login({ ...data, mallID: rootGetters.mallID }).then(resp => {
        const { data } = resp
        commit('SET_TOKEN', data.token)
        commit('SET_CUSTOMER', data)
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },
  setCustomer({ commit }, data) {
    commit('SET_CUSTOMER', data)
  },
  logout({ commit }, data) {
    commit('SET_TOKEN', null)
    commit('SET_CUSTOMER', undefined)
  }
}

export default {
  namespaced: true,
  state,
  actions,
  mutations
}
