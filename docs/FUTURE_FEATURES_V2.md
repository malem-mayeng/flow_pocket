# Flow Pocket - Future Features (v2.0)

**Created:** October 4, 2025
**Status:** Planned for v2.0
**Current Version:** 1.0

---

## Overview

This document tracks features that have been identified and designed but postponed for implementation in version 2.0. The skeleton/placeholder code exists in the current version but is not fully functional.

---

## Table of Contents
1. [Budget Management](#1-budget-management)
2. [Enhanced Statistics & Analytics](#2-enhanced-statistics--analytics)
3. [Implementation Priority](#implementation-priority)
4. [Database Schema Requirements](#database-schema-requirements)

---

## 1. Budget Management

### Current Status
- ✅ Menu item exists in navigation drawer
- ✅ Activity skeleton created: `BudgetManagementActivity.java`
- ✅ Layout file exists: `activity_budget_management.xml`
- ✅ Basic UI framework in place
- ❌ **No database integration**
- ❌ **No functional dialogs**
- ❌ **No data persistence**

### Feature Description
A comprehensive monthly budget management system that allows users to set spending limits for each expense category and track their progress throughout the month.

### Key Features

#### 1.1 Set Monthly Budgets
- **Purpose:** Allocate budget amounts for each expense category
- **Functionality:**
  - Select category (Bills, Food, Transport, Entertainment, Shopping, Healthcare, Others)
  - Set budget amount in ₹
  - Apply to specific month/year
  - Option to copy previous month's budget
  - Option to set recurring monthly budgets

#### 1.2 Budget Tracking Dashboard
- **Purpose:** Visual overview of spending vs budget
- **Display Elements:**
  - Total monthly budget amount
  - Total spent amount
  - Remaining budget
  - Per-category breakdown with progress bars
  - Percentage spent indicator

#### 1.3 Visual Indicators
- **Color Coding:**
  - 🟢 **Green** - Spending ≤ 80% of budget (healthy)
  - 🟠 **Orange** - Spending > 80% < 100% (warning)
  - 🔴 **Red** - Spending ≥ 100% (over budget)
- **Progress Bars:** Show spent/remaining for each category
- **Alerts:** Notifications when approaching or exceeding budget

#### 1.4 Month Navigation
- **Purpose:** View and manage budgets across different months
- **Features:**
  - Previous/Next month arrows
  - Month/Year picker dialog
  - View historical budget performance
  - Plan future budgets

#### 1.5 Budget Management Actions
- **Create Budget:** Add new category budget for current/future month
- **Edit Budget:** Modify existing budget amounts
- **Delete Budget:** Remove budget allocation
- **Copy Budget:** Duplicate from previous month

### User Flow
```
1. User opens Budget Management from drawer
2. Sees current month's budget overview
3. Can navigate to different months
4. Click "+" to add budget for a category
5. Select category, enter amount, save
6. Budget appears in list with progress bar
7. As expenses are added, progress updates automatically
8. Color indicators warn when approaching/exceeding limit
```

### Database Requirements
- **Table:** `monthly_budgets` (already exists in schema)
- **Fields:**
  - `id` - Primary key
  - `category_id` - FK to expense_labels
  - `month` - Integer (1-12)
  - `year` - Integer
  - `budget_amount` - Double
  - `spent_amount` - Double (calculated)
  - `is_active` - Boolean
  - `created_at` - Timestamp
  - `updated_at` - Timestamp

### Implementation Files
- ✅ Activity: `app/src/main/java/com/flowpocket/BudgetManagementActivity.java`
- ✅ DAO: `app/src/main/java/com/flowpocket/dao/MonthlyBudgetDao.java`
- ❌ **TODO:** Create `AddBudgetDialog.java`
- ❌ **TODO:** Create `EditBudgetDialog.java`
- ❌ **TODO:** Complete `BudgetCategoryAdapter.java`
- ❌ **TODO:** Implement budget observation in activity
- ❌ **TODO:** Add auto-calculation of spent_amount

### UI Mockup Reference
- Header: Month/Year navigation
- Total budget summary card
- Category budget list with progress bars
- FAB: Add new budget
- Empty state: "Create your first budget"

---

## 2. Enhanced Statistics & Analytics

### Current Status
- ✅ Menu item exists in navigation drawer
- ✅ Activity skeleton created: `StatisticsActivity.java`
- ✅ Shows basic all-time expense list
- ❌ **No charts/graphs**
- ❌ **No analytics/insights**
- ❌ **No filtering options**
- ❌ **No export functionality**

### Feature Description
A comprehensive analytics dashboard providing insights into spending patterns, trends, and financial health through visualizations and data analysis.

### Key Features

#### 2.1 Overview Dashboard
- **Total Income vs Total Expenses** (current month)
- **Net Savings** calculation
- **Daily Average Spending**
- **Top 3 Expense Categories** (pie chart)
- **Spending Trend Graph** (last 7/30/90 days)

#### 2.2 Category Analysis
- **Pie Chart:** Category-wise expense breakdown
- **Bar Chart:** Monthly comparison by category
- **Percentage Distribution:** Each category's share
- **Top Spending Category:** Highlight biggest expense
- **Category Trends:** Growth/decline over time

#### 2.3 Time-Based Analytics
- **Monthly View:**
  - Income vs Expenses comparison
  - Month-over-month growth/decline
  - Average daily spending
  - Highest spending day

- **Yearly View:**
  - 12-month expense trend line
  - Annual total and average
  - Peak spending months
  - Seasonal patterns

- **Custom Date Range:**
  - User-selectable start/end dates
  - Filtered analytics for specific period
  - Compare two date ranges

#### 2.4 Insights & Predictions
- **Spending Patterns:**
  - "You spend most on weekends"
  - "Your food expenses increased 25% this month"
  - "You're on track to save ₹X this month"

- **Budget Alerts:**
  - "You've spent 80% of your food budget"
  - "3 categories over budget this month"

- **Predictions:**
  - Projected month-end spending
  - Estimated savings
  - Budget runway (days until overspend)

#### 2.5 Data Export
- **CSV Export:** All expenses with filters
- **PDF Report:** Monthly summary with charts
- **Excel Format:** Detailed breakdown
- **Share Options:** Email, Drive, WhatsApp

#### 2.6 Interactive Charts
- **Chart Types:**
  - Pie Chart (category distribution)
  - Line Chart (trends over time)
  - Bar Chart (category comparison)
  - Stacked Bar Chart (income vs expense)

- **Interactions:**
  - Tap chart segment for details
  - Pinch to zoom
  - Swipe through time periods
  - Toggle data series on/off

### User Flow
```
1. User opens Statistics from drawer
2. Sees overview dashboard with key metrics
3. Scroll down for detailed charts
4. Tap on chart segments for drill-down
5. Switch between time periods (week/month/year)
6. View insights and recommendations
7. Export data if needed
```

### Database Requirements
- Uses existing tables: `expenses`, `income`, `expense_labels`
- No new tables required
- Calculations done in DAO queries
- Cached results for performance

### Implementation Requirements
- **Charts Library:** MPAndroidChart (already included)
- **New Files Needed:**
  - `StatisticsViewModel.java` - Data aggregation logic
  - `ChartHelper.java` - Chart configuration utilities
  - `DataExporter.java` - Export to CSV/PDF
  - `InsightsGenerator.java` - Pattern detection
  - `layout/activity_statistics_enhanced.xml` - New layout

- **DAO Methods:**
  - `getCategoryTotals(startDate, endDate)`
  - `getDailyAverageSpending(month, year)`
  - `getMonthlyTrends(year)`
  - `getTopCategories(limit, startDate, endDate)`
  - `getSpendingByDayOfWeek()`

### UI Sections
1. **Header Card:** Total income/expense/savings
2. **Quick Stats:** Daily avg, top category, trending
3. **Category Pie Chart:** Interactive breakdown
4. **Trend Line Chart:** Spending over time
5. **Insights Card:** Auto-generated tips
6. **Export Button:** Share/download options

---

## Implementation Priority

### Phase 1: Budget Management (Higher Priority)
**Why First:**
- More directly useful for users
- Helps prevent overspending
- Simpler implementation
- Database schema already exists

**Estimated Effort:** 2-3 days
- Day 1: Complete dialogs and adapters
- Day 2: Wire up database and observers
- Day 3: Testing and polish

### Phase 2: Enhanced Statistics (After Budget)
**Why Second:**
- Requires budget data to be most useful
- More complex visualizations
- Needs data aggregation logic

**Estimated Effort:** 3-4 days
- Day 1: Data aggregation and ViewModel
- Day 2: Charts and visualizations
- Day 3: Insights generation
- Day 4: Export functionality and testing

---

## Database Schema Requirements

### Already Exists (v1.0)
```sql
CREATE TABLE monthly_budgets (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_id INTEGER NOT NULL,
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

### Indices Needed
```sql
CREATE INDEX idx_monthly_budgets_category_id ON monthly_budgets(category_id);
CREATE INDEX idx_monthly_budgets_month_year ON monthly_budgets(month, year);
```

### Views for Statistics (Optional Optimization)
```sql
CREATE VIEW v_category_monthly_totals AS
SELECT
    el.id as category_id,
    el.name as category_name,
    strftime('%Y', e.date) as year,
    strftime('%m', e.date) as month,
    SUM(e.amount) as total
FROM expenses e
JOIN expense_labels el ON e.category_id = el.id
GROUP BY el.id, year, month;
```

---

## Notes for Future Implementation

### Budget Management
- Consider adding budget templates (e.g., "50/30/20 rule")
- Add notifications for budget alerts
- Consider weekly budget tracking option
- Add ability to rollover unused budget

### Statistics
- Cache computed statistics for performance
- Consider background calculation for large datasets
- Add comparison with previous periods
- Consider adding goals/targets feature
- Add "financial health score"

### Dependencies
- MPAndroidChart: Already included for charting
- No additional libraries needed
- Use existing Room database
- Use existing LiveData/ViewModel architecture

---

## Related Files

### Existing Implementation
- [BudgetManagementActivity.java](app/src/main/java/com/flowpocket/BudgetManagementActivity.java)
- [StatisticsActivity.java](app/src/main/java/com/flowpocket/StatisticsActivity.java)
- [MonthlyBudgetDao.java](app/src/main/java/com/flowpocket/dao/MonthlyBudgetDao.java)
- [FlowPocketDatabase.java](app/src/main/java/com/flowpocket/database/FlowPocketDatabase.java)

### To Be Created
- `AddBudgetDialog.java`
- `EditBudgetDialog.java`
- `StatisticsViewModel.java`
- `ChartHelper.java`
- `DataExporter.java`
- `InsightsGenerator.java`

---

## Change Log

| Date | Author | Changes |
|------|--------|---------|
| 2025-10-04 | AI Assistant | Initial documentation created |

---

**End of Document**
