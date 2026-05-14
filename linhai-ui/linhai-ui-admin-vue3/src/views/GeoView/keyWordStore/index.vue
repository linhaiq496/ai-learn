<template>
  <div class="container_article">
    <headerTag :headerInfo="headerInfo"/>
    <main>
      <div class="left_box">
        <div class="plan_task_title">
          <div class="title_1">
            <span>产品词库</span>
            <el-icon style="cursor:pointer;" @click="addKeyword">
              <Plus/>
            </el-icon>
          </div>
          <div class="key_box_search">
            <el-input
              placeholder="搜索"
              v-model="keyPageParam.name"
              :suffix-icon="Search"
              @keydown.enter="initDataList(false)"
            />
          </div>
        </div>
        <div class="plan_list" ref="planListRef" @scroll="handleScroll">
          <div
            v-for="item in dataList"
            :key="item.id"
            class="plan_item"
            :class="{ active: LongKeyPageParam.productId === item.id }"
            @click="changeLongKeyCurr(item)"
          >
            <div class="plan_item_text">
              <div>{{ item.name.charAt(0) }}</div>
              <div>{{ item.name }}</div>
              <div class="delete_icon" @click="deleteKeyWord(item)">
                <img src="@/assets/geoImage/delete.png" alt="" class="w-20px h-20px"/>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="right_box">
        <div class="right_box_top">
          <div class="right_box_top_title">
            <span>前缀词</span>
            <div class="operator_button" @click="editKeyWordList">
              <img
                src="../../../assets/geoImage/edit.png"
                class="w-16px h-16px"/>
              <span>编辑词缀</span>
            </div>
          </div>
          <div class="before_word">
            <el-input-tag
              v-model="currentWordData.beforeWord"
              placeholder=""
              readonly
            />
          </div>
          <div class="right_box_top_title mt-20px">
            <span>后缀词</span>
          </div>
          <div class="after_word">
            <el-input-tag
              v-model="currentWordData.afterWord"
              placeholder=""
              readonly
            />
          </div>
        </div>
        <div class="right_box_bottom">
          <div class="right_box_bottom_header">
            <div class="key_box_search">
              <el-input
                placeholder="搜索"
                v-model="LongKeyPageParam.keyword"
                :suffix-icon="Search"
                @keydown.enter="initDataList(false)"
              />
            </div>
            <div class="operate_list">

              <el-button
                type="primary"
                style="color:#FFFFFF "
                color="#165DFF" size="default"
                @click="addLongKeyword"
              >
                <el-icon class="el-icon--left" color="#FFFFFF">
                  <Plus/>
                </el-icon>
                添加
              </el-button>
              <el-button
                type="primary"
                style="color:#FFFFFF "
                color="#165DFF" size="default"
                @click="exportLongKeywordList">
                <el-icon class="el-icon--left" color="#FFFFFF">
                  <Upload/>
                </el-icon>
                导出
              </el-button>
              <el-button
                type="warning"
                style="color: #1D2129 "
                color="#F2F3F5" size="default"
                @click="editLongKeyword">
                <el-icon class="el-icon--left" color="#4E5969">
                  <Edit/>
                </el-icon>
                编辑
              </el-button>
              <el-button
                type="danger"
                style="color: #F53F3F"
                color="#FFECE8"
                size="default"
                @click="deleteLongKeyword">
                <el-icon class="el-icon--left" color="#F53F3F">
                  <Delete/>
                </el-icon>
                批量删除
              </el-button>
            </div>
          </div>
          <div class="right_box_bottom_list">
            <el-table :data="LongKeyList" style="width: 100%"
                      height="calc(100vh - var(--headerTagHeight) - 235px - 40px - 52px - 32px - 10px)"
                      ref="longKeyTable">
              <el-table-column type="selection" width="55"/>
              <el-table-column prop="order" label="序号" width="80">
                <template #default="scope">
                  <div>{{
                      (LongKeyPageParam.pageNo - 1) * LongKeyPageParam.pageSize + scope.$index + 1
                    }}
                  </div>
                </template>
              </el-table-column>

              <el-table-column prop="keyword" label="长尾词" width="800"/>

              <el-table-column prop="createTime" label="创建时间" width="240">
                <template #default="scope">
                  {{ formatToDateTime(scope.row.createTime) }}
                </template>
              </el-table-column>

              <el-table-column label="操作" width="160" fixed="right">
                <template #default="scope">
                  <div class="operate_list">
                    <div class="operate_item" @click="handleEditLongKeyword(scope.row)">编辑</div>
                    <div class="operate_item" @click="handleDeleteLongKeyword(scope.row)">删除</div>
                  </div>
                </template>
              </el-table-column>
            </el-table>
            <div class="right_box_bottom_page">
              <span>共计{{ LongKeyPageParam.total }}条</span>
              <el-pagination
                v-model:current-page="LongKeyPageParam.pageNo"
                v-model:page-size="LongKeyPageParam.pageSize"
                :page-sizes="[15, 30, 50, 100]"
                :background="true"
                layout="prev, pager, next, sizes"
                :total="LongKeyPageParam.total"
                @size-change="initLongKeyList"
                @current-change="initLongKeyList"
              />
            </div>
          </div>
        </div>
      </div>
    </main>
    <editKeyWordStore
      ref="editKeyWordStoreRef"
      @init-all="initDataList"/>
    <editLongKeyDiaklog
      ref="editLongKeyDiaklogRef"
      @init-long-key-list="initLongKeyList"
      :productId="LongKeyPageParam.productId"/>
  </div>
