# 用户作品创作与列表功能

## 用户作品创作功能

### 需求分析

用户可以上传自己的视频来进行作品创作，需提供的信息包括：视频文件、封面、标题（文案）、标签、是否公开。
- 封面可省略，作品预览时默认将视频的第一帧图片作为封面
- 标题最多 300 个字，默认为空
- 视频标签最少一个，最多 5 个

> 提供标签是为了实现视频推送功能，因此自带的标签必须可信。项目迭代升级后会采用其他方法（如大模型分析）为视频定制标签，此时无需用户提供。

### 包含接口

- 作品创作接口 - `POST /api/videos`

### 说明事项

- 目前使用后端代理上传 OSS 的方案，后期版本迭代更新为前端直传 OSS 以减轻服务器压力，提高响应速度

## 获取用户作品列表功能

## 需求分析

用户登录后，在个人主页的作品标签处可以查看自己的作品列表：
- 每个作品直接展示的信息包括视频封面、视频描述、点赞量、观看量
- 作品暂时按照最新发布时间进行排序
- 作品描述由视频标题和标签组成，标签放在标题后面（空格隔开），标签之间用#号隔开
- 数据库中存储的视频封面可能为空，此时使用视频的第一帧图片作为封面
- 作品右上角显示视频公开状态（审核中/公开/私密/已下架），违规视频不会出现在列表中

## 包含接口

- 获取用户作品列表接口 - `GET /api/videos`

## 作品创作接口

### 接口定义

- **接口路径**：`POST /api/videos`
- **请求方式**：POST
- **认证要求**：需要登录
- **Content-Type**：multipart/form-data

### 请求参数

**请求头：**

| 参数名 | 类型 | 必填 | 说明 |
|------|------|------|------|
| Authorization | string | 是 | Bearer Token，格式：`Bearer {accessToken}` |

**请求参数（Form Data）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| video | File | 是 | 视频文件，支持 mp4/mov/avi 格式，大小 ≤ 300MB |
| cover | File | 否 | 封面图片文件，支持 jpg/png/jpeg，大小 ≤ 5MB |
| title | string | 否 | 视频标题（文案），0～300 字符|
| tags | string | 是 | 标签列表，JSON 数组字符串，1～5 个 |
| isPublic | integer | 否 | 是否公开，0-私密，1-公开，默认 1 |


**请求示例：**
```
POST /api/videos
Authorization: Bearer xxxxx
Content-Type: multipart/form-data

video: (file)
cover: (file)
title: 周末探店｜这家咖啡店的拿铁真的绝了！
tags: "[\"咖啡\",\"探店\",\"生活记录\"]"
isPublic: 1
```

### 响应结果

**正常响应（HTTP 200）**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**业务错误码：**

| code | message                     | 说明                     |
|-------------|----------------------------------|--------------------------|
| 401         | "登录状态认证失败"                       | 登录状态认证失败             |
| 40007         | "文件大小不能超过 5 MB" | 文件校验失败（格式、大小等）             |
| 500         | "文件上传失败"                       | OSS 服务异常             |

## 获取用户作品列表接口

### 接口定义
- **接口路径**：`GET /api/videos`
- **请求方式**：GET
- **认证要求**：需要登录

### 请求参数
**请求头：**

| 参数名 | 类型 | 必填 | 说明 |
|------|------|------|------|
| Authorization | string | 是 | Bearer Token，格式：`Bearer {accessToken}` |

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | integer | 否 | 页码，从 1 开始，默认 1 |
| pageSize | integer | 否 | 每页数量，默认 20，最大 50 |

**请求示例：**
```
GET /api/videos?page=1&pageSize=20
Authorization: Bearer xxxxx
```


### 响应结果
**正常响应（HTTP 200）**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 10001,
    "total": 42,
    "page": 1,
    "pageSize": 20,
    "videos": [
      {
        "videoId": 20001,
        "title": "周末探店｜这家咖啡店的拿铁真的绝了！",
        "coverUrl": "https://cdn.example.com/covers/10001/cover.jpg",
        "videoUrl": "https://cdn.example.com/videos/10001/video.mp4",
        "tags": ["咖啡", "探店", "生活记录"],
        "viewCount": 1234,
        "likeCount": 88,
        "status": 1,
        "isPublic": 1,
        "createTime": "2026-01-26 14:30:00"
      },
      {
        "videoId": 20002,
        "title": "分享一个超好吃的家常菜",
        "coverUrl": null,
        "videoUrl": "https://cdn.example.com/videos/10001/video.mp4",
        "tags": ["美食", "家常菜"],
        "viewCount": 567,
        "likeCount": 45,
        "status": 0,
        "isPublic": 1,
        "createTime": "2026-01-25 10:20:00"
      }
    ]
  }
}
```

**响应参数说明：**

| 参数 | 类型 | 说明 |
|------|------|------|
| userId | number | 用户 ID，标识当前作品列表所属用户 |
| total | number | 作品总数（不包含违规视频） |
| page | number | 当前页码 |
| pageSize | number | 每页数量 |
| videos | array | 作品列表，按创建时间倒序排序 |

**videos 数组元素说明：**

| 参数 | 类型 | 说明 |
|------|------|------|
| videoId | number | 视频唯一 ID |
| title | string | 视频标题，可能为空字符串 |
| coverUrl | string | 封面图片 URL，若为空则前端使用视频第一帧作为封面 |
| videoUrl | string | 视频文件 URL |
| tags | array | 标签列表，字符串数组 |
| viewCount | number | 观看量 |
| likeCount | number | 点赞量 |
| status | number | 视频状态：0-审核中、1-已发布、2-已下架、3-违规 |
| isPublic | number | 是否公开，0-私密，1-公开 |
| createTime | string | 创建时间，格式：yyyy-MM-dd HH:mm:ss |

**业务错误码说明：**

| code | message                     | 说明                     |
|-------------|----------------------------------|--------------------------|
| 401         | "登录状态认证失败"                       | 登录状态认证失败             |
| 40001         | "页码必须为大于 0 的整数" | 请求参数格式错误             |
| 500         | "服务不可用"                       | 服务端查询数据出错             |
