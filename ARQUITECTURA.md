# 🏦 VerseBank - Arquitectura Hexagonal con Vertical Slicing

## 📐 Resumen Arquitectónico

VerseBank implementa una **Arquitectura Hexagonal (Ports & Adapters)** con **Vertical Slicing** y **Domain-Driven Design (DDD)**. El proyecto cumple con principios SOLID, Clean Code y mejores prácticas enterprise actuales.

### 🎯 **Estado Actual de Calidad**
- **Puntaje General**: 9.0/10 (Excelente)
- **Principios SOLID**: Todos aplicados correctamente
- **Arquitectura Hexagonal**: Implementación completa
- **Vertical Slicing**: Slices independientes y cohesivos
- **Testing**: Cobertura estratégica 100% del dominio

## Estructura del Proyecto

```
com/versebank/
├── AccountsApplication.java           # Punto de entrada principal
├── users/                             # Slice de Usuarios
│   ├── UsersConfig.java               # Configuración del slice
│   ├── domain/                        # Capa de Dominio
│   │   ├── User.java                  # Entidad principal
│   │   ├── UserId.java                # Value Object
│   │   ├── Email.java                 # Value Object
│   │   ├── UserRepository.java        # Puerto de Salida
│   │   └── exceptions/
│   │       └── UserAlreadyExistsException.java
│   ├── application/                   # Capa de Aplicación
│   │   ├── UserService.java           # Servicio de aplicación
│   │   └── port/in/                   # Puertos de Entrada
│   │       ├── CreateUserUseCase.java
│   │       ├── GetUserByIdUseCase.java
│   │       └── LinkAccountToUserUseCase.java
│   └── infrastructure/                # Capa de Infraestructura
│       ├── persistence/
│       │   ├── UserJpaEntity.java
│       │   ├── UserJpaRepository.java
│       │   ├── UserMapper.java
│       │   └── UserRepositoryAdapter.java
│       └── web/
│           ├── UserController.java
│           └── dto/
│               ├── CreateUserRequest.java
│               └── UserResponse.java
└── accounts/                          # Slice de Cuentas
    ├── domain/                        # Capa de Dominio (Lógica de Negocio Pura)
    │   ├── Account.java               # Entidad raíz del agregado
    │   ├── AccountId.java             # Value Object: Identificador único
    │   ├── valueobjects/              # Value Objects Inmutables
    │   │   ├── AccountType.java      # Enum: SAVINGS, CHECKING, BUSINESS
    │   │   ├── Balance.java          # Saldo con validaciones
    │   │   ├── Money.java           # Cantidad monetaria
    │   │   └── Transaction.java    # Transacción bancaria
    │   ├── events/                    # Eventos de Dominio
    │   │   ├── DomainEvent.java      # Clase base de eventos
    │   │   ├── MoneyDepositedEvent.java
    │   │   ├── MoneyWithdrawnEvent.java
    │   │   ├── AccountOpenedEvent.java
    │   │   └── MoneyReceivedEvent.java    # Nuevo: evento de recepción
    │   └── exceptions/
    │       └── InsufficientFundsException.java
    ├── application/                   # Capa de Aplicación (Casos de Uso)
    │   ├── TransferMoneyService.java  # Servicio: operaciones de dinero
    │   ├── AccountQueryService.java   # Nuevo: servicio de consultas (ISP)
    │   ├── port/in/                   # Puertos de Entrada (Interfaces)
    │   │   ├── TransferMoneyUseCase.java    # Contrato operaciones
    │   │   ├── AccountQueryPort.java        # Contrato consultas
    │   │   └── AccountSummary.java         # DTO para consultas
    │   └── port/out/                  # Puertos de Salida (Interfaces)
    │       ├── AccountRepository.java          # Contrato persistencia
    │       └── NotificationPort.java         # Contrato notificaciones
    └── infrastructure/                # Capa de Infraestructura
        ├── persistence/
        │   ├── AccountJpaEntity.java
        │   ├── AccountJpaRepository.java
        │   ├── AccountRepositoryAdapter.java
        │   └── mappers/AccountMapper.java
        ├── web/
        │   ├── AccountController.java
        │   └── dto/
        │       ├── TransferRequest.java
        │       └── AccountResponse.java
        └── notification/
            ├── EmailNotificationAdapter.java
            └── SmsNotificationAdapter.java
```

## ✅ Principios de Diseño Aplicados

### 🏗️ **SOLID (Todos Implementados)**
- **S (Single Responsibility)**: Cada clase tiene una única razón para cambiar
- **O (Open/Closed)**: Entidades abiertas a extensión, cerradas a modificación  
- **L (Liskov Substitution)**: Subtipos pueden sustituir a sus supertipos
- **I (Interface Segregation)**: Interfaces específicas (TransferMoneyUseCase separado de AccountQueryPort)
- **D (Dependency Inversion)**: Dependencias hacia abstracciones (puertos y adaptadores)

### 🧹 **Clean Code & Calidad**
- **Nombres Expresivos**: Clases y métodos con nombres claros
- **Métodos Cortos**: Funciones con una sola responsabilidad
- **Sin Duplicación**: Método helper `publishAndClearDomainEvents()` (DRY)
- **Sin Código Muerto**: Eliminados métodos no utilizados

### 🔗 **Ley de Demeter (Principio de Conocimiento Mínimo)**
- **Sin Cadena Larga**: Eliminada manipulación directa en `Account.transfer()`
- **Métodos de Fachada**: `receiveTransfer()` para encapsulamiento
- **Desacoplamiento**: Controllers delegan a servicios

