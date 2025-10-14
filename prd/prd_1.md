Here is a **Product Requirements Document (PRD)-style list of features** that can be developed **based solely on the current schema** you've defined. These features are scoped to what is *already supported* by your database structure — including accounts, transactions, double-entry journal entries, and the chart of accounts — without assuming any new tables or fields.

---

# 🏦 Banking System PRD: Core Features Based on Existing Schema
**Version:** 1.0  
**Date:** October 14, 2025  
**Scope:** Features derivable from existing `accounts`, `transactions`, `chart_of_accounts`, and `journal_entries` tables

---

## ✅ Overview

This document outlines the set of product features that can be implemented using only the data model currently defined in the schema. The system supports:

- Customer-facing account types (Checking, Savings, Loan, Credit Card)
- Transaction leg modeling with shared `transaction_id`
- Double-entry accounting via `journal_entries`
- GAAP-aligned general ledger (`chart_of_accounts`)
- Auditability and reconciliation tracking

All features below are **implementable today**, given the constraints and relationships already enforced.

---

## 🚀 Feature List

---

### 1. **Account Management**

#### Summary
Enable creation, retrieval, and basic management of customer and operational accounts.

#### Capabilities
- Create a new account with unique `account_number`, type, holder name, and zero balance.
- Retrieve account details (type, number, holder, balance, status).
- List all active accounts filtered by type (e.g., all SAVINGS).
- Detect soft-deleted accounts via `deleted_at`.

#### Supported By
- `accounts` table with `account_number UNIQUE`, `type CHECK`, and `balance`
- Indexes for fast lookup by account number and type

#### Constraints
- No ownership/user linking yet (no foreign key to users), so multi-customer routing not possible.
- Balance must remain ≥ 0 (enforced by CHECK constraint).

> ⚠️ Note: Negative balances (overdrafts) not allowed unless constraint is relaxed.

---

### 2. **Transaction Posting (Single-Leg & Multi-Leg)**

#### Summary
Record financial activity against one or more accounts using atomic transaction groups.

#### Capabilities
- Post single-leg transactions (e.g., deposit into checking).
- Post multi-leg transactions (e.g., transfer from A to B) using same `transaction_id`.
- Support for debits (negative amount) and credits (positive amount) per leg.
- Categorize transaction type (DEPOSIT, WITHDRAWAL, TRANSFER, etc.).

#### Supported By
- `transactions.account_id → accounts.id`
- Shared `transaction_id` across legs
- `amount` signed field indicating direction of cash flow
- Type enum covering customer actions, fees, interest, and internal markers

#### Example Use Cases
- Deposit cash → DEPOSIT leg on Checking Account
- Pay loan principal + interest → Two legs under same `transaction_id`:
    - LOAN_REPAYMENT_PRINCIPAL (-$90)
    - LOAN_REPAYMENT_INTEREST (-$10)

---

### 3. **Balance Calculation Engine**

#### Summary
Compute real-time account balance as sum of all transaction amounts affecting it.

#### Capabilities
- Compute current balance by summing `amount` from all non-deleted transactions linked to an account.
- Optionally exclude PENDING or REVERSED transactions.
- Validate consistency between stored `accounts.balance` and computed value.

#### Supported By
- `transactions.amount` (signed)
- Foreign key from `transactions.account_id` to `accounts.id`
- Trigger updates to `updated_at` on changes

#### Implementation Strategy
```sql
SELECT SUM(amount) FROM transactions 
WHERE account_id = ? AND status = 'POSTED'
GROUP BY account_id;
```

Compare result with `accounts.balance` for audit purposes.

---

### 4. **Double-Entry Journal Entry Generation**

#### Summary
Automatically generate balanced GL entries when business events occur.

#### Capabilities
- For every economic event (e.g., fee charged), create at least two `journal_entries` (DEBIT/CREDIT).
- Link each journal entry to original `transaction_id` for traceability.
- Enforce no duplicate `(transaction_id, gl_code, entry_type)` combinations.

