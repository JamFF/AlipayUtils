package com.jamff.alipay.bean;

import java.util.Date;

/**
 * description:
 * author: JamFF
 * time: 2018/12/12 23:00
 */
public class NotifyParamBean {

    private String method;
    private String device_id;// 终端号
    private String order_sn;// 订单编号
    private String trade_amount;// 支付金额 单位为分
    private long pay_time;// 支付时间 时间戳

    private int count;// 超时访问次数

    public NotifyParamBean() {
        this.method = "notify";
        this.pay_time = new Date().getTime();
    }

    public NotifyParamBean(String device_id, String order_sn, String trade_amount) {
        this.method = "notify";
        this.device_id = device_id;
        this.order_sn = order_sn;
        this.trade_amount = trade_amount;
        this.pay_time = System.currentTimeMillis() / 1000;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "NotifyParamBean{" +
                "method='" + method + '\'' +
                ", device_id='" + device_id + '\'' +
                ", order_sn='" + order_sn + '\'' +
                ", trade_amount='" + trade_amount + '\'' +
                ", pay_time=" + pay_time +
                ", count=" + count +
                '}';
    }
}
