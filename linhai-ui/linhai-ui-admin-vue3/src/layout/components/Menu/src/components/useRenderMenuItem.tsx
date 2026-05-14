import { ElMenuItem, ElMenuItemGroup, ElSubMenu } from 'element-plus'
import { hasOneShowingChild } from '../helper'
import { isUrl } from '@/utils/is'
import { useRenderMenuTitle } from './useRenderMenuTitle'
import { pathResolve } from '@/utils/routerHelper'
import type { LayoutType } from '@/types/layout'
import { useI18n } from '@/hooks/web/useI18n'

const { renderMenuTitle } = useRenderMenuTitle()

export const useRenderMenuItem = (layout: LayoutType) =>
  // allRouters: AppRouteRecordRaw[] = [],
  {
    const { t } = useI18n()

    const renderMenuItem = (routers: AppRouteRecordRaw[] = [], parentPath = '/') => {
      return (routers ?? [])
        .filter((v) => !v.meta?.hidden)
        .flatMap((v) => {
          const meta = v.meta ?? {}
          const fullPath = isUrl(v.path) ? v.path : pathResolve(parentPath, v.path) // getAllParentPath<AppRouteRecordRaw>(allRouters, v.path).join('/')
          const visibleChildren = (v.children ?? []).filter((c) => !c.meta?.hidden)
          const hasVisibleChildren = visibleChildren.length > 0

          // classic：把“有 children 的菜单”渲染成分组标题（类似截图的“内容/关键词/设置”），并始终展开显示子项
          if (layout === 'classic' && hasVisibleChildren) {
            // 顶层“包裹路由”（如 /xxx 下再挂一个 path='' 的子路由）通常没有 title/icon
            // 这类节点不显示分组标题，直接展开渲染子项
            if (!meta.title) {
              return renderMenuItem(visibleChildren, fullPath)
            }

            return [
              <ElMenuItemGroup>
                {{
                  title: () => (
                    <span class="overflow-hidden overflow-ellipsis whitespace-nowrap">
                      {t((meta.title ?? '') as string)}
                    </span>
                  ),
                  default: () => renderMenuItem(visibleChildren, fullPath)
                }}
              </ElMenuItemGroup>
            ]
          }

          // 没有子菜单时，无论 alwaysShow 为何都必须渲染为 ElMenuItem（避免出现箭头但无子项）
          if (!hasVisibleChildren) {
            return [
              <ElMenuItem index={fullPath}>
                {{
                  default: () => renderMenuTitle(meta)
                }}
              </ElMenuItem>
            ]
          }

          // 其它布局：保持原有逻辑（单子节点折叠 / SubMenu）
          const { oneShowingChild, onlyOneChild } = hasOneShowingChild(v.children, v)
          if (
            oneShowingChild &&
            (!onlyOneChild?.children || onlyOneChild?.noShowingChildren) &&
            !meta?.alwaysShow
          ) {
            return [
              <ElMenuItem
                index={onlyOneChild ? pathResolve(fullPath, onlyOneChild.path) : fullPath}
              >
                {{
                  default: () => renderMenuTitle(onlyOneChild ? onlyOneChild?.meta : meta)
                }}
              </ElMenuItem>
            ]
          } else {
            return [
              <ElSubMenu index={fullPath}>
                {{
                  title: () => renderMenuTitle(meta),
                  default: () => renderMenuItem(v.children ?? [], fullPath)
                }}
              </ElSubMenu>
            ]
          }
        })
    }

    return {
      renderMenuItem
    }
  }
