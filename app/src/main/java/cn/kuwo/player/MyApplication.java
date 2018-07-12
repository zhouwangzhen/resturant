package cn.kuwo.player;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.CrashReport;

import cn.kuwo.player.util.RealmHelper;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;

public class MyApplication extends Application {
    private  static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        Logger.addLogAdapter(new AndroidLogAdapter());
        AVOSCloud.initialize(this,"s7XAC1LB5TyCB1o2fObRxnXh","dy57fdLtOpnHBpskQF6qAi7U");
        Realm.init(this);
        RealmConfiguration configuration=new RealmConfiguration.Builder()
                .name(RealmHelper.DB_NAME)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
        CrashReport.initCrashReport(this, "523a908f55", false);
    }
    public static Context getContextObject(){
        return context;
    }
}
