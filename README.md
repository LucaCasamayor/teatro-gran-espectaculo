# Prueba Técnica - Sistema de Gestión de Reservas

## Objetivos del Sistema
1. **Registrar eventos** (teatro, recitales, conferencias) con tipos de entradas diferenciadas.  
2. **Administrar reservas**, evitando sobreventas.   
4. **Controlar pagos y estados de reserva** (`PENDING`, `PAID`, `CANCELLED`).  
5. **Implementar fidelización:** cada 5 asistencias en un año calendario → el siguiente evento es gratuito.  

---

## Arquitectura del Sistema

# Backend
- **Lenguaje:** Java 17  
- **Framework:** Spring Boot 3.x  
- **Persistencia:** Spring Data JPA  
- **Base de datos:** H2 (para pruebas, adaptable a MySQL/PostgreSQL)  
- **Testing:** JUnit5 + Mockito  

---
# Frontend

### Tecnologías y Frameworks
- **Framework principal:** Angular 17 
- **UI Library:** Angular Material
- **Lenguaje:** TypeScript
- **Estilos:** SCSS modular
- **Comunicación con backend:** HttpClient (REST API)
- **Build Tool:** Angular CLI
- **Servidor de desarrollo:** `ng serve` (puerto 4200 por defecto)

### Modelo de Dominio (Diagrama de Clases)

📄 *Ver diagrama completo en [`Clases.puml`](./backend/docs/uml/diagramaClases.puml) elaboración propia*

---

## Flujo del Negocio (Resumen)
1. **Empleado** crea o selecciona evento.  
2. **Empleado** registra cliente y genera reserva → `PENDING`.  
3. El **sistema** descuenta disponibilidad temporalmente.  
4. Si el cliente paga antes del evento → `PAID`.  
5. Si llega la fecha y sigue `PENDING` → el sistema la **cancela automáticamente** (`CANCELLED`).  
6. Al marcar una reserva como `PAID`, se actualiza la fidelización del cliente.  

📄 *Ver diagrama completo en [`diagramaNegocio.puml`](./backend/docs/uml/diagramaNegocio.puml)  elaboración propia*

---

## Base de Datos
 Base de datos embebida con H2, una vez levantado el backend:
 ```
http://localhost:8080/h2-console
```
```
url=jdbc:h2:mem:teatrobd
username=sa
password=

```
 📄 *Ver diagrama entidad-relacion en [`diagrama-er.puml`](./backend/docs/uml/diagrama-er.puml)  elaboración propia*

📘 *Los archivos `schema.sql` y `data.sql` crean y cargan la base de datos inicial.*

---
##  Requisitos del entorno

Antes de ejecutar el proyecto, asegurate de contar con las siguientes versiones o superiores instaladas en tu sistema:

| Herramienta | Versión mínima recomendada | Descripción |
|--------------|----------------------------|--------------|
| **Java JDK** | 17                         | Requerido para compilar y ejecutar el proyecto Spring Boot. |
| **Maven**    | 3.9.0                      | Utilizado para compilar, testear y empaquetar la aplicación. |
| **Spring Boot** | 3.3.4                   | Framework principal del backend. |
| **H2 Database** | 2.2.224       | Base de datos en memoria para desarrollo y testing. |
| **Lombok** | 1.18.34 | Genera automáticamente constructores, getters/setters y logs. |
| **ModelMapper** | 3.2.0 | Mapeo entre entidades y DTOs. |
| **Springdoc OpenAPI** | 2.6.0 | Generación automática de documentación Swagger UI. |


## Ejecución del Backend

###  Comandos
```bash
# Compilar y ejecutar
mvn clean install
mvn spring-boot:run
```

### 🌐 Swagger UI
Una vez iniciado el backend:
```
http://localhost:8080/swagger-ui/index.html
```
### Ejecución del Frontend

```bash
# Instalar dependencias
npm install

# Ejecutar el servidor de desarrollo
npm start
```

