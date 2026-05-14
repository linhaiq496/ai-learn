<template>
  <div class="work_container">
    <input
      ref="uploadRef"
      type="file"
      multiple
      accept="image/png,image/jpeg,image/jpg,image/webp"
      class="upload_input"
      @change="handleUploadChange"
    />
    <el-form :model="formData" :rules="rules" ref="formRef">
      <!-- 文本描述 -->
      <el-form-item required>
        <template #label>
          <div class="form_title">文本描述</div>
        </template>
        <template #default>
          <div class="good_desc_box">
            <el-input
              v-model="formData.goodDescription"
              placeholder="写下你的创作灵感"
              type="textarea"
              :rows="6"
              maxlength="1000"
              show-word-limit
              ref="editorRef"
              :show-word-count="true"
              :max-length="1000"
              class="good_desc_input"
            />
          </div>
          <div class="good_button_list">
            <el-tooltip
              placement="bottom-start"
              effect="light"
              transition="el-fade-in-linear"
              :visible="promptTip"
              :trigger="'click'"
              popper-class="popper-rounded12"
            >
              <div>
                <div class="good_button_list_item" v-show="promptData.status">
                  <img src="@/assets/geoImage/auto_hosting_img/prompt2.png" class="w-20px h-20px" />
                  <span>优化中...</span>
                </div>
                <div @click="fixPrompt" class="good_button_list_item" v-show="!promptData.status">
                  <img src="@/assets/geoImage/auto_hosting_img/prompt1.png" class="w-20px h-20px" />
                  <span>提示词优化</span>
                </div>
              </div>
              <template #content>
                <div class="good_button_list_item_tip">
                  <div class="tip_header">
                    <div class="tip_header_title"> 提示词优化 </div>
                    <div class="tip_header_close_icon" @click="promptTip = false">
                      <img
                        src="@/assets/geoImage/auto_hosting_img/close.png"
                        class="w-16px h-16px"
                      />
                    </div>
                  </div>
                  <div class="tip_content">
                    {{ promptData.prompt }}
                  </div>
                  <div class="tip_footer">
                    <div class="tip_footer_button tip_reset" @click="fixPrompt">
                      <img
                        src="@/assets/geoImage/auto_hosting_img/reset.png"
                        class="w-17px h-17px"
                      />
                      <span>重新生成</span>
                    </div>
                    <div
                      class="tip_footer_button tip_use"
                      @click="
                        formData.goodDescription = promptData.prompt;
                        promptTip = false
                      "
                      >确定使用
                    </div>
                  </div>
                </div>
              </template>
            </el-tooltip>
            <div class="knowledge_base max-w-100px">
              <img src="@/assets/geoImage/knowledge.png" class="w-20px h-20px" />
              <div class="truncate">
                {{ knowledgeBaseLabel }}
              </div>
            </div>
            <div class="note_type">
              <el-select
                v-model="formData.noteType"
                placeholder="请选择笔记类型"
                style="width: 100px"
              >
                <el-option
                  :label="item.label"
                  :value="item"
                  v-for="item in noteTypeList"
                  :key="item.id"
                />
              </el-select>
            </div>
            <div class="article_list">
              <el-select
                v-model="formData.articleNumber"
                placeholder="请选择平台格式"
                style="width: 71px"
              >
                <el-option :label="item + '个'" :value="item" v-for="item in 9" :key="item" />
              </el-select>
            </div>
          </div>
          <!--          <div class="good_instruction">-->
          <!--            <div class="good_instruction_list">-->
          <!--              <div v-for="item in exampleDescList" class="good_instruction_item" :key="item.id"-->
          <!--                   @click="formData.goodDescription = item.desc">-->
          <!--                {{ item.abstract }}-->
          <!--              </div>-->

          <!--            </div>-->
          <!--            <div class="good_instruction_getNew" @click="getNewInstruction(true)">-->

          <!--              <el-icon :size="16" color="#165DFF">-->
          <!--                <Refresh/>-->
          <!--              </el-icon>-->
          <!--              <span>换一换</span>-->
          <!--            </div>-->
          <!--          </div>-->
        </template>
      </el-form-item>
      <!--   mt-14占位   -->
      <!--      <div class="mt-17"></div>-->
      <!-- 参考图数量 -->
      <el-form-item required>
        <template #label>
          <div class="form_title">平台格式</div>
        </template>
        <template #default>
          <div class="platform_list">
            <div
              v-for="item in platformList"
              :key="item.value"
              class="platform_item"
              :class="{ platform_item_active: item.value === formData.platformType.value }"
              @click="formData.platformType = item"
            >
              <div :class="item.cssClass" class="w-24px h-24px"></div>
              <span>{{ item.label }}</span>
            </div>
          </div>
        </template>
      </el-form-item>
      <el-form-item required>
        <template #label>
          <div class="form_title">文本长度</div>
        </template>
        <template #default>
          <div class="option_chip_list">
            <div
              v-for="item in [100, 300, 500, 1000]"
              :key="item"
              class="option_chip"
              :class="{ option_chip_active: formData.articleLength === item }"
              @click="formData.articleLength = item"
            >
              {{ item + '字' }}
            </div>
          </div>
        </template>
      </el-form-item>
      <el-form-item required>
        <template #label>
          <div class="form_title">图片来源</div>
        </template>
        <template #default>
          <div class="image_source_panel">
            <div class="image_source_options">
              <div
                v-for="item in imageSourceOptions"
                :key="item.value"
                class="image_source_card"
                :class="{ image_source_card_active: formData.imageMode === item.value }"
                @click="setImageMode(item.value)"
              >
                <el-checkbox
                  :model-value="formData.imageMode === item.value"
                  @change="setImageMode(item.value)"
                  @click.stop
                />
                <span>{{ item.label }}</span>
              </div>
            </div>

            <div v-if="formData.imageMode === 'upload'" class="upload_image_box">
              <div class="upload_image_action" @click="handleUploadClick">
                <img src="@/assets/geoImage/image.png" class="w-28px h-28px" />
                <span>上传图片</span>
              </div>
              <div v-if="formData.referenceImage.length" class="uploaded_image_list">
                <div
                  v-for="(item, index) in formData.referenceImage"
                  :key="item.id"
                  class="uploaded_image_item"
                >
                  <el-image
                    class="uploaded_image_preview"
                    fit="cover"
                    :src="item.url"
                    :preview-src-list="previewUploadList"
                    :initial-index="index"
                  />
                  <img
                    src="@/assets/geoImage/delete.png"
                    class="uploaded_image_delete"
                    @click.stop="handleRemoveUploadImage(item.id)"
                  />
                </div>
              </div>
            </div>
          </div>
        </template>
      </el-form-item>
      <el-form-item v-if="formData.imageMode === 'generate'" required>
        <template #label>
          <div class="form_title">图片比例</div>
        </template>
        <template #default>
          <div class="image_ratio_list">
            <div
              v-for="item in ratioStyles"
              :key="item.label"
              class="image_ratio_item"
              :class="{ image_ratio_item_active: formData.imageRatio === item.label }"
              @click="formData.imageRatio = item.label"
            >
              <div class="ratio_preview">
                <div class="ratio_box" :style="item.style"></div>
              </div>
              <div class="ratio_text">{{ item.label }}</div>
            </div>
          </div>
        </template>
      </el-form-item>
      <el-form-item v-if="formData.imageMode === 'generate'" required>
        <template #label>
          <div class="form_title">图片风格</div>
        </template>
        <template #default>
          <div class="note_type image_style_select">
            <el-select
              v-model="formData.imageStyle"
              placeholder="请选择图片风格"
              style="width: 120px"
            >
              <el-option
                :label="item.label"
                :value="item"
                v-for="item in imageTypeList"
                :key="item.id"
              />
            </el-select>
          </div>
        </template>
      </el-form-item>
      <el-form-item v-if="formData.imageMode === 'generate'" required>
        <template #label>
          <div class="form_title">图片数量</div>
        </template>
        <template #default>
          <div class="option_chip_list image_number_list">
            <div
              v-for="item in 3"
              :key="item"
              class="option_chip"
              :class="{ option_chip_active: formData.imageNumber === item }"
              @click="formData.imageNumber = item"
            >
              {{ item + '个' }}
            </div>
          </div>
        </template>
      </el-form-item>
    </el-form>
  </div>

  <div class="btn_list">
    <div class="generate_image" @click="generateArticle" v-loading="isLoading">
      <img src="@/assets/geoImage/auto_hosting_img/sendGeneraet.svg" style="width: 20px" />
      <span>立即生成</span>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { getPrompt } from '@/api/geo/generateArticle'
