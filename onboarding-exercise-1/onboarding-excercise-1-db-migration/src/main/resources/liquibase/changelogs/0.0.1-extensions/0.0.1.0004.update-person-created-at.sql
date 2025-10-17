--liquibase formatted sql

--changeset khoi:update-person-created-at
--comment: Make CREATED_DTTM NOT NULL in PERSON table
--rollback ALTER TABLE oe1_db.PERSON ALTER COLUMN CREATED_DTTM DROP NOT NULL;

-- Ensure no existing nulls (use current timestamp for them)
UPDATE oe1_db.PERSON SET CREATED_DTTM = CURRENT_TIMESTAMP WHERE CREATED_DTTM IS NULL;

-- Alter the column to be NOT NULL
ALTER TABLE oe1_db.PERSON
    ALTER COLUMN CREATED_DTTM SET NOT NULL;