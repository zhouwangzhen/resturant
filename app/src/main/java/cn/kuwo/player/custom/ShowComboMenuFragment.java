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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
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
import cn.kuwo.player.bean.entity.SideDishEntity;
import cn.kuwo.player.event.ComboEvent;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ObjectUtil;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.RealmHelper;
import cn.kuwo.player.util.ToastUtil;
import io.realm.RealmList;

public class ShowComboMenuFragment extends DialogFragment implements View.OnClickListener {

    private QMUITipDialog tipDialog;
    private LinearLayout llRoot, llComment;
    private RelativeLayout rlChooseMachine;
    private Switch switchMachine;
    private TextView title, price, close, addbt, subbt, tvNumber, giveName, comboDetail, sideDishesDetail;
    private LinearLayout llHasCombo, llChooseCombo, llChooseSerial, llSideDishes;
    private ScrollView llRootPage;
    private EditText editRemark;
    private FlowRadioGroup radioSerial;
    private Button btnEnsure;

    private View view;

    private ProductBean productBean;
    private Object order;
    private Context context;

    private List<List<String>> comboMenu;
    private List<Integer> chooseTypes = new ArrayList<>();
    RealmList<SideDishEntity> sidedish = new RealmList<>();

    private Boolean isEdit = false;
    private Boolean isComobo = false;
    private Boolean isWeight = false;
    private String barcode = null;
    private String cookStyle = "";
    private int orginNumber = 0;
    private int cookSerial = -1;
    private int orderIndex = -1;
    private int sideDishIndex = -1;
    private double sideDishPrice = 0.0;
    private Double commodityNumber = 1.0;


    @SuppressLint("ValidFragment")
    public ShowComboMenuFragment(Context context, ProductBean productBean, Boolean isEdit, String barcode) {
        this.context = context;
        this.comboMenu = ProductUtil.getComboList(productBean.getComboMenu());
        this.productBean = productBean;
        this.isEdit = isEdit;
        this.barcode = barcode;

        if (productBean.getComboMenu().length() > 0 && productBean.getComboMenu() != null){
            isComobo = true;
            for (int i = 0; i < comboMenu.size(); i++) chooseTypes.add(0);
        }
        if (barcode.length() == 18) isWeight = true;
    }

