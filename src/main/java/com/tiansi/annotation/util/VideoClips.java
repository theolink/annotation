package com.tiansi.annotation.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tiansi.annotation.domain.Video;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoClips {
    private int id;
    private int trialId;
    private String address;
    private int length;
    private int tagged;
    private int tagger;
    private List<NumPair> clipsInfo;

    public Video toVideo() {
        Video video = new Video();
        video.setId(this.getId());
        video.setTrialId(this.getTrialId());
        video.setAddress(this.getAddress());
        video.setLength(this.getLength());
        video.setTagged(this.getTagged());
        video.setTagger(this.getTagger());
        video.setClipsInfo(JSON.toJSONString(this.getClipsInfo()));
        return video;
    }

    public static List<VideoClips> fromVideos(List<Video> videos) {
        List<VideoClips> videoClips = new ArrayList<>();
        videos.forEach(video -> videoClips.add(new VideoClips(video)));
        return videoClips;
    }

    public static List<Video> toVideos(List<VideoClips> videoClipsList) {
        List<Video> videos = new ArrayList<>();
        videoClipsList.forEach(videoClips -> videos.add(videoClips.toVideo()));
        return videos;
    }

    public VideoClips(Video video) {
        if (video != null) {
            this.setId(video.getId());
            this.setTrialId(video.getTrialId());
            this.setAddress(video.getAddress());
            this.setLength(video.getLength());
            this.setTagged(video.getTagged());
            this.setTagger(video.getTagger());
            this.setClipsInfo(JSONObject.parseArray(video.getClipsInfo(), NumPair.class));
        }
    }
}
