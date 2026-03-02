## 用户表设计

### 需求分析

- 使用邮箱作为用户注册入口，通过邮箱和密码进行登录
- 个人主页展示的信息包括头像、昵称、账号、关注数、粉丝数、作品总获赞量、个性签名（简介）

### 字段说明

**登录凭证字段：**
- 邮箱（email）：唯一性约束，统一转小写存储
- 密码（password_hash）：存储密码哈希，保证安全性

**对外展示字段：**
- 头像（avatar_url）：头像 URL
- 昵称（nickname）：昵称，最多 32 个字符
- 账号（username）：账号，唯一性标识，查询时忽略大小写
- 个性签名（bio）：个人简介，最多 512 个字符

**统计字段（冗余存储）：**
- 关注数（following_count）
- 粉丝数（follower_count）
- 作品总获赞量（total_likes_received）

**账号状态与时间戳字段：**
- 创建时间（created_at）：账号创建时间
- 最后登录时间（last_login_at）：最后登录时间
- 注销时间（deleted_at）：注销时间
- 账号状态（status）：账号状态，0-正常，1-封禁，2-注销

### 建表 SQL

```sql
CREATE TABLE users (
    user_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '用户主键ID',

    -- 登录凭证字段
    email VARCHAR(255) NOT NULL UNIQUE COMMENT '邮箱（统一小写存储）',
    password_hash CHAR(60) NOT NULL COMMENT '密码哈希（如 bcrypt 生成）',

    -- 对外展示字段
    avatar_url VARCHAR(512) DEFAULT '' COMMENT '头像URL',
    nickname VARCHAR(32) DEFAULT '' COMMENT '昵称，最多32字符',
    username VARCHAR(64) NOT NULL UNIQUE COMMENT '账号（唯一标识）',
    bio VARCHAR(512) DEFAULT '' COMMENT '个性签名/简介，最多512字符',

    -- 统计字段（冗余存储）
    following_count INT UNSIGNED DEFAULT 0 COMMENT '关注数',
    follower_count INT UNSIGNED DEFAULT 0 COMMENT '粉丝数',
    total_likes_received BIGINT UNSIGNED DEFAULT 0 COMMENT '作品总获赞量',

    -- 账号状态与时间戳字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '账号创建时间',
    last_login_at DATETIME DEFAULT NULL COMMENT '最后登录时间',
    deleted_at DATETIME DEFAULT NULL COMMENT '注销时间（软删除）',
    status TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '账号状态：0-正常，1-封禁，2-注销',
    
    -- 使用 CHECK 约束确保 status 只能是 0、1、2（MySQL 8.0.16+ 支持，低版本会忽略但不影响建表）
    CONSTRAINT chk_status CHECK (status IN (0, 1, 2))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```

**索引说明：**
- `email` 和 `username` 为 UNIQUE 约束，已隐含索引，无需额外创建。
