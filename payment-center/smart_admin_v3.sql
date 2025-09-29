-- --------------------------------------------------------
-- 主机:                           82.157.125.186
-- 服务器版本:                     8.0.26 - MySQL Community Server
-- 服务器操作系统:                 Linux
-- --------------------------------------------------------



-- 导出 smart_admin_v3 的数据库结构
DROP DATABASE IF EXISTS `smart_admin_v3`;
CREATE DATABASE IF NOT EXISTS `smart_admin_v3` ;
USE `smart_admin_v3`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_category
-- ----------------------------
DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category`  (
  `category_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '分类id',
  `category_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `category_type` smallint(0) NOT NULL COMMENT '分类类型',
  `parent_id` int(0) NOT NULL COMMENT '父级id',
  `sort` int(0) NOT NULL DEFAULT 0 COMMENT '排序',
  `disabled_flag` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否禁用',
  `deleted_flag` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 361 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分类表，主要用于商品分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_change_log
-- ----------------------------
DROP TABLE IF EXISTS `t_change_log`;
CREATE TABLE `t_change_log`  (
  `change_log_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '更新日志id',
  `version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '版本',
  `type` int(0) NOT NULL COMMENT '更新类型:[1:特大版本功能更新;2:功能更新;3:bug修复]',
  `publish_author` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发布人',
  `public_date` date NOT NULL COMMENT '发布日期',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新内容',
  `link` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '跳转链接',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`change_log_id`) USING BTREE,
  UNIQUE INDEX `version_unique`(`version`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统更新日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_code_generator_config
-- ----------------------------
DROP TABLE IF EXISTS `t_code_generator_config`;
CREATE TABLE `t_code_generator_config`  (
  `table_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表名',
  `basic` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '基础命名信息',
  `fields` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '字段列表',
  `insert_and_update` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '新建、修改',
  `delete_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '删除',
  `query_fields` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '查询',
  `table_fields` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '列表',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '详情',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`table_name`) USING BTREE,
  UNIQUE INDEX `table_unique`(`table_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成器的每个表的配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_code_generator_config
-- ----------------------------

