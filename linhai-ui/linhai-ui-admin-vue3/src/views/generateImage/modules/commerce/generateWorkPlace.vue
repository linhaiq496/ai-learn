<template>
  <div class="work_container">
    <!-- 隐藏上传 input -->
    <input
        ref="upload_ref"
        type="file"
        multiple
        accept="image/jpeg,image/png,image/jpg"
        style="display: none"
        @change="handleUploadChange"
    />

    <el-form :model="formData" :rules="rules" ref="formRef">
      <!-- 商品图片 -->
      <el-form-item prop="images">
        <template #label>
          <div class="form_title">商品图片</div>
        </template>

        <div class="upload-img-box">
          <!-- 上传按钮 -->
          <div
              class="upload-img-box-content"
              @click="uploadImages(1)"
          >
            <img src="../../../../assets/auto_hosting_img/add.png"/>
            <span>上传图片</span>
          </div>

          <!-- 已上传图片 -->
          <div class="uploaded-images-container">
            <div
                v-for="(item,index) in formData.images"
                :key="item.id"
                class="uploaded-image-item"
            >
              <el-image
                  class="uploaded-image"
                  fit="cover"
                  :src="item.url"
                  :preview-src-list="previewList"
                  :initial-index="index"
              />

              <img
                  class="delete-icon"
                  src="../../../assets/v3Img/aiVideo_img/img_v_02.png"
                  @click="handleRemove(item.id)"
              />
            </div>
          </div>
        </div>
      </el-form-item>

      <!-- 商品描述 -->
      <el-form-item required>
        <template #label>
          <div class="form_title">商品描述</div>
        </template>
        <template #default>
          <div class="good_desc_box">
            <el-input v-model="formData.goodDescription" placeholder="请输入你的创作灵感"
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
            <el-tooltip placement="bottom-start" effect="light" transition="el-fade-in-linear"
                        :visible="promptTip" :trigger="'click'">
              <div>
                <div class="good_button_list_item" v-show="promptData.status">
                  <img src="../../../../assets/auto_hosting_img/prompt2.png" class="w-20px h-20px"/>
                  <span>优化中...</span>
                </div>
                <div @click="fixPrompt" class="good_button_list_item"
                     v-show="!promptData.status">
                  <img src="../../../../assets/auto_hosting_img/prompt1.png" class="w-20px h-20px"/>
                  <span>提示词优化</span>
                </div>
              </div>
              <template #content>
                <div class="good_button_list_item_tip">
                  <div class="tip_header">
                    <div class="tip_header_title">
                      提示词优化
                    </div>
                    <div class="tip_header_close_icon" @click="promptTip = false">
                      <img src="../../../../assets/auto_hosting_img/close.png" class="w-16px h-16px"/>
                    </div>
                  </div>
                  <div class="tip_content">
                    {{ promptData.prompt }}
                  </div>
                  <div class="tip_footer">
                    <div class="tip_footer_button tip_reset" @click="fixPrompt">
                      <img src="../../../../assets/auto_hosting_img/reset.png" class="w-17px h-17px"/>
                      <span>重新生成</span>
                    </div>
                    <div class="tip_footer_button tip_use"
                         @click="formData.goodDescription = promptData.prompt; promptTip=false">确定使用
                    </div>
                  </div>
                </div>
              </template>
            </el-tooltip>

          </div>
          <div class="good_instruction">
            <div class="good_instruction_list">
              <div v-for="item in exampleDescList" class="good_instruction_item" :key="item.id"
                   @click="formData.goodDescription = item.desc">
                {{ item.abstract }}
              </div>

            </div>
            <div class="good_instruction_getNew" @click="getNewInstruction">

              <el-icon :size="16" color="#165DFF">
                <Refresh/>
              </el-icon>
              <span>换一换</span>
            </div>
          </div>
        </template>
      </el-form-item>
      <!--   mt-14占位   -->
      <div class="mt-12"></div>
      <!--  图片数量    -->
      <el-form-item required>
        <template #label>
          <div class="form_title">图片数量
            {{
              (formData.referenceImages.featuresImages + formData.referenceImages.sceneImages + formData.referenceImages.sellingPointImages + formData.referenceImages.productComparisonChartCount + formData.referenceImages.sellingPointImageCount) === 0
                  ? '' : `（${formData.referenceImages.featuresImages + formData.referenceImages.sceneImages + formData.referenceImages.sellingPointImages + formData.referenceImages.productComparisonChartCount + formData.referenceImages.sellingPointImageCount}张）`
            }}
          </div>
        </template>
        <div class="good_button_list2">
          <!--     场景图      -->
          <el-select
              v-model="formData.referenceImages.sceneImages"
              :style="{width:'95px',height: '32px'}"
          >
            <template #label>
              <div
                  class="reference_image"
                  :style="{color: '#252632'}"
              >
                {{ `场景图${formData.referenceImages.sceneImages}张` }}
              </div>
            </template>

            <el-option
                v-for="n in 10"
                :key="n-1"
                :label="String(n-1)"
                :value="Number(n-1)"
            />
          </el-select>
          <!--    特写图      -->
          <el-select
              v-model="formData.referenceImages.featuresImages"
              :style="{width:'95px',height: '32px'}"
          >
            <template #label>
              <div
                  class="reference_image"
                  :style="{color: '#252632'}"
              >
                {{ `特写图${formData.referenceImages.featuresImages}张` }}
              </div>
            </template>
            <el-option
                v-for="n in 10"
                :key="n-1"
                :label="String(n-1)"
                :value="Number(n-1)"
            />
          </el-select>
          <!--     海报图      -->
          <el-select
              v-model="formData.referenceImages.sellingPointImages"
              :style="{width:'95px',height: '32px'}"
          >
            <template #label>
              <div
                  class="reference_image"
                  :style="{color: '#252632'}"
              >
                {{ `海报图${formData.referenceImages.sellingPointImages}张` }}
              </div>
            </template>

            <el-option
                v-for="n in 10"
                :key="n-1"
                :label="String(n-1)"
                :value="Number(n-1)"
            />
          </el-select>
          <!--     卖点图      -->
          <el-select
              v-model="formData.referenceImages.sellingPointImageCount"
              :style="{width:'95px',height: '32px'}"
          >
            <template #label>
              <div
                  class="reference_image"
                  :style="{color: '#252632'}"
              >
                {{ `卖点图${formData.referenceImages.sellingPointImageCount}张` }}
              </div>
            </template>

            <el-option
                v-for="n in 10"
                :key="n-1"
                :label="String(n-1)"
                :value="Number(n-1)"
            />
          </el-select>
          <!--     对比图     -->
          <el-select
              v-model="formData.referenceImages.productComparisonChartCount"
              :style="{width:'95px',height: '32px'}"
          >
            <template #label>
              <div
                  class="reference_image"
                  :style="{color: '#252632'}"
              >
                {{ `对比图${formData.referenceImages.productComparisonChartCount}张` }}
              </div>
            </template>

            <el-option
                v-for="n in 10"
                :key="n-1"
                :label="String(n-1)"
                :value="Number(n-1)"
            />
          </el-select>
        </div>
      </el-form-item>
      <!-- 图片比例 -->
      <el-form-item required>
        <template #label>
          <div class="form_title">图片比例</div>
        </template>
        <div class="image_ratio_list">
          <div
              class="image_ratio_item"
              v-for="item in ratioStyles"
              :key="item.label"
              :class="{ active: formData.imageRatio === item.label }"
              @click="formData.imageRatio = item.label"
          >
            <!-- 固定高度的预览容器 -->
            <div class="ratio-preview">
              <div
                  class="ratio-box"
                  :style="item.style"
              ></div>
            </div>

            <!-- 文本永远在同一行 -->
            <div class="ratio-text">{{ item.label }}</div>
          </div>
        </div>

      </el-form-item>
    </el-form>

  </div>

  <div class="btn_list">
    <div class="generate_image" @click="generateImage" v-loading="isLoading">
      <img src="../../../../assets/auto_hosting_img/sendGeneraet.svg" style="width: 20px"/>
      <span>生成图片</span>
    </div>
  </div>
