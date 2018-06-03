package cn.kuwo.player.bean;

public class CardBean {
    private String card;

    public CardBean(String card) {
        this.card = card;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    @Override
    public String toString() {
        return "CardBean{" +
                "card='" + card + '\'' +
                '}';
    }
}
