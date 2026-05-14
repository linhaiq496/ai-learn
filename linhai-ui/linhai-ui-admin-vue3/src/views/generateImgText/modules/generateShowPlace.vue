<template>
  <main>
    <div class="show_list" v-if="dataList.length" @scroll="handleScroll">
      <div class="show_item" v-for="item in dataList" :key="item.id">
        <div class="show_header">
          <div class="show_header_platform">
            <div :class="[getPlatformCssClass(item.platform)]" class="w-22px h-22px"></div>
            <div class="show_header_platform_title">{{ getPlatformLabel(item.platform) }}</div>
          </div>
          <div class="show_header_detail" v-if="VIEWABLE_STATES.includes(item.articleStatus)">
            <div class="show_header_time">{{ formatTime(item.createTime, 'yyyy-MM-dd HH:mm:ss') }}</div>
            <div class="show_header_operate_list">
              <div v-show="item.articleStatus === SUCCESS_STATUS">
                <img
                    src="@/assets/geoImage/copy.png"
                    class="copy-icon w-16px h-16px cursor-pointer"
                    @click="copyText(item.articleBody)"
                    alt="复制"
                />
              </div>
              <div>
                <img
                    src="@/assets/geoImage/delete2.png"
                    class="w-16px h-16px cursor-pointer"
                    @click.stop="deleteItem(item.articleId)"
                    alt="删除"
                />
              </div>
            </div>
          </div>
        </div>

        <div
            class="show_content"
            v-if="item.articleStatus === SUCCESS_STATUS"
            @click="openDetail(item)"
        >
          <div class="show_content_title">{{ item.articleTitle }}</div>
          <div class="show_content_image_list">
            <div
                class="show_content_image_item"
            >
              <img :src="item.coverImageUrl" alt="" class="show_content_image"/>
              <div class="show_content_cover_tag">封面</div>
            </div>
            <div
                class="show_content_image_item"
                v-for="(image, index) in item.imageList"
                :key="`${item.id}-${index}`"
            >
              <img :src="image" alt="" class="show_content_image"/>
            </div>
          </div>
          <div class="show_content_detail">{{ item.articleBody }}</div>
        </div>
        <div class="create_new" v-else-if="NEED_REFRESH_STATES.includes(item.articleStatus)">
          <img src="@/assets/auto_hosting_img/loading2.png" class="loading_image w-140px h-114px"/>
          <span class="color-#165DFF font-400 text-14px">创作中...</span>
        </div>
        <div class="create_fail" v-else-if="item.articleStatus === FAILED_STATUS">
          <img src="@/assets/geoImage/error.png" class="w-35px h-35px"/>
          <span>生成失败</span>
        </div>
      </div>
    </div>
    <div class="empty_show_item" v-else>
      <img src="@/assets/geoImage/empty.png" class="w-200px h-200px" alt=""/>
      <span>生成的内容会在这里展示哦</span>
    </div>
  </main>
</template>

<script setup>
import {onMounted, onUnmounted, reactive, ref} from 'vue'
import {useDictStore} from '@/store/modules/dict'
import {ElMessage, ElMessageBox} from 'element-plus'
import Clipboard from 'clipboard'
import {formatTime} from '@/utils'
import {
  deleteResult,
  getTaskDetail,
  getTaskDetailPage,
  LongArticleStatus
} from '@/api/ai/longArticle'

const NEED_REFRESH_STATES = [LongArticleStatus.PENDING, LongArticleStatus.EXECUTING]
const VIEWABLE_STATES = [LongArticleStatus.SUCCESS, LongArticleStatus.FAILED]
const SUCCESS_STATUS = LongArticleStatus.SUCCESS
const FAILED_STATUS = LongArticleStatus.FAILED

const useDictStoreRef = useDictStore()
const _emit = defineEmits(['openEditMode'])

const searchParams = reactive({
  pageNo: 1,
  pageSize: 12,
  total: 100
})

