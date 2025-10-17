-- liquibase formatted sql

-- changeset khoi:add-tax-debt-to-person
-- comment: add tax debt column to PERSON table
-- rollback ALTER TABLE oe1_db.PERSON DROP COLUMN TAX_DEBT;
ALTER TABLE oe1_db.PERSON ADD COLUMN TAX_DEBT DECIMAL(15,2) NOT NULL DEFAULT 0.00;


