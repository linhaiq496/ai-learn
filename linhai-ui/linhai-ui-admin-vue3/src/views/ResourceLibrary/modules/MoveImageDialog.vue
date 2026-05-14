<script setup lang="ts">
import {ref} from 'vue'
import {Folder, Document} from '@element-plus/icons-vue'
import {ElMessage} from 'element-plus'
import * as MaterialApi from '@/api/geo/Material'
import type {MaterialFolderTree, MaterialImage} from '@/api/geo/Material'

const emit = defineEmits(['success'])

const dialogVisible = ref(false)
const dialogLoading = ref(false)

const currentImage = ref<MaterialImage | null>(null)
const folderList = ref<MaterialFolderTree[]>([])
const selectedFolderId = ref<number | null>(null)

function openDialog(image: MaterialImage, folders: MaterialFolderTree[]) {
  dialogVisible.value = true
  currentImage.value = image
  folderList.value = folders
  selectedFolderId.value = null
}

function handleClose() {
  dialogVisible.value = false
}

async function handleSubmit() {
  if (!selectedFolderId.value) {
    ElMessage.warning('请选择目标文件夹')
    return
  }

  // 检查是否移动到当前文件夹
  if (currentImage.value?.folderId === selectedFolderId.value) {
    ElMessage.warning('图片已在当前文件夹中')
    dialogVisible.value = false
    return
  }

  dialogLoading.value = true
  try {
    await MaterialApi.moveImage({
      id: currentImage.value!.id,
      targetFolderId: selectedFolderId.value
    })
    ElMessage.success('移动成功')
    dialogVisible.value = false
    emit('success')
  } catch (e) {
    console.error('移动图片失败', e)
    ElMessage.error('移动失败')
  } finally {
    dialogLoading.value = false
  }
}

defineExpose({
  openDialog
})
</script>

<template>
  <el-dialog
      v-model="dialogVisible"
      :show-close="false"
      :destroy-on-close="true"
      width="480px"
      class="custom_dialog"
  >
    <template #header>
      <div
          class="h-56px flex items-center justify-between px-16px"
          style="border-bottom: 1px solid #E5E6EB;"
      >
        <div>移动图片</div>
        <img
            src="@/assets/geoImage/close.png"
            class="w-16px h-16px cursor-pointer"
            @click="handleClose"
        />
      </div>
    </template>

    <template #default>
      <div v-loading="dialogLoading" class="move_dialog_content">
        <!-- 当前图片信息 -->
        <div class="current_image_section">
          <div class="section_label">当前图片</div>
          <div class="image_info_card">
            <div class="image_thumb">
              <img :src="currentImage?.url" :alt="currentImage?.name"/>
            </div>
            <div class="image_name">{{ currentImage?.name }}</div>
          </div>
        </div>

        <!-- 目标文件夹选择 -->
        <div class="target_folder_section">
          <div class="section_label">移动至</div>
          <div class="folder_grid">
            <div
                v-for="item in folderList"
                :key="item.id"
                class="folder_card"
                :class="{ 'is-selected': selectedFolderId === item.id }"
                @click="selectedFolderId = item.id"
            >
              <div class="folder_icon">
                <el-icon size="24">
                  <Folder/>
                </el-icon>
              </div>
              <div class="folder_name" :title="item.name">{{ item.name }}</div>
              <div class="select_indicator" v-if="selectedFolderId === item.id">
                <el-icon size="14">
                  <Document/>
                </el-icon>
              </div>
            </div>
          </div>
          <el-empty v-if="!folderList.length" description="暂无可用文件夹"/>
        </div>
      </div>
    </template>

    <template #footer>
      <div class="flex justify-center py-12px" style="border-top: 1px solid #E5E6EB;">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="dialogLoading" @click="handleSubmit" :disabled="!selectedFolderId">
          确认移动
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.move_dialog_content {
  padding: 20px;
}

.section_label {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
  font-weight: 500;
}

.current_image_section {
  margin-bottom: 20px;
}

.image_info_card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f5f6fa;
  border-radius: 10px;
}

.image_thumb {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  flex-shrink: 0;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.image_name {
  flex: 1;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.target_folder_section {
  .section_label {
    padding: 0 4px;
  }
}

.folder_grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  height: 200px;
  overflow-y: auto;

  &::-webkit-scrollbar {
    display: none;
  }
}

.folder_card {
  position: relative;
  padding: 16px 12px;
  background: #f5f6fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  align-items: center;
  border: 2px solid transparent;

  &:hover {
    background: #eef2ff;
  }

  &.is-selected {
    background: #e6f0ff;
    border-color: #2f68ff;

    .folder_icon {
      color: #2f68ff;
    }
  }
}

.folder_icon {
  color: #8c8c8c;
  margin-bottom: 8px;
  transition: color 0.2s;
}

.folder_name {
  font-size: 13px;
  color: #333;
  text-align: center;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.select_indicator {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 18px;
  height: 18px;
  background: #2f68ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}
</style>
