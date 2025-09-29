# Git 忽略文件配置说明

## 📋 概述

本项目使用 `.gitignore` 文件来指定 Git 版本控制系统应该忽略的文件和目录。这有助于保持仓库的清洁，避免提交不必要的文件。

## 🔧 配置文件结构

`.gitignore` 文件按照以下分类组织：

### 1. Java 相关文件
```gitignore
# Java 编译文件
*.class
*.jar
*.war
*.ear

# Maven 构建文件
target/
pom.xml.tag
dependency-reduced-pom.xml

# Spring Boot 相关
spring-boot-*.log
application-*.properties
application-*.yml
```

### 2. Node.js 相关文件
```gitignore
# 依赖包
node_modules/

# 日志文件
npm-debug.log*
yarn-debug.log*

# 环境变量文件
.env
.env.local
.env.production

# 构建输出
dist/
build/
```

### 3. IDE 和编辑器相关
```gitignore
# IntelliJ IDEA
.idea/
*.iml

# Eclipse
.project
.settings/

# VS Code
.vscode/

# Sublime Text
*.sublime-workspace
```

### 4. 操作系统相关文件
```gitignore
# macOS
.DS_Store
.AppleDouble

# Windows
Thumbs.db
Desktop.ini

# Linux
*~
.directory
```

### 5. Docker 相关文件
```gitignore
# Docker 卷
docker-data/
docker-volumes/

# Docker Compose 覆盖文件
docker-compose.override.yml
```

### 6. 数据库相关文件
```gitignore
# 数据库备份
*.sql.backup
*.sql.gz
*.dump
```

### 7. 日志文件
```gitignore
# 应用日志
logs/
*.log
log/
```

### 8. 临时文件和缓存
```gitignore
# 临时文件
*.tmp
*.temp
*.swp
*.bak

# 缓存文件
.cache/
cache/
```

### 9. 配置文件（敏感信息）
```gitignore
# 敏感配置文件
config/database.yml
config/secrets.yml
application-*.yml
```

### 10. 项目特定文件
```gitignore
# ZFX 项目特定
uploads/
files/
static/uploads/
local.properties
```

## 🚀 使用方法

### 查看被忽略的文件
```bash
# 查看所有被忽略的文件
git status --ignored

# 查看特定目录下被忽略的文件
git status --ignored payment-center/
```

### 强制添加被忽略的文件
```bash
# 如果确实需要添加某个被忽略的文件
git add -f <filename>
```

### 停止跟踪已跟踪的文件
```bash
# 如果某个文件已经被跟踪，但现在想忽略它
git rm --cached <filename>
```

### 测试忽略规则
```bash
# 检查某个文件是否被忽略
git check-ignore -v <filename>

# 检查多个文件
git check-ignore -v file1 file2 file3
```

## ⚠️ 注意事项

### 1. 敏感信息保护
- **环境变量文件** (`.env`) 已被忽略
- **配置文件** 包含敏感信息的版本被忽略
- **数据库备份文件** 被忽略

### 2. 构建产物
- **Maven target 目录** 被忽略
- **Node.js node_modules** 被忽略
- **编译后的 class 文件** 被忽略

### 3. 开发环境文件
- **IDE 配置文件** 被忽略
- **操作系统生成文件** 被忽略
- **临时文件** 被忽略

## 🔍 常见问题

### Q: 为什么我的文件没有被忽略？
A: 检查以下几点：
1. 文件路径是否正确
2. 文件是否已经被 Git 跟踪（已跟踪的文件不会被忽略）
3. `.gitignore` 文件语法是否正确

### Q: 如何忽略已经被跟踪的文件？
A: 使用以下命令：
```bash
git rm --cached <filename>
git commit -m "Remove tracked file from Git"
```

### Q: 如何查看某个文件是否被忽略？
A: 使用以下命令：
```bash
git check-ignore -v <filename>
```

### Q: 如何在忽略规则中添加例外？
A: 使用 `!` 前缀：
```gitignore
# 忽略所有 .log 文件
*.log

# 但不忽略 important.log
!important.log
```

## 📝 维护建议

### 1. 定期检查
- 定期运行 `git status --ignored` 检查忽略规则
- 确保没有敏感信息被意外提交

### 2. 团队协作
- 确保所有团队成员使用相同的 `.gitignore` 文件
- 在添加新的忽略规则时通知团队

### 3. 文档更新
- 当添加新的技术栈时，更新相应的忽略规则
- 保持 `.gitignore` 文件的组织和注释清晰

## 🛠️ 自定义配置

### 添加新的忽略规则
在 `.gitignore` 文件末尾添加：
```gitignore
# 自定义规则
your-custom-pattern/
*.your-extension
```

### 针对特定目录的忽略规则
```gitignore
# 只在特定目录下生效
payment-center/custom-pattern/
!payment-center/custom-pattern/important-file
```

### 使用通配符
```gitignore
# 忽略所有备份文件
*backup*
*.bak

# 忽略特定模式的文件
temp-*
*-temp
```

---

**最后更新**: 2024-09-29  
**维护人**: ZFX开发团队
