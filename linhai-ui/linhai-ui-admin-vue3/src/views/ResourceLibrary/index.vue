<template>
  <div
      class="resourceLibrary"
      ref="resourceLibrary_ref"
      @scroll="handleScroll"
  >
    <!-- 标签栏 -->
    <div class="tag_menu">
      <div
          class="tag_item"
          v-for="(item, index) in tagList"
          :key="item.value + index"
          :class="{ isChooseTag: chooseTag === item.value }"
          @click="handleChooseTag(item.value)"
      >
        {{ item.tagName }}
      </div>
    </div>

    <!-- 瀑布流 -->
    <Waterfall
        :list="examplesList"
        :cols="cols"
        :gutter="12"
        :width="310"
        :imgProps="{ loading: 'lazy' }"
        ref="waterfallRef"
    >
      <template #default="{ item }">
        <div class="resource_item">
          <img
              :src="item.imgUrl"
              @load="$emit('load')"
          />

          <!-- hover 操作区 -->
          <div class="operate_button">
            <div class="operate_desc">
              {{ item.desc }}
            </div>
            <div class="operate_do" @click="handleMakeSame(item)">
              <img src="../../assets/auto_hosting_img/makeSame.png"/>
              <span>做同款</span>
            </div>
          </div>
        </div>
      </template>
    </Waterfall>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted, onBeforeUnmount} from 'vue'
import {Waterfall} from 'vue-waterfall-plugin-next'

import 'vue-waterfall-plugin-next/dist/style.css'


import {
  createMaterialAsset,
  getMaterialAssetPage,
  getMaterialCategoryList,
  deleteMaterialAsset
} from "@/api/resourceLibrary";
import {useRouter} from "vue-router";


const router = useRouter()
const waterfallRef = ref(null)
// ================== refs ==================
const resourceLibrary_ref = ref(null)
const chooseTag = ref(null)
const cols = ref(4)

// ================== 分页参数 ==================
const searchVo = reactive({
  pageNo: 1,
  pageSize: 20,
  total: 100,
  categoryId: ''
})

// ================== 标签 ==================
const tagList = ref([])

const updateTimer = ref(null)


// ================== 列表数据 ==================
const examplesList = ref([])

// ================== 滚动加载 ==================
let scrollTimer = null

//选择tag
function handleChooseTag(tagCode) {
  chooseTag.value = tagCode
  searchVo.pageNo = 1
  searchVo.total = 100
  loadMoreImages(false)

}

// 滚动监听
function handleScroll(e) {
  if (scrollTimer) return

  scrollTimer = setTimeout(() => {
    scrollTimer = null

    const el = e.target
    const currentHeight = el.scrollTop + el.clientHeight + 200

    if (
        currentHeight >= el.scrollHeight &&
        searchVo.pageNo * searchVo.pageSize < searchVo.total
    ) {
      searchVo.pageNo++
      loadMoreImages(true)
    }
  }, 300)
}

// 加载 图片
async function loadMoreImages(isPush = false) {
  try {
    searchVo.categoryId = chooseTag.value
    const res = await getMaterialAssetPage(searchVo)
    searchVo.total = res.total
    if (isPush) {
      examplesList.value.push(...res.list.map(item => {
        return {
          ...item,
          imgUrl: item.resultImageUrl,
          desc: item.finalPrompt || '无'
        }
      }))
    } else {
      examplesList.value = res.list.map(item => {
        return {
          ...item,
          imgUrl: item.resultImageUrl,
          desc: item.finalPrompt || '无'
        }
      })
    }
  } catch (e) {

  }
}

// 初始化 商品类别
async function initTags() {
  try {
    const res = await getMaterialCategoryList("")
    tagList.value = res.map(item => {
      return {
        tagName: item.name,
        value: item.id
      }
    }).filter(item => item)
  } catch (e) {
    console.log(e)
  }

}

// ================== 响应式列数 ==================
function updateCols() {
  const width = window.innerWidth
  if (width < 1000) cols.value = 2
  else if (width < 1400) cols.value = 3
  else cols.value = 4
}


