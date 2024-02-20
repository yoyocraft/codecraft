# CodeCraft 部署上线

> 使用 宝塔 Linux

## 一、部署规划

### 1.1 部署项目

1）前端：通过 Nginx 进行部署，访问地址为 `http://{host}`

2）后端：通过 Nginx 进行转发，访问地址为 `http://{host}/api`

> 为什么要使用 Nginx 做转发？
>
> 前后端域名一致，保证不会出现跨域问题。

3）`xxl-job-admin`：访问地址：`http://{host}:8080/xxl-job-admin`

### 1.2 部署依赖

1）Nginx：服务器 80 端口

2）MySQL：服务器 3306 端口

3）Redis：服务器 6379 端口