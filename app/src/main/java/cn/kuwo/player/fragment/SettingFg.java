package cn.kuwo.player.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.kuwo.player.BuildConfig;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.api.CommodityApi;
import cn.kuwo.player.base.BaseFragment;
import cn.kuwo.player.custom.ScanUserFragment;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.CameraProvider;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.RealmUtil;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.ToastUtil;
import cn.kuwo.player.util.UpgradeUtil;

public class SettingFg extends BaseFragment {
    private static String ARG_PARAM = "param_key";
    ProgressDialog mProgressDialog;
    @BindView(R.id.upgrade)
    TextView upgrade;
    Unbinder unbinder;
    @BindView(R.id.change_commodity_name)
    TextView changeCommodityName;
    @BindView(R.id.change_commodity_price)
    TextView changeCommodityPrice;
    Unbinder unbinder1;
    @BindView(R.id.cb_carema)
    CheckBox cbCarema;
    @BindView(R.id.rl_carema_choose)
    RelativeLayout rlCaremaChoose;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private Activity mActivity;
    private String mParam;
    private Context mContext;
    private AVObject CommodityAVObject = null;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fg_setting;
    }

    @Override
    public void initData() {
        findChangeCommodity();
        setChooseCarema();
    }

    private void setChooseCarema() {
        if (CameraProvider.hasCamera()) {
            rlCaremaChoose.setVisibility(View.VISIBLE);
            if (SharedHelper.readBoolean("useGun")){
                cbCarema.setChecked(true);
            }else{
                cbCarema.setChecked(false);
            }
        } else {
            rlCaremaChoose.setVisibility(View.GONE);
        }
        cbCarema.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SharedHelper.saveBoolean("useGun",true);
                }else{
                    SharedHelper.saveBoolean("useGun",false);
                }
            }
        });
    }

    private void findChangeCommodity() {
        showDialog();
        AVQuery<AVObject> query = new AVQuery<>("OfflineCommodity");
        query.whereEqualTo("objectId", "5b225b9fee920a003b2ca0a3");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                hideDialog();
                if (e == null) {
                    if (list.size() > 0) {
                        CommodityAVObject = list.get(0);
                        changeCommodityName.setText(CommodityAVObject.getString("name"));
                        changeCommodityPrice.setText(CommodityAVObject.getDouble("price") + "");
                        setListener();
                    }
                }
            }
        });
    }

    private void setListener() {
        changeCommodityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
                builder.setTitle("修改商品名称")
                        .setPlaceholder("在此修改商品名称")
                        .setInputType(InputType.TYPE_CLASS_TEXT)
                        .setCanceledOnTouchOutside(false)
                        .setDefaultText(changeCommodityName.getText().toString().trim())
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction("确定", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                final String text = builder.getEditText().getText().toString();
                                if (text != null && text.length() > 0) {
                                    dialog.dismiss();
                                    if (CommodityAVObject != null) {
                                        showDialog();
                                        CommodityAVObject.put("name", text);
                                        CommodityAVObject.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                hideDialog();
                                                if (e == null) {
                                                    showDialog();
                                                    CommodityApi.getOfflineCommodity().findInBackground(new FindCallback<AVObject>() {
                                                        @Override
                                                        public void done(final List<AVObject> list, AVException e) {
                                                            hideDialog();
                                                            if (e == null) {
                                                                RealmUtil.setProductBeanRealm(list);
                                                            }
                                                        }
                                                    });
                                                    ToastUtil.showLong(MyApplication.getContextObject(), "修改成功");
                                                    changeCommodityName.setText(text);
                                                } else {
                                                    ToastUtil.showLong(MyApplication.getContextObject(), e.getMessage());
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "请输入商品名称", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .create(mCurrentDialogStyle).show();
            }
            });
            changeCommodityPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
                builder.setTitle("修改商品价格")
                        .setPlaceholder("在此修改商品价格")
                        .setInputType(InputType.TYPE_CLASS_TEXT)
                        .setCanceledOnTouchOutside(false)
                        .setDefaultText(changeCommodityPrice.getText().toString().trim())
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction("确定", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                final String text = builder.getEditText().getText().toString();
                                if (text != null && text.length() > 0 && MyUtils.isDoubleOrFloat(text) && Double.parseDouble(text) > 0) {
                                    dialog.dismiss();
                                    if (CommodityAVObject != null) {
                                        showDialog();
                                        CommodityAVObject.put("price", Double.parseDouble(text));
                                        CommodityAVObject.put("actualprice", Double.parseDouble(text));
                                        CommodityAVObject.put("nb",Double.parseDouble(text));
                                        CommodityAVObject.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                hideDialog();
                                                if (e == null) {
                                                    showDialog();
                                                    CommodityApi.getOfflineCommodity().findInBackground(new FindCallback<AVObject>() {
                                                        @Override
                                                        public void done(final List<AVObject> list, AVException e) {
                                                            hideDialog();
                                                            if (e == null) {
                                                                RealmUtil.setProductBeanRealm(list);
                                                            }
                                                        }
                                                    });
                                                    ToastUtil.showLong(MyApplication.getContextObject(), "修改成功");
                                                    changeCommodityPrice.setText(text);
                                                } else {
                                                    ToastUtil.showLong(MyApplication.getContextObject(), e.getMessage());
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "请输入商品价格", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .create(mCurrentDialogStyle).show();
            }
        });
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = (Activity) context;
        mParam = getArguments().getString(ARG_PARAM);  //获取参数
    }

    public static SettingFg newInstance(String str) {
        SettingFg settingFg = new SettingFg();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM, str);
        settingFg.setArguments(bundle);
        return settingFg;
    }

    @OnClick(R.id.upgrade)
    public void onViewClicked() {
        UpgradeUtil.checkInfo(mContext);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder1.unbind();
    }
}
