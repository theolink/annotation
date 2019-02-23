package com.tiansi.annotation.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@ConfigurationProperties(prefix = "props")
public class Props {
    private String basePath;
    private String clipHome;
    private String originVideoHome;
    private String videoHome;
    private String imageHome;
    private int stepSize;
    private String serverAddress;
    private String[] videoTypes;
    private List<VideoRange> videoRanges1;
    private List<VideoRange> videoRanges2;
}
