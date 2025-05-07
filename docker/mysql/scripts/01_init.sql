-- database namespace local 정리
CREATE DATABASE IF NOT EXISTS db_local CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS shard_0_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS shard_1_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- init local create user
CREATE USER IF NOT EXISTS 'local_rdb_master'@'%' IDENTIFIED BY 'local_rdb';
GRANT ALL ON *.* TO 'local_rdb_master'@'%';
GRANT REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'local_rdb_master'@'%';

FLUSH PRIVILEGES;


