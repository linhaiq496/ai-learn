import request from '@/config/axios'


// 获取首页统计数据
// GET
// /admin-api/ai/homepage/getHomePageData
export const getHomePageData = async () => {
  return await request.get({url: '/ai/homepage/getHomePageData'})
}

// 获取首页热词排名
// GET
// /admin-api/ai/homepage/getHomePageHotWords
export const getHomePageHotWords = async (limit) => {
  return await request.get({url: `/ai/homepage/getHomePageHotWords?limit=${limit}`})
}
