<script setup>
import {ref, reactive, nextTick} from 'vue'
import {deleteArticle, getArticlePage, updateArticleContent} from '@/api/geo/productTerm'
import richEditor from '@/components/GeoView/RichTextEditor.vue'
import AccountSelectDialog from '@/components/GeoView/AccountSelectDialog.vue'
import {uploadFile} from "@/api/geo/uploadImage"
import {formatToDateTime} from "@/utils/dateUtil"
import {createPublishTaskLogs} from "@/api/geo/publishTask"
import {ElMessage, ElMessageBox} from 'element-plus'

// --- 响应式状态 ---
const AccountSelectDialogRef = ref(null)
const dialogTableVisible = ref(false)
const uploadRef = ref(null)

const planTitle = ref('')
const planList = ref([])
const activeArticleId = ref('')

// 默认空文章结构
const emptyArticle = {
  id: '',
  title: '',
  content: '',
  summary: '',
  coverImageUrl: ''
}

const currentArticle = ref({...emptyArticle})

const planSearchVo = reactive({
  pageNo: 1,
  pageSize: 20,
  planId: '',
  title: ''
})

/**
 * 1. 开启弹窗
 * 逻辑：重置所有旧状态 -> 开启弹窗 -> 加载列表
 */
async function openDialog(planId, title) {
  // 重置状态防止残留
  activeArticleId.value = ''
  currentArticle.value = {...emptyArticle}
  planList.value = []

  // 初始化参数
  planSearchVo.planId = planId
  planTitle.value = title
  dialogTableVisible.value = true

  await initPlan()
}

/**
 * 2. 初始化计划列表
 */
async function initPlan() {
  try {
    const res = await getArticlePage(planSearchVo)
    const list = res.list || []

    if (list.length === 0) {
      ElMessage.warning('当前任务暂无数据')
      dialogTableVisible.value = false
      return
    }

    planList.value = list
    // 自动选中第一篇，传入 skipSave=true 避免初次加载触发“正在保存”提示
    await changeArticle(list[0], true)
  } catch (e) {
    console.error('初始化文章失败:', e)
  }
}

/**
 * 3. 切换文章
 * @param item 选中的文章对象
 * @param skipSave 是否跳过保存（初始化时为 true）
 */
async function changeArticle(item, skipSave = false) {
  if (!item || item.id === activeArticleId.value) return

  // a. 切换前：如果有旧文章，先保存当前更改
  if (!skipSave && activeArticleId.value) {
    // 同步本地 planList 缓存，让左侧列表实时显示编辑后的标题
    const oldIndex = planList.value.findIndex(o => o.id === activeArticleId.value)
    if (oldIndex !== -1) {
      planList.value[oldIndex] = {...currentArticle.value}
    }
    // 静默保存到后端
    await handleKeepArticle(true)
  }

  // b. 切换后：加载新文章数据
  activeArticleId.value = item.id
  currentArticle.value = {
    id: item.id,
    title: item.title || '',
    content: item.content || '',
    summary: item.summary || '',
    coverImageUrl: item.coverImageUrl || ''
  }
}

/**
 * 4. 保存文章
 * @param silent 是否静默保存（切换文章时为 true，点击保存按钮时为 false）
 */
async function handleKeepArticle(silent = false) {
  if (!currentArticle.value.id) return

  if (!silent) ElMessage.info('正在保存...')

  try {
    const bodyParams = {
      ...currentArticle.value,
      planId: planSearchVo.planId
    }
    const res = await updateArticleContent(bodyParams)
    if (res) {
      if (!silent) {
        ElMessage.closeAll()
        ElMessage.success('保存成功')
      }
    }
  } catch (e) {
    console.error('保存失败:', e)
    if (!silent) ElMessage.error('保存失败')
  }
}

/**
 * 5. 封面上传相关
 */
function changeCoverImage() {
  uploadRef.value?.click()
}

async function handleFileChange(e) {
  const file = e.target.files[0]
  if (!file) return

  const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg']
  if (!allowedTypes.includes(file.type)) {
    return ElMessage.error('仅支持 JPG/JPEG/PNG 格式')
  }

  const maxSize = 5 * 1024 * 1024
  if (file.size > maxSize) {
    return ElMessage.error('图片大小不能超过 5MB')
  }

  // 检查图片分辨率
  const img = new Image()
  img.src = URL.createObjectURL(file)
  img.onload = async () => {
    if (img.width < 160 || img.height < 160) {
      return ElMessage.error('图片分辨率不能低于160*160')
    }

    try {
      const formData = new FormData()
      formData.append('file', file)
      const res = await uploadFile(formData)
      currentArticle.value.coverImageUrl = res || ''
      // 上传后重置 input，防止同一张图无法触发 change
      e.target.value = ''
    } catch (e) {
      console.error('上传图片失败:', e)
    }
  }
}

