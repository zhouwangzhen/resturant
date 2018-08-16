package cn.kuwo.player.fragment.activities;

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
import android.widget.TextView;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FunctionCallback;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.kuwo.player.R;
import cn.kuwo.player.util.ApiManager;
import cn.kuwo.player.util.LoadingUtil;
import cn.kuwo.player.util.T;
import me.yokeyword.fragmentation.SupportFragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by lovely on 2018/7/26
 */
public class CardConvertFragment extends SupportFragment {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.scan_meatcode)
    EditText scanMeatcode;
    @BindView(R.id.ll_no_user)
    LinearLayout llNoUser;
    @BindView(R.id.operator_user)
    TextView operatorUser;
    @BindView(R.id.btn_convert)
    Button btnConvert;
    @BindView(R.id.edit_card_code)
    TextView editCardCode;
    @BindView(R.id.last_card)
    TextView lastCard;
    private String realName = "";
    private String operatorId = "";
    private String barcode = "";
    private Runnable delayRun = new Runnable() {
        @Override
        public void run() {
            if (operatorId.length() > 0) {
                editCardCode.setText(scanMeatcode.getText().toString().trim());
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
        title.setText("牛币卡激活");
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
                    mHandler.postDelayed(delayRun, 300);
                }
            }
        });
    }

    private int getLayoutId() {
        return R.layout.fg_card_convert;
    }

    public static CardConvertFragment newInstance() {
        return new CardConvertFragment();
    }

    @OnClick({R.id.tv_back, R.id.btn_convert})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                pop();
                break;
            case R.id.btn_convert:
                toConvert();
                break;
        }
    }

    private void toConvert() {
        LoadingUtil.show(getContext(),"激活中");
        Call<ResponseBody> responseBodyCall = ApiManager.getInstance().getRetrofitService().mouCardConvert(editCardCode.getText().toString().trim(), operatorId, 2);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                LoadingUtil.hide();
                if (response.code() == 200 || response.code() == 201) {
                    lastCard.setText("上次激活卡号:"+editCardCode.getText().toString().trim());
                    editCardCode.setText("");
                    T.L("激活成功");
                } else {
                    try {
                        String errorContent = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorContent);
                        T.L(jsonObject.getString("message"));
                    } catch (Exception e) {
                        T.L("网络错误");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LoadingUtil.hide();
                T.L(t.getMessage());
            }
        });
    }

    private void SearchUser(String barcode) {
        LoadingUtil.show(getContext(),"加载中");
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("payCode", barcode);
        AVCloud.callFunctionInBackground("payCodeGetUser", parameters, new FunctionCallback<Map<String, Object>>() {

            @Override
            public void done(Map<String, Object> map, AVException e) {
                if (e == null) {
                    LoadingUtil.hide();
                    if (Integer.parseInt(map.get("clerk").toString()) > 0) {
                        realName = map.get("realName").toString();
                        operatorId = map.get("objectId").toString();
                        llNoUser.setVisibility(View.GONE);
                        operatorUser.setText("操作人员:" + realName);
                    }else{
                        T.L("权限不足");
                    }
                }else{
                    LoadingUtil.hide();
                    T.L("获取用户信息失败");
                }
            }
        });
    }


}
