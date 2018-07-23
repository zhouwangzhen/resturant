package cn.kuwo.player.util;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.orhanobut.logger.Logger;

import cn.kuwo.player.MyApplication;

/**
 * Created by lovely on 2018/6/26
 */
public class LoginUtil {
    public static final String SYSTEMACCOUNT="13888888888";
    public static final String SYSTEMPASSWORD="123456789";

    public static void checkSystemLogin(){
        SharedHelper sharedHelper = new SharedHelper(MyApplication.getContextObject());
        boolean sessionToken = sharedHelper.read("sessionToken").equals("");
        boolean isLogin = (AVUser.getCurrentUser()!= null);
        if (!sessionToken&&isLogin){
        }else{
            AVUser.logInInBackground(SYSTEMACCOUNT, SYSTEMPASSWORD, new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if (e == null) {
                        SharedHelper.save("sessionToken",avUser.getSessionToken());
                    }else{
                        Logger.d(e.getMessage());
                    }
                }
            });
        }
    }
}
