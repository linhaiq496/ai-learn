<template>
  <div class="editor-wrapper" :style="{ height }">
    <div ref="toolbarRef" class="toolbar-container"></div>

    <div class="title-container">
      <el-input v-model="titleModel" placeholder="请输入标题" />
    </div>

    <div ref="editorRef" class="editor-container"></div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, shallowRef, watch } from 'vue'
import { createEditor, createToolbar } from '@wangeditor/editor'
import '@wangeditor/editor/dist/css/style.css'
import { ElMessage } from 'element-plus'
import { uploadFile } from '@/api/geo/uploadImage'

const props = defineProps({
  height: {
    type: String,
    default: '800px'
  },
  isAllowUploadImage: {
    type: Boolean,
    default: false
  }
})

const titleModel = defineModel('title', {
  type: String,
  default: ''
})

const contentModel = defineModel('content', {
  type: String,
  default: ''
})

const editorRef = ref(null)
const toolbarRef = ref(null)
const editorInstance = shallowRef(null)

const toolbarConfig = computed(() => ({
  toolbarKeys: [
    'undo',
    'redo',
    '|',
    'fontSize',
    'bold',
    'italic',
    'through',
    'underline',
    'color',
    '|',
    'justifyLeft',
    'justifyCenter',
    'justifyRight',
    '|',
    'bulletedList',
    'numberedList',
    'indent',
    ...(props.isAllowUploadImage ? ['|', 'uploadImage'] : [])
  ]
}))

async function uploadImageToServer(file) {
  ElMessage.success('正在上传图片，请稍候')
  const url = await handleFileChange(file)
  return url || null
}

async function handleFileChange(file) {
  if (!file) return ''

  const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg']
  if (!allowedTypes.includes(file.type)) {
    ElMessage.error('仅支持 JPG/JPEG/PNG 格式')
    return ''
  }

  const maxSize = 5 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('图片大小不能超过 5MB')
    return ''
  }

  return new Promise((resolve) => {
    const img = new Image()
    img.src = URL.createObjectURL(file)

    img.onload = async () => {
      URL.revokeObjectURL(img.src)

      if (img.width < 160 || img.height < 160) {
        ElMessage.error('图片分辨率不能低于 160*160')
        resolve('')
        return
      }

      try {
        const formData = new FormData()
        formData.append('file', file)
        const res = await uploadFile(formData)
        resolve(res || '')
      } catch (e) {
        console.error('上传图片失败:', e)
        ElMessage.error('上传图片失败')
        resolve('')
      }
    }

    img.onerror = () => {
      URL.revokeObjectURL(img.src)
      ElMessage.error('图片加载失败')
      resolve('')
    }
  })
}

function syncEditorContent(value) {
  if (!editorInstance.value) return

  const nextContent = value || ''
  if (editorInstance.value.getHtml() !== nextContent) {
    editorInstance.value.setHtml(nextContent)
  }
}

onMounted(() => {
  editorInstance.value = createEditor({
    selector: editorRef.value,
    config: {
      placeholder: '请输入正文内容',
      onChange(editor) {
        const html = editor.getHtml()
        if (contentModel.value !== html) {
          contentModel.value = html
        }
      },
      hoverbarKeys: {
        text: [],
        image: []
      },
      MENU_CONF: {
        uploadImage: {
          maxFileSize: 10 * 1024 * 1024,
          allowedFileTypes: ['image/*'],
          async customUpload(file, insertFn) {
            if (!props.isAllowUploadImage) {
              return
            }

            try {
              const url = await uploadImageToServer(file)
              if (url) insertFn(url)
            } catch (e) {
              console.error(e)
              ElMessage.error('图片上传失败')
            }
          }
        }
      }
    },
    mode: 'default'
  })

  syncEditorContent(contentModel.value)

  createToolbar({
    editor: editorInstance.value,
    selector: toolbarRef.value,
    config: toolbarConfig.value,
    mode: 'default'
  })
})

watch(contentModel, (value) => {
  syncEditorContent(value)
})

onBeforeUnmount(() => {
  editorInstance.value?.destroy()
  editorInstance.value = null
})
</script>

<style scoped>
.editor-wrapper {
  display: flex;
  flex-direction: column;
  scrollbar-width: none;

  .toolbar-container {
    width: calc(100% - 40px);
    margin-left: 20px;
    margin-top: 20px;
    border-bottom: 1px solid #eeeef0;

    :deep(& .w-e-bar-divider) {
      height: 30px;
      margin: 5px 5px;
      width: 1.5px;
      background-color: #d7d7e6;
    }

    :deep(& .w-e-bar svg) {
      fill: #2e2e3a;
    }
  }
}

.title-container {
  width: 98%;
  margin-left: 1%;
}

:deep(.el-input__wrapper, .is_foucs) {
  border: none;
  box-shadow: none;
  border-bottom: 1px solid #eeeef0;

  .el-input__inner {
    color: #1d2129;
    font-family: PingFang SC, sans-serif;
    font-weight: 400;
    font-size: 22px;
    line-height: 100%;
    height: 71px;
  }

  &:hover {
    box-shadow: none;
  }
}

.editor-container {
  min-height: 300px;
  height: 100%;
  overflow-y: auto;
}
</style>
