/** When your routing table is too long, you can split it into small modules **/

import Layout from '@/layout'

const orderManageRouter = {
  path: '/oderManage',
  component: Layout,
  redirect: '/orderManage/allOrder',
  name: 'orderManage',
  alwaysShow: true,
  meta: {
    title: '订单管理',
    icon: 'table'
  },
  children: [
    {
      path: 'allOrder',
      component: () => import('@/views/orderManage/allOrder'),
      name: 'allOrder',
      meta: { title: '全部订单', block: true }
    }
    // {
    //   path: 'unDeliverOrder',
    //   component: () => import('@/views/orderManage/unDeliverOrder'),
    //   name: 'unDeliverOrder',
    //   meta: { title: '待发货订单' }
    // }
  ]
}
export default orderManageRouter
