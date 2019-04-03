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
    public static void checkSystemLogin(){
        SharedHelper sharedHelper = new SharedHelper(MyApplication.getContextObject());
        boolean sessionToken = sharedHelper.read("sessionToken").equals("");
        boolean isLogin = (AVUser.getCurrentUser()!= null);
        if (!sessionToken&&isLogin){
        }else{
            AVUser.logInInBackground(CONST.SYSTEM_ADMIN_TEL, CONST.SYSTEM_ADMIN_PASSWORD, new LogInCallback<AVUser>() {
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
