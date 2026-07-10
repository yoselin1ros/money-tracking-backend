CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    display_name VARCHAR(50) NOT NULL UNIQUE, 
    preferred_currency varchar(5),
    theme_preference VARCHAR(20),
    email_verified BOOLEAN DEFAULT FALSE,
    failed_login_attempts INTEGER,
    locked_until TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    token_hash VARCHAR(200) NOT NULL UNIQUE,
    device_id VARCHAR(200) NOT NULL, -- NORMALIZE???
    device_name VARCHAR(50) NOT NULL, 
    platform_ref_item_id INTEGER,
    expires_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    revoked BOOLEAN DEFAULT FALSE,
    last_activity_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_sessions_user 
        FOREIGN KEY (user_id) 
        REFERENCES users(id)
        ON DELETE CASCADE,
    
    CONSTRAINT fk_sessions_ref_item 
        FOREIGN KEY (platform_ref_item_id) 
        REFERENCES ref_items(id)
        ON DELETE CASCADE
);

CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    token_hash VARCHAR(200) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    used_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_password_rt_user 
        FOREIGN KEY (user_id) 
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE email_verification_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    token_hash VARCHAR(200) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    used_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_email_vt_user 
        FOREIGN KEY (user_id) 
        REFERENCES users(id)
        ON DELETE CASCADE
)
