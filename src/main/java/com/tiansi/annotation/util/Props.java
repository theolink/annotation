package com.tiansi.annotation.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "props")
public class Props {
    private String videoOutputDir;
    private int stepSize;
}
