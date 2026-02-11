# Hostpitami Backend

Backend REST API per la piattaforma Hostpitami - Sistema di gestione per strutture ricettive con booking engine integrato.

## 📋 Indice

- [Panoramica](#panoramica)
- [Tecnologie](#tecnologie)
- [Architettura](#architettura)
- [Prerequisiti](#prerequisiti)
- [Installazione](#installazione)
- [Configurazione](#configurazione)
- [Avvio del Progetto](#avvio-del-progetto)
- [Struttura del Progetto](#struttura-del-progetto)
- [API Endpoints](#api-endpoints)
- [Modello Dati](#modello-dati)
- [Sicurezza](#sicurezza)
- [Database](#database)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)

## 🎯 Panoramica

Hostpitami Backend è un'applicazione Spring Boot che fornisce un sistema completo per la gestione di strutture ricettive. Include funzionalità per:

- **Gestione Multi-tenant**: Supporto per account multipli con strutture separate
- **Booking Engine**: Sistema di prenotazione con disponibilità in tempo reale
- **Rate Management**: Gestione tariffe dinamiche e piani tariffari
- **Room Management**: Configurazione e gestione camere
- **Payment Integration**: Integrazione con Stripe per i pagamenti
- **Website Generation**: Generazione automatica di siti web per le strutture
- **Guest Management**: Gestione anagrafica ospiti
- **Policy Management**: Configurazione politiche di cancellazione e regole di prenotazione

## 🛠 Tecnologie

### Core Framework
- **Java 17+**
- **Spring Boot 3.x**
- **Spring Security** - Autenticazione e autorizzazione
- **Spring Data JPA** - ORM e persistenza dati
- **Hibernate** - Implementazione JPA

### Database
- **PostgreSQL** - Database principale
- **Flyway** - Gestione migrazioni database (configurabile)

### Sicurezza
- **JWT (JSON Web Tokens)** - Autenticazione stateless
- **BCrypt** - Hashing password

### Integrations
- **Stripe API** - Pagamenti
- **JavaMail** - Invio email

### Build & Dependency Management
- **Maven** - Gestione dipendenze e build

### Utilities
- **Lombok** - Riduzione boilerplate code
- **Jakarta Validation** - Validazione input

## 🏗 Architettura

Il progetto segue un'architettura a strati (Layered Architecture) con separazione delle responsabilità:

```
┌─────────────────────────────────────┐
│         API Layer                   │
│    (Controllers & DTOs)             │
├─────────────────────────────────────┤
│       Service Layer                 │
│   (Business Logic)                  │
├─────────────────────────────────────┤
│       Repository Layer              │
│   (Data Access)                     │
├─────────────────────────────────────┤
│       Domain Layer                  │
│   (Entities & Models)               │
└─────────────────────────────────────┘
```

### Principi Architetturali
- **Dependency Injection**: Gestione dipendenze tramite Spring
- **Multi-tenancy**: Isolamento dati per account tramite `TenantEntity`
- **RESTful API**: Design API secondo principi REST
- **DTO Pattern**: Separazione tra entità domain e rappresentazione API

## 📦 Prerequisiti

- **Docker** e **Docker Compose** (richiesto)
- **Java JDK 17** o superiore
- **Maven 3.6+**
- **Git**

### Opzionali
- **Postman** (per testare le API - collection inclusa nel progetto)

## 🚀 Installazione

### 1. Clonare il Repository

```bash
git clone https://github.com/TanaseSebastian/hostpitami-backend.git
cd hostpitami-backend
```

### 2. Avviare l'ambiente con Docker Compose

Il progetto include un file `docker-compose.yml` che configura automaticamente:
- **PostgreSQL 16** - Database principale
- **pgAdmin 4** - Interfaccia web per gestire il database

```bash
docker-compose up -d
```

Questo comando avvia:
- **PostgreSQL** sulla porta `5432`
- **pgAdmin** su `http://localhost:5050`

#### Accesso a pgAdmin

1. Apri il browser su `http://localhost:5050`
2. Login con:
    - **Email**: `admin@hostpitami.com`
    - **Password**: `admin`
3. Aggiungi il server PostgreSQL:
    - **Host**: `postgres` (nome del container)
    - **Port**: `5432`
    - **Database**: `hostpitami`
    - **Username**: `hostpitami`
    - **Password**: `hostpitami`

### 3. Installare le Dipendenze del Backend

```bash
mvn clean install
```

### Verifica dello stato dei container

```bash
docker-compose ps
```

Dovresti vedere:
```
NAME                    STATUS    PORTS
hostpitami-postgres     Up        0.0.0.0:5432->5432/tcp
hostpitami-pgadmin      Up        0.0.0.0:5050->80/tcp
```

## ⚙️ Configurazione

### 1. Variabili d'Ambiente

Crea un file `.env` nella root del progetto (o configura le variabili nel tuo sistema):

```env
# Database (già configurato in docker-compose.yml)
DB_HOST=localhost
DB_PORT=5432
DB_NAME=hostpitami
DB_USER=hostpitami
DB_PASS=hostpitami

# JWT
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
JWT_EXPIRATION_SECONDS=86400

# Stripe
STRIPE_SECRET_KEY=sk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...

# Email (opzionale)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Application
SPRING_PROFILES_ACTIVE=dev
```

**Nota**: Le credenziali del database sono già configurate nel `docker-compose.yml` e corrispondono ai valori di default in `application-dev.yml`.

### 2. Configurazione Application

Il progetto supporta profili Spring multipli:

- **`application.yaml`**: Configurazione base
- **`application-dev.yml`**: Ambiente di sviluppo
- **`application-prod.yml`**: Ambiente di produzione (da creare)

**Nota Importante**: In `application-dev.yml` è presente un JWT_SECRET di esempio. **CAMBIALO ASSOLUTAMENTE in produzione!**

### 3. Database Setup

L'applicazione è configurata con:
- **Hibernate DDL Auto**: `update` (in dev)
- **Flyway**: Disabilitato (può essere abilitato modificando `application-dev.yml`)

Grazie a Docker Compose, il database PostgreSQL è già configurato e pronto all'uso. Non serve installare PostgreSQL localmente.

Se vuoi abilitare Flyway per gestire le migrazioni:
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
```

## 🎬 Avvio del Progetto

### Avvio Ambiente di Sviluppo

**1. Avvia i servizi Docker (se non già avviati):**
```bash
docker-compose up -d
```

**2. Avvia l'applicazione Spring Boot:**

```bash
mvn spring-boot:run
```

**Oppure con IDE (IntelliJ IDEA / Eclipse):**
1. Importa il progetto come progetto Maven
2. Configura le variabili d'ambiente (se necessario)
3. Esegui la classe principale (cercala nel package `com.hostpitami`)

### Verifica Avvio

L'applicazione parte sulla porta **8080** (default Spring Boot).

Verifica che sia attiva:
```bash
curl http://localhost:8080/actuator/health
```

Se configurato correttamente, dovresti ricevere:
```json
{"status": "UP"}
```

### Accesso ai Servizi

- **Backend API**: `http://localhost:8080`
- **PostgreSQL**: `localhost:5432`
- **pgAdmin**: `http://localhost:5050`

### Stop dei Servizi

Per fermare i container Docker:
```bash
docker-compose down
```

Per fermare e rimuovere anche i volumi (dati del database):
```bash
docker-compose down -v
```

## 📮 Testing con Postman

Il progetto include una **Postman Collection completa** per testare tutte le API.

### Importare la Collection

1. **Scarica i file** (inclusi nel repository nella cartella testPostman):
    - `hostpitami_postman_collection.json` - Collection con tutte le richieste
    - `hostpitami_postman_environment.json` - Environment con variabili preconfigurate

2. **Importa in Postman**:
    - Apri Postman
    - Click su **Import**
    - Trascina entrambi i file JSON
    - Seleziona l'environment "Hostpitami Localhost"

### Variabili Environment

L'environment include le seguenti variabili preconfigurate:

| Variabile | Valore Default | Descrizione |
|-----------|----------------|-------------|
| `baseUrl` | `http://localhost:8080` | URL base del backend |
| `token` | (auto) | JWT token (salvato automaticamente dopo login) |
| `structureId` | (auto) | ID struttura corrente |
| `roomId` | (auto) | ID camera corrente |
| `planId` | (auto) | ID piano tariffario corrente |
| `bookingId` | (auto) | ID prenotazione corrente |
| `dateFrom` | `2026-02-10` | Data inizio per test |
| `dateTo` | `2026-02-20` | Data fine per test |
| `checkIn` | `2026-03-10` | Check-in per test booking |
| `checkOut` | `2026-03-12` | Check-out per test booking |

### Struttura della Collection

La collection è organizzata in cartelle per funzionalità:

#### 🔐 Auth
- **Register** - Registrazione nuovo utente
- **Login** - Login e salvataggio automatico del token
- **Me** - Info utente corrente
- **Forgot Password** - Richiesta reset password
- **Reset Password** - Reset password con token

#### 🏨 Admin — Structures
- List Structures
- Create Structure
- Get Structure
- Update Structure (PATCH)
- Archive/Restore Structure
- Publish/Unpublish Structure
- Duplicate Structure
- Website Preview
- Website Regenerate
- Delete Structure

#### 🛏️ Admin — Rooms
- List Rooms
- Create Room
- Get Room
- Update Room (PATCH)
- Archive/Restore Room
- Delete Room

#### 💰 Admin — Rates
- List Rate Plans
- Create Rate Plan
- Get Rate Plan
- Update Rate Plan (PATCH)
- Delete Rate Plan
- Set Rate Plan Rooms
- Set Rates Range
- Get Rate Calendar

#### 📋 Admin — Booking Rules & Policies
- List/Create/Update/Delete Booking Rules
- List/Create/Update/Delete Cancellation Policies

#### 🎯 Booking Engine (Public)
- Search Availability
- Get Quote
- Hold Reservation
- Checkout

#### 💳 Mock Payment (Dev)
- Simulate Payment

### Workflow di Test Consigliato

1. **Registrazione/Login**:
   ```
   Auth → Register → Login
   ```
   Il token JWT viene salvato automaticamente

2. **Crea Struttura**:
   ```
   Admin — Structures → Create Structure
   ```
   L'ID viene salvato in `structureId`

3. **Configura Camere**:
   ```
   Admin — Rooms → Create Room
   ```

4. **Configura Tariffe**:
   ```
   Admin — Rates → Create Rate Plan → Set Rate Plan Rooms → Set Rates Range
   ```

5. **Testa Booking Engine**:
   ```
   Booking Engine → Search Availability → Get Quote → Hold → Checkout
   ```

### Scripts Automatici

La collection include **script automatici** che:
- ✅ Salvano il token JWT dopo login/register
- ✅ Estraggono e salvano gli ID dalle risposte
- ✅ Validano gli status code
- ✅ Semplificano il workflow di test

### Tip: Collection Runner

Per testare l'intera flow automaticamente:
1. Click su **Run collection**
2. Seleziona le richieste da eseguire
3. Configura delay tra richieste (es. 500ms)
4. Click **Run**

## 📁 Struttura del Progetto

```
hostpitami-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/hostpitami/
│   │   │       ├── api/                      # API Layer
│   │   │       │   ├── auth/                 # Autenticazione
│   │   │       │   │   ├── controller/       # AuthController
│   │   │       │   │   └── dto/              # Request/Response DTOs
│   │   │       │   ├── booking/              # Prenotazioni e Politiche
│   │   │       │   │   ├── controller/       # CancellationPolicyAdminController
│   │   │       │   │   └── dto/
│   │   │       │   ├── bookingengine/        # Booking Engine Pubblico
│   │   │       │   │   ├── controller/       # BookingEngineController, MockPaymentController
│   │   │       │   │   └── dto/
│   │   │       │   ├── guest/                # Gestione Ospiti
│   │   │       │   ├── rates/                # Gestione Tariffe
│   │   │       │   │   ├── controller/       # RateAdminController
│   │   │       │   │   └── dto/
│   │   │       │   ├── room/                 # Gestione Camere
│   │   │       │   │   ├── controller/       # RoomAdminController
│   │   │       │   │   └── dto/
│   │   │       │   └── structure/            # Gestione Strutture
│   │   │       │       ├── controller/       # StructureAdminController
│   │   │       │       └── dto/
│   │   │       │
│   │   │       ├── domain/                   # Domain Layer
│   │   │       │   ├── entity/               # Entità JPA
│   │   │       │   │   ├── auth/             # User, Role, PasswordResetToken
│   │   │       │   │   ├── account/          # Account, AccountMember
│   │   │       │   │   ├── booking/          # Booking, BookingRule, CancellationPolicy
│   │   │       │   │   ├── guest/            # Guest
│   │   │       │   │   ├── payment/          # PaymentTransaction
│   │   │       │   │   ├── rates/            # RatePlan, RateCalendar, RatePlanRoom
│   │   │       │   │   ├── room/             # Room, RoomType
│   │   │       │   │   ├── structure/        # Structure, StructureType
│   │   │       │   │   └── base/             # BaseEntity, TenantEntity
│   │   │       │   └── repository/           # Repository JPA
│   │   │       │
│   │   │       ├── security/                 # Security Layer
│   │   │       │   ├── jwt/                  # JwtService, JwtAuthenticationFilter
│   │   │       │   └── auth/                 # UserDetailsServiceImpl, AuthResolver
│   │   │       │
│   │   │       └── service/                  # Service Layer
│   │   │           ├── auth/                 # AuthService
│   │   │           ├── account/              # AccountService
│   │   │           ├── booking/              # BookingRuleService, CancellationPolicyService
│   │   │           ├── bookingengine/        # BookingEngineService
│   │   │           ├── mail/                 # MailService
│   │   │           ├── rates/                # RateService
│   │   │           ├── room/                 # RoomService
│   │   │           └── structure/            # StructureService
│   │   │
│   │   └── resources/
│   │       ├── application.yaml              # Configurazione principale
│   │       ├── application-dev.yml           # Configurazione sviluppo
│   │       ├── db/
│   │       │   └── migration/                # Script Flyway (se abilitato)
│   │       ├── static/                       # File statici
│   │       └── templates/                    # Template email
│   │
│   └── test/                                 # Test unitari e di integrazione
│
├── docker-compose.yml                        # Configurazione Docker (PostgreSQL + pgAdmin)
├── pom.xml                                   # Configurazione Maven
├── hostpitami_postman_collection.json        # Postman Collection
├── hostpitami_postman_environment.json       # Postman Environment
└── README.md                                 # Questo file
```

## 🌐 API Endpoints

### Autenticazione (`/api/auth`)

| Metodo | Endpoint | Descrizione | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Registrazione nuovo utente | No |
| POST | `/api/auth/login` | Login | No |
| POST | `/api/auth/forgot-password` | Richiesta reset password | No |
| POST | `/api/auth/reset-password` | Reset password con token | No |
| GET | `/api/auth/me` | Dettagli utente corrente | Sì |

### Strutture (`/api/admin/structures`)

| Metodo | Endpoint | Descrizione | Auth |
|--------|----------|-------------|------|
| GET | `/api/admin/structures` | Lista strutture account | Sì |
| POST | `/api/admin/structures` | Crea nuova struttura | Sì |
| GET | `/api/admin/structures/{id}` | Dettaglio struttura | Sì |
| PUT | `/api/admin/structures/{id}` | Aggiorna struttura | Sì |
| DELETE | `/api/admin/structures/{id}` | Elimina struttura | Sì |
| POST | `/api/admin/structures/{id}/duplicate` | Duplica struttura | Sì |
| PUT | `/api/admin/structures/{id}/branding` | Aggiorna branding | Sì |
| PUT | `/api/admin/structures/{id}/domain` | Configura dominio | Sì |
| POST | `/api/admin/structures/{id}/website/generate` | Genera sito web | Sì |
| GET | `/api/admin/structures/{id}/website/preview` | Anteprima sito | Sì |

### Camere (`/api/admin/rooms`)

| Metodo | Endpoint | Descrizione | Auth |
|--------|----------|-------------|------|
| GET | `/api/admin/structures/{structureId}/rooms` | Lista camere | Sì |
| POST | `/api/admin/structures/{structureId}/rooms` | Crea camera | Sì |
| GET | `/api/admin/rooms/{id}` | Dettaglio camera | Sì |
| PUT | `/api/admin/rooms/{id}` | Aggiorna camera | Sì |
| DELETE | `/api/admin/rooms/{id}` | Elimina camera | Sì |

### Tariffe (`/api/admin/rates`)

| Metodo | Endpoint | Descrizione | Auth |
|--------|----------|-------------|------|
| GET | `/api/admin/structures/{structureId}/rate-plans` | Lista piani tariffari | Sì |
| POST | `/api/admin/structures/{structureId}/rate-plans` | Crea piano tariffario | Sì |
| GET | `/api/admin/rate-plans/{id}` | Dettaglio piano | Sì |
| PUT | `/api/admin/rate-plans/{id}` | Aggiorna piano | Sì |
| DELETE | `/api/admin/rate-plans/{id}` | Elimina piano | Sì |
| PUT | `/api/admin/rate-plans/{id}/rooms` | Associa camere | Sì |
| POST | `/api/admin/rate-plans/{id}/rates` | Imposta tariffe per range | Sì |
| GET | `/api/admin/rate-plans/{id}/calendar` | Calendario tariffe | Sì |

### Booking Engine (`/api/booking-engine`)

| Metodo | Endpoint | Descrizione | Auth |
|--------|----------|-------------|------|
| POST | `/api/booking-engine/{slug}/availability` | Cerca disponibilità | No |
| POST | `/api/booking-engine/{slug}/quote` | Genera preventivo | No |
| POST | `/api/booking-engine/{slug}/hold` | Blocca prenotazione | No |
| POST | `/api/booking-engine/{slug}/checkout` | Completa prenotazione | No |

### Politiche di Cancellazione (`/api/admin/cancellation-policies`)

| Metodo | Endpoint | Descrizione | Auth |
|--------|----------|-------------|------|
| GET | `/api/admin/structures/{structureId}/cancellation-policies` | Lista politiche | Sì |
| POST | `/api/admin/structures/{structureId}/cancellation-policies` | Crea politica | Sì |
| PUT | `/api/admin/cancellation-policies/{id}` | Aggiorna politica | Sì |
| DELETE | `/api/admin/cancellation-policies/{id}` | Elimina politica | Sì |

### Pagamenti (Mock) (`/api/mock-payment`)

| Metodo | Endpoint | Descrizione | Auth |
|--------|----------|-------------|------|
| POST | `/api/mock-payment/simulate` | Simula pagamento (dev) | No |

## 🗄 Modello Dati

### Entità Principali

#### Account & User
- **Account**: Account principale multi-tenant con piano e stato
- **User**: Utente del sistema con credenziali
- **AccountMember**: Associazione utente-account con ruolo
- **Role / UserRole**: Sistema di ruoli e permessi

#### Structure
- **Structure**: Struttura ricettiva (hotel, B&B, ecc.)
    - Dati base (nome, tipo, slug, contatti)
    - Indirizzo e località
    - Orari check-in/out
    - Branding (logo, colori, template)
    - Dominio personalizzato
    - SEO
    - Stato pubblicazione e generazione sito

#### Room & Rates
- **Room**: Camera/unità abitativa
    - Tipo camera (RoomType enum)
    - Descrizione e capienza
    - Servizi inclusi
- **RatePlan**: Piano tariffario
    - Nome e descrizione
    - Associazione a camere
- **RateCalendar**: Calendario tariffe giornaliere
    - Prezzo per giorno
    - Disponibilità
    - Restrizioni (min/max nights)

#### Booking
- **Booking**: Prenotazione
- **BookingRule**: Regole di prenotazione
    - Anticipo minimo
    - Permanenza minima/massima
    - Finestre di prenotazione
- **CancellationPolicy**: Politica di cancellazione
    - Tipo (flessibile, moderata, rigida)
    - Termini e condizioni

#### Guest & Payment
- **Guest**: Anagrafica ospite
    - Dati personali
    - Contatti
- **PaymentTransaction**: Transazione di pagamento
    - Integrazione Stripe
    - Stato e importi

#### Base Entities
- **BaseEntity**: Classe base con ID, timestamps (createdAt, updatedAt)
- **TenantEntity**: Estende BaseEntity con riferimento ad Account per multi-tenancy

### Relazioni Principali

```
Account (1) ──< (N) Structure
Account (1) ──< (N) AccountMember >── (1) User
Structure (1) ──< (N) Room
Structure (1) ──< (N) RatePlan
Structure (1) ──< (N) CancellationPolicy
Structure (1) ──< (N) BookingRule
RatePlan (1) ──< (N) RatePlanRoom >── (1) Room
RatePlan (1) ──< (N) RateCalendar
```

## 🔒 Sicurezza

### Autenticazione JWT

Il sistema utilizza JWT (JSON Web Tokens) per l'autenticazione stateless:

1. **Login**: L'utente invia credenziali a `/api/auth/login`
2. **Token Generation**: Il server genera un JWT contenente:
    - User ID
    - Account ID
    - Email
    - Ruoli
    - Scadenza
3. **Request Authentication**: Il client include il token nell'header:
   ```
   Authorization: Bearer <token>
   ```
4. **Token Validation**: Il `JwtAuthenticationFilter` valida ogni richiesta

### Password Security
- Hash con **BCrypt** (strength configurabile)
- Password reset con token temporaneo
- Validazione robustezza password (implementare se necessario)

### Multi-tenancy Security
- Ogni richiesta è isolata per Account tramite `TenantEntity`
- Query filtrate automaticamente per `accountId`
- Impossibilità di accesso cross-tenant

### CORS
Configurare CORS in produzione per limitare origini consentite.

### Rate Limiting
Implementare rate limiting per API pubbliche (es. booking engine) per prevenire abusi.

## 🗃 Database

### PostgreSQL con Docker Compose

Il progetto utilizza PostgreSQL 16 tramite Docker Compose. La configurazione è già pronta nel file `docker-compose.yml`:

```yaml
services:
  postgres:
    image: postgres:16
    container_name: hostpitami-postgres
    restart: unless-stopped
    environment:
      POSTGRES_DB: hostpitami
      POSTGRES_USER: hostpitami
      POSTGRES_PASSWORD: hostpitami
    ports:
      - "5432:5432"
    volumes:
      - hostpitami_pgdata:/var/lib/postgresql/data

  pgadmin:
    image: dpage/pgadmin4:8
    container_name: hostpitami-pgadmin
    restart: unless-stopped
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@hostpitami.com
      PGADMIN_DEFAULT_PASSWORD: admin
    ports:
      - "5050:80"
    depends_on:
      - postgres
```

### Gestione Schema

- **Hibernate DDL**: In sviluppo è impostato su `update` per creare/aggiornare automaticamente lo schema
- **Flyway**: Disponibile ma disabilitato di default. Consigliato per produzione.

### Migrazioni

Per gestire le migrazioni in modo controllato:

1. Abilita Flyway in `application.yml`:
   ```yaml
   spring:
     flyway:
       enabled: true
   ```

2. Crea script in `src/main/resources/db/migration/`:
   ```sql
   -- V1__init_schema.sql
   -- V2__add_bookings.sql
   ```

3. Le migrazioni verranno eseguite automaticamente all'avvio

### Gestione Database con pgAdmin

1. Apri `http://localhost:5050`
2. Login: `admin@hostpitami.com` / `admin`
3. Aggiungi server:
    - Host: `postgres`
    - Port: `5432`
    - Database: `hostpitami`
    - Username: `hostpitami`
    - Password: `hostpitami`

### Backup e Restore

**Backup da container Docker:**
```bash
docker exec -t hostpitami-postgres pg_dump -U hostpitami hostpitami > backup.sql
```

**Restore:**
```bash
cat backup.sql | docker exec -i hostpitami-postgres psql -U hostpitami -d hostpitami
```

### Comandi Utili

**Accesso alla console PostgreSQL:**
```bash
docker exec -it hostpitami-postgres psql -U hostpitami -d hostpitami
```

**Visualizza log del database:**
```bash
docker logs hostpitami-postgres
```

**Reset completo del database:**
```bash
docker-compose down -v
docker-compose up -d
```

## 🧪 Testing

### Eseguire i Test

```bash
mvn test
```

### Struttura Test
- **Unit Tests**: Test delle singole classi (Service, Repository)
- **Integration Tests**: Test degli endpoint e integrazione con database
- **Test Database**: Utilizzare profilo `test` con H2 in-memory o Testcontainers

### Coverage
Generare report di coverage:
```bash
mvn jacoco:report
```

Il report sarà disponibile in `target/site/jacoco/index.html`

## 🚢 Deployment

### Preparazione Build

```bash
mvn clean package -DskipTests
```

Il JAR verrà creato in `target/hostpitami-backend-{version}.jar`

### Esecuzione JAR

```bash
java -jar target/hostpitami-backend-{version}.jar \
  --spring.profiles.active=prod \
  --DB_HOST=production-db \
  --JWT_SECRET=production-secret
```

### Deploy su Server

#### 1. Configurazione Systemd (Linux)

Crea `/etc/systemd/system/hostpitami.service`:

```ini
[Unit]
Description=Hostpitami Backend
After=syslog.target network.target

[Service]
User=hostpitami
WorkingDirectory=/opt/hostpitami
ExecStart=/usr/bin/java -jar /opt/hostpitami/hostpitami-backend.jar
SuccessExitStatus=143
StandardOutput=journal
StandardError=journal
SyslogIdentifier=hostpitami

Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="DB_HOST=localhost"
Environment="JWT_SECRET=your-secret"

[Install]
WantedBy=multi-user.target
```

Avvia il servizio:
```bash
sudo systemctl enable hostpitami
sudo systemctl start hostpitami
sudo systemctl status hostpitami
```

#### 2. Deploy con Docker

**Dockerfile:**
```dockerfile
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

Build e run:
```bash
docker build -t hostpitami-backend .
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=postgres \
  -e JWT_SECRET=secret \
  hostpitami-backend
```

#### 3. Deploy su Cloud

**AWS Elastic Beanstalk:**
```bash
eb init
eb create hostpitami-prod
eb deploy
```

**Heroku:**
```bash
heroku create hostpitami-backend
git push heroku main
```

**Google Cloud Run:**
```bash
gcloud run deploy hostpitami --source .
```

### Nginx Reverse Proxy

Configurazione Nginx per proxy l'app:

```nginx
server {
    listen 80;
    server_name api.hostpitami.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### SSL/TLS

Utilizza Let's Encrypt con Certbot:
```bash
sudo certbot --nginx -d api.hostpitami.com
```

## 📝 Best Practices

### Sviluppo
- Usa branch feature per nuove funzionalità
- Scrivi test per logica di business critica
- Mantieni i DTO separati dalle entity
- Valida sempre l'input utente
- Logga in modo strutturato (usa SLF4J)

### Database
- Non usare `ddl-auto=update` in produzione
- Usa Flyway per migrazioni controllate
- Crea indici su colonne frequentemente interrogate
- Usa connection pooling (HikariCP già incluso)

### Sicurezza
- **MAI** committare segreti nel codice
- Usa variabili d'ambiente per credenziali
- Cambia JWT_SECRET in produzione
- Implementa rate limiting
- Valida e sanitizza input utente
- Usa HTTPS in produzione

### Performance
- Usa paginazione per liste grandi
- Implementa caching (Redis/Caffeine)
- Monitora query N+1
- Usa lazy loading dove appropriato
- Profila regolarmente l'applicazione

## 🤝 Contributing

1. Fork il progetto
2. Crea un branch feature (`git checkout -b feature/AmazingFeature`)
3. Commit le modifiche (`git commit -m 'Add some AmazingFeature'`)
4. Push al branch (`git push origin feature/AmazingFeature`)
5. Apri una Pull Request

### Convenzioni Codice
- Segui le convenzioni Java standard
- Usa Lombok per ridurre boilerplate
- Commenta logica complessa
- Mantieni metodi sotto 50 righe
- Un concetto = una classe

## 📄 Licenza

[Specificare la licenza del progetto]

## 👥 Team

[Informazioni sul team di sviluppo]

## 📞 Supporto

Per domande o supporto:
- **GitHub Issues**: [https://github.com/TanaseSebastian/hostpitami-backend/issues](https://github.com/TanaseSebastian/hostpitami-backend/issues)
- **Repository**: [https://github.com/TanaseSebastian/hostpitami-backend](https://github.com/TanaseSebastian/hostpitami-backend)

## 🗺 Roadmap

- [ ] Implementare notifiche email transazionali
- [ ] Aggiungere sistema di reportistica
- [ ] Integrare channel manager esterni
- [ ] Implementare API webhooks
- [ ] Aggiungere supporto multi-lingua
- [ ] Dashboard analytics avanzate
- [ ] Integrazione pagamenti alternativi (PayPal, etc.)

---

**Repository**: [https://github.com/TanaseSebastian/hostpitami-backend](https://github.com/TanaseSebastian/hostpitami-backend)

Sviluppato con ❤️ per semplificare la gestione delle strutture ricettive.