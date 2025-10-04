# Flow Pocket Android App - Core Documentation

## App Overview

**Flow Pocket** is a personal finance tracking Android application built with Java and Room Database. The app helps users manage their finances by tracking income, expenses, borrowed/lent money, and providing visual analytics.

### Core Features
- ✅ Expense tracking with categorization
- ✅ Monthly income management
- ✅ Real-time balance calculation (income - expenses)
- ✅ Borrowed & Lent money tracking
- ✅ Visual analytics with pie charts
- ✅ Month/Year navigation for historical data
- ✅ Export/Import functionality for data backup
- ✅ Navigation drawer with dark theme
- ✅ Swipeable card interface (Recent Expenses, Borrowed, Lent)
- 🚧 Budget management (planned for v2.0)
- 🚧 Enhanced statistics & analytics (planned for v2.0)

## Architecture & Tech Stack

### Architecture Pattern
- **MVVM (Model-View-ViewModel)** with Repository pattern
- **Room Database** for local data persistence
- **LiveData** for reactive UI updates
- **Material Design Components** for UI

### Key Technologies
- **Language**: Java
- **Database**: Room Persistence Library
- **UI**: Material Design Components, ViewPager2, MPAndroidChart
- **Async**: ExecutorService, CompletableFuture
- **Build**: Gradle (Android)

## Database Schema

### Core Entities

#### Expense
```sql
CREATE TABLE expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    amount REAL NOT NULL,
    date INTEGER NOT NULL,           -- Date of expense
    label_id INTEGER NOT NULL,       -- FK to expense_labels
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);
```

#### Income
```sql
CREATE TABLE incomes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    amount REAL NOT NULL,
    month INTEGER NOT NULL,          -- Month for which income is set
    year INTEGER NOT NULL,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);
```

#### Loan (Borrowed/Lent Money)
```sql
CREATE TABLE loans (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    type TEXT NOT NULL,              -- 'borrowed' or 'lent'
    person_name TEXT NOT NULL,
    amount REAL NOT NULL,
    transaction_date INTEGER NOT NULL,
    due_date INTEGER,
    is_settled INTEGER NOT NULL DEFAULT 0,
    settled_date INTEGER,
    notes TEXT,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);
```

#### ExpenseLabel (Categories)
```sql
CREATE TABLE expense_labels (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,             -- Category name (Bills, Food, etc.)
    color TEXT NOT NULL,            -- Hex color code
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);
```

#### MonthlyBudget (v2.0)
```sql
CREATE TABLE monthly_budgets (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_id INTEGER NOT NULL,   -- FK to expense_labels
    month INTEGER NOT NULL,
    year INTEGER NOT NULL,
    budget_amount REAL NOT NULL,
    spent_amount REAL NOT NULL DEFAULT 0,
    is_active INTEGER NOT NULL DEFAULT 1,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY(category_id) REFERENCES expense_labels(id) ON DELETE CASCADE
);
```

