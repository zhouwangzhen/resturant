package cn.kuwo.player.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.base.BaseFragment;
import cn.kuwo.player.print.Bill;
import cn.kuwo.player.print.Pos;
import cn.kuwo.player.util.SharedHelper;

public class NetConnectFg extends BaseFragment implements CompoundButton.OnCheckedChangeListener, TextWatcher {
    private static String ARG_PARAM = "param_key";
    @BindView(R.id.group)
    TextView group;
    @BindView(R.id.ip_1)
    EditText ip1;
    @BindView(R.id.ip_2)
    EditText ip2;
    @BindView(R.id.ip_3)
    EditText ip3;
    @BindView(R.id.ip_4)
    EditText ip4;
    @BindView(R.id.group_kitchen)
    TextView groupKitchen;
    @BindView(R.id.ip_1_kitchen)
    EditText ip1Kitchen;
    @BindView(R.id.ip_2_kitchen)
    EditText ip2Kitchen;
    @BindView(R.id.ip_3_kitchen)
    EditText ip3Kitchen;
    @BindView(R.id.ip_4_kitchen)
    EditText ip4Kitchen;
    @BindView(R.id.test_printer)
    RelativeLayout testPrinter;
    @BindView(R.id.test_printer_kitchen)
    RelativeLayout testPrinterKitchen;
    @BindView(R.id.opem_cash_box)
    TextView opemCashBox;
    Unbinder unbinder;
    @BindView(R.id.search_print_ip)
    TextView searchPrintIp;
    @BindView(R.id.group_drink)
    TextView groupDrink;
    @BindView(R.id.ip_1_drink)
    EditText ip1Drink;
    @BindView(R.id.ip_2_drink)
    EditText ip2Drink;
    @BindView(R.id.ip_3_drink)
    EditText ip3Drink;
    @BindView(R.id.ip_4_drink)
    EditText ip4Drink;
    @BindView(R.id.group_cool)
    TextView groupCool;
    @BindView(R.id.test_printer_drink)
    RelativeLayout testPrinterDrink;
    @BindView(R.id.test_printer_cool)
    RelativeLayout testPrinterCool;
    @BindView(R.id.ip_1_cool)
    EditText ip1Cool;
    @BindView(R.id.ip_2_cool)
    EditText ip2Cool;
    @BindView(R.id.ip_3_cool)
    EditText ip3Cool;
    @BindView(R.id.ip_4_cool)
    EditText ip4Cool;

