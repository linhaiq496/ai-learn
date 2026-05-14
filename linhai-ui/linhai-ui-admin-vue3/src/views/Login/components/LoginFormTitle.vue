<template>
  <div class="flex flex-col">
    <div>
      <img
          :src="logoSrc"
          class="h-64px"
          style="object-fit: contain"
          @error="(e) => ((e.target as HTMLImageElement).src = defaultLogo)"
      />
    </div>
    <h2 class="login_title">
      <!--      {{ TentanTitle || 'GEO平台' }}-->
      GEO平台
    </h2>
  </div>
</template>
<script lang="ts" setup>
import {useAppStore} from '@/store/modules/app'
import {computed} from "vue";

defineOptions({name: 'LoginFormTitle'})

const appStore = useAppStore()
const defaultLogo = new URL('@/assets/geoImage/loginview/logo.png', import.meta.url).href
const logoSrc = computed(() => appStore.getTenantLogo || defaultLogo)
// const TentanTitle = computed(() => appStore.getTitle || 'GEO平台')
</script>
<style scoped>
.login_title {
  color: #1D2129;
  font-family: PingFang SC, sans-serif;
  font-weight: 500;
  font-size: 36px;
  line-height: 44px;
  margin: 20px 0 40px 0;
}
</style>
