# 认证与授权系统

一个基于Spring Boot的完整认证与授权系统，支持基于角色的访问控制（RBAC）。

## 🚀 功能特性

- **用户认证**：基于JWT的安全认证机制
- **用户管理**：完整的用户增删改查操作及分页功能
- **角色管理**：创建、更新、删除和分配角色
- **RBAC系统**：基于角色和菜单的细粒度权限控制
- **菜单管理**：层级菜单系统与权限管理
- **API文档**：Swagger/OpenAPI集成，便于测试
- **安全机制**：Spring Security方法级授权

## 📋 环境要求

- Java 17 或更高版本
- MySQL 8.0 或更高版本（或使用H2开发环境）
- Maven 3.6+

## 🚀 快速开始（零配置）

### 1. 克隆与构建
```bash
git clone <repository-url>
cd auth-system
mvn clean compile
```

### 2. 启动应用
```bash
mvn spring-boot:run
```

**应用启动地址：** http://localhost:8080

### 3. 验证启动成功
应用将自动：
- ✅ 初始化H2内存数据库
- ✅ 创建所有表和关系
- ✅ 插入示例数据（管理员用户、角色、菜单）
- ✅ 在8080端口启动

## ✅ 功能验证

### 实时测试示例

应用启动后，按顺序测试以下接口：

#### 🔑 步骤1：管理员登录
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**预期响应：**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "username": "admin",
  "roles": ["ADMIN"],
  "menus": [
    {
      "id": 1,
      "name": "用户管理",
      "path": "/users",
      "component": "UserManagement",
      "children": [...]
    }
  ]
}
```

#### 👥 步骤2：获取用户列表（需要ADMIN角色）
```bash
curl -X GET "http://localhost:8080/api/users?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**预期响应：**
```json
{
  "content": [
    {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "roles": ["ADMIN"],
      "status": 1
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 3,
  "totalPages": 1
}
```

#### ⚙️ 步骤3：创建新用户
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test123",
    "email": "test@example.com",
    "phone": "13800138000"
  }'
```

**预期响应：**
```json
{
  "id": 4,
  "username": "testuser",
  "email": "test@example.com",
  "phone": "13800138000",
  "status": 1,
  "roles": []
}
```

#### 🔐 步骤4：测试RBAC - 无token访问
```bash
curl -X GET http://localhost:8080/api/users
```

**预期响应：**
```json
{
  "timestamp": "2024-01-01T00:00:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

### 🎯 Web界面
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2控制台**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:auth_system`
  - 用户名: `sa`
  - 密码: *(留空)*

### 📊 预加载数据
系统自动创建以下测试账户：

| 用户名 | 密码 | 角色 | 描述 |
|----------|----------|------|-------------|
| admin    | admin123 | ADMIN | 系统管理员 |
| manager  | admin123 | MANAGER | 管理权限 |
| user1    | admin123 | USER  | 普通用户 |
| user2    | admin123 | USER  | 普通用户 |

### 🔍 数据库结构
访问H2控制台查看：
- **users** 表（4个用户）
- **roles** 表（3个角色）
- **menus** 表（层级菜单结构）
- **user_roles** 和 **role_menus** 关系表

## 🚀 快速测试命令

```bash
# 1. 登录
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq

# 2. 提取token并使用
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq -r '.accessToken')

echo "Token: $TOKEN"

# 3. 测试认证
curl -s -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer $TOKEN" | jq
```

## 📖 API文档

应用启动后可访问：
- Swagger UI: http://localhost:8080/swagger-ui.html
- API文档: http://localhost:8080/api-docs

## 🔧 默认用户

系统预配置用户用于测试：

| 用户名 | 密码 | 角色 | 描述 |
|----------|----------|--------|-------------|
| admin    | admin123 | ADMIN  | 系统管理员 |
| user1    | admin123 | USER   | 普通用户 |

## 🎯 API端点

### 认证接口
- `POST /api/auth/login` - 用户登录
- `GET /api/auth/me` - 获取当前用户信息

### 用户管理
- `GET /api/users` - 获取所有用户（分页）
- `GET /api/users/{id}` - 根据ID获取用户
- `POST /api/users` - 创建新用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户
- `PATCH /api/users/{id}/status` - 更新用户状态

### 角色管理
- `GET /api/roles` - 获取所有角色（分页）
- `GET /api/roles/{id}` - 根据ID获取角色
- `POST /api/roles` - 创建新角色
- `PUT /api/roles/{id}` - 更新角色
- `DELETE /api/roles/{id}` - 删除角色
- `GET /api/roles/active` - 获取激活角色

### 用户角色分配
- `GET /api/users/{userId}/roles` - 获取用户角色
- `POST /api/users/{userId}/roles` - 分配多个角色
- `POST /api/users/{userId}/roles/{roleId}` - 分配单个角色
- `DELETE /api/users/{userId}/roles/{roleId}` - 移除角色

## 🧪 系统测试

### 1. 登录测试
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### 2. 获取用户列表（需JWT token）
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer <your-jwt-token>"
```

### 3. 创建新用户
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "email": "newuser@example.com",
    "phone": "1234567890"
  }'
```

## 🔐 安全特性

- **JWT Token**: 2小时有效期
- **密码加密**: BCrypt哈希加密
- **基于角色的访问控制**: 方法级安全使用`@PreAuthorize`
- **输入验证**: 使用Bean Validation进行验证
- **SQL注入防护**: 使用JPA/Hibernate

## 🏗️ 项目结构

```
src/main/java/com/example/authsystem/
├── config/           # 安全和Swagger配置
├── controller/       # REST API控制器
├── dto/             # 数据传输对象
├── entity/          # JPA实体
├── repository/      # 数据仓库
├── security/        # JWT和安全组件
└── service/         # 业务逻辑服务
```

## 📊 数据库结构

- **users**: 用户账户
- **roles**: 系统角色
- **menus**: 菜单项和权限
- **user_roles**: 用户角色关系
- **role_menus**: 角色菜单关系

## 🚦 开发提示

1. **Swagger测试**: 使用Swagger UI进行交互式API测试
2. **H2控制台**: 访问 http://localhost:8080/h2-console（开发模式）
3. **日志**: 在`application.yml`中启用调试日志：
   ```yaml
   logging:
     level:
       com.example.authsystem: DEBUG
   ```

## 🐛 常见问题

### 常见问题：

1. **数据库连接问题**：
   - 确保MySQL正在运行
   - 检查数据库凭据
   - 验证数据库是否存在

2. **端口占用**：
   - 在`application.yml`中更改端口：
   ```yaml
   server:
     port: 8081
   ```

3. **JWT令牌问题**：
   - 确保令牌包含在Authorization头中
   - 检查令牌是否过期
   - 验证令牌格式： `Bearer <token>`

## 📞 支持

如有问题或疑问，请在仓库中创建问题或联系开发团队。

## 📄 许可证

本项目采用MIT许可证 - 详见 [LICENSE](LICENSE) 文件。