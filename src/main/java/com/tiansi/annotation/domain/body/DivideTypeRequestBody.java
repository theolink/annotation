package com.tiansi.annotation.domain.body;

import com.alibaba.fastjson.JSONObject;
import com.tiansi.annotation.domain.DivideType;
import com.tiansi.annotation.model.VideoRange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DivideTypeRequestBody {
    private Long id;
    private String name;
    private List<VideoRange> videoRanges;

    public DivideTypeRequestBody(DivideType divideType) {
        this.setId(divideType.getId());
        this.setName(divideType.getName());
        this.setVideoRanges(JSONObject.parseArray(divideType.getVideoRanges(), VideoRange.class));
    }

    public static List<DivideTypeRequestBody> fromDivideTypes(List<DivideType> divideTypes) {
        List<DivideTypeRequestBody> divideTypeRequestBodies = new ArrayList<>();
        divideTypes.forEach(divideType -> divideTypeRequestBodies.add(new DivideTypeRequestBody(divideType)));
        return divideTypeRequestBodies;
    }

    public DivideType toDivideType() {
        return new DivideType(this.getId(), this.getName(), JSONObject.toJSONString(this.getVideoRanges()));
    }

    public static List<DivideType> toDivideTypes(List<DivideTypeRequestBody> divideTypeRequestBodies) {
        List<DivideType> divideTypes = new ArrayList<>();
        divideTypeRequestBodies.forEach(divideTypeRequestBody -> divideTypes.add(divideTypeRequestBody.toDivideType()));
        return divideTypes;
    }
}
