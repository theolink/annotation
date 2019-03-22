package com.tiansi.annotation.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@Data
@ConfigurationProperties(prefix = "props")
public class Props {
    private String basePath;
    private String clipHome;
    private String originVideoHome;
    private String undividedVideoHome;
    private String videoHome;
    private String imageHome;
    private int stepSize;
    private String serverAddress;
    private String[] videoTypes;
}
