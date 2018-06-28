package cn.kuwo.player.util;

import java.util.ArrayList;
import java.util.List;

public class CONST {

    public static final class APIURL {
        public static final String DOMAIN="https://api.aobeef.cn";
        public static final String PREDOMAIN="https://preleaseapi.aobeef.cn";
        public static final String HOST="http://192.168.1.66:5000";
        public static final String ROUTER="/api/v1/";
    }
    public static final class NB {
        public static final Double MEATDiSCOUNT=0.5;
        public static final Double OTHERDISCOUNT=0.88;
    }
    public static final class UserCode {
        public static final int SCANCASHIER = 0;
        public static final int SCANCUSTOMER = 1;
        public static final int SCANUSER = 2;

    }

    public static final class ACCOUNT {
        public static final String SYSTEMACCOUNT = "5a27ba4a1579a30062c826bf";//13888888888;
    }

    public static final class StaffCode {
        public static final int cashier = 6;
    }

    public static final class OrderState {
        public static final String ORDER_STATUS_FINSIH = "577b34cda34131006188b821";
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

    public static final int STORECODE = 1;

    public static final String ADDRESS = "瑞平路230号保利时光里B2-18";

    public static final String TEL = "021-5496887";

    public static final int[] GIVETYPES = {8};

    public static final int ForzeenMeatType = 7;

    public static final String[] TESTUSERID = {"56f8cd772e958a005a2a141e", "56f8ef2cc4c971005bbf7235", "5655460f00b0bf379ee81d75", "56f8cb9571cfe4005c8cf7fc", "56f8c3172e958a005ae2dde9"};

    public static final boolean isShowTEST = false;//是否展示测试账号数据

    public static final String MACHINEID = "5b2c7a6dee920a003bdee3df";//加工费

    public static final String EXPLODEID="5b1f57e1a22b9d003a46667d";//爆款商品d

    public static final int NEARBYSTAFFRATE=88;//附近员工折扣

    public static final Boolean ISMACHINE=false;//是否选择加费

}
