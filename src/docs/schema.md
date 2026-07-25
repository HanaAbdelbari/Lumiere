# Lumière — Database Schema (V1)

Accessories e-commerce store. Egypt only, EGP. Guest checkout, 50% deposit
via Vodafone Cash confirmed over WhatsApp.

**Stack:** Next.js + Spring Boot + PostgreSQL (Neon) + Cloudinary.
**Migrations:** Flyway. The SQL lives in `V1__create_tables.sql`.

Six tables: `category`, `product`, `product_image`, `orders`, `order_item`,
`customer_info`.

---

## Core design decisions

These are the principles the schema is built on. They explain *why* the tables
look the way they do.

**Single source of truth.** Anything that can be computed is never stored.
A product is "on sale" when `sale_price IS NOT NULL AND sale_price < price` —
there is no `on_sale` flag to fall out of sync. The discount percentage is
computed in the frontend, never stored.

**Frozen order history.** An order must stay exactly as it was at purchase
time, even if products or prices change later. So `order_item.unit_price`,
`orders.total_amount`, and `orders.deposit_amount` are stored snapshots, not
looked up live. This is the one deliberate exception to "don't store what you
can compute" — because an order reflects a *past moment*, not current state.

**Soft delete.** Products are never physically deleted while linked to orders.
Setting `is_active = false` hides a product from the site while keeping it
intact for historical orders.

**Money is DECIMAL, never float.** All money columns are `DECIMAL(10,2)`
(`BigDecimal` in Java). Floats cause rounding errors that are unacceptable for
currency.

**Status as VARCHAR + CHECK**, not a Postgres ENUM type — maps cleanly to
Spring Boot `@Enumerated(EnumType.STRING)` and is easy to extend in a later
migration.

**`updated_at` is set by the app, not the database.** PostgreSQL does not
auto-update this column. It is refreshed by Hibernate via `@UpdateTimestamp`
on the entity — not by a database trigger or default.

---

## 1. category

| Column       | Type         | Constraints          | Notes             |
| ------------ | ------------ | -------------------- | ----------------- |
| id           | BIGINT       | PK, auto-increment   | Internal ID       |
| name         | VARCHAR(100) | NOT NULL             | Display name      |
| slug         | VARCHAR(120) | NOT NULL, UNIQUE     | URL slug (SEO)    |
| cover_image  | VARCHAR(500) | nullable             | Category banner   |
| created_at   | TIMESTAMP    | NOT NULL, default now |                  |

- `slug` is what appears in the URL (`/shop/rings`).
- Reserved slugs `all` and `sale` are blocked **in the backend**, because they
  collide with the `/shop/all` and `/shop/sale` routes.

---

## 2. product

| Column         | Type          | Constraints                | Notes                  |
| -------------- | ------------- | -------------------------- | ---------------------- |
| id             | BIGINT        | PK, auto-increment         |                        |
| category_id    | BIGINT        | NOT NULL, FK → category(id)|                        |
| name           | VARCHAR(150)  | NOT NULL                   |                        |
| slug           | VARCHAR(180)  | NOT NULL, UNIQUE           | SEO-friendly URL       |
| description    | TEXT          | nullable                   |                        |
| price          | DECIMAL(10,2) | NOT NULL, CHECK > 0        | Current price          |
| sale_price     | DECIMAL(10,2) | nullable, CHECK < price    | Source of truth for sale |
| material       | VARCHAR(100)  | nullable                   | Dropdown in admin      |
| size           | VARCHAR(50)   | nullable                   | Rings, bracelets       |
| chain_length   | VARCHAR(50)   | nullable                   | Necklaces              |
| stock_quantity | INT           | NOT NULL, default 0, CHECK >= 0 |                   |
| display_order  | INT           | NOT NULL, default 0        | Shop ordering          |
| is_active      | BOOLEAN       | NOT NULL, default true     | Soft delete            |
| created_at     | TIMESTAMP     | NOT NULL, default now      |                        |
| updated_at     | TIMESTAMP     | NOT NULL, default now      |                        |

- **On sale** = `sale_price IS NOT NULL AND sale_price < price` (computed).
- `material` / `size` / `chain_length` are optional and only shown on the
  product page when present. A necklace has no `size`; a ring has no
  `chain_length`.