-- ----------------------------
-- Table structure for t_config
-- ----------------------------
DROP TABLE IF EXISTS `t_config`;
CREATE TABLE `t_config`  (
  `config_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `config_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数名字',
  `config_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数key',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '上次修改时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_config
-- ----------------------------
INSERT INTO `t_config` VALUES (1, '万能密码', 'super_password', '1024lab', '建议定期修改', '2023-10-06 16:36:07', '2021-12-16 23:32:46');

-- ----------------------------
-- Table structure for t_data_tracer
-- ----------------------------
DROP TABLE IF EXISTS `t_data_tracer`;
CREATE TABLE `t_data_tracer`  (
  `data_tracer_id` bigint(0) NOT NULL AUTO_INCREMENT,
  `data_id` bigint(0) NOT NULL COMMENT '各种单据的id',
  `type` int(0) NOT NULL COMMENT '单据类型',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '操作内容',
  `diff_old` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '差异：旧的数据',
  `diff_new` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '差异：新的数据',
  `extra_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '额外信息',
  `user_id` bigint(0) NOT NULL COMMENT '用户id',
  `user_type` int(0) NOT NULL COMMENT '用户类型：1 后管用户 ',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名称',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'ip',
  `ip_region` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'ip地区',
  `user_agent` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户ua',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`data_tracer_id`) USING BTREE,
  INDEX `order_id_order_type`(`data_id`, `type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '各种单据操作记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_data_tracer
-- ----------------------------

-- ----------------------------
-- Table structure for t_department
-- ----------------------------
DROP TABLE IF EXISTS `t_department`;
CREATE TABLE `t_department`  (
  `department_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '部门主键id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '部门名称',
  `manager_id` bigint(0) NULL DEFAULT NULL COMMENT '部门负责人id',
  `parent_id` bigint(0) NOT NULL DEFAULT 0 COMMENT '部门的父级id',
  `sort` int(0) NOT NULL COMMENT '部门排序',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`department_id`) USING BTREE,
  INDEX `parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '部门' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_department
-- ----------------------------
INSERT INTO `t_department` VALUES (1, '1024创新实验室', 1, 0, 1, '2022-10-19 20:17:09', '2022-10-19 20:17:09');
INSERT INTO `t_department` VALUES (2, '开发部', 44, 1, 1000, '2022-10-19 20:22:23', '2022-10-19 20:22:23');
INSERT INTO `t_department` VALUES (3, '产品部', 2, 1, 99, '2022-10-21 10:25:30', '2022-10-21 10:25:30');
INSERT INTO `t_department` VALUES (4, '销售部', 64, 1, 9, '2022-10-21 10:25:47', '2022-10-21 10:25:47');
INSERT INTO `t_department` VALUES (5, '测试部', 48, 1, 0, '2022-11-05 10:54:18', '2022-11-05 10:54:18');

-- ----------------------------
-- Table structure for t_dict_key
-- ----------------------------
DROP TABLE IF EXISTS `t_dict_key`;
CREATE TABLE `t_dict_key`  (
  `dict_key_id` bigint(0) NOT NULL AUTO_INCREMENT,
  `key_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编码',
  `key_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted_flag` tinyint(0) NOT NULL DEFAULT 0 COMMENT '删除状态',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`dict_key_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段key' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_dict_key
-- ----------------------------

-- ----------------------------
-- Table structure for t_dict_value
-- ----------------------------
DROP TABLE IF EXISTS `t_dict_value`;
CREATE TABLE `t_dict_value`  (
  `dict_value_id` bigint(0) NOT NULL AUTO_INCREMENT,
  `dict_key_id` bigint(0) NOT NULL,
  `value_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编码',
  `value_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `sort` int(0) NOT NULL DEFAULT 0 COMMENT '排序',
  `deleted_flag` tinyint(0) NOT NULL DEFAULT 0 COMMENT '删除状态',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`dict_value_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典的值' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_dict_value
-- ----------------------------

-- ----------------------------
-- Table structure for t_employee
-- ----------------------------
DROP TABLE IF EXISTS `t_employee`;
CREATE TABLE `t_employee`  (
  `employee_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `login_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录帐号',
  `login_pwd` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录密码',
  `actual_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '员工名称',
  `gender` tinyint(1) NOT NULL DEFAULT 0 COMMENT '性别',
  `phone` varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `department_id` int(0) NOT NULL COMMENT '部门id',
  `disabled_flag` tinyint unsigned NOT NULL COMMENT '是否被禁用 0否1是',
  `deleted_flag` tinyint unsigned NOT NULL COMMENT '是否删除0否 1是',
  `administrator_flag` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否为超级管理员: 0 不是，1是',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`employee_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 73 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '员工表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_employee
-- ----------------------------
INSERT INTO `t_employee` VALUES (1, 'admin', '40cc20b8891cd3fd1f008ea7f4ac17c3', '管理员', 0, '13500000000', 1, 0, 0, 1, NULL, '2024-01-08 18:36:59', '2018-05-11 09:38:54');

-- ----------------------------
-- Table structure for t_feedback
-- ----------------------------
DROP TABLE IF EXISTS `t_feedback`;
CREATE TABLE `t_feedback`  (
  `feedback_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `feedback_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '反馈内容',
  `feedback_attachment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '反馈图片',
  `user_id` bigint(0) NOT NULL COMMENT '创建人id',
  `user_type` int(0) NOT NULL COMMENT '创建人用户类型',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人姓名',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`feedback_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '意见反馈' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_feedback
-- ----------------------------

-- ----------------------------
-- Table structure for t_file
-- ----------------------------
DROP TABLE IF EXISTS `t_file`;
CREATE TABLE `t_file`  (
  `file_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `folder_type` tinyint unsigned NOT NULL COMMENT '文件夹类型',
  `file_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件名称',
  `file_size` int(0) NULL DEFAULT NULL COMMENT '文件大小',
  `file_key` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件key，用于文件下载',
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件类型',
  `creator_id` int(0) NULL DEFAULT NULL COMMENT '创建人，即上传人',
  `creator_user_type` int(0) NULL DEFAULT NULL COMMENT '创建人用户类型',
  `creator_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人姓名',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上次更新时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`file_id`) USING BTREE,
  UNIQUE INDEX `uk_file_key`(`file_key`) USING BTREE,
  INDEX `module_id_module_type`(`folder_type`) USING BTREE,
  INDEX `module_type`(`folder_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_file
-- ----------------------------

-- ----------------------------
-- Table structure for t_goods
-- ----------------------------
DROP TABLE IF EXISTS `t_goods`;
CREATE TABLE `t_goods`  (
  `goods_id` int(0) NOT NULL AUTO_INCREMENT,
  `goods_status` int(0) NULL DEFAULT NULL COMMENT '商品状态:[1:预约中,2:售卖中,3:售罄]',
  `category_id` int(0) NOT NULL COMMENT '商品类目',
  `goods_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
  `place` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产地',
  `price` decimal(10, 2) UNSIGNED NOT NULL COMMENT '价格',
  `shelves_flag` tinyint unsigned NOT NULL COMMENT '上架状态',
  `deleted_flag` tinyint unsigned NOT NULL COMMENT '删除状态',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`goods_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_goods
-- ----------------------------

-- ----------------------------
-- Table structure for t_heart_beat_record
-- ----------------------------
DROP TABLE IF EXISTS `t_heart_beat_record`;
CREATE TABLE `t_heart_beat_record`  (
  `heart_beat_record_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `project_path` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目名称',
  `server_ip` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '服务器ip',
  `process_no` int(0) NOT NULL COMMENT '进程号',
  `process_start_time` datetime(0) NOT NULL COMMENT '进程开启时间',
  `heart_beat_time` datetime(0) NOT NULL COMMENT '心跳时间',
  PRIMARY KEY (`heart_beat_record_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 197 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '公用服务 - 服务心跳' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_heart_beat_record
-- ----------------------------

-- ----------------------------
-- Table structure for t_help_doc
-- ----------------------------
DROP TABLE IF EXISTS `t_help_doc`;
CREATE TABLE `t_help_doc`  (
  `help_doc_id` bigint(0) NOT NULL AUTO_INCREMENT,
  `help_doc_catalog_id` bigint(0) NOT NULL COMMENT '类型1公告 2动态',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `content_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文本内容',
  `content_html` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'html内容',
  `attachment` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '附件',
  `sort` int(0) NOT NULL DEFAULT 0 COMMENT '排序',
  `page_view_count` int(0) NOT NULL DEFAULT 0 COMMENT '页面浏览量，传说中的pv',
  `user_view_count` int(0) NOT NULL DEFAULT 0 COMMENT '用户浏览量，传说中的uv',
  `author` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作者',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`help_doc_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '帮助文档' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_help_doc
-- ----------------------------
INSERT INTO `t_help_doc` VALUES (32, 6, '企业名称该写什么？', '需求1：管理公司基本信息，包含：企业名称、Logo、地区、营业执照、联系人 等等，可以 增删拆改需求2：管理公司的银行账户，包含：银行信息、账户名称、账号、类型等，可以 增删拆改需求3：管理公司的发票信息，包含：开票抬头、纳税号、银行账户、开户行、备注等，可以 增删拆改需求4：对于公司信息、银行信息、发票信息 任何的修改，都有记录 数据变动记录；', '<ul><li style=\"text-align: start;\">需求1：管理公司基本信息，包含：企业名称、Logo、地区、营业执照、联系人 等等，可以 增删拆改</li><li style=\"text-align: start;\">需求2：管理公司的银行账户，包含：银行信息、账户名称、账号、类型等，可以 增删拆改</li><li style=\"text-align: start;\">需求3：管理公司的发票信息，包含：开票抬头、纳税号、银行账户、开户行、备注等，可以 增删拆改</li><li style=\"text-align: start;\">需求4：对于公司信息、银行信息、发票信息 任何的修改，都有记录 数据变动记录；</li></ul>', '', 0, 49, 1, '卓大', '2024-01-08 19:02:31', '2022-11-22 10:41:48');

-- ----------------------------
-- Table structure for t_help_doc_catalog
-- ----------------------------
DROP TABLE IF EXISTS `t_help_doc_catalog`;
CREATE TABLE `t_help_doc_catalog`  (
  `help_doc_catalog_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '帮助文档目录',
  `name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `sort` int(0) NOT NULL DEFAULT 0 COMMENT '排序字段',
  `parent_id` bigint(0) NOT NULL COMMENT '父级id',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`help_doc_catalog_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '帮助文档-目录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_help_doc_catalog
-- ----------------------------
INSERT INTO `t_help_doc_catalog` VALUES (6, '企业信息', 0, 0, '2022-11-05 10:52:40', '2022-11-22 10:37:38');

-- ----------------------------
-- Table structure for t_help_doc_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_help_doc_relation`;
CREATE TABLE `t_help_doc_relation`  (
  `relation_id` bigint(0) NOT NULL COMMENT '关联id',
  `relation_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联名称',
  `help_doc_id` bigint(0) NOT NULL COMMENT '文档id',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`relation_id`, `help_doc_id`) USING BTREE,
  UNIQUE INDEX `uni_menu_help_doc`(`relation_id`, `help_doc_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '帮助文档-关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_help_doc_relation
-- ----------------------------
INSERT INTO `t_help_doc_relation` VALUES (0, '首页', 32, '2023-12-04 13:34:17', '2023-12-04 13:34:17');

-- ----------------------------
-- Table structure for t_help_doc_view_record
-- ----------------------------
DROP TABLE IF EXISTS `t_help_doc_view_record`;
CREATE TABLE `t_help_doc_view_record`  (
  `help_doc_id` bigint(0) NOT NULL COMMENT '通知公告id',
  `user_id` bigint(0) NOT NULL COMMENT '用户id',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名称',
  `page_view_count` int(0) NULL DEFAULT 0 COMMENT '查看次数',
  `first_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '首次ip',
  `first_user_agent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '首次用户设备等标识',
  `last_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后一次ip',
  `last_user_agent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后一次用户设备等标识',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`help_doc_id`, `user_id`) USING BTREE,
  UNIQUE INDEX `uk_notice_employee`(`help_doc_id`, `user_id`) USING BTREE COMMENT '资讯员工'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '帮助文档-查看记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_help_doc_view_record
-- ----------------------------

-- ----------------------------
-- Table structure for t_login_fail
-- ----------------------------
DROP TABLE IF EXISTS `t_login_fail`;
CREATE TABLE `t_login_fail`  (
  `login_fail_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` bigint(0) NOT NULL COMMENT '用户id',
  `user_type` int(0) NOT NULL COMMENT '用户类型',
  `login_name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录名',
  `login_fail_count` int(0) NULL DEFAULT NULL COMMENT '连续登录失败次数',
  `lock_flag` tinyint(0) NULL DEFAULT 0 COMMENT '锁定状态:1锁定，0未锁定',
  `login_lock_begin_time` datetime(0) NULL DEFAULT NULL COMMENT '连续登录失败锁定开始时间',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`login_fail_id`) USING BTREE,
  UNIQUE INDEX `uid_and_utype`(`user_id`, `user_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '登录失败次数记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_login_log
-- ----------------------------
DROP TABLE IF EXISTS `t_login_log`;
CREATE TABLE `t_login_log`  (
  `login_log_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(0) NOT NULL COMMENT '用户id',
  `user_type` int(0) NOT NULL COMMENT '用户类型',
  `user_name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `login_ip` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户ip',
  `login_ip_region` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户ip地区',
  `user_agent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'user-agent信息',
  `login_result` int(0) NOT NULL COMMENT '登录结果：0成功 1失败 2 退出',
  `remark` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`login_log_id`) USING BTREE,
  INDEX `customer_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 111 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户登录日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_menu`;
CREATE TABLE `t_menu`  (
  `menu_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `menu_type` int(0) NOT NULL COMMENT '类型',
  `parent_id` bigint(0) NOT NULL COMMENT '父菜单ID',
  `sort` int(0) NULL DEFAULT NULL COMMENT '显示顺序',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `perms_type` int(0) NULL DEFAULT NULL COMMENT '权限类型',
  `api_perms` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '后端权限字符串',
  `web_perms` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '前端权限字符串',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `context_menu_id` bigint(0) NULL DEFAULT NULL COMMENT '功能点关联菜单ID',
  `frame_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为外链',
  `frame_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '外链地址',
  `cache_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否缓存',
  `visible_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '显示状态',
  `disabled_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '禁用状态',
  `deleted_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态',
  `create_user_id` bigint(0) NOT NULL COMMENT '创建人',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user_id` bigint(0) NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 219 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_menu
-- ----------------------------
INSERT INTO `t_menu` VALUES (26, '菜单管理', 2, 50, 1, '/menu/list', '/system/menu/menu-list.vue', NULL, NULL, NULL, 'CopyOutlined', NULL, 0, NULL, 1, 1, 0, 0, 2, '2021-08-09 15:04:35', 1, '2023-12-01 19:39:03');
INSERT INTO `t_menu` VALUES (40, '删除', 3, 26, NULL, NULL, NULL, 1, 'system:menu:batchDelete', 'system:menu:batchDelete', NULL, 26, 0, NULL, 0, 1, 0, 0, 1, '2021-08-12 09:45:56', 1, '2023-10-07 18:15:50');
INSERT INTO `t_menu` VALUES (45, '部门员工', 1, 0, 3, '/organization', NULL, NULL, NULL, NULL, 'UserSwitchOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2021-08-12 16:13:27', 1, '2023-11-28 17:42:25');
INSERT INTO `t_menu` VALUES (46, '部门员工', 2, 45, 1, '/employee/department', '/system/employee/department/index.vue', NULL, NULL, NULL, 'AuditOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2021-08-12 16:21:50', 1, '2022-06-23 16:19:54');
INSERT INTO `t_menu` VALUES (50, '系统设置', 1, 0, 6, '/setting', NULL, NULL, NULL, NULL, 'SettingOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2021-08-13 16:41:33', 1, '2023-12-01 19:38:03');
INSERT INTO `t_menu` VALUES (76, '角色管理', 2, 45, 2, '/employee/role', '/system/employee/role/index.vue', NULL, NULL, NULL, 'SlidersOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2021-08-26 10:31:00', 1, '2022-06-23 16:21:06');
INSERT INTO `t_menu` VALUES (81, '请求监控', 2, 111, 3, '/support/operate-log/operate-log-list', '/support/operate-log/operate-log-list.vue', NULL, NULL, NULL, 'VideoCameraOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-20 12:37:24', 1, '2022-10-22 18:33:10');
INSERT INTO `t_menu` VALUES (85, '组件演示', 2, 84, NULL, '/demonstration/index', '/support/demonstration/index.vue', NULL, NULL, NULL, 'ClearOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-20 23:16:46', NULL, '2022-05-20 23:16:46');
INSERT INTO `t_menu` VALUES (86, '添加部门', 3, 46, 1, NULL, NULL, 1, 'system:department:add', 'system:department:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-26 23:33:37', 1, '2023-10-07 18:26:35');
INSERT INTO `t_menu` VALUES (87, '修改部门', 3, 46, 2, NULL, NULL, 1, 'system:department:update', 'system:department:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-26 23:34:11', 1, '2023-10-07 18:26:44');
INSERT INTO `t_menu` VALUES (88, '删除部门', 3, 46, 3, NULL, NULL, 1, 'system:department:delete', 'system:department:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-26 23:34:49', 1, '2023-10-07 18:26:49');
INSERT INTO `t_menu` VALUES (91, '添加员工', 3, 46, NULL, NULL, NULL, 1, 'system:employee:add', 'system:employee:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:11:38', 1, '2023-10-07 18:27:46');
INSERT INTO `t_menu` VALUES (92, '编辑员工', 3, 46, NULL, NULL, NULL, 1, 'system:employee:update', 'system:employee:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:12:10', 1, '2023-10-07 18:27:49');
INSERT INTO `t_menu` VALUES (93, '禁用启用员工', 3, 46, NULL, NULL, NULL, 1, 'system:employee:disabled', 'system:employee:disabled', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:12:37', 1, '2023-10-07 18:27:53');
INSERT INTO `t_menu` VALUES (94, '调整员工部门', 3, 46, NULL, NULL, NULL, 1, 'system:employee:department:update', 'system:employee:department:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:12:59', 1, '2023-10-07 18:27:34');
INSERT INTO `t_menu` VALUES (95, '重置密码', 3, 46, NULL, NULL, NULL, 1, 'system:employee:password:reset', 'system:employee:password:reset', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:13:30', 1, '2023-10-07 18:27:57');
INSERT INTO `t_menu` VALUES (96, '删除员工', 3, 46, NULL, NULL, NULL, 1, 'system:employee:delete', 'system:employee:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:14:08', 1, '2023-10-07 18:28:01');
INSERT INTO `t_menu` VALUES (97, '添加角色', 3, 76, NULL, NULL, NULL, 1, 'system:role:add', 'system:role:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:34:00', 1, '2023-10-07 18:42:31');
INSERT INTO `t_menu` VALUES (98, '删除角色', 3, 76, NULL, NULL, NULL, 1, 'system:role:delete', 'system:role:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:34:19', 1, '2023-10-07 18:42:35');
INSERT INTO `t_menu` VALUES (99, '编辑角色', 3, 76, NULL, NULL, NULL, 1, 'system:role:update', 'system:role:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:34:55', 1, '2023-10-07 18:42:44');
INSERT INTO `t_menu` VALUES (100, '更新数据范围', 3, 76, NULL, NULL, NULL, 1, 'system:role:dataScope:update', 'system:role:dataScope:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:37:03', 1, '2023-10-07 18:41:49');
INSERT INTO `t_menu` VALUES (101, '批量移除员工', 3, 76, NULL, NULL, NULL, 1, 'system:role:employee:batch:delete', 'system:role:employee:batch:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:39:05', 1, '2023-10-07 18:43:32');
INSERT INTO `t_menu` VALUES (102, '移除员工', 3, 76, NULL, NULL, NULL, 1, 'system:role:employee:delete', 'system:role:employee:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:39:21', 1, '2023-10-07 18:43:37');
INSERT INTO `t_menu` VALUES (103, '添加员工', 3, 76, NULL, NULL, NULL, 1, 'system:role:employee:add', 'system:role:employee:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:39:38', 1, '2023-10-07 18:44:05');
INSERT INTO `t_menu` VALUES (104, '修改权限', 3, 76, NULL, NULL, NULL, 1, 'system:role:menu:update', 'system:role:menu:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:41:55', 1, '2023-10-07 18:44:11');
INSERT INTO `t_menu` VALUES (105, '添加', 3, 26, NULL, NULL, NULL, 1, 'system:menu:add', 'system:menu:add', NULL, 26, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:44:37', 1, '2023-10-07 17:35:35');
INSERT INTO `t_menu` VALUES (106, '编辑', 3, 26, NULL, NULL, NULL, 1, 'system:menu:update', 'system:menu:update', NULL, 26, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:44:59', 1, '2023-10-07 17:35:48');
INSERT INTO `t_menu` VALUES (109, '参数配置', 2, 50, 3, '/config/config-list', '/support/config/config-list.vue', NULL, NULL, NULL, 'AntDesignOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 13:34:41', 1, '2022-06-23 16:24:16');
INSERT INTO `t_menu` VALUES (110, '数据字典', 2, 50, 4, '/setting/dict', '/support/dict/index.vue', NULL, NULL, NULL, 'BarcodeOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 17:53:00', 1, '2022-05-27 18:09:14');
INSERT INTO `t_menu` VALUES (111, '监控服务', 1, 0, 100, '/monitor', NULL, NULL, NULL, NULL, 'BarChartOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-17 11:13:23', 1, '2023-11-28 17:43:56');
INSERT INTO `t_menu` VALUES (113, '查询', 3, 112, NULL, NULL, NULL, NULL, NULL, 'ad', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-17 11:31:36', NULL, '2022-06-17 11:31:36');
INSERT INTO `t_menu` VALUES (114, '运维工具', 1, 0, 200, NULL, NULL, NULL, NULL, NULL, 'NodeCollapseOutlined', NULL, 0, NULL, 0, 1, 0, 1, 1, '2022-06-20 10:09:16', 1, '2023-12-01 19:36:18');
INSERT INTO `t_menu` VALUES (117, 'Reload', 2, 50, 12, '/hook', '/support/reload/reload-list.vue', NULL, NULL, NULL, 'ReloadOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-20 10:16:49', 1, '2023-12-01 19:39:17');
INSERT INTO `t_menu` VALUES (122, '数据库监控', 2, 111, 4, '/support/druid/index', NULL, NULL, NULL, NULL, 'ConsoleSqlOutlined', NULL, 1, 'http://localhost:1024/druid', 1, 1, 0, 0, 1, '2022-06-20 14:49:33', 1, '2023-02-16 19:15:58');
INSERT INTO `t_menu` VALUES (130, '单号管理', 2, 50, 6, '/support/serial-number/serial-number-list', '/support/serial-number/serial-number-list.vue', NULL, NULL, NULL, 'NumberOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-24 14:45:22', 1, '2022-06-28 16:23:41');
INSERT INTO `t_menu` VALUES (132, '通知公告', 2, 138, 2, '/oa/notice/notice-list', '/business/oa/notice/notice-list.vue', NULL, NULL, NULL, 'SoundOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2022-06-24 18:23:09', 1, '2023-12-04 13:42:23');
INSERT INTO `t_menu` VALUES (133, '缓存管理', 2, 50, 11, '/support/cache/cache-list', '/support/cache/cache-list.vue', NULL, NULL, NULL, 'BorderInnerOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-24 18:52:25', 1, '2023-12-01 19:39:13');
INSERT INTO `t_menu` VALUES (138, 'OA系统', 1, 0, 1, NULL, NULL, NULL, NULL, NULL, 'BankOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-24 20:09:18', 1, '2022-10-22 18:30:15');
INSERT INTO `t_menu` VALUES (142, '公告详情', 2, 132, NULL, '/oa/notice/notice-detail', '/business/oa/notice/notice-detail.vue', NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, 0, 0, 0, 1, '2022-06-25 16:38:47', 1, '2022-09-14 19:46:17');
INSERT INTO `t_menu` VALUES (143, '登录日志', 2, 213, 2, '/support/login-log/login-log-list', '/support/login-log/login-log-list.vue', NULL, NULL, NULL, 'LoginOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-28 15:01:38', 1, '2023-10-17 19:04:34');
INSERT INTO `t_menu` VALUES (144, '企业信息', 2, 138, 1, '/oa/enterprise/enterprise-list', '/business/oa/enterprise/enterprise-list.vue', NULL, NULL, NULL, 'ShopOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-09-14 17:00:07', 1, '2023-12-04 13:42:19');
INSERT INTO `t_menu` VALUES (145, '企业详情', 2, 138, NULL, '/oa/enterprise/enterprise-detail', '/business/oa/enterprise/enterprise-detail.vue', NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, 0, 0, 0, 1, '2022-09-14 18:52:52', 1, '2022-11-22 10:39:07');
INSERT INTO `t_menu` VALUES (147, '帮助文档', 2, 218, 1, '/help-doc/help-doc-manage-list', '/support/help-doc/management/help-doc-manage-list.vue', NULL, NULL, NULL, 'FolderViewOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-09-14 19:59:01', 1, '2023-12-01 19:38:23');
INSERT INTO `t_menu` VALUES (148, '意见反馈', 2, 218, 2, '/feedback/feedback-list', '/support/feedback/feedback-list.vue', NULL, NULL, NULL, 'CoffeeOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-09-14 19:59:52', 1, '2023-12-01 19:38:40');
INSERT INTO `t_menu` VALUES (149, '我的通知', 2, 132, NULL, '/oa/notice/notice-employee-list', '/business/oa/notice/notice-employee-list.vue', NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, 0, 0, 0, 1, '2022-09-14 20:29:41', 1, '2022-09-14 20:31:23');
INSERT INTO `t_menu` VALUES (150, '我的通知公告详情', 2, 132, NULL, '/oa/notice/notice-employee-detail', '/business/oa/notice/notice-employee-detail.vue', NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, 0, 0, 0, 1, '2022-09-14 20:30:25', 1, '2022-09-14 20:31:38');
INSERT INTO `t_menu` VALUES (151, '代码生成', 2, 0, 600, '/support/code-generator', '/support/code-generator/code-generator-list.vue', NULL, NULL, NULL, 'CoffeeOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-09-21 18:25:05', 1, '2022-10-22 11:27:58');
INSERT INTO `t_menu` VALUES (152, '更新日志', 2, 218, 3, '/support/change-log/change-log-list', '/support/change-log/change-log-list.vue', NULL, NULL, NULL, 'HeartOutlined', NULL, 0, NULL, 0, 1, 0, 0, 44, '2022-10-10 10:31:20', 1, '2023-12-01 19:38:51');
INSERT INTO `t_menu` VALUES (153, '清除缓存', 3, 133, NULL, NULL, NULL, 1, 'support:cache:delete', 'support:cache:delete', NULL, 133, 0, NULL, 0, 1, 1, 0, 1, '2022-10-15 22:45:13', 1, '2023-10-07 16:22:29');
INSERT INTO `t_menu` VALUES (154, '获取缓存key', 3, 133, NULL, NULL, NULL, 1, 'support:cache:keys', 'support:cache:keys', NULL, 133, 0, NULL, 0, 1, 1, 0, 1, '2022-10-15 22:45:48', 1, '2023-10-07 16:22:35');
INSERT INTO `t_menu` VALUES (156, '查看结果', 3, 117, NULL, NULL, NULL, 1, 'support:reload:result', 'support:reload:result', NULL, 117, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:17:23', 1, '2023-10-07 14:31:47');
INSERT INTO `t_menu` VALUES (157, '单号生成', 3, 130, NULL, NULL, NULL, 1, 'support:serialNumber:generate', 'support:serialNumber:generate', NULL, 130, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:21:06', 1, '2023-10-07 18:22:46');
INSERT INTO `t_menu` VALUES (158, '生成记录', 3, 130, NULL, NULL, NULL, 1, 'support:serialNumber:record', 'support:serialNumber:record', NULL, 130, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:21:34', 1, '2023-10-07 18:22:55');
INSERT INTO `t_menu` VALUES (159, '新建', 3, 110, NULL, NULL, NULL, 1, 'support:dict:add', 'support:dict:add', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:23:51', 1, '2023-10-07 18:18:24');
INSERT INTO `t_menu` VALUES (160, '编辑', 3, 110, NULL, NULL, NULL, 1, 'support:dict:edit', 'support:dict:edit', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:24:05', 1, '2023-10-07 18:19:17');
INSERT INTO `t_menu` VALUES (161, '批量删除', 3, 110, NULL, NULL, NULL, 1, 'support:dict:delete', 'support:dict:delete', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:24:34', 1, '2023-10-07 18:19:39');
INSERT INTO `t_menu` VALUES (162, '刷新缓存', 3, 110, NULL, NULL, NULL, 1, 'support:dict:refresh', 'support:dict:refresh', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:24:55', 1, '2023-10-07 18:18:37');
INSERT INTO `t_menu` VALUES (163, '新建', 3, 109, NULL, NULL, NULL, 1, 'support:config:add', 'support:config:add', NULL, 109, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:26:56', 1, '2023-10-07 18:16:17');
INSERT INTO `t_menu` VALUES (164, '编辑', 3, 109, NULL, NULL, NULL, 1, 'support:config:update', 'support:config:update', NULL, 109, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:27:07', 1, '2023-10-07 18:16:24');
INSERT INTO `t_menu` VALUES (168, '查询', 3, 147, 11, NULL, NULL, 1, 'support:helpDoc:query', 'support:helpDoc:query', NULL, 147, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:12:13', 1, '2023-10-07 14:05:49');
INSERT INTO `t_menu` VALUES (169, '新建', 3, 147, 12, NULL, NULL, 1, 'support:helpDoc:add', 'support:helpDoc:add', NULL, 147, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:12:37', 1, '2023-10-07 14:05:56');
INSERT INTO `t_menu` VALUES (170, '新建目录', 3, 147, 1, NULL, NULL, 1, 'support:helpDocCatalog:addCategory', 'support:helpDocCatalog:addCategory', NULL, 147, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:12:57', 1, '2023-10-07 14:06:38');
INSERT INTO `t_menu` VALUES (171, '修改目录', 3, 147, 2, NULL, NULL, 1, 'support:helpDocCatalog:update', 'support:helpDocCatalog:update', NULL, 147, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:13:46', 1, '2023-10-07 14:06:49');
INSERT INTO `t_menu` VALUES (181, '查询', 3, 144, NULL, NULL, NULL, 1, 'oa:enterprise:query', 'oa:enterprise:query', NULL, 144, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:25:14', 1, '2023-10-07 12:00:09');
INSERT INTO `t_menu` VALUES (182, '新建', 3, 144, NULL, NULL, NULL, 1, 'oa:enterprise:add', 'oa:enterprise:add', NULL, 144, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:25:25', 1, '2023-10-07 12:00:17');
INSERT INTO `t_menu` VALUES (183, '编辑', 3, 144, NULL, NULL, NULL, 1, 'oa:enterprise:update', 'oa:enterprise:update', NULL, 144, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:25:36', 1, '2023-10-07 12:00:38');
INSERT INTO `t_menu` VALUES (184, '删除', 3, 144, NULL, NULL, NULL, 1, 'oa:enterprise:delete', 'oa:enterprise:delete', NULL, 144, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:25:53', 1, '2023-10-07 12:00:46');
INSERT INTO `t_menu` VALUES (185, '查询', 3, 132, NULL, NULL, NULL, 1, 'oa:notice:query', 'oa:notice:query', NULL, 132, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:26:38', 1, '2023-10-07 11:43:01');
INSERT INTO `t_menu` VALUES (186, '新建', 3, 132, NULL, NULL, NULL, 1, 'oa:notice:add', 'oa:notice:add', NULL, 132, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:27:04', 1, '2023-10-07 11:43:07');
INSERT INTO `t_menu` VALUES (187, '编辑', 3, 132, NULL, NULL, NULL, 1, 'oa:notice:update', 'oa:notice:update', NULL, 132, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:27:15', 1, '2023-10-07 11:43:12');
INSERT INTO `t_menu` VALUES (188, '删除', 3, 132, NULL, NULL, NULL, 1, 'oa:notice:delete', 'oa:notice:delete', NULL, 132, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:27:23', 1, '2023-10-07 11:43:18');
INSERT INTO `t_menu` VALUES (190, '查询', 3, 152, NULL, NULL, NULL, 1, '', 'support:changeLog:query', NULL, 152, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:28:33', 1, '2023-10-07 14:25:05');
INSERT INTO `t_menu` VALUES (191, '新建', 3, 152, NULL, NULL, NULL, 1, 'support:changeLog:add', 'support:changeLog:add', NULL, 152, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:28:46', 1, '2023-10-07 14:24:15');
INSERT INTO `t_menu` VALUES (192, '批量删除', 3, 152, NULL, NULL, NULL, 1, 'support:changeLog:batchDelete', 'support:changeLog:batchDelete', NULL, 152, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:29:10', 1, '2023-10-07 14:24:22');
INSERT INTO `t_menu` VALUES (193, '文件管理', 2, 50, 20, '/support/file/file-list', '/support/file/file-list.vue', NULL, NULL, NULL, 'FolderOpenOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 11:26:11', 1, '2022-10-22 11:29:22');
INSERT INTO `t_menu` VALUES (196, '查看详情', 3, 145, NULL, NULL, NULL, 1, 'oa:enterprise:detail', 'oa:enterprise:detail', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 20:16:47', 1, '2023-10-07 11:48:59');
INSERT INTO `t_menu` VALUES (198, '删除', 3, 152, NULL, NULL, NULL, 1, 'support:changeLog:delete', 'support:changeLog:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 20:42:34', 1, '2023-10-07 14:24:32');
INSERT INTO `t_menu` VALUES (199, '查询', 3, 109, NULL, NULL, NULL, 1, 'support:config:query', 'support:config:query', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 20:45:14', 1, '2023-10-07 18:16:27');
INSERT INTO `t_menu` VALUES (200, '查询', 3, 193, NULL, NULL, NULL, 1, 'support:file:query', 'support:file:query', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 20:47:23', 1, '2023-10-07 18:24:43');
INSERT INTO `t_menu` VALUES (201, '删除', 3, 147, 14, NULL, NULL, 1, 'support:helpDoc:delete', 'support:helpDoc:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 21:03:20', 1, '2023-10-07 14:07:02');
INSERT INTO `t_menu` VALUES (202, '更新', 3, 147, 13, NULL, NULL, 1, 'support:helpDoc:update', 'support:helpDoc:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 21:03:32', 1, '2023-10-07 14:06:56');
INSERT INTO `t_menu` VALUES (203, '查询', 3, 143, NULL, NULL, NULL, 1, 'support:loginLog:query', 'support:loginLog:query', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 21:05:11', 1, '2023-10-07 14:27:23');
INSERT INTO `t_menu` VALUES (204, '查询', 3, 81, NULL, NULL, NULL, 1, 'support:operateLog:query', 'support:operateLog:query', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-22 10:33:31', 1, '2023-10-07 14:27:56');
INSERT INTO `t_menu` VALUES (205, '详情', 3, 81, NULL, NULL, NULL, 1, 'support:operateLog:detail', 'support:operateLog:detail', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-22 10:33:49', 1, '2023-10-07 14:28:04');
INSERT INTO `t_menu` VALUES (206, '心跳监控', 2, 111, 1, '/support/heart-beat/heart-beat-list', '/support/heart-beat/heart-beat-list.vue', 1, NULL, NULL, 'FallOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-22 10:47:03', 1, '2022-10-22 18:32:52');
INSERT INTO `t_menu` VALUES (207, '更新', 3, 152, NULL, NULL, NULL, 1, 'support:changeLog:update', 'support:changeLog:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-22 11:51:32', 1, '2023-10-07 14:24:39');
INSERT INTO `t_menu` VALUES (212, '查询', 3, 117, NULL, NULL, NULL, 1, 'support:reload:query', 'support:reload:query', NULL, NULL, 0, NULL, 1, 1, 1, 0, 1, '2023-10-07 14:31:36', NULL, '2023-10-07 14:31:36');
INSERT INTO `t_menu` VALUES (213, '网络安全', 1, 0, 5, NULL, NULL, 1, NULL, NULL, 'SafetyCertificateOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2023-10-17 19:03:08', 1, '2023-12-01 19:38:00');
INSERT INTO `t_menu` VALUES (214, '登录锁定', 2, 213, 1, '/support/login-fail', '/support/login-fail/login-fail-list.vue', 1, NULL, NULL, 'LockOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2023-10-17 19:04:24', NULL, '2023-10-17 19:04:24');
INSERT INTO `t_menu` VALUES (215, '接口加解密', 2, 213, 3, '/support/api-encrypt', '/support/api-encrypt/api-encrypt-index.vue', 1, NULL, NULL, 'CodepenCircleOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2023-10-24 11:49:28', 1, '2023-10-25 16:28:18');
INSERT INTO `t_menu` VALUES (218, '文档中心', 1, 0, 4, NULL, NULL, 1, NULL, NULL, 'FileSearchOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2023-12-01 19:37:28', 1, '2023-12-01 19:37:51');

-- ----------------------------
-- 进销存系统数据
-- ----------------------------
INSERT INTO `t_menu` VALUES (137, '进销存系统', 1, 0, 2, NULL, NULL, NULL, NULL, NULL, 'AccountBookOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-24 20:07:20', 1, '2022-10-22 18:30:19');
INSERT INTO `t_menu` VALUES (48, '商品管理', 1, 137, 10, '/goods', NULL, NULL, NULL, NULL, 'BarcodeOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2021-08-12 18:02:59', 1, '2022-06-24 20:07:35');
INSERT INTO `t_menu` VALUES (47, '商品管理', 2, 48, 1, '/erp/goods/list', '/business/erp/goods/goods-list.vue', NULL, NULL, NULL, 'AliwangwangOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2021-08-12 17:58:39', 1, '2023-12-01 19:33:08');
INSERT INTO `t_menu` VALUES (78, '商品分类', 2, 48, 2, '/erp/catalog/goods', '/business/erp/catalog/goods-catalog.vue', NULL, NULL, NULL, 'ApartmentOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2022-05-18 23:34:14', 1, '2023-12-01 19:33:13');
INSERT INTO `t_menu` VALUES (79, '自定义分组', 2, 48, 3, '/erp/catalog/custom', '/business/erp/catalog/custom-catalog.vue', NULL, NULL, NULL, 'AppstoreAddOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-18 23:37:53', 1, '2023-12-01 19:33:16');
INSERT INTO `t_menu` VALUES (165, '查询', 3, 47, NULL, NULL, NULL, 1, 'goods:query', 'goods:query', NULL, 47, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 19:55:39', 1, '2023-10-07 13:58:28');
INSERT INTO `t_menu` VALUES (166, '新建', 3, 47, NULL, NULL, NULL, 1, 'goods:add', 'goods:add', NULL, 47, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 19:56:00', 1, '2023-10-07 13:58:32');
INSERT INTO `t_menu` VALUES (167, '批量删除', 3, 47, NULL, NULL, NULL, 1, 'goods:batchDelete', 'goods:batchDelete', NULL, 47, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 19:56:15', 1, '2023-10-07 13:58:35');
INSERT INTO `t_menu` VALUES (194, '删除', 3, 47, NULL, NULL, NULL, 1, 'goods:delete', 'goods:delete', NULL, 47, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 20:00:12', 1, '2023-10-07 13:58:39');
INSERT INTO `t_menu` VALUES (195, '修改', 3, 47, NULL, NULL, NULL, 1, 'goods:update', 'goods:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 20:05:23', 1, '2023-10-07 13:58:42');
INSERT INTO `t_menu` VALUES (216, '导出', 3, 47, NULL, NULL, NULL, 1, 'goods:exportGoods', 'goods:exportGoods', NULL, NULL, 0, NULL, 1, 1, 0, 0, 1, '2023-12-01 19:34:03', NULL, '2023-12-01 19:34:03');
INSERT INTO `t_menu` VALUES (217, '导入', 3, 47, 3, NULL, NULL, 1, 'goods:importGoods', 'goods:importGoods', NULL, NULL, 0, NULL, 1, 1, 0, 0, 1, '2023-12-01 19:34:22', NULL, '2023-12-01 19:34:22');
INSERT INTO `t_menu` VALUES (173, '新建', 3, 78, NULL, NULL, NULL, 1, 'category:add', 'category:add', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:02', 1, '2023-10-07 13:54:01');
INSERT INTO `t_menu` VALUES (174, '查询', 3, 78, NULL, NULL, NULL, 1, 'category:tree', 'category:tree', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:22', 1, '2023-10-07 13:54:33');
INSERT INTO `t_menu` VALUES (175, '编辑', 3, 78, NULL, NULL, NULL, 1, 'category:update', 'category:update', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:38', 1, '2023-10-07 13:54:18');
INSERT INTO `t_menu` VALUES (176, '删除', 3, 78, NULL, NULL, NULL, 1, 'category:delete', 'category:delete', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:50', 1, '2023-10-07 13:54:27');
INSERT INTO `t_menu` VALUES (177, '新建', 3, 79, NULL, NULL, NULL, 1, 'category:add', 'custom:category:add', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:02', 1, '2023-10-07 13:57:32');
INSERT INTO `t_menu` VALUES (178, '查询', 3, 79, NULL, NULL, NULL, 1, 'category:tree', 'custom:category:tree', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:22', 1, '2023-10-07 13:57:50');
INSERT INTO `t_menu` VALUES (179, '编辑', 3, 79, NULL, NULL, NULL, 1, 'category:update', 'custom:category:update', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:38', 1, '2023-10-07 13:58:02');
INSERT INTO `t_menu` VALUES (180, '删除', 3, 79, NULL, NULL, NULL, 1, 'category:delete', 'custom:category:delete', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:50', 1, '2023-10-07 13:58:12');


-- ----------------------------
-- Table structure for t_notice
-- ----------------------------
DROP TABLE IF EXISTS `t_notice`;
CREATE TABLE `t_notice`  (
  `notice_id` bigint(0) NOT NULL AUTO_INCREMENT,
  `notice_type_id` bigint(0) NOT NULL COMMENT '类型1公告 2动态',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `all_visible_flag` tinyint(1) NOT NULL COMMENT '是否全部可见',
  `scheduled_publish_flag` tinyint(1) NOT NULL COMMENT '是否定时发布',
  `publish_time` datetime(0) NOT NULL COMMENT '发布时间',
  `content_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文本内容',
  `content_html` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'html内容',
  `attachment` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '附件',
  `page_view_count` int(0) NOT NULL DEFAULT 0 COMMENT '页面浏览量，传说中的pv',
  `user_view_count` int(0) NOT NULL DEFAULT 0 COMMENT '用户浏览量，传说中的uv',
  `source` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源',
  `author` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作者',
  `document_number` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文号，如：1024创新实验室发〔2022〕字第36号',
  `deleted_flag` tinyint(1) NOT NULL DEFAULT 0,
  `create_user_id` bigint(0) NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 63 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_notice
-- ----------------------------

-- ----------------------------
-- Table structure for t_notice_type
-- ----------------------------
DROP TABLE IF EXISTS `t_notice_type`;
CREATE TABLE `t_notice_type`  (
  `notice_type_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '通知类型',
  `notice_type_name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型名称',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`notice_type_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知类型' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_notice_type
-- ----------------------------
INSERT INTO `t_notice_type` VALUES (1, '公告', '2022-08-16 20:29:15', '2022-08-16 20:29:15');
INSERT INTO `t_notice_type` VALUES (2, '通知', '2022-08-16 20:29:20', '2022-08-16 20:29:20');

-- ----------------------------
-- Table structure for t_notice_view_record
-- ----------------------------
DROP TABLE IF EXISTS `t_notice_view_record`;
CREATE TABLE `t_notice_view_record`  (
  `notice_id` bigint(0) NOT NULL COMMENT '通知公告id',
  `employee_id` bigint(0) NOT NULL COMMENT '员工id',
  `page_view_count` int(0) NULL DEFAULT 0 COMMENT '查看次数',
  `first_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '首次ip',
  `first_user_agent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '首次用户设备等标识',
  `last_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后一次ip',
  `last_user_agent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后一次用户设备等标识',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`notice_id`, `employee_id`) USING BTREE,
  UNIQUE INDEX `uk_notice_employee`(`notice_id`, `employee_id`) USING BTREE COMMENT '资讯员工'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知查看记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_notice_view_record
-- ----------------------------

-- ----------------------------
-- Table structure for t_notice_visible_range
-- ----------------------------
DROP TABLE IF EXISTS `t_notice_visible_range`;
CREATE TABLE `t_notice_visible_range`  (
  `notice_id` bigint(0) NOT NULL COMMENT '资讯id',
  `data_type` tinyint(0) NOT NULL COMMENT '数据类型1员工 2部门',
  `data_id` bigint(0) NOT NULL COMMENT '员工or部门id',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE INDEX `uk_notice_data`(`notice_id`, `data_type`, `data_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知可见范围' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_oa_bank
-- ----------------------------
DROP TABLE IF EXISTS `t_oa_bank`;
CREATE TABLE `t_oa_bank`  (
  `bank_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '银行信息ID',
  `bank_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '开户银行',
  `account_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账户名称',
  `account_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `business_flag` tinyint(1) NOT NULL COMMENT '是否对公',
  `enterprise_id` bigint(0) NOT NULL COMMENT '企业ID',
  `disabled_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '禁用状态',
  `deleted_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态',
  `create_user_id` bigint(0) NOT NULL COMMENT '创建人ID',
  `create_user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`bank_id`) USING BTREE,
  INDEX `idx_enterprise_id`(`enterprise_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'OA银行信息\n' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_oa_bank
-- ----------------------------

-- ----------------------------
-- Table structure for t_oa_enterprise
-- ----------------------------
DROP TABLE IF EXISTS `t_oa_enterprise`;
CREATE TABLE `t_oa_enterprise`  (
  `enterprise_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '企业ID',
  `enterprise_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '企业名称',
  `enterprise_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '企业logo',
  `type` int(0) NOT NULL DEFAULT 1 COMMENT '类型（1:有限公司;2:合伙公司）',
  `unified_social_credit_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '统一社会信用代码',
  `contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '联系人',
  `contact_phone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '联系人电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `province` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '省份',
  `province_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '省份名称',
  `city` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '市',
  `city_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '城市名称',
  `district` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区县',
  `district_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区县名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '详细地址',
  `business_license` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '营业执照',
  `disabled_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '禁用状态',
  `deleted_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态',
  `create_user_id` bigint(0) NOT NULL COMMENT '创建人ID',
  `create_user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`enterprise_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 120 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'OA企业模块\r\n' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_oa_enterprise
-- ----------------------------
INSERT INTO `t_oa_enterprise` VALUES (1, '1024创新区块链实验室', 'public/common/f4a76fa720814949a610f05f6f9545bf_20221022170256_jpg', 1, '1024lab_block', '开云', '18637925892', NULL, '410000', '河南省', '410300', '洛阳市', '410311', '洛龙区', '区块链大楼', 'public/common/852b7e19bef94af39c1a6156edf47cfb_20221022170332_jpg', 0, 0, 1, '管理员', '2021-10-22 17:03:35', '2022-10-22 17:04:18');

-- ----------------------------
-- Table structure for t_oa_enterprise_employee
-- ----------------------------
DROP TABLE IF EXISTS `t_oa_enterprise_employee`;
CREATE TABLE `t_oa_enterprise_employee`  (
  `enterprise_employee_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `enterprise_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单ID',
  `employee_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '货物名称',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`enterprise_employee_id`) USING BTREE,
  UNIQUE INDEX `uk_enterprise_employee`(`enterprise_id`, `employee_id`) USING BTREE,
  INDEX `idx_employee_id`(`employee_id`) USING BTREE,
  INDEX `idx_enterprise_id`(`enterprise_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 159 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '企业关联的员工' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_oa_enterprise_employee
-- ----------------------------

-- ----------------------------
-- Table structure for t_oa_invoice
-- ----------------------------
DROP TABLE IF EXISTS `t_oa_invoice`;
CREATE TABLE `t_oa_invoice`  (
  `invoice_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '发票信息ID',
  `invoice_heads` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '开票抬头',
  `taxpayer_identification_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '纳税人识别号',
  `account_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '银行账户',
  `bank_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '开户行',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `enterprise_id` bigint(0) NOT NULL COMMENT '企业ID',
  `disabled_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '禁用状态',
  `deleted_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态',
  `create_user_id` bigint(0) NOT NULL COMMENT '创建人ID',
  `create_user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`invoice_id`) USING BTREE,
  INDEX `idx_enterprise_id`(`enterprise_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'OA发票信息\n' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_oa_invoice
-- ----------------------------

-- ----------------------------
-- Table structure for t_operate_log
-- ----------------------------
DROP TABLE IF EXISTS `t_operate_log`;
CREATE TABLE `t_operate_log`  (
  `operate_log_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `operate_user_id` bigint(0) NOT NULL COMMENT '用户id',
  `operate_user_type` int(0) NOT NULL COMMENT '用户类型',
  `operate_user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名称',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作模块',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作内容',
  `url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求路径',
  `method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求方法',
  `param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '请求参数',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求ip',
  `ip_region` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求ip地区',
  `user_agent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '请求user-agent',
  `success_flag` tinyint(0) NULL DEFAULT NULL COMMENT '请求结果 0失败 1成功',
  `fail_reason` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '失败原因',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`operate_log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 654 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_reload_item
-- ----------------------------
DROP TABLE IF EXISTS `t_reload_item`;
CREATE TABLE `t_reload_item`  (
  `tag` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '项名称',
  `args` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '参数 可选',
  `identification` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '运行标识',
  `update_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`tag`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'reload项目' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_reload_item
-- ----------------------------

-- ----------------------------
-- Table structure for t_reload_result
-- ----------------------------
DROP TABLE IF EXISTS `t_reload_result`;
CREATE TABLE `t_reload_result`  (
  `tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `identification` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '运行标识',
  `args` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `result` tinyint unsigned NOT NULL COMMENT '是否成功 ',
  `exception` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'reload结果' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role`  (
  `role_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色编码',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色描述',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE INDEX `role_code_uni`(`role_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO `t_role` VALUES (1, '技术总监', NULL, '', '2022-10-19 20:24:09', '2019-06-21 12:09:34');
INSERT INTO `t_role` VALUES (35, '总经理', NULL, '', '2019-08-30 09:31:05', '2019-08-30 09:31:05');
INSERT INTO `t_role` VALUES (36, '董事长', NULL, '', '2019-08-30 09:31:11', '2019-08-30 09:31:11');

-- ----------------------------
-- Table structure for t_role_data_scope
-- ----------------------------
DROP TABLE IF EXISTS `t_role_data_scope`;
CREATE TABLE `t_role_data_scope`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `data_scope_type` int(0) NOT NULL COMMENT '数据范围id',
  `view_type` int(0) NOT NULL COMMENT '数据范围类型',
  `role_id` bigint(0) NOT NULL COMMENT '角色id',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 67 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色的数据范围' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_role_employee
-- ----------------------------
DROP TABLE IF EXISTS `t_role_employee`;
CREATE TABLE `t_role_employee`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(0) NOT NULL COMMENT '角色id',
  `employee_id` bigint(0) NOT NULL COMMENT '员工id',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_employee`(`role_id`, `employee_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 335 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色员工功能表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_role_employee
-- ----------------------------

-- ----------------------------
-- Table structure for t_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_role_menu`;
CREATE TABLE `t_role_menu`  (
  `role_menu_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `role_id` bigint(0) NOT NULL COMMENT '角色id',
  `menu_id` bigint(0) NOT NULL COMMENT '菜单id',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`role_menu_id`) USING BTREE,
  INDEX `idx_role_id`(`role_id`) USING BTREE,
  INDEX `idx_menu_id`(`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 76 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色-菜单\n' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_role_menu
-- ----------------------------
INSERT INTO `t_role_menu` VALUES (50, 1, 138, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (51, 1, 132, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (52, 1, 142, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (53, 1, 149, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (54, 1, 150, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (55, 1, 185, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (56, 1, 186, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (57, 1, 187, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (58, 1, 188, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (59, 1, 145, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (60, 1, 196, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (61, 1, 144, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (62, 1, 181, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (63, 1, 183, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (64, 1, 184, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (65, 1, 165, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (69, 1, 166, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (70, 1, 194, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (72, 1, 173, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (73, 1, 174, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (74, 1, 175, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (75, 1, 176, '2023-12-04 10:02:29', '2023-12-04 10:02:29');

-- ----------------------------
-- 进销存
-- ----------------------------
INSERT INTO `t_role_menu` VALUES (68, 1, 137, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (66, 1, 47, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (67, 1, 48, '2023-12-04 10:02:29', '2023-12-04 10:02:29');
INSERT INTO `t_role_menu` VALUES (71, 1, 78, '2023-12-04 10:02:29', '2023-12-04 10:02:29');

-- ----------------------------
-- Table structure for t_serial_number
-- ----------------------------
DROP TABLE IF EXISTS `t_serial_number`;
CREATE TABLE `t_serial_number`  (
  `serial_number_id` int(0) NOT NULL,
  `business_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务名称',
  `format` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '格式[yyyy]表示年,[mm]标识月,[dd]表示日,[nnn]表示三位数字',
  `rule_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '规则格式。none没有周期, year 年周期, month月周期, day日周期',
  `init_number` int unsigned NOT NULL COMMENT '初始值',
  `step_random_range` int unsigned NOT NULL COMMENT '步长随机数',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `last_number` bigint(0) NULL DEFAULT NULL COMMENT '上次产生的单号, 默认为空',
  `last_time` datetime(0) NULL DEFAULT NULL COMMENT '上次产生的单号时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`serial_number_id`) USING BTREE,
  UNIQUE INDEX `key_name`(`business_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '单号生成器定义表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_serial_number
-- ----------------------------
INSERT INTO `t_serial_number` VALUES (1, '订单编号', 'DK[yyyy][mm][dd]NO[nnnnn]', 'day', 1000, 10, 'DK20201101NO321', 1, '2023-12-04 09:16:42', '2024-01-08 19:24:46', '2021-02-19 14:37:50');
INSERT INTO `t_serial_number` VALUES (2, '合同编号', 'HT[yyyy][mm][dd][nnnnn]-CX', 'none', 1, 1, '', 8, '2023-12-04 09:54:53', '2023-12-04 09:54:52', '2021-08-12 20:40:37');

-- ----------------------------
-- Table structure for t_serial_number_record
-- ----------------------------
DROP TABLE IF EXISTS `t_serial_number_record`;
CREATE TABLE `t_serial_number_record`  (
  `serial_number_id` int(0) NOT NULL,
  `record_date` date NOT NULL COMMENT '记录日期',
  `last_number` bigint(0) NOT NULL DEFAULT 0 COMMENT '最后更新值',
  `last_time` datetime(0) NOT NULL COMMENT '最后更新时间',
  `count` bigint(0) NOT NULL DEFAULT 0 COMMENT '更新次数',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX `uk_generator`(`serial_number_id`, `record_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'serial_number记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_serial_number_record
-- ----------------------------

-- ----------------------------
-- Table structure for t_table_column
-- ----------------------------
DROP TABLE IF EXISTS `t_table_column`;
CREATE TABLE `t_table_column`  (
  `table_column_id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL COMMENT '用户id',
  `user_type` int(0) NOT NULL COMMENT '用户类型',
  `table_id` int(0) NOT NULL COMMENT '表格id',
  `columns` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '具体的表格列，存入的json',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`table_column_id`) USING BTREE,
  UNIQUE INDEX `uni_employee_table`(`user_id`, `table_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '表格的自定义列存储' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_employee_otp
-- ----------------------------
DROP TABLE IF EXISTS `t_employee_otp`;
CREATE TABLE `t_employee_otp`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` bigint(0) NOT NULL COMMENT '员工ID',
  `secret_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '秘钥',
  `bound_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否绑定 0否 1是',
  `enabled_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用 0否1是',
  `deleted_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除 0否 1是',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_employee_id`(`employee_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工多因子信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_payment_channel_info
-- ----------------------------
DROP TABLE IF EXISTS `t_payment_channel_info`;
CREATE TABLE `t_payment_channel_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `mer_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商户名称',
  `mer_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商户编码',
  `mer_ak` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商户号',
  `mer_sk` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商户秘钥',
  `domain` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '域名',
  `ext` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '额外配置信息',
  `mer_username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商户持有人姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '绑定手机号',
  `id_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '证件号',
  `payment_flag` int(0) NULL DEFAULT NULL COMMENT '支付标志',
  `payment_scale` int(0) NULL DEFAULT NULL COMMENT '支付比例',
  `payment_limit` decimal(15, 2) NULL DEFAULT NULL COMMENT '支付限额',
  `payment_count` int(0) NULL DEFAULT NULL COMMENT '支付笔数限制',
  `cash_flag` int(0) NULL DEFAULT NULL COMMENT '兑付标志',
  `cash_scale` int(0) NULL DEFAULT NULL COMMENT '兑付比例',
  `cash_limit` decimal(15, 2) NULL DEFAULT NULL COMMENT '兑付限额',
  `cash_count` int(0) NULL DEFAULT NULL COMMENT '兑付笔数限制',
  `work_time` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工作时间',
  `interface_type` int(0) NULL DEFAULT NULL COMMENT '接口类型 1-API方式，2-非API方式',
  `impl_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '实现类关联值',
  `xj_flag` int(0) NULL DEFAULT NULL COMMENT '是否支持新疆用户 0-不支持 1-支持',
  `status` int(0) NULL DEFAULT 1 COMMENT '状态，0-禁用；1-启用',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_mer_code`(`mer_code`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '渠道基本信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_payment_order_info
-- ----------------------------
DROP TABLE IF EXISTS `t_payment_order_info`;
CREATE TABLE `t_payment_order_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `platform_id` bigint(0) NULL DEFAULT NULL COMMENT '商户ID',
  `channel_id` bigint(0) NULL DEFAULT NULL COMMENT '渠道商户Id',
  `business_id` bigint(0) NULL DEFAULT NULL COMMENT '业务范围Id',
  `order_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单编号',
  `deposit_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '银行类型，bank/qrcode',
  `payment_channel` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '二维码渠道',
  `amount` decimal(15, 2) NOT NULL COMMENT '存款金额',
  `account_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '银行账户',
  `account_name_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账户名哈希',
  `bank_account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '银行卡号码',
  `bank_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '银行代码',
  `deposit_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '存款备注',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '用户ID',
  `callback` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单确认通知回调地址',
  `landing_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '前端回调地址',
  `apply_time` bigint(0) NULL DEFAULT NULL COMMENT '申请时间',
  `expired_time` bigint(0) NULL DEFAULT NULL COMMENT '订单过期时间',
  `finished_time` bigint(0) NULL DEFAULT NULL COMMENT '订单完成时间',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `client_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户IP地址',
  `device` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户设备类型',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电子邮箱',
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '币种',
  `country` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国家',
  `ext` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '额外参数',
  `qrcode_id` bigint(0) NULL DEFAULT NULL COMMENT '二维码文件id',
  `thirdparty_order_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方订单编号',
  `sub_mer_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子商户名称',
  `payment_status` int(0) NULL DEFAULT NULL COMMENT '支付状态 1 待付款 2 已付款 3 已取消 4 过期',
  `status` int(0) NULL DEFAULT NULL COMMENT '订单状态 1 待确认 2 已确认 3 挂起',
  `bill_file_id` bigint(0) NULL DEFAULT NULL COMMENT '支付单据文件id',
  `retry` int(0) NULL DEFAULT NULL COMMENT '查询订单确认状态重试次数',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_no`(`order_no`) USING BTREE,
  INDEX `idx_platform_id`(`platform_id`) USING BTREE,
  INDEX `idx_channel_id`(`channel_id`) USING BTREE,
  INDEX `idx_payment_status`(`payment_status`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_account_name_hash`(`account_name_hash`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '支付订单信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_i18n_language
-- ----------------------------
DROP TABLE IF EXISTS `t_i18n_language`;
CREATE TABLE `t_i18n_language`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `language_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '语言代码',
  `language_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '语言名称',
  `native_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '本地语言名称',
  `flag_emoji` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国旗表情符号',
  `is_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否默认语言',
  `sort_order` int(0) NULL DEFAULT 0 COMMENT '排序',
  `status` int(0) NOT NULL DEFAULT 1 COMMENT '状态(0-禁用，1-启用)',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_language_code`(`language_code`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_sort_order`(`sort_order`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '多语言配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_i18n_message
-- ----------------------------
DROP TABLE IF EXISTS `t_i18n_message`;
CREATE TABLE `t_i18n_message`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息键',
  `language_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '语言代码',
  `message_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息值',
  `category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类',
  `module` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模块',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_key_language`(`message_key`, `language_code`) USING BTREE,
  INDEX `idx_language_code`(`language_code`) USING BTREE,
  INDEX `idx_category`(`category`) USING BTREE,
  INDEX `idx_module`(`module`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '多语言消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_i18n_language
-- ----------------------------
INSERT INTO `t_i18n_language` VALUES (1, 'en_US', 'English', 'English', '🇺🇸', 1, 1, 1, '2024-09-29 16:00:00', '2024-09-29 16:00:00');
INSERT INTO `t_i18n_language` VALUES (2, 'zh_CN', 'Chinese (Simplified)', '简体中文', '🇨🇳', 0, 2, 1, '2024-09-29 16:00:00', '2024-09-29 16:00:00');
INSERT INTO `t_i18n_language` VALUES (3, 'fr_FR', 'French', 'Français', '🇫🇷', 0, 3, 1, '2024-09-29 16:00:00', '2024-09-29 16:00:00');
INSERT INTO `t_i18n_language` VALUES (4, 'es_ES', 'Spanish', 'Español', '🇪🇸', 0, 4, 1, '2024-09-29 16:00:00', '2024-09-29 16:00:00');
INSERT INTO `t_i18n_language` VALUES (5, 'pt_PT', 'Portuguese', 'Português', '🇵🇹', 0, 5, 1, '2024-09-29 16:00:00', '2024-09-29 16:00:00');

-- ----------------------------
-- Records of t_i18n_message
-- ----------------------------
INSERT INTO `t_i18n_message` VALUES (1, 'common.login', 'en_US', 'Login', 'common', 'system', '2024-09-29 16:00:00', '2024-09-29 16:00:00');
INSERT INTO `t_i18n_message` VALUES (2, 'common.logout', 'en_US', 'Logout', 'common', 'system', '2024-09-29 16:00:00', '2024-09-29 16:00:00');
INSERT INTO `t_i18n_message` VALUES (3, 'common.submit', 'en_US', 'Submit', 'common', 'system', '2024-09-29 16:00:00', '2024-09-29 16:00:00');
INSERT INTO `t_i18n_message` VALUES (4, 'common.cancel', 'en_US', 'Cancel', 'common', 'system', '2024-09-29 16:00:00', '2024-09-29 16:00:00');
INSERT INTO `t_i18n_message` VALUES (5, 'payment.order.title', 'en_US', 'Payment Order Management', 'payment', 'business', '2024-09-29 16:00:00', '2024-09-29 16:00:00');
INSERT INTO `t_i18n_message` VALUES (6, 'payment.channel.title', 'en_US', 'Payment Channel Management', 'payment', 'business', '2024-09-29 16:00:00', '2024-09-29 16:00:00');
INSERT INTO `t_i18n_message` VALUES (7, 'status.enable', 'en_US', 'Enable', 'status', 'system', '2024-09-29 16:00:00', '2024-09-29 16:00:00');
INSERT INTO `t_i18n_message` VALUES (8, 'status.disable', 'en_US', 'Disable', 'status', 'system', '2024-09-29 16:00:00', '2024-09-29 16:00:00');
INSERT INTO `t_i18n_message` VALUES (9, 'common.login', 'zh_CN', '登录', 'common', 'system', '2024-09-29 16:00:00', '2024-09-29 16:00:00');
INSERT INTO `t_i18n_message` VALUES (10, 'common.logout', 'zh_CN', '退出', 'common', 'system', '2024-09-29 16:00:00', '2024-09-29 16:00:00');
INSERT INTO `t_i18n_message` VALUES (11, 'common.submit', 'zh_CN', '提交', 'common', 'system', '2024-09-29 16:00:00', '2024-09-29 16:00:00');
INSERT INTO `t_i18n_message` VALUES (12, 'common.cancel', 'zh_CN', '取消', 'common', 'system', '2024-09-29 16:00:00', '2024-09-29 16:00:00');
INSERT INTO `t_i18n_message` VALUES (13, 'payment.order.title', 'zh_CN', '支付订单管理', 'payment', 'business', '2024-09-29 16:00:00', '2024-09-29 16:00:00');
INSERT INTO `t_i18n_message` VALUES (14, 'payment.channel.title', 'zh_CN', '支付渠道管理', 'payment', 'business', '2024-09-29 16:00:00', '2024-09-29 16:00:00');
INSERT INTO `t_i18n_message` VALUES (15, 'status.enable', 'zh_CN', '启用', 'status', 'system', '2024-09-29 16:00:00', '2024-09-29 16:00:00');
INSERT INTO `t_i18n_message` VALUES (16, 'status.disable', 'zh_CN', '禁用', 'status', 'system', '2024-09-29 16:00:00', '2024-09-29 16:00:00');

-- ----------------------------
-- Table structure for t_cash_order_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_cash_order_detail`;
CREATE TABLE `t_cash_order_detail`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `platform_id` bigint(0) NULL DEFAULT NULL COMMENT '平台ID',
  `order_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单编号',
  `batch_no` int(0) NULL DEFAULT NULL COMMENT '处理批次',
  `apply_time` bigint(0) NULL DEFAULT NULL COMMENT '批次申请时间',
  `seq_no` int(0) NULL DEFAULT NULL COMMENT '拆分序号',
  `sub_order_no` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '出金子订单号：orderNo+batchNo+seqNo',
  `chnl_type` int(0) NULL DEFAULT NULL COMMENT '渠道类型 1-自有对接渠道、2-外部渠道',
  `channel_id` bigint(0) NULL DEFAULT NULL COMMENT '渠道商户Id',
  `amount` decimal(15, 2) NULL DEFAULT NULL COMMENT '出金金额',
  `amount_usdt` decimal(15, 2) NULL DEFAULT NULL COMMENT '出金金额（USDT）',
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '币种',
  `country` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国家',
  `account_holder` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '银行账户',
  `bank_account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '银行卡号码',
  `bank_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '银行代码',
  `bank_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收款银行名称',
  `bank_branch` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收款银行分行名称',
  `bank_province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收款银行所在省份',
  `bank_city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收款银行所在城市',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '出金备注',
  `callback` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单确认通知回调地址',
  `ext` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '额外参数',
  `status` int(0) NULL DEFAULT NULL COMMENT '交易状态 1-处理中 2-处理成功 3-处理失败',
  `errmsg` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '错误信息',
  `retry` int(0) NULL DEFAULT NULL COMMENT '查询订单确认状态重试次数',
  `thirdparty_order_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方订单编号',
  `bill_file_id` bigint(0) NULL DEFAULT NULL COMMENT '出金单据文件id',
  `finished_time` bigint(0) NULL DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_no`(`order_no`) USING BTREE,
  INDEX `idx_platform_id`(`platform_id`) USING BTREE,
  INDEX `idx_channel_id`(`channel_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '兑付订单拆分明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_cash_order_info
-- ----------------------------
DROP TABLE IF EXISTS `t_cash_order_info`;
CREATE TABLE `t_cash_order_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `platform_id` bigint(0) NULL DEFAULT NULL COMMENT '平台ID',
  `channel_id` bigint(0) NULL DEFAULT NULL COMMENT '渠道id',
  `order_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单编号',
  `withdraw_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '出金類型，bank',
  `withdraw_channel_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '限定出金渠道',
  `amount` decimal(15, 2) NULL DEFAULT NULL COMMENT '出金金额',
  `amount_usdt` decimal(15, 2) NULL DEFAULT NULL COMMENT '出金金额（USDT）',
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '币种',
  `country` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国家',
  `account_holder` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '银行账户名称',
  `bank_account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '银行卡号码',
  `bank_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '银行代码',
  `bank_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收款银行名称',
  `bank_branch` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收款银行分行名称',
  `bank_province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收款银行所在省份',
  `bank_city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收款银行所在城市',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '出金备注',
  `callback` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单确认通知回调地址',
  `user_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户编号',
  `client_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户IP地址',
  `device` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户设备类型',
  `ext` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '额外参数',
  `status` int(0) NULL DEFAULT NULL COMMENT '交易状态 1-待处理 2-处理中 3-已完成',
  `curr_batch_no` int(0) NULL DEFAULT NULL COMMENT '当前批次号',
  `manual_flag` int(0) NULL DEFAULT 0 COMMENT '人工处理标志：0-自动处理 1-人工处理',
  `manual_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '人工处理原因',
  `manual_split_flag` int(0) NULL DEFAULT 0 COMMENT '人工拆单标志 0-未拆单 1-拆单',
  `manual_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '人工处理备注',
  `apply_time` bigint(0) NULL DEFAULT NULL COMMENT '申请时间',
  `finished_time` bigint(0) NULL DEFAULT NULL COMMENT '订单完成时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_no`(`order_no`) USING BTREE,
  INDEX `idx_platform_id`(`platform_id`) USING BTREE,
  INDEX `idx_channel_id`(`channel_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '兑付订单信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_settle_bill
-- ----------------------------
DROP TABLE IF EXISTS `t_settle_bill`;
CREATE TABLE `t_settle_bill`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `channel_id` bigint(0) NULL DEFAULT NULL COMMENT '渠道商户Id',
  `bill_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '结算单号',
  `bill_date` date NULL DEFAULT NULL COMMENT '结算日期',
  `total_amount` decimal(15, 2) NULL DEFAULT NULL COMMENT '总金额',
  `fee_amount` decimal(15, 2) NULL DEFAULT NULL COMMENT '手续费金额',
  `settle_amount` decimal(15, 2) NULL DEFAULT NULL COMMENT '结算金额',
  `status` int(0) NULL DEFAULT NULL COMMENT '结算状态 1-待结算 2-已结算 3-结算失败',
  `settle_time` datetime(0) NULL DEFAULT NULL COMMENT '结算时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_channel_id`(`channel_id`) USING BTREE,
  INDEX `idx_bill_no`(`bill_no`) USING BTREE,
  INDEX `idx_bill_date`(`bill_date`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '结算单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_settle_order
-- ----------------------------
DROP TABLE IF EXISTS `t_settle_order`;
CREATE TABLE `t_settle_order`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `channel_id` bigint(0) NULL DEFAULT NULL COMMENT '渠道商户Id',
  `bill_id` bigint(0) NULL DEFAULT NULL COMMENT '结算单ID',
  `order_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单编号',
  `order_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单类型',
  `amount` decimal(15, 2) NULL DEFAULT NULL COMMENT '订单金额',
  `fee_amount` decimal(15, 2) NULL DEFAULT NULL COMMENT '手续费',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_channel_id`(`channel_id`) USING BTREE,
  INDEX `idx_bill_id`(`bill_id`) USING BTREE,
  INDEX `idx_order_no`(`order_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '结算单订单关联表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
