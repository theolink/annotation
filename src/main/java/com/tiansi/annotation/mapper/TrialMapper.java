package com.tiansi.annotation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tiansi.annotation.domain.Trial;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

public interface TrialMapper extends BaseMapper<Trial> {
    @Options(useGeneratedKeys = true, keyColumn = "id")
    @Insert("Insert into trial(name,video_num,upload_date,uploader) values(#{name},#{videoNum},#{uploadDate},#{uploader})")
    int insert(Trial trial);
}
