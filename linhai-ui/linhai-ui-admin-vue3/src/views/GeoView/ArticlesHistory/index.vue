<template>
  <div class="container_article">
    <headerTag :headerInfo="headerInfo"/>
    <main>
      <div class="header">
        <div class="search_input">
          <div>
            <el-input
              placeholder="搜索文章"
              v-model="searchVo.planName"
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
        <div class="button" @click="gotoCreate">
          <img src="@/assets/geoImage/begin_create.png"/>
        </div>
      </div>
      <div class="content">
        <div v-if="dataList.length > 0">
          <el-table
            :data="dataList"
            ref="aboutTableRef"
            height="calc(100vh - var(--headerTagHeight) - 150px)"
          >
            <el-table-column prop="order" label="序号" width="80">
              <template #default="scope">
                <div>{{ (searchVo.pageSize * (searchVo.pageNo - 1)) + scope.$index + 1 }}</div>
              </template>
            </el-table-column>

            <el-table-column prop="planName" label="标题" width="240">
              <template #default="scope">
                <div class="title_box">{{ scope.row.planName }}</div>
              </template>
            </el-table-column>
            <el-table-column prop="articleCount" label="文章数量" width="120"/>

            <el-table-column label="所属行业" width="120">
              <template #default="scope">
                {{ scope.row.industry }}
              </template>
            </el-table-column>
            <el-table-column prop="longTailKeywords" label="核心关键词" width="300">
              <template #default="scope">

                <el-tooltip :content="scope.row.longTailKeywords" placement="top">
                  <div class="keywords_box">{{ scope.row.longTailKeywords }}</div>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column prop="creationType" label="创作类型" width="160"/>

            <el-table-column prop="status" label="状态" width="160">
              <template #default="scope">
              <span v-if="scope.row.status === 0" class="status_box"
                    :style="{ color: '#F77234' ,background: '#FFF3E8'}">生成中</span>
                <span v-else-if="scope.row.status === 1" class="status_box"
                      :style="{ color: '#00B42A' ,background: '#E8FFEA'}">成功</span>
                <span v-else-if="scope.row.status === -1" class="status_box"
                      :style="{ color: '#F53F3F' ,background: '#FFECE8'}">失败</span>
              </template>

            </el-table-column>

            <el-table-column prop="createTime" label="创建时间" width="250">
              <template #default="scope">
                {{ formatToDateTime(scope.row.createTime) }}
              </template>
            </el-table-column>


            <el-table-column label="操作" min-width="150" fixed="right">
              <template #default="scope">
                <div class="operate_list">
                  <div class="operate_item" @click="handleEdit(scope.row)">编辑</div>
                  <div class="operate_item" @click="handleDelete(scope.row)">删除</div>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination_box">
            <div class="total_box">共 {{ searchVo.total }} 条</div>
            <el-pagination
              v-model:current-page="searchVo.pageNo"
              v-model:page-size="searchVo.pageSize"
              :page-sizes="[15, 25, 50, 100]"
              :background="true"
              layout="pager, sizes"
              :total="searchVo.total"
              @size-change="initDataList"
              @current-change="initDataList"
            />
          </div>
        </div>
        <div class="empty_box" v-else>
          <div>
            <img src="@/assets/geoImage/empty.png"/>
            <span>生成的内容会在这里展示～</span>
          </div>
        </div>
      </div>
    </main>
    <articleEdit ref="articleEditRef"/>
  </div>
</template>

<script setup>
import headerTag from '@/components/GeoView/headerTag.vue'
import headerImage from '@/assets/geoImage/all_article.png'
import {reactive, ref} from "vue";
import {Search} from "@element-plus/icons-vue";
import {deleteArticlePlan, getArticlePlanPage} from "@/api/geo/productTerm";
import {formatToDateTime} from "@/utils/dateUtil";
import {debounce} from "lodash-es";
import articleEdit from "./articleEdit.vue"
import {useDictStore} from "@/store/modules/dict";

const router = useRouter()
const articleEditRef = ref(null)

const headerInfo = reactive({
  title: "所有文章",
  desc: "您可以在此查看人工智能为您生成过的文章内容，或进行搜索、编辑、删除。",
  img: headerImage
})

const searchVo = reactive({
  pageNo: 1,
  pageSize: 15,
  total: 100,
  planName: "",
  industry: "", // 所属行业
  creationType: "", // 创作类型
})
const industryList = ref([]);
const useDictStoreRef = useDictStore()

// 获取行业字典数据
async function getIndustryList() {
  const res = useDictStoreRef.getDictByType('article_industry') || [];
  industryList.value = res.map(item => item.value);
}


const creationTypeList = ref([
  "排行类",
  "测评类",
  "白皮书类",
  "推荐类",
  "自我介绍类",
  "探店类"
])
const dataList = ref([])
const initDataListTimer = ref(null) // 定时器对象
const initDataList = debounce(async () => {
  try {
    const res = await getArticlePlanPage(searchVo);
    searchVo.total = res.total;
    dataList.value = res.list;

    // 清除之前的定时器
    if (initDataListTimer.value) {
      clearTimeout(initDataListTimer.value);
    }

    // 如果存在未完成的任务，30秒后重新获取数据
    if (res.list.some(item => item.status !== 1)) {
      initDataListTimer.value = setTimeout(() => {
        initDataList();
      }, 30000);
    }
  } catch (error) {
    console.error('获取文章计划列表失败:', error);
    ElMessage.error('获取数据失败，请稍后重试');
  }
}, 500);


async function handleEdit(row) {
  if (row.status === 0) {
    return ElMessage.warning("当前生成任务正在进行，请稍后......")
  } else if (row.status === -1) {
    return ElMessage.warning("任务生成失败，无法编辑")
  }
  articleEditRef.value.openDialog(row.id, row.planName)
}


async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个项目吗？',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    const res = await deleteArticlePlan(row.id)
    if (res) {
      await initDataList()
      ElMessage.success("删除成功")
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error("删除失败")
      console.log(e)
    }
  }
}

function gotoCreate() {
  router.push({path: '/articles/articlesCreate'})
}

// 在组件挂载时初始化行业列表
onMounted(() => {
  initDataList()
  getIndustryList();

})
onUnmounted(() => {
  clearTimeout(initDataListTimer.value)
  initDataListTimer.value = null
})
</script>
<style scoped>
main {
  --headerTagHeight: calc(122px + 40px);
  height: calc(100vh - var(--headerTagHeight));
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
}

.operate_list {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  color: #165DFF;
  font-family: PingFang SC, sans-serif;
  font-weight: 400;
  font-size: 13px;
  margin-left: -12px;

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
  position: absolute;
  bottom: 23px;

  .total_box {
    color: #1D2129;
    font-family: PingFang SC, sans-serif;
    font-weight: 400;
    font-size: 14px;
    line-height: 22px;

  }
}

main .content {
  height: calc(100vh - var(--headerTagHeight) - 61px);

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

