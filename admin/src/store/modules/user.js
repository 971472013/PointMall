import { login, logout } from '@/api/user'
import { resetRouter } from '@/router'

const state = {
  token: localStorage.getItem('token'),
  name: null,
  avatar: '',
  userID: ''
}

const mutations = {
  SET_TOKEN: (state, token) => {
    localStorage.setItem('token', token)
    state.token = token
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  RESET_STATE: (state) => {
    localStorage.removeItem('token')
    state.token = null
    state.name = null
    state.avatar = ''
    state.userID = ''
  },
  SET_ID: (state, id) => {
    state.userID = id
  }
}

const actions = {
  // user login
  login({ commit }, userInfo) {
    const { username, password } = userInfo
    return new Promise((resolve, reject) => {
      login({ username: username.trim(), password: password }).then(response => {
        const { data } = response
        commit('SET_TOKEN', data.token)
        console.log(11, state.token)
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // get user info
  setInfo({ commit }, data) {
    return new Promise((resolve) => {
      const { name, avatar } = data.userInfo
      commit('SET_NAME', name)
      commit('SET_AVATAR', avatar)
      commit('SET_ID', data.id)
      resolve()
    })
  },

  // user logout
  logout({ commit, state }) {
    return new Promise((resolve, reject) => {
      logout(state.token).then(() => {
        commit('RESET_STATE')
        resetRouter()
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  resetToken({ commit }) {
    return new Promise((resolve, reject) => {
      commit('RESET_STATE')
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

