DROP TABLE IF EXISTS home;
DROP TABLE IF EXISTS mention;
DROP TABLE IF EXISTS any_user;
DROP TABLE IF EXISTS fixed_query;
DROP TABLE IF EXISTS queryable;
DROP TABLE IF EXISTS any_list;
DROP TABLE IF EXISTS twitter_user;

CREATE TABLE home (
    id INTEGER NOT NULL,
    tweet_content VARCHAR(140) NOT NULL,
    screen_name VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at INTEGER NOT NULL,
    profile_image_url VARCHAR(500) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE mention (
    id INTEGER NOT NULL,
    tweet_content VARCHAR(140) NOT NULL,
    screen_name VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at INTEGER NOT NULL,
    profile_image_url VARCHAR(500) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE any_user (
    id INTEGER NOT NULL,
    tweet_content VARCHAR(140) NOT NULL,
    screen_name VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at INTEGER NOT NULL,
    profile_image_url VARCHAR(500) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE fixed_query (
    id INTEGER NOT NULL,
    tweet_content VARCHAR(140) NOT NULL,
    screen_name VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at INTEGER NOT NULL,
    profile_image_url VARCHAR(500) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE queryable (
    id INTEGER NOT NULL,
    tweet_content VARCHAR(140) NOT NULL,
    screen_name VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at INTEGER NOT NULL,
    profile_image_url VARCHAR(500) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE any_list (
    id INTEGER NOT NULL,
    list_id INTEGER NOT NULL,
    tweet_content VARCHAR(140) NOT NULL,
    screen_name VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at INTEGER NOT NULL,
    profile_image_url VARCHAR(500) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE twitter_user (
	id INTEGER NOT NULL,
	name VARCHAR(100) NOT NULL,
	screen_name VARCHAR(100) NOT NULL,
    profile_image_url VARCHAR(500) NOT NULL,
    profile_banner_url VARCHAR(500) NULL,
    url VARCHAR(500) NULL,
    description VARCHAR(140) NULL,
    location VARCHAR(200) NULL,
    PRIMARY KEY (id),
    UNIQUE (screen_name)
);

CREATE INDEX home_created_at_idx ON home (created_at DESC);
CREATE INDEX mention_created_at_idx ON mention (created_at DESC);
CREATE INDEX fixed_query_created_at_idx ON fixed_query (created_at DESC);
CREATE INDEX any_user_created_at_idx ON any_user (created_at DESC);
CREATE INDEX queryable_created_at_idx ON queryable (created_at DESC);
CREATE INDEX any_list_created_at_idx ON any_list (created_at DESC);