#### Supported By
- `journal_entries.transaction_id` referencing `transactions.transaction_id`
- `gl_code` tied to `chart_of_accounts`
- `entry_type IN ('DEBIT', 'CREDIT')` and positive-only `amount`
- Exclusion constraint preventing duplicate legs

#### Example
When a $5 monthly fee is assessed:
- Debit: `41100` (Service Fees Revenue) → $5
- Credit: `20100` (Customer Deposits Liability) → $5  
  → Both share same `transaction_id`

---

### 5. **General Ledger Viewer**

#### Summary
Display full ledger activity grouped by GL code.

#### Capabilities
- Query all journal entries by `gl_code`.
- Show total DEBITS vs CREDITS per account.
- Filter by date range, source, or reference number.
- Support drill-down from GL to underlying transaction.

#### Supported By
- `journal_entries.gl_code → chart_of_accounts(gl_code)`
- Indexes on `gl_code`, `source`, `created_at`, `reference_number`
- Descriptive metadata in `chart_of_accounts.name`, `account_class`

#### Output Sample
| GL Code | Name               | Debits ($) | Credits ($) |
|--------|--------------------|------------|-------------|
| 20100  | Checking Accounts  | 0          | 250,000     |
| 41100  | Service Fees       | 12,000     | 0           |

---

### 6. **Trial Balance Report**

#### Summary
Generate a report showing net position of all GL accounts.

#### Capabilities
- Compute net movement per GL account: `SUM(DEBIT) - SUM(CREDIT)`
- Classify by `account_class` (Asset, Liability, etc.)
- Identify imbalances (total debits ≠ total credits across system)

#### Supported By
- `journal_entries.entry_type`, `amount`, `gl_code`
- `chart_of_accounts.account_class`

#### Formula
For each GL code:
```text
Net = Σ(entries WHERE entry_type='DEBIT') - Σ(entries WHERE entry_type='CREDIT')
```

Useful for validating bookkeeping integrity.

---

### 7. **Reconciliation Tracking**

#### Summary
Support reconciliation workflows with external systems or audits.

#### Capabilities
- Mark specific `journal_entries` as reconciled (`reconciled_at`, `reconciled_by`, `reconciliation_source`).
- Track external IDs via `reference_number`.
- Query unreconciled entries for reconciliation runs.

#### Supported By
- Optional `reconciled_at`, `reconciled_by`, `reconciliation_source`, `reference_number` fields
- Index on `reconciled_at` and `reference_number` for performance

#### Example Use Case
After nightly Plaid sync:
```sql
UPDATE journal_entries 
SET reconciled_at = NOW(), 
    reconciliation_source = 'PLAID_CLEARING_RUN',
    reference_number = 'PLD-20251014-001'
WHERE ...
```

---

### 8. **Event-Based Accounting Triggers**

#### Summary
Map high-level transaction types to automatic journal entries.

#### Capabilities
- Define rules like:
    - On `MONTHLY_FEE`: debit `41100`, credit `20100`
    - On `LOAN_DISBURSEMENT`: debit `12000`, credit `20100`
- Ensure every applicable `transactions.type` maps to correct GL behavior.

#### Supported By
- `transactions.type` ENUM includes semantic events like `OVERDRAFT_FEE`, `INTEREST_CREDITED`, etc.
- `journal_entries.source` captures origin (e.g., "MONTHLY_FEE")
- Can use `metadata` JSONB to store rule parameters

> 🔧 This enables building a rules engine later, but logic can start simple.

---

### 9. **Audit Trail & Historical View**

#### Summary
Provide visibility into financial history and modifications.

#### Capabilities
- View all transactions and journal entries over time.
- Detect soft-deleted accounts (`deleted_at IS NOT NULL`).
- Trace back any GL movement to originating transaction and account.

#### Supported By
- `created_at`, `updated_at` (on accounts), `reconciled_at`
- Immutable `transactions` and `journal_entries` (no deletes assumed)
- Full linkage chain:  
  `accounts ← transactions ← transaction_id → journal_entries → chart_of_accounts`

---

### 10. **Fee & Interest Accrual Posting**

#### Summary
Post periodic charges (fees, interest) as system-initiated transactions.