/**
 * 6. 发布逻辑
 */
async function oneClickPublish() {
  if (!currentArticle.value.coverImageUrl) {
    return ElMessage.error('封面不能为空')
  }
  // 发布前强制保存一次
  await handleKeepArticle(true)
  AccountSelectDialogRef.value.openDialog([], true)
}

async function getSelectAccount(accountLists) {
  try {
    const bodyParams = {
      accountIds: accountLists.map(item => item.id),
      articleSaveReqVOList: [{
        ...currentArticle.value,
        planId: planSearchVo.planId
      }],
      publishTime: formatToDateTime(new Date()),
      publishTimeInterval: 300,
    }
    const res = await createPublishTaskLogs(bodyParams)
    if (res) {
      ElMessage.success('发布成功')
      dialogTableVisible.value = false
    }
  } catch (e) {
    console.error('发布失败:', e)
  }
}

/**
 * 7. 删除逻辑（可选）
 */
async function handleDeleteArticle() {
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗？', '删除确认', {
      type: 'warning'
    })
    const res = await deleteArticle(currentArticle.value.id)
    if (res) {
      ElMessage.success('删除成功')
      // 删除后重新刷新列表
      await initPlan()
    }
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

// 暴露开启方法
defineExpose({openDialog})
</script>

<template>
  <el-dialog
    v-model="dialogTableVisible"
    fullscreen
    :show-close="false"
    :destroy-on-close="true"
    class="custom_dialog"
  >
    <div class="edit_box">
      <div class="plan_box">
        <div class="plan_task_title">{{ planTitle }}</div>
        <div class="plan_list">
          <div
            v-for="item in planList"
            :key="item.id"
            class="plan_item"
            :class="{ active: activeArticleId === item.id }"
            @click="changeArticle(item)"
          >
            <div class="plan_item_text">{{ item.title || '无标题文章' }}</div>
          </div>
        </div>
      </div>

      <div class="edit_wang">
        <richEditor
          v-if="activeArticleId"
          v-model:title="currentArticle.title"
          v-model:content="currentArticle.content"
          :is-allow-upload-image="false"
          height="800px"
        />
        <el-empty class="h-100%" v-else description="正在加载文章内容..."/>
      </div>

      <div class="edit_main">
        <div class="edit_main_button_list">
          <div class="edit_main_button" @click="oneClickPublish">一键发布</div>

          <el-tag
            type="info"
            class="action-tag"
            style="color: #1D2129; cursor: pointer"
            color="#F2F3F5"
            @click="dialogTableVisible = false"
          >
            返回
          </el-tag>

          <el-tag
            type="success"
            class="action-tag"
            style="color: #FFFFFF; cursor: pointer"
            color="#165DFF"
            @click="handleKeepArticle(false)"
          >
            保存
          </el-tag>
        </div>

        <div class="editor_summary">
          <div class="editor_summary_title">摘要</div>
          <div class="editor_summary_input">
            <el-input
              v-model="currentArticle.summary"
              type="textarea"
              :rows="4"
              maxlength="120"
              show-word-limit
              placeholder="请输入摘要"
              style="width: 100%"
            />
          </div>
        </div>

        <div class="editor_summary">
          <div class="editor_summary_title">封面</div>
          <div class="editor_summary_image">
            <el-image
              :src="currentArticle.coverImageUrl"
              fit="cover"
              style="width: 100%; height: 100%"
              @click="changeCoverImage"
            >
              <template #error>
                <div class="image_error">
                  <img src="@/assets/geoImage/add_plus.png" alt="add"/>
                  <span>请上传图片作为你的文章封面</span>
                  <span>仅限 .jpg / .jpeg / .png</span>
                </div>
              </template>
            </el-image>

            <div
              v-if="currentArticle.coverImageUrl"
              class="editor_summary_image_cover"
              @click="changeCoverImage"
            >
              <!--              <span>更换封面</span>-->
            </div>

            <input
              type="file"
              ref="uploadRef"
              accept="image/png, image/jpeg, image/jpg"
              style="display: none;"
              @change="handleFileChange"
            />
          </div>
        </div>
      </div>
    </div>
  </el-dialog>

  <AccountSelectDialog
    ref="AccountSelectDialogRef"
    @get-select-account="getSelectAccount"
  />
</template>


<style scoped lang="scss">

.edit_box {
  width: 100%;
  height: 100vh;
  background-color: #F7F8FA;
  display: flex;
  flex-direction: row;
}

$plan_box_width: 250px;
$edit_main_width: 393px;