### Default Categories
- Bills (#2196F3 - Blue)
- Food (#FF9800 - Orange)
- Transport (#4CAF50 - Green)
- Entertainment (#9C27B0 - Purple)
- Shopping (#F44336 - Red)
- Healthcare (#00BCD4 - Cyan)
- Others (#607D8B - Grey)

## Key Activities & Components

### MainActivity (`MainActivity.java`)
**Primary dashboard and main entry point**

**UI Components:**
- **Header** (Dark grey #212121 background):
  - App title
  - Month/Year picker
  - Menu button (opens navigation drawer)
- **Financial Summary Cards**:
  - Income amount
  - Expenses amount
  - Remaining Balance
  - Mini pie chart
- **ViewPager2 with 3 swipeable cards**:
  1. Recent Expenses (last 5)
  2. Borrowed Money list
  3. Lent Money list
- **Page Indicators**: Dots showing current card position
- **FAB (Floating Action Button)**: Opens bottom sheet menu
  - Add Expense
  - Add Borrowed Money
  - Lend Money

**Key Features:**
- Month/Year navigation with `MonthYearPicker`
- Real-time balance calculation
- Toggle between recent (5) and all expenses view (fullscreen mode)
- Swipeable cards for different data views
- Export/Import data functionality via navigation drawer

### Navigation Drawer
**Location**: Slides from left, opened via menu button

**Header**:
- App logo (Flow Pocket BW - circular)
- App name: "Flow Pocket"
- Subtitle: "Personal Finance Tracker"
- Background: Dark grey (#212121) matching main header

**Menu Items**:
- 🏠 Dashboard (current screen)
- 📊 Category Chart
- 📈 Statistics (basic - shows all expenses)
- 💰 Budget Management (skeleton for v2.0)
- 📤 Export Data
- 📥 Import Data
- ℹ️ About

### Borrowed/Lent Money System
**Implementation**: ViewPager2 card-based interface

**Cards**:
1. **Borrowed Money Card** (Red theme #F44336)
   - List of money borrowed from others
   - Shows: Person name, amount, date, due date, notes
   - Checkbox to mark as settled/paid
   - Total pending amount display
   - "+" button to add new borrowed record

2. **Lent Money Card** (Green theme #4CAF50)
   - List of money lent to others
   - Shows: Person name, amount, date, due date, notes
   - Checkbox to mark as returned/settled
   - Total pending amount display
   - "+" button to add new lent record

**AddLoanDialog Features**:
- Person name input
- Amount input
- Transaction date picker
- Optional due date picker
- Optional notes field
- Type: "borrowed" or "lent" (set when opening dialog)

**Visual Indicators**:
- **Unsettled**: Normal text, checkbox unchecked
- **Settled**: Strikethrough text, greyed out, checkbox checked
- **Overdue**: Due date shown in red if past and unsettled
- **Upcoming**: Due date shown in orange

### Dialog Components
- **AddExpenseDialog**: Add new expenses with category selection
- **EditExpenseDialog**: Edit/delete existing expenses with confirmation
- **AddIncomeDialog**: Set monthly income
- **AddLoanDialog**: Add borrowed/lent money records
- **CategoryManagementDialog**: Manage expense categories

## Data Management

### ViewPager2 Card System
**CardPagerAdapter** manages 3 cards:
- Position 0: Recent Expenses
- Position 1: Borrowed Money
- Position 2: Lent Money

**Key Pattern**:
- Each ViewHolder maintains its own adapter (LoanAdapter)
- LiveData observers set up once per ViewHolder
- Observers trigger UI updates when data changes
- RecyclerView height uses `layout_weight` to fill available space

### CRUD Operations

#### Expenses
- **Create**: Validation (name required, amount > 0, valid category)
- **Read**: Monthly filtering by date range, category-wise grouping
- **Update**: Edit with automatic timestamp updates
- **Delete**: Confirmation dialogs, cascade considerations

#### Income
- **Create/Update**: One income record per month/year pair
- **Read**: Current month income for balance calculation

#### Loans (Borrowed/Lent)
- **Create**: Person name required, amount > 0, type (borrowed/lent)
- **Read**: Filtered by type, ordered by settled status then date
- **Update**: Toggle settled status, update timestamps
- **Delete**: (Not currently implemented in UI)

#### Categories
- **Create**: Custom categories with color selection
- **Read**: List for expense categorization
- **Update**: Modify names and colors
- **Delete**: Check for dependent expenses before deletion

### Repository Pattern
- **ExpenseRepository**: Manages expense-related data operations
- **IncomeRepository**: Handles income data management
- **LoanRepository**: Manages borrowed/lent money operations
- **Database**: `FlowPocketDatabase` with TypeConverters for Date handling

## Export/Import System

### Export Functionality (`DataExportUtil.java`)
**Creates comprehensive backup files**

**Export Format:**
```
FlowPocket_Backup_YYYYMMDD_HHMMSS.zip
├── expenses.json          # Complete expense data
├── income.json           # Income records
├── loans.json            # Borrowed/lent money records
├── categories.json       # Expense categories/labels
├── expenses.csv          # CSV format for Excel compatibility
├── income.csv           # CSV format
├── loans.csv            # CSV format
├── categories.csv       # CSV format
└── export_summary.txt   # Statistics and file manifest
```

**Implementation Details:**
- **Android 10+ Compatibility**: Uses MediaStore API for Downloads directory
- **Older Android**: Direct file system access with permissions
- **File Location**: `/storage/emulated/0/Download/` (user-accessible)
- **Includes all data**: Expenses, income, loans, categories

### Import Functionality (`DataImportUtil.java`)
**Restores data from backup files**

**Features:**
- **ZIP Processing**: Reads exported ZIP files
- **JSON Parsing**: Handles malformed data gracefully
- **Data Validation**: Date parsing with fallback to current date
- **Progress Reporting**: Shows import statistics
- **Error Handling**: Comprehensive exception management
- **Imports all entities**: Categories, expenses, income, loans

## Recent Implementations & Fixes

### ✅ Borrowed/Lent Money Feature (v1.0)
- Complete loan tracking system
- Swipeable card interface with ViewPager2
- Separate cards for borrowed and lent money
- Settlement tracking with visual indicators
- Due date tracking with overdue warnings
- Database integration with Loan entity

### ✅ UI/UX Improvements
- **Navigation Drawer**: Custom dark theme header with app logo
- **Circular App Icon**: Flow Pocket BW logo as launcher icon
- **FAB Bottom Sheet**: Multi-option add menu
- **Card Layout Fix**: Proper RecyclerView height allocation
- **Page Indicators**: Dots showing current card position
- **Dark Header Theme**: Consistent #212121 dark grey

### ✅ Bug Fixes (October 2024)
- **Loan Card Display Issue**: Fixed RecyclerView not showing due to incorrect layout heights
  - Changed header from `match_parent` to `wrap_content`
  - Changed RecyclerView to `0dp` height with `layout_weight="1"`
  - Removed `visibility="gone"` from total section
- **Database Migration**: Added MIGRATION_3_4 for loans table creation
- **LiveData Observers**: Fixed observer attachment in ViewPager2 cards

### ✅ Export/Import System
- Complete backup/restore functionality
- MediaStore integration for Android 10+ compatibility
- User-accessible file location in Downloads folder
- Multiple format support (JSON + CSV)
- Includes loans data in exports

## Development Patterns & Best Practices

### Database Patterns
- **Room Database** with proper entity relationships
- **DAOs** with both sync and async methods
- **TypeConverters** for Date serialization
- **Migration Strategy** with fallbackToDestructiveMigration
- **ExecutorService** for background operations

### UI Patterns
- **Material Design Guidelines** followed
- **ViewPager2** for swipeable card interface
- **Responsive Layouts** with CoordinatorLayout
- **Lifecycle Awareness** with ViewModels
- **Observer Pattern** with LiveData
- **Dark Theme** with Material colors

### Error Handling
- **Input Validation** at UI and database level
- **Exception Handling** with user-friendly messages
- **Graceful Degradation** for malformed import data
- **Default Fallbacks** for missing data

### Threading
- **ExecutorService** for async database operations
- **CompletableFuture** for async file operations
- **runOnUiThread** for UI updates from background
- **Room** handles background database operations

## File Structure Overview

```
app/src/main/java/com/flowpocket/
├── MainActivity.java                 # Main dashboard with ViewPager
├── StatisticsActivity.java          # Analytics screen (basic)
├── BudgetManagementActivity.java    # Budget management (v2.0)
├── CategoryChartActivity.java       # Visual charts
├── dao/                            # Database access objects
│   ├── ExpenseDao.java
│   ├── IncomeDao.java
│   ├── ExpenseLabelDao.java
│   ├── LoanDao.java                # Borrowed/lent money DAO
│   └── MonthlyBudgetDao.java
├── database/                       # Database setup
│   └── FlowPocketDatabase.java     # v4 with loans table
├── entities/                       # Data models
│   ├── Expense.java
│   ├── Income.java
│   ├── ExpenseLabel.java
│   ├── Loan.java                   # Borrowed/lent money entity
│   └── MonthlyBudget.java
├── dialog/                         # Dialog components
│   ├── AddExpenseDialog.java
│   ├── EditExpenseDialog.java
│   ├── AddIncomeDialog.java
│   └── AddLoanDialog.java          # Add borrowed/lent dialog
├── adapter/                        # RecyclerView adapters
│   ├── CardPagerAdapter.java       # ViewPager2 card adapter
│   └── LoanAdapter.java            # Borrowed/lent list adapter
├── ui/                            # UI components
│   └── SimpleExpenseAdapter.java
├── util/                          # Utility classes
│   ├── DataExportUtil.java        # Export with loans support
│   ├── DataImportUtil.java        # Import with loans support
│   └── MiniChartManager.java      # Chart management
├── utils/                         # Helper utilities
│   └── MonthYearPicker.java
├── viewmodel/                     # ViewModels
│   ├── ExpenseViewModel.java
│   ├── IncomeViewModel.java
│   └── LoanViewModel.java          # Borrowed/lent ViewModel
└── repository/                    # Data repositories
    ├── ExpenseRepository.java
    ├── IncomeRepository.java
    └── LoanRepository.java         # Loan repository
```

## Current Status & Next Steps

### ✅ Completed Features (v1.0)
- [x] Core expense tracking
- [x] Income management with month/year
- [x] Category system with colors
- [x] Month/Year navigation
- [x] Visual analytics (pie charts)
- [x] Export/Import functionality
- [x] Data backup and restore
- [x] Material Design UI with dark theme
- [x] Navigation drawer
- [x] Borrowed/Lent money tracking
- [x] Swipeable card interface (ViewPager2)
- [x] Settlement tracking for loans
- [x] Custom circular app icon

### 🚀 Planned for v2.0 (See docs/FUTURE_FEATURES_V2.md)
- [ ] Budget Management
  - Monthly budgets per category
  - Progress tracking with visual indicators
  - Budget alerts and warnings
- [ ] Enhanced Statistics & Analytics
  - Charts and graphs (pie, line, bar)
  - Spending trends and insights
  - Category analysis
  - Data export to CSV/PDF

### 🔍 Future Considerations
- [ ] Cloud backup integration
- [ ] Recurring expense tracking
- [ ] Bill reminders
- [ ] Multiple currency support
- [ ] Expense photo attachments
- [ ] Search and filtering features

## Testing & Debugging

### Testing Approach
- Manual testing on Android emulator (API 34)
- Real device testing for file system operations
- Import/Export workflow validation
- Loan tracking with settlement scenarios

### Debug Tools
- ADB logcat monitoring for runtime issues
- Android Studio debugger for development
- File system verification for export/import
- Database inspection with Room database inspector

---

**Last Updated**: October 4, 2025
**Current Version**: 1.0
**Database Version**: 4 (includes loans table)
**Target SDK**: Android API 34 (Android 14)
**Min SDK**: Android API 24 (Android 7.0)
