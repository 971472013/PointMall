import Vue from 'vue'
import Vuex from 'vuex'
import getters from './getters'
import { getInfo } from '@/api/user'
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
  modules,
  getters,
  plugins: [persistedState({ storage: window.sessionStorage,
    reducer(val) {
      return {
        // 只储存state中的user
        user: val.user,
        goods: val.goods,
        mall: val.mall,
        order: val.order,
        point: val.point,
        subject: val.subject
      }
    } })]
})

export default store

export function loadInfo() {
  return new Promise((resolve, reject) => {
    getInfo().then((res) => {
      const { data } = res
      store.dispatch('user/setInfo', data).then(() => {
        store.dispatch('mall/setMallList', data.mallList).then(() => {
          store.dispatch('mall/setCurMall', data.mallList[0].id).then((point) => {
            store.dispatch('point/setPointInfo', point)
            resolve()
          })
        })
      })
      // store.dispatch('user/setInfo', data).then(() => {
      //   store.dispatch('mall/setCurMall', data.mallList[0])
      // }).then(() => {
      //   store.dispatch('point/setPointInfo')
      //   resolve()
      // })
    })
  })
}
