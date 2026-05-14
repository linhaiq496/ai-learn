<!--现页面用的逻辑是《所有文章》的，后端接口出了之后整体逻辑仍然需要修改-->
<template>
  <div class="container_article">
    <headerTag :headerInfo="headerInfo"/>
    <main>
      <div class="header">
        <div class="search_input">
          <div>
            <el-input
              placeholder="搜索文章"
              v-model="searchVo.title"
              :suffix-icon="Search"
              @keydown="initDataList"
            />
          </div>
        </div>
        <div class="button" @click="gotoCreate">
          <img src="@/assets/geoImage/auto_publish.png"/>
        </div>
      </div>
      <div class="content">
        <div v-if="dataList.length > 0">
          <el-table
            :data="dataList"
            ref="aboutTableRef"
            max-height="calc(100vh - var(--headerTagHeight) - 160px)"
          >
            <el-table-column prop="order" label="序号" width="80">
              <template #default="scope">
                <div>{{ (searchVo.pageSize * (searchVo.pageNo - 1)) + scope.$index + 1 }}</div>
              </template>
            </el-table-column>

            <!--            <el-table-column prop="title" label="标题" width="240">-->
            <!--              <template #default="scope">-->
            <!--                <div class="title_box">{{ scope.row.title }}</div>-->
            <!--              </template>-->
            <!--            </el-table-column>-->
            <!--      发布内容      -->
            <el-table-column label="发布内容" width="350">
              <template #default="scope">
                <div class="content_box" v-html="scope.row.title"></div>
              </template>
            </el-table-column>


            <el-table-column prop="createTime" label="发布账号">
              <template #default="scope">
                <div class="flex items-center gap-7px">
                  {{ scope.row.account }}
                  <div :class="[getPlatformCssClass(scope.row.platform)]"
                       class="w-15px h-15px rounded-full"></div>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="发布平台">
              <template #default="scope">
                <div class="flex gap-10px">
                  <div :class="[getPlatformCssClass(scope.row.platform)]"
                       class="w-20px h-20px"></div>
                  {{ getPlatformName(scope.row.platform) }}
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="createTime" label="创建时间">
              <template #default="scope">
                {{ formatToDateTime(scope.row.createTime) }}
              </template>
            </el-table-column>

            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <div
                  v-if="scope.row.status === 'publishing'"
                  class="status_box"
                  :style="{ color: '#F77234', background: '#FFF3E8' }"
                >
                  发布中
                </div>
                <div
                  v-else-if="scope.row.status === 'success'"
                  class="status_box"
                  :style="{ color: '#00B42A', background: '#E8FFEA' }"
                >
                  发布成功
                </div>
                <div
                  v-else-if="scope.row.status === 'fail'"
                  class="status_box"
                  :style="{ color: '#F53F3F', background: '#FFECE8' }"
                >
                  发布失败
                </div>
                <div
                  v-else-if="scope.row.status === 'offline'"
                  class="status_box"
                  :style="{ color: '#86909C', background: '#F2F3F5' }"
                >
                  客户端离线
                </div>
              </template>

            </el-table-column>

            <el-table-column label="操作" min-width="150" fixed="right">
              <template #default="scope">
                <div class="operate_list" v-if="scope.row.status === 'success'">
                  <div class="operate_item" @click="lookDetail(scope.row)">查看详情</div>
                </div>
                <div class="operate_list"
                     v-else-if="scope.row.status === 'fail' || scope.row.status === 'offline'">
                  <div class="operate_item color-[#F53F3F]! underline"
                       @click="handleRepublish(scope.row)">重新发布
                  </div>
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
              @size-change="sizeChange"
              @current-change="sizeChange"
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
  </div>
</template>

<script setup>
import headerTag from '@/components/GeoView/headerTag.vue'
import headerImage from '@/assets/geoImage/all_article.png'
import {reactive, ref} from "vue";
import {Search} from "@element-plus/icons-vue";
import {getArticlePlanPage} from "@/api/geo/productTerm";
import {formatToDateTime} from "@/utils/dateUtil";
import {debounce} from "lodash-es";
import {getPublishTaskLogsPage, rePublish} from "@/api/geo/publishTask";
import {useDictStore} from "@/store/modules/dict";

const router = useRouter()

const headerInfo = reactive({
  title: "发布记录",
  desc: "可以查看您将文章发布到各个关联平台的发布记录，并管理发布过的文章。",
  img: headerImage
})
const useDictStoreRef = useDictStore()

const searchVo = reactive({
  pageNo: 1,
  pageSize: 15,
  total: 100,
  title: "",
})
const publish_platform = ref([])
const dataList = ref([])
const timer = ref(null)


// 初始化 数据
const initDataList = async function () {
  try {
    const res = await getPublishTaskLogsPage(searchVo);
    searchVo.total = res.total;
    dataList.value = res.list || [];

    // 检查是否有正在发布的任务，若有则定时刷新
    const hasPublishing = res.list?.some(item => item.status === 'publishing');

    if (hasPublishing) {
      if (timer.value) clearTimeout(timer.value);
      timer.value = setTimeout(initDataList, 5000);
    } else if (timer.value) {
      // 如果没有正在发布的任务，清除定时器
      clearTimeout(timer.value);
      timer.value = null;
    }
  } catch (e) {
    console.error('获取发布记录列表失败:', e);
    dataList.value = [];
  }
}

// 分页组件数值改变
function sizeChange() {
  if (timer.value) clearTimeout(timer.value);
  initDataList()
}

// 查看详情
async function lookDetail(row) {
  ElMessage.error('功能开发中')
}

// 重新发布
async function handleRepublish(row) {
  try {
    await ElMessageBox.confirm('确定要重新发布吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    const res = await rePublish({integers: [row.id]});
    ElMessage.success('重新发布成功');
  } catch (e) {
    if (e !== 'cancel') {
      console.log(e);
    }
  }
}

// 获取字典 （发布平台）
async function getPlatformList() {
  publish_platform.value = await useDictStoreRef.getDictByType('publish_platform') || [];
}

// 根据code获取平台图标
function getPlatformCssClass(code) {
  return publish_platform.value.find(item => item.value === code)?.cssClass || ''
}

// 根据code获取平台名称
function getPlatformName(code) {
  return publish_platform.value.find(item => item.value === code)?.label || ''
}


function gotoCreate() {
  router.push({path: '/media/AutomaticPublishing'})
}


onMounted(() => {
  getPlatformList()
  initDataList()
})
onUnmounted(() => {
  if (timer.value) clearTimeout(timer.value);
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

.content_box {
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
  }

}

.status_box {
  width: 100px;
  height: 22px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: PingFang SC, sans-serif;
  font-weight: 400;
  font-size: 13px;
  line-height: 22px;
  border-radius: 2px;
  padding: 0 5px;
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

