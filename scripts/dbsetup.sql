CREATE TABLE movies(
id                TEXT  PRIMARY KEY   NOT NULL,
title 				VARCHAR(300)       NOT NULL,
length       NUMERIC             NOT NULL
);

CREATE TABLE tags(
uuid                TEXT  PRIMARY KEY   NOT NULL,
title 				VARCHAR(300)       NOT NULL,
movie_id       TEXT             NOT NULL,
foreign key (movie_id) references movies (id) on update cascade
);