# 登录用户信息管理功能

## 获取用户信息功能

### 需求分析

- 用户登录成功后，前端向后端发起获取用户信息请求，并将返回结果进行保存
- 页面右上角显示用户头像；
- 在个人主页的信息卡片中展示的信息包括：头像、昵称、账号、关注数、粉丝数、作品总获赞量、个性签名

### 包含接口

- 获取登录用户信息 - `GET /api/user/profile`

## 修改个人信息功能

### 需求分析
- 用户可以修改的个人信息包括：头像、昵称、账号、个性签名
- 账号唯一，不区分大小写
- 头像上传到 OSS，数据库中保存 url

### 包含接口

- 头像上传 - `POST /api/user/profile/avatar`
- 登录用户信息修改 - `PUT /api/user/profile`


## 获取登录用户信息接口

### 接口定义

- **接口路径**：`GET /api/user/profile`
- **认证要求**：需要登录

### 请求参数

**请求头：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| Authorization | string | 是 | Bearer Token，格式：`Bearer {accessToken}` |

**请求体**：无

### 响应结果

响应示例：

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "userId": 6,
        "username": "admin@qq.com",
        "nickname": "admin",
        "avatarUrl": "",
        "bio": "热爱可抵岁月漫长",
        "followingCount": 1,
        "followerCount": 2,
        "totalLikesReceived": 3
    }
}
```

**响应参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| userId | number | 用户唯一标识 ID |
| username | string | 用户账号 |
| nickname | string | 用户昵称 |
| avatarUrl | string | 用户头像 URL 地址，默认为空 |
| bio | string | 用户个人简介 |
| followingCount | number | 关注数 |
| followerCount | number | 粉丝数 |
| totalLikesReceived | number | 获赞总数 |

**错误状态码说明：**

| HTTP 状态码 | message 示例        | 说明          |
|-------------|-------------------|---------------|
| 401         | "登录状态认证失败"   | 登录状态认证失败 |
| 404         | "用户不存在"        | 未找到对应用户 |


## 登录用户头像上传接口

### 接口定义

- **接口路径**：`POST /api/user/profile/avatar`
- **认证要求**：需要登录

### 请求参数

**请求头：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| Authorization | string | 是 | Bearer Token，格式：`Bearer {accessToken}` |
| Content-Type | string | 是 | multipart/form-data |

**请求参数（Form Data）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | 头像图片文件，支持 jpg/png/jpeg，建议尺寸 200x200，最大 5MB |


### 响应结果

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "avatarUrl": "https://your-bucket.oss-cn-hangzhou.aliyuncs.com/avatar/12345/1a2b3c.jpg"
  }
}
```

**响应参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| avatarUrl | string | 上传成功后的头像 URL，需保存此 URL 用于后续资料更新 |

**错误状态码说明：**

| HTTP 状态码 | message 示例                     | 说明                     |
|-------------|----------------------------------|--------------------------|
| 401         | "登录状态认证失败"                       | 登录状态认证失败             |
| 40007         | "文件大小不能超过 5 MB" | 文件校验失败（格式、大小等）             |
| 500         | "文件上传失败"                       | OSS 服务异常             |


## 登录用户信息修改接口

### 接口定义

- **接口路径**：`PUT /api/user/profile`
- **认证要求**：需要登录

### 请求参数

**请求头：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| Authorization | string | 是 | Bearer Token，格式：`Bearer {accessToken}` |
| Content-Type | string | 是 | application/json |

**请求参数（JSON Body）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 否 | 账号，4-20 个字符，只能包含字母、数字和下划线，唯一，不区分大小写 |
| nickname | string | 否 | 昵称，不能为空，最多 20 个字符 |
| avatarUrl | string | 否 | 头像 URL，需先通过头像上传接口获取 |
| bio | string | 否 | 个性签名/个人简介，最多 200 个字符 |

**请求示例：**
```json
{
  "username": "john_doe_2025",
  "nickname": "John Doe",
  "avatarUrl": "https://your-bucket.oss-cn-hangzhou.aliyuncs.com/avatar/12345/1a2b3c.jpg",
  "bio": "热爱生活，喜欢分享，记录美好瞬间"
}
```


### 响应结果

**响应示例：**
```json
{
  "code": 200,
  "message": "更新成功！",
  "data": {
    "userId": 10001,
    "username": "john_doe_2025",
    "nickname": "John Doe",
    "avatarUrl": "https://your-bucket.oss-cn-hangzhou.aliyuncs.com/avatar/12345/1a2b3c.jpg",
    "bio": "热爱生活，喜欢分享，记录美好瞬间",
    "followingCount": 128,
    "followerCount": 1024,
    "totalLikesReceived": 8888
  }
}
```

**响应参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| userId | number | 用户唯一标识ID |
| username | string | 用户账号 |
| nickname | string | 用户昵称 |
| avatarUrl | string | 用户头像URL地址，默认为空 |
| bio | string | 用户个人简介 |
| followingCount | number | 关注数 |
| followerCount | number | 粉丝数 |
| totalLikesReceived | number | 获赞总数 |

**错误状态码说明：**

| HTTP 状态码 | message 示例                     | 说明                     |
|-------------|----------------------------------|--------------------------|
| 401         | "登录状态认证失败"                       | 登录状态认证失败             |
| 40001         | "账号长度必须在 4-20 个字符之间"                       | 请求参数格式校验失败             |
| 40008         | "至少需要更新一个字段"                       | 请求参数不符合业务规则             |
