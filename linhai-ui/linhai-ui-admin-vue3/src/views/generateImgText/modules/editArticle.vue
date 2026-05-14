<template>
  <div class="edit_box">
    <input
        ref="coverUploadRef"
        type="file"
        accept="image/png,image/jpeg,image/jpg"
        style="display: none"
        @change="handleCoverFileChange"
    />

    <div class="edit_wang">
      <richEditor
          v-if="activeArticleId"
          v-model:title="currentArticle.title"
          v-model:content="currentArticle.content"
          :is-allow-upload-image="true"
          height="100%"
      />
      <el-empty class="h-100%" v-else description="正在加载文章内容..."/>
    </div>
    <div class="article_list">
      <div class="article_search">
        <div
            class="flex justify-between gap-10px font-[PingFang SC] font-400 text-16px leading-22px mb-20px"
        >
          <div
              class="px-16px py-3px border border-solid border-#165DFF color-#165DFF rounded-3px cursor-pointer"
              @click="oneClickPublish"
          >
            一键发布
          </div>
          <div class="flex gap-10px">
            <div
                @click="closeEditMode"
                class="px-16px py-3px rounded-3px color-#1D2129 bg-#F2F3F5 cursor-pointer"
            >
              返回
            </div>
            <div
                @click="keepEditContent()"
                class="px-16px py-3px rounded-3px color-#FFFFFF bg-#165DFF cursor-pointer"
            >
              保存
            </div>
          </div>
        </div>
        <el-input v-model="searchParam.title" placeholder="搜索" :suffix-icon="Search"/>
      </div>
      <div class="show_list">
        <div
            v-for="item in visibleDataList"
            :key="item.id"
            class="show_item"
            :class="{ activeItem: activeArticleId === item.articleId }"
        >
          <div class="show_header">
            <div class="show_header_platform">
              <div :class="[getPlatformCssClass(item.platform)]" class="w-22px h-22px"></div>
              <div class="show_header_platform_title">{{ getPlatformLabel(item.platform) }}</div>
            </div>
            <div class="show_header_detail">
              <div class="show_header_time">{{
                  formatTime(item.createTime, 'yyyy-MM-dd HH:mm:ss')
                }}
              </div>
              <div class="show_header_operate_list">
                <div>
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
              v-if="item.articleStatus === SUCCESS_STATUS"
              class="show_content"
              @click="editArticle(item)"
          >
            <div class="show_content_title">{{ item.articleTitle }}</div>
            <div class="show_content_cover" @click.stop="changeCoverImage(item)">
              <el-image
                  v-if="item.coverImageUrl"
                  :src="item.coverImageUrl"
                  fit="cover"
                  class="show_content_cover_image"
              />
              <div v-else class="show_content_cover_empty">
                <img src="@/assets/geoImage/add_plus.png" alt="add"/>
                <span>上传封面</span>
                <span>仅限 .jpg / .jpeg / .png</span>
              </div>
              <div
                  v-if="item.coverImageUrl"
                  class="show_content_cover_mask"
                  :class="{
                  show_content_cover_mask_loading:
                    isUploadingCover && uploadingCoverArticleId === item.articleId
                }"
              >
                {{
                  isUploadingCover && uploadingCoverArticleId === item.articleId
                      ? '上传中...'
                      : '更换封面'
                }}
              </div>
            </div>
            <div class="show_content_detail">{{ item.articleBody }}</div>
          </div>
          <div v-else-if="NEED_REFRESH_STATES.includes(item.articleStatus)" class="create_new">
            <img src="@/assets/geoImage/wating.gif" class="w-140px h-114px"/>
          </div>
          <div v-else class="create_fail">
            <img src="@/assets/geoImage/error.png" class="w-35px h-35px"/>
            <span>生成失败</span>
          </div>
        </div>
      </div>
    </div>
  </div>

  <AccountSelectDialog
      ref="accountSelectDialogRef"
      @get-select-account="getSelectAccount"
      @add-account-by-client="addAccountByClient"
  />
  <OpenDeskTopPlusDialog ref="openDeskTopPlusDialogRef"/>
</template>

