<template>
  <div class="container_auto_public">
    <div class="operation_list">
      <header>发布设置</header>
      <div class="operate_item box1">
        <div class="title">
          <span class="color-[#FF3B30]">*</span>
          <span>添加文章{{
              formData.articleList.length ? `（文章：${formData.articleList.length}篇）` : ''
            }}</span>
        </div>
        <div class="content">
          <div class="add_article_icon" v-if="!formData.articleList.length"
               @click="openSelectArticleDialog">
            <img src="@/assets/geoImage/add_plus.png" alt="" class="w-22px"/>
          </div>
          <div class="add_article_icon2 add_article_icon" v-else @click="openSelectArticleDialog">
            <img src="@/assets/geoImage/add_plus2.png" alt="" class="w-22px"/>
            <span>添加文章</span>
          </div>
          <div class="article_list">
            <div
                v-for="(item,index) in formData.articleList"
                :key="item.id"
                @click="handleSelectArticle(index)"
                class="article_item"
                :class="{active_article_item: activeArticleIndex === index}"
            >
              <div class="article_image" @click="changeCoverImage(item)">
                <el-image :src="item.coverImageUrl" fit="cover" style="width: 100%; height:100%">
                  <template #error>
                    <div class="image_error">
                      <img src="@/assets/geoImage/add_plus.png" alt="" width="20px" height="20px"/>
                    </div>
                  </template>
                </el-image>
                <div class="change_cover_image"
                     v-show="item.coverImageUrl">
                  <img src="@/assets/geoImage/changeCover.png" alt="" class="w-12px h-12px"/>
                </div>
              </div>
              <div class="article_info_box">
                <div class="article_info">
                  <div class="article_title">{{ item.title }}</div>
                  <div class="article_time">{{ formatToDateTime(item.createTime) }}</div>
                </div>
                <div class="delete_icon" @click.stop="handleDeleteArticle(item)">
                  <img src="@/assets/geoImage/delete.png" alt="" class="w-22px"/>
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>
      <div class="operate_item box2">
        <div class="title">
          <span class="color-[#FF3B30]">*</span>
          <span>发布平台{{
              formData.accountList.length ? `（账号：${formData.accountList.length}个）` : ''
            }}</span>
        </div>
        <div class="content">
          <div class="add_article_icon2 add_article_icon" @click="openSelectAccountDialog">
            <img src="@/assets/geoImage/add_plus2.png" alt="" class="w-22px"/>
            <span>添加账号</span>
          </div>
          <div class="account_list">
            <div
                class="account_item"
                v-for="(item,index) in formData.accountList"
                :key="item.id">
              <div class="account_icon">
                <!--                <img :src="item.icon" alt="" class="w-48px h-48px rounded-full"/>-->
                <img :src="item.icon" alt="" class="w-48px h-48px rounded-full"
                     v-if="item.icon.includes('http')"/>
                <div v-else :class="item.platformIcon" class="w-48px h-48px rounded-full"></div>

              </div>
              <div class="account_info">
                <div class="account_name">
                  <div class="account_name_text">{{ item.nickName }}</div>
                </div>
                <div class="account_status">
                  <div>
                    <div class="w-18px h-18px" :class="item.platformIcon"></div>
                    <span class="color-[#BCBCBC]">{{ item.platformName }}</span>
                  </div>
                  <div class="ml-12px">
                    <img src="@/assets/geoImage/online.png" alt="" v-show="item.status"
                         class="w-12px h-12px"/>
                    <img src="@/assets/geoImage/offline.png" alt="" v-show="!item.status"
                         class="w-12px h-12px"/>
                    {{ item.status ? '在线' : "离线" }}
                  </div>
                </div>
              </div>
              <div class="delete_icon" @click="handleDeleteAccount(index)">
                <img src="@/assets/geoImage/delete.png" class="w-20px h-20px"/>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="operate_item box3">
        <div class="title">
          <span class="color-[#FF3B30]">*</span>
          <span>定时发布</span>
        </div>
        <div class="content">
          <el-date-picker
              v-model="formData.publishTime"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
              type="datetime"
              placeholder="年/月/日"
              :clearable="false"
          />
        </div>
      </div>
      <div class="operate_item box3 mt-20px!">
        <div class="title">
          <span class="color-[#FF3B30]">*</span>
          <span>发布间隔</span>
        </div>
        <div class="content">
          <el-select
              v-model="formData.intervalTime"
              placeholder="请选择发布间隔"
              :clearable="false"
          >
            <el-option
                v-for="item in intervalOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </div>
      </div>
      <div class="flex justify-center mt-40px">
        <div class="operate_button" @click="handlePublishContent" v-loading="isPublishing">
          一键发布
        </div>
      </div>
    </div>
    <div class="operation_area">
      <div class="empty_box" v-if="!formData.articleList[activeArticleIndex]">
        <div>
          <img src="@/assets/geoImage/empty.png"/>
          <span>请先在左侧添加需要发布的文章</span>
        </div>
      </div>
      <div v-else class="relative">
        <!-- 操作栏-->
        <div class="flex absolute right-25px top-25px z-99">
          <el-tag
              type="success"
              size="large"
              class="action-tag"
              style="color: #FFFFFF; cursor: pointer"
              color="#165DFF"
              @click="KeepArticle"
          >
            保存
          </el-tag>
        </div>
        <richEditor
            v-model:title="formData.articleList[activeArticleIndex].title"
            v-model:content="formData.articleList[activeArticleIndex].content"
            height="100%"
        />
      </div>
    </div>
    <AccountSelectDialog ref="AccountSelectDialogRef" @get-select-account="getSelectedAccount"
                         @add-account-by-client="addAccountByClient"
    />
    <ArticleSelectDialog ref="ArticleSelectDialogRef" @get-select-article="getSelectedArticle"/>
    <OpenDesktopDialog ref="OpenDesktopDialogRef"/>

  </div>
  <MaterialSelectDialog
      ref="MaterialSelectDialogRef"
      @confirm="getSelectMaterial"
  />
