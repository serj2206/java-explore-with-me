
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(512) NOT NULL,
    CONSTRAINT pk_categorys PRIMARY KEY (id),
    CONSTRAINT name_unique UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(512) NOT NULL,
    email VARCHAR (320) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title VARCHAR(512) NOT NULL,
    annotation VARCHAR (1024) NOT NULL,
    description VARCHAR (4096) NOT NULL,
    category_id bigint references categories(id),
    initiator_id bigint references users(id) ON DELETE CASCADE,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    state varchar (20),
    paid BOOLEAN NOT NULL,
    participant_limit integer,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    request_moderation BOOLEAN NOT NULL,
    CONSTRAINT pk_events PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id bigint references events(id) ON DELETE CASCADE,
    requester_id bigint references users(id) ON DELETE CASCADE,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status varchar (20),
    CONSTRAINT pk_requests PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title VARCHAR(512) NOT NULL,
    pinned BOOLEAN NOT NULL,
    CONSTRAINT pk_compilations PRIMARY KEY (id),
    CONSTRAINT title_unique UNIQUE (title)

);

CREATE TABLE IF NOT EXISTS compilations_events (
    compilation_id bigint references compilations (id) ON DELETE CASCADE,
    events_id       bigint references events (id) ON DELETE CASCADE,
    CONSTRAINT events_compilation UNIQUE (compilation_id, events_id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id bigint references events(id) ON DELETE CASCADE,
    commentator_id bigint references users(id) ON DELETE CASCADE,
    text VARCHAR(512) NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    comment_changed boolean NOT NULL,
    comment_status varchar (10) NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id)

);






