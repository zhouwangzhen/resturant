package cn.kuwo.player.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FunctionCallback;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.base.BaseActivity;
import cn.kuwo.player.util.ApiManager;
import cn.kuwo.player.util.LoadingUtil;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.T;
import cn.kuwo.player.util.ToastUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lovely on 2019/1/28
 */
public class MouActivity extends BaseActivity {
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.active_mou)
    Button activeMou;
    @BindView(R.id.tv_back)
    LinearLayout tvBack;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private Context mContext;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_mou;
    }

    @Override
    public void initData() {
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mContext = this;
    }

    @OnClick(R.id.active_mou)
    public void onViewClicked() {
        final String username = etUsername.getText().toString().trim();
        new QMUIDialog.MessageDialogBuilder(mContext)
                .setTitle("提示")
                .setMessage("确认给用户" + username + "激活5000块一头牛只？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                }).addAction("确定", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
                activeMou.setClickable(false);
                LoadingUtil.show(mContext, "加载中");
                AVQuery<AVObject> query = new AVQuery<>("_User");
                query.whereEqualTo("username", etUsername.getText().toString().trim());
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(final List<AVObject> list, AVException e) {
                        if (e == null) {
                            if (list.size() > 0) {
                                Map<String, Object> parameters = new HashMap<String, Object>();
                                parameters.put("customerId", list.get(0).getObjectId());
                                parameters.put("numberSum", 1);
                                AVCloud.callFunctionInBackground("offlineOrder", parameters, new FunctionCallback<Map<String, String>>() {

                                    @Override
                                    public void done(Map<String, String> stringMapMap, AVException e) {
                                        if (e == null) {
                                            Call<ResponseBody> responseBodyCall = ApiManager.getInstance().getRetrofitService().nbCompense(list.get(0).getObjectId(),
                                                    new SharedHelper().read("cashierId"),
                                                    new SharedHelper().read("cashierId"),
                                                    600.0,
                                                    0.0,
                                                    1,
                                                    0,
                                                    1,
                                                    "定期牛订单" + stringMapMap.get("order"));
                                            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    LoadingUtil.hide();
                                                    if (response.code() == 200 || response.code() == 201) {
                                                        Toast.makeText(MyApplication.getContextObject(), "充值成功", Toast.LENGTH_SHORT).show();
                                                        etUsername.setText("");
                                                        new QMUIDialog.MessageDialogBuilder(mContext)
                                                                .setTitle("提示")
                                                                .setMessage("激活成功")
                                                                .addAction("确定", new QMUIDialogAction.ActionListener() {
                                                                    @Override
                                                                    public void onClick(QMUIDialog dialog, int index) {
                                                                        dialog.dismiss();
                                                                        activeMou.setClickable(true);
                                                                    }
                                                                }).show();
                                                    } else {
                                                        T.show(response);
                                                        activeMou.setClickable(true);
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    LoadingUtil.hide();
                                                    activeMou.setClickable(true);
                                                    T.L(t.getMessage());
                                                }
                                            });
                                        } else {
                                            LoadingUtil.hide();
                                            activeMou.setClickable(true);
                                            T.L(e.getMessage());
                                        }
                                    }
                                });
                            } else {
                                LoadingUtil.hide();
                                activeMou.setClickable(true);
                                ToastUtil.show(getApplicationContext(), "未找到指定的用户信息", Toast.LENGTH_LONG);
                            }
                        } else {
                            hideDialog();
                            activeMou.setClickable(true);
                            ToastUtil.show(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                        }
                    }
                });
            }
        }).show();
    }

}
