import request from '@/config/axios'


// 获得长尾关键词
// GET
// /ai/product-keyword/page
export const getProductKeywordPage = async (params: PageParam) => {
    return await request.get({url: '/ai/product-keyword/page', params})
}

// 获得产品词
// GET
// /admin-api/ai/product-term/page
export const getProductTermPage = async (params: PageParam) => {
    return await request.get({url: '/ai/product-term/page', params})
}

// 删除产品词
// DELETE
// /admin-api/ai/product-term/delete
export const deleteProductTerm = async (id: number) => {
    return await request.delete({url: '/ai/product-term/delete', params: {id}})
}


// 创建文章生成计划
// POST
// /admin-api/ai/article-plan/create
export const createArticlePlan = async (data: any) => {
    return await request.post({url: '/ai/article-plan/create', data})
}

// 获得文章生成计划分页
// GET
// /admin-api/ai/article-plan/page
export const getArticlePlanPage = async (params: PageParam) => {
    return await request.get({url: '/ai/article-plan/page', params})
}


// 删除文章生成计划
// DELETE
// /admin-api/ai/article-plan/delete
export const deleteArticlePlan = async (id: number) => {
    return await request.delete({url: '/ai/article-plan/delete', params: {id}})
}


// 获得文章分页
// GET
// /admin-api/ai/article/page
export const getArticlePage = async (params: PageParam) => {
    return await request.get({url: '/ai/article/page', params})
}

// 获得文章详情
// GET
// /admin-api/ai/article/get
export const getArticle = async (id: number) => {
    return await request.get({url: '/ai/article/get', params: {id}})
}

// 删除文章
// DELETE
// /admin-api/ai/article/delete
export const deleteArticle = async (id: number) => {
    return await request.delete({url: '/ai/article/delete', params: {id}})
}

// 修改文章
// PUT
// /admin-api/ai/article/update
export const updateArticle = async (data: any) => {
    return await request.put({url: '/ai/article/update', data})
}

// 修改文章
// PUT
// /ai/article/update
export const updateArticleContent = async (data: any) => {
    return await request.put({url: '/ai/article/update', data})
}


// 智能挖掘
// GET
// /admin-api/ai/mining/library_recommend
export const getMiningLibraryRecommend = async (keyword: string, industry: string) => {
    return await request.get({url: '/ai/mining/library_recommend', params: {keyword, industry}})
}

// 创建产品
// POST
// /admin-api/ai/product-term/create
export const createProductTerm = async (data: any) => {
    return await request.post({url: '/ai/product-term/create', data})
}

// 更新产品词
// PUT
// /admin-api/ai/product-term/update
export const updateProductTerm = async (data: any) => {
    return await request.put({url: '/ai/product-term/update', data})
}

// 批量删除长尾关键词
// DELETE
// /admin-api/ai/product-keyword/delete-list
export const deleteProductKeywordList = async (ids: number[]) => {
    const query = ids.map(id => `ids=${id}`).join('&')
    return request.delete({
        url: `/ai/product-keyword/delete-list?${query}`
    })
}


// 删除长尾关键词
// DELETE
// /admin-api/ai/product-keyword/delete
export const deleteProductKeyword = async (id: number) => {
    return await request.delete({url: '/ai/product-keyword/delete', params: {id}})
}


// 创建长尾关键词
// POST
// /admin-api/ai/product-keyword/create
export const createProductKeyword = async (data: any) => {
    return await request.post({url: '/ai/product-keyword/create', data})
}
// 更新长尾关键词
// PUT
// /admin-api/ai/product-keyword/update
export const updateProductKeyword = async (data: any) => {
    return await request.put({url: '/ai/product-keyword/update', data})
}


// 批量更新长尾关键词（del-insert）
// PUT
// /admin-api/ai/product-keyword/update-batch
export const updateProductKeywordBatch = async (data: any) => {
    return await request.put({url: '/ai/product-keyword/update-batch', data})
}

// 导出长尾关键词
// GET
// /admin-api/ai/product-keyword/export-excel
//导出长尾关键词
export const downloadProductKeywordList = async (params) => {
    return request.download({url: '/ai/product-keyword/export-excel', params})
}


