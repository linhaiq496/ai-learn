import request from '@/config/axios'

// ==================== 文章任务相关接口 ====================

// POST 创建文章任务
// 请求参数: AiArticleTaskCreateReqVO
// - noteSubject: 笔记题材 (必填)
// - textDescription: 文本描述 (必填)
// - articleCount: 文章数量 (必填)
// - articleLength: 文章长度 (可选)
// - knowledgeId: 知识库Id (可选)
// - platformFormat: 平台格式 (必填)
// 返回: { id: 任务编号 }
export const createArticleTask = async (data: any) => {
  return await request.post({url: '/ai/article/task/create', data})
}

// POST 提示词优化（非流式）- 一次性返回，响应较慢
// 请求参数: AiImagePromptStreamReqVO
// - prompt: 提示词 (必填)
// - imageUrls: 图片URL列表 (可选，最多6个)
// 返回: MultiModalConversationOutput
export const getPrompt = async (data: any) => {
  return await request.post({url: '/ai/article/prompt', data})
}

// POST 提示词优化（流式）- 流式返回，响应较快
// 请求参数: AiImagePromptStreamReqVO
// - prompt: 提示词 (必填)
// - imageUrls: 图片URL列表 (可选，最多6个)
// 返回: SSE 流式数据
export const getPromptStream = async (data: any) => {
  return await request.post({
    url: '/ai/article/prompt-stream',
    data,
    headers: { 'Accept': 'text/event-stream' }
  })
}

// POST AI 生成结果分页 - 按创建时间倒序
// 请求参数: AiResultPageReqVO
// - taskId: 任务编号 (可选)
// - status: 生成状态 (可选) 0-待生成 1-生成中 2-生成失败 3-生成完成
// - pageNo: 页码
// - pageSize: 每页条数
// 返回: PageResult<AiResultRespVO>
export const getArticleResultPage = async (data: any) => {
  return await request.post({url: '/ai/article/result/page', data})
}

// GET 查询AI生成结果详情
// 参数: id - ai_result 编号
// 返回: AiResultRespVO
export const getArticleResult = async (id: number) => {
  return await request.get({url: '/ai/article/result/get/' + id})
}

// ==================== 其他接口 ====================

// 电商热点分页
// POST /ai/hot/page
export const getHotPage = async (data: any) => {
  return await request.post({url: '/ai/hot/page', data})
}

// ==================== 兼容旧接口（待删除） ====================

// 旧接口 - 创建文章（已废弃，请使用 createArticleTask）
// @deprecated
// export const createArticle = async (data: any) => {
//   return await request.post({url: '/ai/article/task/create', data})
// }

// 旧接口 - 获取文章列表（已废弃，请使用 getArticleResultPage）
// @deprecated
// export const getArticleList = async (data: any) => {
//   return await request.post({url: '/ai/article/result/page', data})
// }

// 旧接口 - 获取文章详情（已废弃，请使用 getArticleResult）
// 查询【我的】AI 生成结果详情
// GET
// /admin-api/ai/article/result/get/{id}
// @deprecated
export const getArticleDetail = async (id: number) => {
  return await request.get({url: '/ai/article/result/get/' + id})
}

// 删除【我的】AI 文章结果明细
// DELETE
// /admin-api/ai/article/result-detail/delete-my/{id}
export const deleteArticle = async (id: number) => {
  return await request.delete({url: '/ai/article/result-detail/delete-my/' + id})
}

// 更新【我的】AI 文章结果明细
// @deprecated
export const saveArticle = async (data: any) => {
  return await request.put({url: '/ai/article/result-detail/update-my', data})
}
