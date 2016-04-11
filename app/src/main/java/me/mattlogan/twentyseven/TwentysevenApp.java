package me.mattlogan.twentyseven;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import dagger.ObjectGraph;
import timber.log.Timber;

public class TwentysevenApp extends Application implements Application.ActivityLifecycleCallbacks {

  private ObjectGraph graph;

  @Override public void onCreate() {
    super.onCreate();
    Timber.plant(new Timber.DebugTree());
    registerActivityLifecycleCallbacks(this);
  }

  public static TwentysevenApp get(Context context) {
    return (TwentysevenApp) context.getApplicationContext();
  }

  public void inject(Object o) {
    graph.inject(o);
  }

  @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    graph = ObjectGraph.create(new AppModule(activity)); // Create graph with Activity context
    graph.inject(activity);
  }

  @Override public void onActivityStarted(Activity activity) {

  }

  @Override public void onActivityResumed(Activity activity) {

  }

  @Override public void onActivityPaused(Activity activity) {

  }

  @Override public void onActivityStopped(Activity activity) {

  }

  @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

  }

  @Override public void onActivityDestroyed(Activity activity) {

  }
}
