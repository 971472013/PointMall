import Vue from 'vue'
import {
  // getPointInfo,
  updatePoint, deletePoint, distributePointOne, loadDistributeRecord, queryPoint,
  newCustomerDistributePointOne, queryAllPoint } from '@/api/point'

const state = {
  point: undefined,
  recordTable: [],
  queryTable: []
}

const mutations = {
  SET_POINT: (state, data) => {
    Vue.set(state, 'point', data)
  },
  DELETE_POINT: (state, index) => {
    state.point = undefined
    Vue.set(state, 'point', state.point)
  },
  SET_RECORD_TABLE: (state, data) => {
    state.recordTable = data
    Vue.set(state, 'recordTable', state.recordTable)
  },
  SET_QUERY_TABLE: (state, data) => {
    state.queryTable = data
    Vue.set(state, 'queryTable', data)
  }
}

const actions = {
  setPointInfo({ commit, rootGetters }, data) {
    return new Promise((resolve) => {
      commit('SET_POINT', data)
      resolve()
    })
  },
  //   setPointInfo({ commit, rootGetters }) {
  //     return new Promise((resolve) => {
  //       const mallID = rootGetters.curMallID
  //       getPointInfo({ mallID: mallID }).then((resp) => {
  //         const { data } = resp
  //         if (data !== null) {
  //           commit('SET_POINT', data)
  //         }
  //         resolve()
  //       })
  //     })
  //   },
  updatePoint({ commit, rootGetters }, data) {
    return new Promise((res) => {
      updatePoint({ data, mallID: rootGetters.curMallID }).then(() => {
        commit('SET_POINT', data)
        res()
      })
    })
  },
  loadDistributeRecord({ commit, rootGetters }) {
    return new Promise((res) => {
      loadDistributeRecord({ mallID: rootGetters.curMallID }).then(resp => {
        const { data } = resp
        commit('SET_RECORD_TABLE', data)
        res()
      })
    })
  },
  setDistributeRecord({ commit, rootGetters }, data) {
    return new Promise((res) => {
      commit('SET_RECORD_TABLE', data)
      res()
    })
  },
  distributePointOne({ commit, rootGetters }, data) {
    return new Promise((res, rej) => {
      distributePointOne({ ...data, mallID: rootGetters.curMallID }).then(resp => {
        const { data } = resp
        if (data === null) {
          rej()
        }
        commit('SET_RECORD_TABLE', data)
        res()
      })
    })
  },
  newCustomerDistributePointOne({ commit, rootGetters }, data) {
    return new Promise((res, rej) => {
      newCustomerDistributePointOne({ ...data, mallID: rootGetters.curMallID }).then(resp => {
        const { data } = resp
        commit('SET_RECORD_TABLE', data)
        res()
      })
    })
  },
  queryPoint({ commit, rootGetters }, data) {
    return new Promise((res) => {
      queryPoint({ ...data, mallID: rootGetters.curMallID }).then(resp => {
        const { data } = resp
        if (data === null) {
          commit('SET_QUERY_TABLE', [])
        } else {
          commit('SET_QUERY_TABLE', [data])
        }
        res()
      })
    })
  },
  queryAllPoint({ commit, rootGetters }) {
    return new Promise((res) => {
      queryAllPoint({ mallID: rootGetters.curMallID }).then(resp => {
        const { data } = resp
        if (data === null) {
          commit('SET_QUERY_TABLE', [])
        } else {
          commit('SET_QUERY_TABLE', data)
        }
        res()
      })
    })
  },
  deletePoint({ commit, rootGetters }) {
    return new Promise((res) => {
      deletePoint({ mallID: rootGetters.curMallID }).then(() => {
        commit('DELETE_POINT')
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

