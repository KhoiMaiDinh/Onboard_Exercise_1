--liquibase formatted sql

--changeset khoi:create-app-data-schema
--comment: Create schema for application data
--rollback DROP SCHEMA IF EXISTS oe1_db CASCADE;
CREATE SCHEMA IF NOT EXISTS oe1_db;