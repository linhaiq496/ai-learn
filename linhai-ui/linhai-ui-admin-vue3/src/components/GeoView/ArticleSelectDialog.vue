<template>
  <!--  账号选择窗口  -->
  <el-dialog
    v-model="dialogTableVisible"
    class="custom_dialog"
    :show-close="false"
    :close-on-press-escape="false"
    width="1400px" top="5vh"
    :destroy-on-close="true"
  >
    <header>
      <span>选择文章（文章：{{ selectedPlans.length }}篇）</span>
      <img src="@/assets/geoImage/close.png" class="w-16px h-16px"
           @click="dialogTableVisible = false"/>
    </header>
    <main>
      <div class="header">
        <div class="search_input">
          <div>
            <el-input
              placeholder="搜索"
              v-model="searchVo.title"
              :suffix-icon="Search"
              @keydown="initDataList"
            />
          </div>
          <div class="search_select">
            <el-select
              v-model="searchVo.industry"
              placeholder="所属行业"
              clearable
              style="width: 150px; margin-left: 10px;"
              @change="initDataList"
            >
              <el-option
                v-for="item in industryList"
                :key="item"
                :label="item"
                :value="item"
              />
            </el-select>
            <el-select
              v-model="searchVo.creationType"
              placeholder="创作类型"
              clearable
              style="width: 150px; margin-left: 10px;"
              @change="initDataList"
            >
              <el-option
                v-for="item in creationTypeList"
                :key="item"
                :label="item"
                :value="item"
              />
            </el-select>
          </div>
        </div>
      </div>
      <div class="content">
        <div v-show="dataList.length > 0" class="flex flex-col justify-between h-100%">
          <el-table
            v-loading="loading"
            :data="dataList"
            ref="aboutTableRef"
            max-height="calc(100vh - var(--headerTagHeight) - 270px)"
            @row-click="handleRowChoose"
          >
            <el-table-column width="55">
              <template #default="scope">
                <el-checkbox
                  v-model="scope.row.selected"
                  @click.stop="changeSelectedPlan(scope.row)"
                />
              </template>
            </el-table-column>
            <el-table-column prop="order" label="序号" width="50">
              <template #default="scope">
                <div>{{ (searchVo.pageSize * (searchVo.pageNo - 1)) + scope.$index + 1 }}</div>
              </template>
            </el-table-column>

            <el-table-column prop="title" label="标题" width="250">
              <template #default="scope">
                <el-tooltip :content="scope.row.title" placement="top">
                  <div class="title_box">{{ scope.row.title }}</div>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column label="所属行业" width="80">
              <template #default="scope">
                {{ scope.row.industry }}
              </template>
            </el-table-column>
            <el-table-column prop="longTailKeywords" label="核心关键词" width="120">
              <template #default="scope">
                <div class="keywords_box">{{ scope.row.coreKeyword }}</div>
              </template>
            </el-table-column>
            <el-table-column prop="longTailKeywords" label="长尾关键词" width="280">
              <template #default="scope">
                <el-tooltip :content="scope.row.longTailKeywords" placement="top">
                  <div class="keywords_box">{{ scope.row.longTailKeywords }}</div>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column prop="creationType" label="创作类型"/>
            <el-table-column prop="createTime" label="更新时间" width="220">
              <template #default="scope">
                {{ formatToDateTime(scope.row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="platform" label="发布平台" width="150">
              <template #default="scope">
                <div class="platform_box">
                  <div
                    v-for="item in getPlatformCssClass(scope.row.platform || '')"
                    :key="item"
                    :class="item"
                    class="w-24px h-24px ml--5px border border-white border-solid rounded-6px"></div>
                  <div v-show="getPlatformCssClass(scope.row.platform || '').length === 0">-</div>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination_box">
            <div class="total_box">共 {{ searchVo.total }} 条</div>
            <el-pagination
              v-model:current-page="searchVo.pageNo"
              v-model:page-size="searchVo.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :background="true"
              layout="pager, sizes"
              :total="searchVo.total"
              @size-change="initDataList"
              @current-change="initDataList"
            />
          </div>
        </div>
        <div class="empty_box" v-show="!dataList.length">
          <div>
            <img src="@/assets/geoImage/empty.png"/>
            <span>生成的内容会在这里展示～</span>
          </div>
        </div>
      </div>
    </main>
    <footer>
      <div class="operate_button cancel" @click="dialogTableVisible = false">取消</div>
      <div class="operate_button confirm" @click="selectArticle">确定</div>
    </footer>
  </el-dialog>
</template>
<script setup>
import {reactive, ref} from "vue";
import {useDictStore} from "@/store/modules/dict";
import {cloneDeep, debounce} from "lodash-es";
import {getArticlePage, getArticlePlanPage} from "@/api/geo/productTerm";
import {Search} from "@element-plus/icons-vue";
import search from "bpmn-js/lib/features/search";
import {formatToDateTime} from "../../utils/dateUtil";

const _emit = defineEmits(['getSelectArticle'])
const aboutTableRef = ref(null)

const dialogTableVisible = ref(false) // 弹窗显示
const selectedPlans = ref([]) // 当前选中的文章
const dataList = ref([]) // 文章列表
const loading = ref(false) // 加载中

const searchVo = reactive({
  pageNo: 1,
  pageSize: 10,
  total: 100,
  title: "",
  industry: "", // 所属行业
  creationType: "", // 创作类型
})
const industryList = ref([]);
const publish_platform = ref([]);
const useDictStoreRef = useDictStore()


const creationTypeList = ref([
  "排行类",
  "测评类",
  "白皮书类",
  "推荐类",
  "自我介绍类",
  "探店类"
])

watch(() => dialogTableVisible.value, async (newValue) => {
  if (!newValue) {
    selectedPlans.value = []
    dataList.value = []
    loading.value = false
    searchVo.pageNo = 1
    searchVo.pageSize = 10
    searchVo.total = 100
    searchVo.title = ""
    searchVo.industry = ""
    searchVo.creationType = ""
  }
})


const initDataList = debounce(async function () {
  try {
    loading.value = true
    const res = await getArticlePage(searchVo)
    searchVo.total = res.total
    dataList.value = res.list.map(item => {
      item.selected = selectedPlans.value.map(item => item.id).includes(item.id)
      return item
    })
    selectionChange()
    // 使用箭头函数包装
    if (res.list.some(item => item.status !== 1)) {
      setTimeout(initDataList, 3000)
    }
  } catch (e) {
    console.log(e)
  } finally {
    loading.value = false
  }
}, 500) // 300ms 防抖延迟

// 选中行
function handleRowChoose(data) {
  data.selected = !data.selected
  if (data.selected) {
    selectedPlans.value.push(data)
  } else {
    selectedPlans.value = selectedPlans.value.filter(item => item.id !== data.id)
  }
}
// 点击 选择（row 状态selectd状态取反）
function changeSelectedPlan(row) {
  if (!row.selected) {
    selectedPlans.value.push(row)
  } else {
    selectedPlans.value = selectedPlans.value.filter(item => item.id !== row.id)
  }
  console.log(selectedPlans.value)
}

// 获取行业字典数据
async function getIndustryList() {
  const res = useDictStoreRef.getDictByType('article_industry') || [];
  industryList.value = res.map(item => item.value);
}

// 获取发布平台字典数据
async function getPlatformList() {
  publish_platform.value = await useDictStoreRef.getDictByType('publish_platform') || [];
}


// 根据平台code(string 且以,作为分隔符)获取cssClass
function getPlatformCssClass(codeString = '') {
  // codeString = "all,sohu,mp163,smzdm"
  return codeString.split(',').map(code => {
    const platform = publish_platform.value.find(item => item.value === code);
    return platform ? platform.cssClass : '';
  }).filter(cssClass => cssClass) || [];
}


// 打开弹窗
function openDialog(data) {
  dialogTableVisible.value = true;
  selectedPlans.value = cloneDeep(data);
  getIndustryList();
  getPlatformList();
  initDataList()
  selectionChange();
}

// selection 确定选中项 初始化过后再使用
function selectionChange() {
  nextTick(() => {
    const selectedPlansList = selectedPlans.value.map(item => item.id)
    aboutTableRef.value?.clearSelection()
    dataList.value.map(item => {
      if (selectedPlansList.includes(item.id)) {
        aboutTableRef.value.toggleRowSelection(item, true)
      }
    })
  })

}

//确定选择的文章
function selectArticle() {
  _emit("getSelectArticle", selectedPlans.value)
  dialogTableVisible.value = false
}

// onMounted(() => {
//   getIndustryList();
//   initDataList()
// })
defineExpose({
  openDialog
})

</script>
<style scoped lang="scss">
header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #E5E6EB;
  color: #1D2129;
  font-weight: 400;
  font-size: 16px;
  line-height: 24px;

  img {
    cursor: pointer;
  }
}

main {
  --headerTagHeight: calc(122px + 40px);
  height: calc(100vh - var(--headerTagHeight) - 150px);
  padding: 0 20px;
  background-color: #fff;
  font-family: PingFang SC, sans-serif;
  position: relative;
  overflow-y: auto;
  border-radius: 6px;
  margin-top: 20px;

  &::-webkit-scrollbar {
    display: none;
  }
}

main .header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 12px 0;

  .search_input {
    display: flex;

  }

  .button {
    cursor: pointer;

    img {
      height: 32px;
    }
  }
}

.title_box {
  font-family: PingFang SC, sans-serif;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

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

.status_box {
  width: 66px;
  height: 22px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: PingFang SC, sans-serif;
  font-weight: 400;
  font-size: 13px;
  line-height: 22px;
  border-radius: 2px;
}

.pagination_box {
  width: 97.5%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  //position: absolute;
  //bottom: 23px;
  margin-top: 23px;

  .total_box {
    color: #1D2129;
    font-family: PingFang SC, sans-serif;
    font-weight: 400;
    font-size: 14px;
    line-height: 22px;

  }
}

main .content {
  height: calc(100vh - var(--headerTagHeight) - 72px - 30px - 120px);

  :deep(.el-table__row) {
    cursor: pointer;
  }

  :deep(th.el-table__cell) {
    color: #4E5969;
    font-family: PingFang SC, sans-serif;
    font-weight: 400;
    font-size: 13px;
    line-height: 22px;
  }

  :deep(td.el-table__cell) {
    font-family: PingFang SC, sans-serif;
    font-weight: 400;
    font-size: 14px;
    line-height: 22px;
    color: #1D2129;
  }

  .keywords_box {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .platform_box {
    display: flex;
    gap: -5px;

  }


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

footer {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin: 0 0px 12px 0;
  border-top: 1px solid #E5E6EB;

  .operate_button {
    font-weight: 400;
    font-size: 14px;
    padding: 5px 16px;
    border-radius: 3px;
    cursor: pointer;
  }

  .cancel {
    color: #1D2129;
    background-color: #F7F8FA;
  }

  .confirm {
    color: #FFFFFF;
    background-color: #165DFF;
  }
}

</style>

