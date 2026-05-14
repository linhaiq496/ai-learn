export const ASSET_ICON_PREFIX = 'asset-icon:'

const assetIconModules = import.meta.glob(
  '../../../assets/icon/**/*.{svg,png,jpg,jpeg,webp,gif,avif,ico}',
  {
    eager: true,
    import: 'default'
  }
) as Record<string, string>

const normalizeAssetIconName = (path: string) => {
  const match = path.match(/\/assets\/icon\/(.+)$/)
  return match?.[1] ?? ''
}

export const assetIconMap = Object.entries(assetIconModules).reduce(
  (result, [path, url]) => {
    const iconName = normalizeAssetIconName(path)
    if (iconName) {
      result[iconName] = url
    }
    return result
  },
  {} as Record<string, string>
)

export const assetIconNames = Object.keys(assetIconMap).sort((a, b) => a.localeCompare(b))

export const isAssetIcon = (icon?: string) => !!icon && icon.startsWith(ASSET_ICON_PREFIX)

export const parseAssetIconName = (icon?: string) => {
  if (!icon) return ''
  return icon.replace(ASSET_ICON_PREFIX, '')
}

export const resolveAssetIcon = (icon?: string) => {
  return assetIconMap[parseAssetIconName(icon)]
}