</template>

<script setup>
import headerTag from '@/components/GeoView/headerTag.vue'
import headerImage from '@/assets/geoImage/importantKeys.png'
import {reactive, ref} from "vue";
import {Search, Plus, Delete, Edit, Upload} from "@element-plus/icons-vue";
import {
  deleteProductKeyword,
  deleteProductKeywordList, deleteProductTerm, downloadProductKeywordList,
  getProductKeywordPage,
  getProductTermPage
} from "@/api/geo/productTerm";
import {debounce} from "lodash-es";
import {formatToDateTime} from "@/utils/dateUtil";
import editKeyWordStore from './editKeyWordStore.vue'
import editLongKeyDiaklog from './editLongKeyDiaklog.vue'
import download from "@/utils/download";

const router = useRouter()
const longKeyTable = ref(null)

const headerInfo = reactive({
  title: "关键词库",
  desc: "您可以在此处查看并管理您的产品词库，管理您的关键词。<br/>人工智能创作文章时将学习您打造的关键词库。",
  img: headerImage
})
const chooseKeyName = ref(null)

const editKeyWordStoreRef = ref(null)
const editLongKeyDiaklogRef = ref(null)

const keyPageParam = reactive({
  pageNo: 1,
  pageSize: 25,
  total: 200,
  name: ""
})
const LongKeyPageParam = reactive({
  pageNo: 1,
  pageSize: 15,
  total: 100,
  keyword: null,
  productId: null
})

const LongKeyList = ref([])
const planListRef = ref(null)
const dataList = ref([])
const currentWordData = reactive({
  beforeWord: [],
  afterWord: [],
  keyword: []
})

const initDataList = async function (isKeepValue = false, isPushKey = false) {
  try {
    chooseKeyName.value = null
    const params = {
      pageNo: keyPageParam.pageNo,
      pageSize: keyPageParam.pageSize,
      ...(keyPageParam.name && keyPageParam.name.trim() !== '' ? {name: keyPageParam.name.trim()} : {})
    }
    const res = await getProductTermPage(params)
    ElMessage.success('获取数据成功')
    keyPageParam.total = res.total
    if (isPushKey) {
      dataList.value = [...dataList.value, ...res.list]
    } else {
      dataList.value = res.list
    }

    if (dataList.value && dataList.value.length) {
      // LongKeyPageParam.productId = dataList.value[0].id
      chooseKeyName.value = dataList.value[0].name
      if (!isKeepValue) {
        await changeLongKeyCurr(dataList.value[0])
      } else {
        await changeLongKeyCurr(dataList.value.find(item => item.id === LongKeyPageParam.productId))
      }
      // await initLongKeyList()
    }

  } catch (e) {
    console.log(e)
  }
}

const scrollTimer = ref(null)

async function handleScroll(e) {
  if (scrollTimer.value) return
  scrollTimer.value = setTimeout(() => {
    scrollTimer.value = null
    let scrollTop = e.target.scrollTop
    let scrollHeight = e.target.scrollHeight
    let offsetHeight = Math.ceil(e.target.getBoundingClientRect().height)
    let currentHeight = scrollTop + offsetHeight + 100 // 在距离底部550px时触发事件
    if (currentHeight >= scrollHeight && keyPageParam.pageNo * keyPageParam.pageSize < keyPageParam.total) {
      console.log('yes')
      keyPageParam.pageNo++
      initDataList(true, true)
    }
  }, 1000)
}


