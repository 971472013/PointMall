/** When your routing table is too long, you can split it into small modules **/

import Layout from '@/layout'

const goodsManageRouter = {
  path: '/goodsManage',
  component: Layout,
  redirect: '/goodsManage/myGoods',
  name: 'goodsManage',
  meta: {
    title: '商品管理',
    icon: 'table'
  },
  children: [
    {
      path: 'myGoods',
      component: () => import('@/views/goodsManage/myGoods'),
      name: 'myGoods',
      meta: { title: '我的商品', block: true }
    },
    {
      path: 'mySubjects',
      component: () => import('@/views/goodsManage/mySubjects'),
      name: 'mySubjects',
      meta: { title: '我的专题', block: true }
    },
    {
      path: 'addGoods',
      hidden: true,
      component: () => import('@/views/goodsManage/addGoods'),
      name: 'addGoods',
      meta: { title: '添加商品', noCache: true, noTag: true, block: true }
    },
    {
      path: 'subjectGoods',
      hidden: true,
      component: () => import('@/views/goodsManage/subjectGoods'),
      name: 'subjectGoods',
      meta: { title: '专题商品管理', noCache: true, noTag: true, block: true }
    },
    {
      path: 'subjectGoodsAdd',
      hidden: true,
      component: () => import('@/views/goodsManage/subjectGoodsAdd'),
      name: 'subjectGoodsAdd',
      meta: { title: '专题商品添加', noCache: true, noTag: true, block: true }
    }
  ]
}
export default goodsManageRouter
