<template>
  <div class="container_article">
    <!-- 一级页面：文件夹列表 -->
    <template v-if="viewMode === 'folder'">
      <header>
        <el-input
            v-model="searchVo.folderName"
            style="width: 215px"
            placeholder="搜索"
            clearable
            @keydown.enter="initDataList"
        >
          <template #suffix>
            <el-icon class="cursor-pointer" @click="initDataList">
              <Search/>
            </el-icon>
          </template>
        </el-input>
      </header>

      <main v-loading="loading">
        <div class="folder_box">
          <!-- 创建文件夹 -->
          <div class="create_folder_card" @click="handleCreateFolder">
            <div class="create_icon_box">
              <el-icon size="18">
                <Plus/>
              </el-icon>
            </div>
            <div class="create_text">创建图片文件夹</div>
          </div>

          <!-- 文件夹列表 -->
          <div
              v-for="item in showList"
              :key="item.id"
              class="folder_card"
              @click="handleFolderClick(item)"
          >
            <div class="folder_cover">
              <div class="img_stack" v-if="item.imageCount">
                <div class="stack_img stack_img_3" v-if="item.coverImages && item.coverImages[2]">
                  <img :src="item.coverImages[2].url" :alt="item.coverImages[2].name"/>
                </div>
                <div class="stack_img stack_img_2" v-if="item.coverImages && item.coverImages[1]">
                  <img :src="item.coverImages[1].url" :alt="item.coverImages[1].name"/>
                </div>
                <div class="stack_img stack_img_1" v-if="item.coverImages && item.coverImages[0]">
                  <img :src="item.coverImages[0].url" :alt="item.coverImages[0].name"/>
                </div>
              </div>

              <div v-else class="empty_cover">
                暂无图片
              </div>

              <div class="folder_mask">
                {{ item.imageCount || 0 }}张图片
              </div>
            </div>

            <div class="folder_info">
              <div class="folder_name" :title="item.name">
                {{ item.name }}
              </div>

              <el-dropdown trigger="click" @command="(command) => handleCommand(command, item)">
                <div class="more_btn" @click.stop>
                  <el-icon>
                    <MoreFilled/>
                  </el-icon>
                </div>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="rename">编辑</el-dropdown-item>
                    <el-dropdown-item command="delete">删除</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </div>

        <el-empty v-if="!showList.length" description="暂无数据"/>
      </main>
    </template>

    <!-- 二级页面：图片列表 -->
    <template v-else-if="viewMode === 'image'">
      <div class="image_list_view">
        <header class="page_header">
          <div class="header_left" @click="handleBackToFolder">
            <el-icon>
              <ArrowLeft/>
            </el-icon>
            <span class="back_text">返回</span>
            <span class="folder_title" :title="currentFolder?.name">
              {{ currentFolder?.name || '--' }}
            </span>
          </div>

          <el-input
              v-model="imageSearchVo.imgName"
              class="search_input"
              placeholder="搜索"
              clearable
              @keyup.enter="loadImageList"
          >
            <template #suffix>
              <el-icon>
                <Search/>
              </el-icon>
            </template>
          </el-input>
        </header>

        <main class="page_main" v-loading="imageLoading">
          <div class="image_grid">
            <!-- 上传图片 -->
            <div class="upload_card" @click="handleUploadClick">
              <input
                  ref="uploadInputRef"
                  type="file"
                  accept=".jpg,.jpeg,.png"
                  multiple
                  style="display: none"
                  @change="handleFileChange"
              />
              <div class="upload_icon_box">
                <el-icon size="18">
                  <Plus/>
                </el-icon>
              </div>
              <div class="upload_text">上传本地图片</div>
              <div class="upload_tip">图片大小4.5MB内</div>
              <div class="upload_tip">单次上传不超过10张</div>
            </div>

            <!-- 图片列表 -->
            <div
                v-for="item in imageShowList"
                :key="item.id"
                class="image_card"
            >
              <div class="image_cover">
                <el-image
                    :src="item.url"
                    :alt="item.name"
                    fit="cover"
                    :preview-src-list="[item.url]"
                    :initial-index="0"
                />
              </div>

              <div class="image_info">
                <div class="image_name" :title="item.name">
                  {{ item.name }}
                </div>

                <el-dropdown
                    trigger="click"
                    append-to=".image_info"
                    popper-class="image_info_dropdown"
                    @command="(command: string) => handleImageCommand(command, item)"
                >
                  <div class="more_btn" @click.stop>
                    <el-icon>
                      <MoreFilled/>
                    </el-icon>
                  </div>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="download">下载</el-dropdown-item>
                      <el-dropdown-item command="move">移动</el-dropdown-item>
                      <el-dropdown-item command="delete">删除</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
          </div>

          <!-- 分页 -->
          <div class="pagination_wrapper" v-if="imageTotal > 0">
            <el-pagination
                v-model:current-page="imagePageNo"
                v-model:page-size="imagePageSize"
                :total="imageTotal"
                :page-sizes="[20, 40, 60, 80]"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="loadImageList"
                @current-change="loadImageList"
            />
          </div>

          <el-empty v-if="!imageShowList.length && !imageLoading" description="暂无图片"/>
        </main>
      </div>
    </template>

    <FolderEditDialog ref="folderEditDialogRef" @success="handleFolderSuccess"/>
    <MoveImageDialog ref="moveImageDialogRef" @success="loadImageList"/>
  </div>
