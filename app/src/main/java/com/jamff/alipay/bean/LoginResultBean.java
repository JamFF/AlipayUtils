package com.jamff.alipay.bean;

/**
 * description:
 * author: JamFF
 * time: 2018/12/12 22:32
 */
public class LoginResultBean {

    /**
     * errcode : 10000
     * msg : ok
     * data : {"device_id":"59726D1FBA65904D5929FA5AEA3EB8F2","device_no":"victory2","merchant_no":"1180919113616472"}
     * sign : 1A54A511060A3650A80E2ED8C2E69FEC
     */

    private int errcode;
    private String msg;
    private DataBean data;
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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public static class DataBean {
        /**
         * device_id : 59726D1FBA65904D5929FA5AEA3EB8F2
         * device_no : victory2
         * merchant_no : 1180919113616472
         */

        private String device_id;// 终端id
        private String device_no;// 终端号
        private String merchant_no;// 商户号

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getDevice_no() {
            return device_no;
        }

        public void setDevice_no(String device_no) {
            this.device_no = device_no;
        }

        public String getMerchant_no() {
            return merchant_no;
        }

        public void setMerchant_no(String merchant_no) {
            this.merchant_no = merchant_no;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "device_id='" + device_id + '\'' +
                    ", device_no='" + device_no + '\'' +
                    ", merchant_no='" + merchant_no + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LoginResultBean{" +
                "errcode=" + errcode +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", sign='" + sign + '\'' +
                '}';
    }
}
