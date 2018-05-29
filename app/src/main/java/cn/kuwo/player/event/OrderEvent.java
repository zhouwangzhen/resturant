package cn.kuwo.player.event;

import com.avos.avoscloud.AVObject;

public class OrderEvent {
    private AVObject avObject;

    public OrderEvent(AVObject avObject) {
        this.avObject = avObject;
    }

    public AVObject getAvObject() {
        return avObject;
    }

    public void setAvObject(AVObject avObject) {
        this.avObject = avObject;
    }

    @Override
    public String toString() {
        return "OrderEvent{" +
                "avObject=" + avObject +
                '}';
    }
}