</template>
<script setup>
import {reactive} from 'vue';
import {formatToDateTime} from "@/utils/dateUtil";
import richEditor from '@/components/GeoView/RichTextEditor.vue'
import AccountSelectDialog from "@/components/GeoView/AccountSelectDialog.vue";
import ArticleSelectDialog from "@/components/GeoView/ArticleSelectDialog.vue";
import OpenDesktopDialog from "@/components/GeoView/openDesktopDialog.vue";
import MaterialSelectDialog from "@/views/GeoView/MaterialLibrary/modules/MaterialSelectDialog.vue";

import {updateArticleContent} from "@/api/geo/productTerm";
import {createPublishTaskLogs} from "@/api/geo/publishTask";
import {uploadFile} from "@/api/geo/uploadImage";

const MaterialSelectDialogRef = ref(null);
const formData = reactive({
  planList: [],
  articleList: [],
  accountList: [],
  publishTime: null,
  intervalTime: 300,
});
const activeArticleIndex = ref(0);
const AccountSelectDialogRef = ref(null);
const ArticleSelectDialogRef = ref(null);
const OpenDesktopDialogRef = ref(null);
const changeCoverItem = ref(null) //切换照片的临时变量
const intervalOptions = [
  {
    label: '5分钟',
    value: 300,
  },
  {
    label: '15分钟',
    value: 900,
  },
  {
    label: '30分钟',
    value: 1800,
  },
  {
    label: '45分钟',
    value: 2700,
  },
  {
    label: '60分钟',
    value: 3600,
  },
  {
    label: '75分钟',
    value: 4500,
  },
  {
    label: '90分钟',
    value: 5400,
  },
  {
    label: '120分钟',
    value: 7200,
  },
]

// 删除文章
function handleDeleteArticle(item) {
  formData.articleList = formData.articleList.filter((i) => i.id !== item.id);
}

// 删除账号
function handleDeleteAccount(index) {
  formData.accountList.splice(index, 1);
}

// 选中文章
function handleSelectArticle(index) {
  activeArticleIndex.value = index;
}

// 发布文章
const isPublishing = ref(false)