import * as DictDataApi from '@/api/system/dict/dict.data'
import { useDictStore } from '@/store/modules/dict'
import { KnowledgeApi } from '@/api/ai/knowledge/knowledge'
import { getUUIDv4 } from '@/utils/uuid'
import { uploadImage } from '@/api/generateImage'
import { generateAsyncApi } from '@/api/ai/longArticle'

const promptTip = ref(false)
// const noteTypeVisible = ref(false)
const _emits = defineEmits(['createImage'])
const useDictStoreRef = useDictStore()

/* ========================
 * refs
 * ======================== */
// const upload_type = ref(1)
const formRef = ref(null)
const uploadRef = ref(null)

// 示例描述
// const exampleDescList = ref([
//   {id: "11", desc: "家电商品卖点描述家电商品卖点描述", abstract: '家电商品卖点描述'},
//   {id: "122", desc: "电子类产品的卖点描述文本12312312", abstract: '电子类产品的卖点描述文本'}
// ])

const exampleSearchVo = reactive({
  pageNo: 0,
  pageSize: 2,
  total: 100
})

const noteTypeList = ref([])
const platformList = ref([])
const imageTypeList = ref([])
const knowledgeBaseList = ref([])
const ratioList = [{ label: '1:1' }, { label: '3:4' }, { label: '16:9' }, { label: '9:16' }]
const imageSourceOptions = [
  { label: '使用生成图片', value: 'generate' },
  { label: '自主上传图片', value: 'upload' }
]
const MAX_UPLOAD_IMAGES = 6
const ALLOW_TYPES = ['image/jpeg', 'image/png', 'image/jpg', 'image/webp']
const MAX_SIZE = 10 * 1024 * 1024

