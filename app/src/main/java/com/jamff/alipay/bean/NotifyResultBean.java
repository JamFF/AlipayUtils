package com.jamff.alipay.bean;

/**
 * description:
 * author: JamFF
 * time: 2018/12/14 15:19
 */
public class NotifyResultBean {

    /**
     * errcode : 10000
     * msg : ok
     * data :
     * sign : 04A8DB45A3EE377DC809B43DF822E50D
     */
    private int errcode;
    private String msg;
    private String data;
    private String sign;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "NotifyResultBean{" +
                "errcode=" + errcode +
                ", msg='" + msg + '\'' +
                ", data='" + data + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
