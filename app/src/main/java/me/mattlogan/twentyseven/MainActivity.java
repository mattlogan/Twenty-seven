package me.mattlogan.twentyseven;

import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import dagger.ObjectGraph;
import me.mattlogan.twentyseven.intro.IntroFragment;
import me.mattlogan.twentyseven.messages.IncomingMessageRouter;
import timber.log.Timber;

/**
 * Host of IntroFragment and GameFragment, handles connecting to Nearby API
 */
public final class MainActivity extends AppCompatActivity {

  @Inject GoogleApiClient client;
  @Inject Bus bus;
  @Inject IncomingMessageRouter messageRouter;

  private static final int PERMISSION_REQ_CODE = 123;

  private ObjectGraph graph;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (Timber.treeCount() == 0) {
      Timber.plant(new Timber.DebugTree());
    }
    graph = ObjectGraph.create(new AppModule(this));
    inject(this);
    setContentView(R.layout.activity_main);
    if (savedInstanceState == null) {
      getSupportFragmentManager()
          .beginTransaction()
          .add(R.id.fragment_container, new IntroFragment())
          .commit();
    }
  }

  public void inject(Object o) {
    graph.inject(o);
  }

  @Override public void onStart() {
    super.onStart();
    Timber.d("onStart");
    initializeNearbyApi();
  }

  private void initializeNearbyApi() {
    client.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
      @Override public void onConnected(@Nullable Bundle bundle) {
        Timber.d("onConnected");
        checkPermission();
      }

      @Override public void onConnectionSuspended(int i) {
        Timber.d("onConnectionSuspended");
      }
    });

    client.connect();
  }

  private void checkPermission() {
    Nearby.Messages.getPermissionStatus(client).setResultCallback(new ResultCallback<Status>() {
      @Override public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
          Timber.d("Permission request successful");
          onNearbyApiAvailable();
        } else if (status.hasResolution()) {
          try {
            Timber.d("Permission request failed, attempting to resolve");
            status.startResolutionForResult(MainActivity.this, PERMISSION_REQ_CODE);
          } catch (IntentSender.SendIntentException e) {
            Timber.d(e, "Error resolving permission failure");
          }
        }
      }
    });
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == PERMISSION_REQ_CODE && resultCode == RESULT_OK) {
      onNearbyApiAvailable();
    }
  }

  private void onNearbyApiAvailable() {
    Timber.d("onNearbyApiAvailable");
    SubscribeOptions options = new SubscribeOptions.Builder()
        .setStrategy(new Strategy.Builder()
            .setTtlSeconds(Strategy.TTL_SECONDS_INFINITE)
            .build())
        .build();
    Nearby.Messages.subscribe(client, messageRouter, options);
    bus.post(new OnNearbyApiAvailableEvent());
  }

  @Override public void onStop() {
    super.onStop();
    Timber.d("onStop");
    if (client.isConnected()) {
      Nearby.Messages.unsubscribe(client, messageRouter);
      client.disconnect();
    }
  }
}