#### Capabilities
- Run batch jobs to post:
    - Monthly maintenance fees
    - Overdraft penalties
    - Interest accruals (charged or credited)
- Record as appropriate `type` in `transactions`
- Generate corresponding journal entries

#### Supported By
- Predefined types: `MONTHLY_FEE`, `OVERDRAFT_FEE`, `INTEREST_CHARGED`, etc.
- Ability to mark as internal/system-generated via `description` or `metadata`

> 💡 Can schedule via cron-like service; entries fully auditable.

---

## ❌ Out-of-Scope (Not Supported Yet)

These **cannot be built safely or completely** without extending the schema:

| Feature | Why Not Possible |
|-------|------------------|
| User Authentication & Ownership | No `user_id` or identity table |
| Real-time Balance Streaming | No change log/pub-sub mechanism |
| Currency Conversion | Only one currency (`currency` default `'USD'`) |
| Loan Amortization Schedule | No loan terms table |
| Credit Limits | No field for max limit on CC/loan |
| Hierarchical Account Groups | `chart_of_accounts.parent_code` exists but not used beyond level 2 |
| Approval Workflows | No state machine or role-based access control |

---

## ✅ Next Steps (Recommended)

1. **Implement Balance Recalculation Job** – Verify accuracy of `accounts.balance`
2. **Build Journal Entry Rule Mapper** – Auto-generate DE entries based on `transactions.type`
3. **Create Trial Balance Endpoint** – Internal reporting tool
4. **Add Reconciliation UI/CLI** – Allow marking entries as reconciled
5. **Instrument Metadata Field** – Begin storing source context (e.g., `{service: "billing-engine-v2"}`)

---

## 📎 Appendix: Key Data Flows

### Flow: Customer Makes Deposit
```mermaid
flowchart LR
A[Deposit Event] --> B(Create Transaction Leg: DEPOSIT, +$100)]
B --> C[Update Account Balance]
C --> D[Fire Accounting Rule]
D --> E[Journal Entry: Debit 10100 / Credit 20100]
E --> F[Store reference_number=recon_123]
```

### Flow: Assess Monthly Fee
```mermaid
flowchart LR
A[Nightly Fee Batch] --> B(Post WITHDRAWAL: -$5]
B --> C[Two Journal Entries]
C --> D1[Debit 41100: $5 Fee Income]
C --> D2[Credit 20100: $5 Liability Reduction]
```

---

Excellent — with your **updated schema** (where `transactions.account_id` is now nullable to support internal operations), we can define a **comprehensive, feature-level catalog of supported transactions**, based entirely on the types and relationships in your DDL.

This list will be:

- ✅ Grounded in your current `accounts.type`, `transactions.type`, and `chart_of_accounts`
- 🧩 Structured by real-world banking use cases
- 🔗 Mapped to both customer-facing actions and internal accounting events
- 💡 With notes on how each is modeled using your existing structure

---

# ✅ Comprehensive List of Supported Transactions
**Based on Current Schema (v2)**  
*As of October 14, 2025*

> All transactions are modeled as one or more rows in `transactions` sharing a `transaction_id`, with corresponding double-entry journal entries in `journal_entries`.

---

## 🏦 1. Customer Deposit Accounts
*(Checking & Savings)*

| Feature | Transaction Type(s) | Description |
|-------|----------------------|-------------|
| **Cash/Transfer Deposit** | `DEPOSIT` | Funds added via teller, ACH, wire, or internal transfer. Positive amount on account. |
| **Withdrawal** | `WITHDRAWAL` | Cash withdrawal or debit from account. Negative amount. |
| **Account-to-Account Transfer** | `TRANSFER` | Move money between two customer accounts (same or different holders). Two legs: one negative, one positive. |
| **Monthly Maintenance Fee** | `MONTHLY_FEE` | Recurring fee charged. Reduces balance. Paired with revenue recognition in GL. |
| **Overdraft Fee** | `OVERDRAFT_FEE` | Penalty for negative balance (if allowed later). Triggers revenue and liability reduction. |
| **Interest Paid to Customer** | `INTEREST_CREDITED` | Interest accrued and credited to savings/checking. Increases liability and records expense. |
| **Close Account** | `WITHDRAWAL` + `ADJUSTMENT_EVENT` | Final withdrawal + zero-out; optional GL adjustment if gain/loss. |

