CREATE SEQUENCE verified_id_seq;

CREATE TABLE verified (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    USER_ID UUID not null,
	NATIONAL_IDENTIFICATION_NUMBER varchar(255) not null,
	STATUS varchar(255) not null
);