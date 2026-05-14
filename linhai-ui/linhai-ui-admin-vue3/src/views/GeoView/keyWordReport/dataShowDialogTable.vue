<template>
  <div class="main">
    <div class="header_search">
      <div class="header_left">
        <el-input
          placeholder="搜索" v-model="searchVo.keyword" class="header_input"
                  :suffix-icon="Search" @keydown.enter="initDataList"/>
      </div>
    </div>
    <div class="search_platform">
      <div
        v-for="(item, index) in populer_platform" :key="index"
        :class="{platform_item: true, isChecked: isCheckedPlatform === item.label}"
        @click="isCheckedPlatform = item.label">
        <div :class="item.cssClass" class="w-20px h-20px"></div>
        {{ item.label }}
        {{ item.label === '全部' ? `（${searchVo.total}）` : null }}
      </div>
    </div>
    <div class="show_place">
      <el-table :data="dataList"
                :height="`calc(100vh - var(--headerTagHeight) - 14px - 20px - 56.56px - 100px)`">
        <el-table-column prop="coreKeyword" label="序号" width="100px"/>
        <el-table-column prop="extendedWords" label="核心关键词" width="450px"/>
        <el-table-column prop="extendedWords" label="蒸馏关键词" width="450px"/>
        <el-table-column prop="platform" label="平台">
          <template #default="{row}">
            <div :class="row.platform" class="w-20px h-20px"></div>
            {{ row.platform }}
          </template>
        </el-table-column>
        <el-table-column prop="source" label="来源"/>
        <el-table-column prop="findTime" label="查询时间"/>
      </el-table>
      <div class="pagination">
        <div class="page_total">
          共计{{ searchVo.total }}条
        </div>
        <el-pagination
          v-model:current-page="searchVo.pageNum"
          v-model:page-size="searchVo.pageSize"
          :page-sizes="[15, 30, 45, 90]"
          layout="prev, pager, next, sizes"
          :total="searchVo.total"
          @size-change="initDataList"
          @current-change="initDataList"
        />
      </div>

    </div>
  </div>
</template>
<script setup>
import {Search} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import {useDictStore} from "@/store/modules/dict";

const _emit = defineEmits(['lookDataDetail'])

const useDictStoreRef = useDictStore()

const searchVo = reactive({
  keyword: "",
  total: 100,
  pageNum: 1,
  pageSize: 15,
})
const dataList = ref([])

const isCheckedPlatform = ref("全部")
const populer_platform = ref([])

// 获取发布热门平台字典数据
async function getPopulerPlatformList() {
  populer_platform.value = await useDictStoreRef.getDictByType('populer_platform') || [];
  populer_platform.value.unshift(
    {value: 'all', label: '全部', colorType: '', cssClass: 'icon-all'}
  )
}


async function initDataList() {
  try {
    const params = {
      keyword: searchVo.keyword,
      pageNum: searchVo.pageNum,
      pageSize: searchVo.pageSize,
    }
    // const res = await getList(params)
    // dataList.value = res.list
    // searchVo.total = res.total
  } catch (e) {
    console.log(e)
  }
}

onMounted(() => {
  getPopulerPlatformList()
  initDataList()
})

</script>
<style scoped lang="scss">

.main {
  // --headerHeight头部高度 122px本身高度 + 20px内边距
  --headerTagHeight: calc(122px + 20px);
  // 14px上margin、20px下margin
  height: calc(100vh - var(--headerTagHeight) - 14px - 20px);
  font-family: PingFang SC, sans-serif;
  position: relative;
  overflow-y: hidden;
  border-radius: 6px;
  margin-top: 14px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  background-color: #FFFFFF;

  &::-webkit-scrollbar {
    display: none;
  }
}

.main .header_search {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .header_left {
    ::v-deep(.el-input__wrapper) {
      width: 228px;
      height: 32px;
      background-color: #F2F3F5;
      padding: 5px 12px;
    }

    ::v-deep(.el-input__icon) {
      color: #4E5969;
      font-size: 14px;
    }
  }


}

.main .search_platform {
  display: flex;
  margin-top: 13px;
  margin-right: 6px;

  .platform_item {

    font-family: PingFang SC, sans-serif;
    font-weight: 400;
    font-size: 14px;
    line-height: 22px;
    color: #4E5969;
    border-bottom: 1px solid transparent;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 3px 12px;
    height: 28px;
  }

  .isChecked {
    color: #165DFF;
    border-bottom: 1px solid #165DFF;
  }
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 13px;

  .page_total {
    font-family: PingFang SC, sans-serif;
    font-weight: 500;
    font-size: 14px;
    line-height: 22px;
    color: #1D2129;
  }

  ::v-deep(.el-pagination .el-select) {
    width: 94px;
  }
}

</style>
