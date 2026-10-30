# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

JavaFX desktop POS + inventory app ("Angels Burger POS", Maven artifact `AngelsBurgerPOS`, package `org.example.angelsburgerpos`) that embeds a Javalin HTTP server so diners can order from their phones. The repo is named SeatServe; the code and README still use the old name.

## Commands

```bash
./mvnw clean javafx:run      # run the app (main class set in pom's javafx-maven-plugin)
./mvnw clean compile         # build only
./mvnw test                  # there is no src/test yet; JUnit 5 is on the classpath
./mvnw test -Dtest=ClassName#method   # single test, once tests exist
```

- `pom.xml` sets compiler `source`/`target` to **23**. The local JDK may be older (21 was seen in this environment), which fails the build until it is lowered or JDK 23 is installed.
- There is no linter configured.
- Run from the project root. Several paths are relative to the working directory (see below).

## Architecture

Layers: `controller` (JavaFX/FXML) → `service` → `util`/`DAO` → plain text files. FXML views live in `src/main/resources/org/example/angelsburgerpos/` and each is bound to a controller in `controller/`. `module-info.java` is in use, so a new package that FXML or Gson reflects into needs an `opens` entry.

**Startup:** `AngelsBurgerPOS` loads `login-view.fxml`. `DashboardController` (the largest file) is the hub. It creates `ProductServiceImp` and starts `KioskServer` itself in `startKioskServer()`.

**Mobile ordering flow** (spans several files):
- `KioskServer` (Javalin, port 5050) serves the phone UI from `src/main/resources/webapp/` and exposes `GET /api/menu`, `POST /api/order`, `GET /api/order/{id}/status`.
- `POST /api/order` builds a `MobileOrder` with status `PENDING_PAYMENT` and hands it to `DashboardController.addPendingOrder` via `Platform.runLater`. Javalin threads must not touch JavaFX state directly.
- The cashier then finalizes it through the normal checkout flow, which is what writes the sales log. Status lifecycle: PENDING → PENDING_PAYMENT → PREPARING → READY.
- Order IDs come from a static in-memory counter and reset on restart. Order state is not persisted.

**Inventory ("smart stock")** is `util/InventoryManager`, a static singleton holding all state:
- `ingredients.txt` holds stock, `recipes.txt` maps product → `ingredient:qty` pairs.
- A product is sellable if every *critical* ingredient has enough stock. Missing optional ingredients (sauce, veggies) don't block a sale.
- Customizations such as "No Mayo" skip deduction for the excluded ingredient.
- `setRefreshListener` is how the UI gets notified when stock changes.

**Persistence is flat files, with two different access modes** (all paths in `util/AppConfig`):
- Filesystem paths relative to the CWD, for data that is *written*: `users.txt`, `ingredients.txt`, `archives.txt` under `src/main/resources/org/example/angelsburgerpos/util/`. Writes go into the source tree, not `target/`, so they only work when run from the project root.
- Classpath resources, read-only: `recipes.txt` and `product-database.txt`. Editing them needs a rebuild, since `target/` holds a copy.
- Sales logs: `sales_logs/sales_YYYY-MM-DD.csv` (one file per day, written by `SalesLogger`). Receipts go to `receipts/`.
- Records are comma-separated, and the formats are in the README's data table. A comma inside a field will corrupt a file.

## Gotchas

- `users.txt` stores plaintext passwords, and the README lists default credentials. Don't copy real credentials into either.
- `webapp/images/` holds the phone-menu images, keyed by product ID. A missing image falls back to `placeholder.png`, which is not in the repo.
- The README's clone URL is a placeholder from the original project.