</template>

<script setup lang="ts">
import {computed, reactive, ref, onMounted} from 'vue'
import {Search, Plus, MoreFilled, ArrowLeft} from '@element-plus/icons-vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import FolderEditDialog from './modules/FolderEditDialog.vue'
import MoveImageDialog from './modules/MoveImageDialog.vue'
import * as MaterialApi from '@/api/geo/Material'
import type {MaterialFolderTree, MaterialImage} from '@/api/geo/Material'

// 页面模式：folder-文件夹列表(一级) | image-图片列表(二级)
type ViewMode = 'folder' | 'image'
const viewMode = ref<ViewMode>('folder')

const folderEditDialogRef = ref()
const moveImageDialogRef = ref()
const uploadInputRef = ref<HTMLInputElement>()

const list = ref<MaterialFolderTree[]>([])
const currentFolder = ref<MaterialFolderTree | null>(null)

// 页面加载时获取数据
onMounted(() => {
  initDataList()
})

// ========== 一级页面：文件夹列表 ==========
const searchVo = reactive({
  pageNo: 1,
  pageSize: 10,
  folderName: ''
})

const showList = computed(() => {
  const keyword = searchVo.folderName.trim()
  if (!keyword) return list.value
  return list.value.filter(item => item.name.includes(keyword))
})


const loading = ref(false)

async function initDataList() {
  try {
    if (loading.value) return
    loading.value = true
    const res = await MaterialApi.getFolderTree()
    const folders = res || []
    for (const folder of folders) {
      try {
        const imageRes = await MaterialApi.getImagePage({
          pageNo: 1,
          pageSize: 3,
          folderId: folder.id
        })
        if (imageRes?.list) {
          folder.imageCount = imageRes.total
          folder.coverImages = imageRes.list.slice(0, 3)
        }
      } catch (e) {
        console.error('获取封面失败', e)
      }
    }
    list.value = folders
  } catch (e) {
    ElMessage.error('获取数据异常请稍后重试！')
    console.log(e)
  } finally {
    loading.value = false
  }
}

function handleCreateFolder() {
  folderEditDialogRef.value?.openDialog()
}

function handleFolderClick(item: MaterialFolderTree) {
  currentFolder.value = item
  viewMode.value = 'image'
  // 重置图片列表搜索条件
  imageSearchVo.imgName = ''
  imagePageNo.value = 1
  loadImageList()
}

function handleCommand(command: string, item: MaterialFolderTree) {
  if (command === 'rename') {
    folderEditDialogRef.value?.openDialog(item)
  }
  if (command === 'delete') {
    handleDeleteFolder(item)
  }
}

async function handleDeleteFolder(item: MaterialFolderTree) {
  try {
    await ElMessageBox.confirm(
        `确认删除文件夹"${item.name}"吗？`,
        '提示',
        {type: 'warning'}
    )
    await MaterialApi.deleteFolder(item.id)
    ElMessage.success('删除成功')
    await initDataList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.log(error)
    }
  }
}

function handleFolderSuccess() {
  initDataList()
}

// 返回一级页面
function handleBackToFolder() {
  viewMode.value = 'folder'
  currentFolder.value = null
  initDataList()
}

// ========== 二级页面：图片列表 ==========
const imageSearchVo = reactive({
  imgName: ''
})

const imageList = ref<MaterialImage[]>([])
const imageLoading = ref(false)
const imageTotal = ref(0)
const imagePageNo = ref(1)
const imagePageSize = ref(20)

const imageShowList = computed(() => {
  const imgList = imageList.value
  const keyword = imageSearchVo.imgName.trim()
  if (!keyword) return imgList
  return imgList.filter(item => item.name.includes(keyword))
})