function handleMakeSame(item) {
  localStorage.setItem('makeSame', JSON.stringify(item))
  router.push({
    path: '/generate/image',
    query: {
      isMakeSame: 1
    }
  })
}


// async function test() {
// //   // 批量创建素材资产
//   const imageUrls = [
//     "http://192.168.1.50:48080/admin-api/infra/file/29/get/20260204/1_1770207006649.jpg",
//     "http://192.168.1.50:48080/admin-api/infra/file/29/get/20260204/10_1770207006673.jpg",
//     "http://192.168.1.50:48080/admin-api/infra/file/29/get/20260204/2_1770207006695.jpg",
//     "http://192.168.1.50:48080/admin-api/infra/file/29/get/20260204/3_1770207006721.jpg",
//     "http://192.168.1.50:48080/admin-api/infra/file/29/get/20260204/4_1770207006742.jpg",
//     "http://192.168.1.50:48080/admin-api/infra/file/29/get/20260204/5_1770207006767.jpg",
//     "http://192.168.1.50:48080/admin-api/infra/file/29/get/20260204/6_1770207006795.jpg",
//     "http://192.168.1.50:48080/admin-api/infra/file/29/get/20260204/7_1770207006820.jpg",
//     "http://192.168.1.50:48080/admin-api/infra/file/29/get/20260204/8_1770207006843.jpg",
//     "http://192.168.1.50:48080/admin-api/infra/file/29/get/20260204/9_1770207006863.jpg",
//
//   ]
//
//   // 批量创建素材资产
//   for (const url of imageUrls) {
//     try {
//       await createMaterialAsset({
//         "categoryId": 2,
//         "productImageUrl": [url],
//         "finalPrompt": "",
//         "ratio": "1:1",
//         "resultImageUrl": url
//       });
//     } catch (error) {
//       console.error("创建素材资产失败:", error);
//     }
//   }
// }

// test()
function handleRender() {
  if (waterfallRef.value) {
    waterfallRef.value.renderer()
  }
}

onMounted(async () => {
  await initTags()
  handleChooseTag(tagList.value[0]?.value)
  updateCols()
  updateTimer.value = setInterval(handleRender, 2000)
  // window.addEventListener('resize', handleRender)
})

onBeforeUnmount(() => {
  clearInterval(updateTimer.value)
  // window.removeEventListener('resize', handleRender)
})

</script>

<style scoped lang="scss">
$box-padding: 20px;

.resourceLibrary {
  padding: 22px 25px;
  width: calc(100vw - var(--left-menu-max-width) - var(--app-content-padding) * 2);
  height: calc(100vh - $box-padding * 2 - var(--top-tool-height));
  background: #fff;
  border-radius: 12px;
  overflow-y: auto;

  &::-webkit-scrollbar {
    display: none;
  }
}

/* 标签栏 */
.tag_menu {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;

  .tag_item {
    padding: 4px 14px;
    border-radius: 16px;
    color: #7c7d84;
    cursor: pointer;

    &:hover {
      background: #f1f4ff;
    }
  }

  .isChooseTag {
    color: #505cff;
    background: #f1f4ff;
    font-weight: 500;
  }
}

/* 卡片 */
.resource_item {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
}

.resource_item img {
  width: 100%;
  display: block;
}

/* hover 操作区 */
.operate_button {
  position: absolute;
  inset: 0;
  padding-bottom: 16px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s;
  background: linear-gradient(
          to top,
          rgba(0, 0, 0, 0.55),
          rgba(0, 0, 0, 0)
  );
}

.resource_item:hover .operate_button {
  opacity: 1;
}

.operate_desc {
  width: calc(100% - 40px);
  color: #fff;
  font-size: 14px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.operate_do {
  width: calc(100% - 40px);
  margin-top: 12px;
  padding: 10px 0;
  background: #3f69ff;
  color: #fff;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.operate_do img {
  width: 20px;
  height: 20px;
}
</style>
