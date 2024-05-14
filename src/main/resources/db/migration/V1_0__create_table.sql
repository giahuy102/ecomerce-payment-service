CREATE TABLE order_statuses (
    id INTEGER,
    status_name VARCHAR(255) UNIQUE NOT NULL,
    color CHAR(7) UNIQUE NOT NULL,
    created_by_staff_id UUID,
    updated_by_staff_id UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT pk_order_status PRIMARY KEY(id)
);

CREATE TABLE orders (
    id UUID,
    customer_id UUID,
    order_status_id INTEGER,
    order_approved_at DATE,
    order_delivered_customer_date DATE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT pk_order PRIMARY KEY(id),
    CONSTRAINT fk_order_order_status
        FOREIGN KEY(order_status_id)
        REFERENCES order_statuses(id)
        ON DELETE SET NULL
);

CREATE TABLE order_items (
    id UUID,
    product_id UUID,
    order_id UUID,
    price DOUBLE PRECISION,
    quantity INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT pk_order_item PRIMARY KEY(id),
    CONSTRAINT fk_order_item_order
        FOREIGN KEY(order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE
);

CREATE TABLE carts (
    id UUID,
    customer_id UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT pk_cart PRIMARY KEY(id)
);

CREATE TABLE cart_items (
    id UUID,
    product_id UUID,
    cart_id UUID,
    price DOUBLE PRECISION,
    quantity INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT pk_cart_item PRIMARY KEY(id),
    CONSTRAINT fk_cart_item_cart
        FOREIGN KEY(cart_id)
        REFERENCES carts(id)
        ON DELETE CASCADE
);
