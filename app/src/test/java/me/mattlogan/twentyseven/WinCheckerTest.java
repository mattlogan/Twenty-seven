package me.mattlogan.twentyseven;

import org.junit.Test;

import me.mattlogan.twentyseven.game.Game;
import me.mattlogan.twentyseven.game.WinChecker;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class WinCheckerTest {

  @Test
  public void testHorizontalWinInFrontPlane() throws Exception {
    char[][][] grid = Game.createNewGame().grid();
    grid[0][0][0] = 'X';
    grid[0][1][0] = 'X';
    grid[0][2][0] = 'X';

    WinChecker.Win win = WinChecker.checkForWinner(grid);

    assertNotNull(win);
    assertTrue(win.winner() == 'X');
  }

  @Test
  public void testCubeDiagonalWin() throws Exception {
    char[][][] grid = Game.createNewGame().grid();
    grid[0][0][0] = 'O';
    grid[1][1][1] = 'O';
    grid[2][2][2] = 'O';

    WinChecker.Win win = WinChecker.checkForWinner(grid);

    assertNotNull(win);
    assertTrue(win.winner() == 'O');
  }

  @Test
  public void testNoWinner() throws Exception {
    char[][][] grid = Game.createNewGame().grid();

    assertNull(WinChecker.checkForWinner(grid));
  }
}