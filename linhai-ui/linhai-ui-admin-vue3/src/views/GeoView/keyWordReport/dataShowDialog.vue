<template>
  <el-dialog
    v-model="dialogTableVisible"
    class="custom_dialog"
    :show-close="false"
    :close-on-press-escape="false"
    :destroy-on-close="true"
    fullscreen
  >
    <div class="content_box">
      <header>
        <div class="title">
          {{ dataList.title }}
        </div>
      </header>
      <main>
        <div class="box_main">
          <div class="box_main_left">
            <div class="company_detail">
              <div class="company_detail_left">
                <div class="detail_icon"></div>
                <div class="detail_card">AI名片</div>

              </div>
              <div class="company_detail_right">
                <div class="mb-35px text-18px!">{{ dataList.businessCard.companyName }}</div>
                <div class="mb-25px">
                  <el-icon size="14px">
                    <PhoneFilled/>
                  </el-icon>
                  电话：
                  {{ dataList.businessCard.phone }}
                </div>
                <div class="mb-25px">
                  <el-icon size="14px">
                    <Message/>
                  </el-icon>
                  邮件：
                  {{ dataList.businessCard.email }}
                </div>
                <div class="mb-25px">
                  <el-icon size="14px">
                    <Link/>
                  </el-icon>
                  网址：
                  {{ dataList.businessCard.internet }}
                </div>
              </div>
            </div>
            <div class="article_count">
              <div class="article_title">
                文章数量统计
              </div>
              <div class="article_count_list">
                <div class="article_count_list_item">
                  <div class="article_count_list_item_title">
                    <span class="color-[#F7BA1E]">1.</span>
                    <span>近一个月</span>
                  </div>
                  <div class="article_count_list_item_value">
                    {{ dataList.articleCount.oneMonth }}篇
                  </div>
                </div>
                <div class="article_count_list_item">
                  <div class="article_count_list_item_title">
                    <span class="color-[#7E92A4]">2.</span>
                    <span>近三个月</span>
                  </div>
                  <div class="article_count_list_item_value">
                    {{ dataList.articleCount.threeMonth }}篇
                  </div>
                </div>
                <div class="article_count_list_item">
                  <div class="article_count_list_item_title">
                    <span class="color-[#CA7B59]">3.</span>
                    <span>近一年</span>
                  </div>
                  <div class="article_count_list_item_value">
                    {{ dataList.articleCount.oneYear }}篇
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="box_main_right">
            <div class="data_count_list">
              <div class="analysis_data_box" v-for="item in dataList.analysisData" :key="item.name">
                <div class="analysis_name">{{ item.name }}</div>
                <div class="analysis_number">
                  <span> {{ item.number }}</span>
                  <span>{{ item.unit }}</span>
                </div>
                <div class="analysis_bg"></div>
              </div>
            </div>
            <div class="data_count_bottom">
              <div class="data_count_bottom_left">
                <div class="data_count_bottom_title">各平台收录占比</div>
                <Echart width="600px" height="400px" :options="dataCountBottomPie"/>
              </div>
              <div class="data_count_bottom_right">
                <div class="data_count_bottom_title">蒸馏关键词排名</div>
                <Echart width="580px" height="400px" :options="dataCountBottomBar"/>
              </div>
            </div>
          </div>
        </div>
        <div class="box_footer">
          <dataShowDialogTable/>
        </div>
      </main>
    </div>
  </el-dialog>
</template>
<script setup>
import {ref, reactive} from 'vue'
import {PhoneFilled, Message, Link} from '@element-plus/icons-vue'
import dataShowDialogTable from './dataShowDialogTable.vue'

