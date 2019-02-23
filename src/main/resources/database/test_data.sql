DELETE FROM trial;

INSERT into trial (name, video_num, uploader) VALUES
('乡村大妈吵架案',3,1),
('闹市街区凶杀案',3,2),
('老夫老妻离婚案',3,1);

DELETE FROM video;

INSERT into video (trial_id,name,origin_video_id, address, length) VALUES
(1,'不知道是什么',1,'theo.oss-cn-shanghai.aliyuncs.com/projectlow/c4ac3ccdcd71a1959402c960ee86eae8.mp4',8),
(1,'不知道是啥',1,'192.168.1.120:8080/annotation/video/processed/2019-02-21/15507430238950.5105701451339169.mp4',8);
# (1,'不知道是何',1,'http://192.168.1.120:8080/annotation/video/processed/2019-02-22/155083730102880403.mp4',8);

DELETE FROM clips;

INSERT into clips (video_id,name,frame_num, address) VALUES
(1,'clip-1.29-2.05',11,'192.168.1.120:8080/annotation/clips/video-1/clip-1.29-2.05.zip'),
(1,'clip-2.9-3.72',12,'192.168.1.120:8080/annotation/clips/video-1/clip-2.9-3.72.zip');

DELETE FROM users;

INSERT into users (username, password,role) VALUES
('super','123456','ADMIN')