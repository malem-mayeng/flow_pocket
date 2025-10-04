# Flow Pocket v1.0 - Release Notes

**Release Date:** October 4, 2025
**Build Status:** ✅ Stable
**Database Version:** 4
**Branch:** `fp-01-integrate-data-backup`

---

## 🎉 What's New in v1.0

### Core Features

#### 💰 Borrowed & Lent Money Tracking
- Track money borrowed from others
- Track money lent to others
- Swipeable card interface (ViewPager2)
- Settlement tracking with checkboxes
- Due date tracking with overdue warnings
- Optional notes for each transaction
- Total pending amount display

#### 🎨 UI/UX Improvements
- **Navigation Drawer** with dark theme header (#212121)
- **Circular App Icon** - Flow Pocket BW logo
- **Swipeable Cards** - Recent Expenses, Borrowed, Lent
- **Page Indicators** - Dots showing current card
- **FAB Bottom Sheet** - Multi-option add menu with transparency
- **Dark Header** - Consistent dark grey theme

#### 📊 Enhanced Dashboard
- Month/Year navigation
- Financial summary (Income, Expenses, Balance)
- Mini pie chart visualization
- Recent expenses (last 5)
- Fullscreen expense view toggle

#### 💾 Data Management
- Export all data (expenses, income, loans, categories)
- Import from backup ZIP files
- JSON + CSV format support
- User-accessible Downloads folder

---

## 🔧 Technical Details

### Database Schema (v4)

**New Table: loans**
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

**Migration:** MIGRATION_3_4 added for loans table

### New Components

**Java Classes:**
- `Loan.java` - Entity for borrowed/lent money
- `LoanDao.java` - Database access object
- `LoanRepository.java` - Repository pattern
- `LoanViewModel.java` - ViewModel for loan data
- `AddLoanDialog.java` - Dialog for adding loans
- `LoanAdapter.java` - RecyclerView adapter for loan list
- `CardPagerAdapter.java` - ViewPager2 adapter for cards

**Layouts:**
- `bottom_sheet_add_menu.xml` - FAB menu with transparency
- `card_borrowed_money.xml` - Borrowed money card
- `card_lent_money.xml` - Lent money card
- `card_recent_expenses.xml` - Recent expenses card
- `dialog_add_loan.xml` - Add loan dialog
- `item_loan.xml` - Loan list item
- `drawer_header.xml` - Navigation drawer header
- `drawer_menu.xml` - Drawer menu items

**Resources:**
- `app_logo.png` - Circular app logo for drawer
- `ic_launcher.png` (all densities) - Circular app icon
- `dot_active.xml` / `dot_inactive.xml` - Page indicators

---

## 🐛 Bug Fixes

### Critical Fixes
1. **Loan Card Display Issue**
   - Fixed RecyclerView not showing in borrowed/lent cards
   - Changed layout heights from `match_parent` to proper constraints
   - Added `layout_weight` for RecyclerView to fill space

2. **Database Migration**
   - Added MIGRATION_3_4 for loans table creation
   - Proper fallback to destructive migration

3. **LiveData Observers**
   - Fixed observer attachment in ViewPager2 cards
   - Observers now attach once per ViewHolder

### UI/UX Fixes
1. **Dialog Transparency**
   - Reduced CustomDialogTheme dim from 90% to 30%
   - Bottom sheet panel transparency: 40% opacity
   - Consistent background dim across all dialogs

2. **Navigation Drawer**
   - Dark theme header matching main app header
   - Circular logo integration
   - Proper menu item styling

---

## 📁 Project Structure

```
Flow Pocket v1.0
├── app/src/main/java/com/flowpocket/
│   ├── MainActivity.java                    # Main dashboard
│   ├── adapter/
│   │   ├── CardPagerAdapter.java           # ViewPager2 cards
│   │   └── LoanAdapter.java                # Loan list adapter
│   ├── dao/
│   │   └── LoanDao.java                    # Loan database access
│   ├── dialog/
│   │   └── AddLoanDialog.java              # Add borrowed/lent dialog
│   ├── entities/
│   │   └── Loan.java                       # Loan entity
│   ├── repository/
│   │   └── LoanRepository.java             # Loan repository
│   ├── viewmodel/
│   │   └── LoanViewModel.java              # Loan ViewModel
│   └── util/
│       ├── DataExportUtil.java             # Export with loans
│       └── DataImportUtil.java             # Import with loans
├── docs/
│   └── FUTURE_FEATURES_V2.md               # v2.0 roadmap
└── .claude/
    └── flow_pocket_app_documentation.md    # Full app docs
```

---

## 🚀 Next Steps (v2.0)

See `docs/FUTURE_FEATURES_V2.md` for detailed plans:

### Priority 1: Budget Management
- Monthly budgets per category
- Progress tracking with visual indicators
- Budget alerts and warnings
- Estimated effort: 2-3 days

### Priority 2: Enhanced Statistics
- Charts and graphs (pie, line, bar)
- Spending trends and insights
- Category analysis
- Data export to CSV/PDF
- Estimated effort: 3-4 days

---

## 📝 Key Changes Summary

### Added
✅ Borrowed/Lent money tracking system
✅ Swipeable card interface (ViewPager2)
✅ Navigation drawer with dark theme
✅ Circular app icon and logo
✅ FAB bottom sheet menu
✅ Page indicators for cards
✅ Loan settlement tracking
✅ Due date with overdue warnings
✅ Export/Import support for loans

### Modified
🔄 MainActivity - Added ViewPager2 for cards
🔄 Database - Updated to v4 with loans table
🔄 Export/Import - Added loan data support
🔄 Themes - Reduced dialog dim for better UX
🔄 Drawer header - Dark theme with circular logo

### Fixed
🐛 RecyclerView layout issues in loan cards
🐛 LiveData observer attachment in ViewPager2
🐛 Dialog transparency (too dark)
🐛 Database migration for loans table

---

## 🔗 Documentation

- **Full App Documentation:** `.claude/flow_pocket_app_documentation.md`
- **v2.0 Roadmap:** `docs/FUTURE_FEATURES_V2.md`
- **This Release Notes:** `RELEASE_NOTES_V1.0.md`

---

## 💻 Build Information

**Target SDK:** Android 34 (Android 14)
**Min SDK:** Android 24 (Android 7.0)
**Build Type:** Release (Stable)
**APK Location:** `app/build/outputs/apk/debug/flow-pocket.apk`

---

## ✅ Testing Checklist

- [x] Expense tracking and editing
- [x] Income management
- [x] Borrowed money tracking
- [x] Lent money tracking
- [x] Settlement marking
- [x] Export/Import with loans
- [x] Navigation drawer
- [x] Month/Year navigation
- [x] Card swiping
- [x] FAB bottom sheet menu
- [x] Dialog transparency
- [x] App icon display

---

**End of v1.0 Release Notes**

---

## For Next Session

When starting v2.0 development:

1. **Create new branch:** `git checkout -b v2.0-budget-statistics`
2. **Review roadmap:** Read `docs/FUTURE_FEATURES_V2.md`
3. **Start with:** Budget Management feature (higher priority)
4. **Reference docs:** `.claude/flow_pocket_app_documentation.md`

All necessary context is preserved in documentation files for seamless continuation.
