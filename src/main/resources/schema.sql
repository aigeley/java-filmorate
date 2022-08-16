CREATE SEQUENCE IF NOT EXISTS genres_seq;
CREATE TABLE IF NOT EXISTS genres
(
    genre_id   INT DEFAULT NEXT VALUE FOR genres_seq,
    genre_name VARCHAR(100) NOT NULL,
    CONSTRAINT genre_pk PRIMARY KEY (genre_id),
    CONSTRAINT genre_name_uq UNIQUE (genre_name)
);
CREATE SEQUENCE IF NOT EXISTS mpa_seq;
CREATE TABLE IF NOT EXISTS mpa
(
    mpa_id   INT DEFAULT NEXT VALUE FOR mpa_seq,
    mpa_name VARCHAR(100) NOT NULL,
    CONSTRAINT mpa_pk PRIMARY KEY (mpa_id),
    CONSTRAINT mpa_name_uq UNIQUE (mpa_name)
);
CREATE SEQUENCE IF NOT EXISTS films_seq;
CREATE TABLE IF NOT EXISTS films
(
    film_id      BIGINT DEFAULT NEXT VALUE FOR films_seq,
    film_name    VARCHAR(100) NOT NULL,
    description  VARCHAR(200),
    release_date DATE         NOT NULL,
    duration     INT          NOT NULL,
    mpa_id       INT,
    CONSTRAINT films_pk PRIMARY KEY (film_id),
    CONSTRAINT films_mpa_fk FOREIGN KEY (mpa_id) REFERENCES mpa (mpa_id) ON DELETE RESTRICT
);
CREATE SEQUENCE IF NOT EXISTS film_genres_seq;
CREATE TABLE IF NOT EXISTS film_genres
(
    film_genre_id BIGINT DEFAULT NEXT VALUE FOR film_genres_seq,
    film_id       BIGINT,
    genre_id      INT,
    CONSTRAINT film_genres_film_fk FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    CONSTRAINT film_genres_genre_fk FOREIGN KEY (genre_id) REFERENCES genres (genre_id) ON DELETE RESTRICT,
    CONSTRAINT film_genres_uq UNIQUE (film_id, genre_id)
);
CREATE SEQUENCE IF NOT EXISTS users_seq;
CREATE TABLE IF NOT EXISTS users
(
    user_id   BIGINT DEFAULT NEXT VALUE FOR users_seq,
    email     VARCHAR(200) NOT NULL,
    login     VARCHAR(50)  NOT NULL,
    user_name VARCHAR(100),
    birthday  DATE         NOT NULL,
    CONSTRAINT users_pk PRIMARY KEY (user_id)
);
CREATE SEQUENCE IF NOT EXISTS likes_seq;
CREATE TABLE IF NOT EXISTS likes
(
    like_id BIGINT DEFAULT NEXT VALUE FOR likes_seq,
    film_id BIGINT,
    user_id BIGINT,
    CONSTRAINT likes_film_fk FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    CONSTRAINT likes_user_fk FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT likes_uq UNIQUE (film_id, user_id)
);
CREATE SEQUENCE IF NOT EXISTS user_friends_seq;
CREATE TABLE IF NOT EXISTS user_friends
(
    user_friend_id BIGINT DEFAULT NEXT VALUE FOR user_friends_seq,
    user_id        BIGINT,
    friend_id      BIGINT,
    CONSTRAINT user_friends_user_fk FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT user_friends_friend_fk FOREIGN KEY (friend_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT user_friends_uq UNIQUE (user_id, friend_id)
);