const ratioSizeMap = {
  '1:1': { w: 1, h: 1 },
  '3:4': { w: 3, h: 4 },
  '16:9': { w: 16, h: 9 },
  '9:16': { w: 9, h: 16 }
}

const ratioStyles = ratioList.map((item) => {
  const size = ratioSizeMap[item.label]
  const isLandscape = size.w >= size.h

  return {
    ...item,
    style: {
      aspectRatio: `${size.w} / ${size.h}`,
      width: isLandscape ? '40px' : 'auto',
      height: isLandscape ? 'auto' : '40px',
      maxWidth: '40px',
      maxHeight: '40px'
    }
  }
})

/* ========================
 * 表单数据
 * ======================== */
const formData = ref({
  goodDescription: '',
  articleNumber: 1,
  articleLength: 300,
  platformType: {},
  noteType: {},
  knowledgeBase: {},
  imageMode: 'generate',
  // imageDesc: '', // 图片描述，当前页未启用
  imageNumber: 1, // 图片数量
  imageUrls: [], // 做同款时透传素材图，当前页未单独展示
  referenceImage: [],
  imageStyle: { value: 'xieshi', label: '写实', colorType: '', cssClass: '' }, // 图片风格
  imageRatio: '3:4' // 图片比例
})

// 提示词优化
const promptData = reactive({
  prompt: '',
  status: 0
})

const isLoading = ref(false)
const pendingMakeSameItem = ref(null)

const knowledgeBaseLabel = computed(() => {
  return formData.value.knowledgeBase?.name || '知识库'
})

const previewUploadList = computed(() => formData.value.referenceImage.map((item) => item.url))

function getMatchCandidates(...values) {
  return values
    .flatMap((value) => {
      if (value == null || value === '') return []
      if (typeof value === 'object') {
        return [value.id, value.value, value.label, value.name]
      }
      return [value]
    })
    .filter((value) => value != null && value !== '')
}

function findMatchedOption(list = [], ...values) {
  const candidates = getMatchCandidates(...values)

  if (!candidates.length) return null

  return (
    list.find((item) => {
      const itemValues = getMatchCandidates(item)
      return itemValues.some((value) => candidates.includes(value))
    }) || null
  )
}

function hasSelectedOption(value) {
  return !!value && Object.keys(value).length > 0
}

function getPositiveNumber(value) {
  const parsed = Number(value)
  return Number.isFinite(parsed) && parsed > 0 ? parsed : null
}

function getMatchedArticleLength(value) {
  const parsed = getPositiveNumber(value)
  if (!parsed) return null

  return [100, 300, 500, 1000].includes(parsed) ? parsed : null
}

