package cn.kuwo.player.service.entity;

import java.util.List;

/**
 * Created by lovely on 2018/7/26
 */
public class ConsumpteLog {


    private int consumption_count;
    private LastStoreOrderBean last_store_order;
    private List<String> top_three_store_commodities;

    public int getConsumption_count() {
        return consumption_count;
    }

    public void setConsumption_count(int consumption_count) {
        this.consumption_count = consumption_count;
    }

    public LastStoreOrderBean getLast_store_order() {
        return last_store_order;
    }

    public void setLast_store_order(LastStoreOrderBean last_store_order) {
        this.last_store_order = last_store_order;
    }

    public List<String> getTop_three_store_commodities() {
        return top_three_store_commodities;
    }

    public void setTop_three_store_commodities(List<String> top_three_store_commodities) {
        this.top_three_store_commodities = top_three_store_commodities;
    }

    public static class LastStoreOrderBean {
        /**
         * commodity : ["5b2878492f301e0035212806"]
         * commodityDetail : [{"id":"5b2878492f301e0035212806","name":"Q弹牛肉粒","nb":40.8,"number":1,"price":68,"weight":0}]
         * createdAt : 2018-07-19T11:34:32.510000+08:00
         */

        private String createdAt;
        private List<String> commodity;
        private List<CommodityDetailBean> commodityDetail;

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public List<String> getCommodity() {
            return commodity;
        }

        public void setCommodity(List<String> commodity) {
            this.commodity = commodity;
        }

        public List<CommodityDetailBean> getCommodityDetail() {
            return commodityDetail;
        }

        public void setCommodityDetail(List<CommodityDetailBean> commodityDetail) {
            this.commodityDetail = commodityDetail;
        }

        public static class CommodityDetailBean {
            /**
             * id : 5b2878492f301e0035212806
             * name : Q弹牛肉粒
             * nb : 40.8
             * number : 1
             * price : 68
             * weight : 0
             */

            private String id;
            private String name;
            private double nb;
            private double number;
            private double price;
            private double weight;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public double getNb() {
                return nb;
            }

            public void setNb(double nb) {
                this.nb = nb;
            }

            public double getNumber() {
                return number;
            }

            public void setNumber(double number) {
                this.number = number;
            }

            public double getPrice() {
                return price;
            }

            public void setPrice(double price) {
                this.price = price;
            }

            public double getWeight() {
                return weight;
            }

            public void setWeight(double weight) {
                this.weight = weight;
            }

            @Override
            public String toString() {
                return "CommodityDetailBean{" +
                        "id='" + id + '\'' +
                        ", name='" + name + '\'' +
                        ", nb=" + nb +
                        ", number=" + number +
                        ", price=" + price +
                        ", weight=" + weight +
                        '}';
            }
        }
    }

    @Override
    public String toString() {
        return "ConsumpteLog{" +
                "consumption_count=" + consumption_count +
                ", last_store_order=" + last_store_order +
                ", top_three_store_commodities=" + top_three_store_commodities +
                '}';
    }
}
