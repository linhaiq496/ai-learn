<!--平台选择弹窗-->
<template>
  <el-dialog
      v-model="dialogTableVisible"
      class="custom_dialog"
      :show-close="false"
      :close-on-press-escape="false"
      width="740px" top="5vh"
      :destroy-on-close="true"
  >
    <header>
      <span>授权平台</span>
      <img src="@/assets/geoImage/close.png" class="w-16px h-16px" @click="dialogTableVisible = false"/>
    </header>
    <main>
      <div class="platform_list">
        <div class="platform_item" v-for="item in showPlatformList" :key="item.platformName"
             @click="selectPlatform(item)">
          <div :class="item.icon" class="w-64px h-64px"></div>
          <span class="platform_name">{{ item.platformName }}</span>
        </div>
      </div>
    </main>
  </el-dialog>
</template>
<script setup>
import {ref} from 'vue'
const _emit = defineEmits(['selectPlatform'])

const dialogTableVisible = ref(false)
const showPlatformList = ref([
  {platformName: "百家号", icon: 'icon-baijiahao'},
  {platformName: "搜狐号", icon: 'icon-souhu'},
  {platformName: "淘江湖", icon: 'icon-taojianghu'},
  {platformName: "网易号", icon: 'icon-wangyi'},
  {platformName: "雪球号", icon: 'icon-xueqiu'},
  {platformName: "凯迪网", icon: 'icon-kaidi'},
  {platformName: "今日头条", icon: 'icon-toutiao'},
  {platformName: "什么值得买", icon: 'icon-shenmezhidemai'},
])


function openDialog(data = {showPlatformList}) {
  if (data.showPlatformList) {
    // 展示部分选中的平台
    showPlatformList.value = data.showPlatformList
  } else {
    console.log("展示全部platform")
  }
  dialogTableVisible.value = true
}

function selectPlatform(platform) {
  _emit('selectPlatform', platform)
  dialogTableVisible.value = false
}

defineExpose({
  openDialog
})

</script>
<style scoped>
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
  height: 350px;
  padding: 20px;
  overflow-y: auto;

  &::-webkit-scrollbar {
    display: none;
  }

  .platform_list {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;

    .platform_item {
      width: 160px;
      height: 145px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      border-radius: 12px;
      background-color: #F5F6FA;
      gap: 15px;
      cursor: pointer;

      .platform_name {
        color: #1D3444;
        font-family: PingFang SC, sans-serif;
        font-weight: 400;
        font-size: 20px;
        line-height: 100%;
      }
    }
  }
}

</style>
