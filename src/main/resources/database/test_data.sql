DELETE FROM trial;

INSERT into trial (name, video_num, uploader) VALUES
('乡村大妈吵架案',3,1),
('闹市街区凶杀案',3,2),
('老夫老妻离婚案',3,1);

DELETE FROM video;

INSERT into video (trial_id, address, length, tagged, tagger) VALUES
(1,'http://theo.oss-cn-shanghai.aliyuncs.com/projectlow/c4ac3ccdcd71a1959402c960ee86eae8.mp4',10000,0,1),
(1,'http://theo.oss-cn-shanghai.aliyuncs.com/projectlow/c4ac3ccdcd71a1959402c960ee86eae8.mp4',10000,0,1),
(1,'http://theo.oss-cn-shanghai.aliyuncs.com/projectlow/c4ac3ccdcd71a1959402c960ee86eae8.mp4',10000,1,1),
(2,'http://theo.oss-cn-shanghai.aliyuncs.com/projectlow/c4ac3ccdcd71a1959402c960ee86eae8.mp4',10000,2,1),
(2,'http://theo.oss-cn-shanghai.aliyuncs.com/projectlow/c4ac3ccdcd71a1959402c960ee86eae8.mp4',10000,1,1);