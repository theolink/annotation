package com.tiansi.annotation.domain.body;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tiansi.annotation.domain.DivideType;
import com.tiansi.annotation.model.VideoRange;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DivideTypeRequestBody {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @ApiModelProperty(value = "类型名称", name = "name", dataType = "String", required = true)
    private String name;
    @ApiModelProperty(value = "类型分割参数", name = "videoRanges", dataType = "List")
    private List<VideoRange> videoRanges;
    private Long creator;
    private Date createDate;

    public DivideTypeRequestBody(DivideType divideType) {
        this.setId(divideType.getId());
        this.setName(divideType.getName());
        if (!StringUtils.isEmpty(divideType.getVideoRanges())) {
            this.setVideoRanges(JSONObject.parseArray(divideType.getVideoRanges(), VideoRange.class));
        }
        this.setCreator(divideType.getCreator());
        this.setCreateDate(divideType.getCreateDate());
    }

    public static List<DivideTypeRequestBody> fromDivideTypes(List<DivideType> divideTypes) {
        List<DivideTypeRequestBody> divideTypeRequestBodies = new ArrayList<>();
        divideTypes.forEach(divideType -> divideTypeRequestBodies.add(new DivideTypeRequestBody(divideType)));
        return divideTypeRequestBodies;
    }

    public DivideType toDivideType() {
        DivideType divideType = new DivideType();
        divideType.setId(this.getId());
        divideType.setName(this.getName());
        if (this.getVideoRanges() != null) {
            divideType.setVideoRanges(JSONObject.toJSONString(this.getVideoRanges()));
        }
        divideType.setCreator(this.getCreator());
        divideType.setCreateDate(this.getCreateDate());
        return divideType;
    }

    public static List<DivideType> toDivideTypes(List<DivideTypeRequestBody> divideTypeRequestBodies) {
        List<DivideType> divideTypes = new ArrayList<>();
        divideTypeRequestBodies.forEach(divideTypeRequestBody -> divideTypes.add(divideTypeRequestBody.toDivideType()));
        return divideTypes;
    }
}
