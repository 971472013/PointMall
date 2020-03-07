import { updateAddress } from '@/api'
// import Vue from 'vue'

const state = {
  addressList: [],
  tempAddress: null,
  useAddress: null
}

const mutations = {
  ADD_ADDRESS: (state, data) => {
    state.addressList.push(data)
  },
  SET_ADDRESS_LIST: (state, data) => {
    state.addressList = data
  },
  SET_TEMP_ADDRSS: (state, data) => {
    state.tempAddress = data
  },
  EDIT_ADDRESS: (state, data) => {
    const index = state.addressList.findIndex((v, i, arr) => {
      return v.eid === data.eid
    })
    state.addressList.splice(index, 1, data)
  },
  DELETE_ADDRESS: (state, index) => {
    state.addressList.splice(index, 1)
  },
  SET_USE_ADDRESS: (state, data) => {
    state.useAddress = data
  }
}

const actions = {
  setAddressList({ commit }, data) {
    commit('SET_ADDRESS_LIST', data)
  },
  addAddress({ commit, rootGetters }, data) {
    return new Promise((resolve, reject) => {
      commit('ADD_ADDRESS', data)
      resolve()
    }).then(() => {
      const customerID = rootGetters.customer.id
      const addressList = rootGetters.addressList
      const data = {
        customerID: customerID,
        addressList: addressList
      }
      updateAddress(data)
    }).catch(() => {})
  },
  setTempAddress({ commit }, data) {
    commit('SET_TEMP_ADDRSS', data)
  },
  editAddress({ commit, rootGetters }, data) {
    return new Promise((resolve, reject) => {
      commit('EDIT_ADDRESS', data)
      resolve()
    }).then(() => {
      const customerID = rootGetters.customer.id
      const addressList = rootGetters.addressList
      const data = {
        customerID: customerID,
        addressList: addressList
      }
      updateAddress(data)
    }).catch(() => { })
  },
  deleteAddress({ commit, rootGetters }, index) {
    return new Promise((resolve, reject) => {
      commit('DELETE_ADDRESS', index)
      resolve()
    }).then(() => {
      const phone = rootGetters.phone
      const addressList = rootGetters.addressList
      const data = {
        phone: phone,
        addressList: addressList
      }
      updateAddress(data)
    }).catch(() => { })
  },
  setUseAddress({ commit }, data) {
    commit('SET_USE_ADDRESS', data)
  }
}

export default {
  namespaced: true,
  state,
  actions,
  mutations
}
