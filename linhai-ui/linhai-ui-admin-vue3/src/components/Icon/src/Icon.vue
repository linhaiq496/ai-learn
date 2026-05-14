<script lang="ts" setup>
import { CSSProperties } from 'vue'
import { propTypes } from '@/utils/propTypes'
import Iconify from '@purge-icons/generated'
import { useDesign } from '@/hooks/web/useDesign'
import { isAssetIcon, resolveAssetIcon } from './assetIcon'

defineOptions({ name: 'Icon' })

const { getPrefixCls } = useDesign()

const prefixCls = getPrefixCls('icon')

const props = defineProps({
  // icon name
  icon: propTypes.string,
  // icon color
  color: propTypes.string,
  // icon size
  size: propTypes.number.def(16),
  // icon svg class
  svgClass: propTypes.string.def('')
})

const elRef = ref<ElRef>(null)

const isLocal = computed(() => props.icon?.startsWith('svg-icon:'))
const isAsset = computed(() => isAssetIcon(props.icon))

const symbolId = computed(() => {
  return unref(isLocal) ? `#icon-${props.icon.split('svg-icon:')[1]}` : props.icon
})
const assetIconSrc = computed(() => resolveAssetIcon(props.icon))
const assetIconStyle = computed<CSSProperties>(() => ({
  width: `${props.size}px`,
  height: `${props.size}px`,
  display: 'inline-block',
  backgroundColor: props.color || 'currentColor',
  maskImage: assetIconSrc.value ? `url(${assetIconSrc.value})` : undefined,
  maskRepeat: 'no-repeat',
  maskPosition: 'center',
  maskSize: 'contain',
  WebkitMaskImage: assetIconSrc.value ? `url(${assetIconSrc.value})` : undefined,
  WebkitMaskRepeat: 'no-repeat',
  WebkitMaskPosition: 'center',
  WebkitMaskSize: 'contain'
}))

const getIconifyStyle = computed(() => {
  const { color, size } = props
  return {
    fontSize: `${size}px`,
    height: '1em',
    color
  }
})

const getSvgClass = computed(() => {
  const { svgClass } = props
  return `iconify ${svgClass}`
})

const updateIcon = async (icon: string) => {
  if (unref(isLocal) || unref(isAsset)) return

  const el = unref(elRef)
  if (!el) return

  await nextTick()

  if (!icon) return

  const svg = Iconify.renderSVG(icon, {})
  if (svg) {
    el.textContent = ''
    el.appendChild(svg)
  } else {
    const span = document.createElement('span')
    span.className = 'iconify'
    span.dataset.icon = icon
    el.textContent = ''
    el.appendChild(span)
  }
}

watch(
  () => props.icon,
  (icon: string) => {
    updateIcon(icon)
  }
)
</script>

<template>
  <ElIcon :class="prefixCls" :color="color" :size="size">
    <svg v-if="isLocal" :class="getSvgClass">
      <use :xlink:href="symbolId" />
    </svg>
    <span v-else-if="isAsset && assetIconSrc" :style="assetIconStyle"></span>

    <span v-else ref="elRef" :class="$attrs.class" :style="getIconifyStyle">
      <span :class="getSvgClass" :data-icon="symbolId"></span>
    </span>
  </ElIcon>
</template>
