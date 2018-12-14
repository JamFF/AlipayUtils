package com.jamff.alipay.bean;

/**
 * description:收款Bean
 * author: JamFF
 * time: 2018/12/11 15:17
 */
public class TradeBean {

    // 支付金额，单位为分
    private String trade_amount;

    // 订单编号（支付宝中的备注）
    private String order_sn;

    public TradeBean(String trade_amount, String order_sn) {
        this.trade_amount = trade_amount;
        this.order_sn = order_sn;
    }

    public String getTrade_amount() {
        return trade_amount;
    }

    public void setTrade_amount(String trade_amount) {
        this.trade_amount = trade_amount;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    @Override
    public String toString() {
        return "TradeBean{" +
                "trade_amount='" + trade_amount + '\'' +
                ", order_sn='" + order_sn + '\'' +
                '}';
    }
}