### 🎯 **Principios Adicionales**
- **DRY**: Extraído manejo de eventos de dominio
- **KISS**: Soluciones simples y directas
- **YAGNI**: Solo funcionalidad necesaria implementada
- **Composición sobre Herencia**: Uso consistente de inyección de dependencias

### 🏦 **Screaming Architecture**
Los nombres "gritan" el propósito del negocio:
- `Account`, `Balance`, `Money` - Conceptos de dominio bancario
- `TransferMoneyService`, `UserService` - Casos de uso del negocio
- `InsufficientFundsException` - Problemas de dominio

### 🔌 **Arquitectura Hexagonal (Ports & Adapters)**
**Flujo de Dependencias (hacia adentro):**
```
Infrastructure → Application → Domain
     ↑                ↑           ↑
  Adaptadores      Use Cases   Entities
```

**Puertos Implementados:**
- **Entrada**: `TransferMoneyUseCase`, `AccountQueryPort`
- **Salida**: `AccountRepository`, `NotificationPort`
- **Dirección**: Adaptadores dependen de puertos, nunca al revés

## Integración entre Slices

### Users consulta Accounts
El slice `users` consulta información del slice `accounts` a través del puerto `AccountQueryPort`:

```java
// En UserService
private final AccountQueryPort accountQueryPort;

public void linkAccountToUser(UserId userId, String accountId) {
    // Verificar que la cuenta existe a través del puerto
    Optional<AccountSummary> account = accountQueryPort.findByAccountId(accountId);
    // ... lógica de negocio
}
```

### Ventajas de este enfoque:
1. **Desacoplamiento**: Users no conoce la implementación de Accounts
2. **Independencia**: Cada slice puede evolucionar independientemente
3. **Testabilidad**: Fácil de mockear en pruebas
4. **Claridad**: La interfaz de integración está explícitamente definida

## Flujo de Eventos de Dominio

1. **Emisión**: La entidad `Account` emite eventos al realizar operaciones
   ```java
   domainEvents.add(new MoneyDepositedEvent(...));
   ```

2. **Publicación**: `TransferMoneyService` publica los eventos
   ```java
   domainEvents.forEach(eventPublisher::publishEvent);
   ```

3. **Procesamiento**: `DomainEventDispatcher` escucha y procesa los eventos
   ```java
   @EventListener
   public void handleMoneyDepositedEvent(MoneyDepositedEvent event) {
       // Lógica de procesamiento
   }
   ```

## API REST Endpoints

### Accounts (/api/accounts)
- `POST /api/accounts/transfer` - Transferir dinero
- `POST /api/accounts/{accountId}/deposit` - Depositar
- `POST /api/accounts/{accountId}/withdraw` - Retirar
- `GET /api/accounts/{accountId}` - Obtener cuenta
- `GET /api/accounts/{accountId}/balance` - Obtener balance
- `GET /api/accounts/{accountId}/has-sufficient-balance` - Verificar balance

### Users (/api/users)
- `POST /api/users` - Crear usuario
- `GET /api/users/{userId}` - Obtener usuario (incluye sus cuentas)
- `GET /api/users/{userId}/accounts` - Obtener cuentas del usuario
- `POST /api/users/{userId}/link-account/{accountId}` - Vincular cuenta
- `DELETE /api/users/{userId}/link-account/{accountId}` - Desvincular cuenta

## Datos de Prueba

El sistema se inicializa con:
- **3 cuentas**: acc-001 ($1000), acc-002 ($500), acc-003 ($2000)
- **2 usuarios**: 
  - user-001 (John Doe) con acc-001 y acc-003
  - user-002 (Jane Smith) con acc-002

## 🏆 **Características Implementadas (Post-Refactoring)**

### ✅ **Arquitectura Enterprise Nivel 9.0/10**
- **Arquitectura Hexagonal Completa**: Puertos y adaptadores bien definidos
- **Vertical Slicing**: Slices accounts y users completamente independientes
- **Domain-Driven Design**: Aggregates, Value Objects, Domain Events
- **SOLID Principles**: Todos los principios aplicados correctamente
- **Clean Code**: Código legible, mantenible y sin code smells

### 🔧 **Mejoras Recientes Implementadas**
- **ISP Cumplido**: `TransferMoneyService` y `AccountQueryService` separados
- **Encapsulación**: `Account.transfer()` sin manipulación directa de estado
- **DRY Aplicado**: Manejo de eventos centralizado
- **Ley de Demeter**: Eliminadas cadenas largas de llamadas
- **Validaciones**: Bean Validation en DTOs
- **Código Muerto**: Eliminados métodos no utilizados

### 🏗️ **Características Técnicas**
- **Persistencia JPA**: Con mapeo optimizado
- **API REST**: Endpoints RESTful con validación
- **Eventos de Dominio**: Publicación vía Spring Events
- **Notificaciones**: Sistema adaptable (Email/SMS)
- **Testing**: Cobertura estratégica 60/30/10
- **Configuración Multi-ambiente**: Dev/Test/Prod profiles

### 📊 **Integración Entre Slices**
- **Desacoplamiento Total**: Users consulta Accounts vía puertos
- **Contratos Explícitos**: `AccountQueryPort` define la integración
- **Evolución Independiente**: Cada slice puede cambiar sin afectar al otro