<script setup>
import {computed, reactive, ref} from 'vue'
import {Search} from '@element-plus/icons-vue'
import Clipboard from 'clipboard'
import {useDictStore} from '@/store/modules/dict'
import {ElMessage, ElMessageBox} from 'element-plus'
import richEditor from '@/components/GeoView/RichTextEditor.vue'
import AccountSelectDialog from '@/components/GeoView/AccountSelectDialog.vue'
import OpenDeskTopPlusDialog from '@/components/GeoView/OpenDeskTopPlusDialog.vue'
import {formatTime} from '@/utils'
import {formatToDateTime} from '@/utils/dateUtil'
import {LongArticleApi, LongArticleStatus} from '@/api/ai/longArticle'
import {createPublishTaskLogs} from '@/api/geo/publishTask'
import {uploadFile} from '@/api/geo/uploadImage'

const NEED_REFRESH_STATES = [LongArticleStatus.PENDING, LongArticleStatus.EXECUTING]
const SUCCESS_STATUS = LongArticleStatus.SUCCESS
const ALLOW_COVER_TYPES = ['image/jpeg', 'image/png', 'image/jpg']
const MAX_COVER_SIZE = 5 * 1024 * 1024

const _emit = defineEmits(['closeEditMode'])
const useDictStoreRef = useDictStore()

const activeArticleId = ref('')
const currentTaskId = ref(null)
const publishPlatform = ref([])
const dataList = ref([])
const coverUploadRef = ref(null)
const accountSelectDialogRef = ref(null)
const openDeskTopPlusDialogRef = ref(null)
const isUploadingCover = ref(false)
const uploadingCoverArticleId = ref('')
const isPublishing = ref(false)

function createEmptyArticle() {
  return {
    articleId: '',
    title: '',
    content: '',
    summary: '',
    coverImageUrl: '',
    platform: '',
    taskId: null
  }
}

const currentArticle = ref(createEmptyArticle())

const searchParam = reactive({
  title: ''
})

const visibleDataList = computed(() => {
  const keyword = searchParam.title.trim().toLowerCase()
  if (!keyword) return dataList.value

  return dataList.value.filter((item) => {
    return `${item.articleTitle} ${item.articleBody}`.toLowerCase().includes(keyword)
  })
})

async function getPlatformList() {
  publishPlatform.value = (await useDictStoreRef.getDictByType('platform_type')) || []
}

function getPlatformCssClass(value) {
  return (
      publishPlatform.value.find((item) => item.value === value || item.label === value)?.cssClass ||
      'icon-xiaohongshu'
  )
}

function getPlatformLabel(value) {
  return (
      publishPlatform.value.find((item) => item.value === value || item.label === value)?.label ||
      value ||
      '未知平台'
  )
}

