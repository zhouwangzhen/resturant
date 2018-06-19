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
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

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
import cn.kuwo.player.base.BaseFragment;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ToastUtil;

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
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private Activity mActivity;
    private String mParam;
    private Context context;
    private AVObject CommodityAVObject=null;
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
        context = MyApplication.getContextObject();
        findChangeCommodity();
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
                        CommodityAVObject=list.get(0);
                        changeCommodityName.setText(CommodityAVObject.getString("name"));
                        changeCommodityPrice.setText(CommodityAVObject.getDouble("price")+"");
                        setListener();
                    }
                } else {
                    Logger.d(e.getMessage());
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
                                    if (CommodityAVObject!=null){
                                        showDialog();
                                        CommodityAVObject.put("name",text);
                                        CommodityAVObject.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                hideDialog();
                                                if (e==null){
                                                    ToastUtil.showLong(MyApplication.getContextObject(),"修改成功");
                                                    changeCommodityName.setText(text);
                                                }else{
                                                    ToastUtil.showLong(MyApplication.getContextObject(),e.getMessage());
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
                                if (text != null && text.length() > 0&&MyUtils.isDoubleOrFloat(text)&&Double.parseDouble(text)>0) {
                                    dialog.dismiss();
                                    if (CommodityAVObject!=null){
                                        showDialog();
                                        CommodityAVObject.put("price",Double.parseDouble(text));
                                        CommodityAVObject.put("actualprice",Double.parseDouble(text));
                                        CommodityAVObject.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                hideDialog();
                                                if (e==null){
                                                    ToastUtil.showLong(MyApplication.getContextObject(),"修改成功");
                                                    changeCommodityPrice.setText(text);
                                                }else{
                                                    ToastUtil.showLong(MyApplication.getContextObject(),e.getMessage());
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
        upgrade();
    }

    private void upgrade() {
        showDialog();
        AVQuery<AVObject> query = new AVQuery<>("OffineControl");
        query.whereEqualTo("store", CONST.STORECODE);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                hideDialog();
                if (e == null) {
                    if (list.size() > 0 && MyUtils.getVersionCode(MyApplication.getContextObject()) < list.get(0).getInt("version") && list.get(0).getAVFile("upgrade") != null) {
                        String upgradeUrl = list.get(0).getAVFile("upgrade").getUrl();
                        ShowDialog(upgradeUrl);
                    } else {
                        ToastUtil.showShort(MyApplication.getContextObject(), "已经是最新版本");
                    }
                } else {
                    ToastUtil.showShort(MyApplication.getContextObject(), "网络错误");
                }
            }
        });

    }

    private void ShowDialog(final String upgradeUrl) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage("您的版本过低，请去更新最新版本，如不更新将无法继续使用")
                .setPositiveButton("更新",
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestWritePermission(upgradeUrl);
                                } else {
                                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                        startDownloadApk(upgradeUrl);
                                    } else {
                                        Toast.makeText(MyApplication.getContextObject(), "请确认外部存储可用", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        })
                .setNegativeButton(android.R.string.no,
                        new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        })
                .setCancelable(false)
                .create()
                .show();
    }

    private void startDownloadApk(final String url) {
        mProgressDialog = new ProgressDialog(getContext(), android.R.style.Theme_Material_Light_Dialog);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setTitle("正在下载中");
        mProgressDialog.setMax(100);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    downLoadFile(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 下载文件
     *
     * @return
     * @throws IOException
     */
    private File downLoadFile(String upgradeUrl) throws IOException {
        URL url = new URL(upgradeUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        InputStream is = conn.getInputStream();
        final File file = new File(Environment.getExternalStorageDirectory(), "app.apk");
        FileOutputStream fos = new FileOutputStream(file);
        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] buffer = new byte[1024];
        int len;
        int current = 0;
        int total = conn.getContentLength();
        float percent;
        while ((len = bis.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
            current += len;
            //获取当前下载量
            percent = (float) current / (float) total;
            mProgressDialog.setProgress((int) (percent * 100));
        }
        fos.close();
        bis.close();
        is.close();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
                installApk(file);
            }
        });
        return file;
    }

    /**
     * `
     * 安装apk
     *
     * @param file
     */
    private void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        Uri contentUri;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            contentUri = Uri.fromFile(file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");

        }

        startActivity(intent);
    }

    private void requestWritePermission(String upgradeUrl) {
        int permissionCheck = ContextCompat.checkSelfPermission(MyApplication.getContextObject(), "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                startDownloadApk(upgradeUrl);
            } else {
                Toast.makeText(MyApplication.getContextObject(), "请确认外部存储可用", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MyApplication.getContextObject(), "请设置读写存储权限", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions((Activity) mActivity, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 100);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
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
