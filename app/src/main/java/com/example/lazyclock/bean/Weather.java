package com.example.lazyclock.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/22.
 */
public class Weather {
    private BasicBean basic;
    private NowBean now;
    private List<DailyBean> dailys;

    public Weather() {
        basic = new BasicBean();
        now = new NowBean();
        dailys = new ArrayList<>();
    }

    public BasicBean getBasic() {
        return basic;
    }

    public void setBasic(BasicBean basic) {
        this.basic = basic;
    }

    public List<DailyBean> getDaily() {
        return dailys;
    }

    public void setDaily(List<DailyBean> dailys) {
        this.dailys = dailys;
    }

    public NowBean getNow() {
        return now;
    }

    public void setNow(NowBean now) {
        this.now = now;
    }

    public static class BasicBean {
        String city;
        String cnty;


        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCnty() {
            return cnty;
        }

        public void setCnty(String cnty) {
            this.cnty = cnty;
        }


    }

    public static class NowBean {
        String date;
        String temp;
        String text;
        String qlty;
        String fluMessage;

        public String getQlty() {
            return qlty;
        }

        public void setQlty(String qlty) {
            this.qlty = qlty;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getFluMessage() {
            return fluMessage;
        }

        public void setFluMessage(String fluMessage) {
            this.fluMessage = fluMessage;
        }

        public String getTemp() {
            return temp;
        }

        public void setTemp(String temp) {
            this.temp = temp;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class DailyBean {
        String date;
        String temp;
        String text;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTemp() {
            return temp;
        }

        public void setTemp(String temp) {
            this.temp = temp;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
