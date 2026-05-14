<template>
  <div class="work_container">
    <el-form :model="formData" :rules="rules" ref="formRef">
      <!-- 商品图片 -->
      <el-form-item required>
        <template #label>
          <div class="form_title">笔记题材</div>
        </template>
        <template #default>
          <el-tooltip placement="right-start" effect="light" transition="el-fade-in-linear"
                      :visible="noteTypeVisible" :trigger="'click'"
                      popper-class="popper-rounded8">

            <div class="note_type_item" @click="noteTypeVisible = !noteTypeVisible">
              <div class="note_type_item_left">
                <div
                  class="w-48px h-48px flex items-center justify-center rounded-[8px] bg-[#FFFFFF]">
                  <div :class="formData.noteType.cssClass" class="w-20px h-20px"></div>
                </div>
                <div class="note_type_item_left_content">
                  <div class="note_type_item_left_title">{{ formData.noteType.label }}</div>
                  <div class="note_type_item_left_desc">{{ formData.noteType.remark }}</div>
                </div>
              </div>
              <div class="note_type_item_right flex justifg-center">
                <el-icon :size="20" color="#BCBCBC">
                  <ArrowRight/>
                </el-icon>
              </div>
            </div>
            <template #content>
              <div class="note_list" @mouseleave="noteTypeVisible = false">
                <div v-for="item in noteTypeList" class="note_type_item"
                     :class="{active_item: formData.noteType.id === item.id}" :key="item.id"
                     @click="formData.noteType = item">
                  <div class="note_type_item_left">
                    <div
                      class="w-48px h-48px flex items-center justify-center rounded-[8px] bg-[#FFFFFF]">
                      <div :class="item.cssClass" class="w-20px h-20px"></div>
                    </div>
                    <div class="note_type_item_left_content">
                      <div class="note_type_item_left_title">{{ item.label }}</div>
                      <div class="note_type_item_left_desc">{{ item.remark }}</div>
                    </div>
                  </div>
                  <div class="note_type_item_right flex justifg-center">
                    <el-icon :size="20" color="#BCBCBC">
                      <ArrowRight/>
                    </el-icon>
                  </div>
                </div>
              </div>
            </template>
          </el-tooltip>
        </template>

      </el-form-item>

      <!-- 文本描述 -->
      <el-form-item required>
        <template #label>
          <div class="form_title">文本描述</div>
        </template>
        <template #default>
          <div class="good_desc_box">
            <el-input v-model="formData.goodDescription" placeholder="写下你的创作灵感"
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
                        :visible="promptTip" :trigger="'click'"
                        popper-class="popper-rounded12"
            >
              <div>
                <div class="good_button_list_item" v-show="promptData.status">
                  <img src="@/assets/geoImage/auto_hosting_img/prompt2.png" class="w-20px h-20px"/>
                  <span>优化中...</span>
                </div>
                <div @click="fixPrompt" class="good_button_list_item"
                     v-show="!promptData.status">
                  <img src="@/assets/geoImage/auto_hosting_img/prompt1.png" class="w-20px h-20px"/>
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
                      <img src="@/assets/geoImage/auto_hosting_img/close.png"
                           class="w-16px h-16px"/>
                    </div>
                  </div>
                  <div class="tip_content">
                    {{ promptData.prompt }}
                  </div>
                  <div class="tip_footer">
                    <div class="tip_footer_button tip_reset" @click="fixPrompt">
                      <img src="@/assets/geoImage/auto_hosting_img/reset.png"
                           class="w-17px h-17px"/>
                      <span>重新生成</span>
                    </div>
                    <div class="tip_footer_button tip_use"
                         @click="formData.goodDescription = promptData.prompt; promptTip=false">确定使用
                    </div>
                  </div>
                </div>
              </template>
            </el-tooltip>
            <div class="knowledge_base">
              <img src="@/assets/geoImage/knowledge.png" class="w-20px h-20px"/>
              <span>
                知识库
              </span>
            </div>
            <div class="article_list">
              <el-select v-model="formData.articleNumber" placeholder="请选择平台格式"
                         style="width: 71px">
                <el-option :label="item + '个'" :value="item" v-for="item in 9" :key="item"/>
              </el-select>
            </div>
          </div>
          <div class="good_instruction">
            <div class="good_instruction_list">
              <div v-for="item in exampleDescList" class="good_instruction_item" :key="item.id"
                   @click="formData.goodDescription = item.desc">
                {{ item.abstract }}
              </div>

            </div>
            <div class="good_instruction_getNew" @click="getNewInstruction(true)">

              <el-icon :size="16" color="#165DFF">
                <Refresh/>
              </el-icon>
              <span>换一换</span>
            </div>
          </div>
        </template>
      </el-form-item>
      <!--   mt-14占位   -->
      <div class="mt-17"></div>
      <!-- 参考图数量 -->
      <el-form-item required>
        <template #label>
          <div class="form_title">文本长度</div>
        </template>
        <template #default>
          <div
            class="text-15px font-500 color-#646363 min-w-80px h-31px cursor-pointer border border-solid border-#E7E7E7 px-20px py-5px flex justify-center items-center rounded-6px mr-6px"
            v-for="item in [100,300,500,1000]" :key="item"
            :class="[formData.articleLength === item ? 'color-#FFFFFF! bg-#165DFF border-transparent':'']"
            @click="formData.articleLength= item">
            {{ item + '字' }}
          </div>
        </template>
      </el-form-item>
      <el-form-item required>
        <template #label>
          <div class="form_title">平台格式</div>
        </template>
        <template #default>
          <div class="platform_list">
            <div v-for="item in platformList" :key="item.value" class="platform_item"
                 :class="{platform_item_active: item.value === formData.platformType.value}"
                 @click="formData.platformType = item">
              <div :class="item.cssClass" class="w-24px h-24px"></div>
              <span>{{ item.label }}</span>
            </div>
          </div>
        </template>

      </el-form-item>
    </el-form>

  </div>

  <div class="btn_list">
    <div class="generate_image" @click="generateArticle" v-loading="isLoading">
      <img src="@/assets/geoImage/auto_hosting_img/sendGeneraet.svg" style="width: 20px"/>
      <span>立即生成</span>
    </div>
  </div>