async function loadImageList() {
  if (!currentFolder.value) return

  imageLoading.value = true
  try {
    const res = await MaterialApi.getImagePage({
      pageNo: imagePageNo.value,
      pageSize: imagePageSize.value,
      folderId: currentFolder.value.id,
      name: imageSearchVo.imgName || undefined
    })
    imageList.value = res?.list || []
    imageTotal.value = res?.total || 0
  } catch (e) {
    console.error('加载图片列表失败', e)
    ElMessage.error('加载图片列表失败')
  } finally {
    imageLoading.value = false
  }
}

// 上传相关
const ALLOWED_TYPES = ['image/jpeg', 'image/png']
const MAX_SIZE = 5 * 1024 * 1024
const MIN_WIDTH = 160
const MIN_HEIGHT = 160
const MAX_COUNT = 10

function handleUploadClick() {
  uploadInputRef.value?.click()
}

async function getImageSize(file: File): Promise<{ width: number; height: number }> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = (e) => {
      const img = new Image()
      img.onload = () => {
        resolve({width: img.width, height: img.height})
      }
      img.onerror = () => reject(new Error('图片加载失败'))
      img.src = e.target?.result as string
    }
    reader.onerror = () => reject(new Error('文件读取失败'))
    reader.readAsDataURL(file)
  })
}

async function handleFileChange(event: Event) {
  const target = event.target
  if (!(target instanceof HTMLInputElement)) return

  const files = target.files
  if (!files || files.length === 0) return

  if (files.length > MAX_COUNT) {
    ElMessage.error(`单次最多上传 ${MAX_COUNT} 张图片`)
    target.value = ''
    return
  }

  try {
    for (const file of Array.from(files)) {
      if (!ALLOWED_TYPES.includes(file.type)) {
        ElMessage.error(`图片 "${file.name}" 格式不支持，仅支持 JPG/JPEG/PNG`)
        continue
      }

      if (file.size > MAX_SIZE) {
        ElMessage.error(`图片 "${file.name}" 大小不能超过 5MB`)
        continue
      }

      try {
        const {width, height} = await getImageSize(file)
        if (width < MIN_WIDTH || height < MIN_HEIGHT) {
          ElMessage.error(`图片 "${file.name}" 分辨率不能低于 ${MIN_WIDTH}×${MIN_HEIGHT}`)
          continue
        }
      } catch (err) {
        console.error('读取图片失败', err)
        ElMessage.error(`无法读取图片 "${file.name}"`)
        continue
      }

      try {
        await uploadSingleImage(file)
      } catch (err) {
        console.error('上传失败', err)
        ElMessage.error(`图片 "${file.name}" 上传失败`)
      }
    }
  } finally {
    target.value = ''
  }
}

async function uploadSingleImage(file: File) {
  const formData = new FormData()
  formData.append('folderId', String(currentFolder.value!.id))
  formData.append('file', file)

  try {
    await MaterialApi.uploadImage(formData)
    ElMessage.success(`${file.name} 上传成功`)
    loadImageList()
  } catch (e) {
    console.error('上传图片失败', e)
    ElMessage.error(`${file.name} 上传失败`)
  }
}

// 图片操作
async function handleDownload(item: MaterialImage) {
  try {
    const response = await fetch(item.url)
    const blob = await response.blob()
    const blobUrl = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = blobUrl
    link.download = item.name || 'image'
    link.style.display = 'none'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(blobUrl)
  } catch (error) {
    console.error('下载失败:', error)
  }
}

async function handleDeleteImage(item: MaterialImage) {
  try {
    await ElMessageBox.confirm(
        `确认删除图片"${item.name}"吗？`,
        '提示',
        {type: 'warning'}
    )
    await MaterialApi.deleteImage(item.id)
    ElMessage.success('删除成功')
    loadImageList()
  } catch (e: any) {
    if (e !== 'cancel') {
      console.error('删除图片失败', e)
      ElMessage.error(e?.msg || '删除失败')
    }
  }
}

async function handleMoveImage(item: MaterialImage) {
  const availableFolders = list.value.filter(
      folder => folder.id !== currentFolder.value?.id
  )
  if (availableFolders.length === 0) {
    ElMessage.warning('没有其他可用的文件夹')
    return
  }
  moveImageDialogRef.value?.openDialog(item, availableFolders)
}

function handleImageCommand(command: string, item: MaterialImage) {
  if (command === 'download') {
    handleDownload(item)
  }
  if (command === 'move') {
    handleMoveImage(item)
  }
  if (command === 'delete') {
    handleDeleteImage(item)
  }
}
</script>

