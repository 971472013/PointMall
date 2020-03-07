import Vue from 'vue'
import Router from 'vue-router'

// const originalPush = Router.prototype.push
// Router.prototype.push = function push(location, onResolve, onReject) {
//   if (onResolve || onReject) return originalPush.call(this, location, onResolve, onReject)
//   return originalPush.call(this, location).catch(err => err)
// }

Vue.use(Router)

const routes = [
  {
    path: '*',
    redirect: '/index'
  },
  {
    name: 'index',
    component: () => import('./view/index'),
    meta: {
      title: '商城',
      needLogin: false
    }
  },
  {
    name: 'login',
    component: () => import('./view/login'),
    meta: {
      title: '商城',
      needLogin: false
    }
  },
  {
    name: 'user',
    component: () => import('./view/user'),
    meta: {
      title: '个人中心',
      needLogin: true
    }
  },
  {
    name: 'cart',
    component: () => import('./view/cart'),
    meta: {
      title: '购物车',
      needLogin: true
    }
  },
  {
    name: 'goods',
    component: () => import('./view/goods'),
    meta: {
      title: '商品详情',
      needLogin: false
    }
  },
  {
    name: 'order',
    component: () => import('./view/order'),
    meta: {
      title: '我的订单',
      needLogin: true
    }
  },
  {
    name: 'orderConfirm',
    component: () => import('./view/orderConfirm'),
    meta: {
      title: '订单确认',
      needLogin: true
    }
  },
  {
    name: 'addressList',
    component: () => import('./view/addressList'),
    meta: {
      title: '地址列表',
      needLogin: true
    }
  },
  {
    name: 'addressEdit',
    component: () => import('./view/addressEdit'),
    meta: {
      title: '地址编辑',
      needLogin: true
    }
  },
  {
    name: 'search',
    component: () => import('./view/search'),
    meta: {
      title: '搜索',
      needLogin: false
    }
  }
]

// add route path
routes.forEach(route => {
  route.path = route.path || '/' + (route.name || '')
})

const router = new Router({ routes })

router.beforeEach((to, from, next) => {
  if (to.meta.needLogin && sessionStorage.getItem('token') === null) {
    next({ path: '/login' })
  } else {
    const title = to.meta && to.meta.title
    if (title) {
      document.title = title
    }
    document.from = from.name
    next()
  }
})

export {
  router
}

export default{
  router
}
