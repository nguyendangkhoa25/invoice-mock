# Authentication Flow Diagram

## Request Flow with Authentication

```
┌────────────────────────────────────────────────────────────────────────┐
│                          Client Application                            │
│                                                                        │
│  curl -u admin:admin123 POST /api/v1/InvoiceWS/createInvoice         │
└──────────────────────────────┬─────────────────────────────────────────┘
                               │
                               ▼
        ┌─────────────────────────────────────────┐
        │   HTTP Request with Authorization       │
        │                                         │
        │  POST /api/v1/InvoiceWS/createInvoice  │
        │  Authorization: Basic base64(...)      │
        │  Content-Type: application/json         │
        │                                         │
        │  Body: {...invoice data...}            │
        └─────────────────┬───────────────────────┘
                          │
                          ▼
        ┌──────────────────────────────────────────────┐
        │  Spring Security Filter Chain               │
        │                                             │
        │  BasicAuthenticationFilter                  │
        │  ├─ Extract credentials from header         │
        │  └─ Decode base64 → username:password       │
        └──────────────────┬───────────────────────────┘
                          │
                          ▼
        ┌──────────────────────────────────────────────┐
        │  Authentication Manager                     │
        │                                             │
        │  Attempt Authentication                     │
        │  ├─ Verify user exists                      │
        │  └─ Verify password matches                 │
        └──────────────────┬───────────────────────────┘
                          │
           ┌──────────────┴──────────────┐
           │                             │
           ▼                             ▼
    ┌───────────────┐            ┌─────────────────┐
    │  Credentials  │            │  Credentials    │
    │   VALID ✅    │            │   INVALID ❌    │
    └───────┬───────┘            └────────┬────────┘
            │                             │
            ▼                             ▼
    ┌───────────────────────┐    ┌──────────────────────┐
    │ User Details Service  │    │ Return 401           │
    │                       │    │ Unauthorized         │
    │ Load user from        │    │                      │
    │ InMemoryUserDetails   │    │ Response:            │
    │ Manager               │    │ {                    │
    │                       │    │   "error": "Invalid" │
    │ Users:                │    │ }                    │
    │ - admin/admin123      │    └──────────────────────┘
    │ - user/user123        │
    └───────┬───────────────┘
            │
            ▼
    ┌───────────────────────┐
    │ BCryptPasswordEncoder │
    │                       │
    │ Compare passwords     │
    │ Provided: admin123    │
    │ Stored:   bcrypt(...) │
    │                       │
    │ Match? YES ✅         │
    └───────┬───────────────┘
            │
            ▼
    ┌───────────────────────────────────┐
    │ Create Authentication Object      │
    │                                   │
    │ Principal: admin                  │
    │ Authorities: [ROLE_ADMIN]         │
    │ Authenticated: true               │
    └───────┬───────────────────────────┘
            │
            ▼
    ┌────────────────────────────────────────────┐
    │  SecurityContext is Established            │
    │                                            │
    │  SecurityContextHolder.getContext()        │
    │  .setAuthentication(authenticationToken)   │
    └──────────────┬─────────────────────────────┘
                   │
                   ▼
        ┌──────────────────────────────────────────┐
        │  SInvoiceController                      │
        │                                          │
        │  @PostMapping("createInvoice")           │
        │  public ResponseEntity createInvoice() { │
        │    // Process request                    │
        │    // Access granted ✅                  │
        │  }                                       │
        └──────────────┬───────────────────────────┘
                       │
                       ▼
        ┌──────────────────────────────────────────┐
        │  Return HTTP 200 OK                      │
        │                                          │
        │  {                                       │
        │    "errorCode": null,                    │
        │    "code": null,                         │
        │    "result": {                           │
        │      "supplierTaxCode": "...",           │
        │      "invoiceNo": "C25MNP56",            │
        │      ...                                 │
        │    }                                     │
        │  }                                       │
        └──────────────┬───────────────────────────┘
                       │
                       ▼
        ┌──────────────────────────────────────────┐
        │         Client Receives Response         │
        │              200 OK ✅                   │
        └──────────────────────────────────────────┘
```

## Security Configuration Summary

