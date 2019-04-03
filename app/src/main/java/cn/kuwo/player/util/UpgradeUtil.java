package cn.kuwo.player.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import cn.kuwo.player.BuildConfig;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.api.CommodityApi;

/**
 * Created by lovely on 2018/7/18
 */
public class UpgradeUtil {
    private static QMUITipDialog tipDialog;
    private static Context mContext;
    private static ProgressDialog mProgressDialog;
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public static void checkInfo(final Context context) {
        mContext = context;
        initDialog(context);
        tipDialog.show();
        AVQuery<AVObject> query = new AVQuery<>("OffineControl");
        query.whereEqualTo("store", CONST.STORECODE);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    final AVObject avObject = list.get(0);
                    final SharedHelper sharedHelper = new SharedHelper(context);
                    if (!avObject.getString("updateDate").equals(sharedHelper.read("updateDate"))) {
                        CommodityApi.getOfflineCommodity().findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(final List<AVObject> list, AVException e) {
                                if (e == null) {
                                    RealmUtil.setProductBeanRealm(list);
                                    sharedHelper.save("updateDate", avObject.getString("updateDate"));
                                    checkVersion(avObject);
                                } else {
                                    tipDialog.dismiss();
                                }
                            }
                        });
                    } else {
                        checkVersion(avObject);
                    }
                } else {
                    tipDialog.dismiss();
                }
            }
        });
    }

    private static void checkVersion(AVObject avObject) {
        tipDialog.dismiss();
        if (MyUtils.getVersionCode(MyApplication.getContextObject()) < avObject.getInt("version") && avObject.getAVFile("upgrade") != null) {
            String upgradeUrl = avObject.getAVFile("upgrade").getUrl();
            ShowDialog(upgradeUrl);
        } else {
            ToastUtil.showShort(MyApplication.getContextObject(), "已经是最新最新数据");
        }
    }

    private static void ShowDialog(final String upgradeUrl) {
        new AlertDialog.Builder(mContext)
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

    public static void initDialog(Context context) {
        tipDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载中")
                .create();
    }

    private static void startDownloadApk(final String url) {
        mProgressDialog = new ProgressDialog(mContext, android.R.style.Theme_Material_Light_Dialog);
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

    private static File downLoadFile(String upgradeUrl) throws IOException {
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

    private static void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        Uri contentUri;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            contentUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            contentUri = Uri.fromFile(file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");

        }

        mContext.startActivity(intent);
    }

    private static void requestWritePermission(String upgradeUrl) {
        int permissionCheck = ContextCompat.checkSelfPermission(MyApplication.getContextObject(), "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                startDownloadApk(upgradeUrl);
            } else {
                Toast.makeText(MyApplication.getContextObject(), "请确认外部存储可用", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MyApplication.getContextObject(), "请设置读写存储权限", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 100);
        }
    }
}
