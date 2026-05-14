<script setup lang="ts">
import {computed, ref, watch} from 'vue'
import {Search, Plus, MoreFilled} from '@element-plus/icons-vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import * as MaterialApi from '@/api/geo/Material'
import type {MaterialFolderTree, MaterialImage} from '@/api/geo/Material'
import FolderEditDialog from './FolderEditDialog.vue'

defineOptions({name: 'MaterialSelectDialog'})

// 配置接口
export interface MaterialSelectConfig {
  mode?: 'single' | 'multiple' // 默认单选
  // onConfirm?: (images: MaterialImage | MaterialImage[]) => void
}
const _emit = defineEmits(['confirm'])

const dialogVisible = ref(false)

// 组件引用
const folderEditDialogRef = ref()

// 数据
const folderTree = ref<MaterialFolderTree[]>([])
const folderLoading = ref(false)

const imageList = ref<MaterialImage[]>([])
const imageLoading = ref(false)
const imageTotal = ref(0)
const imagePageNo = ref(1)
const imagePageSize = ref(20)

// 当前配置
const currentConfig = ref<MaterialSelectConfig>({
  mode: 'single'
})

// 搜索
const searchName = ref('')

// 当前选中的文件夹
const selectedFolderId = ref<number | null>(null)

// 当前选中的图片
const selectedImages = ref<MaterialImage[]>([])

// 打开弹窗
function openDialog(config: MaterialSelectConfig = {}) {
  currentConfig.value = {
    mode: 'single',
    ...config
  }
  // 重置状态
  selectedImages.value = []
  searchName.value = ''
  imagePageNo.value = 1
  dialogVisible.value = true
  // 加载数据
  loadFolderTree()
}

// 初始化加载文件夹树
async function loadFolderTree() {
  folderLoading.value = true
  try {
    const res = await MaterialApi.getFolderTree()
    folderTree.value = res || []
    // 默认选中第一个文件夹
    if (folderTree.value.length > 0) {
      selectFolder(folderTree.value[0].id)
    }
  } catch (e) {
    console.error('加载文件夹树失败', e)
    ElMessage.error('加载文件夹树失败')
  } finally {
    folderLoading.value = false
  }
}

// 选择文件夹
function selectFolder(folderId: number) {
  selectedFolderId.value = folderId
  loadImageList()
}

