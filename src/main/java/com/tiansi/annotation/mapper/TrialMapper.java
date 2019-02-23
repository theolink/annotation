package com.tiansi.annotation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tiansi.annotation.domain.Trial;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

public interface TrialMapper extends BaseMapper<Trial> {

    @Insert("Insert into trial(name,video_num,upload_date,uploader) values(#{name},#{videoNum},#{uploadDate},#{uploader})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void save(Trial trial);
}
