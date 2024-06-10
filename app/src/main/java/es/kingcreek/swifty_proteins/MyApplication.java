package es.kingcreek.swifty_proteins;

import android.app.Activity;
import android.app.Application;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.LifecycleObserver;

import java.util.ArrayList;
import java.util.List;

import es.kingcreek.swifty_proteins.observers.AppLifecycleObserver;

public class MyApplication extends Application {

    private static MyApplication instance;
    private List<Activity> activityList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    public void finishAllActivities() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        activityList.clear();
    }
}