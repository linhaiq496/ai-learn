<template>
  <div class="show_box" ref="show_box" @scroll="handleScroll">
    <div v-if="dataList.length">
      <div class="show_item" v-for="item in dataList" :key="item.taskId">
        <div class="show_item_header">
          <!-- 左侧 -->
          <div class="header-left">
            <!-- 标题 -->
            <div class="title-row">
              <img
                  src="../../../../assets/auto_hosting_img/titleIcon.png"
                  class="w-20px h-20px"
              />
              <span class="title-text">电商组图</span>
            </div>

            <!-- 描述 -->
            <div class="desc-row">
            <span class="desc-text">
              {{ item.taskDesc }}
            </span>
              <img
                  src="../../../../assets/auto_hosting_img/copy.png"
                  class="copy-icon"
                  @click="copyDesc(item.taskDesc)"
              />
            </div>
          </div>

          <!-- 右侧操作 -->
          <div class="header-right" v-show="!(item?.imageList?.some(item => item.status !== 3 && item.status !== 2))">
            <img
                src="../../../../assets/auto_hosting_img/download1.png"
                class="action-icon"
                @click="downloadALL(item)"
            />
            <img
                src="../../../../assets/auto_hosting_img/delete1.png"
                class="action-icon"
                @click="deleteAll(item)"
            />
          </div>
        </div>
        <!--图片列表-->
        <div class="show_item_image_List">
          <imageShowPlace :imageList="item.imageList"/>
        </div>
      </div>
    </div>
    <div v-else class="empty_box">
      <img src="../../../../assets/auto_hosting_img/empty.png" class="w-300px h-300px"/>
      <span>生成的内容会在这里展示～</span>
    </div>
  </div>
