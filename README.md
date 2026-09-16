# Money tracking Backend
Backend for money tracking app

## Description

The Money Tracking App is a platform for personal finance management. It Allows users to register, authenticate, manage their accounts and payment methods, categorized them and record income and expenses, set budgets besides tracking financial history and generate reports.

## Technology stack

The technology stack to be used for this app consist of:

1. **Spring Boot Backend**: REST/JSON API using a PostgreSQL Database.
2. **Angular web app**: communicates with the backend. To be used when online.
3. **Flutter mobile app**: caches data in SQLite, still works offline, syncs with backend when online.

## Backend Architecture

### Key Architectural decisions

1. Spring Boot as this project backend: Spring Boot is a mature framework, it counts with features such as dependency injection, provides features for using ORM, DB migrations, testing, MVC, Swagger, security configuration, etc. It also counts with abundant documentation, community support, conventions over project structure to help depelopers in their inmersion in the framework.
2. PostgreSQL: when thinking in future it provides better endurance for large amounts of data, good performance and integration with Spring.
3. Flyway: for database migrations and versioning of database schema 
4. Authentication: use of JWT tokens
5. Standard layered architecture:  controller/, service/, repository/, entity/, dto/, config/, security/, exception/

### Design structure decisions

- Normalize database by using ref_categories and ref_items tables for domains such as categories, types, periods, object/entities types, etc.
- Besides using JWT tokens, include sessions stored in database to be able to handle token invalidation (before they expire) and logout of devices
- Using of filters for cross cutting functions such as token validation, token header attachment, email verification, etc
- **`current_balance`** in accounts is derived from **`initial_balance`** and a transaction(income or expense)
- **`history_log.previous_value` and `new_value`** stored as json values to make easy to show them in reports
- **`categories.is_default`** to identify the default categories without creating an extra table for this
- **`users.email_verified`** to be implemented in a filter to intercept all apis but auth ones

### Error Handling

A single filter **`GlobalExceptionHandler`** to handle all exceptions to avoid repetition in each service, endpoint, etc. Handle different scenarios by emiting especific HTTP code status and body responses.