```
SecurityConfig.java
│
├── HTTP Security Configuration
│   ├── Authorize Requests
│   │   ├── /api/v1/** → Requires Authentication
│   │   └── Others → Permit All
│   │
│   ├── HTTP Basic Authentication
│   │   ├── Extract credentials from Authorization header
│   │   ├── Decode base64 string
│   │   └── Pass to Authentication Manager
│   │
│   └── Session Management
│       └── Stateless (No cookies, no session storage)
│
├── User Details Service (In-Memory)
│   ├── User 1: admin / admin123 (ROLE_ADMIN)
│   └── User 2: user / user123 (ROLE_USER)
│
└── Password Encoder (BCrypt)
    ├── Algorithm: BCrypt
    ├── Strength: 10 rounds
    └── One-way encryption (non-reversible)
```

## Authentication Decision Tree

```
Request arrives with Authorization header
│
├─ Is Authorization header present?
│  │
│  ├─ NO → Return 401 Unauthorized
│  │
│  └─ YES
│     │
│     ├─ Parse credentials (username:password)
│     │
│     └─ Is username in user store?
│        │
│        ├─ NO → Return 401 Unauthorized
│        │
│        └─ YES
│           │
│           └─ Does password match (BCrypt)?
│              │
│              ├─ NO → Return 401 Unauthorized
│              │
│              └─ YES → Allow Request to Proceed ✅
│                 │
│                 └─ Call Controller Method
│                    │
│                    └─ Return Response
```

## Default Users Authentication

```
User 1: admin
├─ Username: admin
├─ Password: admin123
├─ Role: ADMIN
└─ Authentication
   ├─ HTTP: -u admin:admin123
   ├─ Base64: YWRtaW46YWRtaW4xMjM=
   └─ Header: Authorization: Basic YWRtaW46YWRtaW4xMjM=

User 2: user
├─ Username: user
├─ Password: user123
├─ Role: USER
└─ Authentication
   ├─ HTTP: -u user:user123
   ├─ Base64: dXNlcjp1c2VyMTIz
   └─ Header: Authorization: Basic dXNlcjp1c2VyMTIz
```

## cURL Authentication Examples

```
┌─────────────────────────────────────────────┐
│  Method 1: Using -u flag (Recommended)      │
├─────────────────────────────────────────────┤
│  curl -u admin:admin123 \                   │
│    -X POST http://localhost:9090/api/v1/... │
│                                             │
│  Spring automatically:                      │
│  ├─ Encodes credentials to base64          │
│  └─ Adds Authorization header              │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│  Method 2: Manual Base64 Encoding           │
├─────────────────────────────────────────────┤
│  BASE64=$(echo -n "admin:admin123" | base64)│
│  curl -H "Authorization: Basic $BASE64" \  │
│    -X POST http://localhost:9090/api/v1/...│
│                                             │
│  Result:                                    │
│  Authorization: Basic YWRtaW46YWRtaW4xMjM= │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│  Method 3: Hardcoded Base64 (Not Secure)    │
├─────────────────────────────────────────────┤
│  curl -H "Authorization: Basic \           │
│    YWRtaW46YWRtaW4xMjM=" \                 │
│    -X POST http://localhost:9090/api/v1/...│
│                                             │
│  ⚠️ Only use for testing                   │
│  ⚠️ Never hardcode in production           │
└─────────────────────────────────────────────┘
```

## Error Responses

```
┌──────────────────────────────────────────────┐
│  HTTP 401 - Unauthorized                     │
├──────────────────────────────────────────────┤
│  Causes:                                     │
│  ├─ Missing Authorization header             │
│  ├─ Invalid base64 encoding                  │
│  ├─ Username doesn't exist                   │
│  ├─ Password is incorrect                    │
│  └─ Credentials expired                      │
│                                              │
│  Response:                                   │
│  HTTP/1.1 401 Unauthorized                   │
│  WWW-Authenticate: Basic realm="..."        │
│                                              │
│  Body: (browser dependent)                   │
│  [Unauthorized] Please authenticate          │
└──────────────────────────────────────────────┘

┌──────────────────────────────────────────────┐
│  HTTP 200 - OK (Successful Authentication)   │
├──────────────────────────────────────────────┤
│  Preconditions:                              │
│  ├─ Authorization header present             │
│  ├─ Credentials valid                        │
│  └─ User has permission for endpoint         │
│                                              │
│  Response:                                   │
│  HTTP/1.1 200 OK                             │
│  Content-Type: application/json              │
│                                              │
│  Body: API response with data                │
└──────────────────────────────────────────────┘
```

---

## Summary

- **Authentication Type**: HTTP Basic
- **Storage**: In-Memory (InMemoryUserDetailsManager)
- **Encoding**: BCrypt
- **Session**: Stateless
- **Protected Paths**: /api/v1/**
- **Default Users**: admin (admin123), user (user123)
- **Status**: ✅ Active and Ready

