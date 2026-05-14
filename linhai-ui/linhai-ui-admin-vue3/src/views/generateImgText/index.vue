<template>
  <div class="generate_box" v-show="!isEditMode">
    <div class="generate_work_place">
      <generate-work-place @createImage="createImage" ref="workPlaceRef"/>
    </div>
    <div class="generate_show_place">
      <generate-show-place ref="showPlaceRef" @openEditMode="openEditMode"/>
    </div>
  </div>
  <div class="generate_box" v-show="isEditMode">
    <editArticle ref="editArticleRef" @closeEditMode="closeEditMode"/>
  </div>
</template>
<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import generateShowPlace from './modules/generateShowPlace.vue'
import generateWorkPlace from './modules/generateWorkPlace.vue'
import editArticle from './modules/editArticle.vue'
import {useRouter, useRoute} from "vue-router";

const isEditMode = ref(false)

const showPlaceRef = ref(null)
const workPlaceRef = ref(null)
const editArticleRef = ref(null)
const router = useRouter()
const route = useRoute();

// 打开创建图片模式
function createImage(data) {
  if (showPlaceRef.value) showPlaceRef.value.getNewImageList()
}

// 打开编辑模式
function openEditMode(item) {
  isEditMode.value = true
  if (editArticleRef.value) editArticleRef.value.openEditMode(item)
}

// 关闭编辑模式（返回）
function closeEditMode() {
  isEditMode.value = false
  createImage()
}


function findTemplate() {
  console.log('findTemplate')
  router.push({path: '/resourceLibrary/index'})
}

// function handleMakeSame(item) {
//   localStorage.setItem('makeSame', JSON.stringify(item))
//   router.push({
//     path: '/generate/image',
//     query: {
//       isMakeSame: true
//     }
//   })
// }

function handleMakeSame(item) {
  const isMakeSame = Number(route.query.isMakeSame) === 1;
  console.log('isMakeSame', isMakeSame)
  if (isMakeSame) {
    const makeSameData = localStorage.getItem('makeSame');
    if (makeSameData) {
      const parsedData = JSON.parse(makeSameData);
      workPlaceRef.value?.makeSameData(parsedData)
      localStorage.removeItem('makeSame');
    }
  }
}

onMounted(() => {
  handleMakeSame()
})
onUnmounted(() => {
  isEditMode.value = false
})
</script>
<style scoped lang="scss">
$top-tool-height: 0px;

$box_padding: 20px;
$tag_box_height: 0px;

.generate_box {
  $box_width: calc(100vw - $box_padding - var(--left-menu-max-width) - var(--app-content-padding));

  width: $box_width;
  height: calc(100vh - $box_padding * 2 - $tag_box_height);
  margin-top: 10px;

  display: flex;
  justify-content: flex-start;

  .generate_show_place, .generate_work_place {
    background-color: #fff;
    border-radius: 12px;
    position: relative;
    overflow: auto;
  }

  .generate_work_place {
    width: calc($box_width * 542 / (1096 + 540));
    position: relative;

    &::-webkit-scrollbar {
      display: none;
    }
  }

  .generate_show_place {
    width: calc($box_width * 1139 / (1096 + 540));
    margin-left: 20px;
  }
}
</style>
