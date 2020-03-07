<template>
  <div class="app-container">
    <el-container>
      <el-aside width="550px" style="padding:40px 60px 0 30px">
        <el-card class="box-card" body-style="height:800px;overflow-y:auto">
          <van-swipe class="index-swipe" :autoplay="temp.interval*1000" height="160px">
            <van-swipe-item v-for="(item, index) in temp.advertise" :key="index">
              <img v-lazy="item.image.url" style="width: 100%;border-radius:8px">
            </van-swipe-item>
          </van-swipe>
          <van-tabs class="entrance-tabs" line-height="0px">
            <van-tab v-for="(item,index) in temp.entrance" :key="index">
              <div slot="title">
                <van-goods-action-icon :text=" item.name">
                  <div slot="icon">
                    <div class="entrance" :style="{backgroundImage:'url(' + item.image.url + '), linear-gradient('+temp.color+', '+temp.color+')'}" />
                  </div>
                </van-goods-action-icon>
              </div>
            </van-tab>
          </van-tabs>
          <div v-for="(item,index) in temp.subjectOrder" :key="index" style="margin:0 0 10px 0">
            <img v-lazy="temp.subjectList[item].image.url" class="subject">
            <van-tabs v-if="temp.subjectList[item].showGoodsList.length!== 0" class="subject-tabs" line-height="0px" title-active-color="#646566" title-inactive-color="#646566">
              <van-tab v-for="(pItem,pIndex) in temp.subjectList[item].showGoodsList" :key="pIndex">
                <div slot="title" align="left">
                  <div class="item-tab">
                    <van-row style="width:100%">
                      <van-image
                        fit="cover"
                        lazy-load
                        :src="pItem.imageList[0].url"
                        width="80.8px"
                        height="80.8px"
                      />
                    </van-row>
                    <div style="line-height:25px">{{ pItem.name }}</div>
                    <div style="line-height:25px"><span class="p-price">{{ pItem.pointPrice }}</span><span>{{ pItem.pointType }}</span></div>
                  </div>
                </div>
              </van-tab>
            </van-tabs>
          </div>
          <div v-if="temp.showList">
            <section class="ft18"><em /><span style="width:84px">&nbsp;为你推荐&nbsp;</span><em /></section>
            <van-list
              v-model="loading"
              :finished="finished"
              finished-text="没有更多了"
              @load="onLoad"
            >
              <van-row
                style="margin-left: -7.5px;
          margin-right: -7.5px;
          padding: 0 25.6px;
          padding-bottom: 25.6px"
              >
                <van-col
                  v-for="(item,index) in list"
                  :key="index"
                  span="12"
                  style="padding-left: 7.5px; padding-right: 7.5px; margin-bottom: 20px;"
                >
                  <div class="p-item">
                    <van-row style="width:100%;height:0;padding-top: 100%;position: relative;">
                      <van-image
                        fit="cover"
                        lazy-load
                        :src="item.imageList[0].url"
                        width="100%"
                        height="100%"
                        style="left: 0;top: 0;position: absolute;"
                      />
                    </van-row>
                    <div style="text-align:center">
                      <span>{{ item.name }}</span>
                    </div>
                    <div style="text-align:center">
                      <span class="p-price">{{ item.pointPrice }}</span><span>{{ item.pointType }}</span>
                    </div>
                  </div>
                </van-col>
              </van-row>
            </van-list>
          </div>
        </el-card>
        <div style="text-align:right;margin:30px 20px 0 0">
          <el-button type="primary" @click="saveSetting()">应用设置</el-button>
        </div>
      </el-aside>
      <el-main>
        <el-collapse v-model="activeNames">
          <el-collapse-item title="主题风格" name="1">
            <div><span>请设置和您页面风格协调的主题色</span><el-color-picker v-model="temp.color" style="margin:0px 0 0 10px" size="medium" /></div>
          </el-collapse-item>
          <el-collapse-item title="广告栏" name="2">
            <div>
              滚动间隔时间
              <el-input-number v-model="temp.interval" size="mini" :min="1" :max="10" />
              秒
            </div>
            <el-row v-for="(item,index) in temp.advertise" :key="index" style="margin:20px 0 0 0">
              <el-col :span="11">
                <img width="160px" height="95px" :src="item.image.url">
              </el-col>
              <el-col :span="8">
                <div>
                  <el-button size="mini" type="primary" @click="editAdvertise(item,index)">编辑</el-button>
                  <el-button size="mini" type="danger" @click="deleteAdvertise(index)">删除</el-button>
                </div>
              </el-col>
              <el-col :span="5">
                <div style="text-align:right">
                  <el-row>
                    <el-button type="primary" plain :disabled="index === 0" round size="mini" icon="el-icon-arrow-up" @click="moveAdvertise(index,true)" />
                  </el-row>
                  <el-row style="margin-top:10px">
                    <el-button type="primary" plain :disabled="index === temp.advertise.length-1" round size="mini" icon="el-icon-arrow-down" @click="moveAdvertise(index,false)" />
                  </el-row>
                </div>
              </el-col>
            </el-row>
            <el-button style="margin-top:15px" size="medium" type="primary" @click="addAdvertise()">新增广告</el-button>
          </el-collapse-item>
          <el-collapse-item title="快捷入口" name="3">
            <el-row v-for="(item,index) in temp.entrance" :key="index" style="margin:20px 0 0 0">
              <el-col :span="6">
                <img width="40px" height="40px" :src="item.image.url">
              </el-col>
              <el-col :span="12">
                <div>入口名称: {{ item.name }}</div>
                <el-button size="mini" type="danger" @click="deleteEntrance(index)">删除</el-button>
              </el-col>
              <el-col :span="5">
                <div style="text-align:right">
                  <el-row>
                    <el-button type="primary" plain :disabled="index === 0" round size="mini" icon="el-icon-arrow-up" @click="moveEntrance(index,true)" />
                  </el-row>
                  <el-row style="margin-top:10px">
                    <el-button type="primary" plain :disabled="index === temp.entrance.length-1" round size="mini" icon="el-icon-arrow-down" @click="moveEntrance(index,false)" />
                  </el-row>
                </div>
              </el-col>
            </el-row>
            <el-button style="margin:20px 0 0 0" type="primary" size="mini" @click="addEntrance()">添加快速入口</el-button>
          </el-collapse-item>
          <el-collapse-item title="商品专题栏" name="4">
            <el-row v-for="(item,index) in temp.subjectOrder" :key="index" style="margin:20px 0 0 0">
              <el-col :span="11">
                <img width="160px" height="95px" :src="temp.subjectList[item].image.url">
              </el-col>
              <el-col :span="9">
                <div>专题名称: {{ temp.subjectList[item].name }}</div>
                <div>专题描述: {{ temp.subjectList[item].describe }}</div>
                <!-- <el-button size="mini" type="danger" @click="deleteSubject(index)">删除</el-button>
                <div>(并不会真正删除专题)</div> -->
              </el-col>
              <el-col :span="4">
                <div style="text-align:right">
                  <el-row>
                    <el-button type="primary" plain :disabled="index === 0" round size="mini" icon="el-icon-arrow-up" @click="moveSubject(index,true)" />
                  </el-row>
                  <el-row style="margin-top:10px">
                    <el-button type="primary" plain :disabled="index === temp.subjectOrder.length-1" round size="mini" icon="el-icon-arrow-down" @click="moveSubject(index,false)" />
                  </el-row>
                </div>
              </el-col>
            </el-row>
          </el-collapse-item>
        </el-collapse>
        <div style="font-weight:500;margin:20px 0 0 0">
          为你推荐
        </div>
        <el-switch
          v-model="temp.showList"
          active-text="展示"
          inactive-text="不展示"
        />
      </el-main>
      <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogVisible">
        <el-form ref="tempAdvertise" :rules="rules" :model="tempAdvertise" label-position="right" label-width="130px" style="width: 400px; margin-left:50px;">
          <el-form-item label="广告名:" prop="name">
            <el-input v-model="tempAdvertise.name" />
          </el-form-item>
          <el-form-item label="广告链接:">
            <el-input v-model="tempAdvertise.link" />
          </el-form-item>
          <el-form-item label="广告图片:" prop="image">
            <el-upload
              class="advertise-uploader"
              action="/api/admin/uploadImage"
              :show-file-list="false"
              :before-upload="beforeUpload"
              :headers="{token:$store.getters.token}"
              accept="image/png, image/jpeg"
              :data="{dir:'advertiseImage'}"
              :on-success="handleSuccess"
            >
              <img v-if="hasImage" :src="tempAdvertise.image.url" class="advertise">
              <i v-else class="el-icon-plus advertise-uploader-icon" />
            </el-upload>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">
            取消
          </el-button>
          <el-button type="primary" @click="confirm()">
            确认
          </el-button>
        </div>
      </el-dialog>

      <el-dialog title="添加快捷入口" :visible.sync="entranceVisible">
        <el-form ref="entrance" :rules="entranceRules" :model="entrance" label-position="right" label-width="130px" style="width: 400px; margin-left:50px;">
          <el-form-item label="快捷入口名称:" prop="name">
            <el-input v-model="entrance.name" />
          </el-form-item>
          <el-form-item label="关联商品类别:" prop="goodsType">
            <el-select v-model="entrance.goodsType" style="width: 160px" class="filter-item">
              <el-option v-for="item in goodsTypeList" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="快捷入口图标:" prop="icon">
            <select-icon v-model="entrance.icon" :options="iconData" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="entranceVisible = false">
            取消
          </el-button>
          <el-button type="primary" @click="entranceConfirm()">
            确认
          </el-button>
        </div>
      </el-dialog>
    </el-container>
  </div>
