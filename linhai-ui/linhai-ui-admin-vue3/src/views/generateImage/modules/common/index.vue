<template>
  <div class="generate_work_place">
    <common-work-place @createImage="createImage" ref="workPlaceRef"/>
  </div>
  <div class="generate_show_place">
    <common-show-place ref="showPlaceRef"/>
  </div>
</template>

<script setup>
import commonShowPlace from './generateShowPlace.vue'
import commonWorkPlace from './generateWorkPlace.vue'

const workPlaceRef = ref(null)
const showPlaceRef = ref(null)

function createImage(data) {
  if (showPlaceRef.value) showPlaceRef.value.openGetImage(data)
}

</script>
<style scoped lang="scss">
$box_padding: 20px;
$tag_box_height: 60px;

.generate_box {
  $box_width: calc(100vw - $box_padding - var(--left-menu-max-width) - var(--app-content-padding));

  width: $box_width;
  height: calc(100vh - $box_padding * 2 - $tag_box_height);

  display: flex;
  justify-content: flex-start;

  .generate_show_place, .generate_work_place {
    background-color: #fff;
    border-radius: 12px;
    position: relative;
    overflow: auto;
  }

  .generate_work_place {
    width: calc($box_width * 542 / (1139 + 540));
    position: relative;

    &::-webkit-scrollbar {
      display: none;
    }
  }

  .generate_show_place {
    width: calc($box_width * 1139 / (1139 + 540));
    margin-left: 20px;
  }
}
</style>