async function handlePublishContent() {
  if (isPublishing.value) {
    ElMessage.warning('发布中，请勿重复点击')
    return
  }
  if (!checkFormData()) {
    return
  }
  // 保存更改
  try {
    isPublishing.value = true
    ElMessage.success('正在保存所有文章...')
    const list = formData.articleList.map(async (item) => {
      return await updateArticleContent(item)
    })
    await Promise.all(list).then(res => {
      console.log(res)
      ElMessage.closeAll()
      ElMessage.success('保存成功')
    }).catch(e => {
      console.log(e)
      throw '文章保存失败'
    })
    // 立即发布
    const bodyParams = {
      accountIds: formData.accountList.map(item => item.id),
      articleSaveReqVOList: formData.articleList,
      publishTime: formData.publishTime || formatToDateTime(new Date()),
      publishTimeInterval: formData.intervalTime || 0,
    }
    const res = await createPublishTaskLogs(bodyParams) // 发布接口
    if (res) {
      ElMessage.success('发布任务创建成功，请稍后')
      formData.articleList = []
      formData.accountList = []
      formData.publishTime = null
    }
  } catch (e) {
    isPublishing.value = false
    console.log(e)
    if (e === "客户端离线，请先启动客户端") {
      OpenDesktopDialogRef.value.openDialog()
    } else if (e === "文章保存失败") {
      ElMessage.closeAll()
      ElMessage.error('文章保存失败')
    }
  } finally {
    isPublishing.value = false
  }
}

// 一键发布参数校验
function checkFormData() {
  if (!formData.articleList.length) {
    ElMessage.error('请选择文章')
    return false
  }
  if (!formData.accountList.length) {
    ElMessage.error('请选择账号')
    return false
  }
  if (formData.articleList.some(item => {
    return !item.coverImageUrl?.trim()
  })) {
    ElMessage.error('请上传文章封面')
    return false
  }
  // if (!formData.publishTime) {
  //   ElMessage.error('请选择发布时间')
  //   return false
  // }

  return true
}

// 打开客户端弹窗
function addAccountByClient() {
  if (OpenDesktopDialogRef.value) {
    OpenDesktopDialogRef.value.openDialog()
  }
}

// 打开文章选择对话框
function openSelectArticleDialog() {
  ArticleSelectDialogRef.value.openDialog(formData.articleList || [])
}

// 打开账号选择对话框
function openSelectAccountDialog() {
  console.log(formData.accountList)
  const list = formData.accountList.map(item => item.id)
  AccountSelectDialogRef.value.openDialog(list)
}

// 获取选中得账号
function getSelectedAccount(data) {
  formData.accountList = data
}

// 获取选中的文章
async function getSelectedArticle(data) {
  formData.articleList = data
  // formData.planList = data.map(item => item.id)
  // formData.planList = data
  // if (!data.length) {
  //   formData.articleList = []
  // }
  //获取plan计划下的所有文章内容
  // try {
  //   const articlePromises = formData.planList.map(async item => {
  //     const params = {
  //       pageNo: 1,
  //       planId: item,
  //       pageSize: 198,
  //     }
  //     const res = await getArticlePage(params)
  //     return res.list
  //   })
  //
  //   const articles = await Promise.all(articlePromises)
  //   formData.articleList = articles.flat()
  //   console.log(formData.articleList)
  // } catch (e) {
  //   console.log(e)
  // }
}

// 切换封面
async function changeCoverImage(item) {
  try {
    changeCoverItem.value = item
    if (MaterialSelectDialogRef.value) MaterialSelectDialogRef.value.openDialog({mode: 'single'})
  } catch (e) {
    console.log(e)
  }
}

function getSelectMaterial(data) {
  const url = data?.[0]?.url
  if (!url) {
    ElMessage.error('请选择有效的封面图片')
    return
  }

  const currentArticle = formData.articleList.find(item => item.id === changeCoverItem.value.id)
  if (!currentArticle) return

  currentArticle.coverImageUrl = url
  handleKeepArticle(currentArticle)
}

