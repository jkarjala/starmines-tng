DROP TABLE IF EXISTS highscores;
CREATE TABLE highscores (
  dt DATETIME,
  field_id smallint,
  user_name varchar(20),
  device varchar(23),
  score int,
  bonusoids int,
  PRIMARY KEY (field_id,user_name),
  KEY idx_highscores_name (user_name),
  KEY idx_highscores_field (field_id),
  KEY idx_highscores_score (score)
);

DROP TABLE IF EXISTS log;
CREATE TABLE log (
  ID int NOT NULL AUTO_INCREMENT,
  dt DATETIME,
  ip char(15),
  st DATETIME,
  pt DATETIME,
  user_name varchar(20),
  device char(23),
  field_id smallint,
  field_time int,
  stars smallint,
  score int,
  lives smallint,
  time_bonus smallint,
  bonusoids_collected smallint,
  bonusoids_total int,
  PRIMARY KEY (ID,dt)
)
PARTITION BY RANGE ( YEAR(dt) ) (
    PARTITION p2018 VALUES LESS THAN ( YEAR('2019-01-01 00:00:00') ),
    PARTITION p2019 VALUES LESS THAN ( YEAR('2020-01-01 00:00:00') ),
    PARTITION p2020 VALUES LESS THAN ( YEAR('2021-01-01 00:00:00') ),
    PARTITION p2021 VALUES LESS THAN ( YEAR('2022-01-01 00:00:00') ),
    PARTITION p2022 VALUES LESS THAN ( YEAR('2023-01-01 00:00:00') ),
    PARTITION p2023 VALUES LESS THAN ( YEAR('2024-01-01 00:00:00') ),
    PARTITION p2024 VALUES LESS THAN ( YEAR('2025-01-01 00:00:00') ),
    PARTITION p2025 VALUES LESS THAN ( YEAR('2026-01-01 00:00:00') ),
    PARTITION p2026 VALUES LESS THAN ( YEAR('2027-01-01 00:00:00') ),
    PARTITION p2027 VALUES LESS THAN ( YEAR('2028-01-01 00:00:00') ),
    PARTITION p2028 VALUES LESS THAN ( YEAR('2029-01-01 00:00:00') ),
    PARTITION p2029 VALUES LESS THAN ( YEAR('2030-01-01 00:00:00') ),
    PARTITION future VALUES LESS THAN (MAXVALUE)
);

ALTER TABLE log ADD COLUMN field_time int AFTER field_id;

-- INSERT INTO log (ip,id,st,pt,field_id,stars,score,lives,time_bonus,bonusoids_collected,bonusoids_total,user_name) VALUES ('1.2.3.4','id1','2019-01-01 00:00:00',from_unixtime(1546072717812/1000),0,0,0,0,0,0,0,'test');


DROP TABLE IF EXISTS devices;
CREATE TABLE devices (
  ID int NOT NULL AUTO_INCREMENT,
  dt DATETIME,
  device varchar(23),
  user_name varchar(20),
  PRIMARY KEY (ID)
);
insert into devices(user_name, device) select user_name, device from log group by 1,2;

DROP TABLE IF EXISTS scores;
CREATE TABLE scores (
  dt DATETIME,
  field_id smallint,
  device_id int,
  score int,
  bonusoids int,
  PRIMARY KEY (field_id,device_id),
  KEY idx_highscores_score (score),
  FOREIGN KEY (device_id) REFERENCES devices(ID)
);
insert into scores(dt, field_id, device_id, score, bonusoids) select h.dt, h.field_id, d.id, h.score, h.bonusoids from highscores as h,devices as d where d.device=h.device;

-- SELECT max(field_id),max(score),device_id,devices.user_name,max(bonusoids) FROM `scores`, devices where scores.device_id=devices.id group by device_id order by max(score) desc limit 10

DROP TABLE IF EXISTS new_log;
CREATE TABLE new_log (
  ID int NOT NULL AUTO_INCREMENT,
  dt DATETIME,
  ip char(15),
  st DATETIME,
  pt DATETIME,
  device_id int,
  field_id smallint,
  field_time int,
  stars smallint,
  score int,
  lives smallint,
  time_bonus smallint,
  bonusoids_collected smallint,
  bonusoids_total int,
  PRIMARY KEY (ID,dt)
)
PARTITION BY RANGE ( YEAR(dt) ) (
    PARTITION p2018 VALUES LESS THAN ( YEAR('2019-01-01 00:00:00') ),
    PARTITION p2019 VALUES LESS THAN ( YEAR('2020-01-01 00:00:00') ),
    PARTITION p2020 VALUES LESS THAN ( YEAR('2021-01-01 00:00:00') ),
    PARTITION p2021 VALUES LESS THAN ( YEAR('2022-01-01 00:00:00') ),
    PARTITION p2022 VALUES LESS THAN ( YEAR('2023-01-01 00:00:00') ),
    PARTITION p2023 VALUES LESS THAN ( YEAR('2024-01-01 00:00:00') ),
    PARTITION p2024 VALUES LESS THAN ( YEAR('2025-01-01 00:00:00') ),
    PARTITION p2025 VALUES LESS THAN ( YEAR('2026-01-01 00:00:00') ),
    PARTITION p2026 VALUES LESS THAN ( YEAR('2027-01-01 00:00:00') ),
    PARTITION p2027 VALUES LESS THAN ( YEAR('2028-01-01 00:00:00') ),
    PARTITION p2028 VALUES LESS THAN ( YEAR('2029-01-01 00:00:00') ),
    PARTITION p2029 VALUES LESS THAN ( YEAR('2030-01-01 00:00:00') ),
    PARTITION future VALUES LESS THAN (MAXVALUE)
);
insert into new_log (dt, ip, st, pt, device_id, field_id, field_time, stars, score, lives, time_bonus, bonusoids_collected, bonusoids_total) select l.dt, ip, st, pt, d.id, field_id, field_time, stars, score, lives, time_bonus, bonusoids_collected, bonusoids_total from log as l,devices as d where d.device=l.device;

-- statistics: 
-- SELECT TABLE_SCHEMA, TABLE_NAME, table_rows, data_length, index_length, round(((data_length + index_length) / 1024 / 1024),3) "Size in MB" FROM information_schema.TABLES WHERE table_schema like "jpkware_smtng_dev" ORDER BY (data_length + index_length) DESC;
