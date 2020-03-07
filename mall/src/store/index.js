import Vue from 'vue'
import Vuex from 'vuex'
import getters from './getters'
import { loadInfo } from '@/api'
import persistedState from 'vuex-persistedstate'

Vue.use(Vuex)

// https://webpack.js.org/guides/dependency-management/#requirecontext
const modulesFiles = require.context('./modules', true, /\.js$/)

// you do not need `import app from './modules/app'`
// it will auto require all vuex module from modules file
const modules = modulesFiles.keys().reduce((modules, modulePath) => {
  // set './app.js' => 'app'
  const moduleName = modulePath.replace(/^\.\/(.*)\.\w+$/, '$1')
  const value = modulesFiles(modulePath)
  modules[moduleName] = value.default
  return modules
}, {})

const store = new Vuex.Store({
  getters,
  modules,
  plugins: [persistedState({
    storage: window.sessionStorage,
    reducer(val) {
      return {
        // 只储存state中的user
        customer: val.customer,
        address: val.address,
        goods: val.goods,
        order: val.order,
        setting: val.setting
      }
    }
  })]
})

export default store

export function clean() {
  store.dispatch('customer/logout')
  store.dispatch('goods/clear')
}

export function initInfo(phone) {
  return new Promise((resolve, reject) => {
    loadInfo({ mallID: store.getters.mallID, phone: phone }).then(resp => {
      const { data } = resp
      store.dispatch('customer/setCustomer', data)
      store.dispatch('goods/setCartGoodsList', data.goodsList)
      store.dispatch('address/setAddressList', data.addressList)
      store.dispatch('order/setOrderList', data.orderList)
      resolve()
    }).catch(() => {})
  })
}
