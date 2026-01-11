# 🛍️ Boutique Retail Management Suite

A cohesive retail management platform for a modern accessories boutique that sells bags, jewelry, and makeup. The solution pairs a Spring Boot API with a JavaFX desktop client to keep store operations, inventory, and customer orders perfectly in sync.

## 🏗️ Architecture

### Backend (Spring Boot API)
- **Framework**: Spring Boot 3.5.5 with Java 17
- **Database**: PostgreSQL with JPA/Hibernate
- **Security**: JWT authentication (inherited from original system)
- **Architecture**: Clean separation with Controllers → Services → Repositories

### Frontend (JavaFX Desktop Client)
- **Framework**: JavaFX 17 with FXML
- **Architecture**: MVC pattern with controllers, models, and views
- **Communication**: RESTful API with JSON payloads

## 📊 Data Model

### Core Entities
- **User**: Store staff with secure logins
- **Customer**: Shopper profile with preferred contact
- **Product**: Boutique inventory (bags, jewelry, makeup) with pricing and stock
- **Order**: Sales orders with retail-specific statuses (Pending, Paid, Fulfilled, Cancelled)
- **OrderItem**: Snapshot of purchased items, including unit price at checkout

### Key Relationships
- Orders belong to Customers
- Orders contain multiple OrderItems
- OrderItems reference Products and capture pricing at the time of sale

## 🚀 Features

### Backend Services
- **Customer Management**: Shopper onboarding and updates
- **Product Management**: Inventory by curated categories (handbags, jewelry, makeup, accessories)
- **Order Management**: Order lifecycle with generated order codes
- **Order Item Management**: Stock-aware item handling per order

### Frontend Views
1. **Retail Dashboard**: Highlights totals, pending/fulfilled orders, inventory value, low-stock alerts, and recent sales
2. **Product Management**: CRUD operations for boutique inventory with category filtering
3. **Order Management**: Create orders, attach items, fulfill orders, and print invoices
4. **Customer Management**: Maintain customer directory for fast checkout

## 🛠️ Technical Implementation

### Backend Components
- **Entities**: JPA entities with proper relationships and validation
- **Repositories**: Spring Data JPA repositories with custom queries
- **Services**: Business logic with transaction management
- **Controllers**: RESTful API endpoints with error handling
- **DTOs**: Data transfer objects for API communication

### Frontend Components
- **Models**: DTOs aligned with updated API fields (order codes, placedAt timestamps, unit prices)
- **API Clients**: Typed REST clients per resource
- **Controllers**: JavaFX controllers orchestrating UI + API calls
- **FXML**: Clean retail-oriented layouts
- **Styling**: Soft boutique palette with modern typography

## 🔧 Configuration


### API Endpoints
- `/api/auth/**` - Authentication and OTP flow
- `/api/customers` - Customer management
- `/api/products` - Inventory management
- `/api/orders` - Order lifecycle
- `/api/orders/{id}/items` - Order item operations

## 🚀 Running the Application

### Backend (Spring Boot)
```bash
cd csis231-api
mvn spring-boot:run
```

### Frontend (JavaFX)
```bash
cd demo
mvn javafx:run
```

## 📱 User Interface

### Dashboard
- Real-time statistics (total, pending, fulfilled orders, inventory value, low stock count)
- Quick navigation to orders, inventory, and customers
- Recent orders table with order codes and totals
- Low stock alert table for fast reordering

### Product Management
- CRUD operations for curated product catalog
- Stock tracking with boutique-specific categories
- Price management with validation

### Order Management
- Create new orders tied to customers with optional custom order codes
- Add/remove items while stock is automatically adjusted
- Enforce retail statuses (Pending → Paid → Fulfilled)
- Print boutique-branded invoices

### Customer Management
- Shopper directory with quick search
- Centralized contact management for follow-ups

### Fulfillment & Customer Care
- Status-aware order completion flow
- Invoice printing and history lookups
- Customer communication touchpoints

## 🎨 Design Features

### Modern UI
- Clean retail aesthetic with neutral palette
- Responsive card-based layout
- Intuitive navigation focused on store workflows

### User Experience
- Real-time data refresh
- Contextual actions and confirmations
- Clear error messaging for staff

## 🔒 Security

- Inherits JWT authentication from the core auth service
- Role-based access control
- Secure REST communication
- Input validation and sanitization on all entry points

## 📈 Business Logic

### Order Management
- Stock validation before item addition
- Automatic stock decrement/rollback during edits
- Status enforcement to prevent modifying fulfilled/cancelled orders

### Inventory Management
- Real-time stock insight
- Low stock detection with configurable threshold
- Product availability filtering for POS workflows

## 🚀 Future Enhancements

- Integrated payment tracking
- Advanced sales and inventory analytics
- Supplier integrations and automated purchase orders
- Multi-store synchronization
- Mobile-friendly dashboards

## 📋 Requirements

- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- JavaFX 17+

This boutique management suite delivers everything a specialty retail shop needs to operate smoothly—from curated inventory tracking to order fulfillment and customer care—inside a polished, unified experience.
