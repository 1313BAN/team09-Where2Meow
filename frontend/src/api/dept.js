import { localAxios } from '@/utils/http-commons'

const deptAPI = localAxios()

const getList = (success, fail) => 
  deptAPI.get(`/api/depts`).then(success).catch(fail)


const register = (dept, success, fail) => 
  deptAPI.post(`/api/depts`, dept).then(success).catch(fail)

const asyncRegister = (dept) =>  deptAPI.post(`/api/depts`, dept).then(()=>console.log('dept asyncRegister successfully'))

const modify = (dept, success, fail) => 
  deptAPI.put(`/api/depts/${dept.deptno}`, dept).then(success).catch(fail)


const get = (deptno, success, fail) => 
  deptAPI.get(`/api/depts/${deptno}`).then(success).catch(fail)


const remove = (deptno, success, fail) => 
  deptAPI.delete(`/api/depts/${deptno}`).then(success).catch(fail)


export default { getList, register, asyncRegister, modify, get, remove }