function applyMakeSameData(sameItem) {
  if (!sameItem) return

  pendingMakeSameItem.value = sameItem

  const prompt =
    sameItem.finalPrompt ||
    sameItem.query ||
    sameItem.prompt ||
    sameItem.textDescription ||
    sameItem.goodDescription ||
    ''
  if (prompt) {
    formData.value.goodDescription = prompt
  }

  const articleCount = getPositiveNumber(sameItem.articleCount || sameItem.articleNumber)
  if (articleCount) {
    formData.value.articleNumber = articleCount
  }

  const articleLength = getMatchedArticleLength(sameItem.wordCount || sameItem.articleLength)
  if (articleLength) {
    formData.value.articleLength = articleLength
  }

  const imageNumber = getPositiveNumber(sameItem.imageNumber)
  if (imageNumber) {
    formData.value.imageNumber = Math.min(imageNumber, 3)
  }

  const imageRatio = sameItem.imageRatio || sameItem.ratio
  if (ratioList.some((item) => item.label === imageRatio)) {
    formData.value.imageRatio = imageRatio
  }

  const imageUrls = Array.isArray(sameItem.imageUrls)
    ? sameItem.imageUrls
    : Array.isArray(sameItem.productImageUrl)
      ? sameItem.productImageUrl
      : []
  formData.value.imageUrls = imageUrls.filter(Boolean)
  releaseReferenceObjectUrls()
  formData.value.referenceImage = formData.value.imageUrls.map((url) => createUploadedImageItem(url))
  if (formData.value.referenceImage.length) {
    formData.value.imageMode = 'upload'
  }

  const matchedNoteType = findMatchedOption(
    noteTypeList.value,
    sameItem.articleType,
    sameItem.noteType,
    sameItem.noteSubject
  )
  if (matchedNoteType) {
    formData.value.noteType = matchedNoteType
  }

  const matchedPlatformType = findMatchedOption(
    platformList.value,
    sameItem.platform,
    sameItem.platformType,
    sameItem.platformFormat
  )
  if (matchedPlatformType) {
    formData.value.platformType = matchedPlatformType
  }

  const matchedImageStyle = findMatchedOption(
    imageTypeList.value,
    sameItem.imageType,
    sameItem.imageStyle
  )
  if (matchedImageStyle) {
    formData.value.imageStyle = matchedImageStyle
  }

  const matchedKnowledgeBase = findMatchedOption(
    knowledgeBaseList.value,
    sameItem.knowledgeBase,
    sameItem.knowledgeBaseId,
    sameItem.knowledgeBaseName
  )
  if (matchedKnowledgeBase) {
    formData.value.knowledgeBase = matchedKnowledgeBase
  }
}

/* ========================
 * 校验
 * ======================== */
const rules = {
  goodDescription: [{ required: true, message: '请输入文本描述', trigger: 'blur' }]
}

/* ========================
 * 笔记类型（从字典获取）
 * ======================== */
async function getNoteTypeList() {
  try {
    const res = await DictDataApi.getDictDataPage({ dictType: 'good_note_type' })
    if (res.list && res.list.length) {
      noteTypeList.value = res.list
      applyMakeSameData(pendingMakeSameItem.value)
      if (!hasSelectedOption(formData.value.noteType)) {
        formData.value.noteType = res.list[0] || {}
      }
    }
  } catch (e) {
    console.log(e)
  }
}

/* ========================
 * 平台类型（从字典获取）
 * ======================== */
async function getplatformList() {
  try {
    const res = (await useDictStoreRef.getDictByType('platform_type')) || []
    if (res && res.length) {
      platformList.value = res
      applyMakeSameData(pendingMakeSameItem.value)
      if (!hasSelectedOption(formData.value.platformType)) {
        formData.value.platformType = res[0] || {}
      }
    }
  } catch (e) {
    console.log(e)
  }
}

/* ========================
 * 获取图片风格 image_type
 * ========================*/
async function getImageTypeList() {
  try {
    const res = (await useDictStoreRef.getDictByType('image_type')) || []
    if (res && res.length) {
      imageTypeList.value = res
      applyMakeSameData(pendingMakeSameItem.value)
      if (!hasSelectedOption(formData.value.imageStyle)) {
        formData.value.imageStyle = res[0] || formData.value.imageStyle
      }
    }
  } catch (e) {
    console.log(e)
  }
}

