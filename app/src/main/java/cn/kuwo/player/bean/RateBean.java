package cn.kuwo.player.bean;

public class RateBean {
    private int rate;
    private String content;

    public RateBean(int rate, String content) {
        this.rate = rate;
        this.content = content;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "RateBean{" +
                "rate=" + rate +
                ", content='" + content + '\'' +
                '}';
    }
}