    @SuppressLint("ValidFragment")
    public ShowComboMenuFragment(Context context, ProductBean productBean, Boolean isEdit, Object order, int orderIndex) {
        this.context = context;
        this.productBean = productBean;
        this.isEdit = isEdit;
        this.order = order;
        this.orderIndex = orderIndex;
        this.comboMenu = ProductUtil.getComboList(productBean.getComboMenu());
        HashMap<String, Object> format = ObjectUtil.format(order);
        if (productBean.getComboMenu().length() > 0 && productBean.getComboMenu() != null)
            isComobo = true;
        if (!isEdit) {
            for (int i = 0; i < comboMenu.size(); i++) chooseTypes.add(0);
        } else {
            List<String> comboList = ObjectUtil.getList(format, "comboList");
            chooseTypes.removeAll(chooseTypes);
            for (int i = 0; i < comboList.size(); i++) {
                int index = comboMenu.get(i).indexOf(comboList.get(i)) == -1 ? 0 : comboMenu.get(i).indexOf(comboList.get(i));
                chooseTypes.add(index);
            }
        }
        barcode = ObjectUtil.getString(format, "barcode");
        if (barcode.length() == 18) isWeight = true;
        sideDishIndex = ObjectUtil.getInt(format, "sideDishIndex");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.fragment_showcombo_menu, container);
        findView();
        setCombo();
        setSideDish();
        initData();
        setListener();
        return view;
    }


    private void setCombo() {
        if (isComobo) {
            setComboChoose();
        } else {
            comboDetail.setVisibility(View.GONE);
        }
    }

    private void initData() {
        llRootPage.smoothScrollTo(0, 0);
        title.setFocusable(true);
        if (isEdit) {
            HashMap<String, Object> format = ObjectUtil.format(order);
            int cookSerial = ObjectUtil.getInt(format, "cookSerial");
            orginNumber = ObjectUtil.getDouble(format, "number").intValue();
            switch (cookSerial) {
                case 1:
                    radioSerial.check(R.id.serial_1);
                    break;
                case 2:
                    radioSerial.check(R.id.serial_2);
                    break;
                case 3:
                    radioSerial.check(R.id.serial_3);
                    break;
                case 4:
                    radioSerial.check(R.id.serial_4);
                    break;
                case 5:
                    radioSerial.check(R.id.serial_5);
                    break;
            }
            if (productBean.getSidedish().size() > 0 && productBean.getSidedish().size() - 1 >= sideDishIndex && sideDishIndex >= 0) {
                sideDishPrice = productBean.getSidedish().get(sideDishIndex).getPrice();
                sideDishesDetail.setText(productBean.getSidedish().get(sideDishIndex).getName());
            }
        }
        if (order != null && isEdit) {
            HashMap<String, Object> format = ObjectUtil.format(order);
            if (productBean.getType() == 3 || productBean.getType() == 4) {
                radioSerial.setVisibility(View.GONE);
            }
            Double number = ObjectUtil.getDouble(format, "number");
            commodityNumber = number;
            if (isWeight) {
                price.setText("￥" + MyUtils.formatDouble(ProductUtil.calCommodityMoney(barcode) * number) + number * sideDishPrice);
            } else {
                price.setText("￥" + MyUtils.formatDouble(productBean.getPrice() * number + number * sideDishPrice));
            }
            editRemark.setText(ObjectUtil.getString(format, "comment"));
            tvNumber.setText(commodityNumber + "");
            List<String> comboList = ObjectUtil.getList(format, "comboList");
            if (comboMenu.size() > 0 && format.containsKey("comboList") && comboList != null && comboList.size() > 0) {
                isComobo = true;
                llHasCombo.setVisibility(View.VISIBLE);
                llChooseCombo.setVisibility(View.VISIBLE);
                String content = "";
                chooseTypes.removeAll(chooseTypes);
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
                chooseTypes.removeAll(chooseTypes);
                for (int i = 0; i < comboMenu.size(); i++) {
                    chooseTypes.add(0);
                }
            }
        } else {
            if (isWeight) {
                price.setText("￥" + MyUtils.formatDouble(ProductUtil.calCommodityMoney(barcode + commodityNumber * sideDishPrice)));
            } else {
                price.setText("￥" + MyUtils.formatDouble(productBean.getPrice() + sideDishPrice));
            }
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
        if (isWeight) {
            rlChooseMachine.setVisibility(View.VISIBLE);
        }
        setDialog();
        setTitle();
        setComment();
    }

    private void setComboChoose() {
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
                    Logger.d(chooseTypes);
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
        comboDetail.setVisibility(View.VISIBLE);
        comboDetail.setText(detail);
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
        switchMachine = view.findViewById(R.id.switch_mach);
        subbt = view.findViewById(R.id.subbt);
        addbt = view.findViewById(R.id.addbt);
        btnEnsure = view.findViewById(R.id.btn_ensure);
        rlChooseMachine = view.findViewById(R.id.rl_choose_machine);
        sideDishesDetail = view.findViewById(R.id.side_dishes_detail);
        llSideDishes = view.findViewById(R.id.ll_side_dishes);
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
                SideDishEntity sideDishEntity = null;
                Logger.d(sidedish.size());
                Logger.d(sideDishIndex);
                if (sidedish.size() > 0 && sideDishIndex != -1) {
                    sideDishEntity = sidedish.get(sideDishIndex);
                }
                EventBus.getDefault().post(new ComboEvent(
                                productBean,
                                chooseIds,
                                cookSerial,
                                editRemark.getText().toString(),
                                commodityNumber,
                                isEdit,
                                orderIndex,
                                barcode,
                                cookStyle,
                                orginNumber,
                                sideDishEntity,
                                sideDishPrice,
                                sideDishIndex
                        )
                );
                getDialog().dismiss();
                break;
            case R.id.close:
                getDialog().dismiss();
                break;
            case R.id.subbt:
                if (isEdit) {
                    if (Double.parseDouble(tvNumber.getText().toString().trim()) >= 1) {
                        tvNumber.setText(Double.parseDouble(tvNumber.getText().toString().trim()) - 1.0 + "");
                        commodityNumber = Double.parseDouble(tvNumber.getText().toString().trim());
                        price.setText("￥" + MyUtils.formatDouble(commodityNumber * productBean.getPrice() + commodityNumber * sideDishPrice));
                    }
                } else {
                    if (Double.parseDouble(tvNumber.getText().toString().trim()) >= 2) {
                        tvNumber.setText(Double.parseDouble(tvNumber.getText().toString().trim()) - 1.0 + "");
                        commodityNumber = Double.parseDouble(tvNumber.getText().toString().trim());
                        if (isWeight) {
                            price.setText("￥" + MyUtils.formatDouble(ProductUtil.calCommodityMoney(barcode) * commodityNumber + sideDishPrice * commodityNumber));
                        } else {
                            price.setText("￥" + MyUtils.formatDouble(commodityNumber * productBean.getPrice() + sideDishPrice * commodityNumber));
                        }
                    }
                }
                if (commodityNumber >= 1) {
                    if (isEdit) {
                        btnEnsure.setText("修改");
                    } else {
                        btnEnsure.setText("添加");
                    }
                    btnEnsure.setBackgroundColor(getResources().getColor(R.color.material_green));
                } else {
                    btnEnsure.setText("删除");
                    btnEnsure.setBackgroundColor(getResources().getColor(R.color.red));

                }
                break;
            case R.id.addbt:
                tvNumber.setText(Double.parseDouble(tvNumber.getText().toString().trim()) + 1.0 + "");
                commodityNumber = Double.parseDouble(tvNumber.getText().toString().trim());
                if (isWeight) {
                    price.setText("￥" + MyUtils.formatDouble(ProductUtil.calCommodityMoney(barcode) * commodityNumber + sideDishPrice * commodityNumber));
                } else {
                    price.setText("￥" + MyUtils.formatDouble(commodityNumber * productBean.getPrice() + sideDishPrice * commodityNumber));
                }
                if (commodityNumber >= 1) {
                    if (isEdit) {
                        btnEnsure.setText("修改");
                    } else {
                        btnEnsure.setText("添加");
                    }

                    btnEnsure.setBackgroundColor(getResources().getColor(R.color.material_green));
                }
                break;
        }
    }

    private void setSideDish() {
        if (isEdit) {
            if (order != null && productBean.getSidedish().size() > 0) {
                llSideDishes.setVisibility(View.VISIBLE);
                HashMap<String, Object> format = ObjectUtil.format(order);
                String sideName;
                sideName = ObjectUtil.getString(format, "sideDishCommodity");
                sideDishesDetail.setText(sideName);
                sideDishIndex = ObjectUtil.getInt(format, "sideDishIndex");
                llSideDishes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSideDishBottomSheetList();
                    }
                });
            } else {
                llSideDishes.setVisibility(View.GONE);
            }
        } else {
            sidedish = productBean.getSidedish();
            if (sidedish.size() > 0) {
                llSideDishes.setVisibility(View.VISIBLE);
                sideDishesDetail.setText(sidedish.get(0).getName());
                sideDishPrice = sidedish.get(0).getPrice();
                sideDishIndex = 0;
                llSideDishes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSideDishBottomSheetList();
                    }
                });
            } else {
                llSideDishes.setVisibility(View.GONE);
            }
        }
    }

    private void showSideDishBottomSheetList() {
         sidedish = productBean.getSidedish();
        QMUIBottomSheet.BottomListSheetBuilder bs = new QMUIBottomSheet.BottomListSheetBuilder(getActivity());
        for (int i = 0; i < sidedish.size(); i++) {
            bs.addItem(sidedish.get(i).getName());
        }
        bs.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
            @Override
            public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                dialog.dismiss();
                sideDishesDetail.setText(sidedish.get(position).getName());
                commodityNumber = Double.parseDouble(tvNumber.getText().toString().trim());
                sideDishPrice = sidedish.get(position).getPrice();
                Double totalDishPrice = MyUtils.formatDouble(sideDishPrice * commodityNumber);
                sideDishIndex = position;
                if (isWeight) {
                    price.setText("￥" + MyUtils.formatDouble(ProductUtil.calCommodityMoney(barcode) * commodityNumber + totalDishPrice));
                } else {
                    price.setText("￥" + MyUtils.formatDouble(productBean.getPrice() * commodityNumber + totalDishPrice));
                }
            }
        }).build().show();
    }

    private void setDialog() {
        tipDialog = new QMUITipDialog.Builder(getActivity())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载中")
                .create();
    }

    private void setTitle() {
        String titleContent = "";
        if (productBean.getSerial() != null) {
            titleContent += productBean.getSerial();
        }
        titleContent += productBean.getName();
        if (isWeight) {
            titleContent += "(" + ProductUtil.calCommodityWeight(barcode);
            if (ProductUtil.calCommodityWeight(barcode) > 20) {
                titleContent += "ml)";
            } else {
                titleContent += "kg)";
            }
        }
        title.setText(titleContent);
    }

    private void setComment() {
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


}
