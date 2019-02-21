package com.tiansi.annotation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tiansi.annotation.domain.Video;

import java.util.ArrayList;
import java.util.List;

public interface VideoService extends IService<Video> {
    List<Video> findAll();

    List<Video> findUntagged();

    List<Video> findSomeones(int userId);

    List<Video> findSomeonesTagged(int userId);

    List<Video> findSomeonesTagging(int userId);

    /**
     * @param videoPath  视频路径
     * @param outputPath 视频输出路径
     * @param start      开始时间
     * @param end        结束时间
     * @param step       步长
     * @return 输出路径
     * @apiNote 生成帧文件
     */
    Long interceptFrame(String videoPath, String outputPath, double start, double end, int step);

    /**
     * @param videoId videoId
     * @apiNote 对指定video进行剪辑，对其所有AB区间生成帧文件，并压缩保存
     */
    void cutVideo(int videoId) throws Exception;

    boolean segment(Video video);


    /**
     * 扫描视频
     *
     * @return 添加视频个数
     */
    int addVideos();

    /**
     * 切割视频
     *
     * @param originPath 原始路径
     * @param outputPath 输出路径
     * @return 切割完成的视频路径
     */
    ArrayList<String> divide(String originPath, String outputPath);
}
