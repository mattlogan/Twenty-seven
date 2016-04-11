package me.mattlogan.twentyseven.intro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.mattlogan.twentyseven.OnNearbyApiAvailableEvent;
import me.mattlogan.twentyseven.Plane;
import me.mattlogan.twentyseven.PlaneTracker;
import me.mattlogan.twentyseven.R;
import me.mattlogan.twentyseven.TwentysevenApp;
import me.mattlogan.twentyseven.game.GameFragment;
import me.mattlogan.twentyseven.messages.IncomingMessageRouter;
import me.mattlogan.twentyseven.messages.MessagePublisher;

public class IntroFragment extends Fragment
    implements IncomingMessageRouter.RemotePlaneSelectedListener {

  @Inject Bus bus;
  @Inject PlaneTracker planeTracker;
  @Inject MessagePublisher messagePublisher;
  @Inject IncomingMessageRouter messageRouter;

  @Bind(R.id.buttons_layout) View buttonsLayout;
  @Bind(R.id.button_front) Button frontButton;
  @Bind(R.id.button_middle) Button middleButton;
  @Bind(R.id.button_back) Button backButton;
  @Bind(R.id.waiting_text) TextView waitingText;

  private int numSelectedRemotePlanes;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle state) {
    TwentysevenApp.get(getActivity()).inject(this);
    View view = inflater.inflate(R.layout.fragment_intro, root, false);
    ButterKnife.bind(this, view);
    bus.register(this);
    messageRouter.addPlaneSelectedListener(this);
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    bus.unregister(this);
    messageRouter.removePlaneSelectedListener(this);
  }

  @OnClick(R.id.button_front) public void onFrontButtonClicked() {
    onLocalPlaneSelected(Plane.FRONT);
  }

  @OnClick(R.id.button_middle) public void onMiddleButtonClicked() {
    onLocalPlaneSelected(Plane.MIDDLE);
  }

  @OnClick(R.id.button_back) public void onBackButtonClicked() {
    onLocalPlaneSelected(Plane.BACK);
  }

  private void enableButtons() {
    frontButton.setEnabled(true);
    middleButton.setEnabled(true);
    backButton.setEnabled(true);
  }

  private void onLocalPlaneSelected(Plane plane) {
    messagePublisher.publishPlaneSelectedMessage(plane);
    planeTracker.updatePlane(plane);
    if (numSelectedRemotePlanes == 2) {
      continueToGame();
    } else {
      showWaiting();
    }
  }

  @Subscribe public void onNearbyApiAvailable(OnNearbyApiAvailableEvent e) {
    enableButtons();
  }

  @Override public void onRemotePlaneSelected(Plane plane) {
    switch (plane) {
      case FRONT:
        frontButton.setEnabled(false);
        break;
      case MIDDLE:
        middleButton.setEnabled(false);
        break;
      case BACK:
        backButton.setEnabled(false);
        break;
    }

    if (++numSelectedRemotePlanes == 2 && planeTracker.currentPlane() != null) {
      continueToGame();
    }
  }

  private void showWaiting() {
    buttonsLayout.setVisibility(View.GONE);
    waitingText.setVisibility(View.VISIBLE);
  }

  private void continueToGame() {
    getActivity().getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fragment_container, new GameFragment())
        .commit();
  }
}
