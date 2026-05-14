<template>
  <!--  账号选择窗口  -->
  <el-dialog
    v-model="dialogTableVisible"
    class="custom_dialog"
    :show-close="false"
    :close-on-press-escape="false"
    width="1300px" top="5vh"
    :destroy-on-close="true"
  >
    <header>
      <div class="flex">
        {{ `选择账号（共计${checkAccountTotal}个）` }}
        <!--      <el-button  class="color-[#165DFF]">全选</el-button>-->
        <div @click="selectAllAccount" class="choose_all">全选</div>
      </div>
      <img src="@/assets/geoImage/close.png" class="w-16px h-16px"
           @click="dialogTableVisible = false"/>
    </header>
    <main>
      <div class="platform_box">
        <div
          v-for="(item, index) in platformList"
          :key="index" class="platform_item"
          :class="{active_platform: item.isChecked, isDisabled: item.isDisabled}"
          @click="checkPlatform(item)"
        >
          <div :class="item.icon" class="w-28px h-28px"></div>
          <span>{{ item.platformName }}</span>
        </div>
      </div>
      <div class="account_box" v-if="activeAccountList.length">
        <div
          class="account_item"
          v-for="item in activeAccountList"
          :class="{active_account: item.isChecked}"
          :key="item.id" @click="checkAccount(item)">
          <div class="account_icon">
            <!--            <img :src="item.icon" alt="" class="w-48px h-48px rounded-full"/>-->
            <img :src="item.icon" alt="" class="w-48px h-48px rounded-full"
                 v-if="item.icon.includes('http')"/>
            <div v-else :class="item.platformIcon" class="w-48px h-48px rounded-full"></div>
          </div>
          <div class="account_info">
            <div class="account_name">
              <div class="account_name_text">{{ item.nickName }}</div>
              <img :src="activePlatform.icon" alt="" class="w-15px h-15px"/>
            </div>
            <div class="account_status">
              <img src="@/assets/geoImage/online.png" alt="" v-show="item.status"
                   class="w-12px h-12px"/>
              <img src="@/assets/geoImage/offline.png" alt="" v-show="!item.status"
                   class="w-12px h-12px"/>
              {{ item.status ? '在线' : "离线" }}
              <!--   <div-->
              <!--       class="account_rebind"-->
              <!--       v-show="!item.status"-->
              <!--       @click.stop="accountRebind(item)">重绑-->
              <!--   </div>-->
            </div>
          </div>
        </div>
      </div>
      <div class="empty_box" v-else>
        <div>
          <img src="@/assets/geoImage/empty.png"/>
          <span>暂无账号，开始添加您的第一个社交媒体账号，开启内容创作之旅</span>
          <div class="add_account cursor-pointer" @click="addAccount">添加账号</div>
        </div>
      </div>

    </main>
    <footer>
      <div class="operate_button cancel" @click="dialogTableVisible = false">取消</div>
      <div class="operate_button confirm" @click="selectAccount">{{
          isPublished ? "确定发布" : '确定'
        }}
      </div>
    </footer>
  </el-dialog>
</template>
<script setup>
import {ref, reactive} from 'vue'
import {} from "@/api/geo/productTerm";
// 导入所有平台图标
import baijiahao from "@/assets/geoImage/platformIcon/baijia.png"
import souhu from "@/assets/geoImage/platformIcon/souhu.png"
import taojianghu from "@/assets/geoImage/platformIcon/taojianghu.png"
import wangyi from "@/assets/geoImage/platformIcon/wangyi.png"
import xueqiu from "@/assets/geoImage/platformIcon/xueqiu.png"
import kaidi from "@/assets/geoImage/platformIcon/kaidi.png"
import toutiao from "@/assets/geoImage/platformIcon/jinritoutiao.png"
import shenmezhidemai from "@/assets/geoImage/platformIcon/shenmezhidemai.png"
import {useDictStore} from "@/store/modules/dict";
import {getPublishAccountsPage} from "@/api/geo/platformAccount";
import OpenDeskTopPlusDialog from "@/components/GeoView/OpenDeskTopPlusDialog.vue";


