package android.amoscxy.com.smartsearchlayout;

import android.amoscxy.com.smartsearchlayout.ormlite.CacheAll;
import android.amoscxy.com.smartsearchlayout.ormlite.DatabaseHelper;
import android.app.Application;

import com.iflytek.cloud.SpeechUtility;

/**
 * Created by cxy on 2018/7/26.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initXunFei();
        initDatabase();
    }

    private void initXunFei() {
        /**
         * @Description: 调用讯飞语音后台
         * @Author: BG235144/AMOSCXY
         * @Data 2017/3/2 18:35
         */
        SpeechUtility.createUtility(getApplicationContext(), "appid=" + getString(R.string.app_id));
    }

    //初始化ormlite数据库
    public void initDatabase(){
        DatabaseHelper.version = 2;
        DatabaseHelper.classes.add(CacheAll.class); //全部数据项
    }
}
