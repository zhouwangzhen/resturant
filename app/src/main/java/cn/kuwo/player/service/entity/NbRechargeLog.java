package cn.kuwo.player.service.entity;

import java.util.List;

/**
 * Created by lovely on 2018/7/1
 */
public class NbRechargeLog {

    private List<NiuTokenOfflineOperationsBean> niu_token_offline_operations;

    public List<NiuTokenOfflineOperationsBean> getNiu_token_offline_operations() {
        return niu_token_offline_operations;
    }

    public void setNiu_token_offline_operations(List<NiuTokenOfflineOperationsBean> niu_token_offline_operations) {
        this.niu_token_offline_operations = niu_token_offline_operations;
    }

    public static class NiuTokenOfflineOperationsBean {
        /**
         * amount : 2000
         * cashier : {"real_name":"肖发","username":"18523551512"}
         * cashier_id : 5ad8564f1b69e600670cce44
         * created_at : 1530438313
         * message : {}
         * op_type : 4
         * operator_id : 5a27ba4a1579a30062c826bf
         * salesman : {"real_name":"肖发","username":"18523551512"}
         * salesman_id : 5ad8564f1b69e600670cce44
         * status : 2
         * store : 2
         * target_user : {"real_name":null,"username":"13681752111"}
         * target_user_id : 5aed9b6c9f54546d18e13db9
         * updated_at : 1530438313
         */

        private int amount;
        private CashierBean cashier;
        private String cashier_id;
        private long created_at;
        private MessageBean message;
        private int op_type;
        private String operator_id;
        private SalesmanBean salesman;
        private String salesman_id;
        private int status;
        private int store;
        private TargetUserBean target_user;
        private String target_user_id;
        private long updated_at;

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
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

        public int getOp_type() {
            return op_type;
        }

        public void setOp_type(int op_type) {
            this.op_type = op_type;
        }

        public String getOperator_id() {
            return operator_id;
        }

        public void setOperator_id(String operator_id) {
            this.operator_id = operator_id;
        }

        public SalesmanBean getSalesman() {
            return salesman;
        }

        public void setSalesman(SalesmanBean salesman) {
            this.salesman = salesman;
        }

        public String getSalesman_id() {
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
        }

        public static class MessageBean {
        }

        public static class SalesmanBean {
            /**
             * real_name : 肖发
             * username : 18523551512
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
        }

        public static class TargetUserBean {
            /**
             * real_name : null
             * username : 13681752111
             */

            private Object real_name;
            private String username;

            public Object getReal_name() {
                return real_name;
            }

            public void setReal_name(Object real_name) {
                this.real_name = real_name;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }
        }

        @Override
        public String toString() {
            return "NiuTokenOfflineOperationsBean{" +
                    "amount=" + amount +
                    ", cashier=" + cashier +
                    ", cashier_id='" + cashier_id + '\'' +
                    ", created_at=" + created_at +
                    ", message=" + message +
                    ", op_type=" + op_type +
                    ", operator_id='" + operator_id + '\'' +
                    ", salesman=" + salesman +
                    ", salesman_id='" + salesman_id + '\'' +
                    ", status=" + status +
                    ", store=" + store +
                    ", target_user=" + target_user +
                    ", target_user_id='" + target_user_id + '\'' +
                    ", updated_at=" + updated_at +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NbRechargeLog{" +
                "niu_token_offline_operations=" + niu_token_offline_operations +
                '}';
    }
}
