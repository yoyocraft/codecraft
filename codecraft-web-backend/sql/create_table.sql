# 数据库初始化

-- 创建库
CREATE DATABASE IF NOT EXISTS `code_craft`;

-- 切换库
USE `code_craft`;

-- 用户表
CREATE TABLE IF NOT EXISTS `user`
(
    id           BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    userAccount  VARCHAR(32)                           NOT NULL COMMENT '账号',
    userPassword VARCHAR(128)                          NOT NULL COMMENT '密码',
    userName     VARCHAR(32)                           NULL COMMENT '用户昵称',
    userAvatar   VARCHAR(512)                          NULL COMMENT '用户头像',
    userProfile  VARCHAR(512)                          NULL COMMENT '用户简介',
    userRole     VARCHAR(16) DEFAULT 'user'            NOT NULL COMMENT '用户角色：user/admin/ban',
    createTime   DATETIME    DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime   DATETIME    DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete     TINYINT     DEFAULT 0                 NOT NULL COMMENT '是否删除',
    INDEX idx_user_account (userAccount)
) COMMENT '用户' COLLATE = utf8mb4_unicode_ci;

-- 代码生成器表
CREATE TABLE IF NOT EXISTS `generator`
(
    id            BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    name          VARCHAR(64)                        NOT NULL COMMENT '名称',
    description   VARCHAR(512)                       NULL COMMENT '描述',
    basePackage   VARCHAR(64)                        NOT NULL COMMENT '基础包',
    version       VARCHAR(64)                        NOT NULL COMMENT '版本',
    author        VARCHAR(64)                        NULL COMMENT '作者',
    tags          VARCHAR(512)                       NULL COMMENT '标签列表（json 数组）',
    picture       VARCHAR(512)                       NULL COMMENT '图片',
    fileConfig    TEXT                               NULL COMMENT '文件配置（json 字符串）',
    modelConfig   TEXT                               NULL COMMENT '模型配置（json 字符串）',
    distPath      VARCHAR(512)                       NULL COMMENT '代码生成器产物路径',
    status        TINYINT  DEFAULT 0                 NOT NULL COMMENT '状态：0-默认',
    userId        BIGINT                             NOT NULL COMMENT '创建用户 id',
    useCount      INT      DEFAULT 0                 NOT NULL COMMENT '使用次数',
    downloadCount INT      DEFAULT 0                 NOT NULL COMMENT '下载次数',
    createTime    DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime    DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete      TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除',
    INDEX idx_userId (userId)
) COMMENT '代码生成器' COLLATE = utf8mb4_unicode_ci;


-- 模拟用户数据
INSERT INTO code_craft.user (id, userAccount, userPassword, userName, userAvatar, userProfile,
                             userRole)
VALUES (1, 'youyi', 'b0dd3697a192885d7c055db46155b26a', '游艺',
        'https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png',
        '游于艺', 'admin');
INSERT INTO code_craft.user (id, userAccount, userPassword, userName, userAvatar, userProfile,
                             userRole)
VALUES (2, 'youyi2', 'b0dd3697a192885d7c055db46155b26a', '游艺',
        'https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png',
        '游于艺', 'user');

-- 模拟代码生成器数据
INSERT INTO code_craft.generator (id, name, description, basePackage, version, author, tags,
                                  picture, fileConfig, modelConfig, distPath, status, userId)
VALUES (1, 'ACM 模板项目', 'ACM 模板项目生成器', 'com.youyi', '1.0', '游艺', '["Java"]',
        'https://pic.yupi.icu/1/_r0_c1851-bf115939332e.jpg', '{}', '{}', null, 0, 1);
INSERT INTO code_craft.generator (id, name, description, basePackage, version, author, tags,
                                  picture, fileConfig, modelConfig, distPath, status, userId)
VALUES (2, 'Spring Boot 初始化模板', 'Spring Boot 初始化模板项目生成器', 'com.youyi', '1.0',
        '游艺', '["Java"]', 'https://pic.yupi.icu/1/_r0_c0726-7e30f8db802a.jpg', '{}', '{}',
        null, 0, 1);
INSERT INTO code_craft.generator (id, name, description, basePackage, version, author, tags,
                                  picture, fileConfig, modelConfig, distPath, status, userId)
VALUES (3, '游艺外卖', '游艺外卖项目生成器', 'com.youyi', '1.0', '游艺', '["Java", "前端"]',
        'https://pic.yupi.icu/1/_r1_c0cf7-f8e4bd865b4b.jpg', '{}', '{}', null, 0, 1);
INSERT INTO code_craft.generator (id, name, description, basePackage, version, author, tags,
                                  picture, fileConfig, modelConfig, distPath, status, userId)
VALUES (4, '游艺用户中心', '游艺用户中心项目生成器', 'com.youyi', '1.0', '游艺',
        '["Java", "前端"]', 'https://pic.yupi.icu/1/_r1_c1c15-79cdecf24aed.jpg', '{}', '{}', null,
        0, 1);
INSERT INTO code_craft.generator (id, name, description, basePackage, version, author, tags,
                                  picture, fileConfig, modelConfig, distPath, status, userId)
VALUES (5, '游艺商城', '游艺商城项目生成器', 'com.youyi', '1.0', '游艺', '["Java", "前端"]',
        'https://pic.yupi.icu/1/_r1_c0709-8e80689ac1da.jpg', '{}', '{}', null, 0, 1);