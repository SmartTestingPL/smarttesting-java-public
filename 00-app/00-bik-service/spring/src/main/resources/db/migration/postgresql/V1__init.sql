CREATE SEQUENCE occupation_id_seq;

CREATE TYPE occupation AS ENUM ('PROGRAMMER', 'LAWYER', 'DOCTOR', 'OTHER');

CREATE TABLE occupation_to_score
(
    ID                             smallint     NOT NULL DEFAULT nextval('occupation_id_seq'),
    OCCUPATION                     occupation   NOT NULL,
    OCCUPATION_SCORE               integer      NOT NULL
);