//获得产品词列表
async function initLongKeyList() {
  try {
    if (!LongKeyPageParam.productId) {
      ElMessage.warning('请选择产品词')
    }
    const params = {
      pageNo: LongKeyPageParam.pageNo,
      pageSize: LongKeyPageParam.pageSize,
      ...(LongKeyPageParam.keyword && LongKeyPageParam.keyword.trim() !== '' ? {keyword: LongKeyPageParam.keyword.trim()} : {}),
      ...(LongKeyPageParam.productId ? {productId: LongKeyPageParam.productId} : {})
    }
    const res = await getProductKeywordPage(params)
    LongKeyPageParam.total = res.total
    LongKeyList.value = res.list

    if (!res.total) {
      return ElMessage.error('暂无数据')
    }
  } catch (e) {
    console.log(e)
    ElMessage.error('获取数据失败，请重试')
  }
}

// 打开添加词库弹窗
async function addKeyword() {
  editKeyWordStoreRef.value.openDialog()
}

// 添加长尾词
async function addLongKeyword() {
  editLongKeyDiaklogRef.value.openDialog()
}

// 导出长尾词列表
async function exportLongKeywordList() {
  try {
    const coreName = dataList.value.find(item => {
      return item.id === LongKeyPageParam.productId
    })
    const res = await downloadProductKeywordList({productId: LongKeyPageParam.productId})
    ElMessage.success('正在导出请稍后！')
    download.excel(res, coreName.name)
  } catch (e) {
    console.log(e)
  }
}

// 批量编辑
async function editLongKeyword() {
  const selectedRows = longKeyTable.value.getSelectionRows().filter(item => item.id)
  if (!selectedRows.length) {
    ElMessage.warning('请先选择要编辑的长尾词')
    return
  }
  editLongKeyDiaklogRef.value.openDialog(selectedRows)

}

