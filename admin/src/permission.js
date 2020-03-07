import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import getPageTitle from '@/utils/get-page-title'
import { loadInfo } from '@/store'

NProgress.configure({ showSpinner: false }) // NProgress Configuration

const whiteList = ['/login'] // no redirect whitelist

router.beforeEach(async(to, from, next) => {
  // start progress bar
  NProgress.start()

  // set page title
  document.title = getPageTitle(to.meta.title)

  // determine whether the user has logged in
  const hasToken = localStorage.getItem('token')
  // console.log(111, hasToken)
  if (hasToken !== null) {
    // console.log(to)
    if (to.path === '/login') {
      // if is logged in, redirect to the home page
      next()
      NProgress.done()
    } else {
      const hasGetUserInfo = store.getters.name

      if (hasGetUserInfo) {
        // console.log(hasGetUserInfo)
        if (to.meta.block && (store.getters.point === null || store.getters.point === undefined)) {
          next(`/pointManage/publishPoint`)
          NProgress.done()
        } else {
          if (to.path === '/dashboard') {
            store.dispatch('mall/refresh', true).then(() => {
              next()
              NProgress.done()
            })
          } else {
            next()
            NProgress.done()
          }
        }
      } else {
        try {
          console.log(store.getters.name, store.state.user.name, store.getters)
          await loadInfo()
          next()
        } catch (error) {
          // remove token and go to login page to re-login
          await store.dispatch('user/resetToken')
          Message.error(error || 'Has Error')
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      }
    }
  } else {
    /* has no token*/

    if (whiteList.indexOf(to.path) !== -1) {
      // in the free login whitelist, go directly
      next()
    } else {
      // other pages that do not have permission to access are redirected to the login page.
      next({ path: '/login' })
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  // finish progress bar
  NProgress.done()
})