const dialogTableVisible = ref(false)
const dataList = reactive({
  title: "广州时趣科技有限公司报表名片",
  // 公司名片
  businessCard: {
    companyName: '广州时趣科技有限公司', // 公司名称
    phone: '176****2819', // 手机号
    email: 'kasjddf****jlkas@gmail.com', // 邮箱
    internet: 'www.***d.com', // 官网
  },
  // 文章数量统计
  articleCount: {
    oneMonth: '600',
    threeMonth: '1200',
    oneYear: '3000',
  },
  analysisData: [
    {
      name: '核心关键词',
      number: 12310,
      unit: '个'
    }, {
      name: '蒸馏关键词',
      number: 12931,
      unit: '个'
    }, {
      name: '品牌关键词',
      number: 210391,
      unit: '个'
    },
    {
      name: '总收录条数',
      number: '91023',
      unit: '条'
    }
  ],
  dataCount: {
    coreKeywords: '91023', // 核心关键词
    distilledKeywords: '91029', // 蒸馏关键词
    brandKeywords: '80192', // 品牌关键词
    totalEntries: '30221' // 总收录条数
  },
  // 各平台收录占比
  platformCount: [
    {name: '分类一', value: '100'},
    {name: '分类二', value: '12'},
    {name: '分类三', value: '32'},
    {name: '分类四', value: '10'},
    {name: '分类五', value: '16'},
  ],
//   蒸馏关键词排名
  distilledKeywordsRank: [
    {
      name: '关键词1',
      value: '1000'
    },
    {
      name: '关键词2',
      value: '3000'
    },
    {
      name: '关键词3',
      value: '5000'
    },
    {
      name: '关键词4',
      value: '7000'
    },
    {
      name: '关键词5',
      value: '9000'
    }, {
      name: '关键词5',
      value: '10002'
    }, {
      name: '关键词5',
      value: '8192'
    }, {
      name: '关键词5',
      value: '9182'
    }
  ]
})
const dataCountBottomPie = computed(() => {
  const list = dataList.platformCount
  const total = list.reduce((sum, cur) => sum + Number(cur.value), 0)
  const colors = [
    '#1890FF',
    '#13C2C2',
    '#2FC25B',
    '#FACC14',
    '#F04864'
  ]

  return {
    tooltip: {
      trigger: 'item'
    },

    legend: {
      orient: 'vertical',
      right: 0,
      top: 'center',
      icon: 'circle',
      textStyle: {
        color: '#666',
        fontSize: 14
      }
    },

    color: colors,

    series: [
      {
        name: '各平台收录占比',
        type: 'pie',

        radius: ['40%', '65%'], // ✅ 中空环形
        center: ['45%', '50%'], // 左移给 legend 留空间

        avoidLabelOverlap: false,

        label: {
          show: true,
          formatter: '{b}: {d}%',  // 分类名 + 百分比
          fontSize: 14,
          color: '#252632'
        },

        labelLine: {
          show: true,
          length: 15,
          length2: 15
        },

        data: list.map((item, index) => ({
          name: item.name,
          value: Number(item.value),
          itemStyle: {
            color: colors[index]
          }
        }))
      },
    ],
  }
})
const dataCountBottomBar = computed(() => {
  const list = dataList.distilledKeywordsRank
  const values = list.map(item => Number(item.value)).sort((a, b) => b - a)
  const max = Math.max(...values) + Math.max(...values) * 0.1

  return {
    grid: {
      left: 100,
      right: 70,
      top: 20,
      bottom: 20,
    },

    xAxis: {
      type: 'value',
      show: false,
      max: max
    },

    yAxis: {
      type: 'category',
      inverse: true, // 关键：让最大在最上面
      data: list.map(item => item.name),
      axisLine: {show: false},
      axisTick: {show: false},
      axisLabel: {
        margin: 20,
        color: '#252632',
        fontFamily: 'PingFang SC',
        fontSize: 16,
        fontWeight: 400,   // Medium 对应 500
        lineHeight: 16     // 100% ≈ 等于 fontSize
      }
    },

    series: [
      // ① 灰色背景柱（负责显示数值）
      {
        type: 'bar',
        barWidth: 12,
        barGap: '-100%',
        data: new Array(values.length).fill(max),
        itemStyle: {
          color: '#E5E6EB',
          borderRadius: [2, 2, 2, 2]
        },
        silent: true,
        label: {
          show: true,
          position: 'right',   // 在背景柱最右侧
          margin: 20,
          color: '#252632',
          fontSize: 16,
          fontWeight: 500,
          formatter: (params) => {
            return values[params.dataIndex]
          }
        },
      },

      // ② 蓝色真实值柱
      {
        type: 'bar',
        barWidth: 12,
        data: values,
        itemStyle: {
          color: '#165DFF',
          borderRadius: [2, 2, 2, 2]
        },
        silent: true
      }
    ]
  }
})

