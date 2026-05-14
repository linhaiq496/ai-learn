import request from '@/config/axios'

//POST 提示词优化-一次返回
// http://localhost:48080/admin-api/ai/image/prompt
export const getPrompt = async (data: any) => {
    return await request.post({url: '/ai/image/prompt', data})
}


// 创建电商生图任务
// 开发中
// POST
// http://localhost:48080/admin-api/ai/image/task/create

export const createImageTask = async (data: any) => {
    return await request.post({url: '/ai/image/task/create', data})
}

// 电商生图板块
// 下载电商生图结果
// 开发中
// GET
// http://localhost:48080/admin-api/ai/image/zip
export const downloadImageZip = async (data: any) => {
    return await request.download({url: '/ai/image/zip', params: data})
}


// 电商生图结果分页
// 开发中
// POST
// http://localhost:48080/admin-api/ai/image/result/page
export const getImageResultPage = async (data: any) => {
    return await request.post({url: '/ai/image/result/page', data})
}


// 电商生图结果删除
// 开发中
// DELETE
// http://localhost:48080/admin-api/ai/image/result/delete-my/{id}
export const deleteImageResult = async (id: number) => {
    return await request.delete({url: '/ai/image/result/delete-my/' + id})
}

// 电商生图板块
// 电商热点分页
// 开发中
// POST
// http://localhost:48080/admin-api/ai/hot/page
export const getHotPage = async (data: any) => {
    return await request.post({url: '/ai/hot/page', data})
}


// 图片上传
// 开发中
// POST
// http://localhost:48080/admin-api/ai/image/upload
export const uploadImage = async (files: any) => {
    return await request.post({
        url: '/ai/image/upload',
        data: {files},
        headers: {"Content-Type": "multipart/form-data"},
    })
}