const _emit = defineEmits(['getSelectAccount', 'addAccountByClient'])
const useDictStoreRef = useDictStore()

const dialogTableVisible = ref(false)
const isPublished = ref(false)
// 平台及其账号信息List
const platformList = ref([])

const activeAccountList = computed(() => {
  const data = platformList.value.find(item => item.isChecked)
  if (data.platformName == '全部') {
    return platformList.value.flatMap(item => item.accountList)
  }
  return platformList.value.find(item => item.isChecked)?.accountList || []
})
const activePlatform = computed(() => {
  return platformList.value.find(item => item.isChecked) || {}
})
const checkAccountTotal = computed(() => {
  const accountList = platformList.value.flatMap(item => {
    return item.accountList.filter(pre => {
      return pre.isChecked
    })
  })
  return accountList.length || 0
})


watch(() => dialogTableVisible.value, (val) => {
  if (!val) {
  }
})


// 行业列表字典
async function getIndustryList() {
  const res = useDictStoreRef.getDictByType('publish_platform') || [];
  platformList.value = res.map(item => {
    // if (item.label === '全部') {
    //   return null
    // }
    return {
      platformName: item.label,
      icon: item.cssClass,
      platform: item.value,
      accountList: [],
      isChecked: false,
      isDisabled: false
    }
  }).filter(item => item)
  platformList.value.unshift({
    platformName: "全部",
    icon: "icon-all",
    platform: "all",
    accountList: [],
    isChecked: true,
    isDisabled: false
  })

}

//  获取账号列表
async function getPublishAccountsList() {
  try {
    const res = await getPublishAccountsPage({
      pageNo: 1,
      pageSize: 200
    })
    platformList.value.forEach(item => {
      item.accountList = []
    })
    res.list.map(item => {
      platformList.value.forEach(pre => {
        if (item.platform === pre.platform) {
          pre.accountList.push({
            id: item.id,
            nickName: item.account,
            icon: item.icon || "", // 用户头像 todo
            status: item.status === "online" ? 1 : 0,
            isChecked: false,
            bindTime: item.createTime,
            platformIcon: pre.icon,
            platformName: pre.platformName
          })
        }
      })
    })
    // accountInfo.accountTotal = res.total
    // accountInfo.accountOnline = 0
    // platformList.value.map(item => {
    //   item.accountList.forEach(item => {
    //     if (item.status === 1) {
    //       accountInfo.accountOnline++
    //     }
    //   })
    // })
  } catch (e) {
    console.log(e)
  }
}

// 选中所有账号（选中当前账号类型下的所有账号）
function selectAllAccount() {
  const data = platformList.value.find(item => item.isChecked)
  if (data.platformName === '全部') {
    platformList.value.forEach(item => {
      item.accountList.forEach(account => {
        account.isChecked = true
      })
    })
    return
  }

  const list = platformList.value.find(item => item.isChecked)
  list.accountList.forEach(item => {
    item.isChecked = true
  })

}

function openDialog(data = [], isPublish = false) {
  dialogTableVisible.value = true
  isPublished.value = isPublish

  initPlatformList(data)
  checkPlatform(platformList.value[0])
}

// 初始化平台列表
function initPlatformList(data) {
  console.log(data)
  try {
    // const res = await getAccountList()

    platformList.value.forEach(item => {
      item.accountList.forEach(account => {
        account.isChecked = data.includes(account.id) ? 1 : 0
      })
    })
  } catch (e) {
    console.log(e)
  }
}

// 勾选平台
function checkPlatform(item) {
  if (item.isDisabled) {
    return ElMessage.error('该平台已停用')
  }

  platformList.value.forEach(pre => {
    pre.isChecked = false
  })
  item.isChecked = true
}

// 勾选或取消勾选账号
function checkAccount(item) {
  console.log(item)
  const selectId = item.id
  platformList.value.forEach(item => {
    item.accountList.forEach(account => {
      if (account.id === selectId) {
        account.isChecked = !account.isChecked
      }
    })
  })
}

// 添加账号 todo
function addAccount() {
  dialogTableVisible.value = false
  _emit('addAccountByClient')
}

// 账号重新绑定
function accountRebind(accountInfo) {
  console.log(accountInfo)
  //todo 账号重新绑定
}

