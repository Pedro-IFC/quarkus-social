CREATE DATABASE quarkusSocial;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100),
    age integer not null
);

CREATE TABLE posts (
    id SERIAL PRIMARY KEY,
    post_text varchar(150) not null,
    dateTime timestamp,
    user_id bigint not null references users(id)
);

CREATE TABLE followers (
    id SERIAL PRIMARY KEY,
    user_id bigint not null references users(id),
    follower_id bigint not null references users(id)
);