<style scoped lang="scss">
.container_article {
  height: calc(100vh - 40px - 50px);
  padding: 20px;
  background-color: #fff;
  box-sizing: border-box;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

// ========== 一级页面样式 ==========
header {
  height: 72px;
  flex-shrink: 0;

  ::v-deep(.el-input__wrapper) {
    background-color: #F2F3F5;
    box-shadow: none;
  }
}

main {
  flex: 1;
  overflow-y: auto;
}

.folder_box {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(128px, 1fr));
  gap: 18px 14px;
  align-items: start;
}

.create_folder_card {
  aspect-ratio: 1;
  border-radius: 14px;
  background: #f5f6fa;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.create_folder_card:hover {
  background: #eef2ff;
}

.create_icon_box {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #2f68ff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.create_text {
  margin-top: 12px;
  font-size: 14px;
  color: #666;
}

.folder_card {
  cursor: pointer;
}

.folder_cover {
  position: relative;
  aspect-ratio: 1;
  border-radius: 14px;
  overflow: hidden;
  background: linear-gradient(180deg, #dfe7ff 0%, #b9ccff 100%);
}

.img_stack {
  position: relative;
  width: 100%;
  height: 100%;
}

.stack_img {
  position: absolute;
  border-radius: 10px;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 6px 14px rgba(0, 0, 0, 0.12);
}

.stack_img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.stack_img_3 {
  width: 58px;
  height: 78px;
  top: 18px;
  left: 24px;
  z-index: 1;
  transform: rotate(-8deg);
  opacity: 0.8;
}

.stack_img_2 {
  width: 62px;
  height: 82px;
  top: 12px;
  left: 34px;
  z-index: 2;
  transform: rotate(-3deg);
}

.stack_img_1 {
  width: 66px;
  height: 86px;
  top: 8px;
  left: 44px;
  z-index: 3;
  transform: rotate(4deg);
}

.folder_mask {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 10;
  opacity: 0.8;
  height: 75px;
  background: url('@/assets/geoImage/bgi_image.png') no-repeat;
  background-size: cover;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding-bottom: 10px;
  box-sizing: border-box;
  color: #fff;
  font-size: 14px;
}

.folder_info {
  margin-top: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 6px;
}

.folder_name {
  flex: 1;
  min-width: 0;
  font-size: 12px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.more_btn {
  width: 24px;
  height: 24px;
  flex-shrink: 0;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}

.more_btn:hover {
  background: #f2f3f5;
}

.empty_cover {
  width: 100%;
  height: 100%;
  color: #999;
  font-size: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
}

// ========== 二级页面样式 ==========
.image_list_view {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin: -20px;
  padding: 0;
  background: #fff;
}

.page_header {
  height: 60px;
  padding: 0 16px;
  border-bottom: 1px solid #e5e6eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-sizing: border-box;
  flex-shrink: 0;
}

.header_left {
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #333;
}

.back_text {
  color: #666;
  flex-shrink: 0;
}

.folder_title {
  max-width: 260px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.search_input {
  width: 215px;
  flex-shrink: 0;
}

:deep(.search_input .el-input__wrapper) {
  background-color: #f2f3f5;
  box-shadow: none;
}

.page_main {
  position: relative;
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  box-sizing: border-box;
}

.image_grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(136px, 1fr));
  gap: 18px 14px;
  align-items: start;
}

.upload_card {
  height: 136px;
  aspect-ratio: 1;
  border-radius: 10px;
  background: #f5f6fa;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.upload_card:hover {
  background: #eef2ff;
}

.upload_icon_box {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #2f68ff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload_text {
  margin-top: 12px;
  font-size: 14px;
  color: #666;
}

.upload_tip {
  margin-top: 2px;
  font-size: 12px;
  color: #999;
  line-height: 1.4;
}

.image_card {
  cursor: pointer;
}

.image_cover {
  height: 136px;
  aspect-ratio: 1;
  border-radius: 10px;
  overflow: hidden;
  background: #f2f3f5;

  :deep(.el-image) {
    width: 100%;
    height: 100%;
  }

  :deep(.el-image__wrapper) {
    width: 100%;
    height: 100%;
  }
}

.image_info {
  margin-top: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 6px;
}

.image_name {
  flex: 1;
  min-width: 0;
  font-size: 12px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pagination_wrapper {
  position: absolute;
  bottom: 20px;
  left: 16px;
  right: 16px;
  display: flex;
  justify-content: flex-end;
}

// ========== 通用样式 ==========
:deep(.el-dropdown-menu__item) {
  width: 90px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: PingFang SC, sans-serif;
  font-weight: 400;
  font-size: 15px;
  line-height: 100%;
  letter-spacing: 0;
}

:deep(.el-popper__arrow) {
  display: none;
}

.el-dropdown-menu {
  padding: 0;
}
</style>
