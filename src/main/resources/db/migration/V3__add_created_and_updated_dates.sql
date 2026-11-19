ALTER TABLE users
    ADD COLUMN created_date timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_date timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE schedules
    ADD COLUMN created_date timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_date timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE medications
    ADD COLUMN created_date timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_date timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE medication_forms
    ADD COLUMN created_date timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_date timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE medication_categories
    ADD COLUMN created_date timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_date timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP;
