<template>
  <el-dialog
      v-model="dialogTableVisible"
      class="custom_dialog"
      :show-close="false"
      :close-on-press-escape="false"
      width="740px" top="5vh"
      :destroy-on-close="true"
  >
    <div class="dialog_box">
      <header>
        <span></span>
        <img src="@/assets/geoImage/close.png" class="w-16px h-16px" @click="dialogTableVisible = false"/>
      </header>
      <main>
        <div>
          <img src="@/assets/geoImage/row.png" alt="" class="w-56px h-56px"/>
        </div>
        <div class="color-[#0A1B39] text-16px mt-20px">本地服务未连接</div>
        <div class="color-[#4E5969] text-13px mt-4px">当前功能需要本地客户端支持，请确保应用已在运行或重新尝试连接。</div>
      </main>
      <footer>
        <div @click="gotoRetry" class="button_operation color-[#1D2129] bg-[#F7F8FA]">立即重试</div>
        <div @click="gotoDownload" class="button_operation color-[#FFFFFF] bg-[#165DFF]">下载安装</div>
      </footer>
    </div>
  </el-dialog>
</template>
<script setup>
import {ref} from 'vue'
import {useDictStore} from "@/store/modules/dict";

const dialogTableVisible = ref(false)
const useDictStoreRef = useDictStore()

function openDialog() {
  dialogTableVisible.value = true
}

function gotoRetry() {
  window.open('postbot://')
  dialogTableVisible.value = false
}

async function gotoDownload() {
  const res = await useDictStoreRef.getDictByType('desptop_download_url')
  if (res[0] && res[0].value) {
    window.open(res[0].value || '')
  }
  dialogTableVisible.value = false
}

defineExpose({
  openDialog
})

</script>
<style scoped>
.dialog_box {
  background: url("@/assets/geoImage/dialog_bgc.png");
}

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
  height: 320px;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  &::-webkit-scrollbar {
    display: none;
  }

  & > div {
    font-family: PingFang SC, sans-serif;
    font-weight: 400;
    line-height: 22px;
  }
}

footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  padding: 12px;
  border-top: 1px solid #e5e6eb;

  .button_operation {
    width: 80px;
    height: 32px;
    opacity: 1;
    border-radius: 3px;
    padding: 5px 12px;
    cursor: pointer;
  }
}

</style>
