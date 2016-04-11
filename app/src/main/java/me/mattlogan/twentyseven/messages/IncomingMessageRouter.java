package me.mattlogan.twentyseven.messages;

import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.util.ArrayList;
import java.util.List;

import me.mattlogan.twentyseven.Plane;
import me.mattlogan.twentyseven.game.Game;
import timber.log.Timber;

import static me.mattlogan.twentyseven.messages.MessagePublisher.PLANE_SELECTED;
import static me.mattlogan.twentyseven.messages.MessagePublisher.GAME_UPDATED;

public final class IncomingMessageRouter extends MessageListener {

  public interface RemotePlaneSelectedListener {
    /** Plane selected on another device */
    void onRemotePlaneSelected(Plane plane);
  }

  public interface GameplayListener {
    void onGameUpdated(Game game);
  }

  private List<RemotePlaneSelectedListener> remotePlaneSelectedListeners = new ArrayList<>();
  private List<GameplayListener> gameplayListeners = new ArrayList<>();

  @Override public void onFound(Message message) {
    String s = new String(message.getContent());
    Timber.d("onFound: %s", s);
    if (s.startsWith(PLANE_SELECTED)) {
      Plane selectedPlane = Plane.valueOf(s.substring(PLANE_SELECTED.length()));
      for (RemotePlaneSelectedListener listener : remotePlaneSelectedListeners) {
        listener.onRemotePlaneSelected(selectedPlane);
      }
    } else if (s.startsWith(GAME_UPDATED)) {
      Game game = Game.fromString(s.substring(GAME_UPDATED.length()));
      for (GameplayListener listener : gameplayListeners) {
        listener.onGameUpdated(game);
      }
    }
  }

  public void addPlaneSelectedListener(RemotePlaneSelectedListener listener) {
    remotePlaneSelectedListeners.add(listener);
  }

  public void addGameUpdatedListener(GameplayListener listener) {
    gameplayListeners.add(listener);
  }

  public void removePlaneSelectedListener(RemotePlaneSelectedListener listener) {
    remotePlaneSelectedListeners.remove(listener);
  }

  public void removeGameUpdatedListener(GameplayListener listener) {
    gameplayListeners.remove(listener);
  }
}
