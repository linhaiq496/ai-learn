<template>
  <el-dialog
      v-model="dialogTableVisible"
      class="custom_dialog"
      :show-close="false"
      :close-on-press-escape="false"
      width="1200px" top="5vh"
      :destroy-on-close="true"
  >
    <header>
      <span>长尾词编辑器</span>
      <img src="@/assets/geoImage/close.png" class="w-16px h-16px" @click="dialogTableVisible = false"/>
    </header>
    <main>
      <div class="edit_box">
        <el-input type="textarea" :rows="20" v-model="keyword" placeholder="请输入长尾词"/>
      </div>
    </main>
    <footer>
      <div class="operate_button cancel" @click="dialogTableVisible = false">取消</div>
      <div class="operate_button confirm" @click="createLongKey">确定</div>
    </footer>
  </el-dialog>
</template>
<script setup>
import {ref, reactive} from 'vue'
import {updateProductKeyword, createProductKeyword, updateProductKeywordBatch} from "@/api/geo/productTerm";

const _emit = defineEmits(['initLongKeyList'])
const props = defineProps({
  productId: {
    type: Number,
    default: 0
  }
})
const dialogTableVisible = ref(false)
watch(() => dialogTableVisible.value, (val) => {
  if (!val) {
    _emit('initLongKeyList')
    dataList.ids = []
    dataList.keyWordList = []
    keyword.value = ''
  }
})
const dataList = reactive({
  ids: [],
  keyWordList: []
})
const keyword = ref('')

function openDialog(data = []) {
  dialogTableVisible.value = true
  data.map(item => {
    dataList.ids.push(item.id)
    dataList.keyWordList.push(item.keyword)
    keyword.value += item.keyword + '\n'
  })

}

// 创建或编辑词库
async function createLongKey() {
  const wordList = keyword.value.split('\n').filter(item => item)
  const ids = dataList.ids

  if (!wordList.length) {
    return ElMessage.warning('请输入长尾词')
  }
  try {
    const params = {
      updateReqVO: [],
      deleteIds: ids,
    }
    wordList.map(item => {
      params.updateReqVO.push({
        productId: props.productId,
        keyword: item
      })
    })
    const res = await updateProductKeywordBatch(params)
    if (res) {
      ElMessage.success('更新成功')
      dialogTableVisible.value = false
    }


  } catch (e) {
    console.log(e)
  }
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
  padding: 10px 0 10px 40px;
  background-color: #f7f8fa;

  .edit_box {

    :deep(.el-textarea__inner) {
      resize: none;
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-size: 16px;
      line-height: 27px;
      color: #1D2129;

      &::-webkit-scrollbar {
        display: none;
      }

      &:focus {
        box-shadow: none;
      }
    }
  }
}

footer {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  margin: 0 20px 12px 0;

  .operate_button {
    font-weight: 400;
    font-size: 14px;
    padding: 5px 16px;
    border-radius: 3px;
    cursor: pointer;
  }

  .cancel {
    color: #1D2129;
    background-color: #F7F8FA;
  }

  .confirm {
    color: #FFFFFF;
    background-color: #165DFF;
  }
}

</style>
