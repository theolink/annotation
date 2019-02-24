package com.tiansi.annotation.util;

import com.tiansi.annotation.model.Props;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddressUtil {
    @Autowired
    private Props props;

    public String toServerAddress(String localAddress) {
        localAddress = addressFormat(localAddress);
        return localAddress.replace(props.getBasePath(), props.getServerAddress());
    }

    public String toLocalAddress(String serverAddress) {
        return serverAddress.replace(props.getServerAddress(), props.getBasePath());
    }

    public String addressFormat(String address) {
        return address.replace("\\", "/");
    }

    public String getFileName(String path) {
        path = addressFormat(path);
        int start = path.lastIndexOf('/');
        start = start >= 0 ? start + 1 : 0;
        int end = path.lastIndexOf('.');
        end = end >= 0 ? end : 0;
        return path.substring(start, end);
    }

    public String getTrialName(String path) {
        path = addressFormat(path);
        return path.replace(props.getOriginVideoHome() + "/", "").replace('/', '-');
    }
}
