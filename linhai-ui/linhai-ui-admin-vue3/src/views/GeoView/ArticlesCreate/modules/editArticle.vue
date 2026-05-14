<template>
  <!--  <div>编辑</div>-->
  <!--  <div @click="closeEditMode">返回</div>-->
  <div class="edit_box">
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
    <div class="article_list">
      <div class="article_search">
        <div class="flex justify-between gap-10px font-[PingFang SC] font-400 text-16px leading-22px mb-20px">
          <div class="px-16px py-3px border border-solid border-#165DFF color-#165DFF rounded-3px cursor-pointer">
            一键发布
          </div>
          <div class="flex gap-10px">
            <div
                @click="closeEditMode"
                class="px-16px py-3px rounded-3px color-#1D2129 bg-#F2F3F5 cursor-pointer">返回
            </div>
            <div
                @click="keepEditContent"
                class="px-16px py-3px rounded-3px color-#FFFFFF bg-#165DFF cursor-pointer">保存
            </div>
          </div>
        </div>
        <el-input v-model="SearchParam.title" placeholder="搜索"
                  :suffix-icon="Search"
                  @keydown="initDataList(false , null )"
        />
      </div>
      <div class="show_list">
        <div class="show_item"
             :class="{activeItem:activeArticleId === item.articleId}"
             v-for="item in dataList"
             :key="item.id">
          <div class="show_header">
            <div class="show_header_platform">
              <div :class="[getPlatformCssClass(item.platformFormat)]" class="w-22px h-22px"></div>
              <div class="show_header_platform_title">{{ getPlatformLabel(item.platformFormat) }}</div>
            </div>
            <div class="show_header_detail">
              <div class="show_header_time">{{ formatTime(item.createTime, 'yyyy-MM-dd HH:mm:ss') }}</div>
              <div class="show_header_operate_list">
                <div><img src="@/assets/geoImage/copy.png"
                          class="copy-icon w-16px h-16px cursor-pointer"
                          @click="copyText(item.articleDetail?.content)" alt="复制"/></div>
                <div><img src="@/assets/geoImage/delete2.png" class="w-16px h-16px cursor-pointer"
                          @click.stop="deleteItem(item.articleId)" alt="删除"/></div>
              </div>
            </div>
          </div>
          <div class="show_content" v-if="ALLOWED_TO_VIEW.includes(item.articleStatus)"
               @click="editArticle(item)">
            <div class="show_content_title">{{ item.articleTitle }}</div>
            <div class="show_content_detail">{{ item.articleBody }}</div>
          </div>
          <div class="create_new" v-else-if="NEED_RERET_STATE.includes(item.articleStatus)">
            <img src="@/assets/geoImage/wating.gif" class="w-140px h-114px"/>
          </div>
          <div class="create_fail" v-else>
            <img src="@/assets/geoImage/error.png" class="w-35px h-35px"/>
            <span>生成失败</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>

import {reactive, ref} from "vue";
import {Search} from '@element-plus/icons-vue'
import Clipboard from 'clipboard';
import {getArticleResultPage, getArticleResult, deleteArticle, saveArticle} from "@/api/geo/generateArticle";
import {useDictStore} from '@/store/modules/dict'
import {ElMessage, ElMessageBox} from "element-plus";
import richEditor from '@/components/GeoView/RichTextEditor.vue'
import {formatTime} from "@/utils";

const _emit = defineEmits(['closeEditMode'])
const useDictStoreRef = useDictStore()

// 状态值  生成状态：0-待生成，1-生成中，2-生成失败，3-生成完成
const NEED_RERET_STATE = [0, 1] // 生成中
const ALLOWED_TO_VIEW = [2, 3] // 生成完成

const activeArticleId = ref('') // 选中的文章id

const publish_platform = ref([])
// 默认空文章结构
const emptyArticle = {
  id: '',
  title: '',
  content: '',
  summary: '',
  coverImageUrl: ''
}

const currentArticle = ref({...emptyArticle})

const SearchParam = reactive({
  pageNo: 1,
  pageSize: 100,
  taskId: null,
  total: 100,
  status: 3, // 必须是生成成功的文章状态 // todo
  title: ''
})
// 数据源
const dataList = ref([])

//打开编辑模式时，设置初始数据
async function openEditMode(initData) {
  console.log(initData.taskId)
  SearchParam.taskId = initData.taskId
  await initDataList(false, initData.articleId)
  await getPlatformList()
}