.plan_box {
  width: $plan_box_width;
  height: 100%;
  background-color: #fff;

  .plan_task_title {
    height: 60px;
    display: flex;
    justify-content: center;
    align-items: center;
    font-family: PingFang SC, sans-serif;
    font-weight: 500;
    font-size: 16px;
    line-height: 24px;

    color: #1D2129;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    border-bottom: 1px solid #e5e6eb;
  }

  .plan_list {
    height: calc(100% - 60px);
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    align-items: center;

    .plan_item {
      height: 42px;
      width: 100%;
      display: flex;
      align-items: center;
      cursor: pointer;
      border-bottom: 1px dotted #E5E6EB;
      padding: 0 20px;
      box-sizing: border-box;

      .plan_item_text {
        font-family: PingFang SC, sans-serif;
        font-weight: 400;
        font-size: 14px;
        line-height: 22px;
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
        width: 100%;
        color: #1D2129;
      }
    }

    .active {
      background-color: #E8F3FF;

      .plan_item_text {
        color: #165DFF;
        font-weight: 500;
      }
    }

    &::-webkit-scrollbar {
      width: 0;
      display: none;
    }
  }
}

.edit_wang {
  width: calc(100% - $plan_box_width - $edit_main_width);
  height: 96%;
  background-color: #fff;
  border-radius: 6px;
  margin: 20px;
  overflow: auto;
  position: relative;

  &::-webkit-scrollbar {
    width: 0;
    display: none;
  }
}

.edit_main {
  width: $edit_main_width;
  height: 96%;
  background-color: #fff;
  border-radius: 6px;
  margin: 20px 20px 20px 0px;
  overflow: auto;
  position: relative;

  .edit_main_button_list {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    margin: 20px;
    gap: 10px;

    .edit_main_button {
      margin-left: 17px;
      min-width: 96px;
      min-height: 28px;
      box-sizing: border-box;
      display: flex;
      justify-content: center;
      align-items: center;
      opacity: 1;
      border-radius: 3px;
      margin-right: 15px;
      border: 1.8px solid #165DFF;
      color: #165DFF;
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-size: 16px;
      line-height: 22px;

      position: relative;
      cursor: pointer;

      &:after {
        content: '';
        display: flex;
        position: absolute;
        width: 1px;
        height: 16px;
        background-color: #C9CDD4;
        top: 50%;
        right: -12px;
        transform: translateY(-50%);
      }
    }

    .action-tag {
      min-width: 64px;
      height: 28px;
      cursor: pointer;
      font-family: PingFang SC, sans-serif;
      box-sizing: border-box;
      font-weight: 400;
      font-size: 15px;
      line-height: 22px;
    }
  }

  .editor_summary {
    margin: 20px;

    .editor_summary_title {
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-size: 16px;
      line-height: 24px;
      color: #1D2129;
      margin-bottom: 12px;
    }

    .editor_summary_input {
      padding: 10px;
      box-shadow: 0 0 0 1px #E5E6EB;
      border-radius: 8px;

      :deep(.el-textarea__inner) {
        color: #252632;
        font-family: PingFang SC, sans-serif;
        font-weight: 400;
        font-size: 15px;
        line-height: 150%;
      }

      /* 标题 input 样式（沿用你之前的风格） */
      :deep(.el-textarea__inner, .is_foucs) {
        box-shadow: none;
        resize: none;
        padding: 0;

        &::-webkit-scrollbar {
          width: 0;
          display: none;
        }

        .el-input__inner {
          color: #1D2129;
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

      :deep(.el-input__count) {
        color: #252632;
        top: 105px;
        right: 0px;
      }
    }

    .editor_summary_image {
      width: 353px;
      height: 151px;
      border-radius: 12px;
      position: relative;

      .editor_summary_image_cover {
        width: 27px;
        height: 27px;
        background-image: url(../../../assets/geoImage/change_cover_image.png);
        background-repeat: no-repeat;
        background-size: 100% 100%;
        position: absolute;
        right: 17px;
        top: 17px;
        cursor: pointer;

      }

      .image_error {
        min-width: 353px;
        min-height: 150px;
        display: flex;
        flex-direction: column;
        border-radius: 12px;
        border: 1px solid #E5E6EB;
        justify-content: center;
        align-items: center;
        padding: 32px 76px;
        cursor: pointer;

        img {
          //height: calc(151px - 20px);
          height: 20px;
          object-fit: contain;
          margin-bottom: 10px;
        }

        span {
          font-family: PingFang SC, sans-serif;
          font-weight: 400;
          line-height: 22px;
        }

        span:nth-child(1) {
          font-size: 16px;
          color: #1D3444;
        }

        span:nth-child(2) {
          font-size: 12px;
          color: #959DAA;
        }
      }
    }
  }
}
</style>
