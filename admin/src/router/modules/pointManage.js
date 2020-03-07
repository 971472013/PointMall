/** When your routing table is too long, you can split it into small modules **/

import Layout from '@/layout'

const pointManageRouter = {
  path: '/pointManage',
  component: Layout,
  redirect: '/pointManage/publishPoint',
  name: 'pointManage',
  meta: {
    title: '积分管理',
    icon: 'table'
  },
  children: [
    {
      path: 'publishPoint',
      component: () => import('@/views/pointManage/publishPoint'),
      name: 'publishPoint',
      meta: { title: '发行积分', block: false }
    },
    {
      path: 'distributePoint',
      component: () => import('@/views/pointManage/distributePoint'),
      name: 'distributePoint',
      meta: { title: '发放积分', block: true }
    }
    // {
    //   path: 'userPoint',
    //   component: () => import('@/views/pointManage/userPoint'),
    //   name: 'userPoint',
    //   meta: { title: '用户积分' }
    // }
  ]
}
export default pointManageRouter
