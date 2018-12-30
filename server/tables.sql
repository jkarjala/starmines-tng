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