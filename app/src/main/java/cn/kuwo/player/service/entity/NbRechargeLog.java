package cn.kuwo.player.service.entity;

/**
 * Created by lovely on 2018/7/1
 */
public class NbRechargeLog {


    private Double acctually_paid;
    private Double amount;
    private CashierBean cashier;
    private String cashier_id;
    private long created_at;
    private MessageBean message;
    private String operator_id;
    private SalesManBean salesman;
    private String salesman_id;
    private int status;
    private int store;
    private TargetUserBean target_user;
    private String target_user_id;
    private long updated_at;
    private int payment;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getAcctually_paid() {
        return acctually_paid;
    }

    public void setAcctually_paid(Double acctually_paid) {
        this.acctually_paid = acctually_paid;
    }

    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public CashierBean getCashier() {
        return cashier;
    }

    public void setCashier(CashierBean cashier) {
        this.cashier = cashier;
    }

    public String getCashier_id() {
        return cashier_id;
    }

    public void setCashier_id(String cashier_id) {
        this.cashier_id = cashier_id;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public MessageBean getMessage() {
        return message;
    }

    public void setMessage(MessageBean message) {
        this.message = message;
    }

    public String getOperator_id() {
        return operator_id;
    }

    public void setOperator_id(String operator_id) {
        this.operator_id = operator_id;
    }

    public SalesManBean getSalesman() {
        return salesman;
    }

    public void setSalesman(SalesManBean salesman) {
        this.salesman = salesman;
    }

    public Object getSalesman_id() {
        return salesman_id;
    }

    public void setSalesman_id(String salesman_id) {
        this.salesman_id = salesman_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStore() {
        return store;
    }

    public void setStore(int store) {
        this.store = store;
    }

    public TargetUserBean getTarget_user() {
        return target_user;
    }

    public void setTarget_user(TargetUserBean target_user) {
        this.target_user = target_user;
    }

    public String getTarget_user_id() {
        return target_user_id;
    }

    public void setTarget_user_id(String target_user_id) {
        this.target_user_id = target_user_id;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }

    public static class CashierBean {
        /**
         * real_name : 张睿提
         * username : 13791000673
         */

        private String real_name;
        private String username;

        public String getReal_name() {
            return real_name;
        }

        public void setReal_name(String real_name) {
            this.real_name = real_name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        @Override
        public String toString() {
            return "CashierBean{" +
                    "real_name='" + real_name + '\'' +
                    ", username='" + username + '\'' +
                    '}';
        }
    }
    public static class SalesManBean {
        /**
         * real_name : 张睿提
         * username : 13791000673
         */

        private String real_name;
        private String username;

        public String getReal_name() {
            return real_name;
        }

        public void setReal_name(String real_name) {
            this.real_name = real_name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        @Override
        public String toString() {
            return "CashierBean{" +
                    "real_name='" + real_name + '\'' +
                    ", username='" + username + '\'' +
                    '}';
        }
    }



    public static class MessageBean {
        private String gift_reason;

        public String getGift_reason() {
            return gift_reason;
        }

        public void setGift_reason(String gift_reason) {
            this.gift_reason = gift_reason;
        }

        @Override
        public String toString() {
            return "MessageBean{" +
                    "gift_reason='" + gift_reason + '\'' +
                    '}';
        }
    }

    public static class TargetUserBean {


        private String real_name;
        private String username;

        public String getReal_name() {
            return real_name;
        }

        public void setReal_name(String real_name) {
            this.real_name = real_name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        @Override
        public String toString() {
            return "TargetUserBean{" +
                    "real_name='" + real_name + '\'' +
                    ", username='" + username + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NbRechargeLog{" +
                "amount=" + amount +
                ", cashier=" + cashier +
                ", cashier_id='" + cashier_id + '\'' +
                ", created_at=" + created_at +
                ", message=" + message +
                ", operator_id='" + operator_id + '\'' +
                ", salesman=" + salesman +
                ", salesman_id=" + salesman_id +
                ", status=" + status +
                ", store=" + store +
                ", target_user=" + target_user +
                ", target_user_id='" + target_user_id + '\'' +
                ", updated_at=" + updated_at +
                '}';
    }
}
