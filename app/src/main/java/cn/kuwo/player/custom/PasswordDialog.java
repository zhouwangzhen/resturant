package cn.kuwo.player.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.kuwo.player.R;

/**
 * Created by zhouwangzhen on 2019-07-05
 */
public class PasswordDialog extends Dialog {

    private Context mContext;
    private TextView titleTV;
    private EditText passwordET, oldPasswordET;
    private TextView submitTV, cancelTV;
    private OnListener mListener;
    private View line, line1, line2;

    private String title;
    private String passwordHint;
    private boolean single;
    private boolean show;
    private boolean change;

    public void setListener(OnListener listener) {
        mListener = listener;
    }

    public PasswordDialog(Context context) {
        super(context);
        mContext = context;
    }

    public PasswordDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_password);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        setOnDismissListener(dialog -> {
            passwordET.setText("");
            oldPasswordET.setText("");
        });
        initView();
    }

    private void initView(){
        titleTV = findViewById(R.id.tv_title);
        oldPasswordET = findViewById(R.id.et_old_password);
        passwordET = findViewById(R.id.et_password);
        submitTV = findViewById(R.id.tv_submit);
        cancelTV = findViewById(R.id.tv_cancel);
        line2 = findViewById(R.id.line2);
        line = findViewById(R.id.line);


        submitTV.setOnClickListener(v -> {
            if (mListener != null){
                mListener.onClick(passwordET.getText().toString(), oldPasswordET.getText().toString());
            }
        });
        cancelTV.setOnClickListener(v -> dismiss());

        titleTV.setText(title);
        passwordET.setHint(passwordHint);

        if (single) {
            cancelTV.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
        }
        if (show) {
            passwordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        if (change) {
            oldPasswordET.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        }
    }

    public PasswordDialog setTitle(String title){
        this.title = title;
        return this;
    }

    public PasswordDialog setPasswordHint(String passwordHint) {
        this.passwordHint = passwordHint;
        return this;
    }

    public PasswordDialog setSingle(boolean single) {
        this.single = single;
        return this;
    }

    public PasswordDialog setPasswordShow(boolean show) {
        this.show = show;
        return this;
    }

    public PasswordDialog setPasswordChange(boolean change) {
        this.change = change;
        return this;
    }

    public interface OnListener{
        void onClick(String password, String oldPassword);
    }
}
