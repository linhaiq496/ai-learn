import request from '@/config/axios'



// 获得平台账号
// GET
// /admin-api/ai/publish-accounts/page
export const getPublishAccountsPage = async (params) => {
    return await request.get({ url: '/ai/publish-accounts/page', params })
}


// 删除平台账号
// DELETE
// /admin-api/ai/publish-accounts/delete
export const deletePublishAccounts = async (id: number) => {
  return await request.delete({ url: '/ai/publish-accounts/delete', params: { id } })
}
