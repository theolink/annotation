package com.tiansi.annotation.domain.body;

import com.tiansi.annotation.domain.Clips;
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
@ApiModel(value = "ClipsRequestBody对象", description = "clips对象的请求体")
public class ClipsRequestBody {
    @ApiModelProperty(value = "clips的id", name = "id", dataType = "Long", required = true, example = "1234326723")
    private Long id;
    @ApiModelProperty(value = "片段名称", name = "name", dataType = "String", hidden = true)
    private String name;
    @ApiModelProperty(value = "视频ID", name = "videoId", dataType = "Long", hidden = true)
    private Long videoId;
    @ApiModelProperty(value = "片段帧数", name = "frameNum", dataType = "Integer", hidden = true)
    private Integer frameNum;
    @ApiModelProperty(value = "片段地址", name = "address", dataType = "String", hidden = true)
    private String address;
    @ApiModelProperty(value = "标签数据", name = "tag", dataType = "String", required = true)
    private String tag;
    @ApiModelProperty(value = "标注状态；0：未标注，1：标注中，2：已标注", name = "tagged", dataType = "Integer", hidden = true)
    private Integer tagged;
    @ApiModelProperty(value = "标注者Id", name = "tagger", dataType = "Long", hidden = true)
    private Long tagger;
    @ApiModelProperty(value = "区间信息标注日期", name = "tagDate", dataType = "Date", hidden = true)
    private Date tagDate;

    public ClipsRequestBody(Clips clips) {
        if (clips != null) {
            this.id = clips.getId();
            this.name = clips.getName();
            this.videoId = clips.getVideoId();
            this.frameNum = clips.getFrameNum();
            this.address = clips.getAddress();
            this.tag = clips.getTag();
            this.tagged = clips.getTagged();
            this.tagger = clips.getTagger();
            this.tagDate = clips.getTagDate();

        }
    }

    public Clips toClips() {
        Clips clips = new Clips();
        clips.setId(this.id);
        clips.setName(this.name);
        clips.setVideoId(this.videoId);
        clips.setFrameNum(this.frameNum);
        clips.setAddress(this.address);
        clips.setTag(this.tag);
        clips.setTagged(this.tagged);
        clips.setTagger(this.tagger);
        clips.setTagDate(this.tagDate);
        return clips;
    }

    public static List<ClipsRequestBody> fromClips(List<Clips> clipsList) {
        List<ClipsRequestBody> clipsRequestBodies = new ArrayList<>();
        clipsList.forEach(clips -> clipsRequestBodies.add(new ClipsRequestBody(clips)));
        return clipsRequestBodies;
    }

    public static List<Clips> toClips(List<ClipsRequestBody> clipsRequestBodies) {
        List<Clips> clipsList = new ArrayList<>();
        clipsRequestBodies.forEach(clipsRequestBody -> clipsList.add(clipsRequestBody.toClips()));
        return clipsList;
    }
}