</template>

<script>
import { Swipe, SwipeItem, Tab, Tabs, GoodsActionIcon, Row, Image, List, Col } from 'vant'
import SelectIcon from './selectIcon'
import * as v from '../goodsManage/variable'
export default {
  name: 'Fitment',
  components: {
    [Swipe.name]: Swipe,
    [SwipeItem.name]: SwipeItem,
    [GoodsActionIcon.name]: GoodsActionIcon,
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
    [Row.name]: Row,
    [Col.name]: Col,
    [Image.name]: Image,
    [List.name]: List,
    SelectIcon
  },
  data() {
    var validateImage = (rule, value, callback) => {
      console.log(value, value.url)
      if (value.url === undefined) {
        callback(new Error('广告图片为必选项'))
      } else {
        callback()
      }
    }
    return {
      activeNames: ['1', '2', '3', '4'],
      temp: {},
      list: [],
      loading: false,
      finished: false,
      textMap: {
        update: '编辑',
        create: '新增'
      },
      dialogStatus: '',
      dialogVisible: false,
      tempAdvertise: {
        name: '',
        link: '',
        image: {}
      },
      tempIndex: -1,
      hasImage: false,
      entranceRules: {
        name: [{ required: true, message: '快捷入口名称为必填项', trigger: 'blur' }],
        goodsType: [{ required: true, message: '关联商品类别为必选项', trigger: 'blur' }],
        icon: [{ required: true, message: '快捷入口图标为必选项', trigger: 'blur' }]
      },
      rules: {
        name: [{ required: true, message: '广告名为必填项', trigger: 'blur' }],
        image: [{ required: true, validator: validateImage, trigger: 'blur' }]
      },
      iconData: [{ key: 'el-icon-ali-huwai', value: { name: 'entranceImage/户外车品.png', url: 'https://xmall123.oss-cn-shenzhen.aliyuncs.com/entranceImage/%E6%88%B7%E5%A4%96%E8%BD%A6%E5%93%81.png' }},
        { key: 'el-icon-ali-yu', value: { name: 'entranceImage/粮油生鲜.png', url: 'https://xmall123.oss-cn-shenzhen.aliyuncs.com/entranceImage/%E7%B2%AE%E6%B2%B9%E7%94%9F%E9%B2%9C.png' }},
        { key: 'el-icon-ali-naiping', value: { name: 'entranceImage/母婴玩具.png', url: 'https://xmall123.oss-cn-shenzhen.aliyuncs.com/entranceImage/%E6%AF%8D%E5%A9%B4%E7%8E%A9%E5%85%B7.png' }},
        { key: 'el-icon-ali-hufupin', value: { name: 'entranceImage/个护清洁.png', url: 'https://xmall123.oss-cn-shenzhen.aliyuncs.com/entranceImage/%E4%B8%AA%E6%8A%A4%E6%B8%85%E6%B4%81.png' }},
        { key: 'el-icon-ali-chuju', value: { name: 'entranceImage/居家厨具.png', url: 'https://xmall123.oss-cn-shenzhen.aliyuncs.com/entranceImage/%E5%B1%85%E5%AE%B6%E5%8E%A8%E5%85%B7.png' }},
        { key: 'el-icon-ali-qinlei', value: { name: 'entranceImage/美食酒水.png', url: 'https://xmall123.oss-cn-shenzhen.aliyuncs.com/entranceImage/%E7%BE%8E%E9%A3%9F%E9%85%92%E6%B0%B4.png' }},
        { key: 'el-icon-ali-yiliao', value: { name: 'entranceImage/医疗计生.png', url: 'https://xmall123.oss-cn-shenzhen.aliyuncs.com/entranceImage/医疗计生.png' }},
        { key: 'el-icon-ali-shuma', value: { name: 'entranceImage/家电数码.png', url: 'https://xmall123.oss-cn-shenzhen.aliyuncs.com/entranceImage/%E5%AE%B6%E7%94%B5%E6%95%B0%E7%A0%81.png' }}],
      entranceVisible: false,
      entrance: {
        name: '',
        goodsType: '',
        icon: '',
        image: {}
      },
      goodsTypeList: v.goodsTypeList
    }
  },
  computed: {
    curMall() {
      return this.$store.getters.curMall
    }
  },
  created() {
    this.$store.dispatch('mall/setCurMall', this.curMall.id).then(() => {
      this.temp = Object.assign({}, this.curMall)
    })
  },
  methods: {
    entranceConfirm() {
      this.$refs['entrance'].validate((valid) => {
        if (valid) {
          const index = this.iconData.findIndex((v, i, arr) => {
            if (this.entrance.icon === v.key) {
              return true
            }
          })
          this.entrance.image = this.$deepCopy(this.iconData[index].value)
          delete this.entrance.icon
          this.temp.entrance.push(this.$deepCopy(this.entrance))
          this.entranceVisible = false
        }
      })
    },
    addEntrance() {
      this.entrance = {
        name: '',
        goodsType: '',
        icon: '',
        image: {}
      }
      this.$nextTick(() => {
        this.$refs['entrance'].clearValidate()
      })
      this.entranceVisible = true
    },
    handleSuccess(res, file) {
      const { data } = res
      this.tempAdvertise.image = data
      this.hasImage = true
    },
    beforeUpload() {

    },
    onLoad() {
      this.$store.dispatch('goods/getMallGoods').then(() => {
        this.list = this.$store.getters.goodsList
        this.loading = false
        this.finished = true
      })
    },
    addAdvertise() {
      this.tempAdvertise = {
        name: '',
        link: '',
        image: {}
      }
      this.hasImage = false
      this.dialogVisible = true
      this.tempIndex = -1
      this.$nextTick(() => {
        this.$refs['tempAdvertise'].clearValidate()
      })
    },
    confirm() {
      this.$refs['tempAdvertise'].validate((valid) => {
        if (valid) {
          if (this.tempIndex === -1) {
            this.temp.advertise.push(Object.assign({}, this.tempAdvertise))
          } else {
            this.temp.advertise.splice(this.tempIndex, 1, Object.assign({}, this.tempAdvertise))
          }
          this.dialogVisible = false
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    saveSetting() {
      this.$store.dispatch('mall/updateMallFitment', this.temp).then(() => {
        this.$notify({
          title: 'Success',
          message: '商城设置保存成功',
          type: 'success',
          duration: 2000
        })
      })
    },
    editAdvertise(item, index) {
      this.tempAdvertise = this.$deepCopy(item)
      this.tempIndex = index
      this.dialogVisible = true
      this.hasImage = true
      this.$nextTick(() => {
        this.$refs['tempAdvertise'].clearValidate()
      })
    },
    deleteAdvertise(index) {
      this.temp.advertise.splice(index, 1)
    },
    moveAdvertise(index, up) {
      const now = this.temp.advertise[index]
      if (up) {
        const ano = this.temp.advertise[index - 1]
        this.temp.advertise.splice(index - 1, 2, now, ano)
      } else {
        const ano = this.temp.advertise[index + 1]
        this.temp.advertise.splice(index, 2, ano, now)
      }
    },
    moveEntrance(index, up) {
      const now = this.temp.entrance[index]
      if (up) {
        const ano = this.temp.entrance[index - 1]
        this.temp.entrance.splice(index - 1, 2, now, ano)
      } else {
        const ano = this.temp.entrance[index + 1]
        this.temp.entrance.splice(index, 2, ano, now)
      }
    },
    deleteEntrance(index) {
      this.temp.entrance.splice(index, 1)
    },
    moveSubject(index, up) {
      const now = this.temp.subjectOrder[index]
      if (up) {
        const ano = this.temp.subjectOrder[index - 1]
        this.temp.subjectOrder.splice(index - 1, 2, now, ano)
      } else {
        const ano = this.temp.subjectOrder[index + 1]
        this.temp.subjectOrder.splice(index, 2, ano, now)
      }
    },
    deleteSubject(index) {
      this.temp.subjectOrder.splice(index, 1)
    }
  }
}
</script>

<style>
.entrance {
  width: 20px;
  height: 20px;
  background-blend-mode: lighten;
  background-size: cover;
}
.entrance-tabs{
    height: 60px;
    margin-bottom: 20px
    /* .van-tabs__nav--line {
      height: @height - 40px;
    } */

}
.van-hairline--top-bottom::after{
    border-width:0 0
}
.subject{
    width: 100%!important;
    border-radius:8px;

}
.van-tabs--line .van-tabs__wrap{
  height: 100%!important;
}
.van-tabs__nav--line {
    box-sizing: content-box;
    padding-top: 20px;
    padding-bottom: 20px;
}
.item-tab{
  width: 120px;
  text-align: center;
}
.p-price {
    color: #ff763d;
    margin-right: 10px;
  }
  .van-tab--active {
      font-weight: 400
  }
.van-tab {
    max-width: 91px
    }
    .ft18{
    display: -webkit-box;
    display: -webkit-flex;
    display: flex;
    height: 85px;
    line-height: 3.125rem;
    color: #444;
    text-align: center;
    padding: 0 .9375rem;
    -webkit-box-align: center;
    -webkit-align-items: center;
    align-items: center;
    -webkit-box-pack: center;
    -webkit-justify-content: center;
    justify-content: center;
    font-size: 18px;
 }
 em{
      width: 10px;
      height: 2px;
      background: #999;
      transform: rotate(-60deg);
    }
    .advertise-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .advertise-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .advertise-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 260px;
    height: 150px;
    line-height: 178px;
    text-align: center;
  }
  .advertise {
    width: 260px;
    height: 150px;
    display: block;
  }
</style>
