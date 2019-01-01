package com.jamff.alipay.bean;

/**
 * description:
 * author: JamFF
 * time: 2019/1/1 02:25
 */
public class BillBean {

    private String billName;
    private String billAmount;

    public BillBean(String billName, String billAmount) {
        this.billName = billName;
        this.billAmount = billAmount;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public String getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(String billAmount) {
        this.billAmount = billAmount;
    }

    @Override
    public String toString() {
        return "BillBean{" +
                "billName='" + billName + '\'' +
                ", billAmount='" + billAmount + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof BillBean) {
            BillBean bean = (BillBean) obj;
            return bean.billAmount.equals(billAmount);
        }
        return false;
    }
}
