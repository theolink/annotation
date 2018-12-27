DROP TABLE IF EXISTS trial;
CREATE TABLE trial
(
  id          BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  name        VARCHAR(30) COMMENT '名称',
  video_num   INT(10)  DEFAULT 0 COMMENT '视频数量',
  upload_date datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  uploader    BIGINT(20) COMMENT '上传者id'
);
DROP TABLE IF EXISTS video;
CREATE TABLE video
(
  id       BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  trial_id BIGINT(20) COMMENT '庭审ID',
  address  VARCHAR(256) COMMENT '视频地址',
  length   INT(10) COMMENT '视频时长（秒）',
  tagged   INT(1) DEFAULT 0 COMMENT '是否已标注,0未标注，1标注中，2已标注',
  tagger   INT(20) COMMENT '标注者ID',
  clips_info VARCHAR(255) COMMENT '区间信息'
);

DROP TABLE IF EXISTS clips;
CREATE TABLE clips
(
  id          BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  video_id    BIGINT(20) COMMENT '视频ID',
  frame_num   INT(10) COMMENT '帧数',
  address     VARCHAR(256) COMMENT '帧文件地址',
  xml_address VARCHAR(256) COMMENT 'XML文件地址',
  tag         VARCHAR(256) COMMENT '标签JSON',
  tagged      INT(1) DEFAULT 0 COMMENT '是否已标注,0未标注，1标注中，2已标注',
  tagger      INT(20) COMMENT '标注者ID'
);

DROP TABLE IF EXISTS clips_info;
CREATE TABLE clips_info
(
  id         BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  video_id   BIGINT(20) COMMENT '庭审ID',
  start_time BIGINT(20) COMMENT 'AB区间起点A的时间(秒数)',
  end_time   BIGINT(20) COMMENT 'AB区间终点B的时间(秒数)'
);

DROP TABLE IF EXISTS users;
CREATE TABLE users
(
  id       BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  username VARCHAR(30) COMMENT '用户名',
  password VARCHAR(30) COMMENT '密码'
);