<template>
  <el-dialog
      v-model="dialogTableVisible"
      class="custom_dialog"
      :show-close="false"
      :close-on-press-escape="false"
      width="1200px" top="5vh"
      :destroy-on-close="true"
  >
    <template #title>
    </template>
    <template #default>
      <div class="dialog_box">
        <header>
          <span> {{ isEdit ? '修改产品词库' : '新增产品词库' }} </span>
          <img src="@/assets/geoImage/close.png" class="w-16px h-16px" @click="dialogTableVisible = false"/>
        </header>
        <main>
          <div class="key_box">
            <div class="set_title">
              <div>Step1</div>
              <span>基础配置</span>
            </div>
            <div class="icon">
              <span class="mb-12px flex">产品图标</span>
              <div class="operate_icon">
                <img src="@/assets/geoImage/keywordIcon/icon.png"/>
                <div class="edit" @click="editIcon"></div>
              </div>
            </div>

            <div class="title">
              <span class="mb-12px flex">词库名称</span>
              <el-input v-model="dataList.name" placeholder="请输入词库名称"/>
            </div>

            <div class="title">
              <span class="mb-12px flex">所属行业</span>
              <el-select v-model="dataList.industry" placeholder="请选择所属行业">
                <el-option
                    v-for="item in industryList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"/>
              </el-select>
            </div>


            <div class="title">
              <span class="mb-12px flex">智能策略</span>
              <!--              <el-button type="primary" @click="aiGetData" v-loading="loading">Agent挖掘词缀</el-button>-->
              <div class="agent_get_data" @click="aiGetData" v-loading="loading">
                <div>
                  <img alt="" src="@/assets/geoImage/AgentGetData.png" class="w-24px h-24px"/>
                </div>
                <div class="text1">Agent挖掘词缀</div>
                <div class="text2">根据产品词库名称自动挖掘前、后缀词</div>
              </div>
            </div>
          </div>

          <div class="about_box">
            <div class="set_title">
              <div>Step2</div>
              <span>进阶配置</span>
            </div>
            <div class="before_word2">
              <div class="word_header">
                <span>前缀词 <img src="@/assets/geoImage/about_detail.png" class="w-13px h-13px ml-1px"/></span>
                <div class="export" @click="insertWord(1)">
                  <el-icon color="#165DFF">
                    <Download/>
                  </el-icon>
                  导入推荐词
                </div>
                <div class="clear" @click="dataList.beforeWord=''">
                  <el-icon color="#F53F3F">
                    <DeleteFilled/>
                  </el-icon>
                  一键清空
                </div>
              </div>
              <el-input
                  v-model="dataList.beforeWord"
                  type="textarea"
                  :rows="12"
              />
            </div>
            <div class="before_word2 mt-20px">
              <div class="word_header">
                <span>后缀词 <img src="@/assets/geoImage/about_detail.png" class="w-13px h-13px ml-1px"/></span>
                <div class="export" @click="insertWord(2)">
                  <el-icon color="#165DFF">
                    <Download/>
                  </el-icon>
                  导入推荐词
                </div>
                <div class="clear" @click="dataList.afterWord=''">
                  <el-icon color="#F53F3F">
                    <DeleteFilled/>
                  </el-icon>
                  一键清空
                </div>
              </div>
              <el-input
                  v-model="dataList.afterWord"
                  type="textarea"
                  :rows="12"
              />
            </div>
          </div>
        </main>
        <footer>
          <div class="operate_button cancel" @click="dialogTableVisible = false">取消</div>
          <div class="operate_button confirm" @click="createCoreKey">{{ isEdit ? '修改词库' : '创建词库' }}</div>
        </footer>
      </div>
    </template>
  </el-dialog>
</template>
<script setup>

import {ref} from 'vue'
import {ElMessage} from "element-plus";
import {DeleteFilled, Download} from "@element-plus/icons-vue";
import {
  createProductTerm,
  getMiningLibraryRecommend,
  updateProductTerm
} from "@/api/geo/productTerm";
import {useDictStore} from "@/store/modules/dict";

const emit = defineEmits(['initAll'])

const useDictStoreRef = useDictStore()
const dialogTableVisible = ref(false)
const isEdit = ref(false)

const type = ref(null)
const loading = ref(false)

