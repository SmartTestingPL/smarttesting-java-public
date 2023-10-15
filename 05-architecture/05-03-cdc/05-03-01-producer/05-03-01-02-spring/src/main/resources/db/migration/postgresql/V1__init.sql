CREATE SEQUENCE verified_id_seq;

CREATE TABLE verified (
    -- IN H2
    -- 	ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    -- IN POSTGRESQL
    ID smallint NOT NULL DEFAULT nextval('verified_id_seq'),
	USER_ID UUID not null,
	NATIONAL_IDENTIFICATION_NUMBER varchar(255) not null,
	STATUS varchar(255) not null
);