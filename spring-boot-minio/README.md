# Spring Boot MinIO 文件上传示例

本项目是一个基于Spring Boot和MinIO的文件上传示例，实现了文件的基础上传和高级上传功能。

## 功能特性

- 基础文件上传和下载
- 文件秒传
- 分片上传
- 断点续传
- 文件URL预签名

## MinIO Docker安装指南

### 1. 拉取MinIO镜像

```bash
docker pull minio/minio
```

### 2. 创建数据持久化目录

```bash
mkdir -p ~/minio/data
```

### 3. 启动MinIO服务器

```bash
docker run -d \
  --name minio \
  -p 9000:9000 \
  -p 9001:9001 \
  -e "MINIO_ROOT_USER=minioadmin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin" \
  -v ~/minio/data:/data \
  minio/minio server /data --console-address ":9001"
```

参数说明：
- `-d`: 后台运行容器
- `-p 9000:9000`: API端口映射
- `-p 9001:9001`: 控制台端口映射
- `-e`: 设置环境变量（用户名和密码）
- `-v`: 数据卷挂载

### 4. 访问MinIO

- **API端点**: http://localhost:9000
- **控制台地址**: http://localhost:9001
- **默认用户名**: minioadmin
- **默认密码**: minioadmin

### 常用Docker命令

```bash
# 查看容器状态
docker ps

# 停止MinIO服务
docker stop minio

# 启动MinIO服务
docker start minio

# 查看容器日志
docker logs minio
```

## 项目配置

在`application.properties`或`application.yml`中配置MinIO连接信息：

```yaml
minio:
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  default-bucket-name: default-bucket
```

## API接口

### 基础上传
- POST `/api/file/upload` - 上传文件
- GET `/api/file/download/{fileName}` - 下载文件
- DELETE `/api/file/delete/{fileName}` - 删除文件

### 高级上传
- POST `/api/upload/tryFast` - 尝试秒传
- POST `/api/upload/chunk` - 上传分片
- GET `/api/upload/chunks/{fileId}` - 获取已上传分片列表
- POST `/api/upload/merge` - 合并分片

## 注意事项

1. 确保Docker服务正常运行
2. 数据目录权限设置正确
3. 端口未被其他服务占用
4. 在生产环境中修改默认用户名和密码



如果启动服务了，先把没有关闭的java进程杀掉，之后重新启动。

## 参考文档

- [MinIO官方文档](https://min.io/docs/minio/container/index.html)
- [Spring Boot文档](https://spring.io/projects/spring-boot)
- [Docker文档](https://docs.docker.com/)