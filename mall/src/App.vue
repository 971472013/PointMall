<template>
  <div id="app">
    <van-nav-bar
      :title="getTitle()"
      left-arrow
      fixed
      :z-index="100"
      @click-left="onClickLeft"
    />
    <div class="main" style="overflow:auto;overflow-x: hidden">
      <router-view />
    </div>
  </div>
</template>

<script>
import { NavBar, SubmitBar } from 'vant'

export default {
  components: {
    [NavBar.name]: NavBar,
    [SubmitBar.name]: SubmitBar
  },
  methods: {
    onClickLeft() {
      if (this.$route.meta.title === '搜索') {
        this.$store.dispatch('goods/clearSearch')
      }
      this.$router.go(-1)
    },
    getTitle() {
      if (this.$route.meta.title === '购物车') {
        const num = this.$store.getters.cartNum
        if (num !== 0) {
          return '购物车(' + num + ')'
        }
      }
      if (this.$route.meta.title === '商城') {
        return this.$store.getters.mall.title
      }
      if (this.$route.meta.title === '搜索') {
        return this.$store.getters.searchTitle
      }
      return this.$route.meta.title
    }
  }
}
</script>

<style>
body {
  font-size: 16px;
  background-color: #f5f5f5;
  -webkit-font-smoothing: antialiased;
}
body, html {
    height: 100%;
    width: 100%;
    max-width: 640px;
    margin: 0 auto;
    -webkit-font-smoothing: antialiased;
}
.main{
  margin-top: 46px;
  height: 100%;
  width: 100%;
}
#app {
    max-width: 640px;
    margin: 0 auto;
    height: calc(100% - 46px);
    width: 100%;
}
.van-nav-bar.van-nav-bar--fixed {
    max-width: 640px;
    left: 50%;
    -webkit-transform: translateX(-50%);
    transform: translateX(-50%);
}
</style>
