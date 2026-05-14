<template>
  <div class="image-carousel-wrapper">
    <el-carousel
        indicator-position="none"
        :autoplay="false"
        height="364px"
    >
      <el-carousel-item
          v-for="(group, index) in imageGroups"
          :key="index"
      >
        <div class="image-row">
          <div class="image_box"
               v-for="(item,groupIndex) in group"
               :key="item.imageId"
          >
            <el-image
                class="carousel-image"
                :src="item.imageUrl"
                fit="cover"
                :preview-src-list="previewList"
                :initial-index="index * 5 + groupIndex"
                preview-teleported
            >
              <template #placeholder>
                <div class="image-placeholder">
                  <img src="../../../../assets/auto_hosting_img/loading.png" class="w-30px h-30px"/>
                  <span>图片加载中...</span>
                </div>
              </template>
              <template #error>
                <div v-if="item.status === 1 ||item.status === 0 " class="error_card">
                  <img src="../../../../assets/auto_hosting_img/wating.gif" class="w-107px h-90px"/>
                  <span>图片生成中...</span>
                </div>
                <div v-else-if="item.status === 2 || !item.imageUrl" class="error_card">
                  <img src="../../../../assets/auto_hosting_img/error.png" class="h-30px"/>
                  <span>生成失败</span>
                </div>
                <div v-else class="error_card">
                  <img src="../../../../assets/auto_hosting_img/error.png" class="h-30px"/>
                  <span>加载失败</span>
                </div>
              </template>
            </el-image>
            <div class="download_img" @click="downloadImage(item.imageUrl)">
              <img
                  src="../../../../assets/auto_hosting_img/download1.png"
                  class="w-24px h-24px"
                  v-show="item.status === 3 && item.imageUrl"
              />
            </div>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>
<script setup>
import {computed} from 'vue'
import {Loading} from '@element-plus/icons-vue'  // 添加这行


const props = defineProps({
  imageList: {
    type: Array,
    default: () => [],
  },
  column: {
    type: Number,
    default: 5, // ⭐ 每屏显示 5 张（和你截图一致）
  },
})

/* 预览列表 */
const previewList = computed(() =>
    props.imageList.map(i => i.imageUrl)
)

/* 分组：一页多列 */
const imageGroups = computed(() => {
  const result = []
  for (let i = 0; i < props.imageList.length; i += props.column) {
    result.push(props.imageList.slice(i, i + props.column))
  }
  return result
})


async function downloadImage(imgUrl) {
  console.log('Downloading image:', imgUrl)
  try {
    const response = await fetch(imgUrl);
    if (!response.ok) throw new Error('Network response was not ok');

    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);

    const a = document.createElement('a');
    a.href = url;
    a.download = imgUrl.split('/').pop() || 'image.png';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);

    window.URL.revokeObjectURL(url); // 释放内存
  } catch (e) {
    console.error('Download failed:', e);
  }
}
</script>


<style scoped>
@keyframes rotate360 {
  0% {
    rotate: 0deg;
  }
  100% {
    rotate: 360deg;
  }
}

.image-carousel-wrapper {
  width: 100%;
  overflow: hidden;
}

/* 一页里的图片行 */
.image-row {
  display: flex;
  justify-content: flex-start;
  gap: 20px;
  height: 364px;
  background-color: #F5F6FA;
  padding: 15px 20px;
  align-items: center;
  border-radius: 12px;

  .image_box {
    width: 193px;
    height: 100%;
    border-radius: 12px;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #eceef4;
    position: relative;


    .download_img {
      position: absolute;
      right: 10px;
      top: 10px;
      cursor: pointer;
      opacity: 0;
      display: none;
      transition: opacity 0.3s ease-in-out;
    }

    &:hover .download_img {
      display: block;
      opacity: 1;
    }
  }
}

/* 每张图片 */
.carousel-image {
  flex: 1;
}

.image-placeholder {
  display: flex;
  align-items: center;
  flex-direction: column;
  justify-content: flex-end;
  gap: 17px;
  height: 150px;
  padding-bottom: 30px;


  span {
    font-family: PingFang SC, sans-serif;
    font-weight: 400;
    font-size: 14px;
    line-height: 100%;
    color: #7C7D84;
  }

  img {
    animation: rotate360 1s linear infinite;
  }
}

.error_card {
  display: flex;
  align-items: center;
  flex-direction: column;
  justify-content: flex-end;
  gap: 17px;
  height: 150px;
  padding-bottom: 30px;

  span {
    font-family: PingFang SC, sans-serif;
    font-weight: 400;
    font-size: 14px;
    line-height: 100%;
    color: #7C7D84;
  }
}

:deep(.el-image__inner) {
  border-radius: 12px;
  max-height: 329px;
}

:deep(.el-carousel__arrow) {
  height: 48px;
  width: 48px;
  background-color: #fff;
  border: 1px solid #e7e7e7;
  color: #252632;
  font-weight: 800;
  font-size: 16px;
}
</style>
