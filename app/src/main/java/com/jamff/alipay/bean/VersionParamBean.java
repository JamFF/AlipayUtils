package com.jamff.alipay.bean;

import com.jamff.alipay.util.UIUtils;

/**
 * description:
 * author: JamFF
 * time: 2018/12/22 17:02
 */
public class VersionParamBean {

    private String method;
    private String version;

    public VersionParamBean() {
        method = "end";
        version = UIUtils.getVersionName();
    }

    public String getMethod() {
        return method;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "VersionParamBean{" +
                "method='" + method + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
