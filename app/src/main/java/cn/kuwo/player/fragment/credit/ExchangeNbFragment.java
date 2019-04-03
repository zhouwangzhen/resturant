package cn.kuwo.player.fragment.credit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.kuwo.player.R;
import cn.kuwo.player.util.ApiManager;
import cn.kuwo.player.util.LoadingUtil;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.T;
import me.yokeyword.fragmentation.SupportFragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lovely on 2018/8/21
 */
public class ExchangeNbFragment extends SupportFragment {

    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.scan_meatcode)
    EditText scanMeatcode;
    @BindView(R.id.ll_no_user)
    LinearLayout llNoUser;
    @BindView(R.id.operator_user)
    TextView operatorUser;
    @BindView(R.id.edit_card_code)
    EditText editCardCode;
    @BindView(R.id.nb_number)
    TextView nbNumber;
    @BindView(R.id.btn_convert)
    Button btnConvert;
    @BindView(R.id.convert_content)
    TextView convertContent;
    @BindView(R.id.btn_reset)
    Button btnReset;
    @BindView(R.id.has_user)
    LinearLayout hasUser;
    Unbinder unbinder;
    private String realName = "";
    private String userId = "";
    private int userCredit = 0;
    private int exchangeNb = 0;
    private String barcode = "";
    private Runnable delayRun = new Runnable() {
        @Override
        public void run() {
            Logger.d(barcode);
            if (userId.length() > 0) {
                scanMeatcode.setText("");
            } else {
                scanMeatcode.setText("");
                SearchUser(barcode);
            }

        }
    };
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    private void initView(View view) {
        title.setText("积分兑换牛币");
        setListener();
    }

    private void setListener() {
        scanMeatcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    if (delayRun != null) {
                        mHandler.removeCallbacks(delayRun);
                    }
                    barcode = s.toString().trim();
                    mHandler.postDelayed(delayRun, 500);
                }
            }
        });
    }

    public static ExchangeNbFragment newInstance() {
        ExchangeNbFragment creditExchangeFragment = new ExchangeNbFragment();
        return creditExchangeFragment;
    }

    public int getLayoutId() {
        return R.layout.fg_credit_exchange;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick({R.id.tv_back, R.id.btn_convert, R.id.btn_reset})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                pop();
                break;
            case R.id.btn_convert:
                toConvert();
                break;
            case R.id.btn_reset:
                llNoUser.setVisibility(View.VISIBLE);
                userId = "";
                scanMeatcode.setFocusable(true);
                scanMeatcode.setFocusableInTouchMode(true);
                scanMeatcode.requestFocus();
                scanMeatcode.findFocus();
                break;
        }
    }

    private void toConvert() {
        exchangeNb = Integer.parseInt(editCardCode.getText().toString().trim());
        if (exchangeNb > 0) {
            if (userCredit >= exchangeNb * 100) {
                final NormalDialog dialog = new NormalDialog(getContext());
                dialog.content("确定使用" + exchangeNb * 100 + "积分兑换" + exchangeNb + "牛币?")
                        .style(NormalDialog.STYLE_TWO)
                        .titleTextSize(28)
                        .widthScale(0.6f)
                        .show();
                dialog.setCanceledOnTouchOutside(false);
                dialog.setOnBtnClickL(
                        new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                dialog.dismiss();
                            }
                        },
                        new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                dialog.dismiss();
                                LoadingUtil.show(getContext(), "兑换中");
                                AVObject avObject = new AVObject("CreditsLog");
                                avObject.put("change", 0 - exchangeNb * 100);
                                avObject.put("store", 2);
                                avObject.put("active", 1);
                                avObject.put("user", AVObject.createWithoutData("_User", userId));
                                avObject.put("type", AVObject.createWithoutData("CreditsType", "5b715dc917d0090035d4f102"));
                                avObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            Call<ResponseBody> responseBodyCall = ApiManager.getInstance().getRetrofitService().offlineRecharge(
                                                    userId,
                                                    SharedHelper.read("cashierId"),
                                                    SharedHelper.read("cashierId"),
                                                    exchangeNb + 0.0,
                                                    0.0,
                                                    1,
                                                    2,
                                                    2);
                                            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    LoadingUtil.hide();
                                                    if (response.code() == 200 || response.code() == 201) {
                                                        T.L("兑换成功");
                                                        convertContent.setText("上次操作:兑换成功,使用" + exchangeNb * 100 + "积分兑换" + exchangeNb + "牛币");
                                                        refreshUser();
                                                    } else {
                                                        T.L("兑换失败");
                                                        convertContent.setText("上次操作:兑换失败");
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    LoadingUtil.hide();
                                                }
                                            });
                                        } else {
                                            LoadingUtil.hide();
                                            convertContent.setText("上次操作:扣款失败");
                                            T.L(e.getMessage());
                                        }
                                    }
                                });

                            }
                        });
            } else {
                T.L("超过可兑换数量");
            }
        } else {
            T.L("请兑换大于0个牛币");
        }
    }

    private void refreshUser() {
        LoadingUtil.show(getContext(), "重新用户信息");
        AVQuery<AVObject> query = new AVQuery<>("_User");
        query.getInBackground(userId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    LoadingUtil.hide();
                    realName = avObject.getString("username");
                    userId = avObject.getObjectId();
                    userCredit = MyUtils.formatDouble(avObject.getDouble("credits")).intValue();
                    exchangeNb = userCredit / 100;
                    editCardCode.setText(userCredit / 100 + "");
                    llNoUser.setVisibility(View.GONE);
                    operatorUser.setText("用户手机号:" + realName + "\n" + "用户积分:" + userCredit);
                } else {
                    LoadingUtil.hide();
                    T.L("获取用户信息失败");
                }
            }
        });
    }

    private void SearchUser(String barcode) {
        LoadingUtil.show(getContext(), "正在查询用户信息");
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("payCode", barcode);
        AVCloud.callFunctionInBackground("payCodeGetUser", parameters, new FunctionCallback<Map<String, Object>>() {

            @Override
            public void done(Map<String, Object> map, AVException e) {
                if (e == null) {
                    LoadingUtil.hide();
                    realName = map.get("username").toString();
                    userId = map.get("objectId").toString();
                    userCredit = MyUtils.formatDouble(Double.parseDouble(map.get("credits").toString())).intValue();
                    exchangeNb = userCredit / 100;
                    editCardCode.setText(userCredit / 100 + "");
                    llNoUser.setVisibility(View.GONE);
                    operatorUser.setText("用户手机号:" + realName + "\n" + "用户积分:" + userCredit);
                } else {
                    LoadingUtil.hide();
                    T.L("获取用户信息失败");
                }
            }
        });
    }


}