// 批量删除
async function deleteLongKeyword() {
  try {
    // 获取选中的行数据
    const selectedRows = longKeyTable.value.getSelectionRows().filter(item => item.id)
    if (!selectedRows.length) {
      ElMessage.warning('请先选择要删除的长尾词')
      return
    }

    // 确认删除
    await ElMessageBox.confirm(
      `确定要删除 ${selectedRows.length} 个长尾词吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 调用删除接口
    const ids = selectedRows.map(row => Number(row.id))
    const res = await deleteProductKeywordList(ids)

    if (res) {
      // 删除成功后刷新数据
      await initLongKeyList()
      ElMessage.success(`成功删除 ${selectedRows.length} 个长尾词`)
    }
  } catch (e) {
    if (e !== 'cancel') {
      console.log(e)
      ElMessage.error('删除失败')
    }
  }
}

// 删除单个长尾词
async function handleDeleteLongKeyword(row) {
  try {
    await ElMessageBox.confirm(
      `确定要删除 "${row.keyword}" 这个长尾词吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const res = await deleteProductKeyword(row.id)
    if (res) {
      // 删除成功后刷新数据
      await initLongKeyList()
      ElMessage.success('删除成功')
    }

  } catch (e) {
    if (e !== 'cancel') {
      console.log(e)
      ElMessage.error('删除失败')
    }
  }
}

// 编辑单个长尾词
async function handleEditLongKeyword(row) {
  console.log(row)
  editLongKeyDiaklogRef.value.openDialog([row])
}

async function editKeyWordList() {
  editKeyWordStoreRef.value.openDialog(dataList.value.find(item => LongKeyPageParam.productId === item.id))
}

// 选择产品词（更新长尾词列表）
async function changeLongKeyCurr(row) {
  LongKeyPageParam.productId = row.id
  const prefixList = row.prefix.split(',').filter(item => item) || ['暂无数据']
  const suffixList = row.suffix.split(',').filter(item => item) || ['暂无数据']
  if (prefixList.length > 5) {
    currentWordData.beforeWord = prefixList.slice(0, 5)
    currentWordData.beforeWord.push(`${prefixList.length - 5}+`)
  } else {
    currentWordData.beforeWord = prefixList
  }
  if (suffixList.length > 5) {
    currentWordData.afterWord = suffixList.slice(0, 5)
    currentWordData.afterWord.push(`${suffixList.length - 5}+`)
  } else {
    currentWordData.afterWord = suffixList
  }
  await initLongKeyList()
}

// 删除产品词
async function deleteKeyWord(item) {
  try {
    // 添加二次确认
    await ElMessageBox.confirm(
      `确定要删除 "${item.name}" 这个产品词吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const res = await deleteProductTerm(item.id)
    if (res) {
      ElMessage.success('删除成功')
      // 删除成功后刷新数据
      await initDataList(false, false)
    }
  } catch (e) {
    if (e !== 'cancel') {
      console.log(e)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  initDataList(false,false)
})
</script>
<style scoped>
main {
  --headerTagHeight: calc(122px + 60px);
  height: calc(100vh - var(--headerTagHeight));
  font-family: PingFang SC, sans-serif;
  position: relative;
  overflow-y: hidden;
  border-radius: 6px;
  margin-top: 20px;
  display: flex;
  justify-content: space-between;

  &::-webkit-scrollbar {
    display: none;
  }
}

main .left_box {
  width: 272px;
  height: calc(100vh - var(--headerTagHeight));
  background-color: #fff;
  border-radius: 6px;

  .plan_task_title {
    height: 95px;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;

    font-weight: 500;
    font-size: 16px;
    line-height: 24px;
    color: #1D2129;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    border-bottom: 1px solid #e5e6eb;
    gap: 8px;

    .title_1 {
      width: calc(100% - 40px);
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .key_box_search {
      width: calc(100% - 40px);

      :deep(.el-input__wrapper) {
        background-color: #F2F3F5;
      }
    }
  }

  .plan_list {
    height: calc(100% - 60px);
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    align-items: center;

    &::-webkit-scrollbar {
      display: none;
    }

    .plan_item {
      height: 40px;
      width: 100%;
      display: flex;
      align-items: center;
      cursor: pointer;
      padding: 0 20px;
      box-sizing: border-box;
      transition: all 0.3s ease;

      .plan_item_text {
        width: 100%;
        font-weight: 400;
        font-size: 14px;
        display: flex;
        align-items: center;
        justify-content: flex-start;
        gap: 8px;
        padding: 8px 12px;
        cursor: pointer;
        position: relative;

        div:nth-child(1) {
          width: 24px;
          height: 24px;
          border-radius: 4px;
          color: #ffffff;
          background-color: #165DFF;
          display: flex;
          justify-content: center;
          align-items: center;
        }

        div:nth-child(2) {
          color: #165DFF;
        }

        &:hover .delete_icon {
          display: block;
        }

        .delete_icon {
          width: 20px;
          height: 20px;
          display: none;
          position: absolute;
          right: 0;
          top: 50%;
          transform: translateY(-50%);
          cursor: pointer;
        }
      }

      &:hover {
        background-color: #E8F3FF;
      }
    }

    .active {
      background-color: #E8F3FF;


    }

    &::-webkit-scrollbar {
      width: 0;
      display: none;
    }
  }
}

main .right_box {
  width: calc(100% - 272px - 20px);
  height: calc(100vh - var(--headerTagHeight));
  border-radius: 6px;

  .right_box_top {
    padding: 20px;
    background-color: #fff;
    border-radius: 6px;
    font-family: PingFang SC, sans-serif;
    font-weight: 400;
    font-size: 16px;
    line-height: 24px;

    .after_word, .before_word {
      :deep(.el-input-tag) {
        background-color: #F2F3F5;
        padding: 0;
        border-radius: 2px;
      }
    }

    .right_box_top_title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 18px;
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-size: 16px;
      line-height: 24px;
      color: #1D2129;

      .operator_button {
        min-width: 108px;
        min-height: 32px;
        border-radius: 3px;
        padding: 5px 16px 5px 12px;
        display: flex;
        align-items: center;
        cursor: pointer;
        gap: 8px;
        font-family: PingFang SC, sans-serif;
        font-weight: 400;
        font-size: 14px;
        line-height: 22px;
        color: white;
        background-color: #165DFF;

      }
    }
  }

  .right_box_bottom {
    margin-top: 20px;
    background-color: #fff;
    padding: 20px 24px;
    border-radius: 6px;
    min-height: calc(100vh - var(--headerTagHeight) - 235px);
    position: relative;

    .right_box_bottom_page {
      width: 97%;
      display: flex;
      position: absolute;
      justify-content: space-between;
      bottom: 20px;
    }


    .right_box_bottom_header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 100%;
      margin-bottom: 20px;

      .key_box_search {
        width: 228px;
        height: 32px;
        border-radius: 3px;
        padding: 5px 12px;

        :deep(.el-input__wrapper) {
          background-color: #F2F3F5;
        }
      }

      .operate_list {
        width: 50%;
        display: flex;
        justify-content: flex-end;
      }
    }

    .right_box_bottom_list {
      .operate_list {
        display: flex;
        justify-content: flex-start;
        align-items: center;
        color: #165DFF;
        font-family: PingFang SC, sans-serif;
        font-weight: 400;
        font-size: 13px;

        .operate_item {
          width: 42px;
          height: 18px;
          display: flex;
          justify-content: center;
          align-items: center;
          cursor: pointer;
          font-family: PingFang SC, sans-serif;
          font-weight: 400;
          font-size: 13px;
          line-height: 22px;
          color: #165DFF;

          &:nth-child(1) {
            border-right: 1px solid #e5e6eb;
          }
        }

      }
    }
  }

}


</style>