const dataList = ref([])
const taskIdList = ref([])
const publishPlatform = ref([])
const scrollTimer = ref(null)
const refreshTimer = ref(null)

async function getPlatformList() {
  publishPlatform.value = await useDictStoreRef.getDictByType('platform_type') || []
}

function getPlatformCssClass(value) {
  return publishPlatform.value.find(item => item.value === value || item.label === value)?.cssClass || 'icon-xiaohongshu'
}

function getPlatformLabel(value) {
  return publishPlatform.value.find(item => item.value === value || item.label === value)?.label || value || '未知平台'
}

function normalizeArticleBody(body) {
  if (!body) return ''

  const div = document.createElement('div')
  div.innerHTML = body

  let result = ''
  const blockTags = ['P', 'DIV', 'H1', 'H2', 'H3', 'H4', 'H5', 'H6', 'LI', 'UL', 'OL', 'BLOCKQUOTE', 'BR']

  function traverse(node) {
    if (node.nodeType === 3) {
      result += node.textContent
      return
    }

    if (node.nodeType !== 1) return

    const tag = node.tagName
    if (tag === 'BR' || tag === 'HR' || tag === 'DIV' || tag === 'P' || tag.startsWith('H')) {
      result += '\n'
    }

    node.childNodes.forEach(traverse)

    if (blockTags.includes(tag)) {
      result += '\n'
    }
  }

  traverse(div)

  return result.trim().replace(/\n+/g, '\n') || body
}

function buildTaskItems(task) {
  const fallbackResults = Array.from({length: task.articleCount || 1}, (_, index) => ({
    id: `placeholder-${task.id}-${index + 1}`,
    articleIndex: index + 1,
    status: task.status,
    title: '',
    body: '',
    finalArticle: '',
    errorMessage: task.errorMessage,
    createTime: task.createTime || task.startTime || task.finishTime
  }))

  const results = task.results?.length ? task.results : fallbackResults

  return results.map((result, index) => ({
    id: result.id ?? `placeholder-${task.id}-${index + 1}`,
    taskId: task.id,
    taskStatus: task.status,
    platform: task.platform,
    articlePrompt: task.query,
    articleType: task.articleType,
    articleId: result.id ?? null,
    articleStatus: typeof result.status === 'number' ? result.status : task.status,
    articleSort: result.articleIndex || index + 1,
    articleTitle: result.title || `第 ${result.articleIndex || index + 1} 篇长图文`,
    articleBody: normalizeArticleBody(result.body || result.finalArticle || task.query || ''),
    tags: result.tags || '',
    imageList: result.imgUrls || [],
    coverImageUrl: result.coverImageUrl || '',
    finalArticle: result.finalArticle || '',
    errorMessage: result.errorMessage || task.errorMessage || '',
    createTime: result.createTime || task.finishTime || task.startTime || task.createTime
  }))
}

function replaceTaskItems(task) {
  const nextItems = buildTaskItems(task)
  const insertIndex = dataList.value.findIndex(item => item.taskId === task.id)

  if (insertIndex === -1) {
    dataList.value.push(...nextItems)
    return
  }

  const removeCount = dataList.value.filter(item => item.taskId === task.id).length
  dataList.value.splice(insertIndex, removeCount, ...nextItems)
}

async function refreshTaskItems() {
  if (!taskIdList.value.length) return

  const currentTaskIds = dataList.value.map(item => item.taskId)
  const validTaskIds = Array.from(new Set(taskIdList.value)).filter(taskId =>
      currentTaskIds.includes(taskId)
  )

  taskIdList.value = validTaskIds

  if (!validTaskIds.length) return

  const promiseList = validTaskIds.map(taskId =>
      getTaskDetail(taskId).catch(error => {
        console.error(`获取长图文任务 ${taskId} 详情失败:`, error)
        return null
      })
  )

  const results = await Promise.all(promiseList)
  const successResults = results.filter(item => item !== null)
  const failedTaskIds = validTaskIds.filter((taskId, index) => results[index] === null)

  if (!successResults.length) {
    taskIdList.value = failedTaskIds
    return
  }

  successResults.forEach(task => {
    replaceTaskItems(task)
  })

  taskIdList.value = [
    ...failedTaskIds,
    ...successResults
        .filter(task => NEED_REFRESH_STATES.includes(task.status))
        .map(task => task.id)
  ]
}

