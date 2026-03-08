# 视频相关表设计

## 需求分析

- 数据库中保存用户上传的视频，每个视频关联着一个 `user_id`
- 每个视频包含以下基础信息：视频、封面、标题（文案）、标签
- 每个视频可以包含多个标签，标签名称唯一
- 视频还包括以下属性：创建时间、是否公开、状态（审核中、已发布、已下架、违规）
- 用户互动产生的数据：播放量、点赞数、收藏数、评论数


## 表字段设计

存储视频相关信息需要三张表，分别为视频表（`videos`）、标签字典表（`tags`）和视频标签关联表（`video_tags`）

### 视频表 videos

**基本信息字段**
- `video_id` (BIGINT)，视频唯一标识，主键
- `user_id` (BIGINT)，上传用户的 ID
- `video_url` (VARCHAR(512))，视频文件 URL
- `cover_url` (VARCHAR(512))，视频封面图片 URL，默认为空
- `title` (VARCHAR(300))，视频标题（文案）

**互动数据字段**
- `view_count` (INT)，播放量，默认 0
- `like_count` (INT)，点赞量，默认 0
- `collect_count` (INT)，收藏数，默认 0
- `comment_count` (INT)，评论数，默认 0

**状态控制字段**
- `is_public` (TINYINT)，是否公开，默认 1
  - 0: 私密
  - 1: 公开
- `status` (TINYINT)，视频状态，默认 0
  - 0: 审核中
  - 1: 已发布
  - 2: 已下架
  - 3: 违规

**时间戳**
- `created_at` (TIMESTAMP)，创建时间，默认当前时间

### 标签字典表 tags

管理系统中所有的视频标签信息。

- `tag_id` (BIGINT)，标签唯一标识，主键
- `tag_name` (VARCHAR(50))，标签名称，唯一
- `use_count` (INT)，使用次数，默认 0（冗余字段，用于热门标签统计）
- `created_at` (TIMESTAMP)，创建时间，默认当前时间

### 视频标签关联表 video_tags

用于实现视频与标签的多对多关系，支持高效的标签查询和统计。

- `video_tag_id` (BIGINT)，关联记录唯一标识，主键
- `video_id` (BIGINT)，视频 ID
- `tag_id` (BIGINT)，标签 ID
- `created_at` (TIMESTAMP)，创建时间，默认当前时间


## 建表 SQL

### 视频表

```sql
-- 创建视频表 videos
CREATE TABLE `videos` (
  `video_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '视频唯一标识',
  `user_id` BIGINT NOT NULL COMMENT '上传用户的ID',
  `video_url` VARCHAR(512) NOT NULL COMMENT '视频文件URL',
  `cover_url` VARCHAR(512) DEFAULT '' COMMENT '视频封面图片URL，默认为空',
  `title` VARCHAR(300) NOT NULL COMMENT '视频标题（文案）',
  `view_count` INT DEFAULT 0 COMMENT '播放量，默认0',
  `like_count` INT DEFAULT 0 COMMENT '点赞量，默认0',
  `collect_count` INT DEFAULT 0 COMMENT '收藏数，默认0',
  `comment_count` INT DEFAULT 0 COMMENT '评论数，默认0',
  `is_public` TINYINT DEFAULT 1 COMMENT '是否公开：0-私密，1-公开，默认1',
  `status` TINYINT DEFAULT 0 COMMENT '视频状态：0-审核中，1-已发布，2-已下架，3-违规，默认0',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  PRIMARY KEY (`video_id`),
  KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引，用于按用户查询视频'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频表';
```

### 标签字典表

```sql
-- 创建标签字典表 tags
CREATE TABLE `tags` (
  `tag_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签唯一标识',
  `tag_name` VARCHAR(50) NOT NULL COMMENT '标签名称，唯一',
  `use_count` INT DEFAULT 0 COMMENT '使用次数，默认0（冗余字段，用于热门标签统计）',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `uk_tag_name` (`tag_name`) COMMENT '标签名称唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签字典表';
```

### 视频标签关联表

```sql
-- 创建视频标签关联表 video_tags
CREATE TABLE `video_tags` (
  `video_tag_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联记录唯一标识',
  `video_id` BIGINT NOT NULL COMMENT '视频ID',
  `tag_id` BIGINT NOT NULL COMMENT '标签ID',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  PRIMARY KEY (`video_tag_id`),
  UNIQUE KEY `uk_video_tag` (`video_id`, `tag_id`) COMMENT '视频ID+标签ID唯一索引，防止重复关联'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频标签关联表';
```
