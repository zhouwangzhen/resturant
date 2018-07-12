package cn.kuwo.player.custom;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.api.TableApi;
import cn.kuwo.player.event.SuccessEvent;
import cn.kuwo.player.print.Bill;
import cn.kuwo.player.util.ObjectUtil;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.ToastUtil;

public class RefundFragment extends DialogFragment {
    TextView refundName;
    TextView refundNumber;
    Button refundCancel;
    Button refundEnsure;
    RadioGroup rgNumber;
    RadioGroup rgContent;
    private QMUITipDialog tipDialog;
    private View view;
    private HashMap<String, Object> commodity;
    private AVObject tableAVObject;
    private int position;
    private RadioButton rbLongTime;


    @SuppressLint("ValidFragment")
    public RefundFragment(AVObject tableAVObject, List<Object> orders, int i) {
        this.tableAVObject = tableAVObject;
        this.commodity = ObjectUtil.format(orders.get(i));
        this.position = i;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.fragment_refund, container);
        findView();
        initData();
        return view;
    }

    private void initData() {
        tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载中")
                .create();
        refundName.setText(ObjectUtil.getString(commodity, "name"));
        refundNumber.setText(ObjectUtil.getDouble(commodity, "number") + "份");
        rbLongTime.setChecked(true);
        refundCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        refundEnsure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                Bill.printRefundOrder(tableAVObject,commodity,refundNumber.getText().toString().substring(0, refundNumber.getText().toString().length() - 1), ((TextView) view.findViewById(rgContent.getCheckedRadioButtonId())).getText().toString());
                TableApi.refundOrder(
                        tableAVObject,
                        commodity,
                        refundNumber.getText().toString().substring(0, refundNumber.getText().toString().length() - 1),
                        ((TextView) view.findViewById(rgContent.getCheckedRadioButtonId())).getText().toString(),
                        position)
                        .saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                hideDialog();
                                if (e == null) {
                                   ToastUtil.showShort(MyApplication.getContextObject(), "退菜成功");
                                    getDialog().dismiss();
                                    EventBus.getDefault().post(new SuccessEvent(0));
                                } else {
                                    ToastUtil.showShort(MyApplication.getContextObject(), "网络错误" + e.getMessage());
                                }
                            }
                        });
            }
        });
    }

    private void findView() {
        refundName = view.findViewById(R.id.refund_name);
        refundNumber = view.findViewById(R.id.refund_number);
        rgNumber = view.findViewById(R.id.rg_number);
        refundCancel = view.findViewById(R.id.refund_cancel);
        refundEnsure = view.findViewById(R.id.refund_ensure);
        rgContent = view.findViewById(R.id.rg_content);
        rbLongTime = view.findViewById(R.id.rb_long_time);
        refundNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = new String[ObjectUtil.getDouble(commodity, "number").intValue()];
                for (int i = 0; i < items.length; i++) {
                    items[i] = i + 1 + "";
                }
                new QMUIDialog.MenuDialogBuilder(getActivity())
                        .addItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                refundNumber.setText(items[which] + "份");
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
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
        }
    }
}
