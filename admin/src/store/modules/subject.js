import {
  getSubjectByMallID,
  updateSubject,
  deleteSubject,
  addGoodsToSubject,
  goodsMove,
  moveOut,
  getMallGoodsWithSubject,
  createSubject,
  filterSubject,
  filterGoodsInSubject
} from '@/api/subject'

import Vue from 'vue'

const state = {
  subjectList: [],
  subjectGoodsList: [],
  curSubjectID: undefined
}

const mutations = {
  CREATE_SUBJECT: (state, data) => {
    state.subjectList.unshift(data)
    Vue.set(state, 'subjectList', state.subjectList)
  },
  SET_SUBJECT_LIST: (state, data) => {
    state.subjectList = data
    Vue.set(state, 'subjectList', state.subjectList)
  },
  SET_CUR_SUBJECTID: (state, data) => {
    state.curSubjectID = data
    Vue.set(state, 'curSubjectID', state.curSubjectID)
  },
  SET_SUBJECT_GOODS_LIST: (state, data) => {
    state.subjectGoodsList = data
    Vue.set(state, 'subjectGoodsList', state.subjectGoodsList)
  },
  DELETE_SUBJECT: (state, index) => {
    state.subjectList.splice(index, 1)
    Vue.set(state, 'subjectList', state.subjectList)
  },
  GOODS_MOVE: (state, data) => {
    const index = state.subjectGoodsList.findIndex((v, i, arr) => {
      if (v.id === data.goodsID) {
        return true
      }
    })
    const now = Object.assign({}, state.subjectGoodsList[index])
    if (data.up === true) {
      const ano = Object.assign({}, state.subjectGoodsList[index - 1])
      state.subjectGoodsList.splice(index - 1, 2, now, ano)
    } else {
      const ano = Object.assign({}, state.subjectGoodsList[index + 1])
      state.subjectGoodsList.splice(index, 2, ano, now)
    }
    Vue.set(state, 'subjectGoodsList', state.subjectGoodsList)
  },
  MOVE_OUT: (state, data) => {
    const index = state.subjectGoodsList.findIndex((v, i, arr) => {
      if (v.id === data.goodsID) {
        return true
      }
    })
    state.subjectGoodsList.splice(index, 1)
    Vue.set(state, 'subjectGoodsList', state.subjectGoodsList)
  }
}

const actions = {
  createSubject({ commit, rootGetters }, data) {
    return new Promise((resolve) => {
      createSubject({ mallID: rootGetters.curMallID, data: data }).then((resp) => {
        const { data } = resp
        commit('CREATE_SUBJECT', data)
        resolve()
      })
    })
  },
  setCurSubjectID({ commit, rootGetters }, data) {
    return new Promise((resolve) => {
      console.log(data)
      commit('SET_CUR_SUBJECTID', data)
      resolve()
    })
  },
  filterSubject({ commit, rootGetters }, data) {
    return new Promise((resolve) => {
      filterSubject({ subjectID: rootGetters.curSubjectID, mallID: rootGetters.curMallID, ...data }).then((resp) => {
        const { data } = resp
        commit('SET_SUBJECT_LIST', data)
        resolve()
      })
    })
  },
  getSubjectByMallID({ commit, rootGetters }, data) {
    return new Promise((resolve) => {
      getSubjectByMallID({ subjectID: rootGetters.curSubjectID, mallID: rootGetters.curMallID }).then((resp) => {
        const { data } = resp
        commit('SET_SUBJECT_LIST', data)
        resolve()
      })
    })
  },
  updateSubject({ commit, rootGetters }, data) {
    return new Promise((resolve) => {
      console.log(rootGetters)
      updateSubject({ subjectID: rootGetters.curSubjectID, mallID: rootGetters.curMallID, data }).then((resp) => {
        resolve()
      })
    })
  },
  deleteSubject({ commit, rootGetters }, data) {
    return new Promise((resolve) => {
      deleteSubject({ subjectID: rootGetters.curSubjectID, mallID: rootGetters.curMallID, ...data }).then((resp) => {
        commit('DELETE_SUBJECT', data.index)
        resolve()
      })
    })
  },
  addGoodsToSubject({ commit, rootGetters }, data) {
    return new Promise((resolve) => {
      addGoodsToSubject({ subjectID: rootGetters.curSubjectID, mallID: rootGetters.curMallID, ...data }).then((resp) => {
        commit('MOVE_OUT', data)
        resolve()
      })
    })
  },
  goodsMove({ commit, rootGetters }, data) {
    return new Promise((resolve) => {
      goodsMove({ subjectID: rootGetters.curSubjectID, mallID: rootGetters.curMallID, ...data }).then((resp) => {
        commit('GOODS_MOVE', data)
        resolve()
      })
    })
  },
  moveOut({ commit, rootGetters }, data) {
    return new Promise((resolve) => {
      moveOut({ subjectID: rootGetters.curSubjectID, mallID: rootGetters.curMallID, ...data }).then((resp) => {
        commit('MOVE_OUT', data)
        resolve()
      })
    })
  },
  getMallGoodsWithSubject({ commit, rootGetters }, data) {
    return new Promise((resolve) => {
      getMallGoodsWithSubject({ subjectID: rootGetters.curSubjectID, userID: rootGetters.userID, mallID: rootGetters.curMallID, ...data }).then((resp) => {
        const { data } = resp
        commit('SET_SUBJECT_GOODS_LIST', data)
        resolve()
      })
    })
  },
  filterGoodsInSubject({ commit, rootGetters }, data) {
    return new Promise((resolve) => {
      filterGoodsInSubject({ subjectID: rootGetters.curSubjectID, userID: rootGetters.userID, mallID: rootGetters.curMallID, ...data }).then((resp) => {
        const { data } = resp
        commit('SET_SUBJECT_GOODS_LIST', data)
        resolve()
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

