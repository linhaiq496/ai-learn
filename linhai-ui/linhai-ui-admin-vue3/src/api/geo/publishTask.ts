// 发布任务
import request from '@/config/axios'
/**
 * 一键发布
 *  POST
 *  创建发布任务记录
 * POST
 * /admin-api/ai/publish-task-logs/create
 */
export const createPublishTaskLogs = async (data: any) => {
    return await request.post({url: '/ai/publish-task-logs/create', data})
}


// 获得发布任务记录分页
// GET
// /admin-api/ai/publish-task-logs/page
export const getPublishTaskLogsPage = async (params: PageParam) => {
    return await request.get({url: '/ai/publish-task-logs/page', params})
}


// 重新发布
// POST
// /admin-api/ai/publish-task-logs/re-publish
export const rePublish = async (data: any) => {
    return await request.post({url: '/ai/publish-task-logs/re-publish', data})
}