</template>

<script setup>
import {ref, computed, onBeforeUnmount, reactive, onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {Refresh, ArrowRight} from '@element-plus/icons-vue'
import {
  getPrompt,
  getHotPage,
  createArticleTask,
} from '@/api/geo/generateArticle'
import * as DictDataApi from "@/api/system/dict/dict.data";
import {useDictStore} from "@/store/modules/dict";

const promptTip = ref(false)
const noteTypeVisible = ref(false)
const _emits = defineEmits(['createImage'])
const useDictStoreRef = useDictStore()

/* ========================
 * refs
 * ======================== */
const upload_type = ref(1)
const formRef = ref(null)


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

const noteTypeList = ref([])
const platformList = ref([])

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
  goodDescription: [{required: true, message: '请输入文本描述', trigger: 'blur'}],
}


/* ========================
 * 笔记类型（从字典获取）
 * ======================== */
async function getNoteTypeList() {
  try {
    const res = await DictDataApi.getDictDataPage({dictType: 'good_note_type'})
    if (res.list && res.list.length) {
      noteTypeList.value = res.list
      formData.value.noteType = res.list[0] || {}
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
    const res = await useDictStoreRef.getDictByType('platform_type') || [];
    if (res && res.length) {
      platformList.value = res
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
      prompt: formData.value.goodDescription,
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

// 立即生成
async function generateArticle() {
// 1、必填项过滤
  const ispassed = RequiredFiltering()
  if (!ispassed) {
    return
  } else {
    isLoading.value = true
  }
  try {
    // 映射表单数据到后端接口参数
    const params = {
      noteSubject: formData.value.noteType?.label || '', // 笔记题材
      textDescription: formData.value.goodDescription || '', // 文本描述
      articleCount: formData.value.articleNumber || 1, // 文章数量
      articleLength: formData.value.articleLength, // 文章长度（可选）
      // articleLength: undefined, // 文章长度（可选）
      knowledgeId: formData.value.knowledgeBase?.id || undefined, // 知识库Id
      platformFormat: formData.value.platformType?.value || '', // 平台格式
    }
    const res = await createArticleTask(params)
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
  } else if (!formData.value.noteType) {
    ElMessage.warning('请选择笔记类型')
    return false
  } else if (!formData.value.platformType) {
    ElMessage.warning('请选择平台类型')
    return false
  } else if (!formData.value.knowledgeBase) {
    ElMessage.warning('请选择知识库')
    return false
  } else if (!formData.value.articleNumber) {
    ElMessage.warning('请选择生成文章数量')
    return false
  }
  return true
}

/* ========================
 * 示例文案
 * ======================== */
async function getNewInstruction(isOnce = false) {
  try {
    if (!isOnce) {
      exampleSearchVo.pageNo = 1
    } else {
      const totalPages = Math.ceil(exampleSearchVo.total / exampleSearchVo.pageSize)
      const randomPage = Math.floor(Math.random() * totalPages) + 1
      exampleSearchVo.pageNo = Math.max(1, Math.min(randomPage, totalPages))
    }
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
  ElMessage.success('正在生成图片，请稍后')
  // formData.value.goodDescription = sameItem.finalPrompt
  // formData.value.images = sameItem.productImageUrl.map(url => ({
  //   id: getUUIDv4(),
  //   url,
  //   uploaded: true,
  // }))
}

defineExpose({makeSameData})

onMounted(async () => {
  await getNoteTypeList()
  await getplatformList()
  await getNewInstruction(true)
})
</script>


<style scoped lang="scss">
$form_item_width: calc((100vw - 20px - var(--left-menu-max-width) - var(--app-content-padding)) * 542 / 1679 - 40px);

.work_container {
  padding: 10px 20px;
  --el-color-danger: #FF3B30;
  --el-border-radius-base: 8px;
  width: 100%;
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

    .knowledge_base {
      width: 90px;
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

    .article_list {
      :deep(.el-select__wrapper) {
        border-radius: 6px;
        background-color: #fff;
        box-shadow: none;

        .el-select__placeholder {
          font-family: PingFang SC, sans-serif;
          font-weight: 400;
          font-style: Regular;
          font-size: 14px;
          leading-trim: NONE;
          line-height: 100%;
          letter-spacing: 0%;
          color: #252632;
        }
      }
    }
  }

  .good_instruction {
    position: absolute;
    //top: calc(100% + 20px);
    bottom: calc(-20px - 32px);
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

.platform_list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
}

.platform_item {
  width: 115px;
  height: 50px;
  gap: 10px;
  margin: 0 10px 10px 0;
  border-radius: 6px;
  background-color: #F5F6FA;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;

  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.platform_item_active {
  border: 2px solid #165DFF;
}

.note_type_item {
  width: $form_item_width;
  height: 76px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  background-color: #F5F6FA;
  cursor: pointer;

  .note_type_item_left {
    display: flex;
    gap: 14px;

    .note_type_item_left_content {
      display: flex;
      gap: 4px;
      flex-direction: column;
      justify-content: center;

      .note_type_item_left_title {
        font-family: PingFang SC, sans-serif;
        font-weight: 500;
        font-size: 15px;
        line-height: 100%;
        color: #252632;
      }

      .note_type_item_left_desc {
        font-family: PingFang SC, sans-serif;
        font-weight: 400;
        font-size: 13px;
        line-height: 100%;
        letter-spacing: 0%;
        color: #7C7D84;
      }
    }
  }
}

.note_list {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;

  .note_type_item {
    max-width: 380px;
    border: 2px solid transparent;
    box-sizing: content-box;
    height: calc(76px - 14px * 2);
  }

  .active_item {
    border: 2px solid #165DFF;
    position: relative;

    &::after {
      content: "✔";
      position: absolute;
      bottom: 0;
      right: 0;
      width: 24px;
      height: 24px;
      border-radius: 12px 0 6px 0;
      display: flex;
      justify-content: center;
      align-items: center;
      background-color: #165DFF;
      color: #FFFFFF;
    }
  }
}
</style>

