package com.tiansi.annotation.domain.body;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tiansi.annotation.domain.Video;
import com.tiansi.annotation.model.NumPair;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "VideoRequestBody对象", description = "video对象的请求体")
public class VideoRequestBody {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "video的id", name = "id", dataType = "Long", required = true, example = "1234326723")
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "庭审Id", name = "trialId", dataType = "Long", hidden = true)
    private Long trialId;
    @ApiModelProperty(value = "视频名称", name = "name", dataType = "String", hidden = true)
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "原始视频id", name = "originVideoId", dataType = "Long", hidden = true)
    private Long originVideoId;
    @ApiModelProperty(value = "视频地址", name = "address", dataType = "String", hidden = true)
    private String address;
    @ApiModelProperty(value = "视频长度", name = "length", dataType = "Integer", hidden = true)
    private Integer length;
    @ApiModelProperty(value = "标注状态；0：未标注，1：已标注正剪切，2：已剪切", name = "tagged", dataType = "Integer", hidden = true)
    private Integer tagged;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "区间信息标注者id", name = "tagger", dataType = "Long", hidden = true)
    private Long tagger;
    @ApiModelProperty(value = "区间信息标注日期", name = "tagDate", dataType = "Date", hidden = true)
    private Date tagDate;
    @ApiModelProperty(value = "分段信息列表", name = "clipsInfo", dataType = "List<NumPair>", required = true,
            example = "[{start:23.5,end:45.6},{start:67,end:99}]")
    private List<NumPair> clipsInfo;

    public Video toVideo() {
        Video video = new Video();
        video.setId(this.getId());
        video.setTrialId(this.getTrialId());
        video.setName(this.getName());
        video.setOriginVideoId(this.getOriginVideoId());
        video.setAddress(this.getAddress());
        video.setLength(this.getLength());
        video.setTagged(this.getTagged());
        video.setTagger(this.getTagger());
        video.setTagDate(this.getTagDate());
        video.setClipsInfo(JSON.toJSONString(this.getClipsInfo()));
        return video;
    }

    public static List<VideoRequestBody> fromVideos(List<Video> videos) {
        List<VideoRequestBody> videoRequestBodies = new ArrayList<>();
        videos.forEach(video -> videoRequestBodies.add(new VideoRequestBody(video)));
        return videoRequestBodies;
    }

    public static List<Video> toVideos(List<VideoRequestBody> videoRequestBodyList) {
        List<Video> videos = new ArrayList<>();
        videoRequestBodyList.forEach(videoClips -> videos.add(videoClips.toVideo()));
        return videos;
    }

    public VideoRequestBody(Video video) {
        if (video != null) {
            this.setId(video.getId());
            this.setTrialId(video.getTrialId());
            this.setName(video.getName());
            this.setOriginVideoId(video.getOriginVideoId());
            this.setAddress(video.getAddress());
            this.setLength(video.getLength());
            this.setTagged(video.getTagged());
            this.setTagger(video.getTagger());
            this.setTagDate(video.getTagDate());
            this.setClipsInfo(JSONObject.parseArray(video.getClipsInfo(), NumPair.class));
        }
    }
}
