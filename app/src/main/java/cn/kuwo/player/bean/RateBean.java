package cn.kuwo.player.bean;

public class RateBean {
    private int rate;

    public RateBean(int rate) {
        this.rate = rate;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "RateBean{" +
                "rate=" + rate +
                '}';
    }
}
