import request from '@/config/axios'

// ======================== 文件夹相关接口 ========================

// 文件夹树节点
export interface MaterialFolderTree {
  id: number
  parentId: number
  name: string
  level: number
  sort: number
  createTime: string
  children: MaterialFolderTree[]
  // 扩展字段（用于前端展示）
  imageCount?: number
  coverImages?: MaterialImage[]
}

// 获取文件夹树
export const getFolderTree = () => {
  return request.get<MaterialFolderTree[]>({ url: '/ai/material-folder/tree' })
}

// 创建文件夹参数
export interface CreateFolderParams {
  parentId: number
  name: string
}

// 创建文件夹
export const createFolder = (data: CreateFolderParams) => {
  return request.post<number>({ url: '/ai/material-folder/create', data })
}

// 重命名文件夹参数
export interface UpdateFolderNameParams {
  id: number
  name: string
}

// 重命名文件夹
export const updateFolderName = (data: UpdateFolderNameParams) => {
  return request.put<boolean>({ url: '/ai/material-folder/update-name', data })
}

// 删除文件夹
export const deleteFolder = (id: number) => {
  return request.delete<boolean>({ url: '/ai/material-folder/delete', params: { id } })
}

// ======================== 图片相关接口 ========================

// 图片分页查询参数
export interface ImagePageParams {
  pageNo: number
  pageSize: number
  folderId: number
  name?: string
  type?: string
}

// 图片项
export interface MaterialImage {
  id: number
  folderId: number
  fileId: number
  name: string
  url: string
  path: string
  type: string
  size: number
  createTime: string
}

// 图片分页响应
export interface ImagePageResp {
  list: MaterialImage[]
  total: number
}

// 图片分页查询
export const getImagePage = (params: ImagePageParams) => {
  return request.get<ImagePageResp>({ url: '/ai/material-image/page', params })
}

// 上传图片
export const uploadImage = (data: FormData, onUploadProgress?: (progress: number) => void) => {
  return request.post<MaterialImage>({
    url: '/ai/material-image/upload',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress
  })
}

// 移动图片参数
export interface MoveImageParams {
  id: number
  targetFolderId: number
}

// 移动图片
export const moveImage = (data: MoveImageParams) => {
  return request.put<boolean>({ url: '/ai/material-image/move', data })
}

// 删除图片
export const deleteImage = (id: number) => {
  return request.delete<boolean>({ url: '/ai/material-image/delete', params: { id } })
}
