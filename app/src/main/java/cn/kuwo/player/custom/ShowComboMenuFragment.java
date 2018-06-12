package cn.kuwo.player.custom;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.event.ComboEvent;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ObjectUtil;
import cn.kuwo.player.util.ProductUtil;

public class ShowComboMenuFragment extends DialogFragment implements View.OnClickListener {
    private List<List<String>> comboMenu;
    private QMUITipDialog tipDialog;
    private LinearLayout llRoot, llComment;
    private View view;
    private TextView title, price, close, addbt, subbt, tvNumber, giveName, comboDetail;
    private LinearLayout llHasCombo, llChooseCombo, llChooseSerial;
    private ScrollView llRootPage;
    private EditText editRemark;
    private FlowRadioGroup radioSerial;
    private List<Integer> chooseTypes = new ArrayList<>();
    private Button btnEnsure;
    private ProductBean productBean;
    private Object order;
    private Context context;
    private Double commodityNumber = 1.0;
    private int cookSerial = -1;
    private int orderIndex = -1;
    private Boolean isEdit = false;
    private Boolean isComobo = true;

    @SuppressLint("ValidFragment")
    public ShowComboMenuFragment(Context context, ProductBean productBean, Boolean isEdit) {
        this.comboMenu = ProductUtil.getComboList(productBean.getComboMenu());
        this.context = context;
        this.productBean = productBean;
        this.isEdit = isEdit;
        if (productBean.getComboMenu().length() > 0 && productBean.getComboMenu() != null) {
            isComobo = true;
        } else {
            isComobo = false;
        }
        for (int i = 0; i < comboMenu.size(); i++) {
            chooseTypes.add(0);
        }

    }