// 返回 （关闭编辑模式）
async function closeEditMode() {
  try {
    if (!currentArticle.value.articleId) {
      _emit('closeEditMode')
      return
    }
    const params = {
      id: currentArticle.value.articleId,
      title: currentArticle.value.title,
      body: currentArticle.value.content,
    }
    const res = await saveArticle(params)
    if (res) {
      ElMessage.closeAll()
      ElMessage.success('保存成功')
      _emit('closeEditMode')
    }
  } catch (e) {
    console.log(e)
  } finally {
  }
}

// 保存编辑内容
async function keepEditContent() {
  try {
    // 保存数据 todo
    const params = {
      id: currentArticle.value.articleId,
      title: currentArticle.value.title,
      body: currentArticle.value.content,
    }
    const res = await saveArticle(params)
    if (res) {
      ElMessage.closeAll()
      ElMessage.success('保存成功')
      return true
    }
  } catch (e) {
    console.log(e)
    return false
  } finally {
  }
}


// 获取发布平台字典数据
async function getPlatformList() {
  publish_platform.value = await useDictStoreRef.getDictByType('platform_type') || [];
}

// 根据code获取平台图标
function getPlatformCssClass(value) {
  return publish_platform.value.find(item => item.value === value)?.cssClass || 'icon-xiaohongshu'
}

// 根据code获取平台name
function getPlatformLabel(value) {
  return publish_platform.value.find(item => item.value === value)?.label || '未知平台'
}


