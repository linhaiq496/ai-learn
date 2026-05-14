<template>
  <div class="container_knowledge">
    <headerTag :headerInfo="headerInfo"/>
    <main>
      <div class="left_box">
        <div class="left_box_item" :class="{item_active: chooseView === item.key}" v-for="item in viewList"
             :key="item.key" @click="chooseView = item.key">
          {{ item.label }}
        </div>
      </div>
      <div class="right_box">
        <div class="right_box_title">
          <span>{{ viewList[chooseView - 1].label }}</span>
          <div class="editor_box" v-show="!isEdit" @click="startEdit">
            <el-icon>
              <Edit/>
            </el-icon>
            <span>编辑</span>
          </div>
        </div>
        <div class="right_box_content">
          <!--     公司介绍     -->
          <div v-show="chooseView === 1">
            <div class="content_show" v-show="!isEdit">
              {{ editData.companyIntroduction }}
            </div>
            <el-input
                v-show="isEdit"
                v-model="editData.companyIntroduction"
                type="textarea"
                :autosize="{ minRows: 10, maxRows: 10 }"
                placeholder="请输入内容"
                style="width: 100%; height: 100%;"
            />
          </div>
          <!--     产品背书     -->
          <div v-show="chooseView === 2">
            <div class="content_show" v-show="!isEdit">
              {{ editData.productsServices }}
            </div>
            <el-input
                v-show="isEdit"
                v-model="editData.productsServices"
                type="textarea"
                :autosize="{ minRows: 10, maxRows: 10 }"
                placeholder="请输入内容"
                style="width: 100%; height: 100%;"
            />
          </div>
          <!--     信任背书     -->
          <div v-show="chooseView === 3">
            <div class="content_show" v-show="!isEdit">
              {{ editData.trustBacking }}
            </div>
            <el-input
                v-show="isEdit"
                v-model="editData.trustBacking"
                type="textarea"
                :autosize="{ minRows: 10, maxRows: 10 }"
                placeholder="请输入内容"
                style="width: 100%; height: 100%;"
            />
          </div>
          <!--     合作案例     -->
          <div v-show="chooseView === 4">
            <div class="content_show" v-show="!isEdit">
              {{ editData.cooperationCases }}
            </div>
            <el-input
                v-show="isEdit"
                v-model="editData.cooperationCases"
                type="textarea"
                :autosize="{ minRows: 10, maxRows: 10 }"
                placeholder="请输入内容"
                style="width: 100%; height: 100%;"
            />
          </div>
          <!--     AI名片     -->
          <div v-show="chooseView === 5">
            <div>
              <div class="content_title">网址</div>
              <div class="content_show" v-show="!isEdit">
                {{ editData.businessCardWebsite }}
              </div>
              <el-input
                  v-show="isEdit"
                  v-model="editData.businessCardWebsite"
                  type="textarea"
                  :autosize="{ minRows: 1, maxRows: 1 }"
                  placeholder="请输入内容"
                  style="width: 100%; height: 100%;"
              />

              <div class="content_title">电话</div>
              <div class="content_show" v-show="!isEdit">
                {{ editData.businessCardPhone }}
              </div>
              <el-input
                  v-show="isEdit"
                  v-model="editData.businessCardPhone"
                  type="textarea"
                  :autosize="{ minRows: 1, maxRows: 1 }"
                  placeholder="请输入内容"
                  style="width: 100%; height: 100%;"
              />

              <div class="content_title">邮箱</div>
              <div class="content_show" v-show="!isEdit">
                {{ editData.businessCardEmail }}
              </div>
              <el-input
                  v-show="isEdit"
                  v-model="editData.businessCardEmail"
                  type="textarea"
                  :autosize="{ minRows: 1, maxRows: 1 }"
                  placeholder="请输入内容"
                  style="width: 100%; height: 100%;"
              />

            </div>

          </div>
          <div v-show="chooseView === 6">
            <div class="content_show" v-show="!isEdit">
              {{ editData.otherInfo }}
            </div>
            <el-input
                v-show="isEdit"
                v-model="editData.otherInfo"
                type="textarea"
                :autosize="{ minRows: 10, maxRows: 10 }"
                placeholder="请输入内容"
                style="width: 100%; height: 100%;"
            />
          </div>
        </div>
        <div class="right_box_footer" v-show="isEdit">
          <div @click="handleCannelEdit">取消</div>
          <div @click="handleKeepEdit">保存</div>
        </div>
      </div>
    </main>
  </div>
</template>
<script setup>
import {ref} from "vue";
import headerTag from "@/components/GeoView/headerTag.vue";
import headerImage from '@/assets/geoImage/importantKeys.png'
import {Edit} from "@element-plus/icons-vue";
import {getCompanyKnowledgeBaseByTenant, updateCompanyKnowledgeBase} from "@/api/geo/knowledgeBase";
import {cloneDeep} from "lodash-es";

const headerInfo = reactive({
  title: "知识库",
  desc: "人工智能会学习您录入的公司信息，为您定制输出更适合您企业的文章。",
  img: headerImage
})