### Accounting Flow Example: Interest Credited
```sql
-- Customer leg
INSERT INTO transactions (transaction_id, account_id, amount, type, currency)
VALUES ('int-202504', 'acc-sav-789', +15.00, 'INTEREST_CREDITED', 'USD');

-- Double-entry
INSERT INTO journal_entries (transaction_id, gl_code, amount, entry_type, source)
VALUES
  ('int-202504', '20200', 15.00, 'CREDIT', 'INTEREST_POSTING'), -- Liability ↑
  ('int-202504', '50100', 15.00, 'DEBIT',  'INTEREST_POSTING'); -- Expense ↑
```

---

## 💳 2. Credit Card Accounts

| Feature | Transaction Type(s) | Description |
|--------|---------------------|------------|
| **Card Purchase** | `CARD_PURCHASE` | Merchant transaction debited from card balance. Negative amount. |
| **Payment from Bank Account** | `TRANSFER` | Pay off part/all of credit card balance from checking/savings. |
| **Cash Advance** | `WITHDRAWAL` | Treated like purchase but may carry fees/interest immediately. |
| **Late Payment Fee** | `LATE_FEE` | Assessed when minimum due not paid. Revenue event. |
| **Interest Charged** | `INTEREST_CHARGED` | Monthly interest posted to outstanding balance. Increases liability. |
| **Credit Limit Increase (Accounting Impact)** | *(none directly)* | No direct transaction until used. Can be tracked in metadata. |

### Accounting Flow: Interest Charged
```sql
-- Cardholder sees increase in balance
INSERT INTO transactions (transaction_id, account_id, amount, type)
VALUES ('cc-int-apr', 'acc-cc-101', -85.00, 'INTEREST_CHARGED');

-- Bank books: asset ↑ (receivable), revenue ↑
INSERT INTO journal_entries (transaction_id, gl_code, amount, entry_type, source)
VALUES
  ('cc-int-apr', '12000', 85.00, 'DEBIT',  'INTEREST_CHARGED'), -- Loan receivable ↑
  ('cc-int-apr', '40100', 85.00, 'CREDIT', 'INTEREST_CHARGED'); -- Revenue ↑
```

> ⚠️ Note: You're treating credit card balances as liabilities (`20200`) — correct from bank’s POV. The *bank* owes the merchant until customer repays.

---

## 📈 3. Loan Accounts
*(Personal, Auto, Mortgage, etc.)*

| Feature | Transaction Type(s) | Description |
|--------|---------------------|------------|
| **Loan Disbursement** | `LOAN_DISBURSEMENT` | Funds sent to borrower or third party. Increases loan receivable and deposits. |
| **Principal Repayment** | `LOAN_REPAYMENT_PRINCIPAL` | Part of EMI that reduces principal. Decreases asset and liability. |
| **Interest Repayment** | `LOAN_REPAYMENT_INTEREST` | Part of EMI that is income. Recognizes revenue. |
| **Prepayment / Early Payoff** | `LOAN_REPAYMENT_PRINCIPAL` (+optional fee) | Full or partial early repayment. May include `LATE_FEE` reversal or prepayment fee. |
| **Default & Write-off** | `ADJUSTMENT_EVENT` | Internal event to write off uncollectible loan. Uses `journal_entries` only. |
| **Loan Loss Provisioning** | `LOAN_LOSS_PROVISION` | Periodic reserve setting aside expected losses. Pure GL movement. |

### Accounting Flow: Loan Disbursement ($10k)
```sql
-- Customer receives funds into checking
INSERT INTO transactions (transaction_id, account_id, amount, type)
VALUES ('loan-disb-001', 'acc-chk-202', +10000.00, 'LOAN_DISBURSEMENT');

-- Double-entry
INSERT INTO journal_entries (transaction_id, gl_code, amount, entry_type, source)
VALUES
  ('loan-disb-001', '12000', 10000.00, 'DEBIT',  'LOAN_DISBURSEMENT'), -- Asset ↑
  ('loan-disb-001', '20100', 10000.00, 'CREDIT', 'LOAN_DISBURSEMENT'); -- Liability ↑
```