    @SuppressLint("ValidFragment")
    public ShowComboMenuFragment(Context context, ProductBean productBean, Boolean isEdit, Object order, int orderIndex) {
        this.comboMenu = ProductUtil.getComboList(productBean.getComboMenu());
        this.isComobo = false;
        this.context = context;
        this.productBean = productBean;
        this.isEdit = isEdit;
        this.order = order;
        this.orderIndex = orderIndex;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.fragment_showcombo_menu, container);
        findView();
        setCombo();
        initData();
        setListener();
        return view;
    }

    private void setListener() {

        btnEnsure.setOnClickListener(this);
        close.setOnClickListener(this);
        addbt.setOnClickListener(this);
        subbt.setOnClickListener(this);
        radioSerial.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                cookSerial = radioSerial.indexOfChild(group.findViewById(checkedId)) + 1;
            }
        });
    }

    private void setCombo() {
        if (isComobo) {
            String detail = "";
            for (int i = 0; i < comboMenu.size(); i++) {
                RadioGroup radioGroup = new RadioGroup(getActivity());
                radioGroup.setOrientation(LinearLayout.HORIZONTAL);
                for (int j = 0; j < comboMenu.get(i).size(); j++) {
                    RadioButton radioButton = new RadioButton(getActivity());
                    RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                    radioButton.setLayoutParams(layoutParams);
                    radioButton.setText(comboMenu.get(i).get(j));
                    radioButton.setTextSize(12);
                    radioButton.setGravity(Gravity.CENTER);
                    radioButton.setPadding(20, 20, 20, 20);
                    radioGroup.addView(radioButton);

                }
                ((RadioButton) radioGroup.getChildAt(chooseTypes.get(i))).setChecked(true);
                final int finalI = i;
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        chooseTypes.set(finalI, group.indexOfChild(group.findViewById(checkedId)));
                        String detail = "";
                        for (int k = 0; k < chooseTypes.size(); k++) {
                            detail += (k == 0 ? "" : "+") + comboMenu.get(k).get(chooseTypes.get(k));
                        }
                        comboDetail.setText(detail);
                    }
                });
                llRoot.addView(radioGroup);
                detail += (i == 0 ? "" : "+") + comboMenu.get(i).get(chooseTypes.get(i));
            }
            comboDetail.setText(detail);
        } else {
            comboDetail.setVisibility(View.GONE);
        }
        llRootPage.smoothScrollTo(0, 0);
    }

    private void initData() {
        tipDialog = new QMUITipDialog.Builder(getActivity())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载中")
                .create();
        title.setText(productBean.getSerial() + "  " + productBean.getName());
        for (int i = 0; i < productBean.getComments().size(); i++) {
            Button button = new Button(context);
            button.setText(productBean.getComments().get(i).toString());
            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editRemark.getText().toString().trim().length() == 0) {
                        editRemark.setText(productBean.getComments().get(finalI).toString());
                    } else {
                        editRemark.setText(editRemark.getText().toString() + "+" + productBean.getComments().get(finalI).toString());
                    }

                }
            });
            llComment.addView(button);
        }

    }

    private void findView() {
        title = view.findViewById(R.id.title);
        price = view.findViewById(R.id.price);
        close = view.findViewById(R.id.close);
        giveName = view.findViewById(R.id.give_name);
        radioSerial = view.findViewById(R.id.radio_serial);
        llRoot = view.findViewById(R.id.ll_root);
        llComment = view.findViewById(R.id.ll_comment);
        tvNumber = view.findViewById(R.id.tv_number);
        comboDetail = view.findViewById(R.id.combo_detail);
        llRootPage = view.findViewById(R.id.ll_root_page);
        llHasCombo = view.findViewById(R.id.ll_has_combo);
        llChooseSerial = view.findViewById(R.id.ll_choose_serial);
        llChooseCombo = view.findViewById(R.id.ll_choose_combo);
        editRemark = view.findViewById(R.id.edit_remark);
        subbt = view.findViewById(R.id.subbt);
        addbt = view.findViewById(R.id.addbt);
        btnEnsure = view.findViewById(R.id.btn_ensure);
        if (isEdit) {
            HashMap<String, Object> format = ObjectUtil.format(order);
            int cookSerial = ObjectUtil.getInt(format, "cookSerial");
            if (cookSerial == 1) radioSerial.check(R.id.serial_1);
            if (cookSerial == 2) radioSerial.check(R.id.serial_2);
            if (cookSerial == 3) radioSerial.check(R.id.serial_3);
            if (cookSerial == 4) radioSerial.check(R.id.serial_4);
            if (cookSerial == 5) radioSerial.check(R.id.serial_5);
        }
        if (order != null && isEdit) {
            HashMap<String, Object> format = ObjectUtil.format(order);
            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
            Double number = ObjectUtil.getDouble(format, "number");
            commodityNumber = number;
            price.setText("￥" + MyUtils.formatDouble(number * productBean.getPrice()));
            editRemark.setText(ObjectUtil.getString(format, "comment"));
            tvNumber.setText(commodityNumber + "");
            List<String> comboList = ObjectUtil.getList(format, "comboList");
            if (comboMenu.size()>0&&format.containsKey("comboList") && comboList != null && comboList.size() > 0) {
                isComobo = true;
                llHasCombo.setVisibility(View.VISIBLE);
                llChooseCombo.setVisibility(View.VISIBLE);
                String content = "";
                for (int i = 0; i < comboList.size(); i++) {
                    int index = comboMenu.get(i).indexOf(comboList.get(i)) == -1 ? 0 : comboMenu.get(i).indexOf(comboList.get(i));
                    if (i > 0) {
                        content += "+" + comboList.get(i);
                    } else {
                        content += comboList.get(i);
                    }
                    chooseTypes.add(index);
                }
                comboDetail.setText(content);
            } else {
                isComobo = false;
                for (int i = 0; i < comboMenu.size(); i++) {
                    chooseTypes.add(0);
                }
            }
        } else {
            price.setText("￥" + productBean.getPrice());
        }
        if (productBean.getGivecode().length() > 0 && MyUtils.getProductById(productBean.getGivecode()) != null) {
            try {
                String giveContent = "";
                if (productBean.getGiveRule() == 1) {
                    giveContent = "(会员专享)";
                } else if (productBean.getGiveRule() == 2) {
                    giveContent = "(超牛会员专享)";
                }
                giveName.setText(MyUtils.getProductById(productBean.getGivecode()).getName() + giveContent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (isComobo) {
            boolean ableChoose = false;
            for (int i = 0; i < comboMenu.size(); i++) {
                if (comboMenu.get(i).size() > 1) {
                    ableChoose = true;
                }
            }
            llHasCombo.setVisibility(View.VISIBLE);
            if (ableChoose) {
                llChooseCombo.setVisibility(View.VISIBLE);
            } else {
                llChooseCombo.setVisibility(View.GONE);
                String content = "";
                for (int i = 0; i < comboMenu.size(); i++) {
                    if (i > 0) {
                        content += "+" + comboMenu.get(chooseTypes.get(i));
                    } else {
                        content += comboMenu.get(chooseTypes.get(i));
                    }
                }
                comboDetail.setText(content);
            }

        } else {
            llHasCombo.setVisibility(View.GONE);
            llChooseCombo.setVisibility(View.GONE);
        }
        if (productBean.getType() == 4) {
            llChooseSerial.setVisibility(View.VISIBLE);
        } else {
            llChooseSerial.setVisibility(View.GONE);
        }


    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.75), (int) (dm.widthPixels * 0.5));
            final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            getDialog().getWindow().setAttributes(layoutParams);
            getDialog().setCanceledOnTouchOutside(false);
        }
    }

    public void showDialog() {
        tipDialog.show();
    }

    public void hideDialog() {
        if (tipDialog != null) {
            tipDialog.dismiss();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ensure:
                List<String> chooseIds = new ArrayList<>();
                if (comboMenu.size() > 0 && comboMenu != null) {
                    for (int i = 0; i < chooseTypes.size(); i++) {
                        chooseIds.add(comboMenu.get(i).get(chooseTypes.get(i)));
                    }
                }
                EventBus.getDefault().post(new ComboEvent(
                        productBean,
                        chooseIds,
                        cookSerial,
                        editRemark.getText().toString(),
                        commodityNumber,
                        isEdit,
                        orderIndex)
                );
                getDialog().dismiss();
            case R.id.close:
                getDialog().dismiss();
                break;
            case R.id.subbt:
                if (isEdit) {
                    if (Double.parseDouble(tvNumber.getText().toString().trim()) >= 1) {
                        tvNumber.setText(Double.parseDouble(tvNumber.getText().toString().trim()) - 1.0 + "");
                        commodityNumber = Double.parseDouble(tvNumber.getText().toString().trim());
                        price.setText("￥" + MyUtils.formatDouble(commodityNumber * productBean.getPrice()));
                    }
                } else {
                    if (Double.parseDouble(tvNumber.getText().toString().trim()) >= 2) {
                        tvNumber.setText(Double.parseDouble(tvNumber.getText().toString().trim()) - 1.0 + "");
                        commodityNumber = Double.parseDouble(tvNumber.getText().toString().trim());
                        price.setText("￥" + MyUtils.formatDouble(commodityNumber * productBean.getPrice()));
                    }
                }
                if (commodityNumber >= 1) {
                    btnEnsure.setText("添加");
                    btnEnsure.setBackgroundColor(getResources().getColor(R.color.material_green));
                } else {
                    btnEnsure.setText("删除");
                    btnEnsure.setBackgroundColor(getResources().getColor(R.color.red));

                }
                break;
            case R.id.addbt:
                tvNumber.setText(Double.parseDouble(tvNumber.getText().toString().trim()) + 1.0 + "");
                commodityNumber = Double.parseDouble(tvNumber.getText().toString().trim());
                price.setText("￥" + MyUtils.formatDouble(commodityNumber * productBean.getPrice()));
                if (commodityNumber >= 1) {
                    btnEnsure.setText("添加");
                    btnEnsure.setBackgroundColor(getResources().getColor(R.color.material_green));
                }
                break;
        }
    }
}