Luego abrir en el navegador:
```
http://localhost:4200
```

---

## Diagramas Incluidos
- `diagramaClases.puml ` → Modelo de dominio  
- `diagramaNegocio.puml ` → Flujo del negocio interno
- `diagrama-er.puml ` → Diagrama entidad-relación 
- `schema.sql` / `data.sql` → Generación y carga inicial de la BDD H2.

### 📁 Estructura del proyecto (Backend)
```
src/
└── main/
├── java/
│ └── com/teatro/backend/
│ ├── config/ # Configuración general de la app
│ │
│ ├── controllers/ # Controladores REST
│ │ ├── CustomerController.java
│ │ ├── EventController.java
│ │ └── ReservationController.java
│ │
│ ├── exceptions/ # Manejo global de excepciones
│ │ └── GlobalExceptionHandler.java
│ │
│ ├── models/
│ │ ├── dtos/ # Data Transfer Objects (DTOs)
│ │ │ ├── CustomerDTO.java
│ │ │ ├── EventDTO.java
│ │ │ ├── ReservationDTO.java
│ │ │ ├── ReservationItemDTO.java
│ │ │ └── TicketOptionDTO.java
│ │ │
│ │ ├── entities/ # Entidades JPA
│ │ │ ├── Customer.java
│ │ │ ├── Event.java
│ │ │ ├── Reservation.java
│ │ │ ├── ReservationItem.java
│ │ │ └── TicketOption.java
│ │ │
│ │ └── enums/ # Enums del sistema
│ │ ├── EventStatus.java
│ │ ├── EventType.java
│ │ └── ReservationStatus.java
│ │
│ ├── repositories/ # Interfaces de acceso a datos
│ │ ├── CustomerRepository.java
│ │ ├── EventRepository.java
│ │ ├── ReservationRepository.java
│ │ └── TicketOptionRepository.java
│ │
│ ├── services/ # Servicios (interfaces y lógica de negocio)
│ │ ├── CustomerService.java
│ │ ├── EventService.java
│ │ ├── ReservationService.java
│ │ │
│ │ └── impl/ # Implementaciones concretas de servicios
│ │ ├── CustomerServiceImpl.java
│ │ ├── EventServiceImpl.java
│ │ └── ReservationServiceImpl.java
│ │
│ └── BackendApplication.java # Clase principal 
│
└── resources/
├── application.properties # Configuración de la aplicación
├── data.sql # Datos iniciales (seed)
├── schema.sql # Estructura de base de datos
└── test/ # Tests unitarios y de integración
```
### 📁 Estructura del proyecto Front

```
src/
└── app/
├── core/ # Lógica central
│ ├── models/ # Modelos de datos
│ └── services/ # Servicios HTTP
│
├── layout/ # Estructura visual general
│
├── pages/ # Páginas principales del sistema
│ ├── customers/ # Módulo de clientes
│ │ ├── customer-form/ 
│ │ └── customer-list/
│ │
│ ├── dashboard/ # Panel principal
│ │
│ ├── events/ # Módulo de eventos
│ │ ├── event-form/ 
│ │ └── event-list/ 
│ │
│ └── reservation/ # Módulo de reservas
│ ├── reservation-form/ 
│ └── reservation-list/ 
│
├── shared/ # Componentes genéricos reutilizables
│ ├── confirm-dialog/
│ ├── generic-form/ 
│ └── generic-table/ 
```

---

### Funcionalidades destacadas

- **Gestión completa de eventos**  
  Crear, editar, cancelar y listar eventos.  
  Soporta distintos tipos:  
  - Obras de teatro → Entradas *General* y *VIP*  
  - Recitales → *Campo*, *Platea*, *Palco*  
  - Conferencias → *General* y *Meet & Greet*

- **Gestión de reservas y clientes**  
  - Visualización y administración de clientes.  
  - Creación de reservas asociadas a eventos.  
  - Cálculo automático de descuentos por fidelización. 
---

