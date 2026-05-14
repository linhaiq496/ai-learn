<script setup>
import {ref, computed} from "vue";
import PlatformSelectDialog from "@/components/GeoView/PlatformSelectDialog.vue"
import OpenDeskTopPlusDialog from "@/components/GeoView/OpenDeskTopPlusDialog.vue"
import {useDictStore} from "@/store/modules/dict";
import {formatToDateTime} from "@/utils/dateUtil";
import {getPublishAccountsPage, deletePublishAccounts} from "@/api/geo/platformAccount";

const PlatformSelectDialogRef = ref(null)
const OpenDeskTopPlusDialogRef = ref(null)

const accountInfo = reactive({
  accountTotal: 19,
  accountOnline: 0
})
const platformList = ref([
  {
    platformName: "全部",
    icon: 'icon-all',
    accountList: [],
    isChecked: true,
    isDisabled: false
  }
])
const useDictStoreRef = useDictStore()

async function getIndustryList() {
  const res = useDictStoreRef.getDictByType('publish_platform') || [];
  platformList.value = res.map(item => {
    return {
      platformName: item.label,
      icon: item.cssClass,
      platform: item.value,
      accountList: [],
      isChecked: false,
      isDisabled: false
    }
  })
  platformList.value.unshift({
    platformName: "全部",
    icon: "icon-all",
    platform: "all",
    accountList: [],
    isChecked: true,
    isDisabled: false
  })
  console.log(platformList.value)
  platformList.value[0] && (platformList.value[0].isChecked = true) // 默认勾选第一个

}

// 获取当前选中的账号列表
const activeAccountList = computed(() => {
  const list = platformList.value.find(item => item.isChecked)
  if (list.platformName === "全部") {
    return platformList.value.flatMap(item => item.accountList)
  }
  return list.accountList
})

// 添加账号
function addAccount() {
  OpenDeskTopPlusDialogRef.value.openDialog()

  // 过滤出需要展示的平台
  // const list = platformList.value.filter(item => !item.isDisabled).filter(item => item.platformName !== '全部')
  // PlatformSelectDialogRef.value.openDialog({showPlatformList: list})
}

function selectPlatform(platform) {
  console.log(platform)
}

// 删除账号
async function handleDeleteAccount(item) {
  // 弹窗确认是否删除账号
  try {
    await ElMessageBox.confirm('确定要删除此账号吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    console.log(item.id)
    const res = await deletePublishAccounts(item.id)
    if (res) {
      await getPublishAccountsList()
      ElMessage.success('删除成功')
    }
  } catch (e) {
    if (e !== 'cancel') {
      console.log(e)
      ElMessage.error('删除失败')
    }
  }
}

// 获取账号列表
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
    accountInfo.accountTotal = res.total
    accountInfo.accountOnline = 0
    platformList.value.map(item => {
      item.accountList.forEach(item => {
        if (item.status === 1) {
          accountInfo.accountOnline++
        }
      })
    })
  } catch (e) {
    console.log(e)
  }
}

// 账户重绑
// function accountRebind() {
//   console.log('accountRebind')
// }

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

onMounted(() => {
  getIndustryList()
  getPublishAccountsList()
})
</script>

<template>
  <div class="container_accountBind">
    <header>
      <div class="account_detail">
        <div class="account_total">
          <span>{{ accountInfo.accountTotal }}</span>
          <span>账号总数</span>
        </div>
        <div class="account_online">
          <span>{{ accountInfo.accountOnline }}</span>
          <span>在线账号</span>
        </div>
      </div>
      <div class="account_operation">
        <div class="account_add" @click="addAccount">
          <img src="@/assets/geoImage/person.png" alt="" class="w-20px h-20px"/>
          <span>添加账号</span>
        </div>
      </div>
    </header>
    <main>
      <div class="platform_list">
        <div
            v-for="(item, index) in platformList"
            :key="index" class="platform_item"
            :class="{active_platform: item.isChecked, isDisabled: item.isDisabled}"
            @click="checkPlatform(item)"
        >
          <!--          <img :src="item.icon" alt="" class="w-20px h-20px"/>-->
          <div :class="item.icon" class="w-28px h-28px"></div>
          <span>{{ item.platformName }}</span>
        </div>
      </div>
      <div class="account_list" v-if="activeAccountList.length">
        <div
            class="account_item"
            v-for="pre in activeAccountList"
            :key="pre.id">
          <div class="account_icon">
            <img :src="pre.icon" alt="" class="w-48px h-48px rounded-full"
                 v-if="pre.icon.includes('http')"/>
            <div v-else :class="pre.platformIcon" class="w-48px h-48px rounded-full"></div>
          </div>
          <div class="account_info">
            <div class="account_name">
              <div class="account_name_text">{{ pre.nickName }}</div>
            </div>
            <div class="account_status">
              <div>
                <div :class="pre.platformIcon" class="w-18px h-18px"></div>
                <span class="color-[#BCBCBC]">{{ pre.platformName }}</span>
              </div>
              <div>
                <img src="@/assets/geoImage/online.png" alt="" v-show="pre.status"
                     class="w-12px h-12px"/>
                <img src="@/assets/geoImage/offline.png" alt="" v-show="!pre.status"
                     class="w-12px h-12px"/>
                {{ pre.status ? '在线' : "离线" }}
              </div>
              <div>
                {{ formatToDateTime(pre.bindTime) }}
              </div>
            </div>
          </div>
          <div class="delete_icon" @click="handleDeleteAccount(pre)">
            <img src="@/assets/geoImage/delete.png" class="w-20px h-20px"/>
          </div>
        </div>
      </div>
      <div class="account_list empty_box" v-else>
        <div>
          <img src="@/assets/geoImage/empty.png" alt=""/>
          <span>暂无账号，开始添加您的第一个社交媒体账号，开启内容创作之旅</span>
          <div class="add_account" @click="addAccount">添加账号</div>
        </div>
      </div>
    </main>
    <PlatformSelectDialog ref="PlatformSelectDialogRef" @select-platform="selectPlatform"/>
    <OpenDeskTopPlusDialog ref="OpenDeskTopPlusDialogRef"/>
  </div>
