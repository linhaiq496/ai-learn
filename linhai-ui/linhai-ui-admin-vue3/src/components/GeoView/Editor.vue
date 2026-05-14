<script lang="ts" setup>
import { PropType } from 'vue'
import { Editor, Toolbar } from '@wangeditor-next/editor-for-vue'
import { i18nChangeLanguage, IDomEditor, IEditorConfig } from '@wangeditor-next/editor'
import { propTypes } from '@/utils/propTypes'
import { isNumber } from '@/utils/is'
import { ElMessage } from 'element-plus'
import { useLocaleStore } from '@/store/modules/locale'
import { useUpload } from '@/components/UploadFile/src/useUpload'
import merge from 'lodash-es/merge'

defineOptions({ name: 'Editor' })

type InsertFnType = (url: string, alt: string, href: string) => void

const localeStore = useLocaleStore()
const currentLocale = computed(() => localeStore.getCurrentLocale)
i18nChangeLanguage(unref(currentLocale).lang)

/* ================== props ================== */
const props = defineProps({
  editorId: propTypes.string.def('wangEditor-1'),
  height: propTypes.oneOfType([Number, String]).def('500px'),
  editorConfig: {
    type: Object as PropType<Partial<IEditorConfig>>,
    default: () => undefined
  },
  readonly: propTypes.bool.def(false),
  modelValue: {
    type: Object as PropType<{
      title: string
      content: string // slate json
    }>,
    default: () => ({
      title: '',
      content: ''
    })
  },
  directory: propTypes.string.def('editor-default')
})

const emit = defineEmits(['change', 'update:modelValue'])

/* ================== upload ================== */
const { httpRequest: imageHttpRequest } = useUpload(`${props.directory}-image`)
const { httpRequest: videoHttpRequest } = useUpload(`${props.directory}-video`)

/* ================== editor ================== */
const editorRef = shallowRef<IDomEditor>()

const valueHtml = ref('')      // editor 内部 html
const titleContent = ref('')   // 标题
const contentSlate = ref('')   // slate json（对外）

/* ================== 回填父组件 ================== */
watch(
    () => props.modelValue,
    async (val) => {
      if (!val) return

      titleContent.value = val.title || ''

      if (val.content && editorRef.value) {
        try {
          const slate = JSON.parse(val.content)
          editorRef.value.setContent(slate)
        } catch (e) {
          console.warn('slate 内容解析失败')
        }
      }
    },
    { immediate: true, deep: true }
)

/* ================== 双向绑定 emit ================== */
watch(
    [titleContent, contentSlate],
    () => {
      emit('update:modelValue', {
        title: titleContent.value,
        content: contentSlate.value
      })
    },
    { deep: true }
)

/* ================== readonly ================== */
watch(
    () => props.readonly,
    async (val) => {
      if (!editorRef.value) await nextTick()
      val ? editorRef.value?.disable() : editorRef.value?.enable()
    }
)

const handleCreated = (editor: IDomEditor) => {
  editorRef.value = editor
}

/* ================== editor config ================== */
const editorConfig = computed((): IEditorConfig => {
  return merge(
      {
        placeholder: '请输入内容...',
        readOnly: props.readonly,

        // ✅ 关键：放在根级
        mentionConfig: {
          showModal() {},
          hideModal() {}
        },

        customAlert: (s: string, t: string) => {
          switch (t) {
            case 'success':
              ElMessage.success(s)
              break
            case 'info':
              ElMessage.info(s)
              break
            case 'warning':
              ElMessage.warning(s)
              break
            case 'error':
              ElMessage.error(s)
              break
            default:
              ElMessage.info(s)
              break
          }
        },

        autoFocus: false,
        scroll: true,

        MENU_CONF: {
          uploadImage: { /* 不动 */ },
          uploadVideo: { /* 不动 */ }
        }
      },
      props.editorConfig || {}
  )
})


const editorStyle = computed(() => ({
  height: isNumber(props.height) ? `${props.height}px` : props.height
}))

/* ================== editor change ================== */
const handleChange = (editor: IDomEditor) => {
  // slate json
  contentSlate.value = JSON.stringify(editor.children)
  emit('change', editor)
}

/* ================== destroy ================== */
onBeforeUnmount(() => {
  editorRef.value?.destroy()
})

const getEditorRef = async (): Promise<IDomEditor> => {
  await nextTick()
  return editorRef.value as IDomEditor
}

defineExpose({ getEditorRef })
</script>

<template>
  <div class="z-10">
    <Toolbar
        :editor="editorRef"
        :editorId="editorId"
        class="m-20px"
        style="border-bottom: 1px solid #eeeef0"
    />

    <div class="w-98%" style="margin-left: 1%">
      <el-input v-model="titleContent" />
    </div>

    <Editor
        v-model="valueHtml"
        :defaultConfig="editorConfig"
        :editorId="editorId"
        :style="editorStyle"
        @on-change="handleChange"
        @on-created="handleCreated"
    />
  </div>
</template>

<style src="@wangeditor-next/editor/dist/css/style.css"></style>

<style scoped>
:deep(.el-input__wrapper, .is_foucs) {
  border: none;
  box-shadow: none;
  border-bottom: 1px solid #eeeef0;

  .el-input__inner {
    color: #1D2129;
    font-family: PingFang SC, sans-serif;
    font-weight: 400;
    font-size: 22px;
    line-height: 100%;
  }
  &:hover {
    box-shadow: none;
  }
}
</style>