//操作栏tab
const viewList = [
  {label: '公司介绍', key: 1},
  {label: '产品背书', key: 2},
  {label: '信任背书', key: 3},
  {label: '合作案例', key: 4},
  {label: 'AI名片', key: 5},
  {label: '其他', key: 6},
]
const chooseView = ref(1) // 当前选中的tab 对应 viewList的key
const isEdit = ref(false) // 是否处于编辑状态
const editData = ref({
  id: null, // 主键ID, 类型: number|null, 示例: 16110
  companyIntroduction: "", // 公司介绍内容, 类型: string
  productsServices: "", // 产品/服务内容 (可以是逗号分隔的字符串或文本描述), 类型: string
  trustBacking: "", // 信任背书内容, 类型: string
  cooperationCases: "", // 合作案例内容, 类型: string
  businessCardWebsite: "", // AI名片 - 官方网站URL, 类型: string
  businessCardPhone: "", // AI名片 - 联系电话, 类型: string
  businessCardEmail: "", // AI名片 - 联系邮箱, 类型: string
  businessCardAddress: "", // AI名片 - 公司地址, 类型: string
  otherInfo: "", // 其他信息, 类型: string
  userProfiles: "", // 用户画像列表 (逗号分隔的字符串), 类型: string
  searchScenarios: "", // 搜索场景列表 (逗号分隔的字符串), 类型: string
  createTime: "" // 创建时间, 类型: string
})
const backupData = ref({})

// 取消编辑
function handleCannelEdit() {
  isEdit.value = false
  editData.value = cloneDeep(backupData.value)
}

// 开启编辑模式
function startEdit() {
  backupData.value = cloneDeep(editData.value)
  isEdit.value = true
}

// 保存编辑数据
async function handleKeepEdit() {
  try {
    const params = {
      id: editData.value.id
    }
    switch (chooseView.value) {
      case 1:
        params.companyIntroduction = editData.value.companyIntroduction
        break;
      case 2:
        params.productsServices = editData.value.productsServices
        break;
      case 3:
        params.trustBacking = editData.value.trustBacking
        break;
      case 4:
        params.cooperationCases = editData.value.cooperationCases
        break;
      case 5:
        params.businessCardWebsite = editData.value.businessCardWebsite
        params.businessCardPhone = editData.value.businessCardPhone
        params.businessCardEmail = editData.value.businessCardEmail
        break;
      case 6:
        params.otherInfo = editData.value.otherInfo
        break;
    }

    const res = await updateCompanyKnowledgeBase(params)
    if (res) {
      ElMessage.success("保存成功")
      isEdit.value = false
    } else {
      ElMessage.error("保存失败")
    }

  } catch (e) {
    console.log(e)
  }
}

// 初始化 数据
async function loadKnowledgeData() {
  try {
    const res = await getCompanyKnowledgeBaseByTenant()
    editData.value = res
    backupData.value = cloneDeep(res)
  } catch (e) {
    console.log(e)
  }
}

onMounted(() => {
  loadKnowledgeData()
})

</script>
<style scoped>
.container_knowledge {
  background-color: #F7F8FA;
}

main {
  display: flex;
  gap: 20px;
  margin-top: 20px;
  --headerTagHeight: calc(122px + 40px);
  height: calc(100vh - var(--headerTagHeight));
}

main .left_box {
  width: calc((100% - 20px) * 0.16);
  height: 100%;
  background-color: #FFFFFF;
  padding: 20px;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  gap: 14px;


  .left_box_item {
    width: 100%;
    height: 52px;
    border-radius: 8px;
    background-color: #F5F6FA;
    border: 1px solid transparent;
    padding: 14px 20px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);

    &:hover {
      border-color: #165DFF;
    }
  }

  .item_active {
    border-color: #165DFF;
    background: linear-gradient(180deg, #DDEFFD 0%, #FFFFFF 45%, #FEFFFF 100%);
  }
}

main .right_box {
  width: calc((100% - 20px) * 0.84);
  height: 100%;
  background-color: #FFFFFF;
  border-radius: 6px;
  padding: 20px 30px;

  .right_box_title {
    display: flex;
    justify-content: space-between;
    align-items: center;

    span {
      color: #252632;
      font-family: PingFang SC, sans-serif;
      font-weight: 800;
      font-size: 18px;
      line-height: 150%;
    }

    .editor_box {
      display: flex;
      align-items: center;
      gap: 4px;
      cursor: pointer;

      span {
        font-size: 14px;
        color: #165dff;
        font-weight: 600;

      }
    }
  }

  .right_box_content {
    margin: 20px 0;

    :deep(.el-textarea__inner) {
      box-shadow: none;
      border-radius: 8px;
      resize: none;
      color: #666;
      line-height: 1.6;
      white-space: pre-wrap;
      padding: 16px;
      box-sizing: border-box;
      background: #ffffff;
      border: 1px solid #e4e7ed;
    }

    .content_title {
      margin-top: 20px;
      margin-bottom: 10px;
    }

    .content_show {
      color: #666;
      line-height: 1.6;
      white-space: pre-wrap;
      padding: 16px;
      box-sizing: border-box;
      background: #F7F8FA;
      border-radius: 8px;
      border: 1px solid #e4e7ed;
      max-height: calc(100vh - 260px);
      overflow-y: auto;
      scrollbar-color: rgba(22, 93, 255, 0.1) transparent;
    }

  }

  .right_box_footer {
    width: 100%;
    display: flex;
    justify-content: flex-end;
    gap: 14px;

    & > div {
      width: 60px;
      height: 32px;
      padding: 8px 15px;
      border-radius: 4px;
      font-size: 14px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
    }

    & div:nth-child(1) {
      color: #606266;
      background-color: #FFFFFF;
      border: 1px solid #dcdfe6;
    }

    & div:nth-child(2) {
      color: #FFFFFF;
      background-color: #165dff;
    }
  }
}
</style>
