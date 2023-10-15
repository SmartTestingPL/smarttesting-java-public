CREATE SEQUENCE verified_id_seq;

CREATE TABLE verified (
    ID smallint NOT NULL DEFAULT nextval('verified_id_seq'),
	USER_ID varchar(255) not null,
	NATIONAL_IDENTIFICATION_NUMBER varchar(255) not null,
	STATUS varchar(255) not null
);