async function deleteItem(id) {
  if (!id) return
  try {
    await ElMessageBox.confirm('确定要删除该项吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // const res = await deleteArticle(id)
    // if (res) {
    //   ElMessage.success('删除成功')
    // }
    // await initDataList(false)

  } catch {
    // 用户取消删除，不做任何操作
  }
}

async function editArticle(item) {
  if (activeArticleId.value) {
    if (activeArticleId.value === item.articleId) {
      return
    }
    try {
      const flag = keepEditContent()
      if (flag) {
        await initDataList(false, item.articleId)
      }
    } catch (e) {
      console.log(e)
    }
  }
}

// 复制内容
function copyText(text) {
  if (!text) return
  try {
    const clipboard = new Clipboard('.copy-icon', {
      text: () => text
    })
    clipboard.on('success', () => {
      ElMessage.success('复制成功')
      clipboard.destroy()
    })

    clipboard.on('error', () => {
      ElMessage.error('复制失败')
      clipboard.destroy()
    })
  } catch (e) {
    console.log(e)
  }
  // e.stopPropagation() // 禁止冒泡
}


// 过滤HTML标签
function htmlToTextWithLineBreak(html) {
  const div = document.createElement('div')
  div.innerHTML = html

  let result = ''

  const blockTags = [
    'P', 'DIV', 'H1', 'H2', 'H3', 'H4', 'H5', 'H6',
    'LI', 'UL', 'OL', 'BLOCKQUOTE', 'BR'
  ]

  function traverse(node) {
    if (node.nodeType === 3) { // 文本节点 nodeType = 3 表示 文本节点
      // 文本节点
      result += node.textContent
    } else if (node.nodeType === 1) {
      const tag = node.tagName

      if (tag === 'BR' || tag === 'HR' || tag === 'div' || tag === 'P' || tag === 'H1' || tag === 'H2' || tag === 'H3' || tag === 'H4' || tag === 'H5' || tag === 'H6') {
        result += '\n'
      }

      node.childNodes.forEach(traverse)

      // 块级元素结束后加换行
      if (blockTags.includes(tag)) {
        result += '\n'
      }
    }
  }

  traverse(div)
  return result
      .trim()
      .replace(/\n+/g, '\n')   // 多个换行合并
}

// 获取数据
async function initDataList(isPush = false, defaultSelectId = null) {
  try {

    const params = {
      pageNo: SearchParam.pageNo,
      pageSize: SearchParam.pageSize,
      taskId: SearchParam.taskId,
      status: SearchParam.status,
    }
    // 获取文章列表
    const res = await getArticleResultPage(params)
    SearchParam.total = res.total

    let list = res.list.flatMap(item => item.details.map(pre => ({
      taskId: item.taskId, // 任务编号
      taskStatus: item.status, // 任务状态 0-待生成，1-生成中，2-生成失败，3-生成完成
      prompt: item.prompt, // 提示词
      aiType: item.aiType, // 生成类型（ARTICLE、IMAGE）
      platformFormat: item.platformFormat, //平台格式
      noteSubject: item.noteSubject, // 笔记题材
      articleId: pre.id, // 文章编号
      articleStatus: pre.status,// 文章状态 0-待生成，1-生成中，2-生成失败，3-生成完成
      articleSort: pre.sort, // 文章排序
      articleTitle: pre.title,
      articleBody: htmlToTextWithLineBreak(pre.body),
      createTime: pre.createTime, // 文章创建时间
    })))
    if (defaultSelectId) {
      activeArticleId.value = defaultSelectId
      const findItem = list.find(item => item.articleId === defaultSelectId) || {}
      currentArticle.value = {
        articleId: findItem.articleId,
        title: findItem.articleTitle,
        content: findItem.articleBody,
      }
    } else {
      activeArticleId.value = list[0].articleId
      currentArticle.value = {
        articleId: list[0].articleId,
        title: list[0].articleTitle,
        content: list[0].articleBody,
      }
    }

    if (isPush) {
      dataList.value.push(...list)
    } else {
      dataList.value = list
    }
  } catch (e) {
    console.log(e)
  }
}


defineExpose({
  openEditMode
})
</script>
<style scoped lang="scss">
.edit_box {
  display: flex;
  width: 100%;
  height: 100%;
  flex-direction: row;
  gap: 20px;
  background-color: #F7F8FA;
}

.edit_wang {
  height: 100%;
  width: calc(100% * (1241 / (1241 + 393)));
  border-radius: 6px;
  background-color: #FFFFFF;
  overflow: auto;

  &::-webkit-scrollbar {
    width: 0;
  }
}

.article_list {
  height: 100%;
  width: calc(100% * (393 / (1241 + 393)));
  border-radius: 6px;
  background-color: #FFFFFF;
  padding: 0 20px 20px;
  overflow: auto;

  &::-webkit-scrollbar {
    width: 0;
  }

  .article_search {
    padding-top: 20px;
    position: sticky;
    top: 0px;
    background-color: #fff;

    :deep(.el-input__wrapper) {
      height: 40px;
      border-radius: 3px;
      padding: 5px 12px;
      background-color: #F2F3F5;
      box-shadow: none;

      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-size: 14px;
      leading-trim: NONE;
      line-height: 22px;
      letter-spacing: 0%;
      vertical-align: middle;
      color: #86909C;
    }

  }

  .show_list {
    padding-top: 20px;
    display: flex;
    flex-direction: column;
    gap: 20px 18px;
    width: 100%;

    .show_item {
      //width: 340px;
      width: 100%;
      min-height: 340px;
      border-radius: 6px;
      border: 1px solid #F7F8FA;
      padding: 20px;
      overflow: hidden;
      background-color: #F7F8FA;

      &:hover {
        .show_header .show_header_detail {
          display: flex;

        }
      }

      .show_header {
        display: flex;
        justify-content: space-between;
        height: 24px;

        .show_header_platform {
          display: flex;
          gap: 5px;
          align-items: center;

          .show_header_platform_title {
            font-family: PingFang SC, sans-serif;
            font-weight: 400;
            font-size: 12px;
            line-height: 100%;
            letter-spacing: 0;
            color: #252632;
          }
        }

        .show_header_detail {
          display: none;
          gap: 5px;

          .show_header_time {
            font-family: PingFang SC, sans-serif;
            font-weight: 400;
            font-size: 12px;
            leading-trim: NONE;
            line-height: 22px;
            color: #5E5E5E;
          }

          .show_header_operate_list {
            display: flex;
            gap: 5px;

            > div {
              display: flex;
              align-items: center;
            }
          }
        }
      }

      .show_content {
        display: flex;
        margin-top: 20px;
        gap: 10px;
        flex-direction: column;
        overflow: hidden;
        cursor: pointer;

        .show_content_title {
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
          font-family: PingFang SC, sans-serif;
          font-weight: 600;
          font-size: 16px;
          line-height: 22px;
          letter-spacing: 0%;
          color: #252632;
        }

        .show_content_detail {
          white-space: pre-wrap; // 换行
          display: -webkit-box;
          -webkit-line-clamp: 11;
          -webkit-box-orient: vertical;
          overflow: hidden;
          text-overflow: ellipsis;
          font-family: PingFang SC, sans-serif;
          font-weight: 400;
          font-size: 14px;
          line-height: 22px;
          color: #252632;
          min-height: 220px;
        }
      }

      .create_new {
        height: calc(100% - 24px - 20px - 20px * 2);
        margin-top: 20px;
        display: flex;
        justify-content: center;
        align-items: center;
      }

      .create_fail {
        height: calc(100% - 24px - 20px - 20px * 2);
        margin-top: 20px;
        display: flex;
        justify-content: center;
        align-items: center;
        flex-direction: column;
        gap: 20px;
        font-family: PingFang SC, sans-serif;
        font-weight: 400;
        font-style: Regular;
        font-size: 17px;
        leading-trim: NONE;
        line-height: 22px;
        letter-spacing: 0%;
        color: #252632;
      }
    }

    .activeItem {
      border: 1px solid #165DFF;
    }
  }
}
</style>
