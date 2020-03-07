const port = process.env.port || process.env.npm_config_port || 9529
// const baseUrl = process.env.VUE_APP_BASE_API || '/api'
// const autoprefixer = require('autoprefixer')
// const pxtorem = require('postcss-pxtorem')

module.exports = {
  publicPath: './',
  assetsDir: 'static',

  outputDir: 'dist',
  // publicPath: process.env.NODE_ENV === 'production' ? '/vant-demo/' : '/'

  devServer: {
    port: port,
    open: true,
    overlay: {
      warnings: false,
      errors: true
    }
    // proxy: {
    //   // change xxx-api/login => mock/login
    //   // detail: https://cli.vuejs.org/config/#devserver-proxy
    //   '/api': {

    //     // target: `http://120.77.183.132:8080`,
    //     target: `http://localhost:8080`,
    //     changeOrigin: true,
    //     pathRewrite: {
    //       '^/api': '/api'
    //     }
    //   }
    // }
  }
  // css: {
  //   loaderOptions: {
  //     postcss: {
  //       plugins: [
  //         autoprefixer(),
  //         pxtorem({
  //           rootValue: 75,
  //           propList: ['*']
  //         })
  //       ]
  //     }
  //   }
  // }
}
