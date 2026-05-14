<template>
  <div class="headerTag">
    <div class="img">
      <img :src="headerInfo.img"/>
    </div>
    <div class="content">
      <div class="title">{{ headerInfo.title }}</div>
      <div class="desc" v-html="headerInfo.desc"></div>
    </div>
  </div>

</template>
<script setup>

import {toRefs, watch} from "vue";

const _props = defineProps({
  headerInfo: {
    type: Object,
    default: () => {
      return {}
    }
  },
})
// const {headerInfo} = toRefs(_props)
</script>
<style scoped>
.headerTag {
  width: 100%;
  height: 122px;
  border-radius: 6px;
  background-image: url(../../assets/geoImage/headerTag.png);
  background-repeat: no-repeat;
  background-size: 100% 100%;
  display: flex;
  align-items: center;
  justify-content: flex-start;

  font-family: PingFang SC, sans-serif;
  font-weight: 400;
  line-height: 22px;

  .img {
    height: 100%;
    width: 122px;
    margin: 0 20px;

    img {
      width: 100%;
      height: 100%;
    }
  }

  .content {
    height: 100%;
    display: flex;
    justify-content: center;
    flex-direction: column;
    font-family: PingFang SC, sans-serif;

    .title {
      font-size: 32px;
      color: #1D2129;
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      line-height: 38px;
    }

    .desc {
      font-size: 14px;
      color: #4E5969;
      margin-top: 11px;
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      line-height: 22px;
    }
  }
}
</style>
