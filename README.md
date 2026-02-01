# VerseBank - Modern Banking System

VerseBank es un sistema bancario moderno construido con arquitectura hexagonal y Domain-Driven Design (DDD).

## 🏗️ Arquitectura

- **Domain Layer**: Lógica de negocio pura
- **Application Layer**: Casos de uso y servicios de aplicación  
- **Infrastructure Layer**: Adaptadores externos (base de datos, notificaciones)
- **Web Layer**: Controladores REST

## 🚀 Inicio Rápido

### Requisitos
- Java 17+
- Maven 3.6+

### Ejecutar aplicación
```bash
mvn spring-boot:run -s settings.xml
```

### Compilar y empaquetar
```bash
mvn clean install -s settings.xml
```

## 📊 Base de Datos

- **Desarrollo**: H2 en memoria
- **Consola H2**: http://localhost:8080/h2-console
- **URL JDBC**: `jdbc:h2:mem:versebankdb`
- **Usuario**: `sa`, **Password**: (vacío)

## 🛠️ Tecnologías

- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database**
- **Maven**
- **Java 17**

## 📡 API Endpoints

Ver [API_EXAMPLES.md](./API_EXAMPLES.md) para ejemplos detallados.

## 🧪 Testing

Ejecutar tests:
```bash
mvn test -s settings.xml
```

## 📦 Build

Crear JAR ejecutable:
```bash
mvn clean package -s settings.xml
java -jar target/versebank-1.0.0-exec.jar
```

## 📁 Estructura del Proyecto

```
src/
├── main/
│   └── java/com/versebank/
│       ├── AccountsApplication.java
│       ├── accounts/
│       │   ├── application/
│       │   │   ├── port/
│       │   │   │   ├── in/
│       │   │   │   └── out/
│       │   │   ├── TransferMoneyService.java
│       │   │   └── TransferMoneyUseCase.java
│       │   ├── domain/
│       │   │   ├── Account.java
│       │   │   ├── AccountId.java
│       │   │   ├── valueobjects/
│       │   │   ├── events/
│       │   │   ├── exceptions/
│       │   │   └── AccountDomainService.java
│       │   └── infrastructure/
│       │       ├── persistence/
│       │       ├── notification/
│       │       ├── web/
│       │       └── dto/
│       └── resources/
│           ├── application.yml
│           └── data.sql
└── test/
    └── java/com/versebank/
        └── accounts/
```

## 🔄 Principios DDD Aplicados

- **Ubiquitous Language**: Lenguaje del dominio en todo el código
- **Bounded Contexts**: Contextos delimitados claros
- **Aggregates**: Cuentas como agregados raíz
- **Domain Events**: Eventos de dominio para desacoplamiento
- **Hexagonal Architecture**: Puertos y adaptadores

## 📝 Notas de Desarrollo

- Base de datos se reinicia al parar la aplicación (H2 en memoria)
- Cuentas de prueba se crean automáticamente al iniciar
- Logs configurados para desarrollo y producción