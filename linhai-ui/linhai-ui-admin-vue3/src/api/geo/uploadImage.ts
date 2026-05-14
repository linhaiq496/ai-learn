import request from '@/config/axios'


// 上传文件
// POST
// /app-api/infra/file/upload
export const uploadFile = async (data: any) => {
    return await request.post({
            url: '/infra/file/upload',
            data,
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }
    )
}
