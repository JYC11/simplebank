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