/* ========================
 * 获取知识库列表
 * ======================== */
async function getKnowledgeBaseList() {
  try {
    // const res = await KnowledgeApi.getSimpleKnowledgeList()
    // if (res && res.length) {
    //   knowledgeBaseList.value = res
    //   applyMakeSameData(pendingMakeSameItem.value)
    //   if (!hasSelectedOption(formData.value.knowledgeBase)) {
    //     formData.value.knowledgeBase = res[0]
    //   }
    // }
    // 默认数据 todo todo todo todo todo todo todo todo todo todo todo todo todo todo  todo todo todo todo todo todo todo todo todo todo todo todo todo todo
    formData.value.knowledgeBase = {
      id: 1,
      name: '默认知识库'
    }
  } catch (e) {
    console.log(e)
  }
}

/* ========================
 * 提示词优化
 * ======================== */
async function fixPrompt() {
  promptTip.value = false

  if (!formData.value.goodDescription) {
    return ElMessage.warning('请输入文本描述')
  }
  // if (!formData.value.images.length) {
  //   return ElMessage.warning('请上传商品图片')
  // }
  if (promptData.status) {
    return ElMessage.warning('正在优化中，请稍后')
  }

  promptData.status = 1
  try {
    const res = await getPrompt({
      prompt: formData.value.goodDescription
    })
    promptData.prompt = res.choices[0].message.content[0].text
    promptTip.value = true
  } catch (e) {
    console.log(e)
    ElMessage.warning('提示词优化失败，请稍后重试')
  } finally {
    promptData.status = 0
  }
}

async function ensureUploadedImageUrls() {
  const images = formData.value.referenceImage
  if (!images.length) return []

  const needUpload = images.filter((item) => !item.uploaded && item.file)
  if (needUpload.length) {
    const res = await uploadImage(needUpload.map((item) => item.file))
    const urls = res?.urls || []
    if (urls.length < needUpload.length) {
      ElMessage.error('图片上传失败，请稍后重试')
      throw new Error('Upload image response missing urls')
    }

    needUpload.forEach((item, index) => {
      const url = urls[index]
      if (!url) return
      if (item.objectUrl) {
        URL.revokeObjectURL(item.url)
      }
      item.url = url
      item.uploaded = true
      item.objectUrl = false
      delete item.file
    })
  }

  return images.map((item) => item.url).filter(Boolean)
}

// 立即生成
async function generateArticle() {
  // 1、必填项过滤
  const ispassed = RequiredFiltering()
  console.log(ispassed)
  if (!ispassed) {
    return
  } else {
    isLoading.value = true
  }
  try {
    const uploadedImageUrls =
      formData.value.imageMode === 'upload' ? await ensureUploadedImageUrls() : []
    formData.value.imageUrls = uploadedImageUrls

    // 映射表单数据到后端接口参数
    const params = {
      articleType: formData.value.noteType?.label || '',
      query: formData.value.goodDescription || '',
      articleCount: formData.value.articleNumber || 1,
      wordCount: formData.value.articleLength || 300,
      imageNumber:
        formData.value.imageMode === 'upload'
          ? uploadedImageUrls.length
          : Math.min(formData.value.imageNumber || 1, 3),
      imageRatio: formData.value.imageMode === 'generate' ? formData.value.imageRatio : undefined,
      imageType:
        formData.value.imageMode === 'generate' ? formData.value.imageStyle?.label : undefined,
      isImageGenerated: formData.value.imageMode === 'generate',
      imageUrls: uploadedImageUrls.length ? uploadedImageUrls : undefined,
      knowledgeBase: formData.value.knowledgeBase?.name || '',
      platform: formData.value.platformType?.label || ''
    }
    const res = await generateAsyncApi(params)
    _emits('createImage', res)
  } catch (e) {
    console.log(e)
  } finally {
    isLoading.value = false
  }
}

