package me.mattlogan.twentyseven;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.mattlogan.twentyseven.game.GameFragment;
import me.mattlogan.twentyseven.intro.IntroFragment;
import me.mattlogan.twentyseven.messages.IncomingMessageRouter;
import me.mattlogan.twentyseven.messages.MessagePublisher;

@Module(
    injects = {
        MainActivity.class,
        IntroFragment.class,
        GameFragment.class
    }
)
public final class AppModule {

  private final Context context;

  public AppModule(Context context) {
    this.context = context;
  }

  @Provides @Singleton Bus bus() {
    return new Bus();
  }

  @Provides @Singleton PlaneTracker planeTracker() {
    return new PlaneTracker();
  }

  @Provides @Singleton GoogleApiClient client() {
    return new GoogleApiClient.Builder(context)
        .addApi(Nearby.MESSAGES_API)
        .build();
  }

  @Provides @Singleton IncomingMessageRouter router() {
    return new IncomingMessageRouter();
  }

  @Provides @Singleton MessagePublisher publisher(GoogleApiClient client) {
    return new MessagePublisher(client);
  }
}