// 手动触发保存操作
async function KeepArticle() {
  try {
    const res = await handleKeepArticle(formData.articleList[activeArticleIndex.value])
    if (res) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error('保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

// 保存文章数据
async function handleKeepArticle(keepItem) {
  try {
    const bodyParams = {
      id: keepItem.id,
      planId: keepItem.planId,
      title: keepItem.title,
      summary: keepItem.summary,
      coverImageUrl: keepItem.coverImageUrl,
      content: keepItem.content
    }

    const res = await updateArticleContent(bodyParams)
    if (res) {
      return res
    } else {
      return false
    }

  } catch (e) {
    console.log(e)
    return false
  }
}

onMounted(() => {
});

</script>
<style scoped lang="scss">
$operation_list_width: 395px - 20px;
$operation_list_height: calc(100vh - 30px - 60px);

.container_auto_public {
  width: 100%;
  height: $operation_list_height;
  display: flex;
}

.operation_list {
  width: $operation_list_width;
  border-radius: 6px;
  background-color: #FFFFFF;
  padding: 11px 20px;
  position: relative;
  overflow-y: auto;

  &::-webkit-scrollbar {
    display: none;
  }

  header {
    height: 36px;
    font-family: PingFang SC, sans-serif;
    font-weight: 400;
    font-size: 16px;
    line-height: 100%;
    border-bottom: 1px solid #EEEEF0;
  }

  .operate_item {
    margin-top: 14px;

    .title {
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-size: 16px;
      line-height: 100%;
      color: #252632;
      gap: 5px;
      display: flex;
    }

    .content {
      margin-top: 23px;

    }
  }


  .operate_button {
    width: 213px;
    height: 40px;
    border-radius: 3px;
    padding: 3px 16px;
    background-color: #165DFF;
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    //position: absolute;
    //bottom: 20px;
    //left: 50%;
    //transform: translateX(-50%);
    cursor: pointer;
  }
}

.box1 {
  .content {
    .add_article_icon {
      width: 90px;
      height: 90px;
      cursor: pointer;
      border-radius: 6px;
      background-color: #F5F6FA;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .add_article_icon2 {
      width: 100%;
      height: 64px;
      border-radius: 3px;
      display: flex;
      gap: 6px;
      background-color: #E8F3FF;
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-size: 16px;
      line-height: 100%;
      color: #165DFF;
    }

    .article_list {
      max-height: 115px;
      width: 100%;
      overflow-y: auto;
      overflow-x: hidden;
      margin-top: 8px;
      padding-right: 2px;
      scrollbar-color: rgba(22, 93, 255, 0.1) transparent;
      scrollbar-width: thin;

      &::-webkit-scrollbar {
        //display: none;
      }

      .article_item {
        display: flex;
        min-height: 80px;
        width: 100%;
        justify-content: space-between;
        gap: 10px;
        padding: 11px 12px;
        border: 1px solid #e5e6eb;
        border-radius: 6px;
        margin-top: 14px;
        cursor: pointer;
        transition: all 0.2s ease-in-out;
        background-color: #F5F6FA;

        .article_image {
          width: calc((100% - 14px) * 0.2);
          min-width: 64px;
          height: 64px;
          border-radius: 3px;
          overflow: hidden;
          position: relative;

          &:hover .change_cover_image {
            display: flex;
          }

          .image_error {
            height: 100%;
            width: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            background-color: #E8F3FF;
            border-radius: 3px;
          }

          .change_cover_image {
            width: 20px;
            height: 20px;
            background: #00000080;
            backdrop-filter: blur(4px);
            position: absolute;
            left: 50%;
            top: 50%;
            transform: translate(-50%, -50%);
            cursor: pointer;
            z-index: 999;
            display: none;
            align-items: center;
            justify-content: center;
            border-radius: 3px;

          }
        }

        .article_info_box {
          width: calc((100% - 14px) * 0.8);
          background-color: #FFFFFF;
          display: flex;
          align-items: center;
          border-radius: 3px;

          &:hover .delete_icon {
            display: block;
          }


          .article_info {
            max-width: 85%;
            padding: 10px 5px 10px 15px;


            .article_title {
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
              font-family: PingFang SC, sans-serif;
              font-weight: 400;
              font-size: 16px;
              line-height: 24px;
              color: #1D2129;
            }

            .article_time {
              font-family: PingFang SC, sans-serif;
              font-weight: 400;
              font-size: 13px;
              line-height: 22px;
              color: #86909C;
            }
          }

          .delete_icon {
            display: none;
            margin-right: 10px;
          }
        }


      }

      .article_item:hover {
        border: 1px solid #165DFF;
      }

      .active_article_item {
        //background: linear-gradient(180deg, #DDEFFD 0%, #FFFFFF 45%, #FEFFFF 100%);
        border: 1px solid #165DFF;
      }

    }
  }
}

.box2 {
  .content {
    .add_article_icon {
      width: 90px;
      height: 90px;
      cursor: pointer;
      border-radius: 6px;
      background-color: #F5F6FA;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .add_article_icon2 {
      width: 100%;
      height: 64px;
      border-radius: 3px;
      display: flex;
      gap: 6px;
      background-color: #E8F3FF;
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-size: 16px;
      line-height: 100%;
      color: #165DFF;
    }

    .account_list {
      max-height: 90px;
      overflow-y: auto;
      margin-top: 14px;
      padding-right: 2px;
      scrollbar-color: rgba(22, 93, 255, 0.1) transparent;
      scrollbar-width: thin;
    }

    .account_item {
      display: flex;
      align-items: center;
      justify-content: flex-start;
      gap: 12px;
      height: 80px;
      background-color: #FFFFFF;
      padding: 15px;
      border-radius: 6px;
      position: relative;
      border: 1px solid #E5E6EB;
      transition: all 0.3s;
      margin-top: 13px;

      &:nth-child(1) {
        margin-top: 0;
      }

      &:hover {
        border: 1px solid #165DFF;
        background: linear-gradient(180deg, #DDEFFD 0%, #FFFFFF 45%, #FEFFFF 100%);
      }

      .account_info {
        font-family: PingFang SC, sans-serif;
        line-height: 100%;
        min-width: 200px;

        .account_name {
          display: flex;
          align-items: center;
          justify-content: flex-start;
          gap: 6px;
          font-size: 16px;
          font-weight: 400;
          color: #252632;
          margin-bottom: 8px;

          .account_name_text {
            width: 120px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }

        .account_status {
          color: #B9BAC1;
          font-family: PingFang SC, sans-serif;
          font-weight: 400;
          font-size: 13px;
          line-height: 100%;
          display: flex;
          align-items: center;
          justify-content: flex-start;

          > div {
            display: flex;
            align-items: center;
            gap: 6px;
          }

          .account_rebind {
            text-decoration: underline;
            text-decoration-style: solid;
            text-decoration-skip-ink: auto;
            color: #505CFF;
          }
        }
      }

      .delete_icon {
        display: none;
        align-items: center;
        justify-content: flex-end;
        cursor: pointer;
        width: 100%;
      }

      &:hover .delete_icon {
        display: flex;
      }
    }
  }
}

.box3 {
  .content {
    :deep(.el-date-editor.el-input) {
      width: 100%;
      height: 64px;
      border-radius: 3px;
      border: none;
      box-shadow: none;


      .el-input__wrapper {
        background-color: #F5F6FA;
        box-shadow: none;
        border: none;
      }

      .el-input__inner {
        font-family: PingFang SC, sans-serif;
        font-weight: 400;
        font-size: 15px;
        line-height: 100%;
        color: #7C7D84;
      }

      .el-input__prefix-inner {
        display: none;
      }
    }

    :deep(.el-select__wrapper) {
      width: 100%;
      height: 64px;
      border-radius: 3px;
      border: none;
      box-shadow: none;
      background-color: #F5F6FA;
    }

    :deep(.el-select__placeholder) {
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-size: 15px;
      line-height: 100%;
      color: #7C7D84;
    }
  }
}

.operation_area {
  width: calc(100% - $operation_list_width);
  height: 100%;
  border-radius: 6px;
  margin-left: 20px;
  background-color: #FFFFFF;
  overflow: auto;
  scrollbar-color: rgba(22, 93, 255, 0.1) transparent;

  .empty_box {
    height: 100%;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 7px;

    > div {
      display: flex;
      flex-direction: column;
    }

    img {
      width: 200px;
      height: 200px;
    }

    span {
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-size: 15px;
      line-height: 150%;
      text-align: center;
      color: #646363;
    }
  }
}
</style>
