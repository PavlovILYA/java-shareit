CREATE TABLE IF NOT EXISTS users (
     id BIGINT GENERATED ALWAYS AS IDENTITY,
     name VARCHAR(255),
     email VARCHAR(255),
     PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items (
     id BIGINT GENERATED ALWAYS AS IDENTITY,
     name VARCHAR(255),
     description TEXT,
     available BOOLEAN,
     owner_id BIGINT,
     PRIMARY KEY (id),
     FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    item_id BIGINT,
    booker_id BIGINT,
    status VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (item_id) REFERENCES  items(id),
    FOREIGN KEY (booker_id) REFERENCES  users(id)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    description TEXT,
    requester_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (requester_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    test TEXT,
    item_id BIGINT,
    author_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (item_id) REFERENCES  items(id),
    FOREIGN KEY (author_id) REFERENCES users(id)
);

-- DROP TABLE bookings;
-- DROP TABLE requests;
-- DROP TABLE comments;
-- DROP TABLE items;
-- DROP TABLE users;



