CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    user_id INTEGER,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    type_ref_item_id INTEGER NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_categories_user 
        FOREIGN KEY (user_id) 
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_categories_ref_item 
        FOREIGN KEY (type_ref_item_id) 
        REFERENCES ref_items(id)
        ON DELETE CASCADE
);

CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    type_ref_item_id INTEGER NOT NULL,
    initial_balance DECIMAL DEFAULT 0,
    current_balance DECIMAL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_accounts_user 
        FOREIGN KEY (user_id) 
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_accounts_ref_item 
        FOREIGN KEY (type_ref_item_id) 
        REFERENCES ref_items(id)
        ON DELETE CASCADE
);

CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    account_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL, 
    amount DECIMAL default 0,
    type_ref_item_id INTEGER NOT NULL,-- TODO: remove??
    transaction_date DATE DEFAULT now(),
    note VARCHAR(200),
    used_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_transactions_user 
        FOREIGN KEY (user_id) 
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_transactions_account 
        FOREIGN KEY (account_id) 
        REFERENCES accounts(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_transactions_category 
        FOREIGN KEY (category_id) 
        REFERENCES categories(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_transactions_ref_item 
        FOREIGN KEY (type_ref_item_id) 
        REFERENCES ref_items(id)
        ON DELETE CASCADE
);

CREATE TABLE frequent_expenses (
    id BIGSERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    name VARCHAR(200) NOT NULL,
    amount DECIMAL default 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_frequent_expenses_user 
        FOREIGN KEY (user_id) 
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_frequent_expenses_category 
        FOREIGN KEY (category_id) 
        REFERENCES categories(id)
        ON DELETE CASCADE
);

CREATE TABLE budgets (
    id BIGSERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    spending_limit DECIMAL(10, 2) DEFAULT 0.00,
    period_type_ref_item_id INTEGER,
    period_start DATE DEFAULT CURRENT_DATE,
    period_end DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_budgets_user 
        FOREIGN KEY (user_id) 
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_budgets_category 
        FOREIGN KEY (category_id) 
        REFERENCES categories(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_budgets_ref_item 
        FOREIGN KEY (period_type_ref_item_id) 
        REFERENCES ref_items(id)
        ON DELETE CASCADE
);

CREATE TABLE history_log (
    id BIGSERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    object_type_ref_item_id INTEGER NOT NULL,
    object_id INTEGER NOT NULL,
    action_ref_item_id INTEGER NOT NULL,
    previous_value JSONB, 
    new_value JSONB, 
    occurred_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_history_log_user 
        FOREIGN KEY (user_id) 
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_history_log_ot_ref_item 
        FOREIGN KEY (object_type_ref_item_id) 
        REFERENCES ref_items(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_history_log_a_ref_item 
        FOREIGN KEY (action_ref_item_id) 
        REFERENCES ref_items(id)
        ON DELETE CASCADE
);

CREATE TABLE sync_queue (
    id BIGSERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    device_id VARCHAR(200) NOT NULL, -- NORMALIZE???
    object_type VARCHAR(50) NOT NULL,
    object_id INTEGER NOT NULL,
    operation_ref_item_id INTEGER NOT NULL,
    payload JSONB,
    client_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, --???
    status_ref_item_id INTEGER NOT NULL,
    retry_count INTEGER,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_sync_queue_user 
        FOREIGN KEY (user_id) 
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_sync_queue_o_ref_item 
        FOREIGN KEY (operation_ref_item_id) 
        REFERENCES ref_items(id)
        ON DELETE CASCADE,
    
    CONSTRAINT fk_sync_queue_s_ref_item 
        FOREIGN KEY (status_ref_item_id) 
        REFERENCES ref_items(id)
        ON DELETE CASCADE
);

CREATE TABLE notification_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    budget_approaching_enabled BOOLEAN DEFAULT FALSE,
    budget_exceeded_enabled BOOLEAN DEFAULT FALSE,
    sync_conflict_enabled BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_notification_preferences_user 
        FOREIGN KEY (user_id) 
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- table for push notifications
CREATE TABLE device_push_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    device_id VARCHAR(200) NOT NULL,
    push_token VARCHAR(200),
    platform_ref_item_id INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_device_pt_user 
        FOREIGN KEY (user_id) 
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_device_pt_item 
        FOREIGN KEY (platform_ref_item_id) 
        REFERENCES ref_items(id)
        ON DELETE CASCADE
);

CREATE TABLE onboarding_status (
    id BIGSERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    status_ref_item_id INTEGER NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_onboarding_status_user 
        FOREIGN KEY (user_id) 
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_onboarding_status_item 
        FOREIGN KEY (status_ref_item_id) 
        REFERENCES ref_items(id)
        ON DELETE CASCADE
);