// 加载图片列表
async function loadImageList() {
  if (!selectedFolderId.value) {
    imageList.value = []
    imageTotal.value = 0
    return
  }

  imageLoading.value = true
  try {
    const res = await MaterialApi.getImagePage({
      pageNo: imagePageNo.value,
      pageSize: imagePageSize.value,
      folderId: selectedFolderId.value,
      name: searchName.value || undefined
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

// 搜索图片
function handleSearch() {
  imagePageNo.value = 1
  loadImageList()
}

// 选择图片
function handleSelectImage(image: MaterialImage) {
  if (currentConfig.value.mode === 'multiple') {
    // 多选模式
    const index = selectedImages.value.findIndex(img => img.id === image.id)
    if (index > -1) {
      selectedImages.value.splice(index, 1)
    } else {
      selectedImages.value.push(image)
    }
  } else {
    // 单选模式
    selectedImages.value = [image]
  }
}

// 检查图片是否被选中
function isSelected(image: MaterialImage) {
  return selectedImages.value.some(img => img.id === image.id)
}

// 上传图片
const uploadInputRef = ref<HTMLInputElement>()

function handleUploadClick() {
  if (!selectedFolderId.value) {
    ElMessage.warning('请先选择一个文件夹')
    return
  }
  uploadInputRef.value?.click()
}

const ALLOWED_TYPES = ['image/jpeg', 'image/png']
const MAX_SIZE = 5 * 1024 * 1024
const MIN_WIDTH = 160
const MIN_HEIGHT = 160
const MAX_COUNT = 10

async function getImageSize(file: File): Promise<{ width: number; height: number }> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()

    reader.onload = (e) => {
      const img = new Image()

      img.onload = () => {
        resolve({
          width: img.width,
          height: img.height
        })
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
        const { width, height } = await getImageSize(file)

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
  if (!selectedFolderId.value) return

  const formData = new FormData()
  formData.append('folderId', String(selectedFolderId.value))
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

// 新建文件夹
function handleCreateFolder() {
  folderEditDialogRef.value?.openDialog()
}

// 文件夹创建/编辑成功
async function handleFolderSuccess() {
  await loadFolderTree()
}

// 关闭弹窗
function handleClose() {
  dialogVisible.value = false
}

// 确认选择
function handleConfirm() {
  if (selectedImages.value.length === 0) {
    ElMessage.warning('请选择图片')
    return
  }
  console.log( selectedImages.value)
  _emit('confirm', selectedImages.value)

  // if (currentConfig.value.mode === 'multiple') {
  //   _emit('confirm', selectedImages.value)
  // } else {
  //   _emit('confirm', selectedImages.value[0])
  // }
  handleClose()
}

function handleCommand(command: string, item: MaterialImage) {
  if (command === 'download') {
    handleDownload(item)
  }
  if (command === 'delete') {
    handleDelete(item)
  }
}

async function handleDelete(item: MaterialImage) {
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


// 暴露方法
defineExpose({
  openDialog
})
</script>

<template>
  <el-dialog
      v-model="dialogVisible"
      :show-close="false"
      :destroy-on-close="true"
      width="960px"
      class="custom_dialog material_select_dialog"
  >
    <template #header>
      <div class="dialog_header">
        <div class="header_title">我的素材</div>
        <img
            src="@/assets/geoImage/close.png"
            class="close_icon"
            @click="handleClose"
        />
      </div>
    </template>

    <div class="dialog_content">
      <!-- 左侧：文件夹选择 -->
      <aside class="folder_panel">
        <div class="panel_header">
          <div class="panel_title">选择文件夹</div>
          <div class="create_folder_btn" @click="handleCreateFolder">
            <el-icon size="14">
              <Plus/>
            </el-icon>
            <span>新建</span>
          </div>
        </div>

        <div class="folder_tree" v-loading="folderLoading">
          <div
              v-for="level1 in folderTree"
              :key="level1.id"
              class="folder_item_wrapper"
          >
            <!-- 一级文件夹 -->
            <div
                class="folder_item"
                :class="{ 'is-active': selectedFolderId === level1.id }"
                @click="selectFolder(level1.id)"
            >
              <el-icon size="16" class="folder_icon">
                <component :is="selectedFolderId === level1.id ? 'FolderOpened' : 'Folder'"/>
              </el-icon>
              <span class="folder_name">{{ level1.name }}</span>
            </div>

            <!-- 二级文件夹 -->
            <div
                v-for="level2 in level1.children || []"
                :key="level2.id"
                class="folder_item level2"
                :class="{ 'is-active': selectedFolderId === level2.id }"
                @click="selectFolder(level2.id)"
            >
              <el-icon size="16" class="folder_icon">
                <component :is="selectedFolderId === level2.id ? 'FolderOpened' : 'Folder'"/>
              </el-icon>
              <span class="folder_name">{{ level2.name }}</span>
            </div>
          </div>

          <el-empty v-if="!folderTree.length && !folderLoading" description="暂无文件夹" :image-size="60"/>
        </div>
      </aside>

      <!-- 右侧：图片列表 -->
      <main class="image_panel">
        <!-- 工具栏 -->
        <div class="toolbar">
          <el-input
              v-model="searchName"
              class="search_input"
              placeholder="搜索"
              clearable
              @keyup.enter="handleSearch"
          >
            <template #suffix>
              <el-icon class="cursor-pointer" @click="handleSearch">
                <Search/>
              </el-icon>
            </template>
          </el-input>

          <div class="upload_btn" @click="handleUploadClick">
            <input
                ref="uploadInputRef"
                type="file"
                accept=".jpg,.jpeg,.png"
                multiple
                style="display: none"
                @change="handleFileChange"
            />
            <el-icon size="16">
              <Plus/>
            </el-icon>
            <span>上传</span>
          </div>
        </div>

        <!-- 图片网格 -->
        <div class="image_grid" :class="[(!imageList.length && !imageLoading) ? 'block!':'']" v-loading="imageLoading">
          <div
              v-for="item in imageList"
              :key="item.id"
              class="image_card"
              :class="{ 'is-selected': isSelected(item) }"
              @click="handleSelectImage(item)"
          >
            <div class="image_cover">
              <img :src="item.url" :alt="item.name"/>
              <div class="select_indicator" v-if="isSelected(item)">
              </div>
            </div>
            <div class="flex justify-between items-center px-5px">
              <div class="image_name" :title="item.name">{{ item.name }}</div>
              <el-dropdown
                  trigger="click"
                  append-to=".image_info"
                  popper-class="image_info_dropdown"
                  @command="(command: string) => handleCommand(command, item)"
              >
                <div class="more_btn" @click.stop>
                  <el-icon>
                    <MoreFilled/>
                  </el-icon>
                </div>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="download">下载</el-dropdown-item>
                    <el-dropdown-item command="delete">删除</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>

          <el-empty v-if="!imageList.length && !imageLoading" description="暂无图片"/>
        </div>

        <!-- 分页 -->
        <div class="pagination_wrapper" v-if="imageTotal > 0">
          <el-pagination
              v-model:current-page="imagePageNo"
              v-model:page-size="imagePageSize"
              :total="imageTotal"
              :page-sizes="[20, 40, 60]"
              layout="prev, pager, next"
              @size-change="loadImageList"
              @current-change="loadImageList"
          />
        </div>
      </main>
    </div>

    <template #footer>
      <div class="dialog_footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确定</el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 新建文件夹弹窗 -->
  <FolderEditDialog ref="folderEditDialogRef" @success="handleFolderSuccess"/>
</template>

<style scoped lang="scss">
.dialog_header {
  height: 56px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #E5E6EB;
}

.header_title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.close_icon {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.dialog_content {
  display: flex;
  height: 520px;
}

// 左侧文件夹面板
.folder_panel {
  width: 200px;
  flex-shrink: 0;
  border-right: 1px solid #E5E6EB;
  display: flex;
  flex-direction: column;
  background: #fafafa;
}

.panel_header {
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #E5E6EB;
}

.panel_title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.create_folder_btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 5px 12px;
  background: #2f68ff;
  color: #fff;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.2s;

  &:hover {
    background: #5a82ff;
  }
}

.folder_tree {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.folder_item_wrapper {
  margin-bottom: 4px;
}

.folder_item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  color: #666;
  font-size: 13px;

  &:hover {
    background: #e6f0ff;
    color: #333;
  }

  &.is-active {
    background: #e6f0ff;
    color: #2f68ff;

    .folder_icon {
      color: #2f68ff;
    }
  }

  &.level2 {
    padding-left: 32px;
  }
}

.folder_icon {
  color: #8c8c8c;
  flex-shrink: 0;
}

.folder_name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

// 右侧图片面板
.image_panel {
  flex: 1;
  padding: 16px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.search_input {
  width: 200px;
}

.upload_btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 5px 12px;
  background: #2f68ff;
  color: #fff;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.2s;

  &:hover {
    background: #5a82ff;
  }
}

.image_grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  grid-auto-rows: 180px;
  gap: 51px 12px;
  overflow-y: auto;
  padding-right: 4px;

  &::-webkit-scrollbar {
    width: 4px;
  }

  &::-webkit-scrollbar-thumb {
    background: #dcdfe6;
    border-radius: 2px;
  }
}

.image_card {
  height: 220px;
  cursor: pointer;
  border-radius: 12px;
  border: 2px solid transparent;
  transition: all 0.2s;
  background: #fff;
  box-sizing: border-box;
  overflow: hidden;

  &:hover {
    border-color: #2f68ff;
  }

  &.is-selected {
    border-color: #2f68ff;
  }
}

.image_cover {
  position: relative;
  aspect-ratio: 1;
  background: #f5f6fa;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.select_indicator {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 20px;
  height: 20px;
  background: url("@/assets/geoImage/check.png") no-repeat center;
  background-size: cover;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.image_name {
  width: 75%;
  padding: 8px;
  font-size: 12px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pagination_wrapper {
  margin-top: 12px;
  display: flex;
  justify-content: center;
}

.dialog_footer {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding: 12px 0;
  border-top: 1px solid #E5E6EB;
}


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
