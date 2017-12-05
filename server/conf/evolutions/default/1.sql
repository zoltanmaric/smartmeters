# --- !Ups

CREATE TABLE users (
	id SERIAL PRIMARY KEY,
	username VARCHAR(256) UNIQUE,
	public_key VARCHAR(1024) UNIQUE
);

# --- !Downs

DROP TABLE users;