function normalizeArticleBody(body) {
  if (!body) return ''

  const div = document.createElement('div')
  div.innerHTML = body

  let result = ''
  const blockTags = [
    'P',
    'DIV',
    'H1',
    'H2',
    'H3',
    'H4',
    'H5',
    'H6',
    'LI',
    'UL',
    'OL',
    'BLOCKQUOTE',
    'BR'
  ]

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

function extractImageUrlsFromContent(content) {
  if (!content) return []

  const div = document.createElement('div')
  div.innerHTML = content

  return Array.from(div.querySelectorAll('img'))
      .map((img) => img.getAttribute('src')?.trim())
      .filter((url) => !!url)
}

function buildTaskItems(task) {
  return (task.results || []).map((result) => ({
    imageList: result.imageList || result.imgUrls || result.images || [],
    id: result.id,
    taskId: task.id,
    platform: task.platform,
    articleId: result.id,
    articleStatus: result.status,
    articleTitle: result.title,
    articleBody: normalizeArticleBody(result.body || result.finalArticle || ''),
    articleBodyRaw: result.body || result.finalArticle || '',
    summary: result.summary || '',
    coverImageUrl:
        result.coverImageUrl ||
        result.imageList?.[0] ||
        result.imgUrls?.[0] ||
        result.images?.[0] ||
        '',
    createTime: result.createTime || task.finishTime || task.startTime || task.createTime
  }))
}

function syncCurrentArticleToList() {
  const currentItem = dataList.value.find(
      (item) => item.articleId === currentArticle.value.articleId
  )
  if (!currentItem) return

  currentItem.articleTitle = currentArticle.value.title
  currentItem.articleBodyRaw = currentArticle.value.content
  currentItem.articleBody = normalizeArticleBody(currentArticle.value.content)
  currentItem.summary = currentArticle.value.summary
  currentItem.coverImageUrl = currentArticle.value.coverImageUrl
  currentItem.imageList = currentArticle.value.imageList || []
  currentItem.platform = currentArticle.value.platform || currentItem.platform
}

function setCurrentArticle(articleId) {
  const targetItem =
      dataList.value.find((item) => item.articleId === articleId) || dataList.value[0]
  if (!targetItem) {
    activeArticleId.value = ''
    currentArticle.value = createEmptyArticle()
    return
  }

  activeArticleId.value = targetItem.articleId
  currentArticle.value = {
    articleId: targetItem.articleId,
    title: targetItem.articleTitle || '',
    content: targetItem.articleBodyRaw || targetItem.articleBody || '',
    summary: targetItem.summary || '',
    coverImageUrl: targetItem.coverImageUrl || '',
    imageList: targetItem.imageList || [],
    platform: targetItem.platform || '',
    taskId: targetItem.taskId
  }
}

function buildPublishArticlePayload() {
  const currentItem = dataList.value.find(
      (item) => item.articleId === currentArticle.value.articleId
  )

  return {
    ...currentArticle.value,
    id: currentArticle.value.articleId,
    body: currentArticle.value.content,
    content: currentArticle.value.content,
    title: currentArticle.value.title,
    summary: currentArticle.value.summary || '',
    coverImageUrl: currentArticle.value.coverImageUrl || '',
    platform: currentItem?.platform || currentArticle.value.platform || ''
  }
}

function applyCoverImage(articleId, url) {
  const targetItem = dataList.value.find((item) => item.articleId === articleId)
  if (targetItem) {
    targetItem.coverImageUrl = url
  }

  if (currentArticle.value.articleId === articleId) {
    currentArticle.value.coverImageUrl = url
  }
}

async function loadTaskResults(taskId, defaultSelectId = null) {
  try {
    const res = await LongArticleApi.getTaskDetail(taskId)
    currentTaskId.value = res.id
    dataList.value = buildTaskItems(res)

    if (!dataList.value.length) {
      ElMessage.warning('当前任务下的文章已经全部删除')
      _emit('closeEditMode')
      return
    }

    setCurrentArticle(defaultSelectId)
  } catch (e) {
    console.log(e)
    _emit('closeEditMode')
  }
}

async function openEditMode(initData) {
  if (!initData?.taskId) return

  searchParam.title = ''
  await getPlatformList()
  await loadTaskResults(initData.taskId, initData.articleId)
}

async function closeEditMode() {
  try {
    if (!currentArticle.value.articleId) {
      _emit('closeEditMode')
      return
    }

    const isSaved = await keepEditContent()
    if (isSaved) {
      _emit('closeEditMode')
    }
  } catch (e) {
    console.log(e)
  }
}

async function keepEditContent(options = {}) {
  const {silent = false} = options

  try {
    if (!currentArticle.value.articleId) return true

    currentArticle.value.imageList = extractImageUrlsFromContent(currentArticle.value.content)

    const params = {
      id: currentArticle.value.articleId,
      title: currentArticle.value.title,
      body: currentArticle.value.content,
      summary: currentArticle.value.summary,
      coverImageUrl: currentArticle.value.coverImageUrl,
      imgUrls: currentArticle.value.imageList
    }
    const res = await LongArticleApi.updateResult(params)
    if (res) {
      syncCurrentArticleToList()
      if (!silent) {
        ElMessage.closeAll()
        ElMessage.success('保存成功')
      }
      return true
    }
    return false
  } catch (e) {
    console.log(e)
    if (!silent) {
      ElMessage.error('保存失败')
    }
    return false
  }
}

async function deleteItem(id) {
  if (!id) return

  try {
    await ElMessageBox.confirm('确定要删除该项吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await LongArticleApi.deleteResult(id)
    if (res) {
      ElMessage.success('删除成功')
    }

    if (currentTaskId.value) {
      await loadTaskResults(currentTaskId.value)
    }

    if (!dataList.value.length) {
      _emit('closeEditMode')
    }
  } catch {
  }
}

async function editArticle(item) {
  if (activeArticleId.value === item.articleId) return

  const isSaved = await keepEditContent({silent: true})
  if (isSaved) {
    setCurrentArticle(item.articleId)
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

async function changeCoverImage(item) {
  if (isUploadingCover.value || !item?.articleId) return

  if (item.articleId !== activeArticleId.value) {
    const isSaved = await keepEditContent({silent: true})
    if (!isSaved) return
    setCurrentArticle(item.articleId)
  }

  uploadingCoverArticleId.value = item.articleId
  if (coverUploadRef.value) {
    coverUploadRef.value.value = ''
    coverUploadRef.value.click()
  }
}

async function handleCoverFileChange(event) {
  const file = event.target.files?.[0]
  event.target.value = ''

  if (!file) return
  if (!uploadingCoverArticleId.value) return

  if (!ALLOW_COVER_TYPES.includes(file.type)) {
    ElMessage.error('仅支持 JPG / PNG / JPEG 格式图片')
    return
  }

  if (file.size > MAX_COVER_SIZE) {
    ElMessage.error('图片大小不能超过 5MB')
    return
  }

  try {
    isUploadingCover.value = true
    ElMessage.info('正在上传封面...')

    const formData = new FormData()
    formData.append('file', file)
    const url = await uploadFile(formData)

    if (!url) {
      throw new Error('上传失败')
    }

    applyCoverImage(uploadingCoverArticleId.value, url)
    ElMessage.closeAll()
    ElMessage.success('封面上传成功')
  } catch (e) {
    console.log(e)
    ElMessage.closeAll()
    ElMessage.error('封面上传失败')
  } finally {
    isUploadingCover.value = false
    uploadingCoverArticleId.value = ''
  }
}

async function oneClickPublish() {
  if (isPublishing.value) {
    ElMessage.warning('发布中，请勿重复点击')
    return
  }

  if (!currentArticle.value.coverImageUrl?.trim()) {
    return ElMessage.error('请先上传文章封面')
  }

  const isSaved = await keepEditContent({silent: true})
  if (!isSaved) {
    ElMessage.error('文章保存失败')
    return
  }

  accountSelectDialogRef.value?.openDialog([], true)
}

function addAccountByClient() {
  openDeskTopPlusDialogRef.value?.openDialog()
}

async function getSelectAccount(accountLists) {
  try {
    isPublishing.value = true

    const bodyParams = {
      accountIds: accountLists.map((item) => item.id),
      articleSaveReqVOList: [buildPublishArticlePayload()],
      publishTime: formatToDateTime(new Date()),
      publishTimeInterval: 300
    }
    const res = await createPublishTaskLogs(bodyParams)
    if (res) {
      ElMessage.success('发布任务已提交')
      _emit('closeEditMode')
    }
  } catch (e) {
    console.log(e)
    const errorMessage = typeof e === 'string' ? e : e?.message || e?.msg || e?.data?.message || ''

    if (errorMessage.includes('客户端离线')) {
      addAccountByClient()
      return
    }

    ElMessage.error('发布任务提交失败')
  } finally {
    isPublishing.value = false
  }
}

defineExpose({
  openEditMode
})
</script>

<style scoped lang="scss">
.edit_box {
  display: flex;
  width: 100%;
  height: 100%;
  flex-direction: row;
  gap: 20px;
  background-color: #f7f8fa;
}

.edit_wang {
  height: 100%;
  width: calc(100% * (1241 / (1241 + 393)));
  border-radius: 6px;
  background-color: #fff;
  overflow: auto;

  &::-webkit-scrollbar {
    width: 0;
  }
}

.article_list {
  height: 100%;
  width: calc(100% * (393 / (1241 + 393)));
  border-radius: 6px;
  background-color: #fff;
  padding: 0 20px 20px;
  overflow: auto;

  &::-webkit-scrollbar {
    width: 0;
  }

  .article_search {
    padding-top: 20px;
    position: sticky;
    top: 0;
    background-color: #fff;
    z-index: 2;

    :deep(.el-input__wrapper) {
      height: 40px;
      border-radius: 3px;
      padding: 5px 12px;
      background-color: #f2f3f5;
      box-shadow: none;
      font-family: PingFang SC,
      sans-serif;
      font-weight: 400;
      font-size: 14px;
      line-height: 22px;
      vertical-align: middle;
      color: #86909c;
    }
  }

  .show_list {
    padding-top: 20px;
    display: flex;
    flex-direction: column;
    gap: 20px 18px;
    width: 100%;

    .show_item {
      width: 100%;
      min-height: 340px;
      border-radius: 6px;
      border: 1px solid #f7f8fa;
      padding: 20px;
      overflow: hidden;
      background-color: #f7f8fa;

      &:hover {
        .show_header .show_header_detail {
          display: flex;
        }
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
            font-family: PingFang SC,
            sans-serif;
            font-weight: 400;
            font-size: 12px;
            line-height: 100%;
            letter-spacing: 0;
            color: #252632;
          }
        }

        .show_header_detail {
          display: none;
          gap: 5px;

          .show_header_time {
            font-family: PingFang SC,
            sans-serif;
            font-weight: 400;
            font-size: 12px;
            line-height: 22px;
            color: #5e5e5e;
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
        gap: 12px;
        flex-direction: column;
        overflow: hidden;
        cursor: pointer;

        .show_content_title {
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
          font-family: PingFang SC,
          sans-serif;
          font-weight: 600;
          font-size: 16px;
          line-height: 22px;
          letter-spacing: 0;
          color: #252632;
        }

        .show_content_cover {
          width: 100%;
          height: 128px;
          border-radius: 8px;
          position: relative;
          overflow: hidden;
          background-color: #fff;
          border: 1px dashed #d9dde6;
          cursor: pointer;

          .show_content_cover_image {
            width: 100%;
            height: 100%;
            display: block;
          }

          .show_content_cover_empty {
            width: 100%;
            height: 100%;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            gap: 6px;
            color: #86909c;

            img {
              width: 20px;
              height: 20px;
            }

            span:nth-child(2) {
              font-family: PingFang SC,
              sans-serif;
              font-weight: 500;
              font-size: 14px;
              line-height: 22px;
              color: #1d3444;
            }

            span:nth-child(3) {
              font-family: PingFang SC,
              sans-serif;
              font-weight: 400;
              font-size: 12px;
              line-height: 20px;
              color: #959daa;
            }
          }

          .show_content_cover_mask {
            position: absolute;
            inset: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: PingFang SC,
            sans-serif;
            font-weight: 500;
            font-size: 14px;
            line-height: 22px;
            color: #fff;
            background: rgb(0 0 0 / 35%);
            opacity: 0;
            transition: opacity 0.2s ease;
          }

          .show_content_cover_mask_loading {
            opacity: 1;
          }

          &:hover {
            .show_content_cover_mask {
              opacity: 1;
            }
          }
        }

        .show_content_detail {
          white-space: pre-wrap;
          display: -webkit-box;
          -webkit-line-clamp: 6;
          -webkit-box-orient: vertical;
          overflow: hidden;
          text-overflow: ellipsis;
          font-family: PingFang SC,
          sans-serif;
          font-weight: 400;
          font-size: 14px;
          line-height: 22px;
          color: #252632;
          min-height: 132px;
        }
      }

      .create_new {
        height: calc(100% - 24px - 20px - 20px * 2);
        margin-top: 20px;
        display: flex;
        justify-content: center;
        align-items: center;
      }

      .create_fail {
        height: calc(100% - 24px - 20px - 20px * 2);
        margin-top: 20px;
        display: flex;
        justify-content: center;
        align-items: center;
        flex-direction: column;
        gap: 20px;
        font-family: PingFang SC,
        sans-serif;
        font-weight: 400;
        font-size: 17px;
        line-height: 22px;
        letter-spacing: 0;
        color: #252632;
      }
    }

    .activeItem {
      border: 1px solid #165dff;
    }
  }
}
</style>
