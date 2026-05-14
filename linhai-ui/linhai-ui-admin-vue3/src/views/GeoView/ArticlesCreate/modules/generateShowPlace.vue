<template>
  <main>
    <div class="show_list" v-if="dataList.length" @scroll="handleScroll">
      <div class="show_item" v-for="item in dataList" :key="item.id">
        <div class="show_header">
          <div class="show_header_platform">
            <div :class="[getPlatformCssClass(item.platformFormat)]" class="w-22px h-22px"></div>
            <div class="show_header_platform_title">{{
                getPlatformLabel(item.platformFormat)
              }}
            </div>
          </div>
          <div class="show_header_detail" v-if="ALLOWED_TO_VIEW.includes(item.articleStatus)">
            <div class="show_header_time">{{
                formatTime(item.createTime, 'yyyy-MM-dd HH:mm:ss')
              }}
            </div>
            <div class="show_header_operate_list">
              <div v-show="item.articleStatus !== 2">
                <img src="@/assets/geoImage/copy.png"
                     class="copy-icon w-16px h-16px cursor-pointer"
                     @click="copyText(item.articleBody)" alt="复制"/>
              </div>
              <div>
                <img src="@/assets/geoImage/delete2.png" class="w-16px h-16px cursor-pointer"
                     @click.stop="deleteItem(item.articleId)" alt="删除"/>
              </div>
            </div>
          </div>
        </div>

        <div class="show_content"
             v-if="ALLOWED_TO_VIEW.filter(t => t !== 2).includes(item.articleStatus)"
             @click="openDetail(item)">
          <div class="show_content_title">{{ item.articleTitle }}</div>
          <div class="show_content_detail">{{ item.articleBody }}</div>
        </div>
        <div class="create_new" v-else-if="NEED_RERET_STATE.includes(item.articleStatus)">
          <img src="@/assets/auto_hosting_img/loading2.png" class="loading_image  w-140px h-114px"/>
          <span class="color-#165DFF font-400 text-14px">创作中</span>
        </div>
        <div class="create_fail" v-else-if="item.articleStatus === 2">
          <img src="@/assets/geoImage/error.png" class="w-35px h-35px"/>
          <span>生成失败</span>
        </div>
      </div>
    </div>
    <div class="empty_show_item" v-else>
      <img src="@/assets/geoImage/empty.png" class="w-200px h-200px" alt=""/>
      <span>请先在左侧添加需要发布的文章</span>
    </div>
  </main>
</template>
<script setup>
import {ref} from 'vue'
import {useDictStore} from '@/store/modules/dict'
import {ElMessage, ElMessageBox} from "element-plus";
import {getArticleResult, getArticleResultPage, deleteArticle} from "@/api/geo/generateArticle";
import Clipboard from 'clipboard';
import {formatTime} from "@/utils";

// 状态值  生成状态：0-待生成，1-生成中，2-生成失败，3-生成完成
const NEED_RERET_STATE = [0, 1] // 生成中
const ALLOWED_TO_VIEW = [2, 3] // 生成完成
const SUCCESS_STATE = [3]


const useDictStoreRef = useDictStore()
const _emit = defineEmits(['openEditMode'])
const searchParams = reactive({
  pageNo: 1,
  pageSize: 12,
  total: 100,
})

const dataList = ref([])
const taskIdList = ref([])

const publish_platform = ref([])

// 获取发布平台字典数据
async function getPlatformList() {
  publish_platform.value = await useDictStoreRef.getDictByType('platform_type') || [];
}


// 根据code获取平台图标
function getPlatformCssClass(value) {
  return publish_platform.value.find(item => item.value === value)?.cssClass || 'icon-xiaohongshu'
}

// 根据code获取平台name
function getPlatformLabel(value) {
  return publish_platform.value.find(item => item.value === value)?.label || '未知平台'
}


// 过滤HTML标签
function htmlToTextWithLineBreak(html) {
  const div = document.createElement('div')
  div.innerHTML = html

  let result = ''

  const blockTags = [
    'P', 'DIV', 'H1', 'H2', 'H3', 'H4', 'H5', 'H6',
    'LI', 'UL', 'OL', 'BLOCKQUOTE', 'BR'
  ]

  function traverse(node) {
    if (node.nodeType === 3) { // 文本节点 nodeType = 3 表示 文本节点
      // 文本节点
      result += node.textContent
    } else if (node.nodeType === 1) {
      const tag = node.tagName

      if (tag === 'BR' || tag === 'HR' || tag === 'div' || tag === 'P' || tag === 'H1' || tag === 'H2' || tag === 'H3' || tag === 'H4' || tag === 'H5' || tag === 'H6') {
        result += '\n'
      }

      node.childNodes.forEach(traverse)

      // 块级元素结束后加换行
      if (blockTags.includes(tag)) {
        result += '\n'
      }
    }
  }

  traverse(div)
  return result
      .trim()
      .replace(/\n+/g, '\n')   // 多个换行合并
}

