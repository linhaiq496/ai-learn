<script setup lang="ts">
import {computed, reactive, ref} from 'vue'
import {ElMessage} from 'element-plus'
import * as MaterialApi from '@/api/geo/Material'

const emit = defineEmits(['success'])

interface FolderData {
  id?: number
  name?: string
}

const dialogVisible = ref(false)
const formLoading = ref(false)
const formRef = ref()

const dialogType = ref('create') // create | edit
const currentRow = ref<FolderData | null>(null)

const formData = reactive({
  folderName: '',
})

const dialogTitle = computed(() => {
  return dialogType.value === 'edit' ? '编辑文件夹' : '新建文件夹'
})

const formRules = reactive({
  folderName: [
    {required: true, message: '请输入文件夹名称', trigger: 'blur'},
    {min: 1, max: 15, message: '文件夹名称长度为 1-15 个字符', trigger: 'blur'}
  ],

})

function resetForm() {
  formData.folderName = ''
  formRef.value?.clearValidate()
}

function openDialog(data?: FolderData) {
  dialogVisible.value = true
  resetForm()

  if (data && data.id) {
    dialogType.value = 'edit'
    currentRow.value = data
    formData.folderName = data.name || ''
  } else {
    dialogType.value = 'create'
    currentRow.value = null
  }
}

function handleClose() {
  dialogVisible.value = false
}

async function handleSubmit() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    formLoading.value = true
    try {
      const params = {
        id: currentRow.value?.id,
        folderName: formData.folderName.trim()
      }

      if (dialogType.value === 'edit') {
        // 编辑文件夹
        await MaterialApi.updateFolderName({
          id: params.id!,
          name: params.folderName
        })
        ElMessage.success('编辑成功')
        dialogVisible.value = false
        emit('success')
      } else {
        // 创建一级文件夹
        await MaterialApi.createFolder({
          parentId: 0,
          name: params.folderName
        })
        ElMessage.success('创建成功')
        dialogVisible.value = false
        emit('success')
      }
    } catch (error) {
      console.error(error)
      ElMessage.error(dialogType.value === 'edit' ? '编辑失败' : '创建失败')
    } finally {
      formLoading.value = false
    }
  })
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
      width="560px"
      class="custom_dialog"
  >
    <template #title>
      <div
          class="h-56px flex items-center justify-between px-16px"
          style="border-bottom: 1px solid #E5E6EB;"
      >
        <div>{{ dialogTitle }}</div>
        <img
            src="@/assets/geoImage/close.png"
            class="w-16px h-16px cursor-pointer"
            @click="handleClose"
        />
      </div>
    </template>

    <template #default>
      <div v-loading="formLoading" class="p-20px">
        <el-form
            ref="formRef"
            :model="formData"
            :rules="formRules"
            label-width="100px"
            @submit.prevent="handleSubmit"
        >
          <el-form-item prop="folderName" required class="label-top-item">
            <div class="text1  mb-20px color-#252632">文件夹名称</div>
            <el-input
                v-model="formData.folderName"
                maxlength="15"
                show-word-limit
                placeholder="请输入文件夹名称"
                clearable
            />
          </el-form-item>
        </el-form>

      </div>
    </template>

    <template #footer>
      <div class="flex justify-center py-12px" style="border-top: 1px solid #E5E6EB;">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="handleSubmit">
          提交
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.text1 {
  font-family: PingFang SC, sans-serif;
  font-weight: 400;
  font-size: 17px;
  line-height: 100%;
  letter-spacing: 0;
}

:deep(.el-form-item__content) {
  margin-left: 0 !important;
}

:deep(.el-input__wrapper) {
  height: 60px;
  border-radius: 3px;
  background-color: #F5F6FA;
  box-shadow: none !important;

  :deep(.el-input__count-inner) {
    background-color: transparent;
    font-family: PingFang SC, sans-serif;
    font-weight: 400;
    font-size: 14px;
    line-height: 100%;
    letter-spacing: 0;
  }
}

:deep(.el-textarea__inner) {
  border-radius: 3px;
  background-color: #F5F6FA;
  box-shadow: none !important;
  padding: 15px 20px;
}

:deep(.el-textarea .el-input__count) {
  background-color: transparent;
  font-family: PingFang SC, sans-serif;
  font-weight: 400;
  font-size: 14px;
  line-height: 100%;
  letter-spacing: 0;
}

.label-top-item {
  flex-direction: column;
}
</style>
