# Angels Burger POS & Inventory System

A robust, JavaFX-based Point of Sale (POS) and Inventory Management system designed for fast-food operations. This application bridges the gap between traditional desktop cashier operations and modern mobile ordering queues, featuring "Smart Inventory" logic and automated sales tracking.

## Table of Contents
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Architecture](#project-architecture)
- [Installation & Setup](#installation--setup)
- [Configuration & Data](#configuration--data)
- [Mobile Kiosk API](#mobile-kiosk-api)
- [Contributors](#contributors)

---

## Features

### Point of Sale (Desktop)
* **Visual Menu:** Image-based menu selection with category filtering (Burgers, Sandwiches, Add-ons).
* **Smart Stock Display:** Real-time calculation of how many items can be made based on current inventory.
* **Customization:** Ability to customize orders (e.g., "No Mayo") without affecting stock deduction logic for excluded items.
* **Real-time Change Calculator:** Dynamic change calculation during checkout.
* **Receipt Generation:** Automated text-based receipt generation upon payment.

### Smart Inventory Management
* **Recipe-Based Deduction:** Selling a burger automatically deducts specific ingredients (e.g., 1 Bun, 1 Patty, 1 Wrapper).
* **Critical vs. Optional Logic:** The system distinguishes between Critical ingredients (Meat, Buns) and Optional ones (Sauce, Veggies).
    * *Critical Missing:* Product is marked "Out of Stock."
    * *Optional Missing:* Product remains available, allowing sales to continue.
* **Low Stock Alerts:** Visual warning button appears when ingredients dip below threshold levels.
* **Restocking & Archives:** Admin interface for adding stock and archiving discontinued ingredients.

### Mobile Ordering Integration
* **Local Kiosk Server:** Integrated Javalin server to accept incoming orders via HTTP/REST.
* **Kitchen Queue:** Real-time visualization of incoming mobile orders.
* **Order Status Lifecycle:** PENDING -> PENDING_PAYMENT -> PREPARING -> READY.
* **Payment Integration:** Mobile orders are finalized through the standard cashier checkout flow to ensure sales logging.

### Admin & Analytics
* **Role-Based Access Control:** Distinct views and capabilities for Admins vs. Cashiers.
* **Sales Reporting:** Automated CSV logging of all transactions.
* **Dynamic Filtering:** Filter revenue by Daily, Weekly, Monthly, or Yearly periods.
* **Employee Management:** Add or remove system users.

---

## Technology Stack

* **Language:** Java 17+
* **GUI Framework:** JavaFX (FXML)
* **Server Framework:** Javalin (for Mobile Kiosk API)
* **JSON Processing:** GSON
* **Persistence:** File-based storage (TXT/CSV) for portability and simplicity.

---

## Project Architecture

The project follows a standard MVC (Model-View-Controller) pattern with a Service layer:

* **controller:** Handles UI logic (Dashboard, Checkout, Inventory, Sales).
* **model:** POJOs representing data (Product, Ingredient, SaleRecord, User).
* **service:** Business logic (TransactionService, ProductService, KioskServer).
* **util:** Helpers for file I/O, configuration paths, and logging.
* **DAO:** Data Access Objects for reading text databases.

---

## Installation & Setup

1.  **Prerequisites:**
    * Java Development Kit (JDK) 17 or higher.
    * Maven or Gradle (dependency management).
    * JavaFX SDK.

2.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/yourusername/angels-burger-pos.git](https://github.com/yourusername/angels-burger-pos.git)
    ```

3.  **Run the Application:**
    * Open the project in IntelliJ IDEA (recommended) or Eclipse.
    * Locate the main class (e.g., `HelloApplication.java` or `Main.java`).
    * Run the main method.

4.  **Login Credentials (Default):**
    * **Admin:** `admin` / `admin123`
    * **Cashier:** `cashier1` / `1234`

---

## Configuration & Data

The system uses local text files located in `src/main/resources/org/example/angelsburgerpos/util/` for data persistence. **Do not delete these files.**

| File | Description | Format |
| :--- | :--- | :--- |
| `ingredients.txt` | Stock levels & Critical status | `ID,Name,Stock,Unit,IsCritical` |
| `recipes.txt` | Ingredient mapping per product | `ProdID,IngID:Qty,IngID:Qty...` |
| `product-database.txt` | Menu items & prices | `ID,Name,Price,Image,Category,BaseStock` |
| `users.txt` | Authorized login credentials | `Username,Password,Role,FullName` |
| `archives.txt` | Log of deleted ingredients | `Date,ID,Name,Stock,Unit,Reason` |

### Sales Logs
Sales logs are automatically generated in the `sales_logs/` directory in the project root.
* **Format:** `sales_YYYY-MM-DD.csv`
* **Data:** `Date, Time, Amount, CashierName`

---

## Mobile Kiosk API

The application starts a local server to listen for mobile orders.

**Endpoint:** `POST /api/order`

**Body (JSON):**
```json
[
  { "productId": "B01", "quantity": 2 },
  { "productId": "S04", "quantity": 1 }
]
```
## Contributors

* **Prince Olaguera** - Lead Developer / Admin
* **Rowena Acabado** - Contributor
* **James Agmata** - Contributor
* **Japhet Mondigo** - Contributor
* **Ayende** - Contributor

---

**Note:** This project was developed for educational purposes/internal business logic demonstration. Ensure all file paths in `AppConfig.java` match your local environment structure.
