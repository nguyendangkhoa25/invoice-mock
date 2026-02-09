# Invoice Mock Application - Basic Authentication Setup

## Summary

Basic HTTP Authentication has been successfully enabled for the Invoice Mock Application with in-memory username and password credentials.

## What Was Added

### 1. Security Configuration Class
**File**: `src/main/java/com/knp/invoice_mock/config/SecurityConfig.java`

Features:
- HTTP Basic Authentication enabled
- In-memory user details service with BCrypt password encoding
- Stateless session management (no cookies needed)
- CSRF protection disabled (suitable for REST APIs)
- All `/api/v1/**` endpoints require authentication

### 2. Application Configuration Update
**File**: `src/main/resources/application.yaml`

Added:
- Spring Security user configuration
- Logging configuration for security debugging
- Default user credentials

### 3. Documentation
**File**: `AUTHENTICATION.md`

Complete guide including:
- Default credentials
- How to use with cURL, Postman, JavaScript, and Python
- Configuration details
- Troubleshooting guide
- Security best practices

## Default Credentials

| Username | Password  | Role  |
|----------|-----------|-------|
| admin    | admin123  | ADMIN |
| user     | user123   | USER  |

## Quick Start

### Test with cURL (admin user):
```bash
curl -X POST http://localhost:9090/api/v1/InvoiceWS/createInvoice \
  -H "Content-Type: application/json" \
  -u admin:admin123 \
  -d '{"itemInfo":[],"payments":[],"buyerInfo":{},"generalInvoiceInfo":{},"summarizeInfo":{}}'
```

### Test with cURL (regular user):
```bash
curl -X POST http://localhost:9090/api/v1/InvoiceWS/createInvoice \
  -H "Content-Type: application/json" \
  -u user:user123 \
  -d '{"itemInfo":[],"payments":[],"buyerInfo":{},"generalInvoiceInfo":{},"summarizeInfo":{}}'
```

## Security Filter Chain Configuration

```
Request without credentials → 401 Unauthorized
Request with valid credentials → Proceed to controller
Request with invalid credentials → 401 Unauthorized
```

## What Endpoints Require Authentication

✅ **Protected** (Requires authentication):
- POST `/api/v1/InvoiceWS/createInvoice`
- All other `/api/v1/**` endpoints

✅ **Public** (No authentication required):
- All endpoints outside `/api/v1/` path

## Configuration Details

### Password Encoding
- Algorithm: BCrypt
- Strength: 10 (default)

### Session Management
- Type: Stateless (SessionCreationPolicy.STATELESS)
- No cookies or session storage needed
- Each request must include credentials

### CSRF Protection
- Disabled (suitable for stateless REST APIs)

## How to Change Credentials

Edit `SecurityConfig.java` and modify the username/password in the `userDetailsService()` method:

```java
UserDetails adminUser = User.builder()
    .username("new_username")
    .password(passwordEncoder().encode("new_password"))
    .roles("ADMIN")
    .build();
```

## Integrating with Client Applications

### JavaScript/Node.js Example:
```javascript
const response = await fetch('http://localhost:9090/api/v1/InvoiceWS/createInvoice', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Basic ' + btoa('admin:admin123')
  },
  body: JSON.stringify(payload)
});
```

### Java/Spring Boot Example:
```java
RestTemplate restTemplate = new RestTemplate();
HttpHeaders headers = new HttpHeaders();
headers.setBasicAuth("admin", "admin123");
headers.setContentType(MediaType.APPLICATION_JSON);

HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);
ResponseEntity<Map> response = restTemplate.exchange(
    "http://localhost:9090/api/v1/InvoiceWS/createInvoice",
    HttpMethod.POST,
    entity,
    Map.class
);
```

## Testing the Authentication

### 1. Test without credentials (should fail):
```bash
curl -X POST http://localhost:9090/api/v1/InvoiceWS/createInvoice \
  -H "Content-Type: application/json" \
  -d '{}'
# Expected: 401 Unauthorized
```

### 2. Test with correct credentials (should succeed):
```bash
curl -X POST http://localhost:9090/api/v1/InvoiceWS/createInvoice \
  -H "Content-Type: application/json" \
  -u admin:admin123 \
  -d '{}'
# Expected: 200 OK with response
```

### 3. Test with wrong password (should fail):
```bash
curl -X POST http://localhost:9090/api/v1/InvoiceWS/createInvoice \
  -H "Content-Type: application/json" \
  -u admin:wrongpassword \
  -d '{}'
# Expected: 401 Unauthorized
```

## Next Steps

1. **Start the application**: 
   ```bash
   mvn spring-boot:run
   ```

2. **Test with provided credentials**

3. **Update credentials** as needed for your environment

4. **Enable additional security features** such as:
   - JWT token-based authentication (instead of Basic Auth)
   - Role-based access control (RBAC)
   - API key authentication
   - OAuth2 integration

5. **Refer to AUTHENTICATION.md** for detailed documentation and examples

## Architecture

```
┌─────────────────────────────────────────────┐
│         Client Application                  │
│  (Sends request with Basic Auth)           │
└─────────────────┬───────────────────────────┘
                  │
                  ↓
┌─────────────────────────────────────────────┐
│    Spring Security Filter Chain             │
│  - BasicAuthenticationFilter                │
│  - AuthenticationManager                    │
└─────────────────┬───────────────────────────┘
                  │
                  ↓
┌─────────────────────────────────────────────┐
│   Validate Credentials Against:             │
│  - InMemoryUserDetailsManager               │
│  - BCryptPasswordEncoder                    │
└─────────────────┬───────────────────────────┘
                  │
                  ↓ (if valid)
┌─────────────────────────────────────────────┐
│         SInvoiceController                  │
│  (Process request)                          │
└─────────────────┬───────────────────────────┘
                  │
                  ↓
┌─────────────────────────────────────────────┐
│         Return Response (200 OK)            │
└─────────────────────────────────────────────┘
```

---

## Files Modified/Created

1. ✅ **CREATED**: `src/main/java/com/knp/invoice_mock/config/SecurityConfig.java`
   - Contains all Spring Security configuration

2. ✅ **UPDATED**: `src/main/resources/application.yaml`
   - Added Spring Security settings and logging configuration

3. ✅ **CREATED**: `AUTHENTICATION.md`
   - Comprehensive authentication documentation

## Complete Configuration

The Invoice Mock Application now includes:
- ✅ HTTP Basic Authentication
- ✅ In-memory user storage (admin, user)
- ✅ BCrypt password encoding
- ✅ Stateless session management
- ✅ Security logging
- ✅ Complete documentation

**Ready to use!** Start the application and authenticate with the provided credentials.