### Accounting Flow: Loan Loss Provision ($5k)
```sql
-- No account involved
INSERT INTO transactions (transaction_id, type, description)
VALUES ('provision-q1', 'LOAN_LOSS_PROVISION', 'Q1 risk-based provisioning');

INSERT INTO journal_entries (transaction_id, gl_code, amount, entry_type, source)
VALUES
  ('provision-q1', '50000', 5000.00, 'DEBIT',  'LOAN_LOSS_PROVISION'), -- Expense ↑
  ('provision-q1', '12000', 5000.00, 'CREDIT', 'LOAN_LOSS_PROVISION'); -- Contra-asset ↑
```

---

## 🔄 4. Transfers & Payments

| Feature | Transaction Type(s) | Description |
|--------|---------------------|------------|
| **Internal Transfer** | `TRANSFER` | Between two accounts under same customer. Atomic: one `-`, one `+`. |
| **External Transfer (ACH/Wire)** | `TRANSFER` | Outbound payment to external institution. Single negative leg. |
| **Bill Payment** | `BILL_PAYMENT` | Pay utility, rent, etc., from account. May go through clearing system. |
| **Peer-to-Peer Send** | `TRANSFER` | Send money to another user’s account (if system supports it). |

> All transfers must balance within their `transaction_id` group across accounts.

---

## 💸 5. Fees & Penalties

| Feature | Transaction Type(s) | Description |
|--------|---------------------|------------|
| **Monthly Maintenance Fee** | `MONTHLY_FEE` | Charged on checking/savings. |
| **Overdraft Fee** | `OVERDRAFT_FEE` | When balance goes negative (requires policy change). |
| **Late Payment Fee** | `LATE_FEE` | On loans or credit cards. |
| **Returned Payment Fee** | `OVERDRAFT_FEE` or custom | If bill payment bounces. |
| **Service Fees (e.g., paper statement)** | `MONTHLY_FEE` or `OVERDRAFT_FEE` | Can reuse or extend enum. |

### Accounting: Any Fee
Always:
- Debit revenue GL (`41100`, `41200`)
- Credit liability GL (`20100`, `20200`) → reduces what bank "owes" customer

```sql
-- $5 late fee
INSERT INTO journal_entries (transaction_id, gl_code, amount, entry_type, source)
VALUES
  ('fee-late-001', '41200', 5.00, 'CREDIT', 'LATE_FEE'),
  ('fee-late-001', '20100', 5.00, 'DEBIT',  'LATE_FEE');
```

---

## 📊 6. Interest & Yield Operations

| Feature | Transaction Type(s) | Description |
|--------|---------------------|------------|
| **Savings Interest Accrual** | `INTEREST_CREDITED` | Periodic posting to depositor. |
| **Loan Interest Accrual** | `INTEREST_CHARGED` | Accrued but not yet paid by borrower. |
| **Credit Card Interest Posting** | `INTEREST_CHARGED` | Monthly compounding. |
| **Interest Reversal** | `REVERSED` status + new event | Reverse prior interest (e.g., error correction). |

> Use `status = 'REVERSED'` and create a new `transaction_id` for reversal leg.

---

## 🔧 7. Internal & System-Level Events
*(Enabled by `account_id NULL`)*

| Feature | Transaction Type(s) | Description |
|--------|---------------------|------------|
| **Fee Income Recognition** | `FEE_INCOME_EVENT` | Batch posting of earned fees (e.g., amortized). |
| **Reserve Movement** | `RESERVE_TRANSFER_EVENT` | Shift funds between internal liquidity pools. |
| **Adjustment for Error Correction** | `ADJUSTMENT_EVENT` | Fix misposted entries. Requires full audit trail. |
| **Capital Raise / Equity Injection** | `EQUITY_CAPITAL_EVENT` | Record investment into the bank. |
| **Dividend Distribution** | `ADJUSTMENT_EVENT` | Reduce retained earnings and cash. |
| **Intercompany Transfer** | `TRANSFER` or `ADJUSTMENT_EVENT` | Between subsidiaries or ledgers. |

