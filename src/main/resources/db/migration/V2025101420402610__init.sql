-- Table: accounts
-- Represents customer-facing and core operational accounts only
CREATE TABLE IF NOT EXISTS accounts
(
    id                  UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    created_at          TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ DEFAULT NULL,
    deleted_at          TIMESTAMPTZ DEFAULT NULL,
    type                VARCHAR(50)        NOT NULL CHECK (
        type IN (
            -- Customer Accounts
                 'CHECKING',
                 'SAVINGS',
                 'LOAN',
                 'CREDIT_CARD'
            )
        ),
    account_number      VARCHAR(32) UNIQUE NOT NULL,
    account_holder_name TEXT               NOT NULL,
    balance             NUMERIC(15, 2)           DEFAULT 0.00 CHECK (balance >= 0)
);

-- Indexes on accounts
CREATE INDEX IF NOT EXISTS idx_accounts_account_number ON accounts (account_number);
CREATE INDEX IF NOT EXISTS idx_accounts_type ON accounts (type);


-- Table: transactions
-- One row per leg of a transaction affecting an account
-- Multi-leg transactions share the same transaction_id
-- A row in `transactions` represents either a customer-facing movement or a system-initiated financial event.
CREATE TABLE IF NOT EXISTS transactions
(
    id             UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    created_at     TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    transaction_id UUID           NOT NULL,
    -- Groups related legs (e.g., transfer from A to B)
    account_id     UUID           REFERENCES accounts (id),
    amount         NUMERIC(15, 2) NOT NULL,
    -- Positive = credit to account, Negative = debit
    currency       VARCHAR(3)     NOT NULL  DEFAULT 'USD',
    status         VARCHAR(20)    NOT NULL  DEFAULT 'POSTED'
        CHECK (status IN ('PENDING', 'POSTED', 'REVERSED')),
    type           VARCHAR(50)    NOT NULL CHECK (
        type IN (
            -- Customer Activity
                 'DEPOSIT',
                 'WITHDRAWAL',
                 'TRANSFER',
                 'LOAN_DISBURSEMENT',
                 'LOAN_REPAYMENT_PRINCIPAL',
                 'LOAN_REPAYMENT_INTEREST',
                 'CARD_PURCHASE',
                 'BILL_PAYMENT', -- missing

            -- Fees & Charges
                 'OVERDRAFT_FEE',
                 'MONTHLY_FEE',
                 'LATE_FEE', -- missing

            -- Interest
                 'INTEREST_CREDITED',
                 'INTEREST_CHARGED',

            -- Internal Operations (now just semantic markers)
                 'FEE_INCOME_EVENT', -- missing
                 'RESERVE_TRANSFER_EVENT', -- missing
                 'ADJUSTMENT_EVENT'
            )
        ),
    description    TEXT,
    metadata       JSONB
);

-- Indexes on transactions
CREATE INDEX IF NOT EXISTS idx_transactions_account_id ON transactions (account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_transaction_id ON transactions (transaction_id);
CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON transactions (created_at);
CREATE INDEX IF NOT EXISTS idx_transactions_type ON transactions (type);
CREATE INDEX IF NOT EXISTS idx_transactions_status ON transactions (status);


-- Table: chart_of_accounts
-- Standardized general ledger (GL) accounts (GAAP-friendly)
CREATE TABLE IF NOT EXISTS chart_of_accounts
(
    gl_code       VARCHAR(10) PRIMARY KEY,
    name          TEXT        NOT NULL,
    account_class VARCHAR(20) NOT NULL
        CHECK (account_class IN ('ASSET', 'LIABILITY', 'EQUITY', 'REVENUE', 'EXPENSE')),
    level         SMALLINT    NOT NULL DEFAULT 1,
    parent_code   VARCHAR(10) REFERENCES chart_of_accounts (gl_code)
);

-- Insert sample GL accounts
INSERT INTO chart_of_accounts (gl_code, name, account_class, level, parent_code)
VALUES
    -- Assets
    ('10000', 'Cash and Due from Banks', 'ASSET', 1, NULL),
    ('10100', 'Operating Account', 'ASSET', 2, '10000'),
    ('12000', 'Loans Receivable', 'ASSET', 2, '10000'),

    -- Liabilities
    ('20000', 'Customer Deposits', 'LIABILITY', 1, NULL),
    ('20100', 'Checking Accounts', 'LIABILITY', 2, '20000'),
    ('20200', 'Savings Accounts', 'LIABILITY', 2, '20000'),

    -- Equity
    ('30000', 'Bank Capital', 'EQUITY', 1, NULL),

    -- Revenue
    ('40000', 'Interest Income', 'REVENUE', 1, NULL),
    ('40100', 'Loan Interest', 'REVENUE', 2, '40000'),
    ('41000', 'Fee Income', 'REVENUE', 1, NULL),
    ('41100', 'Service Fees', 'REVENUE', 2, '41000'),
    ('41200', 'Late Fees', 'REVENUE', 2, '41000'),

    -- Expenses
    ('50000', 'Loan Loss Provision', 'EXPENSE', 1, NULL)
ON CONFLICT (gl_code) DO NOTHING;


-- Table: journal_entries
-- Double-entry bookkeeping records linked to real-world transactions
CREATE TABLE IF NOT EXISTS journal_entries
(
    id             UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    created_at     TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    transaction_id UUID           NOT NULL,
    -- Links to source event in `transactions`
    gl_code        VARCHAR(10)    NOT NULL REFERENCES chart_of_accounts (gl_code),
    amount         NUMERIC(15, 2) NOT NULL CHECK (amount > 0),
    -- Always positive; direction comes from entry_type
    entry_type     VARCHAR(10)    NOT NULL
        CHECK (entry_type IN ('DEBIT', 'CREDIT')),
    description    TEXT,
    source         VARCHAR(50)    NOT NULL,
    -- e.g., 'LOAN_REPAYMENT', 'MONTHLY_FEE', 'INTEREST_POSTING'
    -- 🔁 Reconciliation Fields
    reconciled_at           TIMESTAMPTZ,
    reconciled_by           UUID,                    -- Can link to users or services
    reconciliation_source   VARCHAR(50),             -- e.g., "PLAID_CLEARING_RUN"
    reference_number        VARCHAR(64),             -- External trace ID

    -- Constraints
    EXCLUDE USING btree (transaction_id WITH =, gl_code WITH =, entry_type WITH =)
    -- Prevent duplicate legs
);

-- Indexes for performance and integrity
CREATE INDEX IF NOT EXISTS idx_journal_entries_transaction_id ON journal_entries (transaction_id);
CREATE INDEX IF NOT EXISTS idx_journal_entries_gl_code ON journal_entries (gl_code);
CREATE INDEX IF NOT EXISTS idx_journal_entries_entry_type ON journal_entries (entry_type);
CREATE INDEX IF NOT EXISTS idx_journal_entries_source ON journal_entries (source);
CREATE INDEX IF NOT EXISTS idx_journal_entries_reconciled ON journal_entries (reconciled_at);
CREATE INDEX IF NOT EXISTS idx_journal_entries_ref_num    ON journal_entries (reference_number);


-- Trigger: Update updated_at on accounts
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS
$$
BEGIN
    new.updated_at = CURRENT_TIMESTAMP;
    RETURN new;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_accounts_updated_at
    BEFORE UPDATE
    ON accounts
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
