package me.mattlogan.twentyseven.game;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;
import me.mattlogan.twentyseven.R;
import me.mattlogan.twentyseven.TwentysevenApp;
import me.mattlogan.twentyseven.messages.IncomingMessageRouter;

public class GameFragment extends Fragment implements IncomingMessageRouter.GameplayListener {

  @Inject IncomingMessageRouter messageRouter;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle state) {
    TwentysevenApp.get(getActivity()).inject(this);
    View view = inflater.inflate(R.layout.fragment_game, root, false);
    ButterKnife.bind(this, view);
    messageRouter.addGameUpdatedListener(this);
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    messageRouter.removeGameUpdatedListener(this);
  }

  @Override public void onGameUpdated(Game game) {

  }
}