### Example: Capital Raise ($5M)
```sql
INSERT INTO transactions (transaction_id, type, description)
VALUES ('eq-cap-a1', 'EQUITY_CAPITAL_EVENT', 'Series A funding round');

INSERT INTO journal_entries (transaction_id, gl_code, amount, entry_type, source)
VALUES
  ('eq-cap-a1', '30000', 5000000.00, 'CREDIT', 'EQUITY_CAPITAL_EVENT'), -- Equity ↑
  ('eq-cap-a1', '10100', 5000000.00, 'DEBIT',  'EQUITY_CAPITAL_EVENT'); -- Cash ↑
```

---

## 🧾 8. Reconciliation & Audit Support

| Feature | How It's Supported |
|--------|--------------------|
| **Match External Clearing Files** | Use `reference_number` in `journal_entries` |
| **Reconcile Daily Close** | Set `reconciled_at`, `reconciliation_source` |
| **Trace Transactions** | Link `transactions.transaction_id` → `journal_entries` |
| **Audit Trail** | Immutable `created_at`, `reconciled_by`, `metadata` JSONB |

> Ideal for integrating with Plaid, FedLine, or internal batch runs.

---

## 🧩 Summary Table: Mapping `transactions.type` to Features

| `transactions.type` | Primary Use Case(s) |
|---------------------|---------------------|
| `DEPOSIT` | Cash deposit, ACH credit |
| `WITHDRAWAL` | ATM, teller, cash advance |
| `TRANSFER` | Internal/external fund move |
| `LOAN_DISBURSEMENT` | Fund release on loan |
| `LOAN_REPAYMENT_PRINCIPAL` | Principal paydown |
| `LOAN_REPAYMENT_INTEREST` | Interest portion of payment |
| `CARD_PURCHASE` | Credit card swipe |
| `BILL_PAYMENT` | Scheduled payment |
| `OVERDRAFT_FEE` | Penalty for insufficient funds |
| `MONTHLY_FEE` | Maintenance charge |
| `LATE_FEE` | Missed payment penalty |
| `INTEREST_CREDITED` | Interest paid to customer |
| `INTEREST_CHARGED` | Interest charged to borrower |
| `FEE_INCOME_EVENT` | Internal fee recognition |
| `RESERVE_TRANSFER_EVENT` | Liquidity management |
| `ADJUSTMENT_EVENT` | Corrections, equity, write-offs |
| `LOAN_LOSS_PROVISION` | Risk provisioning |
| `EQUITY_CAPITAL_EVENT` | Capital raise |

---

## ✅ What This Schema Enables

You now support a **full-stack core banking ledger layer** capable of:

| Capability | Supported? |
|----------|-----------|
| Customer account lifecycle | ✅ |
| Multi-leg transaction integrity | ✅ |
| Double-entry general ledger | ✅ |
| Regulatory reporting (Trial Balance, GL) | ✅ |
| Accurate revenue/expense recognition | ✅ |
| Internal adjustments & provisions | ✅ |
| Auditability & reconciliation | ✅ |

---

## 🔜 Next Steps (Optional Enhancements)

Once this is stable, consider adding:

1. **`event_category`** column in `transactions` (e.g., `'CUSTOMER_INITIATED'`, `'SYSTEM_POSTING'`, `'INTERNAL_ADJUSTMENT'`)
2. **Check constraints**: `CHECK ((account_id IS NOT NULL AND amount IS NOT NULL) OR (account_id IS NULL))`
3. **Separate service layer** to enforce business rules (e.g., cannot repay more than loan balance)
4. **Balance snapshots** table for fast reporting

---

Let me know if you'd like:
- A **state diagram** for loan/fee lifecycle
- **API specs** for posting these transactions
- **SQL views** for trial balance, P&L, etc.

You’ve built a solid foundation! 🏗️