function RequiredFiltering() {
  formData.value.goodDescription = formData.value.goodDescription.trim()
  if (!formData.value.goodDescription) {
    ElMessage.warning('请输入文本描述')
    return false
  } else if (!formData.value.noteType?.label) {
    ElMessage.warning('请选择笔记类型')
    return false
  } else if (!formData.value.platformType?.label) {
    ElMessage.warning('请选择平台类型')
    return false
  } else if (!formData.value.knowledgeBase?.name) {
    ElMessage.warning('未获取到知识库，请先配置知识库')
    return false
  } else if (!formData.value.articleNumber) {
    ElMessage.warning('请选择生成文章数量')
    return false
  } else if (formData.value.imageMode === 'generate' && !formData.value.imageNumber) {
    ElMessage.warning('请选择图片数量')
    return false
  } else if (formData.value.imageMode === 'generate' && formData.value.imageNumber > 3) {
    ElMessage.warning('图片数量最多选择 3 张')
    return false
  } else if (formData.value.imageMode === 'upload' && !formData.value.referenceImage.length) {
    ElMessage.warning('请上传图片')
    return false
  } else if (formData.value.imageMode === 'generate' && !formData.value.imageStyle?.label) {
    ElMessage.warning('请选择图片风格')
    return false
  } else if (formData.value.imageMode === 'generate' && !formData.value.imageRatio) {
    ElMessage.warning('请选择图片比例')
    return false
  }

  return true
}

function setImageMode(mode) {
  formData.value.imageMode = mode
}

function handleUploadClick() {
  if (formData.value.referenceImage.length >= MAX_UPLOAD_IMAGES) {
    ElMessage.warning(`最多上传 ${MAX_UPLOAD_IMAGES} 张图片`)
    return
  }
  if (!uploadRef.value) return
  uploadRef.value.value = ''
  uploadRef.value.click()
}

function validateImage(file) {
  if (!ALLOW_TYPES.includes(file.type)) {
    ElMessage.error('仅支持 JPG / PNG / JPEG / GIF / WEBP 格式图片')
    return false
  }
  if (file.size > MAX_SIZE) {
    ElMessage.error('图片大小不能超过 10MB')
    return false
  }
  return true
}

function createImageItem(file) {
  return {
    id: getUUIDv4(),
    file,
    fileName: file.name,
    url: URL.createObjectURL(file),
    uploaded: false,
    objectUrl: true
  }
}

function createUploadedImageItem(url) {
  return {
    id: getUUIDv4(),
    url,
    uploaded: true,
    objectUrl: false
  }
}

function releaseReferenceObjectUrls() {
  formData.value.referenceImage.forEach((item) => {
    if (item.objectUrl) {
      URL.revokeObjectURL(item.url)
    }
  })
}

function handleUploadChange(e) {
  const files = Array.from(e.target.files || [])
  if (!files.length) return

  const remain = MAX_UPLOAD_IMAGES - formData.value.referenceImage.length
  if (files.length > remain) {
    ElMessage.warning(`最多上传 ${MAX_UPLOAD_IMAGES} 张图片`)
  }

  files.slice(0, remain).forEach((file) => {
    if (!validateImage(file)) return
    formData.value.referenceImage.push(createImageItem(file))
  })
}

function handleRemoveUploadImage(id) {
  const index = formData.value.referenceImage.findIndex((item) => item.id === id)
  if (index === -1) return

  const [item] = formData.value.referenceImage.splice(index, 1)
  if (item.objectUrl) {
    URL.revokeObjectURL(item.url)
  }
}

/* ========================
 * 做同款
 * ======================== */
function makeSameData(sameItem) {
  applyMakeSameData(sameItem)
  ElMessage.success('已为你填充同款参数')
}

defineExpose({ makeSameData })

onMounted(async () => {
  await getNoteTypeList()
  await getplatformList()
  await getImageTypeList()
  await getKnowledgeBaseList()
  // await getNewInstruction(true)
})

onBeforeUnmount(() => {
  releaseReferenceObjectUrls()
})
</script>

<style scoped lang="scss">
$form_item_width: calc(
  (100vw - 20px - var(--left-menu-max-width) - var(--app-content-padding)) * 542 / 1679 - 40px
);