const dataList = reactive({
  id: null,
  name: '',
  icon: '',
  industry: '',
  beforeWord: "",
  afterWord: "",
})
const industryList = ref([])

watch(dialogTableVisible, (newValue) => {
  if (!newValue) {
    dataList.name = ''
    dataList.icon = ''
    dataList.beforeWord = ""
    dataList.afterWord = ""

    emit('initAll', true)
  }
})


// 获取行业字典数据
async function getIndustryList() {
  industryList.value = useDictStoreRef.getDictByType('article_industry') || [];
}


function openDialog(data) {
  dialogTableVisible.value = true
  isEdit.value = !!data
  if (isEdit.value) {
    dataList.id = data.id
    dataList.name = data.name
    dataList.beforeWord = data.prefix.replace(/,/g, '\n')
    dataList.afterWord = data.suffix.replace(/,/g, '\n')
  }
//   _emit('writeInKeyword', type.value, 'test')
}

function editIcon() {
  ElMessage({
    message: '选择图标功能暂未开放',
    type: 'warning',
  })

}

function insertWord(type) {
  if (type === 1) {
    dataList.beforeWord +=
        `\n口碑好的
耐用的
比较好的
靠谱的
有实力的
可靠的
诚信的
正规的
专业的
质量好的
热门的
知名的
评价高的
优质的
优秀的`
  } else {
    dataList.afterWord +=
        '\n品牌推荐\n' +
        '公司推荐\n' +
        '品牌排行\n' +
        '公司排行\n' +
        '品牌排名\n' +
        '公司排名\n' +
        '品牌电话\n' +
        '公司电话\n' +
        '品牌哪家好\n' +
        '公司哪家好\n' +
        '品牌哪家强\n' +
        '公司哪家强\n' +
        '品牌怎么选\n' +
        '公司怎么选\n' +
        '品牌有哪些\n' +
        '公司有哪些\n' +
        '品牌哪家权威\n' +
        '公司哪家权威\n' +
        '品牌哪里有卖\n' +
        '公司哪里有卖\n' +
        '品牌哪家靠谱\n' +
        '公司哪家靠谱\n' +
        '品牌哪家专业\n' +
        '公司哪家专业\n' +
        '品牌哪家质量好\n' +
        '公司哪家质量好\n' +
        '品牌哪家便宜\n' +
        '公司哪家便宜\n' +
        '品牌哪家可靠\n' +
        '公司哪家可靠\n' +
        '品牌推荐几家\n' +
        '公司推荐几家\n' +
        '品牌帮我推荐几家\n' +
        '公司帮我推荐几家\n' +
        '品牌如何选\n' +
        '公司如何选\n' +
        '品牌选哪家\n' +
        '公司选哪家\n' +
        '品牌哪里有\n' +
        '公司哪里有'
  }
}

async function createCoreKey() {
  if (!dataList.name) {
    return ElMessage({
      message: '请输入词库名称',
      type: 'warning',
    })
  } else if (!dataList.industry) {
    return ElMessage({
      message: '请选择行业',
      type: 'warning',
    })
  }
  try {

    const param = {
      name: dataList.name,
      prefix: dataList.beforeWord
          .split('\n') // 按行分割
          .filter(line => line.trim() !== '') // 过滤掉空行
          .join(',') // 用逗号连接
          .trim(), // 去除首尾空白
      suffix: dataList.afterWord
          .split('\n') // 按行分割
          .filter(line => line.trim() !== '') // 过滤掉空行
          .join(',') // 用逗号连接
          .trim(), // 去除首尾空白
      status: 1
    }

    if (isEdit.value) {
      param.id = dataList.id
    }
    if (isEdit.value) {
      const res = await updateProductTerm(param)
      if (res) {
        ElMessage({
          message: '修改成功',
          type: 'success',
        })
        return dialogTableVisible.value = false
      }
    } else {
      const res = await createProductTerm(param)
      if (res) {
        ElMessage({
          message: '创建成功',
          type: 'success',
        })
        return dialogTableVisible.value = false
      }
    }

  } catch (e) {
    console.log(e)
  }
}

