[README.md](https://github.com/user-attachments/files/24127778/README.md)
# 💰 Expense Tracker

A full-stack web application for tracking and managing personal expenses. Built with Spring Boot and modern JavaScript, featuring a clean, responsive interface and comprehensive expense management capabilities.

## ✨ Features

- **Expense Management** - Add, edit, and delete expenses with ease
- **Smart Categorization** - Organize expenses by categories (Food, Transport, Entertainment, etc.)
- **Date Filtering** - Filter expenses by date range and category
- **Financial Insights** - View total spending and monthly summaries
- **Modern UI** - Clean, colorful interface with smooth animations
- **Real-time Updates** - Instant feedback and data synchronization
- **Responsive Design** - Works seamlessly across different screen sizes

## 🛠️ Tech Stack

**Backend:**
- Java 21
- Spring Boot 4.0.0
- Spring Data JPA
- H2 Database (development)
- PostgreSQL/MySQL ready (production)
- Maven

**Frontend:**
- HTML5
- CSS3 (Grid, Flexbox, Animations)
- Vanilla JavaScript (ES6+)
- Fetch API

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6+
- Modern web browser

## 🚀 Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/expense-tracker.git
   cd expense-tracker
   ```

2. **Navigate to backend folder**
   ```bash
   cd backend
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - Open `frontend/index.html` in your web browser
   - API will be running at `http://localhost:8080`

## 📖 Usage

### Adding an Expense
1. Select a category from the dropdown
2. Enter the amount and description
3. Choose the date
4. Click "Add Expense"

### Filtering Expenses
- Use the date range picker to filter by period
- Select a category to view specific expense types
- Click "Reset" to clear all filters

### Managing Expenses
- Click the edit icon to modify an expense
- Click the delete icon to remove an expense
- All changes are saved immediately

## 🏗️ Architecture

The application follows a layered architecture pattern:

```
┌─────────────────────────────────┐
│     Frontend (HTML/CSS/JS)      │
└──────────────┬──────────────────┘
               │ REST API
┌──────────────▼──────────────────┐
│      Controllers Layer          │
├─────────────────────────────────┤
│       Services Layer            │
├─────────────────────────────────┤
│     Repositories Layer          │
├─────────────────────────────────┤
│      Entities/Models            │
└──────────────┬──────────────────┘
               │
         ┌─────▼─────┐
         │  Database │
         └───────────┘
```

## 📊 Database Schema

```sql
categories
├── id (PK)
├── name (unique)
├── color
└── created_at

expenses
├── id (PK)
├── amount
├── description
├── category_id (FK → categories.id)
├── expense_date
├── created_at
└── updated_at
```

## 🔌 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/categories` | Get all categories |
| GET | `/api/expenses` | Get all expenses |
| POST | `/api/expenses` | Create new expense |
| PUT | `/api/expenses/{id}` | Update expense |
| DELETE | `/api/expenses/{id}` | Delete expense |
| GET | `/api/expenses/summary` | Get spending summary |

## ⚙️ Configuration

The application uses H2 in-memory database by default. To switch to PostgreSQL or MySQL:

1. Update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/expensedb
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

2. Add the database driver dependency in `pom.xml`

## 🧪 Testing

Run the test suite:
```bash
mvn test
```

## 📦 Building for Production

Create a production build:
```bash
mvn clean package
```

The JAR file will be available in `target/` directory.

## 🚀 Deployment

The application can be deployed to:
- Heroku
- Railway
- AWS Elastic Beanstalk
- Google Cloud Platform
- Any platform supporting Spring Boot applications

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is open source and available under the MIT License.

## 📧 Contact

Telegram - @extziz

Email - katherinemalakhova@gmail.com

Project Link: [https://github.com/extziz/expense-tracker]

---

Built with ❤️ using Spring Boot and JavaScript
