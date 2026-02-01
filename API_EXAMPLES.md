# VerseBank API - Ejemplos de Uso

## Configuración Inicial

La aplicación inicia con 3 cuentas de prueba:
- **acc-001**: Cuenta de Ahorros con $1000.00
- **acc-002**: Cuenta Corriente con $500.00  
- **acc-003**: Cuenta de Ahorros con $2000.00

## 1. Verificar Saldo

```bash
curl -X GET http://localhost:8080/api/accounts/acc-001/balance
```

**Respuesta esperada:**
```json
1000.00
```

## 2. Depositar Dinero

```bash
curl -X POST "http://localhost:8080/api/accounts/acc-001/deposit?amount=100&description=Deposito de prueba"
```

**Respuesta esperada:**
```
Deposit completed successfully
```

## 3. Transferencia Entre Cuentas

```bash
curl -X POST http://localhost:8080/api/accounts/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "sourceAccountId": "acc-001",
    "targetAccountId": "acc-002", 
    "amount": 50.00,
    "description": "Transferencia prueba"
  }'
```

**Respuesta esperada:**
```
Transfer completed successfully
```

## 4. Consultar Información Completa de Cuenta

```bash
curl -X GET http://localhost:8080/api/accounts/acc-001
```

**Respuesta esperada:**
```json
{
  "id": "acc-001",
  "customerId": "customer-001",
  "accountType": "SAVINGS",
  "balance": 1050.00,
  "createdAt": "2026-01-30T13:30:00",
  "updatedAt": "2026-01-30T13:30:00"
}
```

## 5. Verificar Saldo Suficiente

```bash
curl -X GET "http://localhost:8080/api/accounts/acc-001/has-sufficient-balance?amount=75.00"
```

**Respuesta esperada:**
```json
true
```

## 6. Retirar Dinero

```bash
curl -X POST "http://localhost:8080/api/accounts/acc-001/withdraw?amount=25&description=Retiro en cajero"
```

**Respuesta esperada:**
```
Withdrawal completed successfully
```

## 7. Transferencia con Fondos Insuficientes (Error)

```bash
curl -X POST http://localhost:8080/api/accounts/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "sourceAccountId": "acc-002",
    "targetAccountId": "acc-001",
    "amount": 1000.00,
    "description": "Transferencia excedida"
  }'
```

**Respuesta esperada:**
```
Insufficient funds: Insufficient funds for withdrawal
```

## 8. Consultar Cuenta No Existente (Error)

```bash
curl -X GET http://localhost:8080/api/accounts/cuenta-inexistente/balance
```

**Respuesta esperada:**
```json
{
  "timestamp": "2026-01-30T13:30:00.000+00:00",
  "status": "404",
  "error": "Not Found",
  "path": "/api/accounts/cuenta-inexistente/balance"
}
```

## 9. Flujo Completo de Operaciones

### Paso 1: Verificar saldos iniciales
```bash
curl -X GET http://localhost:8080/api/accounts/acc-001/balance  # 1000.00
curl -X GET http://localhost:8080/api/accounts/acc-002/balance  # 500.00
```

### Paso 2: Realizar transferencia
```bash
curl -X POST http://localhost:8080/api/accounts/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "sourceAccountId": "acc-001",
    "targetAccountId": "acc-002", 
    "amount": 200.00,
    "description": "Pago de servicios"
  }'
```

### Paso 3: Verificar saldos posteriores
```bash
curl -X GET http://localhost:8080/api/accounts/acc-001/balance  # 800.00 (menos comisión)
curl -X GET http://localhost:8080/api/accounts/acc-002/balance  # 700.00
```

### Paso 4: Depositar en la primera cuenta
```bash
curl -X POST "http://localhost:8080/api/accounts/acc-001/deposit?amount=150&description=Deposito salario"
```

### Paso 5: Verificar saldo final
```bash
curl -X GET http://localhost:8080/api/accounts/acc-001/balance  # 950.00
```

## Notas Importantes

- **Base de datos**: H2 en memoria (los datos se reinician al parar la aplicación)
- **Consola H2**: http://localhost:8080/h2-console
- **URL JDBC**: `jdbc:h2:mem:versebankdb`
- **Usuario**: `sa`, **Password**: (vacío)

## Códigos de Respuesta

- **200 OK**: Operación exitosa
- **400 Bad Request**: Parámetros inválidos
- **404 Not Found**: Cuenta no encontrada
- **409 Conflict**: Fondos insuficientes
- **500 Internal Server Error**: Error del servidor

## Headers Requeridos

Para endpoints con cuerpo JSON:
```bash
-H "Content-Type: application/json"
```

## Testing con PowerShell (Windows)

Si usas PowerShell en Windows:
```powershell
# Depositar
Invoke-RestMethod -Uri "http://localhost:8080/api/accounts/acc-001/deposit?amount=100&description=test" -Method POST

# Transferencia
$body = @{
    sourceAccountId = "acc-001"
    targetAccountId = "acc-002"
    amount = 50.00
    description = "Transferencia prueba"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/accounts/transfer" -Method POST -Body $body -ContentType "application/json"
```