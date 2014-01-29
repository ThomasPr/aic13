
CREATE TABLE customer (
    id BIGSERIAL PRIMARY KEY,
    name TEXT
);

CREATE TABLE task (
    id BIGSERIAL PRIMARY KEY,
    finished BOOLEAN,
    numberoftweets BIGINT,
    result DOUBLE PRECISION,
    searchend TIMESTAMP WITHOUT TIME ZONE,
    searchpattern TEXT,
    searchstart TIMESTAMP WITHOUT TIME ZONE,
    user_id BIGINT
);

CREATE TABLE tweet (
    id BIGSERIAL PRIMARY KEY,
    date TIMESTAMP WITHOUT TIME ZONE,
    text TEXT
);

ALTER TABLE ONLY task ADD CONSTRAINT task_user_fk FOREIGN KEY (user_id) REFERENCES customer(id);


CREATE INDEX tweets_date ON tweet USING btree (date(date));

CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA public;
CREATE INDEX tweets_text ON tweet USING gin (text gin_trgm_ops);

