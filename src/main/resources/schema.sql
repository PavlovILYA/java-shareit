CREATE TABLE IF NOT EXISTS users (
     id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
     name VARCHAR(255) NOT NULL,
     email VARCHAR(255) NOT NULL UNIQUE,
     PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items (
     id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
     name VARCHAR(255) NOT NULL,
     description TEXT,
     available BOOLEAN NOT NULL,
     owner_id BIGINT NOT NULL,
     PRIMARY KEY (id),
     FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (item_id) REFERENCES  items(id),
    FOREIGN KEY (booker_id) REFERENCES  users(id)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    description TEXT,
    requester_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (requester_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    test TEXT NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (item_id) REFERENCES  items(id),
    FOREIGN KEY (author_id) REFERENCES users(id)
);

-- DROP TABLE bookings;
-- DROP TABLE requests;
-- DROP TABLE comments;
-- DROP TABLE items;
-- DROP TABLE users;