async function deleteItem(id) {
  if (!id) return
  try {
    await ElMessageBox.confirm('删除后将无法恢复，请谨慎操作', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteArticle(id)
    if (res) {
      ElMessage.success('删除成功')
    }
    searchParams.pageNo = 1
    await initDataList(false)

  } catch {
    // 用户取消删除，不做任何操作
  }
}

// 复制内容
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
  // e.stopPropagation() // 禁止冒泡
}

const scrollTimer = ref(null)

async function handleScroll(e) {
  if (scrollTimer.value) return
  scrollTimer.value = setTimeout(() => {
    scrollTimer.value = null
    let scrollTop = e.target.scrollTop
    let scrollHeight = e.target.scrollHeight
    let offsetHeight = Math.ceil(e.target.getBoundingClientRect().height)
    let currentHeight = scrollTop + offsetHeight + 100 // 在距离底部100px时触发事件
    if (currentHeight >= scrollHeight && searchParams.pageNo * searchParams.pageSize < searchParams.total) {
      searchParams.pageNo++
      initDataList(true)
    }
  }, 1000)
}

// 初始化数据
const initDataListTimer = ref(null)
// 需要刷新的项 id list
const RefreshList = ref([])

function RefreshDataList() {
  if (initDataListTimer.value) {
    clearTimeout(initDataListTimer.value)
    initDataListTimer.value = null
  }
  initDataListTimer.value = setTimeout(async () => {
    if (!RefreshList.value.length) {
      clearTimeout(initDataListTimer.value)
      initDataListTimer.value = null
      return
    }
    const taskIds = dataList.value.map(item => item.taskInfo.id)
    const validRefreshList = RefreshList.value.filter(taskId => taskIds.includes(taskId))

    if (!validRefreshList.length) {
      clearTimeout(initDataListTimer.value)
      initDataListTimer.value = null
      return
    }

    // 解析结果数据
    // 转换单个文章数据格式（与 initDataList 保持一致）
    const transformArticleData = (taskItem, pre) => ({
      taskInfo: taskItem,
      taskId: taskItem.taskId,
      taskStatus: taskItem.status,
      prompt: taskItem.prompt,
      aiType: taskItem.aiType,
      platformFormat: taskItem.platformFormat,
      noteSubject: taskItem.noteSubject,
      articleId: pre.id,
      articleStatus: pre.status,
      articleSort: pre.sort,
      articleTitle: pre.title,
      articleBody: htmlToTextWithLineBreak(pre.body),
      createTime: pre.createTime,
    })

    const promiseList = validRefreshList.map(taskId =>
        getArticleResult(taskId).catch(error => {
          console.error(`获取文章 ${taskId} 详情失败:`, error)
          return null
        })
    )

    await Promise.all(promiseList).then(results => {
      const successResults = results.filter(item => item !== null)
      if (!successResults.length) {
        return
      }

      successResults.forEach(taskItem => {
        // 遍历任务下的所有文章明细
        if (taskItem.details && Array.isArray(taskItem.details)) {
          taskItem.details.forEach(pre => {
            const index = dataList.value.findIndex(item => item.articleId === pre.id)
            if (index !== -1) {
              const newItem = transformArticleData(taskItem, pre)
              dataList.value.splice(index, 1, newItem)
              // 如果文章已完成/失败，从刷新列表中移除
              if (!NEED_RERET_STATE.includes(newItem.taskStatus)) {
                const refreshIndex = RefreshList.value.indexOf(newItem.articleId)
                if (refreshIndex !== -1) {
                  RefreshList.value.splice(refreshIndex, 1)
                }
              }
            }
          })
          //   获取所有成功状态的 taskId
          const idsList = dataList.value.map(item => {
            if (SUCCESS_STATE.includes(item.taskStatus)) {
              return item.taskId
            }
          }).filter(item => item)
          taskIdList.value = Array.from(new Set(idsList))
        }
      })

      if (!RefreshList.value.length && initDataListTimer.value) {
        clearTimeout(initDataListTimer.value)
        initDataListTimer.value = null
      } else {
        RefreshDataList()
      }
    })
  }, 10000)
}