// 选择账号
async function selectAccount() {
  const accountList = platformList.value.flatMap(item => {
    return item.accountList.filter(pre => {
      return pre.isChecked
    })
  })
  if (!accountList.length) {
    ElMessage.closeAll()
    return ElMessage.error('请勾选账号')
  }
  _emit('getSelectAccount', accountList)
  dialogTableVisible.value = false
}

defineExpose({
  openDialog
})


onMounted(() => {
  getIndustryList()
  getPublishAccountsList()
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

  .choose_all {
    cursor: pointer;
    width: 62px;
    height: 28px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #165DFF;
    border: 1px solid #165DFF;
    border-radius: 3px;
    font-family: PingFang SC, sans-serif;
    font-weight: 400;
    font-size: 16px;
    line-height: 22px;
  }
}

main {
  background-color: #FFFFFF;
  height: 560px;
  display: flex;

  .platform_box {
    width: 200px;
    height: 100%;
    padding: 16px 20px;
    overflow-y: auto;

    &::-webkit-scrollbar {
      width: 0;
      display: none;
    }

    .platform_item {
      min-width: 160px;
      min-height: 48px;
      border-radius: 8px;
      background-color: #F5F6FA;
      display: flex;
      align-items: center;
      justify-content: flex-start;
      gap: 12px;
      cursor: pointer;
      padding-left: 20px;
      margin-bottom: 20px;
      border: 2px solid #F5F6FA;

      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-size: 17px;
      line-height: 100%;
      color: #252632;

      &:nth-last-child(1) {
        margin-bottom: 0;
      }

      img {
        width: 28px;
        height: 28px;
      }
    }

    .active_platform {
      border-color: #165DFF;
    }

    .isDisabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  }

  .account_box {
    width: calc(100% - 200px);
    height: calc(100% - 16px - 20px);
    margin: 16px 0 20px 0;
    overflow-y: auto;
    border-radius: 6px;
    background-color: #F5F6FA;
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    align-content: start;
    gap: 20px;
    padding: 20px;

    &::-webkit-scrollbar {
      width: 0;
      display: none;
    }

    .account_item {
      display: flex;
      align-items: center;
      justify-content: flex-start;
      gap: 12px;
      width: 180px;
      height: 60px;
      background-color: #FFFFFF;
      padding: 15px;
      border-radius: 6px;
      border: 2px solid transparent;
      position: relative;
      cursor: pointer;

      .account_info {
        font-family: PingFang SC, sans-serif;
        line-height: 100%;

        .account_name {
          display: flex;
          align-items: center;
          justify-content: flex-start;
          gap: 6px;
          font-size: 16px;
          font-weight: 400;
          color: #252632;
          margin-bottom: 8px;

          .account_name_text {
            width: 64px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }

        .account_status {
          color: #B9BAC1;
          font-family: PingFang SC, sans-serif;
          font-weight: 400;
          font-size: 13px;
          line-height: 100%;
          display: flex;
          gap: 6px;

          .account_rebind {
            text-decoration: underline;
            text-decoration-style: solid;
            text-decoration-skip-ink: auto;
            color: #505CFF;
          }
        }
      }
    }

    .active_account {
      border-color: #165DFF;

      &::after {
        position: absolute;
        width: 16px;
        height: 16px;
        content: "✔";
        border-radius: 0 6px;
        color: white;
        background-color: #165DFF;
        right: -1px;
        top: -1px;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
  }

  .empty_box {
    width: calc(100% - 200px);
    height: calc(100% - 16px - 20px);
    display: flex;
    justify-content: center;
    align-items: center;

    > div {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 10px;
    }

    img {
      width: 200px;
      height: 200px;
    }

    span {
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-size: 15px;
      line-height: 150%;
      text-align: center;
      color: #646363;
    }

    .add_account {
      width: 201px;
      height: 40px;
      opacity: 1;
      border-radius: 8px;
      gap: 10px;
      background-color: #165DFF;
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-top: 14px;
    }
  }
}

footer {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin: 0 0px 12px 0;
  border-top: 1px solid #E5E6EB;

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
