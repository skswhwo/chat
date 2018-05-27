package kr.ac.koreatech.chat.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import kr.ac.koreatech.chat.model.User;

public class ApplicationLifecycleHandler implements Application.ActivityLifecycleCallbacks {

    private int numStarted = 0;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (numStarted == 0) {
            // app went to foreground
            if (User.currentUser != null) {
                User.currentUser.setIsOnline(true);
                User.currentUser.update();
            }
        }
        numStarted++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        numStarted--;
        if (numStarted == 0) {
            // app went to background
            if (User.currentUser != null) {
                User.currentUser.setIsOnline(false);
                User.currentUser.update();
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}