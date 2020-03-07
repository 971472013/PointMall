/** When your routing table is too long, you can split it into small modules **/

import Layout from '@/layout'

const mallManageRouter = {
  path: '/mallManage',
  component: Layout,
  redirect: '/mallManage/fitment',
  name: 'mallManage',
  alwaysShow: true,
  meta: {
    title: '商城管理',
    icon: 'table'
  },
  children: [
    {
      path: 'fitment',
      component: () => import('@/views/mallManage/fitment'),
      name: 'fitment',
      meta: { title: '商城装修', block: true }
    },
    {
      path: 'mallSetting',
      component: () => import('@/views/mallManage/mallSetting'),
      name: 'mallSetting',
      meta: { title: '前端商城配置', block: true }
    }
  ]
}
export default mallManageRouter
