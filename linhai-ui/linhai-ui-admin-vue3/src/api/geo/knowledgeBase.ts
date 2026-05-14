import request from '@/config/axios'

// 获得当前租户的公司知识库
// GET
// /admin-api/ai/company-knowledge-base/get-by-tenant
export const getCompanyKnowledgeBaseByTenant = async () => {
    return await request.get({url: '/ai/company-knowledge-base/get-by-tenant'})
}


// 更新公司知识库
// PUT
// /admin-api/ai/company-knowledge-base/update
export const updateCompanyKnowledgeBase = async (data: any) => {
    return await request.put({url: '/ai/company-knowledge-base/update', data})
}
