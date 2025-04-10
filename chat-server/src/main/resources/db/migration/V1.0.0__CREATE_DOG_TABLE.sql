CREATE SEQUENCE IF NOT EXISTS dog_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS dog
(
    id          integer      DEFAULT nextval('dog_id_seq') PRIMARY KEY,
    name        text NOT NULL,
    owner       text,
    description text NOT NULL
);
