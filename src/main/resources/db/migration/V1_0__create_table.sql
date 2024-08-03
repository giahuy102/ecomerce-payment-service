CREATE TABLE payment_accounts (
    id UUID,
    user_id UUID,
    balance DOUBLE PRECISION,
    payment_method VARCHAR(255) NOT NULL,
    CONSTRAINT pk_payment_account PRIMARY KEY(id)
);

CREATE TABLE payments (
    id UUID,
    payment_account_id UUID,
    order_id UUID,
    payment_time TIMESTAMP,
    total_price DOUBLE PRECISION,
    CONSTRAINT pk_payment PRIMARY KEY(id),
    CONSTRAINT fk_payment_payment_account
        FOREIGN KEY(payment_account_id)
        REFERENCES payment_account(id)
        ON DELETE CASCADE
);