async function aiGetData() {
  if (loading.value) {
    return ElMessage.success('正在挖掘中，请稍后')
  }
  loading.value = true
  try {
    if (!dataList.name || !dataList.name.trim()) {
      return ElMessage({
        message: '请输入词库名称',
        type: 'warning',
      })
    } else if (!dataList.industry) {
      return ElMessage({
        message: '请选择行业',
        type: 'warning',
      })
    }
    const res = await getMiningLibraryRecommend(dataList.name, dataList.industry)
    dataList.beforeWord = res.prefix.replace(/,/g, '\n')
    dataList.afterWord = res.suffix.replace(/,/g, '\n')
  } catch (e) {
    ElMessage({
      message: '挖掘失败！请稍后重试',
      type: 'error',
    })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getIndustryList()
})

defineExpose({
  openDialog
})


</script>
<style scoped lang="scss">
span {
  color: #1D2129;
  font-family: PingFang SC, sans-serif;
  font-weight: 400;
  font-size: 16px;
  line-height: 24px;
}

.dialog_box {
  font-family: PingFang SC, sans-serif;
  padding-bottom: 1px;

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
    display: flex;
    flex-direction: row;
    height: 686px;
    border-bottom: 1px solid #E5E6EB;

    .key_box {
      width: 35%;
      height: 100%;
      border-right: 1px solid #E5E6EB;
      background-color: #F2F3F5;
      padding: 20px 16px;

      .set_title {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 21px;

        div {
          color: #165DFF;
          font-family: PingFang SC, sans-serif;
          font-weight: 400;
          font-size: 12px;
          line-height: 16px;
          background-color: #E8F3FF;
          min-width: 48px;
          min-height: 22px;
          border-radius: 3px;
          padding: 3px 7px;
          border: 1px solid #165DFF;
        }


      }

      .icon {
        margin-bottom: 16px;

        .operate_icon {
          background-color: #165DFF;
          width: 48px;
          height: 48px;
          border-radius: 6px;
          display: flex;
          justify-content: center;
          align-items: center;
          position: relative;

          img {
            width: 24px;
          }

          .edit {
            width: 25px;
            height: 25px;
            position: absolute;
            background-image: url(../../../assets/geoImage/edit_2.png);
            background-size: 100% 100%;
            background-repeat: no-repeat;
            right: -10px;
            bottom: -10px;
            cursor: pointer;
          }
        }
      }

      .title {
        margin-bottom: 16px;
      }

      .agent_get_data {
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
        background-color: #fff;
        padding: 12px;
        border-radius: 6px;
        border: 1px solid #E5E6EB;
        cursor: pointer;
        transition:all 0.3s cubic-bezier(0.15, 0.62, 0.35, 0.88);

        &:hover {
          border: 1px solid #165DFF;
        }

        .text1 {
          margin-top: 12px;
          font-family: PingFang SC, sans-serif;
          font-weight: 400;
          font-size: 14px;
          line-height: 22px;
          color: #1D2129;
        }

        .text2 {
          font-family: PingFang SC, sans-serif;
          font-weight: 400;
          font-size: 12px;
          line-height: 20px;
          color: #86909C;
        }
      }

    }

    .about_box {
      width: 80%;
      height: 100%;
      border-right: 1px solid #E5E6EB;
      padding: 20px 16px;

      .set_title {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 21px;

        div {
          color: #165DFF;
          font-family: PingFang SC, sans-serif;
          font-weight: 400;
          font-size: 12px;
          line-height: 16px;
          background-color: #E8F3FF;
          min-width: 48px;
          min-height: 22px;
          border-radius: 3px;
          padding: 3px 7px;
          border: 1px solid #165DFF;
        }
      }

      .before_word2 {
        .word_header {
          display: flex;
          justify-content: flex-start;
          gap: 10px;
          align-items: center;
          font-family: PingFang SC, sans-serif;
          font-weight: 400;
          font-size: 14px;
          line-height: 22px;
          margin-bottom: 8px;

          span {
            color: #1D2129;
          }

          .export {
            color: #165DFF;
            display: flex;
            align-items: center;
            cursor: pointer;
          }

          .clear {
            color: #F53F3F;
            display: flex;
            align-items: center;
            cursor: pointer;
          }
        }

        :deep(.el-textarea__inner) {
          border-radius: 8px;
          border: 1px solid #E5E6EB;
          box-shadow: none;
          resize: none;
          scrollbar-width: none;
          scrollbar-color: rgba(22, 93, 255, 0.1) transparent;
        }
      }
    }
  }

  footer {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    margin: 12px 0;

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

}
</style>