</template>
<script setup>
import {ref, watch, onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import imageShowPlace from './imageShowPlace.vue'
import {
  getPrompt,
  createImageTask,
  uploadImage,
  downloadImageZip,
  getImageResultPage,
  deleteImageResult,
  getHotPage
} from '@/api/generateImage'
import download from "@/utils/download";
import Clipboard from 'clipboard';

const dataList = ref([])
const scrollTimer = ref(null)
const searchVo = reactive({
  pageNo: 1,
  pageSize: 5,
  total: 100
})
const show_box = ref(null)

function copyDesc(text) {
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
}

async function deleteAll(taskItem) {
  try {
    // 二次确认删除
    const confirmResult = await ElMessageBox.confirm(
        '确定要删除这个任务吗？此操作不可恢复。',
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    if (confirmResult === 'confirm') {
      console.log('deleteAll', taskItem)
      const res = await deleteImageResult(taskItem.taskId)
      if (res) {
        ElMessage.success('删除成功')
        await getList()
      } else {
        ElMessage.error('删除失败')
      }
    }
  } catch (err) {
    if (err !== 'cancel') {
      console.log(err)
    }
  }
}

const isDownloading = ref(false)

async function downloadALL(item) {
  try {
    if (isDownloading.value) return ElMessage.error('正在下载中，请稍后再试')
    ElMessage.success('开启下载，请稍后')
    isDownloading.value = true
    const res = await downloadImageZip({id: item.taskId})
    download.zip(res, item.taskDesc)
  } catch (err) {
    console.log(err)
  } finally {
    isDownloading.value = false
  }
}

// 创建任务开启轮询
async function openGetImage(data) {
  try {
    searchVo.pageNo = 1
    show_box.value.scrollTop = 0
    await reapeatgetList()
  } catch (err) {
    console.log(err)
  }
}

const getLisTimer = ref(null)
const lastTimeToget = ref(false) // 最后一次获取图片任务完成


async function reapeatgetList() {
  if (getLisTimer.value) {
    clearTimeout(getLisTimer.value)
  }
  await getList({pageNo: 1, pageSize: searchVo.pageSize}, "replacePage")
  getLisTimer.value = setInterval(async () => {
    await getList({pageNo: 1, pageSize: searchVo.pageSize}, "replacePage")
    if (dataList.value.some(item => item.taskStatus === 1 || item.taskStatus === 0)) {
      //继续轮询搜索
    } else if (dataList.value.some(item => item.taskStatus === 3 || item.taskStatus === 2) && !lastTimeToget.value) {
      lastTimeToget.value = true
    } else {
      clearTimeout(getLisTimer.value)
      getLisTimer.value = null
    }
  }, 5000)
}


async function handleScroll(e) {
  if (scrollTimer.value) return
  scrollTimer.value = setTimeout(() => {
    scrollTimer.value = null
    let scrollTop = e.target.scrollTop
    let scrollHeight = e.target.scrollHeight
    let offsetHeight = Math.ceil(e.target.getBoundingClientRect().height)
    let currentHeight = scrollTop + offsetHeight + 550 // 在距离底部550px时触发事件
    if (currentHeight >= scrollHeight && searchVo.pageNo * searchVo.pageSize < searchVo.total) {
      searchVo.pageNo++
      getList(searchVo, 'append')
    }
  }, 1000)
}

/**
 * 获取电商组图列表
 * @param params 查询参数
 * @param mode 'reset' | 'append' | 'replacePage'
 */

async function getList(
    params = unref(searchVo),
    mode = 'reset' // 'reset' | 'append' | 'replacePage'
) {
  try {
    const res = await getImageResultPage(params)

    searchVo.total = res.total || 0
    const list = normalizeList(res.list)

    if (!list.length) {
      if (mode === 'reset') {
        dataList.value = []
      }
      return
    }

    switch (mode) {
      case 'append':
        dataList.value = [...dataList.value, ...list]
        break

      case 'replacePage': {
        const start = (params.pageNo - 1) * params.pageSize
        dataList.value.splice(start, params.pageSize, ...list)
        break
      }

      case 'reset':
      default:
        dataList.value = list
        break
    }

    dataList.value = dataList.value.map(item => {
      return {
        ...item,
        imageList: item.imageList.map(img => {
          return {
            ...img,
            imageUrl: img.imageUrl // todo
          }
        })
      }
    })
    // .replace('127.0.0.1','192.168.1.52')
  } catch (e) {
    console.error('获取列表失败', e)
  }
}

/**
 * 数据格式化
 * @param list
 * @return {{taskDesc, imageList, taskId: *, taskStatus: *}[]}
 */
function normalizeList(list = []) {
  return list.map(item => ({
    taskId: item.taskId,
    taskDesc: item.prompt || '',
    taskStatus: item.status,
    imageList: (item.details || []).map(img => ({
      imageId: img.id,
      status: img.status,
      imageUrl: img.url
    }))
  }))
}

onMounted(async () => {
  await getList()
  reapeatgetList()
})
onUnmounted(() => {
  clearTimeout(scrollTimer.value)
  clearInterval(getLisTimer.value)
})
defineExpose({
  openGetImage
})
</script>
<style scoped lang="scss">
.show_box {
  height: 100%;
  padding: 20px;
  font-family: PingFang SC, sans-serif;
  background-color: #fff;
  border-radius: 12px;
  overflow-y: auto;

  //&::-webkit-scrollbar {
  //  display: none; /* 隐藏滚动条 */
  //}

  -ms-overflow-style: none; /* IE 和 Edge 隐藏滚动条 */
  scrollbar-width: none; /* Firefox 隐藏滚动条 */

  .show_item {
    width: 100%;
    margin-bottom: 24px;

    /* =====================
     * 标题栏 Header
     * ===================== */

    .show_item_header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 16px;
    }

    /* 左侧信息 */

    .header-left {
      display: flex;
      flex-direction: column;
      gap: 6px;
      max-width: calc(100% - 80px);
    }

    .title-row {
      display: flex;
      align-items: center;
      gap: 6px;

      img {
        width: 20px;
        height: 20px;
      }

      .title-text {
        font-size: 17px;
        font-weight: 400;
        color: #252632;
        line-height: 1;
      }
    }

    .desc-row {
      display: flex;
      align-items: center;
      gap: 6px;
    }

    .desc-text {
      max-width: 400px;
      font-size: 14px;
      color: #7C7D84;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      line-height: 1.4;
    }

    .copy-icon {
      width: 14px;
      height: 14px;
      cursor: pointer;
      flex-shrink: 0;
    }

    /* 右侧操作区 */

    .header-right {
      display: flex;
      gap: 10px;
      flex-shrink: 0;
      height: 45px;
      align-items: flex-end;

      .action-icon {
        width: 24px;
        height: 24px;
        border-radius: 8px;
        cursor: pointer;
      }
    }


    /* =====================
     * 图片展示区域
     * ===================== */

    .show_item_image_List {
      width: 100%;
    }
  }

  .empty_box {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;

    font-weight: 400;
    font-size: 15px;
    line-height: 150%;
    color: #646363;

  }
}

</style>
