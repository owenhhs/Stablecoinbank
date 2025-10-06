# ZFX Payment Center System

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Vue](https://img.shields.io/badge/Vue-3.x-green.svg)](https://vuejs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7+-blue.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-orange.svg)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)](https://docs.docker.com/compose/)

## 🌟 项目简介

ZFX Payment Center System 是一个基于 Spring Boot + Vue 3 的现代化支付中心管理系统，提供完整的支付、结算、商户管理等核心功能。

### ✨ 主要特性

- 🏗️ **现代化架构**: Spring Boot 2.7+ + Vue 3 + Vite
- 🌍 **多语言支持**: 英文优先的国际化架构，支持中英文切换
- 🔐 **安全可靠**: JWT认证、RBAC权限控制、数据加密
- 📱 **响应式设计**: 支持桌面端和移动端访问
- 🐳 **容器化部署**: Docker + Docker Compose 一键部署
- 📊 **实时监控**: 系统监控、日志管理、性能分析
- 🔧 **开发友好**: 代码生成器、API文档、调试工具

## 🚀 快速开始

### 环境要求

- **JDK**: 8+ (推荐 OpenJDK 25)
- **Node.js**: 16+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Docker**: 20.0+ & Docker Compose

### 一键启动

```bash
# 1. 克隆项目
git clone <repository-url>
cd zfx

# 2. 启动所有服务
docker-compose up -d

# 3. 访问系统
# 前端: http://localhost:1881
# 后端API: http://localhost:1681
# 默认账号: admin / 123456
```

### 手动启动

```bash
# 1. 启动基础服务
docker-compose up -d mysql redis

# 2. 导入数据库
mysql -h localhost -u root -p < payment-center/smart_admin_v3.sql

# 3. 启动后端服务
cd payment-center/smart-admin-api
docker-compose up -d

# 4. 启动前端服务
cd payment-center/smart-admin-web
docker-compose up -d
```

## 📁 项目结构

```
zfx/
├── payment-center/                 # 支付中心主项目
│   ├── smart-admin-api/           # 后端API服务
│   │   ├── sa-admin/             # 管理后台模块
│   │   ├── sa-base/              # 基础模块
│   │   └── pom.xml               # Maven配置
│   ├── smart-admin-web/          # 前端Web应用
│   │   ├── src/                  # 源代码
│   │   │   ├── components/       # Vue组件
│   │   │   ├── views/           # 页面视图
│   │   │   ├── api/             # API接口
│   │   │   ├── i18n/            # 国际化配置
│   │   │   └── ...
│   │   ├── package.json          # Node.js依赖
│   │   └── vite.config.js        # Vite配置
│   └── smart_admin_v3.sql        # 数据库初始化脚本
├── docs/                          # 项目文档
│   ├── README.md                 # 详细文档
│   ├── API接口文档.md            # API文档
│   ├── 数据结构文档.md           # 数据库设计
│   └── 多语言扩展文档.md         # 国际化文档
├── docker-compose.yaml           # Docker编排配置
├── CHANGELOG.md                  # 变更日志
└── README.md                     # 项目说明
```

## 🌍 多语言支持

系统采用**英文优先**的国际化架构：

- **默认语言**: English (en_US)
- **支持语言**: 中文 (zh_CN)
- **语言切换**: 右上角语言选择器，实时切换
- **数据架构**: 所有数据库主键、菜单标识符使用英文

### 语言切换功能

- ✅ 菜单导航实时切换
- ✅ 页面内容多语言显示
- ✅ 用户界面完全响应
- ✅ 日期信息本地化
- ✅ 面包屑导航国际化

## 🏗️ 系统架构

### 技术栈

- **后端**: Spring Boot 2.7+, MyBatis Plus, MySQL 8.0+
- **前端**: Vue 3, Vite, Ant Design Vue, Vue Router
- **缓存**: Redis 6.0+
- **容器**: Docker, Docker Compose
- **Web服务器**: Nginx

### 核心模块

```
ZFX Payment Center
├── 系统管理模块
│   ├── 用户管理
│   ├── 角色权限管理
│   ├── 菜单管理
│   └── 部门管理
├── 商户管理模块
│   ├── 商户信息管理
│   └── 商户卡片管理
├── 支付渠道管理模块
│   ├── 渠道配置管理
│   ├── 支付方式配置
│   └── 业务配置管理
├── 订单管理模块
│   ├── 支付订单管理
│   ├── 现金订单管理
│   └── 订单回单管理
├── 结算管理模块
│   ├── 结算信息管理
│   └── 结算详情管理
├── OA办公模块
│   ├── 企业管理
│   └── 通知公告
└── 系统支持模块
    ├── 数据字典
    ├── 文件管理
    ├── 日志管理
    └── 代码生成器
```

## 📊 系统状态

- ✅ **后端服务**: 正常运行 (端口 1681)
- ✅ **前端服务**: 正常运行 (端口 1881)
- ✅ **MySQL数据库**: 已连接并初始化
- ✅ **Redis缓存**: 已连接
- ✅ **数据库表**: 完整创建，包含所有业务表
- ✅ **登录功能**: 正常可用
- ✅ **支付模块**: 数据库表结构完整
- ✅ **多语言支持**: 英文优先架构，支持中英文切换
- ✅ **语言切换**: 头部导航栏语言切换器，实时响应
- ✅ **国际化修复**: 所有菜单、页面内容、用户信息支持多语言

## 📚 文档

详细的文档请查看 [docs/README.md](docs/README.md)：

- 📋 [产品需求文档](docs/PRD-产品需求文档.md)
- 🔄 [业务流程文档](docs/业务流程文档.md)
- 🔌 [API接口文档](docs/API接口文档.md)
- 🗄️ [数据结构文档](docs/数据结构文档.md)
- 📖 [数据字典文档](docs/数据字典文档.md)
- 🌍 [多语言扩展文档](docs/多语言扩展文档.md)
- 📝 [变更日志](CHANGELOG.md)

## 🔧 开发指南

### 本地开发

```bash
# 后端开发
cd payment-center/smart-admin-api
mvn spring-boot:run

# 前端开发
cd payment-center/smart-admin-web
npm install
npm run dev
```

### 代码规范

- **后端**: 遵循阿里巴巴Java开发手册
- **前端**: 遵循Vue.js官方风格指南
- **数据库**: 遵循第三范式设计原则

### 接口规范

- RESTful API 设计
- 统一的响应格式
- 完善的错误码定义

## 📝 版本历史

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.2.0 | 2025-01-07 | 多语言国际化修复版本，实现英文优先架构 |
| v1.1.0 | 2024-09-29 | 添加多语言支持，完善国际化功能 |
| v1.0.1 | 2024-09-29 | 修复数据库表缺失问题，更新技术文档 |
| v1.0 | 2024-09-29 | 初始版本，包含完整的项目文档 |

## 🤝 贡献指南

1. Fork 项目仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📄 许可证

本项目采用 [MIT License](LICENSE) 许可证。

## 📞 联系方式

- **项目负责人**: 待定
- **技术支持**: 待定
- **邮箱**: 待定

---

**最后更新**: 2025-01-07  
**文档版本**: v1.2.0  
**维护团队**: ZFX开发团队
