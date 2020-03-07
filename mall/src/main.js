// import 'amfe-flexible'
import Vue from 'vue'
import App from './App'
import { router } from './router'
import store from '@/store'

import * as filters from '@/filter'
import * as global from '@/utils/methods'

Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})

Object.keys(global).forEach(key => {
  Vue.prototype['$' + key] = global[key]
})

new Vue({
  router,
  store,
  el: '#app',
  render: h => h(App)
})
