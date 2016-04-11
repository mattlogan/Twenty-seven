package me.mattlogan.twentyseven.messages;

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

  public MessagePublisher(GoogleApiClient client) {
    this.client = client;
  }

  public void publishPlaneSelectedMessage(Plane plane) {
    String str = PLANE_SELECTED + plane;
    Timber.d("Publishing plane selected message: %s", str);
    publish(str);
  }

  public void publishGameUpdateMessage(Game game) {
    String str = GAME_UPDATED + game;
    Timber.d("Publishing game updated message: %s", str);
    publish(str);
  }

  private void publish(String message) {
    Nearby.Messages.publish(client, new Message(message.getBytes()))
        .setResultCallback(new ResultCallback<Status>() {
          @Override public void onResult(@NonNull Status status) {
            if (status.isSuccess()) {
              Timber.d("Successfully published message");
            } else {
              Timber.d("Failed to publish message: %s", status.getStatusMessage());
            }
          }
        });
  }
}
