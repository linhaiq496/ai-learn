<template>
  <div class="container_data">
    <div class="analysis_data">
      <div class="analysis_data_box" v-for="item in analysisData" :key="item.name">
        <div class="analysis_name">{{ item.name }}</div>
        <div class="analysis_number">
          <span> {{ item.number }}</span>
          <span>{{ item.unit }}</span>
        </div>
        <div class="analysis_bg"></div>
      </div>
    </div>
    <!--热门AI工具关键词数量统计-->
    <div class="hot_data">
      <div class="hot_data_header">热门AI工具关键词数量统计</div>
      <div class="hot_data_box">
        <el-table :data="hotData">
          <el-table-column prop="toolName" label="工具名称">
            <template #default="{row}">
              <div class="tool_name">
                <!--   <el-image-->
                <!--     :src="row.iconUrl"-->
                <!--     class="w-20px h-20px"-->
                <!--     fit="cover"-->
                <!--     :preview-src-list="null"-->
                <!--   />-->
                <div :class="row.iconUrl" class="w-20px h-20px"></div>
                <span> {{ row.toolName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="aiSearchCoverage" label="AI搜索场景覆盖"/>
          <el-table-column prop="distillKeywords" label="蒸馏关键词数量"/>
        </el-table>
      </div>

    </div>
    <div class="new_data">
      <div class="new_data_left">
        <div class="new_data_header">
          <span>最新发布文章</span>
          <div class="more_data" @click="lookMoreInfo">
            <span>更多</span>
            <el-icon>
              <ArrowRight/>
            </el-icon>
          </div>
        </div>
        <div class="new_data_table" ref="leftDataRef"
             style="max-height: calc(100vh - 30px - 200px - 360px);">
          <el-table :data="newPublishArticle" :max-height="null" v-loading="isNewLoading">
            <el-table-column prop="title" width="450">
              <template #default="{row}">
                <el-tooltip :content="row.title" placement="top">
                  <div class="new_data_title">{{ row.title }}</div>
                </el-tooltip>

              </template>
            </el-table-column>
            <el-table-column width="150">
              <template #default="{row}">
                <div class="new_data_platform">
                  <div :class="[getPlatformCssClass(row.platform)]" class="w-20px h-20px"></div>
                  {{ getPlatformName(row.platform) }}
                </div>
              </template>
            </el-table-column>
            <el-table-column>
              <template #default="{row}">
                <div class="new_data_time">{{ formatToDateTime(row.createTime) }}</div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <div class="new_data_right">
        <div class="new_data_header">
          <span>热门关键词排名</span>
        </div>
        <div class="new_data_table">
          <el-table :data="hotKeywordList" max-height="240px" v-loading="isHotLoading">
            <el-table-column prop="coreKeyword" label="核心关键词"/>
            <el-table-column prop="extendedWords" label="扩展词"/>
            <el-table-column prop="rankingPlatformIcon" label="排行平台">
              <template #default="{row}">
                <div class="new_data_rank">
                  <!--                  <img :src="row.rankingPlatformIcon" alt="" class="w-30px h-30px"/>-->
                  <div :class="row.rankingPlatformIcon" class="w-24px h-24px"></div>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import {reactive, ref} from 'vue'
import {formatToDateTime} from "../../../utils/dateUtil";
import {ArrowRight} from '@element-plus/icons-vue'

import {useRouter} from 'vue-router'
import {getHomePageData, getHomePageHotWords} from "@/api/geo/analysisData";
import {useDictStore} from "@/store/modules/dict";
import {getPublishTaskLogsPage} from "@/api/geo/publishTask";

const router = useRouter()
const leftDataRef = ref(null)
const populer_platform = ref([]); //发布热门平台
const publish_platform = ref([]); //发布平台
const useDictStoreRef = useDictStore()

const analysisData = reactive([
  {
    name: '文章数量',
    number: 0,
    unit: '次'
  },
  {
    name: '发布数量',
    number: 0,
    unit: '次'
  },
  {
    name: '账号数量',
    number: 0,
    unit: '个'
  },
  {
    name: '算力损耗',
    number: 0,
    unit: 'Tokens'
  }
])

const hotData = ref([
  {toolName: '豆包', aiSearchCoverage: 87432, distillKeywords: 87432, iconUrl: 'icon-doubao'},
  {toolName: 'DeepSeek', aiSearchCoverage: 87432, distillKeywords: 87432, iconUrl: 'icon-DeepSeek'},
  {toolName: '腾讯元宝', aiSearchCoverage: 87432, distillKeywords: 87432, iconUrl: 'icon-hunyuan'},
  {toolName: '文心一言', aiSearchCoverage: 87432, distillKeywords: 87432, iconUrl: 'icon-wxyy'},
  {toolName: 'Kimi', aiSearchCoverage: 87432, distillKeywords: 87432, iconUrl: 'icon-kimi'},
  {toolName: '通义千问', aiSearchCoverage: 87432, distillKeywords: 87432, iconUrl: 'icon-qwen'},
])

const newPublishArticle = ref([])


const hotKeywordList = ref([])

// 获取发布热门平台字典数据
async function getPopulerPlatformList() {
  populer_platform.value = await useDictStoreRef.getDictByType('populer_platform') || [];
}

// 获取发布平台字典数据
async function getPlatformList() {
  publish_platform.value = await useDictStoreRef.getDictByType('publish_platform') || [];
}


// 查看更多
function lookMoreInfo() {
  router.push({path: '/media/ReleaseRecord'})
}

// 获取analysisData数据
async function getAnalysisData() {
  try {
    const res = await getHomePageData()
    analysisData.forEach(item => {
      switch (item.name) {
        case '文章数量':
          item.number = res?.articleCount || 0;
          break;
        case '发布数量':
          item.number = res?.publishCount || 0;
          break;
        case '账号数量':
          item.number = res?.accountCount || 0;
          break;
        case '算力损耗':
          item.number = res?.powerLoss || 0;
          break;
      }
    })
  } catch (e) {
    console.log(e)
  }
}

// 获取最新发布文章
const isNewLoading = ref(false)

async function getNewPublishArticle() {
  try {
    isNewLoading.value = true
    const searchVo = {
      pageNo: 1,
      pageSize: 20,
      total: 100,
      title: "",
    }
    const res = await getPublishTaskLogsPage(searchVo)
    newPublishArticle.value = res.list

  } catch (e) {
    console.log(e)
  } finally {
    isNewLoading.value = false
  }
}

// 根据code获取平台图标
function getPlatformCssClass(code) {
  return publish_platform.value.find(item => item.value === code)?.cssClass || ''
}

// 根据code获取平台名称
function getPlatformName(code) {
  return publish_platform.value.find(item => item.value === code)?.label || ''
}


//  获取热词数据
const isHotLoading = ref(false)

async function getHotWords() {
  try {
    isHotLoading.value = true
    const res = await getHomePageHotWords(10)
    hotKeywordList.value = res.map(item => {
      return {
        coreKeyword: item.keyword,
        extendedWords: item.extend,  // 拓展词
        rankingPlatform: populer_platform.value.find(pre => pre.value === item.platform)?.label || '未知',// 排行平台
        rankingPlatformIcon: populer_platform.value.find(pre => pre.value === item.platform)?.cssClass || ''// 排行平台
      }
    })

  } catch (e) {
    console.log(e)
  } finally {
    isHotLoading.value = false
  }
}


// 自动滚动
function autoScroll() {
  const scrollContainer = leftDataRef.value
  if (!scrollContainer) return

  let scrollPosition = 0
  const scrollStep = 1 // 每帧 1px

  const scroll = () => {
    const maxScroll =
      scrollContainer.scrollHeight - scrollContainer.clientHeight

    if (scrollPosition >= maxScroll) {
      // ⭐ 滚到底，回到顶部（关键）
      // scrollPosition = 0
      if (newPublishArticle.value.length !== 0) {
        newPublishArticle.value = [...newPublishArticle.value, ...newPublishArticle.value]
      }
    } else {
      scrollPosition += scrollStep
    }

    scrollContainer.scrollTop = scrollPosition
    requestAnimationFrame(scroll)
  }

  scroll()
}


onMounted(async () => {
  isNewLoading.value = true
  isHotLoading.value = true
  try {
    await getPlatformList()
    await getPopulerPlatformList()
    // 获取数据
    await getAnalysisData()
    await getHotWords()
    await getNewPublishArticle()
  } finally {
    isNewLoading.value = false
    isHotLoading.value = false
  }

  nextTick(() => {
    autoScroll()
  })
})
onUnmounted(() => {
  isNewLoading.value = false
  isHotLoading.value = false
})

</script>
<style scoped lang="scss">
$all_height: calc(100vh - 30px);
$analysis_data_height: 200px;
$hot_data_height: 360px;
$new_data_height: calc($all_height - $analysis_data_height - $hot_data_height);
$box_gap: 20px;

.container_data {
  width: 100%;
  height: $all_height;
  display: flex;
  flex-direction: column;
  gap: $box_gap;
}

.analysis_data {
  width: 100%;
  height: $analysis_data_height;
  background-color: #fff;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  padding: 20px;
  border-radius: 6px;

  .analysis_data_box {
    padding: 30px;
    position: relative;
    overflow: hidden;

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
        background-size: 100%;
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
        background-size: 100%;
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
        background-size: 100%;
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
        background-size: 100%;
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

.hot_data {
  width: 100%;
  height: $hot_data_height;
  background-color: #fff;
  padding: 20px;
  overflow-y: auto;
  position: relative;
  border-radius: 6px;

  &::-webkit-scrollbar {
    display: none;
  }

  .hot_data_header {
    margin-bottom: 10px;
    font-family: PingFang SC, sans-serif;
    font-weight: 500;
    font-size: 20px;
    line-height: 28px;
    color: #1D2129;
  }

  .hot_data_box {
    :deep(th.el-table__cell) {
      padding: 0;
      height: 40px;
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-size: 13px;
      line-height: 22px;
    }

    :deep(td.el-table__cell) {
      font-family: Roboto, sans-serif;
      font-weight: 400;
      font-size: 14px;
      line-height: 22px;
    }

    .tool_name {
      display: flex;
      align-items: center;
      gap: 8px;

      span {
        font-family: PingFang SC, sans-serif;
        font-weight: 400;
        font-size: 14px;
        line-height: 100%;
        color: #252632;

      }
    }
  }
}

.new_data {
  width: 100%;
  height: $new_data_height;
  display: flex;

  .new_data_left {
    scrollbar-width: none;
  }

  .new_data_left, .new_data_right {
    width: calc((100% - 20px) / 2);
    background-color: #fff;
    border-radius: 6px;
    padding: 20px;
    overflow: auto;

    .new_data_header {
      margin-bottom: 5px;
      display: flex;
      align-items: center;
      justify-content: space-between;

      & > span:nth-child(1) {
        font-family: PingFang SC, sans-serif;
        font-weight: 500;
        font-size: 20px;
        line-height: 28px;
        color: #1D2129;
      }

      .more_data {
        font-family: PingFang SC, sans-serif;
        font-weight: 400;
        font-size: 12px;
        line-height: 20px;
        text-decoration: underline;
        text-underline-offset: 3px; // 下划线距离文字的距离
        color: #0D6CE4;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }

    .new_data_table {
      max-height: 300px;
      overflow: auto;
      scrollbar-width: none;
      //scrollbar-color: rgba(22, 93, 255, 0.1) transparent;

      .new_data_title {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-family: PingFang SC, sans-serif;
        font-weight: 400;
        font-size: 14px;
        line-height: 22px;
        color: #4E5969;
      }

      .new_data_platform {
        display: flex;
        align-items: center;
        gap: 8px;
        color: #4E5969;
        font-family: PingFang SC, sans-serif;
        font-weight: 400;
        font-size: 14px;
        line-height: 22px;
      }

    }
  }

  .new_data_right {
    margin-left: 20px;

    .new_data_rank {
      display: flex;
      align-items: center;
      justify-content: flex-start;
    }
  }
}
</style>
