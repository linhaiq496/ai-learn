import request from '@/config/axios'

export enum LongArticleStatus {
  PENDING = 0,
  EXECUTING = 1,
  SUCCESS = 2,
  FAILED = 3
}

export interface LongArticleGenerateReq {
  query: string
  platform: string
  knowledgeBase: string
  articleType: string
  wordCount: number
  imageNumber: number
  imageRatio?: string
  imageType?: string
  isImageGenerated?: boolean
  articleCount: number
  imageUrls?: string[]
}

export interface LongArticleResultVO {
  id: number
  taskId: number
  articleIndex: number
  status: LongArticleStatus
  title: string
  body: string
  tags?: string
  finalArticle?: string
  imgUrls?: string[]
  errorMessage?: string | null
  rawResponse?: string
  createTime?: string
}

export interface LongArticleTaskVO {
  id: number
  query: string
  platform: string
  knowledgeBase: string
  articleType: string
  wordCount: number
  imageNumber: number
  articleCount: number
  imageRatio?: string
  imageType?: string
  isImageGenerated?: boolean
  imageUrls?: string[] | null
  triggerMode?: string
  workflowAppId?: string
  workflowRequest?: string
  workflowTaskId?: string
  errorMessage?: string
  successCount?: number
  failCount?: number
  startTime?: string
  finishTime?: string
  createTime?: string
  status: LongArticleStatus
  results?: LongArticleResultVO[]
}

export interface LongArticleGenerateResp {
  taskId: number
  resultIds?: number[]
  results?: LongArticleResultVO[]
  successCount: number
  failCount: number
  status: LongArticleStatus
}

export interface LongArticleTaskPageReq {
  pageNo: number
  pageSize: number
  platform?: string
  status?: LongArticleStatus
  articleType?: string
}

export interface PageResult<T> {
  list: T[]
  total: number
}

export interface LongArticleResultUpdateReq {
  id: number
  title?: string
  body?: string
  tags?: string
  finalArticle?: string
  imgUrls?: string[]
}

export const generateSyncApi = (data: LongArticleGenerateReq) => {
  return request.post<LongArticleGenerateResp>({ url: '/ai/long-article/generate-sync', data })
}

export const generateAsyncApi = (data: LongArticleGenerateReq) => {
  return request.post<LongArticleGenerateResp>({ url: '/ai/long-article/generate-async', data })
}

export const getTaskPage = (params: LongArticleTaskPageReq) => {
  return request.get<PageResult<LongArticleTaskVO>>({ url: '/ai/long-article/task/page', params })
}

export const getTaskDetailPage = (params: LongArticleTaskPageReq) => {
  return request.get<PageResult<LongArticleTaskVO>>({ url: '/ai/long-article/task/list', params })
}

export const getTaskDetail = (id: number) => {
  return request.get<LongArticleTaskVO>({ url: '/ai/long-article/task/get', params: { id } })
}

export const getResultDetail = (id: number) => {
  return request.get<LongArticleResultVO>({ url: '/ai/long-article/result/get', params: { id } })
}

export const updateResult = (data: LongArticleResultUpdateReq) => {
  return request.put<boolean>({ url: '/ai/long-article/result-detail/update-my', data })
}

export const deleteResult = (id: number) => {
  return request.delete<boolean>({ url: '/ai/long-article/result/delete', params: { id } })
}
