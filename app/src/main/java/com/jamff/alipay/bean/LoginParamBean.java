package com.jamff.alipay.bean;

/**
 * description: 登录传参
 * author: JamFF
 * time: 2018/12/12 20:58
 */
public class LoginParamBean {

    private String method;
    private String device_no;// 终端号
    private String password;// 密码

    public LoginParamBean() {
        this.method = "login";
    }

    public LoginParamBean(String device_no, String password) {
        this.method = "login";
        this.device_no = device_no;
        this.password = password;
    }

    public String getDevice_no() {
        return device_no;
    }

    public void setDevice_no(String device_no) {
        this.device_no = device_no;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginParamBean{" +
                "method='" + method + '\'' +
                ", device_no='" + device_no + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
