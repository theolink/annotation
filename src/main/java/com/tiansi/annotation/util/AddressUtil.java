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

    public String disFormat(String address) {
        return address.replace("/", "\\");
    }

    public String getFileName(String path) {
        path = addressFormat(path);
        path = path.replace(props.getUndividedVideoHome() + "/", "");
        path = path.replace("/", "-");
        path = path.substring(0, path.lastIndexOf("."));
        return path;
    }

    public String getTrialName(String path) {
        path = addressFormat(path);
        return path.replace(props.getOriginVideoHome() + "/", "").replace('/', '-');
    }
}