</template>

<style scoped lang="scss">
$headerHeight: 119px;

.container_accountBind {
  width: 100%;
  height: calc(100vh - 30px - 70px);
}

header {
  height: $headerHeight;
  background-color: #FFFFFF;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 6px;

  .account_detail {
    display: flex;
    align-items: center;

    .account_total, .account_online {
      width: 180px;
      height: 80px;
      border-radius: 8px;
      background-color: #F5F5F5;
      margin-right: 20px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;

      span:first-child {
        font-weight: 400;
        font-size: 24px;
        line-height: 150%;
        color: #505CFF;
      }

      span:last-child {
        font-family: PingFang SC, sans-serif;
        font-weight: 400;
        font-size: 16px;
        line-height: 150%;
        color: #252632;
      }
    }
  }

  .account_operation {
    .account_add {
      width: 200px;
      height: 36px;
      border-radius: 8px;
      gap: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #165DFF;
      color: white;
      cursor: pointer;
      font-family: PingFang SC, sans-serif;
      font-weight: 400;
      font-size: 16px;
      line-height: 150%;
    }
  }
}

main {
  width: 100%;
  height: calc(100vh - #{$headerHeight} - 30px - 20px - 60px);
  background-color: #fff;
  margin-top: 20px;
  border-radius: 6px;
  padding: 0 20px;

  .platform_list {
    height: 64px;
    display: flex;
    border-bottom: 1px solid #E1E1E1;

    .platform_item {
      min-width: 150px;
      gap: 12px;
      padding: 0 20px;
      border-bottom: 4px solid transparent;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.3s;

      img {
        width: 28px;
        height: 28px;
      }
    }

    .active_platform {
      background-color: #E8F3FF;
      border-bottom: 4px solid #165DFF;
    }

    .isDisabled {
      opacity: 0.5;
    }
  }

  .account_list {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    padding: 20px 0;

    .account_item {
      display: flex;
      align-items: center;
      justify-content: flex-start;
      gap: 12px;
      height: 80px;
      background-color: #FFFFFF;
      padding: 15px;
      border-radius: 6px;
      position: relative;
      border: 1px solid #E5E6EB;
      transition: all 0.3s;

      &:hover {
        border: 1px solid #165DFF;
        background: linear-gradient(180deg, #DDEFFD 0%, #FFFFFF 45%, #FEFFFF 100%);
      }

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
            width: 120px;
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
          align-items: center;
          justify-content: flex-start;
          gap: 10px;

          > div {
            display: flex;
            align-items: center;
            gap: 6px;
          }

          .account_rebind {
            text-decoration: underline;
            text-decoration-style: solid;
            text-decoration-skip-ink: auto;
            color: #505CFF;
          }
        }
      }

      .delete_icon {
        display: none;
        align-items: center;
        justify-content: flex-end;
        cursor: pointer;
        position: absolute;
        right: 16px;
      }

      &:hover .delete_icon {
        display: flex;
      }
    }

    //.active_account {
    //  border: 1px solid #165DFF;
    //  background: linear-gradient(180deg, #DDEFFD 0%, #FFFFFF 45%, #FEFFFF 100%);
    //}
  }

  .empty_box {
    display: flex;
    flex-direction: column;
    height: 450px;
    width: 100%;
    justify-content: center;
    align-items: center;
    gap: 10px;

    > div {
      display: flex;
      flex-direction: column;
      justify-content: center;
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
      cursor: pointer;
    }
  }
}
</style>
