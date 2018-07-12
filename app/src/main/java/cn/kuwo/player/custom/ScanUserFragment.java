package cn.kuwo.player.custom;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FunctionCallback;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.bean.UserBean;
import cn.kuwo.player.util.ApiManager;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.CameraProvider;
import cn.kuwo.player.util.DataUtil;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.T;
import cn.kuwo.player.util.ToastUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class ScanUserFragment extends DialogFragment {
    private int REQUEST_CODE_SCAN = 111;
    Button btnCancel;
    TextView title;
    private EditText editScanCode;
    private String barcode = "";
    private int type;
    private Handler mHandler = new Handler();
    QMUITipDialog tipDialog;

    @SuppressLint("ValidFragment")
    public ScanUserFragment(int type) {
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_scan_user, container);
        initPosition();
        findView(view);
        initData();
        return view;
    }


    private void initPosition() {
        getDialog().getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        lp.dimAmount = 0.6f;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(lp);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        if (type == 0) {
            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void findView(View view) {
        editScanCode = view.findViewById(R.id.edit_code);
        title = view.findViewById(R.id.title);
        btnCancel = view.findViewById(R.id.btn_cancel);
        editScanCode.setInputType(InputType.TYPE_NULL);
        tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("查询中")
                .create();
        if (!CameraProvider.hasCamera() && type == 1) {
            title.setText("扫描全民养牛会员码或刷会员卡登录");
        }else if(type==2){
            title.setText("操作员工扫描员工自己的条码");
        }

    }

    private void initData() {
        if (CameraProvider.hasCamera()&&!SharedHelper.readBoolean("useGun")) {
            btnCancel.setVisibility(View.VISIBLE);
        } else {
            btnCancel.setVisibility(View.GONE);
            editScanCode.requestFocus();
            editScanCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equals("")) {
                        if (delayRun != null) {
                            mHandler.removeCallbacks(delayRun);
                        }
                        barcode = s.toString().trim();
                        mHandler.postDelayed(delayRun, 300);
                    }
                }
            });
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseScanType();

            }
        });
    }

    private void chooseScanType() {
        if (CameraProvider.hasCamera()) {
            if (MyUtils.getCameraPermission(getContext())) {
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                intent.putExtra(Constant.INTENT_ZXING_CONFIG, MyUtils.caremaSetting());
                startActivityForResult(intent, REQUEST_CODE_SCAN);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                showDialog();
                String code = data.getStringExtra(Constant.CODED_CONTENT);
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("payCode", code.trim());
                AVCloud.callFunctionInBackground("payCodeGetUser", parameters, new FunctionCallback<Map<String, Object>>() {
                    @Override
                    public void done(Map<String, Object> object, AVException e) {
                        if (e == null) {
                            Logger.d("收银员登录");
                            hideDialog();
                            if (type == 0) {
                                Logger.d(object);
                                if ((Boolean) object.get("test") || Integer.parseInt(object.get("clerk").toString()) > 0) {
                                    SharedHelper sharedHelper = new SharedHelper(MyApplication.getContextObject());
                                    sharedHelper.saveBoolean("Test", (Boolean) object.get("test"));
                                    sharedHelper.saveBoolean("cashierLogin", true);
                                    sharedHelper.save("cashierId", object.get("objectId").toString());
                                    sharedHelper.save("mobilePhoneNumber", object.get("username").toString());
                                    sharedHelper.save("cashierName", (object.get("realName")== null ? object.get("nickName").toString() : object.get("realName").toString()));
                                    TextView waiterName = (TextView) getActivity().findViewById(R.id.waiter_name);
                                    waiterName.setText("收银员：" + (object.get("realName") == null || object.get("realName").toString().equals("") ? object.get("nickName").toString() : object.get("realName").toString()));
                                    getDialog().dismiss();
                                }
                            }
                        } else {
                            hideDialog();
                            ToastUtil.showLong(getContext(), e.getMessage()+"网络连接失败请重试");
                        }
                    }
                });

            }
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

    private Runnable delayRun = new Runnable() {
        @Override
        public void run() {
            showDialog();
            editScanCode.setText("");
            barcode = barcode.replace(";", "").replace(";?", "").replace(":", "").replace("?", "");
            if (barcode.length() == 4) {
                barcode = barcode.trim().replace(";", "").replace("?", "");
                AVQuery<AVObject> power = new AVQuery<>("Power");
                power.whereEqualTo("card", barcode);
                power.include("user");
                power.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null) {
                            if (list.size() > 0) {
                                final AVObject map = list.get(0).getAVObject("user");
                                if (type == 0 && ((Boolean) map.get("test") || Integer.parseInt(map.get("clerk").toString()) > 0)) {//收银员登录
                                    hideDialog();
                                    SharedHelper sharedHelper = new SharedHelper(MyApplication.getContextObject());
                                    sharedHelper.saveBoolean("Test", map.getBoolean("test"));
                                    sharedHelper.saveBoolean("cashierLogin", true);
                                    sharedHelper.save("cashierId", map.getObjectId());
                                    sharedHelper.save("mobilePhoneNumber", map.getString("username"));
                                    sharedHelper.save("cashierName", (map.getString("realName") == null || map.get("realName").toString().equals("") ? map.getString("nickName") : map.getString("realName")));
                                    TextView waiterName = (TextView) getActivity().findViewById(R.id.waiter_name);
                                    waiterName.setText("收银员：" + (map.getString("realName") == null || map.get("realName").toString().equals("") ? map.get("nickName").toString() : map.get("realName").toString()));
                                    getDialog().dismiss();
                                } else if (type == 1) {//获取客户信息和剩余可提牛肉数量
                                    Map<String, Object> parameters = new HashMap<String, Object>();
                                    parameters.put("userID", map.getObjectId());
                                    AVCloud.callFunctionInBackground("svip", parameters, new FunctionCallback<Map<String, Object>>() {
                                        @Override
                                        public void done(final Map<String, Object> objectMap, AVException e) {
                                            if (e == null) {
                                                Call<ResponseBody> responseBodyCall = ApiManager.getInstance().getRetrofitService().QueryofflineRecharge(map.getObjectId());
                                                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        if (response.code()==200||response.code()==201){
                                                            Double nb=0.0;
                                                            hideDialog();

                                                            try {
                                                                String responseText = DataUtil.JSONTokener(response.body().string());
                                                                JSONObject jsonObject = new JSONObject(responseText);
                                                                nb = jsonObject.getDouble("amount");
                                                            } catch (Exception e1) {
                                                                e1.printStackTrace();
                                                            }
                                                            String avatar=null;
                                                            try {
                                                                avatar = map.get("avatarurl") != null && !map.get("avatarurl").equals("") ? map.get("avatarurl").toString() : null;
                                                            } catch (Exception e1) {
                                                                e1.printStackTrace();
                                                            }
                                                            UserBean userBean = new UserBean(
                                                                    CONST.UserCode.SCANCUSTOMER,
                                                                    map.getObjectId(),
                                                                    map.get("username").toString(),
                                                                    map.get("realName") == null || map.get("realName").toString().equals("") ? map.get("realName").toString() : map.get("nickName").toString(),
                                                                    Integer.parseInt(map.get("vip").toString()),
                                                                    MyUtils.formatDouble(Double.parseDouble(map.get("credits").toString())),
                                                                    MyUtils.formatDouble(Double.parseDouble(map.get("stored").toString())),
                                                                    MyUtils.formatDouble(Double.parseDouble(map.get("gold").toString()) - Double.parseDouble(map.get("arrears").toString())),
                                                                    (Boolean) map.get("test"),
                                                                    Integer.parseInt(map.get("clerk").toString()),
                                                                    MyUtils.formatDouble(Double.parseDouble(objectMap.get("meatWeight").toString())),
                                                                    objectMap.get("meatId").toString().length() > 0 ? objectMap.get("meatId").toString() : "",
                                                                    (Boolean) objectMap.get("svip"),
                                                                    avatar,
                                                                    (Boolean) objectMap.get("alreadySVIP"),
                                                                    nb
                                                            );
                                                            EventBus.getDefault().post(userBean);
                                                            getDialog().dismiss();
                                                        }else{
                                                            hideDialog();
                                                            T.show(response);
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                        hideDialog();
                                                        ToastUtil.showShort(getContext(), t.getMessage());
                                                    }
                                                });

                                            } else {
                                                hideDialog();
                                                ToastUtil.showShort(MyApplication.getContextObject(), "获取用户信息错误");
                                            }
                                        }
                                    });

                                } else if (type == 2) {//获取用户信息id,username
                                    hideDialog();
                                    UserBean userBean = new UserBean(CONST.UserCode.SCANUSER, map.getObjectId(), map.get("username").toString(), Double.parseDouble(map.get("gold").toString()) - Double.parseDouble(map.get("arrears").toString()), (Boolean) map.get("test"), Integer.parseInt(map.get("clerk").toString()), map.get("realName") == null || map.get("realName").toString().equals("") ? map.get("nickName").toString() : map.get("realName").toString());
                                    EventBus.getDefault().post(userBean);
                                    getDialog().dismiss();
                                } else {
                                    hideDialog();
                                    ToastUtil.showShort(MyApplication.getContextObject(), "非收银员账号");
                                }
                            } else {
                                hideDialog();
                                ToastUtil.showShort(MyApplication.getContextObject(), "此卡未绑定用户");
                            }
                        } else {
                            hideDialog();
                            ToastUtil.showShort(MyApplication.getContextObject(), "网络错误,请刷新后再试");
                        }
                    }
                });
            } else {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("payCode", barcode);
                AVCloud.callFunctionInBackground("payCodeGetUser", parameters, new FunctionCallback<Map<String, Object>>() {
                    @Override
                    public void done(final Map<String, Object> map, AVException e) {
                        if (e == null) {
                            if (type == 0 && ((Boolean) map.get("test") || Integer.parseInt(map.get("clerk").toString()) > 0)) {//收银员登录
                                hideDialog();
                                SharedHelper sharedHelper = new SharedHelper(MyApplication.getContextObject());
                                sharedHelper.saveBoolean("Test", (Boolean) map.get("test"));
                                sharedHelper.saveBoolean("cashierLogin", true);
                                sharedHelper.save("cashierId", map.get("objectId").toString());
                                sharedHelper.save("mobilePhoneNumber", map.get("username").toString());
                                sharedHelper.save("cashierName", (map.get("realName") == null ? map.get("nickName").toString() : map.get("realName").toString()));
                                TextView waiterName = (TextView) getActivity().findViewById(R.id.waiter_name);
                                waiterName.setText("收银员：" + (map.get("realName") == null || map.get("realName").toString().equals("") ? map.get("nickName").toString() : map.get("realName").toString()));
                                getDialog().dismiss();
                            } else if (type == 1) {//获取客户信息和剩余可提牛肉数量
                                Map<String, Object> parameters = new HashMap<String, Object>();
                                parameters.put("userID", map.get("objectId").toString());
                                AVCloud.callFunctionInBackground("svip", parameters, new FunctionCallback<Map<String, Object>>() {
                                    @Override
                                    public void done(final Map<String, Object> objectMap, AVException e) {
                                        if (e == null) {
                                            Call<ResponseBody> responseBodyCall = ApiManager.getInstance().getRetrofitService().QueryofflineRecharge( map.get("objectId").toString());
                                            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    hideDialog();
                                                    if (response.code()==200||response.code()==201) {
                                                        Double nb=0.0;
                                                        try {
                                                            String responseText = DataUtil.JSONTokener(response.body().string());
                                                            JSONObject jsonObject = new JSONObject(responseText);
                                                            nb = jsonObject.getDouble("amount");
                                                        } catch (Exception e1) {
                                                            e1.printStackTrace();
                                                        }
                                                        String avatar=null;
                                                        try {
                                                            avatar = map.get("avatarurl") != null && !map.get("avatarurl").equals("") ? map.get("avatarurl").toString() : null;
                                                        } catch (Exception e1) {
                                                            e1.printStackTrace();
                                                        }
                                                        UserBean userBean = new UserBean(
                                                                CONST.UserCode.SCANCUSTOMER,
                                                                map.get("objectId").toString(),
                                                                map.get("username").toString(),
                                                                map.get("realName") == null || map.get("realName").toString().equals("") ? map.get("nickName").toString() : map.get("realName").toString(),
                                                                Integer.parseInt(map.get("vip").toString()),
                                                                MyUtils.formatDouble(Double.parseDouble(map.get("credits").toString())),
                                                                MyUtils.formatDouble(Double.parseDouble(map.get("stored").toString())),
                                                                MyUtils.formatDouble(Double.parseDouble(map.get("gold").toString()) - Double.parseDouble(map.get("arrears").toString())),
                                                                (Boolean) map.get("test"),
                                                                Integer.parseInt(map.get("clerk").toString()),
                                                                MyUtils.formatDouble(Double.parseDouble(objectMap.get("meatWeight").toString())),
                                                                objectMap.get("meatId").toString().length() > 0 ? objectMap.get("meatId").toString() : "",
                                                                (Boolean) objectMap.get("svip"),
                                                                avatar,
                                                                (Boolean) objectMap.get("alreadySVIP"),
                                                                nb
                                                        );
                                                        EventBus.getDefault().post(userBean);
                                                        getDialog().dismiss();
                                                    }else{
                                                        hideDialog();
                                                        T.show(response);
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                                }
                                            });

                                        } else {
                                            hideDialog();
                                            ToastUtil.showShort(MyApplication.getContextObject(), "获取用户信息错误");
                                        }
                                    }
                                });

                            } else if (type == 2) {//获取用户信息id,username
                                hideDialog();
                                UserBean userBean = new UserBean(CONST.UserCode.SCANUSER,
                                        map.get("objectId").toString(),
                                        map.get("username").toString(),
                                        Double.parseDouble(map.get("gold").toString()) - Double.parseDouble(map.get("arrears").toString()),
                                        (Boolean) map.get("test"),
                                        Integer.parseInt(map.get("clerk").toString()),
                                        map.get("realName")== null || map.get("realName").toString().equals("") ? map.get("nickName").toString() : map.get("realName").toString()
                                );
                                EventBus.getDefault().post(userBean);
                                getDialog().dismiss();
                            } else {
                                hideDialog();
                                ToastUtil.showShort(MyApplication.getContextObject(), "非收银员账号");
                            }
                        } else {
                            hideDialog();
                            Logger.d(barcode);
                            ToastUtil.showShort(MyApplication.getContextObject(), "二维码已经失效,请刷新后再试");
                        }
                    }
                });

            }
            editScanCode.requestFocus();
        }
    };
}
