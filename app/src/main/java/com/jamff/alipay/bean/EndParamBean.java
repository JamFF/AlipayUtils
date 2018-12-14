package com.jamff.alipay.bean;

/**
 * description:
 * author: JamFF
 * time: 2018/12/12 23:00
 */
public class EndParamBean {

    private String method;
    private String device_id;// 终端号

    public EndParamBean() {
        this.method = "end";
    }

    public EndParamBean(String device_id) {
        this.method = "end";
        this.device_id = device_id;
    }

    @Override
    public String toString() {
        return "EndParamBean{" +
                "method='" + method + '\'' +
                ", device_id='" + device_id + '\'' +
                '}';
    }
}