function openDialog() {
  dialogTableVisible.value = true
}

defineExpose({
  openDialog
})
</script>
<style scoped lang="scss">
.content_box {
  height: 100%;
  min-height: 100vh;
  width: 100vw;
  padding: 7px;
  background: url("@/assets/geoImage/dataShow/bgi_1.png") no-repeat;
  background-size: 100% 100%;
}

header {
  width: calc(100vw - 300px);
  margin-left: 150px;
  height: 100px;
  display: flex;
  justify-content: center;
  background: url("@/assets/geoImage/dataShow/header_1.png") no-repeat;
  background-size: contain;
  margin-bottom: 20px;

  .title {
    padding-top: 12px;
    font-family: Alibaba PuHuiTi, sans-serif;
    font-weight: 800;
    font-style: Heavy;
    font-size: 43px;
    line-height: 100%;
    letter-spacing: 16%;
    text-align: center;
    color: #FFFFFF;
    text-shadow: 0px 4px 0px rgba(14, 41, 65, 0.22);
  }
}

main {
  padding: 0 43px;

  .box_main {
    display: flex;
    gap: 20px;
    margin-bottom: 20px;
  }
}

main .box_main .box_main_left {
  width: calc(100vw * (515 / 1900)); // figma一比一🙂

  .company_detail {
    width: 100%;
    height: 300px;
    margin-bottom: 20px;
    display: flex;
    padding: 55px 51px;
    background: #FFFFFF;
    border-radius: 8px;

    .company_detail_left {
      display: flex;
      flex-direction: column;
      align-items: center;
      border-right: 1px solid #EFEFEF;
      padding-right: 20px;

      .detail_icon {
        width: 104px;
        height: 105px;
        background: url("@/assets/geoImage/bigScreen/dialogTag.png") no-repeat;
        background-size: 100%;
        background-position: center;

      }

      .detail_card {
        background: radial-gradient(75.78% 508.45% at 57.87% 50.78%, #0E7EFA 0%, #EFF2FF 100%);
        padding: 5px 23.5px;
        margin-top: 21px;
        border-radius: 100px;
        font-family: Alibaba PuHuiTi, sans-serif;
        font-weight: 700;
        font-size: 14px;
        leading-trim: NONE;
        line-height: 22px;
        vertical-align: middle;

      }
    }

    .company_detail_right {
      margin-left: 28px;
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-style: Medium;
      font-size: 16px;
      line-height: 100%;
      letter-spacing: 0%;
      color: #252632;

      > div {
        display: flex;
        align-items: center;
        gap: 7px;
        white-space: nowrap;
        text-overflow: ellipsis;
      }
    }
  }

  .article_count {
    width: 100%;
    height: 300px;
    border-radius: 8px;
    padding: 12px 15px;
    background-color: #fff;

    .article_title {
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-size: 18px;
      line-height: 22px;
      vertical-align: middle;
      color: #000000;
      margin-bottom: 32px;
    }

    .article_count_list {
      .article_count_list_item {
        display: flex;
        justify-content: space-between;
        height: 46px;
        color: #1D2129;
        margin-bottom: 30px;
        padding: 11px 16px;

        &:nth-child(1) {
          background: url("@/assets/geoImage/bigScreen/bg1.png") no-repeat;
          background-size: 100%;
        }

        &:nth-child(2) {
          background: url("@/assets/geoImage/bigScreen/bg2.png") no-repeat;
          background-size: 100%;
        }

        &:nth-child(3) {
          background: url("@/assets/geoImage/bigScreen/bg3.png") no-repeat;
          background-size: 100%;
        }

        .article_count_list_item_title {
          display: flex;
          gap: 8px;
          font-family: PingFang SC, sans-serif;
          font-weight: 400;
          font-size: 14px;
          line-height: 22px;

        }

        .article_count_list_item_value {
          font-family: PingFang SC, sans-serif;
          font-weight: 400;
          font-size: 16px;
          line-height: 22px;
          color: #1D2129;
        }
      }
    }

  }
}

main .box_main .box_main_right {
  width: calc(100vw * (1280 / 1900)); // figma一比一🙂

  .data_count_list {
    display: flex;
    gap: 20px;

    .analysis_data_box {
      padding: 30px;
      position: relative;
      overflow: hidden;
      width: 305px;
      height: 160px;

      &:nth-child(1) {
        background: linear-gradient(270deg, rgba(82, 187, 255, 0.1) 0%, rgba(87, 161, 247, 0.1) 100%);

        .analysis_name {
          color: #0B5695;
        }

        .analysis_number {
          color: #6386A4;
        }

        .analysis_bg {
          background: url("@/assets/geoImage/analysis_bg/analysis_bg1.png") no-repeat 100% 100%;
          background-size: 120% 110%;
        }
      }

      &:nth-child(2) {
        background: linear-gradient(270deg, rgba(110, 184, 254, 0.1) 0%, rgba(102, 138, 245, 0.1) 100%);

        .analysis_name {
          color: #3F549C;
        }

        .analysis_number {
          color: #6386A4;
        }

        .analysis_bg {
          background: url("@/assets/geoImage/analysis_bg/analysis_bg2.png") no-repeat 100% 100%;
          background-size: 120% 110%;
        }
      }

      &:nth-child(3) {
        background: linear-gradient(270deg, rgba(82, 187, 255, 0.1) 0%, rgba(87, 161, 247, 0.1) 100%);

        .analysis_name {
          color: #0B5695;
        }

        .analysis_number {
          color: #6386A4;
        }

        .analysis_bg {
          background: url("@/assets/geoImage/analysis_bg/analysis_bg3.png") no-repeat 100% 100%;
          background-size: 120% 110%;
        }
      }

      &:nth-child(4) {
        background: linear-gradient(270deg, rgba(249, 149, 153, 0.1) 0%, rgba(239, 112, 115, 0.1) 100%);

        .analysis_name {
          color: #852020;
        }

        .analysis_number {
          color: #A87171;
        }

        .analysis_bg {
          background: url("@/assets/geoImage/analysis_bg/analysis_bg4.png") no-repeat 100% 100%;
          background-size: 120% 110%;
        }
      }

      .analysis_name {
        font-family: PingFang SC, sans-serif;
        font-weight: 400;
        font-size: 24px;
        line-height: 100%;
        margin-bottom: 20px;
      }

      .analysis_number {
        font-family: PingFang SC, sans-serif;
        font-weight: 400;
        font-size: 24px;
        line-height: 32px;
        display: flex;
        align-items: flex-end;
        gap: 5px;

        span:nth-child(1) {
          font-size: 24px;
        }

        span:nth-child(2) {
          font-size: 12px;
          line-height: 25px;
        }
      }

      .analysis_bg {
        width: 100%;
        height: 100%;
        position: absolute;
        top: 0;
        left: 0;
      }
    }
  }

  .data_count_bottom {
    display: flex;
    justify-content: space-between;
    gap: 20px;
    margin-top: 20px;
    height: 440px;

    .data_count_bottom_title {
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-size: 18px;
      leading-trim: NONE;
      line-height: 22px;
      letter-spacing: 0%;
      vertical-align: middle;
      color: #000000;
    }

    .data_count_bottom_left {
      padding: 15px 12px;
      width: 50%;
      background: #fff;
      border-radius: 8px;

    }

    .data_count_bottom_right {
      padding: 15px 12px;
      width: 50%;
      background: #fff;
      border-radius: 8px;
    }
  }
}

//main .box_footer {
//  height: 850px;
//}
</style>
