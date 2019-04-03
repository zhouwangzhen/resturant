package cn.kuwo.player.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.base.BaseFragment;
import cn.kuwo.player.custom.CommomDialog;
import cn.kuwo.player.custom.ScanUserFragment;
import cn.kuwo.player.print.Bill;
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

/**
 * Created by lovely on 2019/3/29
 */
public class CouponFg extends BaseFragment {

    private static String ARG_PARAM = "userId";
    @BindView(R.id.btn_scan_user)
    Button btnScanUser;
    @BindView(R.id.ll_no_user)
    LinearLayout llNoUser;
    @BindView(R.id.reset_data)
    Button resetData;
    Unbinder unbinder;
    private Activity mActivity;
    private String mParam;
    private int REQUEST_CODE_SCAN = 111;

    @Override
    protected int getLayoutId() {
        return R.layout.fg_coupon;
    }

    @Override
    public void initData() {
    }


    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mParam = getArguments().getString(ARG_PARAM);  //获取参数
    }

    public static CouponFg newInstance(String str) {
        CouponFg couponFg = new CouponFg();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM, str);
        couponFg.setArguments(bundle);
        return couponFg;
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showDialog();
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                showDialog();
                String code = data.getStringExtra(Constant.CODED_CONTENT);
                new AVQuery<>("MallOrder")
                        .getInBackground(code, new GetCallback<AVObject>() {
                            @Override
                            public void done(final AVObject avObject, AVException e) {
                                if (e == null) {
                                    List commoditys = avObject.getList("commodity");
                                    if (commoditys.size() == 1 && (commoditys.get(0).equals("5c9c9ffac05a80007465b968") || commoditys.get(0).equals("5c9ca03942cda600687e2ccc"))) {
                                        if (CONST.OrderState.ORDER_STATUS_FINSIH.equals(avObject.getAVObject("orderStatus").getObjectId())) {
                                            new AVQuery<>("Commodity")
                                                    .getInBackground(commoditys.get(0).toString(), new GetCallback<AVObject>() {
                                                        @Override
                                                        public void done(final AVObject commodityAvObject, AVException e) {
                                                            new CommomDialog(getContext(), R.style.dialog, commodityAvObject.getString("name") + "*1", new CommomDialog.OnCloseListener() {
                                                                @Override
                                                                public void onClick(Dialog dialog, boolean confirm) {
                                                                    if (confirm) {
                                                                        dialog.dismiss();
                                                                        avObject.put("orderStatus", AVObject.createWithoutData("MallOrderStatus", CONST.OrderState.ORDER_STATUS_RECEIVE));
                                                                        avObject.saveInBackground(new SaveCallback() {
                                                                            @Override
                                                                            public void done(AVException e) {
                                                                                if (e == null) {
                                                                                    new CommomDialog(getContext(), R.style.dialog, commodityAvObject.getString("name") + "*1", new CommomDialog.OnCloseListener() {
                                                                                        @Override
                                                                                        public void onClick(Dialog dialog, boolean confirm) {
                                                                                            if (confirm) {
                                                                                                dialog.dismiss();
                                                                                            }
                                                                                        }
                                                                                    }).setTitle("兑换成功").setPositiveButton("确定").show();
                                                                                } else {
                                                                                    T.L(e.getMessage());
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }).setTitle("兑换提示").setNegativeButton("取消").setPositiveButton("核销卡券").show();
                                                        }
                                                    });
                                        } else if (CONST.OrderState.ORDER_STATUS_RECEIVE.equals(avObject.getAVObject("orderStatus").getObjectId())) {
                                            T.L("此订单已经提货");

                                        } else {
                                            T.L("此订单暂时不可提货");
                                        }
                                    } else {
                                        T.L("此订单不是提货订单");
                                    }
                                } else {
                                    T.L("获取订单信息错误");
                                }
                                hideDialog();
                            }
                        });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick({R.id.btn_scan_user, R.id.reset_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_scan_user:
                if (MyUtils.getCameraPermission(MyApplication.getContextObject())) {
                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
                    intent.putExtra(Constant.INTENT_ZXING_CONFIG, MyUtils.caremaSetting());
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                }else{
                    T.L("请在平板上进行操作");
                }
                break;
            case R.id.reset_data:
                break;
        }
    }
}