const loading = ref(false)

async function initDataList(isPush = false) {
  try {
    loading.value = true
    const params = {
      pageNo: searchParams.pageNo,
      pageSize: searchParams.pageSize,
    }
    const res = await getArticleResultPage(params) // todo 取消注释启用真实接口
    res.list = res.list.flatMap(item => item.details.map(pre => ({
      taskInfo: item,
      taskId: item.taskId, // 任务编号
      taskStatus: item.status, // 任务状态 0-待生成，1-生成中，2-生成失败，3-生成完成
      prompt: item.prompt, // 提示词
      aiType: item.aiType, // 生成类型（ARTICLE、IMAGE）
      platformFormat: item.platformFormat, //平台格式
      noteSubject: item.noteSubject, // 笔记题材
      articleId: pre.id, // 文章编号
      articleStatus: pre.status,// 文章状态 0-待生成，1-生成中，2-生成失败，3-生成完成
      articleSort: pre.sort, // 文章排序
      articleTitle: pre.title, // 笔记内容
      articleBody: htmlToTextWithLineBreak(pre.body), // 笔记内容
      createTime: pre.createTime, // 文章创建时间
    })))
    if (isPush) {
      dataList.value.push(...res.list)
    } else {
      dataList.value = res.list
    }
    const idsList = dataList.value.map(item => {
      if (SUCCESS_STATE.includes(item.taskStatus)) {
        return item.taskId
      }
    }).filter(item => item)
    taskIdList.value = Array.from(new Set(idsList))

    searchParams.total = res.total || 0

    //  判断是否需要重新获取数据 iterable
    let rlist1 = res.list
        .filter(item => NEED_RERET_STATE.includes(item.taskStatus))
        .map(item => item.taskInfo.id)

    const set = new Set([...rlist1, ...RefreshList.value])
    RefreshList.value = Array.from(set)
    if (RefreshList.value.length) {
      RefreshDataList()
    }

  } catch (e) {
    ElMessage.error('获取失败，请重新尝试')
    console.log(e)
  } finally {
    loading.value = false
  }
}

// 打开编辑模式
async function openDetail(item) {
  console.log(item)
  // if (ALLOWED_TO_VIEW.includes(item.articleStatus)) {
  //   const list = taskIdList.value.map((pre, index) => {
  //     if (pre === item.taskId) {
  //       return {
  //         index: index + 1,
  //         articleSort: item.articleSort,
  //         articleId: item.articleId
  //       }
  //     }
  //   }).filter(item => item)
  //   if (list[0]) {
  //     const pageSize = 7
  //     const pageNo = Math.floor(list[0].index / pageSize) + 1
  //     _emit('openEditMode', item, {pageNo, pageSize, ...list[0]})
  //   }
  // }
  _emit('openEditMode', item)

}

// 创建新图片
async function getNewImageList() {
  searchParams.pageNo = 1
  await initDataList(false)
}

defineExpose({
  getNewImageList
})

onMounted(async () => {
  await getPlatformList()
  await initDataList(false)
})
onUnmounted(() => {
  clearTimeout(scrollTimer.value)
  clearTimeout(initDataListTimer.value)

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
  //scrollbar-width: thin;
  //scrollbar-color: rgba(255, 255, 255, 0.8) transparent;
  //scrollbar-width: 0;
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
          leading-trim: NONE;
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
      gap: 20px;
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
        leading-trim: NONE;
        line-height: 22px;
        letter-spacing: 0%;
        color: #252632;
      }

      .show_content_detail {
        white-space: pre-wrap; // 换行
        display: -webkit-box;
        -webkit-line-clamp: 10;
        -webkit-box-orient: vertical;
        overflow: hidden;
        text-overflow: ellipsis;
        font-family: PingFang SC, sans-serif;
        font-weight: 400;
        font-size: 14px;
        leading-trim: NONE;
        line-height: 22px;
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
      background: url("@/assets/auto_hosting_img/bgc_1.png") no-repeat;
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
      font-style: Regular;
      font-size: 17px;
      leading-trim: NONE;
      line-height: 22px;
      letter-spacing: 0%;
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
    leading-trim: NONE;
    line-height: 150%;
    letter-spacing: 0%;
    text-align: center;
    color: #646363;
  }
}
</style>
