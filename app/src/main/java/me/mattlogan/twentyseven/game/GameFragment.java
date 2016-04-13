package me.mattlogan.twentyseven.game;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.mattlogan.twentyseven.MainActivity;
import me.mattlogan.twentyseven.PlaneTracker;
import me.mattlogan.twentyseven.R;
import me.mattlogan.twentyseven.messages.IncomingMessageRouter;
import me.mattlogan.twentyseven.messages.MessagePublisher;
import timber.log.Timber;

public class GameFragment extends Fragment
    implements IncomingMessageRouter.GameplayListener, BoardView.ActionListener {

  @Inject IncomingMessageRouter messageRouter;
  @Inject MessagePublisher messagePublisher;
  @Inject PlaneTracker planeTracker;

  @Bind(R.id.board_view) BoardView boardView;
  @Bind(R.id.game_status_text) TextView statusText;
  @Bind(R.id.plane_label) TextView planeLabel;
  @Bind(R.id.button_new_game) Button newGameButton;

  private Game game;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle state) {
    ((MainActivity) getActivity()).inject(this);
    View view = inflater.inflate(R.layout.fragment_game, root, false);
    ButterKnife.bind(this, view);
    messageRouter.addGameUpdatedListener(this);
    boardView.setActionListener(this);
    planeLabel.setText(planeTracker.currentPlane().toDisplayString());
    onGameUpdated(Game.createNewGame()); // Start with new game
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    messageRouter.removeGameUpdatedListener(this);
  }

  @Override public void onGameUpdated(Game game) {
    Timber.d("onGameUpdated: %s", game);
    this.game = game;
    updateViews();
  }

  @Override public void onActionTaken(int space, char mark) {
    Timber.d("onActionTaken, space: %d, mark %c", space, mark);
    char[][][] grid = game.grid();
    grid[space % 3][space / 3][planeTracker.currentPlane().zValue()] = mark;
    game.incrementTurn();
    messagePublisher.publishGameUpdateMessage(game);
    updateViews();
  }

  private void updateViews() {
    WinChecker.Win win = WinChecker.checkForWinner(game.grid());
    if (win != null) {
      statusText.setText(getString(R.string.x_wins, win.winner()));
      boardView.showWin(win.winner(), win.spaces(), planeTracker.currentPlane().zValue());
      newGameButton.setVisibility(View.VISIBLE);
    } else {
      boardView.updateTurn(game.turn());
      boardView.updateGrid(game.grid(), planeTracker.currentPlane().zValue());
      boardView.clearWin();
      statusText.setText(getString(R.string.xs_turn, game.turn()));
      newGameButton.setVisibility(View.GONE);
    }
  }

  @OnClick(R.id.button_new_game)
  public void onNewGameClicked() {
    Timber.d("onNewGameClicked");
    game = Game.createNewGame();
    updateViews();
    messagePublisher.publishGameUpdateMessage(game);
  }
}
