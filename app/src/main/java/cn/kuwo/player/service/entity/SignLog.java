package cn.kuwo.player.service.entity;

/**
 * Created by lovely on 2018/7/24
 */
public class SignLog {

    /**
     * amount : 1
     * created_at : 1532413479
     * message : {}
     * status : 2
     * store : 2
     * target_user : {"id":"5655460f00b0bf379ee81d75","real_name":"张睿提","username":"13791000673"}
     * target_user_id : 5655460f00b0bf379ee81d75
     * updated_at : 1532413479
     */

    private int amount;
    private int created_at;
    private MessageBean message;
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

    public int getCreated_at() {
        return created_at;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    public MessageBean getMessage() {
        return message;
    }

    public void setMessage(MessageBean message) {
        this.message = message;
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

    public static class MessageBean {
    }

    public static class TargetUserBean {
        /**
         * id : 5655460f00b0bf379ee81d75
         * real_name : 张睿提
         * username : 13791000673
         */

        private String id;
        private String real_name;
        private String username;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

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
                    "id='" + id + '\'' +
                    ", real_name='" + real_name + '\'' +
                    ", username='" + username + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SignLog{" +
                "amount=" + amount +
                ", created_at=" + created_at +
                ", message=" + message +
                ", status=" + status +
                ", store=" + store +
                ", target_user=" + target_user +
                ", target_user_id='" + target_user_id + '\'' +
                ", updated_at=" + updated_at +
                '}';
    }
}
