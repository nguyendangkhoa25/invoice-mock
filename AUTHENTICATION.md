# Invoice Mock Server - Authentication Guide

## Overview

The Invoice Mock Server now includes HTTP Basic Authentication to secure the API endpoints. All requests to `/api/v1/**` endpoints require authentication using in-memory credentials.

## Authentication Configuration

### Default Credentials

Two default users are configured with the following credentials:

| Username | Password  | Role  |
|----------|-----------|-------|
| admin    | admin123  | ADMIN |
| user     | user123   | USER  |

These credentials are stored in-memory and configured in `SecurityConfig.java`.

## Implementation Details

### Security Configuration File
- **Location**: `src/main/java/com/knp/invoice_mock/config/SecurityConfig.java`
- **Features**:
  - HTTP Basic Authentication enabled
  - In-memory user details service
  - BCrypt password encoding
  - Stateless session management (no cookies)
  - CSRF protection disabled for API use
  - All `/api/v1/**` endpoints require authentication

### Key Configuration Points

```java
// Users are defined with encoded passwords
UserDetails adminUser = User.builder()
    .username("admin")
    .password(passwordEncoder().encode("admin123"))
    .roles("ADMIN")
    .build();

UserDetails normalUser = User.builder()
    .username("user")
    .password(passwordEncoder().encode("user123"))
    .roles("USER")
    .build();
```

## How to Use

### Using cURL

#### Request with Basic Auth (admin user):
```bash
curl -X POST http://localhost:9090/api/v1/InvoiceWS/createInvoice/ \
  -H "Content-Type: application/json" \
  -u admin:admin123 \
  -d '{"itemInfo":[],"payments":[],"buyerInfo":{},"generalInvoiceInfo":{},"summarizeInfo":{}}'
```

#### Request with Basic Auth (regular user):
```bash
curl -X POST http://localhost:9090/api/v1/InvoiceWS/createInvoice/ \
  -H "Content-Type: application/json" \
  -u user:user123 \
  -d '{"itemInfo":[],"payments":[],"buyerInfo":{},"generalInvoiceInfo":{},"summarizeInfo":{}}'
```

#### Using base64 encoded credentials:
```bash
# Encode credentials: echo -n "admin:admin123" | base64
curl -X POST http://localhost:9090/api/v1/InvoiceWS/createInvoice/ \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{"itemInfo":[],"payments":[],"buyerInfo":{},"generalInvoiceInfo":{},"summarizeInfo":{}}'
```

### Using Postman

1. **Create a new request** (POST, GET, etc.)
2. **Set URL**: `http://localhost:9090/api/v1/InvoiceWS/createInvoice/`
3. **Authentication Tab**:
   - Select "Basic Auth" from the dropdown
   - Username: `admin`
   - Password: `admin123`
4. **Headers Tab**:
   - Add `Content-Type: application/json`
5. **Body Tab**:
   - Select "raw" and "JSON"
   - Paste your request payload
6. **Send**

### Using JavaScript/Fetch

```javascript
const credentials = btoa('admin:admin123'); // Base64 encode

fetch('http://localhost:9090/api/v1/InvoiceWS/createInvoice/', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Basic ${credentials}`
  },
  body: JSON.stringify({
    itemInfo: [],
    payments: [],
    buyerInfo: {},
    generalInvoiceInfo: {},
    summarizeInfo: {}
  })
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```

### Using Python/Requests

```python
import requests
from requests.auth import HTTPBasicAuth

url = 'http://localhost:9090/api/v1/InvoiceWS/createInvoice/'
payload = {
    'itemInfo': [],
    'payments': [],
    'buyerInfo': {},
    'generalInvoiceInfo': {},
    'summarizeInfo': {}
}

response = requests.post(
    url,
    json=payload,
    auth=HTTPBasicAuth('admin', 'admin123')
)

print(response.json())
```

## Changing Credentials

To change the default credentials, modify the `SecurityConfig.java` file:

```java
UserDetails adminUser = User.builder()
    .username("your_new_username")  // Change username
    .password(passwordEncoder().encode("your_new_password"))  // Change password
    .roles("ADMIN")
    .build();
```

Then rebuild and restart the application.

## Disabling Authentication (Development Only)

To disable authentication for development purposes, modify `SecurityConfig.java`:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authz -> authz
            .anyRequest().permitAll()  // Allow all requests without authentication
        )
        .csrf().disable();

    return http.build();
}
```

**Warning**: Only use this in development environments. Always enable authentication in production.

## Environment Variables (Optional)

You can also configure credentials via environment variables in `application.yaml`:

```yaml
spring:
  security:
    user:
      name: ${SECURITY_USERNAME:admin}
      password: ${SECURITY_PASSWORD:admin123}
```

Then set environment variables:
```bash
export SECURITY_USERNAME=myuser
export SECURITY_PASSWORD=mypassword
```

## Troubleshooting

### 401 Unauthorized
- **Cause**: Missing or incorrect credentials
- **Solution**: Verify username and password are correct

### 403 Forbidden
- **Cause**: User exists but doesn't have required role
- **Solution**: Check role permissions in SecurityConfig

### Cannot authenticate
- **Cause**: Spring Security not properly initialized
- **Solution**: Check application startup logs for security configuration errors

## Security Best Practices

1. **Use HTTPS in Production**: Always use HTTPS instead of HTTP for API calls
2. **Rotate Credentials**: Change default passwords regularly
3. **Use Strong Passwords**: Use complex passwords instead of simple ones
4. **Limit User Access**: Create separate users with limited roles for different use cases
5. **Monitor Access**: Enable logging to monitor authentication attempts
6. **Store Credentials Securely**: Never hardcode credentials in client applications

## Related Files

- Security Configuration: `src/main/java/com/knp/invoice_mock/config/SecurityConfig.java`
- Application Configuration: `src/main/resources/application.yaml`
- Logging Configuration: Can be adjusted in `application.yaml`

