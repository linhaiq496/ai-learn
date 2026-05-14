// 素材库api
import request from '@/config/axios'


// 商品类别-查询列表
// GET
// http://localhost:48080/admin-api/ai/material/category/list
// Query 参数 name
export const getMaterialCategoryList = async (name: string) => {
  return await request.get({url: `/ai/material/category/list`, params: {name}})
}

// 商品类别-新增
// POST
// http://localhost:48080/admin-api/ai/material/category/create
export const createMaterialCategory = async (data: any) => {
  return await request.post({url: `/ai/material/category/create`, data})
}

// 商品类别-删除
// DELETE
// http://localhost:48080/admin-api/ai/material/category/delete/{id}
export const deleteMaterialCategory = async (id: number) => {
  return await request.delete({url: `/ai/material/category/delete/${id}`})
}

// 商品类别-根据id修改
// PUT
// http://localhost:48080/admin-api/ai/material/category/update
export const updateMaterialCategory = async (data: any) => {
  return await request.put({url: `/ai/material/category/update`, data})
}


// 素材-新增
// POST
// http://localhost:48080/admin-api/ai/material/asset/create
export const createMaterialAsset = async (data: any) => {
  return await request.post({url: `/ai/material/asset/create`, data})
}

// 素材-分页
// POST
// http://localhost:48080/admin-api/ai/material/asset/page
export const getMaterialAssetPage = async (data: any) => {
  return await request.post({url: `/ai/material/asset/page`, data})
}

// 素材-获取详情
// GET
// http://localhost:48080/admin-api/ai/material/asset/get/{id}
export const getMaterialAsset = async (id: number) => {
  return await request.get({url: `/ai/material/asset/get/${id}`})
}

// 素材-删除
// DELETE
// http://localhost:48080/admin-api/ai/material/asset/delete/{id}
export const deleteMaterialAsset = async (id: number) => {
  return await request.delete({url: `/ai/material/asset/delete/${id}`})
}

// 素材-根据id修改
// PUT
// http://localhost:48080/admin-api/ai/material/asset/update
export const updateMaterialAsset = async (data: any) => {
  return await request.put({url: `/ai/material/asset/update`, data})
}
