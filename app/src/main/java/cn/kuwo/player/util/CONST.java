package cn.kuwo.player.util;


public class CONST {

    public static final class APIURL {
        public static final String DOMAIN="https://api.aobeef.cn";
        public static final String PREDOMAIN="https://preleaseapi.aobeef.cn";
        public static final String HOST="http://192.168.1.66:5000";
        public static final String ROUTER="/api/v1/";
    }
    public static final class NB {
        public static final Double MEATDiSCOUNT=0.6;//牛肉折扣
        public static final Double OTHERDISCOUNT=0.95;//其他折扣
    }
    public static final class UserCode {
        public static final int SCANCASHIER = 0;
        public static final int SCANCUSTOMER = 1;
        public static final int SCANUSER = 2;

    }

    public static final class ACCOUNT {
        public static final String SYSTEMACCOUNT = "5a27ba4a1579a30062c826bf";//13888888888;系统账号
    }

    public static final class StaffCode {
        public static final int cashier = 6;//可以登录的员工号
    }

    public static final class OrderState {
        public static final String ORDER_STATUS_FINSIH = "577b34cda34131006188b821";//订单已完成
        public static final String DELIVER_STATUS_FINSIH = "577b34d9165abd005529fdaf";
        public static final String ORDER_STATUS_CANCEL = "577b36ebc4c97100669cb248";//订单退款
    }

    public static final class SVIPSTYLE {
        public static final String DATE_1_MONTH = "5ad6b09f9f54540045285213";
        public static final String DATE_11_MONTH = "5ad6b0c517d0090062129b1f";
        public static final String DATE_12_MONTH = "5ad6aeef9f54540045284808";
    }

    public static final class POWERTYLE {
        public static final String EXPERIENCE = "5acc712117d00900619c1b20";
        public static final String MEMBER = "5acc7125ac502e213db18f8d";
    }
    public static final class MALLPAYMENTTYPE {
       public static final String NB_PAY="5b4c203667f35600352794f1";//牛币支付
        public static final String MIX_PAY="59794db8ac502e0069a377d0";//线下消费金/白条支付+第三方支付
        public static final String ONLINE_PAY="577b364a79bc440032772ba5";//消费金/白条支付
        public static final String OFFLINE_PAY="59794daf128fe10056f43170";//线下支付
    }
    public static final int STORECODE = 1;//店铺编号

    public static final String ADDRESS = "瑞平路230号保利时光里B2-18";

    public static final String TEL = "021-54968887";

    public static final int[] GIVETYPES = {13};

    public static final int ForzeenMeatType = 7;

    public static final String[] TESTUSERID = {"56f8cd772e958a005a2a141e", "56f8ef2cc4c971005bbf7235", "5655460f00b0bf379ee81d75", "56f8cb9571cfe4005c8cf7fc", "56f8c3172e958a005ae2dde9"};//测试账号

    public static final boolean isShowTEST = false;//是否展示测试账号数据

    public static final String MACHINEID = "5b2c7a6dee920a003bdee3df";//加工费

    public static final String EXPLODEID="5b1f57e1a22b9d003a46667d";//爆款商品id

    public static final String[] COUNTIDS=new String[]{"5b1f57e1a22b9d003a46667d","5b4f13750b616000311b9605"};//爆款商品id

    public static final int NEARBYSTAFFRATE=88;//附近员工折扣

    public static final Boolean ISMACHINE=false;//是否选择加费

    public static final class DZDP {
        public static final String menu_1_name="超值单人套餐(大众点评专享)";
        public static final String menu_1_id="5b35b93b756571003a4e5f50";
        public static final Double menu_1_price=188.0;

        public static final String menu_2_name="PAPA招牌牛肉汉堡配大蒜薯条(大众点评专享)";
        public static final String menu_2_id="5b35b8cfee920a003a0da220";
        public static final Double menu_2_price=48.0;

        public static final String menu_3_name="爆款西冷(大众点评)";
        public static final String menu_3_id="5b4f13750b616000311b9605";
        public static final Double menu_3_price=38.0;


    }

    public static final class ACTIVITYCOMMODITY {
        public static final String GROUPPAYBILL="5b3c40b32f301e005f76a62e";//你组团我买单商品
    }

}
