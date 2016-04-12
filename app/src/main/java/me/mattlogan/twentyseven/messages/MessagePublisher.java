package me.mattlogan.twentyseven.messages;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;

import me.mattlogan.twentyseven.Plane;
import me.mattlogan.twentyseven.game.Game;
import timber.log.Timber;

public final class MessagePublisher {

  static final String PLANE_SELECTED = "PLANE_SELECTED_";
  static final String GAME_UPDATED = "GAME_UPDATED_";

  private final GoogleApiClient client;
  private final Handler handler;

  public MessagePublisher(GoogleApiClient client) {
    this.client = client;
    this.handler = new Handler(Looper.getMainLooper());
  }

  public void publishPlaneSelectedMessage(Plane plane) {
    String message = PLANE_SELECTED + plane;
    Timber.d("Publishing plane selected message: %s", message);
    publish(message);
  }

  public void publishGameUpdateMessage(Game game) {
    String message = GAME_UPDATED + game;
    Timber.d("Publishing game updated message: %s", message);
    publish(message);
  }

  private void publish(String messageString) {
    final Message message = new Message(messageString.getBytes());
    Nearby.Messages.publish(client, message)
        .setResultCallback(new ResultCallback<Status>() {
          @Override public void onResult(@NonNull Status status) {
            if (status.isSuccess()) {
              Timber.d("Successfully published message");
            } else {
              Timber.d("Failed to publish message: %s", status.getStatusMessage());
            }
          }
        });

    // Unpublish message after five seconds
    handler.postDelayed(new Runnable() {
      @Override public void run() {
        Nearby.Messages.unpublish(client, message);
        Timber.d("Unpublished message");
      }
    }, 5000);
  }
}