.work_container {
  padding: 10px 20px;
  --el-color-danger: #ff3b30;
  --el-border-radius-base: 8px;
  width: 100%;

  .upload_input {
    display: none;
  }

  // 让 el-form-item 垂直排列
  .el-form-item {
    display: flex;
    flex-direction: column;
    align-items: flex-start;

    :deep(.el-form-item__label) {
      margin-bottom: 10px; // 添加间距
    }

    .el-form-item__content {
      width: 100%;
    }
  }

  .form_title {
    font-family:
      PingFang SC,
      sans-serif;
    font-weight: 400;
    font-size: 17px;
    color: #252632;
  }

  .good_desc_box {
    width: $form_item_width;
    border-radius: 12px;

    .good_desc_input {
      padding: 11px;
      background-color: #f5f6fa;
      border-radius: 12px;

      --el-fill-color-blank: transparent;

      :deep(.el-textarea__inner) {
        resize: none;
        border: none;
        box-shadow: none;
        background-color: transparent;
        margin-bottom: 40px;
        color: #2d2d31;
        scrollbar-color: #cbe4ff transparent;
        scrollbar-width: thin;
      }
    }
  }

  .good_button_list {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    gap: 10px;
    position: absolute;
    top: calc(100% - 32px - 12px);
    left: 10px;
    font-family:
      PingFang SC,
      sans-serif;
    font-weight: 400;
    font-size: 14px;
    line-height: 100%;

    $button_color: #252632;

    .good_button_list_item {
      display: flex;
      align-items: center;
      gap: 7px;
      color: $button_color;
      height: 32px;
      //margin-left: 10px;
      padding: 8px;
      background-color: #fff;
      border-radius: 8px;
      cursor: pointer;
    }

    .knowledge_base {
      min-width: 90px;
      height: 32px;
      background-color: #fff;
      border-radius: 6px;
      gap: 10px;
      padding: 10px 8px;
      display: flex;
      justify-content: center;
      align-items: center;
      cursor: pointer;
    }
  }

  //.good_instruction {
  //  position: absolute;
  //  //top: calc(100% + 20px);
  //  bottom: calc(-20px - 32px);
  //  display: flex;
  //  justify-content: space-between;
  //  width: 500px;
  //
  //  @mixin item_style {
  //    border-radius: 7px;
  //    padding: 5px 14px;
  //    font-family: PingFang SC, sans-serif;
  //    font-weight: 400;
  //    font-size: 15px;
  //    line-height: 150%;
  //    cursor: pointer;
  //  }
  //
  //  .good_instruction_list {
  //    display: flex;
  //    gap: 14px;
  //    margin-right: 26.58px;
  //
  //    .good_instruction_item {
  //      @include item_style;
  //      background-color: #F5F6FA;
  //      color: #7C7D84;
  //    }
  //  }
  //
  //  .good_instruction_getNew {
  //    @include item_style;
  //    min-width: 100px;
  //    max-height: 32px;
  //    background-color: #EDF1FF;
  //    color: #165DFF;
  //    display: flex;
  //    align-items: center;
  //    gap: 10px;
  //  }
  //}
}

