DROP TABLE IF EXISTS trial;
CREATE TABLE trial
(
  id          BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  name        VARCHAR(256) COMMENT '名称',
  video_num   INT(10)  DEFAULT 0 COMMENT '视频数量',
  upload_date datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  uploader    BIGINT(20) COMMENT '上传者id',
  is_deleted  INT(1) DEFAULT 0 COMMENT '是否删除，0：未删除；1：已删除'
);
DROP TABLE IF EXISTS video;
CREATE TABLE video
(
  id       BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  trial_id BIGINT(20) COMMENT '庭审ID',
  name   VARCHAR(256) COMMENT '视频名称',
  origin_video_id BIGINT(20) COMMENT '原始视频ID',
  address  VARCHAR(256) COMMENT '视频地址',
  length   INT(10) COMMENT '视频时长（秒）',
  tagged   INT(1) DEFAULT 3 COMMENT '区间标记状态：3：未分配，0：已分配，未标记，1：已标记，剪切中，2：已剪切',
  tagger   BIGINT(20) COMMENT '标注者ID',
  tag_date datetime  COMMENT 'AB区间信息标记时间',
  clips_info VARCHAR(20000) COMMENT '区间信息',
  is_deleted  INT(1) DEFAULT 0 COMMENT '是否删除，0：未删除；1：已删除'
);

DROP TABLE IF EXISTS clips;
CREATE TABLE clips
(
  id          BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  name   VARCHAR(256) COMMENT '帧文件名称',
  video_id    BIGINT(20) COMMENT '视频ID',
  frame_num   INT(10) COMMENT '帧数',
  address     VARCHAR(256) COMMENT '帧文件地址',
  xml_address VARCHAR(256) COMMENT 'XML文件地址',
  tag         MEDIUMTEXT COMMENT '标签JSON',
  tagged      INT(1) DEFAULT 0 COMMENT '标签标注状态：0：未分配，1：已分配，未标注，2：已标注',
  tagger      BIGINT(20) COMMENT '标注者ID',
  tag_date datetime  COMMENT '标签标注时间',
  is_deleted  INT(1) DEFAULT 0 COMMENT '是否删除，0：未删除；1：已删除'
);

DROP TABLE IF EXISTS directories;
CREATE TABLE directories
(
  id     BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  name   VARCHAR(256) COMMENT '目录名称',
  is_deleted  INT(1) DEFAULT 0 COMMENT '是否删除，0：未删除；1：已删除'
);

DROP TABLE IF EXISTS users;
CREATE TABLE users
(
  id       BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  username VARCHAR(255) COMMENT '用户名',
  password VARCHAR(255) COMMENT '密码',
  role     VARCHAR(30) COMMENT '角色',
  is_deleted  INT(1) DEFAULT 0 COMMENT '是否删除，0：未删除；1：已删除'
);
DROP TABLE IF EXISTS origin_video;
CREATE TABLE origin_video
(
  id       BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  trial_id BIGINT(20) COMMENT '庭审ID',
  name VARCHAR(256) COMMENT '原始视频名称',
  address  VARCHAR(256) COMMENT '原始视频地址',
  uploader  BIGINT(20) COMMENT '上传者ID',
  upload_date datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  pre_deal INT(1) DEFAULT 0 COMMENT '视频预处理状态,0：未分配，1：已分配，未设置类型，2：已设置类型，未分割，3：分割中，4：已分割',
  pre_dealer BIGINT(20) COMMENT '预处理执行者ID',
  pre_deal_date datetime  COMMENT '预处理执行时间',
  divide_type BIGINT(20) COMMENT '分割类型ID',
  type_name VARCHAR(256) COMMENT '分割类型名称',
  img_path  VARCHAR(256) COMMENT '原始视频截图地址',
  is_deleted  INT(1) DEFAULT 0 COMMENT '是否删除，0：未删除；1：已删除'
);
DROP TABLE IF EXISTS divide_type;
CREATE TABLE divide_type
(
  id       BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  name VARCHAR(256) COMMENT '名称',
  video_ranges VARCHAR(20000) COMMENT '分割格式',
  is_deleted  INT(1) DEFAULT 0 COMMENT '是否删除，0：未删除；1：已删除',
  creator  BIGINT(20) COMMENT '创建者ID',
  create_date datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);