<template>
  <el-dialog
      v-model="dialogTableVisible"
      class="custom_dialog"
      :show-close="false"
      :close-on-press-escape="false"
      width="548px" top="20vh"
      :destroy-on-close="true"
  >
    <header>
      <span></span>
      <img src="@/assets/geoImage/close.png" class="w-16px h-16px" @click="dialogTableVisible = false"/>
    </header>
    <main class="flex">
      <div class="main_left p-12px pointer" @click="gotoDownload">
        <img src="@/assets/geoImage/downloadDesktop.png" alt="" class="w-248px h-201px"/>
      </div>
      <div class="main_right p-12px pointer" @click="gotoRetry">
        <img
            src="@/assets/geoImage/weakup.png"
            alt=""
            class="w-248px h-201px"
        />
      </div>
    </main>

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
  console.log(res)
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
  overflow-y: auto;
  display: flex;

  &::-webkit-scrollbar {
    display: none;
  }

  .main_left, .main_right {
    img {
      border: 1px solid transparent;
    }

    &:hover {
      cursor: pointer;

      img {
        border-radius: 6px;
        border: 1px solid #165DFF;
      }
    }
  }
}


</style>
