# Inventory OBS – Java Spring Boot Application

Simple inventory API built as part of the Java Backend Test from OBS Solution.  
The application manages **Items**, **Inventories**, and **Orders** using **H2 database** and **Spring Boot**.

---

## Tech Stack

- Java 17+
- Spring Boot 4 (Web, Data JPA, Validation)
- H2 Database (file based)
- JUnit 5 & Mockito (unit tests)
- Maven

---

## Business Rules

### 1. Items
- Master data for products.
- Fields: `id`, `name`, `price`.
- Item price is the **single source of truth**.
- On **Order create / update**, the `price` of the order is always taken from `Item.price` (not from client input).

### 2. Inventory (Stock Ledger)

Inventory is treated as a **transaction ledger**, not a mutable stock table.

- Fields: `id`, `itemId`, `qty`, `type`.
- `type`:
  - `T` = **Top Up** → stock increases
  - `W` = **Withdrawal** → stock decreases
- Each inventory record is an immutable movement.  
  To adjust stock, a **new record** is added instead of updating an existing one.
- This design keeps the stock history consistent and auditable.

> Because of this, an “update inventory” operation is conceptually wrong.  
> The API exposes a PUT endpoint to fulfil CRUD requirement, but the recommended usage is **create-only** for inventory.

### 3. Stock Calculation

`StockServiceImpl#getRemainingStock(itemId)`:

```text
remainingStock = topUp(T) - withdraw(W) - orderedQty
```

---

## API Overview

Base URL:
```text
http://localhost:9000/api/v1/
```

### 1. Items

- GET /items – Paged list with optional filters: id and name
- GET /items/{id} – Get single item
- POST /items/create – Create item
- PUT /items/update – Update item
- DELETE /items/delete/{id} – Delete item

### 2. Inventories

- GET /inventories – Paged list with filters: id, itemId, type
- GET /inventories/{id} – Get single inventory record
- POST /inventories/create – Create inventory movement (T or W)
- PUT /inventories/update – Update inventory (not recommended in real usage; inventory should be append-only)
- DELETE /inventories/delete/{id} – Delete inventory record

### 3. Orders

- GET /orders – Paged list with filters: orderNo, itemId
- GET /orders/{orderNo} – Get single order
- POST /orders/create – Create order
- PUT /orders/update – Update order (revalidates stock)
- DELETE /orders/delete/{orderNo} – Delete order