.btn_list {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.generate_image {
  width: 266px;
  height: 40px;
  padding: 10px 90px;
  background-color: #3f69ff;
  border-radius: 8px;
  color: #ffffff;
  display: flex;
  justify-content: center;
  align-items: center;
  //position: absolute;
  //bottom: 20px;
  //left: calc(50%);
  //transform: translateX(-50%);
  gap: 4px;
  font-family:
    PingFang SC,
    sans-serif;
  font-weight: 400;
  font-size: 15px;
  letter-spacing: -3%;
  cursor: pointer;

  span {
    white-space: nowrap;
  }
}

.good_button_list_item_tip {
  background-color: #fff;
  width: 420px;
  min-height: 307px;
  border-radius: 12px;
  box-shadow: 0px 0px 2px #00000040;
  padding: 20px;

  color: #252632;
  font-weight: 400;
  font-size: 15px;
  line-height: 150%;
  letter-spacing: 0%;

  .tip_header {
    display: flex;
    justify-content: space-between;

    .tip_header_close_icon {
      cursor: pointer;
      width: 16px;
      height: 16px;
    }
  }

  .tip_content {
    height: 206px;
    width: 100%;
    overflow-y: auto;
    padding-right: 5px;
    margin: 10px 0;

    transition: scrollbar-color 1s cubic-bezier(0.075, 0.82, 0.165, 1);

    scrollbar-width: thin;
    scrollbar-color: #cbe4ff transparent;

    &:hover {
      scrollbar-color: #cbe4ff transparent;
    }

    //&::-webkit-scrollbar {
    //  display: none;
    //}
  }

  .tip_footer {
    display: flex;
    justify-content: space-between;

    .tip_footer_button {
      cursor: pointer;
      font-family: ABeeZee, sans-serif;
    }

    .tip_use {
      width: 180px;
      background-color: #505cff;
      border-radius: 8px;
      padding: 11px 0;
      color: #ffffff;
      font-size: 15px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .tip_reset {
      color: #505cff;
      gap: 6px;
      font-size: 16px;
      display: flex;
      align-items: center;
    }
  }
}

.platform_list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
}

.option_chip_list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.option_chip {
  min-width: 80px;
  height: 31px;
  padding: 5px 20px;
  border: 1px solid #e7e7e7;
  border-radius: 6px;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 500;
  color: #646363;
  cursor: pointer;
  transition: all 0.2s ease;
}

.option_chip_active {
  color: #ffffff;
  background-color: #165dff;
  border-color: transparent;
}

.image_number_list {
  max-width: 462px;
}

.image_setting_panel {
  width: $form_item_width;
  padding: 12px;
  border-radius: 12px;
  background-color: #f5f6fa;
}

.image_setting_controls {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
}

.image_source_panel {
  width: $form_item_width;
}

.image_source_options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.image_source_card {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 44px;
  min-width: 154px;
  padding: 0 14px;
  font-size: 15px;
  color: #646363;
  cursor: pointer;
  background-color: #fff;
  border: 1px solid #e7e7e7;
  border-radius: 8px;
  transition: all 0.2s ease;

  :deep(.el-checkbox) {
    height: 18px;
  }

  :deep(.el-checkbox__label) {
    display: none;
  }
}

.image_source_card_active {
  color: #165dff;
  background-color: #eef3ff;
  border-color: #165dff;
}

.upload_image_box {
  display: flex;
  align-items: center;
  gap: 12px;
  min-height: 118px;
  padding: 12px;
  margin-top: 10px;
  background-color: #f5f6fa;
  border-radius: 12px;
}

.upload_image_action {
  display: flex;
  align-items: center;
  flex: 0 0 auto;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
  width: 94px;
  height: 94px;
  font-size: 14px;
  color: #7c7d84;
  cursor: pointer;
  background-color: #fff;
  border: 1px dashed #cfd7e6;
  border-radius: 8px;
  transition: all 0.2s ease;

  &:hover {
    color: #165dff;
    border-color: #165dff;
  }
}

.uploaded_image_list {
  display: flex;
  gap: 10px;
  min-width: 0;
  padding-bottom: 2px;
  overflow-x: auto;
  scrollbar-width: thin;
  scrollbar-color: #cbe4ff transparent;
}

.uploaded_image_item {
  position: relative;
  width: 94px;
  height: 94px;
  flex: 0 0 auto;
  overflow: hidden;
  background-color: #fff;
  border-radius: 8px;
}

.uploaded_image_preview {
  display: block;
  width: 94px;
  height: 94px;
}

.uploaded_image_delete {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 18px;
  height: 18px;
  padding: 3px;
  cursor: pointer;
  background-color: rgb(255 255 255 / 86%);
  border-radius: 50%;
}

.image_style_select {
  flex-shrink: 0;

  :deep(.el-select__wrapper) {
    background-color: #f5f6fa;
  }
}

.image_ratio_list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.image_ratio_item {
  width: 80px;
  height: 103px;
  border-radius: 12px;
  background-color: #f5f6fa;
  border: 1px solid transparent;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #252632;
  cursor: pointer;
  transition: all 0.2s ease;
}

.image_ratio_item_active {
  border-color: #5b6eff;
  background-color: #eef0ff;
}

.ratio_preview {
  width: 40px;
  height: 40px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ratio_box {
  max-width: 40px;
  max-height: 40px;
  border-radius: 4px;
  background-color: #d3dcff;
}

.ratio_text {
  font-size: 14px;
  line-height: 20px;
}

.platform_item {
  width: 115px;
  height: 50px;
  gap: 10px;
  margin: 0 10px 10px 0;
  border-radius: 6px;
  background-color: #f5f6fa;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;

  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.platform_item_active {
  border: 2px solid #165dff;
}

.note_type {
  :deep(.el-select__wrapper) {
    box-shadow: none;
  }
}

:deep(.el-select__wrapper) {
  border-radius: 6px;
  background-color: #fff;
  box-shadow: none;
}

:deep(.el-select__placeholder) {
  font-family:
    PingFang SC,
    sans-serif;
  font-weight: 400;
  font-style: Regular;
  font-size: 14px;
  leading-trim: NONE;
  line-height: 100%;
  letter-spacing: 0%;
  color: #252632;
}
</style>
