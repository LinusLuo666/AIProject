# Authentication and Authorization System

A comprehensive Spring Boot-based authentication and authorization system with RBAC (Role-Based Access Control) support.

## 🚀 Features

- **User Authentication**: JWT-based authentication with secure token generation
- **User Management**: Complete CRUD operations with pagination
- **Role Management**: Create, update, delete and assign roles
- **RBAC System**: Fine-grained permission control using roles and menus
- **Menu Management**: Hierarchical menu system with permissions
- **API Documentation**: Swagger/OpenAPI integration for easy testing
- **Security**: Spring Security with method-level authorization

## 📋 Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher (or H2 for development)
- Maven 3.6+

## 🚀 Quick Start (Zero Configuration)

### 1. Clone and Build
```bash
git clone <repository-url>
cd auth-system
mvn clean compile
```

### 2. Start Application
```bash
mvn spring-boot:run
```

**Application starts on:** http://localhost:8080

### 3. Verify Success
The application will automatically:
- ✅ Initialize H2 in-memory database
- ✅ Create all tables and relationships
- ✅ Insert sample data (admin user, roles, menus)
- ✅ Start on port 8080

## ✅ Proof of Functionality

### Live Testing Examples

Once the application is running, test these endpoints in order:

#### 🔑 Step 1: Login as Admin
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**Expected Response:**
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

#### 👥 Step 2: List Users (requires ADMIN role)
```bash
curl -X GET "http://localhost:8080/api/users?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected Response:**
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

#### ⚙️ Step 3: Create New User
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

**Expected Response:**
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

#### 🔐 Step 4: Test RBAC - Try accessing without token
```bash
curl -X GET http://localhost:8080/api/users
```

**Expected Response:**
```json
{
  "timestamp": "2024-01-01T00:00:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

### 🎯 Web Interface
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:auth_system`
  - Username: `sa`
  - Password: *(empty)*

### 📊 Pre-loaded Data
The system automatically creates these test accounts:

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| admin    | admin123 | ADMIN | Full system access |
| manager  | admin123 | MANAGER | Management access |
| user1    | admin123 | USER  | Regular user access |
| user2    | admin123 | USER  | Regular user access |

### 🔍 Database Schema
Access H2 Console to see:
- **users** table with 4 users
- **roles** table with 3 roles
- **menus** table with hierarchical menu structure
- **user_roles** and **role_menus** relationship tables

## 🚀 Quick Test Commands

```bash
# 1. Login
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq

# 2. Extract token and use it
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq -r '.accessToken')

echo "Token: $TOKEN"

# 3. Test authentication
curl -s -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer $TOKEN" | jq
```

## 📖 API Documentation

Once the application is running, you can access the API documentation at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

## 🔧 Default Users

The system comes with pre-configured users for testing:

| Username | Password | Role  | Description |
|----------|----------|--------|-------------|
| admin    | admin123 | ADMIN  | Full system access |
| user1    | admin123 | USER   | Regular user access |

## 🎯 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `GET /api/auth/me` - Get current user info

### User Management
- `GET /api/users` - Get all users (paginated)
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `PATCH /api/users/{id}/status` - Update user status

### Role Management
- `GET /api/roles` - Get all roles (paginated)
- `GET /api/roles/{id}` - Get role by ID
- `POST /api/roles` - Create new role
- `PUT /api/roles/{id}` - Update role
- `DELETE /api/roles/{id}` - Delete role
- `GET /api/roles/active` - Get active roles

### User-Role Assignment
- `GET /api/users/{userId}/roles` - Get user roles
- `POST /api/users/{userId}/roles` - Assign multiple roles
- `POST /api/users/{userId}/roles/{roleId}` - Assign single role
- `DELETE /api/users/{userId}/roles/{roleId}` - Remove role

## 🧪 Testing the System

### 1. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### 2. Get Users (with JWT token)
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer <your-jwt-token>"
```

### 3. Create New User
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

## 🔐 Security Features

- **JWT Token**: 2-hour expiration time
- **Password Encryption**: BCrypt hashing
- **Role-based Access Control**: Method-level security with `@PreAuthorize`
- **Input Validation**: Comprehensive validation using Bean Validation
- **SQL Injection Prevention**: Using JPA/Hibernate

## 🏗️ Project Structure

```
src/main/java/com/example/authsystem/
├── config/           # Security and Swagger configuration
├── controller/       # REST API controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA entities
├── repository/      # Data repositories
├── security/        # JWT and security components
└── service/         # Business logic services
```

## 📊 Database Schema

- **users**: User accounts
- **roles**: System roles
- **menus**: Menu items and permissions
- **user_roles**: User-role relationships
- **role_menus**: Role-menu relationships

## 🚦 Development Tips

1. **Swagger Testing**: Use Swagger UI for interactive API testing
2. **H2 Console**: Access at http://localhost:8080/h2-console (dev mode)
3. **Logs**: Enable debug logging in `application.yml`:
   ```yaml
   logging:
     level:
       com.example.authsystem: DEBUG
   ```

## 🐛 Troubleshooting

### Common Issues:

1. **Database Connection Issues**:
   - Ensure MySQL is running
   - Check database credentials
   - Verify database exists

2. **Port Already in Use**:
   - Change port in `application.yml`:
   ```yaml
   server:
     port: 8081
   ```

3. **JWT Token Issues**:
   - Ensure token is included in Authorization header
   - Check token expiration
   - Verify token format: `Bearer <token>`

## 📞 Support

For issues or questions, please create an issue in the repository or contact the development team.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.