function refreshDataList() {
  if (refreshTimer.value) {
    clearTimeout(refreshTimer.value)
    refreshTimer.value = null
  }

  if (!taskIdList.value.length) return

  refreshTimer.value = setTimeout(async () => {
    if (!taskIdList.value.length) {
      clearTimeout(refreshTimer.value)
      refreshTimer.value = null
      return
    }

    await refreshTaskItems()

    if (taskIdList.value.length) {
      refreshDataList()
    } else if (refreshTimer.value) {
      clearTimeout(refreshTimer.value)
      refreshTimer.value = null
    }
  }, 10000)
}

async function deleteItem(id) {
  if (!id) return

  try {
    await ElMessageBox.confirm('删除后将无法恢复，请谨慎操作', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteResult(id)
    if (res) {
      ElMessage.success('删除成功')
    }

    await initDataList()
  } catch {
  }
}

function copyText(text) {
  if (!text) return

  try {
    const clipboard = new Clipboard('.copy-icon', {
      text: () => text
    })

    clipboard.on('success', () => {
      ElMessage.success('复制成功')
      clipboard.destroy()
    })

    clipboard.on('error', () => {
      ElMessage.error('复制失败')
      clipboard.destroy()
    })
  } catch (e) {
    console.log(e)
  }
}

function getQueryPageSize() {
  return searchParams.pageNo * searchParams.pageSize
}

async function handleScroll(e) {
  if (scrollTimer.value) return

  scrollTimer.value = setTimeout(async () => {
    scrollTimer.value = null

    const scrollTop = e.target.scrollTop
    const scrollHeight = e.target.scrollHeight
    const offsetHeight = Math.ceil(e.target.getBoundingClientRect().height)
    const currentHeight = scrollTop + offsetHeight + 100

    if (currentHeight >= scrollHeight && getQueryPageSize() < searchParams.total) {
      searchParams.pageNo++
      await initDataList()
    }
  }, 1000)
}

async function initDataList() {
  try {
    const params = {
      pageNo: 1,
      pageSize: getQueryPageSize()
    }

    const res = await getTaskDetailPage(params)
    const taskList = res.list || []

    dataList.value = taskList.flatMap(buildTaskItems)
    searchParams.total = res.total || 0
    taskIdList.value = taskList
        .filter(item => NEED_REFRESH_STATES.includes(item.status))
        .map(item => item.id)

    if (taskIdList.value.length) {
      refreshDataList()
    } else if (refreshTimer.value) {
      clearTimeout(refreshTimer.value)
      refreshTimer.value = null
    }
  } catch (e) {
    ElMessage.error('获取失败，请重新尝试')
    console.log(e)
  }
}

function openDetail(item) {
  if (item.articleStatus === SUCCESS_STATUS) {
    _emit('openEditMode', item)
  }
}

async function getNewImageList() {
  searchParams.pageNo = 1
  await initDataList()
}

defineExpose({
  getNewImageList
})

onMounted(async () => {
  await getPlatformList()
  await initDataList()
})

onUnmounted(() => {
  clearTimeout(scrollTimer.value)
  clearTimeout(refreshTimer.value)
})
</script>

<style scoped lang="scss">
main {
  height: 100%;
  padding: 20px;
  position: relative;
}

main .show_list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-auto-rows: 340px;
  gap: 20px 18px;
  height: calc(100vh - 80px);
  overflow: auto;

  &::-webkit-scrollbar {
    display: none;
  }

  .show_item {
    width: 340px;
    height: 340px;
    border-radius: 6px;
    border: 1px solid #F7F8FA;
    padding: 20px;
    overflow: hidden;

    &:hover {
      .show_header .show_header_detail {
        display: flex;
      }
    }

    &::-webkit-scrollbar {
      width: 0;
    }

    .show_header {
      display: flex;
      justify-content: space-between;
      height: 24px;

      .show_header_platform {
        display: flex;
        gap: 5px;
        align-items: center;

        .show_header_platform_title {
          font-family: PingFang SC, sans-serif;
          font-weight: 400;
          font-size: 12px;
          line-height: 100%;
          letter-spacing: 0%;
          color: #252632;
        }
      }

      .show_header_detail {
        display: none;
        gap: 5px;

        .show_header_time {
          font-family: PingFang SC, sans-serif;
          font-weight: 400;
          font-size: 12px;
          line-height: 22px;
          color: #5E5E5E;
          display: flex;
          align-items: center;
        }

        .show_header_operate_list {
          display: flex;
          gap: 5px;

          > div {
            display: flex;
            align-items: center;
          }
        }
      }
    }

    .show_content {
      display: flex;
      margin-top: 20px;
      gap: 10px;
      flex-direction: column;
      overflow: hidden;
      cursor: pointer;

      .show_content_title {
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        font-family: PingFang SC, sans-serif;
        font-weight: 600;
        font-size: 16px;
        line-height: 22px;
        letter-spacing: 0%;
        color: #252632;
      }

      .show_content_image_list {
        display: flex;
        gap: 8px;
        overflow-x: auto;
        overflow-y: hidden;
        padding-bottom: 2px;

        &::-webkit-scrollbar {
          display: none;
        }

        .show_content_image_item {
          position: relative;
          flex: 0 0 auto;
          border-radius: 3px;
          overflow: hidden;
          min-width: 67px;
          height: 90px;
          background-color: #F5F6FA;

          .show_content_image {
            display: block;
            width: auto;
            min-width: 67px;
            max-width: 100%;
            height: 90px;
            border-radius: 3px;
            object-fit: cover;
          }

          .show_content_cover_tag {
            position: absolute;
            right: 0;
            bottom: 0;
            padding: 2px 5.2px;
            border-top-left-radius: 3px;
            background-color: #FF8000;
            color: #FFFFFF;
            font-family: PingFang SC, sans-serif;
            font-weight: 400;
            font-size: 12px;
            line-height: 100%;
            letter-spacing: 0;
          }
        }
      }

      .show_content_detail {
        white-space: pre-wrap;
        display: -webkit-box;
        //-webkit-line-clamp: 5;
        min-height: 100px;
        -webkit-box-orient: vertical;
        overflow: hidden;
        text-overflow: ellipsis;
        font-family: PingFang SC, sans-serif;
        font-weight: 400;
        font-size: 14px;
        line-height: 24px;
        color: #252632;
      }
    }

    .create_new {
      height: calc(100% - 20px - 20px * 2);
      margin-top: 20px;
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 6px;
      background: url('@/assets/auto_hosting_img/bgc_1.png') no-repeat;
      background-size: cover;

      @keyframes spin {
        0% {
          transform: rotate(0deg);
        }
        100% {
          transform: rotate(360deg);
        }
      }

      .loading_image {
        width: 20px;
        height: 20px;
        animation: spin 2s linear infinite;
      }
    }

    .create_fail {
      height: calc(100% - 24px - 20px - 20px * 2);
      margin-top: 20px;
      display: flex;
      justify-content: center;
      align-items: center;
      flex-direction: column;
      gap: 20px;
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-size: 17px;
      line-height: 22px;
      letter-spacing: 0;
      color: #252632;
    }
  }
}

main .empty_show_item {
  display: flex;
  flex-direction: column;
  gap: 7px;
  position: absolute;
  top: calc(50% - 30px);
  left: 50%;
  transform: translate(-50%, -50%);

  span {
    font-family: PingFang SC, sans-serif;
    font-weight: 400;
    font-size: 15px;
    line-height: 150%;
    letter-spacing: 0%;
    text-align: center;
    color: #646363;
  }
}
</style>
