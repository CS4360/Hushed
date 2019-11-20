package com.example.hushed;

import android.os.Bundle;
import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hushed.database.DataSource;
import com.example.hushed.messages.ConversationsActivity;

public class HushedApplication extends Application implements Application.ActivityLifecycleCallbacks {
    public void onCreate() {
        super.onCreate();
        DataSource.Companion.loadFrom(getSharedPreferences("DataSource", Context.MODE_PRIVATE));

        registerActivityLifecycleCallbacks(this);
    }

    @Override public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) { }
    @Override public void onActivityStarted(@NonNull Activity activity) { }
    @Override public void onActivityResumed(@NonNull Activity activity) { }
    @Override public void onActivityPaused(@NonNull Activity activity) { }
    @Override public void onActivityStopped(@NonNull Activity activity) {
        if (activity instanceof ConversationsActivity) {
            DataSource.Companion.saveTo(getSharedPreferences("DataSource", Context.MODE_PRIVATE));
        }
    }
    @Override public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) { }
    @Override public void onActivityDestroyed(@NonNull Activity activity) { }
    @Override public void onActivityPreDestroyed(@NonNull Activity activity) { }
}
