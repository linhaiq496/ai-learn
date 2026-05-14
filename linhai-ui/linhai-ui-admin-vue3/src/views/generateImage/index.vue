<template>
  <div class="top_box">
    <div class="top_box_left">
      <div class="top_box_left_item" :class="{isactive: index === activeIndex}"
           v-for="(item,index) in generateList"
           :key="index" @click="activeIndex=index">
        <div class="w-20px h-20px item_card" :class="item.cssclass"></div>
        <!--  <img :src="item.defaultImg" v-show="index!==activeIndex" class="w-17px h-16px"/>-->
        <!--  <img :src="item.activeImg" v-show="index===activeIndex" class="w-17px h-16px"/>-->
        <span>{{ item.title }}</span>
      </div>
    </div>
    <div class="top_box_right">
      <!-- todo暂时隐藏 -->
      <div class="top_box_right_item" @click="findTemplate" style="display: none">
        <img src="../../assets/auto_hosting_img/image.png" class="w-17px h-16px"/>
        <span>找灵感</span>
      </div>
    </div>
  </div>
  <div class="generate_box" v-if="activeIndex === 1">
    <div class="generate_work_place">
      <generate-work-place @createImage="createImage" ref="workPlaceRef"/>
    </div>
    <div class="generate_show_place">
      <generate-show-place ref="showPlaceRef"/>
    </div>
  </div>
  <div class="generate_box" v-else>
    <commonView/>
  </div>
</template>
<script setup>
import {ref} from 'vue'
import generateShowPlace from './modules/commerce/generateShowPlace.vue'
import generateWorkPlace from './modules/commerce/generateWorkPlace.vue'
import commonView from './modules/common/index.vue'


import {useRouter, useRoute} from "vue-router";

const showPlaceRef = ref(null)
const workPlaceRef = ref(null)
const router = useRouter()
const route = useRoute();

const generateList = [
  {
    cssclass: 'item_2',
    title: '图片生成'
  },
  {
    cssclass: 'item_1',
    title: '电商生图'
  },
]
const activeIndex = ref(0)

function createImage(data) {
  showPlaceRef.value.openGetImage(data)
}

function findTemplate() {
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
</script>
<style scoped lang="scss">
$top-tool-height: 60px;
.top_box {
  display: flex;
  justify-content: space-between;
  height: 40px;
  margin-bottom: 20px;

  .top_box_left {
    height: 100%;
    display: flex;
    gap: 12px;

    .top_box_left_item {
      display: flex;
      align-items: center;
      gap: 10px;
      background-color: #fff;
      padding: 9.5px 15px;
      cursor: pointer;
      border-radius: 8px;
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-size: 16px;
      line-height: 100%;
      letter-spacing: 0;
    }


    .isactive {
      color: #505CFF;
      .item_card{
        background-color: #505CFF;
      }
    }
  }

  .top_box_right {
    height: 100%;

    .top_box_right_item {
      display: flex;
      align-items: center;
      gap: 10px;
      background-color: #fff;
      padding: 10px 33px;
      cursor: pointer;
      border-radius: 39px;
    }
  }
}

$box_padding: 20px;
$tag_box_height: 60px;

.generate_box {
  $box_width: calc(100vw - $box_padding - var(--left-menu-max-width) - var(--app-content-padding));

  width: $box_width;
  height: calc(100vh - $box_padding * 2 - $tag_box_height);

  display: flex;
  justify-content: flex-start;

  .generate_show_place, .generate_work_place {
    background-color: #fff;
    border-radius: 12px;
    position: relative;
    overflow: auto;
  }

  .generate_work_place {
    width: calc($box_width * 542 / (1139 + 540));
    position: relative;

    &::-webkit-scrollbar {
      display: none;
    }
  }

  .generate_show_place {
    width: calc($box_width * 1139 / (1139 + 540));
    margin-left: 20px;
  }
}

.item_1 {
  mask-image: url("@/assets/auto_hosting_img/img_1.png");
  mask-size: cover;
  mask-repeat: no-repeat;
  background-color: #1D2129;
}
.item_2 {
  mask-image: url("@/assets/auto_hosting_img/img_2.png");
  mask-size: cover;
  mask-repeat: no-repeat;
  background-color: #1D2129;
}
</style>
