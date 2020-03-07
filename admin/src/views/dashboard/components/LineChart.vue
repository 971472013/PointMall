<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'
import { getNearMonthOrder } from '@/api/order'

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '350px'
    },
    autoResize: {
      type: Boolean,
      default: true
    }
    // chartData: {
    //   type: Object,
    //   required: true
    // }
  },

  data() {
    return {
      chart: null,
      xAxis_data: [],
      v_data: []
    }
  },
  // watch: {
  //   chartData: {
  //     deep: true,
  //     handler(val) {
  //       this.setOptions(val)
  //     }
  //   }
  // },
  // mounted() {
  //   this.$nextTick(() => {
  //     this.initChart()
  //   })
  // },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  mounted() {
    this.chart = echarts.init(this.$el, 'macarons')
    this.setDefaultOptions()
    getNearMonthOrder({ mallID: this.$store.getters.curMallID }).then((resp) => {
      const { data } = resp
      for (var i in data) {
        this.xAxis_data.push(data[i].key)
        this.v_data.push(this.countTotal(data[i].value))
      }
      this.chart.setOption({
        xAxis: {
          data: this.xAxis_data
        },
        series: [{
          // 根据名字对应到相应的系列
          name: '日成交额',
          data: this.v_data
        }]
      })
      this.$emit('setNum', this.countNum(data[data.length - 1].value))
      this.$emit('setTotal', this.countTotal(data[data.length - 1].value))
      // this.$nextTick(() => {
      //   this.$emit('setNum', this.countNum(data[data.length - 1].value))
      //   this.$emit('setTotal', this.countTotal(data[data.length - 1].value))
      // })
    })
  },
  methods: {
    countTotal(list) {
      return list.reduce((total, item) => total + item.totalMarketPrice, 0)
    },
    countNum(list) {
      return list.reduce((total, item) => total + Object.keys(item.orderGoodsList).length, 0)
    },
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')
      this.setOptions()
    },
    setDefaultOptions() {
      // const that = this
      this.chart.setOption({
        xAxis: {
          data: [],
          boundaryGap: false,
          axisTick: {
            show: false
          },
          axisLabel: {
            interval: 4,
            rotate: 45,
            align: 'right'
          }
        },
        grid: {
          left: 28,
          right: 5,
          bottom: 20,
          top: 30,
          containLabel: true
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          },
          padding: [5, 10]
        },
        yAxis: {
          axisTick: {
            show: false
          }
        },
        legend: {
          data: ['日成交额']
        },
        series: [{
          name: '日成交额',
          smooth: true,
          type: 'line',
          itemStyle: {
            normal: {
              color: '#3888fa',
              lineStyle: {
                color: '#3888fa',
                width: 2
              },
              areaStyle: {
                color: '#f3f8ff'
              }
            }
          },
          data: [],
          animationDuration: 2800,
          animationEasing: 'quadraticOut'
        }]
      })
    }
  }
}
</script>
