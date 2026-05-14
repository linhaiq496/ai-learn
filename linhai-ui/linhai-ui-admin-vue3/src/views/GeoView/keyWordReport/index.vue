<template>
  <div class="container_article">
    <headerTag :headerInfo="headerInfo"/>
    <dataShowForm @look-data-detail="lookDataDetail"/>
    <dataShowDialog ref="dataShowDialogRef"/>
  </div>
</template>
<script setup>
import headerTag from "@/components/GeoView/headerTag.vue";
import dataShowForm from "./dataShowForm.vue"
import dataShowDialog from "./dataShowDialog.vue"
import {reactive, ref} from "vue";
import headerImage from "@/assets/geoImage/importantKeys.png";

const dataShowDialogRef = ref(null)
const headerInfo = reactive({
  title: "关键词报表",
  desc: "基于您在本平台添加的关键词生成，汇总了公开监测数据与分析结果。",
  img: headerImage
})

function lookDataDetail(data) {
  dataShowDialogRef.value.openDialog(data)
}


</script>
<style scoped lang="scss"></style>