    private SharedHelper sharedHelper;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(MyApplication.getContextObject(), "IP连接失败", Toast.LENGTH_SHORT).show();

        }
    };

    public static NetConnectFg newInstance(String str) {
        NetConnectFg netConnectFg = new NetConnectFg();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM, str);
        netConnectFg.setArguments(bundle);
        return netConnectFg;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fg_netconnect;
    }

    @Override
    public void initData() {
        ip1.addTextChangedListener(this);
        ip2.addTextChangedListener(this);
        ip3.addTextChangedListener(this);
        ip4.addTextChangedListener(this);
        ip1Kitchen.addTextChangedListener(this);
        ip2Kitchen.addTextChangedListener(this);
        ip3Kitchen.addTextChangedListener(this);
        ip4Kitchen.addTextChangedListener(this);
        ip1Drink.addTextChangedListener(this);
        ip2Drink.addTextChangedListener(this);
        ip3Drink.addTextChangedListener(this);
        ip4Drink.addTextChangedListener(this);
        ip1Cool.addTextChangedListener(this);
        ip2Cool.addTextChangedListener(this);
        ip3Cool.addTextChangedListener(this);
        ip4Cool.addTextChangedListener(this);
        sharedHelper = new SharedHelper(MyApplication.getContextObject());
        String ip1String = sharedHelper.read("ip1");
        String ip2String = sharedHelper.read("ip2");
        String ip3String = sharedHelper.read("ip3");
        String ip4String = sharedHelper.read("ip4");

        String ip1StringKitchen = sharedHelper.read("ip1_kitchen");
        String ip2StringKitchen = sharedHelper.read("ip2_kitchen");
        String ip3StringKitchen = sharedHelper.read("ip3_kitchen");
        String ip4StringKitchen = sharedHelper.read("ip4_kitchen");

        String ip1StringDrink = sharedHelper.read("ip1_drink");
        String ip2StringDrink = sharedHelper.read("ip2_drink");
        String ip3StringDrink = sharedHelper.read("ip3_drink");
        String ip4StringDrink = sharedHelper.read("ip4_drink");

        String ip1StringCool = sharedHelper.read("ip1_drink");
        String ip2StringCool = sharedHelper.read("ip2_drink");
        String ip3StringCool = sharedHelper.read("ip3_drink");
        String ip4StringCool = sharedHelper.read("ip4_drink");



        ip1.setText(ip1String);
        ip2.setText(ip2String);
        ip3.setText(ip3String);
        ip4.setText(ip4String);
        ip1Kitchen.setText(ip1StringKitchen);
        ip2Kitchen.setText(ip2StringKitchen);
        ip3Kitchen.setText(ip3StringKitchen);
        ip4Kitchen.setText(ip4StringKitchen);
        ip1Drink.setText(ip1StringDrink);
        ip2Drink.setText(ip2StringDrink);
        ip3Drink.setText(ip3StringDrink);
        ip4Drink.setText(ip4StringDrink);
        ip1Cool.setText(ip1StringCool);
        ip2Cool.setText(ip2StringCool);
        ip3Cool.setText(ip3StringCool);
        ip4Cool.setText(ip4StringCool);

    }

    @OnClick({R.id.test_printer, R.id.test_printer_kitchen, R.id.opem_cash_box, R.id.search_print_ip, R.id.test_printer_drink, R.id.test_printer_cool})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.test_printer:
                testPrinter();
                break;
            case R.id.test_printer_kitchen:
                testKitchenPrinter();
                break;
            case R.id.opem_cash_box:
                openBox();
                break;
            case R.id.search_print_ip:
                checkAllPrint();
                break;
            case R.id.test_printer_drink:
                testDrinkPrinter();
                break;
            case R.id.test_printer_cool:
                tesCoolPrinter();
                break;
        }
    }

    private void tesCoolPrinter() {
        new Thread() {
            @Override
            public void run() {
                try {
                    SharedHelper sharedHelper = new SharedHelper(MyApplication.getContextObject());
                    String url = SharedHelper.read("ip1_cool") + "." + SharedHelper.read("ip2_cool") + "." + SharedHelper.read("ip3_cool") + "." + SharedHelper.read("ip4_cool");
                    Pos pos = null;
                    pos = new Pos(url, 9100, "GBK");    //第一个参数是打印机网口IP
                    pos.initPos();
                    pos.printText("我是冷菜间打印机测试" + url);
                    pos.printLine(2);
                    pos.feedAndCut();
                    pos.closeIOAndSocket();

                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(1);
                }
            }
        }.start();
    }

    private void testDrinkPrinter() {
        new Thread() {
            @Override
            public void run() {
                try {
                    String url = sharedHelper.read("ip1_drink") + "." + sharedHelper.read("ip2_drink") + "." + sharedHelper.read("ip3_drink") + "." + sharedHelper.read("ip4_drink");
                    Logger.d(url);
                    Pos pos;
                    pos = new Pos(url, 9100, "GBK");    //第一个参数是打印机网口IP
                    pos.initPos();
                    pos.printLine(4);
                    pos.printText("我是水吧打印机测试" + url);
                    pos.printLine(1);
                    pos.feedAndCut();
                    pos.closeIOAndSocket();
                } catch (Exception e) {
                    Logger.d(e.getMessage());
                    mHandler.sendEmptyMessage(1);
                }
            }
        }.start();
    }

    private void openBox() {
        Bill.openMoneyBox(MyApplication.getContextObject());
    }

    /**
     * 测试打印机是否连接
     */
    private void testPrinter() {
        new Thread() {
            @Override
            public void run() {
                try {
                    String url = sharedHelper.read("ip1") + "." + sharedHelper.read("ip2") + "." + sharedHelper.read("ip3") + "." + sharedHelper.read("ip4");
                    Logger.d(url);
                    Pos pos;
                    pos = new Pos(url, 9100, "GBK");    //第一个参数是打印机网口IP
                    pos.initPos();
                    pos.printLine(4);
                    pos.printText("我是帐台打印机测试" + url);
                    pos.printLine(1);
                    pos.feedAndCut();
                    pos.closeIOAndSocket();
                } catch (Exception e) {
                    Logger.d(e.getMessage());
                    mHandler.sendEmptyMessage(1);
                }
            }
        }.start();

    }

    private void testKitchenPrinter() {
        new Thread() {
            @Override
            public void run() {
                try {
                    SharedHelper sharedHelper = new SharedHelper(MyApplication.getContextObject());
                    String url = SharedHelper.read("ip1_kitchen") + "." + SharedHelper.read("ip2_kitchen") + "." + SharedHelper.read("ip3_kitchen") + "." + SharedHelper.read("ip4_kitchen");
                    Pos pos = null;
                    pos = new Pos(url, 9100, "GBK");    //第一个参数是打印机网口IP
                    pos.initPos();
                    pos.printText("我是厨房打印机测试" + url);
                    pos.printLine(2);
                    pos.feedAndCut();
                    pos.closeIOAndSocket();

                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(1);
                }
            }
        }.start();
    }

    private void checkAllPrint() {
        showDialog();
        for (int i = 1; i < 250; i++) {
            final int finalI = i;
            new Thread() {
                @Override
                public void run() {
                    try {
                        String url = "192.168.1." + finalI;
                        Pos pos = null;
                        pos = new Pos(url, 9100, "GBK");    //第一个参数是打印机网口IP
                        pos.initPos();
                        pos.printText("我的ip地址  " + url);
                        Logger.d(finalI);
                        pos.printLine(1);
                        pos.feedAndCut();
                        pos.closeIOAndSocket();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }.start();
            if (i == 249) {
                hideDialog();
            }
        }

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        int size1, size2, size3, size4;
        int size1_kitchen, size2_kitchen, size3_kitchen, size4_kitchen;
        int size1_drink, size2_drink, size3_drink, size4_drink;
        int size1_cool, size2_cool, size3_cool, size4_cool;
        size1 = ip1.getText().toString().trim().length();
        size2 = ip2.getText().toString().trim().length();
        size3 = ip3.getText().toString().trim().length();
        size4 = ip4.getText().toString().trim().length();
        size1_kitchen = ip1Kitchen.getText().toString().trim().length();
        size2_kitchen = ip2Kitchen.getText().toString().trim().length();
        size3_kitchen = ip3Kitchen.getText().toString().trim().length();
        size4_kitchen = ip4Kitchen.getText().toString().trim().length();
        size1_drink = ip1Drink.getText().toString().trim().length();
        size2_drink = ip2Drink.getText().toString().trim().length();
        size3_drink = ip3Drink.getText().toString().trim().length();
        size4_drink = ip4Drink.getText().toString().trim().length();

        size1_cool = ip1Cool.getText().toString().trim().length();
        size2_cool = ip2Cool.getText().toString().trim().length();
        size3_cool = ip3Cool.getText().toString().trim().length();
        size4_cool = ip4Cool.getText().toString().trim().length();
        if (size1 == 3 && size2 == 3 && size3 == 1 && (size4 == 2 || size4 == 3 || size4 == 1)) {
            sharedHelper.save("ip1", ip1.getText().toString().trim());
            sharedHelper.save("ip2", ip2.getText().toString().trim());
            sharedHelper.save("ip3", ip3.getText().toString().trim());
            sharedHelper.save("ip4", ip4.getText().toString().trim());
        }

        if (size1_kitchen == 3 && size2_kitchen == 3 && size3_kitchen == 1 && (size4_kitchen == 2 || size4_kitchen == 3 || size4_kitchen == 1)) {
            sharedHelper.save("ip1_kitchen", ip1Kitchen.getText().toString().trim());
            sharedHelper.save("ip2_kitchen", ip2Kitchen.getText().toString().trim());
            sharedHelper.save("ip3_kitchen", ip3Kitchen.getText().toString().trim());
            sharedHelper.save("ip4_kitchen", ip4Kitchen.getText().toString().trim());
        }
        if (size1_drink == 3 && size2_drink == 3 && size3_drink == 1 && (size4_drink == 2 || size4_drink == 3 || size4_drink == 1)) {
            sharedHelper.save("ip1_drink", ip1Drink.getText().toString().trim());
            sharedHelper.save("ip2_drink", ip2Drink.getText().toString().trim());
            sharedHelper.save("ip3_drink", ip3Drink.getText().toString().trim());
            sharedHelper.save("ip4_drink", ip4Drink.getText().toString().trim());
        }
        if (size1_cool == 3 && size2_cool == 3 && size3_cool == 1 && (size4_cool == 2 || size4_cool == 3 || size4_cool == 1)) {
            sharedHelper.save("ip1_cool", ip1Cool.getText().toString().trim());
            sharedHelper.save("ip2_cool", ip2Cool.getText().toString().trim());
            sharedHelper.save("ip3_cool", ip3Cool.getText().toString().trim());
            sharedHelper.save("ip4_cool", ip4Cool.getText().toString().trim());
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}