- No `image_url` here — all images live in `product_image`.

---

## 3. product_image

| Column        | Type         | Constraints              | Notes            |
| ------------- | ------------ | ------------------------ | ---------------- |
| id            | BIGINT       | PK, auto-increment       |                  |
| product_id    | BIGINT       | NOT NULL, FK → product(id) |                |
| image_url     | VARCHAR(500) | NOT NULL                 | Cloudinary URL   |
| display_order | INT          | NOT NULL, default 0      | 0 = main image   |

- The image with the lowest `display_order` (0) is the **main image** shown in
  Shop, Sale, Cart, Best Sellers, and New Arrivals.
- One-to-many: a product can have one or many images.

---

## 4. orders

Named `orders` (plural) because `order` is a reserved SQL keyword.

| Column          | Type          | Constraints          | Notes                       |
| --------------- | ------------- | -------------------- | --------------------------- |
| id              | BIGINT        | PK, auto-increment   | Internal ID                 |
| order_number    | VARCHAR(30)   | NOT NULL, UNIQUE     | Customer-facing (`LUM-...`) |
| status          | VARCHAR(30)   | NOT NULL, CHECK      | See status values below     |
| total_amount    | DECIMAL(10,2) | NOT NULL, CHECK > 0  | Frozen at order time        |
| deposit_amount  | DECIMAL(10,2) | NOT NULL, CHECK >= 0 | Actual deposit value        |
| deposit_paid_at | TIMESTAMP     | nullable             | When customer says paid     |
| confirmed_at    | TIMESTAMP     | nullable             | When admin confirms         |
| created_at      | TIMESTAMP     | NOT NULL, default now |                            |

**Status values** (enforced by CHECK constraint):

```
PENDING_DEPOSIT → DEPOSIT_UNDER_REVIEW → CONFIRMED → PREPARING → SHIPPED → DELIVERED
                          ↓
                  CANCELLED / DEPOSIT_REJECTED
```

- `confirmed_at` is when the 24-hour refund window starts.

---

## 5. order_item

| Column     | Type          | Constraints              | Notes                    |
| ---------- | ------------- | ------------------------ | ------------------------ |
| id         | BIGINT        | PK, auto-increment       |                          |
| order_id   | BIGINT        | NOT NULL, FK → orders(id)|                          |
| product_id | BIGINT        | NOT NULL, FK → product(id)|                         |
| quantity   | INT           | NOT NULL, CHECK > 0      |                          |
| unit_price | DECIMAL(10,2) | NOT NULL, CHECK > 0      | **Price frozen at purchase** |

- `unit_price` is copied from the product **at purchase time**: `sale_price` if
  the product was on sale, otherwise `price`. It never changes afterwards.

---

## 6. customer_info

| Column      | Type         | Constraints                    | Notes            |
| ----------- | ------------ | ------------------------------ | ---------------- |
| id          | BIGINT       | PK, auto-increment             |                  |
| order_id    | BIGINT       | NOT NULL, UNIQUE, FK → orders(id) | one-to-one    |
| full_name   | VARCHAR(150) | NOT NULL                       |                  |
| phone       | VARCHAR(20)  | NOT NULL                       | Links customer to order |
| governorate | VARCHAR(50)  | NOT NULL                       | Egyptian governorate |
| address     | TEXT         | NOT NULL                       |                  |
| notes       | TEXT         | nullable                       |                  |

- `UNIQUE` on `order_id` enforces the one-to-one relationship: each order has
  exactly one customer_info.

---

## Relationships

```
category  1───∞  product  1───∞  product_image

orders    1───∞  order_item  ∞───1  product
orders    1───1  customer_info
```

---

## Categories (seed data)

Rings, Necklaces, Bracelets, Bangles, Earrings, Sets, Hand Chains, Watches,
Organisers.

> Only create categories you actually have products in. The dynamic
> `/shop/[category]` route means new categories appear automatically once
> added — no empty categories for show.

---

## Not in V1 (deferred, no schema rebuild needed to add later)

User accounts, order tracking page, online payment gateway, wishlist (backend),
reviews, coupons, email notifications, analytics, Redis.