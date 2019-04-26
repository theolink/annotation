package com.tiansi.annotation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tiansi.annotation.domain.OriginVideo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

public interface OriginVideoMapper extends BaseMapper<OriginVideo> {
    @Update("<script>"
            + "UPDATE origin_video SET origin_video.pre_deal=#{preDeal} WHERE id in"
            + "<foreach collection='ids' item='item' index='index' open='(' separator=',' close=')'>"
            + "#{item}"
            + "</foreach>"
            + "</script>"
    )
    void startDivide(@Param("preDeal") Integer preDeal, @Param("ids") List<Long> ids);

    @Update("UPDATE origin_video SET origin_video.pre_deal=#{preDeal} WHERE id=#{id}")
    void startDivide(@Param("preDeal") Integer preDeal, @Param("id") Long id);

    @Update("<script>"
            + "UPDATE origin_video SET origin_video.pre_deal=#{preDeal}, origin_video.pre_deal_date=#{preDealDate} WHERE id in"
            + "<foreach collection='ids' item='item' index='index' open='(' separator=',' close=')'>"
            + "#{item}"
            + "</foreach>"
            + "</script>"
    )
    void endDivide(@Param("preDeal") Integer preDeal, @Param("preDealDate") Date preDealDate, @Param("ids") List<Long> ids);

    @Update("UPDATE origin_video SET origin_video.pre_deal=#{preDeal}, origin_video.pre_deal_date=#{preDealDate} WHERE id=#{id}")
    void endDivide(@Param("preDeal") Integer preDeal, @Param("preDealDate") Date preDealDate, @Param("id") Long id);
}
