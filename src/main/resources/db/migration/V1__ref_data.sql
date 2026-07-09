CREATE TABLE ref_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(100) NOT NULL
);

CREATE TABLE ref_items (
    id BIGSERIAL PRIMARY KEY,
    ref_category_id INTEGER NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(100) NOT NULL,

    -- Defining the Foreign Key constraint
    CONSTRAINT fk_items_category 
        FOREIGN KEY (ref_category_id) 
        REFERENCES ref_categories(id)
        ON DELETE CASCADE
);

-- creating default data
insert into ref_categories(id, name, description) values(1, 'account_type', 'Account type');
insert into ref_items(id, ref_category_id, name, description) values (1, 1, 'cash', 'Physical money');
insert into ref_items(id, ref_category_id, name, description) values (2, 1, 'card', 'Credit/debit card');
insert into ref_items(id, ref_category_id, name, description) values (3, 1, 'qr', 'QR account');

insert into ref_categories(id, name, description) values(2, 'transaction_type', 'Income or expense');
insert into ref_items(id, ref_category_id, name, description) values (4, 2, 'income', 'Income of money');
insert into ref_items(id, ref_category_id, name, description) values (5, 2, 'expense', 'Expense made');

insert into ref_categories(id, name, description) values(3, 'category_type', 'Type of category');
insert into ref_items(id, ref_category_id, name, description) values (6, 3, 'income', 'Category of incomes');
insert into ref_items(id, ref_category_id, name, description) values (7, 3, 'expense', 'Expenses category');

insert into ref_categories(id, name, description) values(4, 'period_type', 'Period of time for filters');
insert into ref_items(id, ref_category_id, name, description) values (8, 4, 'day', 'By day');
insert into ref_items(id, ref_category_id, name, description) values (9, 4, 'week', 'By week');
insert into ref_items(id, ref_category_id, name, description) values (10, 4, 'month', 'By month');
insert into ref_items(id, ref_category_id, name, description) values (11, 4, 'year', 'By year');
insert into ref_items(id, ref_category_id, name, description) values (12, 4, 'custom', 'Custom period of time');

-- just in case
insert into ref_categories(id, name, description) values(5, 'sync_operation', 'Type of operation being executed');
insert into ref_items(id, ref_category_id, name, description) values (13, 5, 'CREATE', 'Creating operation');
insert into ref_items(id, ref_category_id, name, description) values (14, 5, 'UPDATE', 'Updating operation');
insert into ref_items(id, ref_category_id, name, description) values (15, 5, 'DELETE', 'Deleting operation');

insert into ref_categories(id, name, description) values(6, 'sync_status', 'Result of Syncing progress');
insert into ref_items(id, ref_category_id, name, description) values (16, 6, 'PENDING', 'Pending syncing');
insert into ref_items(id, ref_category_id, name, description) values (17, 6, 'SYNCED', 'Syncing completed');
insert into ref_items(id, ref_category_id, name, description) values (18, 6, 'CONFLICT', 'Conflicts when syncing');
insert into ref_items(id, ref_category_id, name, description) values (19, 6, 'ERROR', 'Error when syncing');

insert into ref_categories(id, name, description) values(7, 'object_type', 'What king of object is being synced');
insert into ref_items(id, ref_category_id, name, description) values (20, 7, 'transaction', 'Object of transaction type');
insert into ref_items(id, ref_category_id, name, description) values (21, 7, 'account', 'Object of account type');
insert into ref_items(id, ref_category_id, name, description) values (22, 7, 'category', 'Object of category type');
insert into ref_items(id, ref_category_id, name, description) values (23, 7, 'budget', 'Object of budget type');
insert into ref_items(id, ref_category_id, name, description) values (24, 7, 'frequent_expense', 'Object of frequent expense type');

insert into ref_categories(id, name, description) values(8, 'audit_action', 'Action made in history_log table');
insert into ref_items(id, ref_category_id, name, description) values (25, 8, 'CREATE', 'Creating operation');
insert into ref_items(id, ref_category_id, name, description) values (26, 8, 'UPDATE', 'Updating operation');
insert into ref_items(id, ref_category_id, name, description) values (27, 8, 'DELETE', 'Deleting operation');

insert into ref_categories(id, name, description) values(9, 'platform_type', 'Platform type');
insert into ref_items(id, ref_category_id, name, description) values (28, 9, 'ios', 'IOS device');
insert into ref_items(id, ref_category_id, name, description) values (29, 9, 'android', 'Android device');
insert into ref_items(id, ref_category_id, name, description) values (30, 9, 'web', 'Web platform');

insert into ref_categories(id, name, description) values(10, 'onboarding_status', 'Status when user install/register to app');
insert into ref_items(id, ref_category_id, name, description) values (31, 10, 'pending', 'Pending on board');
insert into ref_items(id, ref_category_id, name, description) values (32, 10, 'completed', 'On boarding completed');
insert into ref_items(id, ref_category_id, name, description) values (33, 10, 'skipped', 'On boarding skipped');

-- add timestamp values (created_at, updated_at)