</template>

<script setup>
import {ref, computed, onBeforeUnmount, reactive, onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {Refresh} from '@element-plus/icons-vue'
import {
  getPrompt,
  createImageTask,
  uploadImage,
  getHotPage
} from '@/api/generateImage'
import {getUUIDv4} from "@/utils/uuid"

const promptTip = ref(false)
const _emits = defineEmits(['createImage'])

/* ========================
 * refs
 * ======================== */
const upload_ref = ref(null)
const upload_type = ref(1)
const formRef = ref(null)

/* ========================
 * 常量
 * ======================== */
const MAX_IMAGES = 1
const ALLOW_TYPES = ['image/jpeg', 'image/png', 'image/jpg']
const MAX_SIZE = 10 * 1024 * 1024 // 10MB

const ratioList = [
  {label: '1:1', w: 1, h: 1},
  {label: '16:9', w: 16, h: 9},
  {label: '9:16', w: 9, h: 16},
]

const ratioStyles = ratioList.map(item => {
  const isLandscape = item.w >= item.h
  return {
    ...item,
    style: {
      aspectRatio: `${item.w} / ${item.h}`,
      width: isLandscape ? '40px' : 'auto',
      height: isLandscape ? 'auto' : '40px',
      maxWidth: '40px',
      maxHeight: '40px',
    },
  }
})

// 示例描述
const exampleDescList = ref([
  {id: "11", desc: "家电商品卖点描述家电商品卖点描述", abstract: '家电商品卖点描述'},
  {id: "122", desc: "电子类产品的卖点描述文本12312312", abstract: '电子类产品的卖点描述文本'}
])

const exampleSearchVo = reactive({
  pageNo: 0,
  pageSize: 2,
  total: 100,
})

/* ========================
 * 表单数据
 * ======================== */
const formData = ref({
  images: [], // 商品图片
  goodDescription: '',
  imgNumber: 1,
  referenceImages: {
    featuresImages: 1,
    sceneImages: 1,
    sellingPointImages: 1,
    productComparisonChartCount: 1,
    sellingPointImageCount: 1,
  },
  imageRatio: '1:1',
})

// 提示词优化
const promptData = reactive({
  prompt: '',
  status: 0,
})

const isLoading = ref(false)

/* ========================
 * 校验
 * ======================== */
const rules = {
  images: [{required: true, message: '请上传商品图片', trigger: 'change'}],
  goodDescription: [{required: true, message: '请输入商品描述', trigger: 'blur'}],
}

/* ========================
 * 计算属性
 * ======================== */
const previewList = computed(() =>
    formData.value.images.map(i => i.url)
)

/* ========================
 * 统一图片上传（核心）
 * ======================== */
async function ensureImageUrls() {
  const images = formData.value.images
  if (!images.length) return []

  const needUpload = images.filter(i => !i.uploaded && i.file)

  if (needUpload.length) {
    const res = await uploadImage(needUpload.map(i => i.file))
    const urls = res.urls || []

    urls.forEach((url, index) => {
      needUpload[index].url = url
      needUpload[index].uploaded = true
      delete needUpload[index].file
    })
  }

  return images.map(i => i.url).filter(Boolean)
}

/* ========================
 * 提示词优化
 * ======================== */
async function fixPrompt() {
  promptTip.value = false

  if (!formData.value.goodDescription) {
    return ElMessage.warning('请输入商品描述')
  }
  if (!formData.value.images.length) {
    return ElMessage.warning('请上传商品图片')
  }
  if (promptData.status) {
    return ElMessage.warning('正在优化中，请稍后')
  }

  promptData.status = 1
  try {
    const imageUrls = await ensureImageUrls()
    const res = await getPrompt({
      prompt: formData.value.goodDescription,
      imageUrls,
    })
    promptData.prompt = res.choices[0].message.content[0].text
    promptTip.value = true
  } catch {
    ElMessage.warning('提示词优化失败，请稍后重试')
  } finally {
    promptData.status = 0
  }
}

/* ========================
 * 上传入口
 * ======================== */
function uploadImages(type) {
  if (type === 1 && formData.value.images.length >= MAX_IMAGES) {
    ElMessage.warning(`最多只能上传 ${MAX_IMAGES} 张图片`)
    return
  }
  upload_type.value = type
  upload_ref.value.value = ''
  upload_ref.value.click()
}

/* ========================
 * 图片校验
 * ======================== */
function validateImage(file) {
  if (!ALLOW_TYPES.includes(file.type)) {
    ElMessage.error('仅支持 JPG / PNG / JPEG 格式图片')
    return false
  }
  if (file.size > MAX_SIZE) {
    ElMessage.error('图片大小不能超过 10MB')
    return false
  }
  return true
}

/* ========================
 * 创建图片对象
 * ======================== */
function createImageItem(file) {
  return {
    id: getUUIDv4(),
    file,
    url: URL.createObjectURL(file),
    uploaded: false,
  }
}

/* ========================
 * 上传处理
 * ======================== */
function handleUploadChange(e) {
  const files = Array.from(e.target.files)
  if (!files.length) return

  const remain = MAX_IMAGES - formData.value.images.length
  files.slice(0, remain).forEach(file => {
    if (!validateImage(file)) return
    formData.value.images.push(createImageItem(file))
  })
}

/* ========================
 * 删除图片
 * ======================== */
function handleRemove(id) {
  const index = formData.value.images.findIndex(i => i.id === id)
  if (index === -1) return

  URL.revokeObjectURL(formData.value.images[index].url)
  formData.value.images.splice(index, 1)
}

/* ========================
 * 生成图片
 * ======================== */
async function generateImage() {
  if (isLoading.value) return
  isLoading.value = true

  try {
    await formRef.value.validate()

    const r = formData.value.referenceImages
    if (!r.sceneImages && !r.featuresImages && !r.sellingPointImages && !r.sellingPointImageCount && !r.productComparisonChartCount) {
      return ElMessage.warning('特写图、场景图、海报图、卖点图、对比图需至少选择一张！')
    }

    const fileUrls = await ensureImageUrls()

    const params = {
      prompt: formData.value.goodDescription,
      fileUrls,
      sceneImageCount: r.sceneImages,
      closeUpImageCount: r.featuresImages,
      posterImageCount: r.sellingPointImages,
      sellingPointImageCount: r.sellingPointImageCount,
      productComparisonChartCount: r.productComparisonChartCount,
      ratio: formData.value.imageRatio,
    }

    const res = await createImageTask(params)
    formData.value = {
      images: [], // 商品图片
      goodDescription: '',
      imgNumber: 1,
      referenceImages: {
        featuresImages: 1, //特写图
        sceneImages: 1, //场景图
        sellingPointImages: 1, //海报图
        productComparisonChartCount: 1, // 对比图
        sellingPointImageCount: 1, // 卖点图数量
      },
      imageRatio: '1:1',
    }

    _emits('createImage', res)
  } finally {
    isLoading.value = false
  }
}

/* ========================
 * 示例文案
 * ======================== */
async function getNewInstruction() {
  try {
    exampleSearchVo.pageNo++
    const res = await getHotPage(exampleSearchVo)
    if (res.total && res.list.length) {
      exampleDescList.value = res.list.slice(0, 2).map(item => ({
        id: item.id,
        desc: item.prompt,
        abstract: item.title.split('：')[1],
      }))
    }
  } catch {
  }
}

/* ========================
 * 做同款
 * ======================== */
function makeSameData(sameItem) {
  formData.value.goodDescription = sameItem.finalPrompt
  formData.value.images = sameItem.productImageUrl.map(url => ({
    id: getUUIDv4(),
    url,
    uploaded: true,
  }))
}

defineExpose({makeSameData})

onMounted(getNewInstruction)

onBeforeUnmount(() => {
  formData.value.images.forEach(i => URL.revokeObjectURL(i.url))
})
</script>


<style scoped lang="scss">
.work_container {
  padding: 10px 20px;
  --el-color-danger: #FF3B30;
  --el-border-radius-base: 8px;
  width: 100%;
  $form_item_width: 500px;
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
    font-family: PingFang SC, sans-serif;
    font-weight: 400;
    font-size: 17px;
    color: #252632;
  }

  .upload-img-box {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    width: $form_item_width;
    height: 160px;
    padding-left: 24px;
    border-radius: 12px;
    background-color: #F5F6FA;
    position: relative;

    .el-upload__span {
      font-family: PingFang SC;
      font-weight: 400;
      font-size: 14px;
    }

    .upload-img-box-content {
      background-color: #ffffff;
      border-radius: 12px;
      height: 120px;
      width: 120px;
      display: flex;
      justify-content: center;
      align-items: center;
      flex-direction: column;
      cursor: pointer;
      position: relative;

      img {
        height: 36px;
        width: 36px;
      }

      span {
        font-family: PingFang SC;
        font-weight: 400;
        font-size: 14px;
        color: #B6C1D6;
      }

      &::after {
        $line_height: 94%;
        display: flex;
        content: "";
        height: $line_height;
        border: 1px dashed #D3D4D6;
        position: absolute;
        top: calc((100% - $line_height) / 2);
        right: -15px;
      }

    }

    :deep(.el-upload--picture-card) {
      width: 120px;
      height: 120px;
      display: flex;
      justify-content: center;
      align-items: center
    }

    .uploaded-images-container {
      width: calc(100% - 170px);
      //height: 120px;
      height: auto;
      display: flex;
      flex-wrap: nowrap;
      overflow-y: auto;
      position: absolute;
      left: 145px;
      bottom: 8px;

      scrollbar-width: thin;
      scrollbar-color: rgba(255, 255, 255, 0.8) transparent;

      &:hover {
        scrollbar-color: rgba(255, 255, 255, 0.8) transparent;
      }

      .cover-badge {
        position: absolute;
        bottom: 13px;
        right: 1px;
        background-color: #FF8000;
        border-radius: 4px;
        padding: 3px 6px;
        z-index: 2;
        width: 36px;
        height: 18px;
        font-weight: 400;
        font-size: 12px;
        font-family: PingFang SC, sans-serif;
        color: #FFFFFF;
        display: flex;
        justify-content: center;
        align-items: center
      }
    }

    .uploaded-image-item {
      position: relative;
      margin: 0 0 0 10px;


      &:hover img {
        display: block;
      }

      img {
        display: none;
        position: absolute;
        top: 50%;
        left: 50%;
        width: 24px;
        height: 24px;
        opacity: 0.7;
        transform: translate(-50%, -50%);
        cursor: pointer;
      }
    }

    .uploaded-image {
      //width: 90px;
      max-width: 120px;
      min-width: 76px;
      height: 120px;
      border-radius: 6px;
      box-sizing: content-box;
      object-fit: cover;
      border: 1px solid white;
    }

    .image-slot {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 100%;
      height: 100%;
      background: #f5f7fa;
      color: #909399;
    }

    .delete-button {
      width: 24px;
      height: 24px;
      border-radius: 22px;
      background-color: rgba(255, 255, 255, 0.99);
      display: none;
      position: absolute;
      top: 50%;
      left: 50%;
      opacity: 0.5;
      transform: translate(-50%, -50%);
      background-position: center center;
    }

    .uploaded-image-item:hover .delete-button {
      display: block;
    }

    .el-icon-delete-2 {
      display: flex;
      position: absolute;
      top: 20%;
      left: 20%;
    }

  }

  .good_desc_box {
    width: $form_item_width;
    border-radius: 12px;


    .good_desc_input {
      padding: 11px;
      background-color: #F5F6FA;
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
    font-family: PingFang SC, sans-serif;
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

    .reference_image {
      display: flex;
      align-items: center;
      gap: 6px;
      width: 20px;
    }
  }

  .good_instruction {
    position: absolute;
    top: calc(100% + 7px);
    display: flex;
    justify-content: space-between;
    width: 500px;

    @mixin item_style {
      border-radius: 7px;
      padding: 5px 14px;
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-size: 15px;
      line-height: 150%;
      cursor: pointer;
    }

    .good_instruction_list {
      display: flex;
      gap: 14px;
      margin-right: 26.58px;

      .good_instruction_item {
        @include item_style;
        background-color: #F5F6FA;
        color: #7C7D84;
      }
    }

    .good_instruction_getNew {
      @include item_style;
      min-width: 100px;
      max-height: 32px;
      background-color: #EDF1FF;
      color: #165DFF;
      display: flex;
      align-items: center;
      gap: 10px;
    }
  }


  .image_ratio_list {
    display: flex;
    gap: 20px;
  }

  .image_ratio_item {
    width: 80px;
    height: 103px;
    background-color: #F5F6FA;
    border-radius: 12px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    color: #252632;

    &.active {
      border: 2px solid #5B6EFF;
      background-color: #EEF0FF;
    }
  }

  .ratio-box {
    max-width: 40px;
    max-height: 40px;
    background-color: #D3DCFF;
    border-radius: 4px;
  }

  .ratio-preview {
    height: 40px;
    width: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 8px;
  }

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
  background-color: #3F69FF;
  border-radius: 8px;
  color: #FFFFFF;
  display: flex;
  justify-content: center;
  align-items: center;
  //position: absolute;
  //bottom: 20px;
  //left: calc(50%);
  //transform: translateX(-50%);
  gap: 4px;
  font-family: PingFang SC, sans-serif;
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
      background-color: #505CFF;
      border-radius: 8px;
      padding: 11px 0;
      color: #FFFFFF;
      font-size: 15px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .tip_reset {
      color: #505CFF;
      gap: 6px;
      font-size: 16px;
      display: flex;
      align-items: center;
    }
  }
}
</style>
<style>
.el-popper {
  padding: 0;
  border: none !important;
}
</style>
<style lang="scss" scoped>
.el-form-item--default {
  margin-bottom: 10px;
}

//图片数量 样式
.good_button_list2 {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 6px;
  font-family: PingFang SC, sans-serif;
  font-weight: 400;
  font-size: 14px;
  line-height: 100%;

  $button_color: #252632;

  :deep(.el-select__placeholder) {
    color: $button_color;
    font-family: PingFang SC, sans-serif;
    font-weight: 400;
    font-size: 15px;
    line-height: 100%;
  }

  :deep(.el-select__wrapper) {
    box-shadow: none;
    padding: 5px 6px;
    gap: 0;
    border: 1px solid #F2F3F5;
  }

  :deep(.reference_image) {
    display: flex;
    align-items: center;
    gap: 6px;
    font-family: PingFang SC, sans-serif;
    font-weight: 400;
    font-size: 14px;
    line-height: 100%;
  }

  :deep(.el-select__suffix) {
    //display: none;
  }

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
}


</style>
