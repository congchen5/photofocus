create table if not exists users (
  user_id integer primary key autoincrement,
  username text,
  pw_hash text,
  auth_token text
);

create table if not exists photos (
  photo_id integer primary key autoincrement,
  mimetype text,
  latitude real,
  longitude real,
  user_id integer
);

create table if not exists comments (
  comment_id integer primary key autoincrement,
  body text,
  user_id integer,
  photo_id integer
);

create table if not exists notes (
  note_id integer primary key autoincrement,
  body text,
  user_id integer,
  photo_id integer
);