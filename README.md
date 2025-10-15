# Teatro Gran Espectáculo - Sistema de Gestión de Reservas

## Contexto
El **Teatro Gran Espectáculo** organiza obras de teatro, conciertos y conferencias.  
Hasta ahora, la gestión de reservas se realizaba manualmente, generando errores, sobreventas y confusión entre los asistentes.  
El objetivo de este sistema es **automatizar la administración de eventos y reservas**, optimizando el control de disponibilidad, pagos y fidelización de clientes.

---

## Problemas Detectados
- Reservas manuales sin control de disponibilidad.  
- Clientes que reservan y olvidan pagar.  
- Falta de registro histórico de clientes y asistencias.  
- Necesidad de premiar a los clientes frecuentes con **pases gratuitos**.  

---

## Objetivos del Sistema
1. **Registrar eventos** (teatro, recitales, conferencias) con tipos de entradas diferenciadas.  
2. **Administrar reservas** de manera segura y controlada, evitando sobreventas.  
3. **Actualizar automáticamente la disponibilidad** de entradas en tiempo real.  
4. **Controlar pagos y estados de reserva** (`PENDING`, `PAID`, `CANCELLED`).  
5. **Implementar fidelización:** cada 5 asistencias en un año calendario → el siguiente evento es gratuito.  
6. **Cancelar automáticamente reservas no pagadas** cuando inicia el evento.

---

## Arquitectura del Sistema

### 🖥️ Backend
- **Lenguaje:** Java 17  
- **Framework:** Spring Boot 3.x  
- **Persistencia:** Spring Data JPA  
- **Base de datos:** H2 (para pruebas, adaptable a MySQL/PostgreSQL)  
- **Documentación:** Swagger UI  
- **Testing:** JUnit5 + Mockito  

---

## Modelo de Dominio (Diagrama de Clases)

| Entidad | Descripción |
|----------|--------------|
| **Customer** | Datos del cliente (nombre, apellido, email, fidelización, pase gratuito, actividad). |
| **Event** | Representa cada espectáculo con su tipo, fecha y estado (`SCHEDULED`, `CANCELLED`, `FINISHED`). |
| **TicketOption** | Tipos de entrada del evento (general, VIP, platea, palco, etc.). Incluye control de stock y versión. |
| **Reservation** | Reserva de entradas realizada por el empleado. Contiene estado (`PENDING`, `PAID`, `CANCELLED`), monto total, fecha y cliente. |
| **ReservationItem** | Detalle de cada ticket reservado. |

📄 *Ver diagrama completo en [`Clases.puml`](./Clases.puml)*

---

## Flujo del Negocio (Resumen)
1. **Empleado** crea o selecciona evento.  
2. **Empleado** registra cliente y genera reserva → `PENDING`.  
3. El **sistema** descuenta disponibilidad temporalmente.  
4. Si el cliente paga antes del evento → `PAID`.  
5. Si llega la fecha y sigue `PENDING` → el sistema la **cancela automáticamente** (`CANCELLED`).  
6. Al marcar una reserva como `PAID`, se actualiza la fidelización del cliente.  

📄 *Ver diagrama completo en [`diagramaNegocio.puml`](./diagramaNegocio.puml)*

---

## Base de Datos
 Base de datos embebida con H2, una vez levantado el backend:
 ```
http://localhost:8080/h2-console/
```
```
url=jdbc:h2:mem:teatrobd
username=sa
password=

```
 

📘 *Los archivos `schema.sql` y `data.sql` crean y cargan la base de datos inicial.*

---
##  Requisitos del entorno

Antes de ejecutar el proyecto, asegurate de contar con las siguientes versiones o superiores instaladas en tu sistema:

| Herramienta | Versión mínima recomendada | Descripción |
|--------------|----------------------------|--------------|
| **Java JDK** | 17                         | Requerido para compilar y ejecutar el proyecto Spring Boot. |
| **Maven**    | 3.9.0                      | Utilizado para compilar, testear y empaquetar la aplicación. |
| **Spring Boot** | 3.3.4                   | Framework principal del backend. |
| **H2 Database** | 2.2.224 (runtime)       | Base de datos en memoria para desarrollo y testing. |
| **Lombok** | 1.18.34 | Genera automáticamente constructores, getters/setters y logs. |
| **ModelMapper** | 3.2.0 | Mapeo entre entidades y DTOs. |
| **Springdoc OpenAPI** | 2.6.0 | Generación automática de documentación Swagger UI. |

>  Si usás IntelliJ IDEA o VS Code, asegurate de tener el plugin de **Lombok** habilitado para evitar errores de compilación en tiempo de diseño.

## Ejecución del Proyecto

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

---

## ✅ Endpoints Principales

| Método | Endpoint | Descripción |
|--------|-----------|-------------|
| `GET` | `/api/events` | Listar eventos |
| `POST` | `/api/events` | Crear nuevo evento |
| `PUT` | `/api/events/{id}` | Modificar evento |
| `PATCH` | `/api/events/{id}/cancel` | Cancelar evento |
| `GET` | `/api/customers` | Listar clientes |
| `POST` | `/api/customers` | Crear cliente |
| `PUT` | `/api/customers/{id}` | Actualizar cliente |
| `DELETE` | `/api/customers/{id}` | Baja lógica |
| `GET` | `/api/reservations` | Listar reservas |
| `POST` | `/api/reservations` | Crear reserva |
| `PUT` | `/api/reservations/{id}/pay` | Marcar como pagada |
| `DELETE` | `/api/reservations/{id}` | Baja lógica |

---

## 🧾 Diagramas Incluidos
- `Clases.puml ` → Modelo de dominio completo  
- `diagramaNegocio.puml ` → Flujo del negocio interno  
- `schema.sql` / `data.sql` → Generación y carga inicial de la BDD H2.

---
 
### 💻 Frontend 
- **Framework:** Angular  
- **Objetivo:** interfaz interna para empleados del teatro (no para clientes externos).
