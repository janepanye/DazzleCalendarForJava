package fuck.dazzlecalendar.MyApplication;

import android.app.Application;

/**
 * Created by mac on 2017/12/28.
 */

public class MyApplication extends Application {
    private static MyApplication instance = null;
    public static MyApplication getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
