-- Создание таблицы пользователей
CREATE TABLE t_user (
     id SERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL
);
-- Создание таблицы альбомов
CREATE TABLE t_album (
     id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    id_creator INTEGER NOT NULL,
    FOREIGN KEY (id_creator) REFERENCES t_user(id) ON DELETE CASCADE
);
-- Создание таблицы фотокарточек
CREATE TABLE t_photocard (
     id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    image bigint,
    id_album INTEGER,
    id_creator INTEGER NOT NULL,
    FOREIGN KEY (id_album) REFERENCES t_album(id) ON DELETE CASCADE,
    FOREIGN KEY (id_creator) REFERENCES t_user(id) ON DELETE CASCADE
);
