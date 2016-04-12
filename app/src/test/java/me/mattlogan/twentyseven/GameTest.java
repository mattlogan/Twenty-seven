package me.mattlogan.twentyseven;

import org.junit.Test;

import me.mattlogan.twentyseven.game.Game;

import static junit.framework.Assert.assertEquals;

public class GameTest {

  @Test
  public void testGameToMessage() {
    Game game = Game.createNewGame();
    char[][][] grid = game.grid();
    grid[0][0][0] = 'X';
    grid[1][0][0] = 'X';
    grid[2][0][0] = 'X';

    grid[0][0][2] = 'O';
    grid[1][1][2] = 'O';
    grid[2][1][2] = 'O';

    assertEquals("XXXEEEEEEEEEEEEEEEOEEEOOEEE_TURN_X", game.toString());
  }

  @Test
  public void testMessageToGame() {
    Game game = Game.createNewGame();
    char[][][] grid = game.grid();
    grid[0][0][0] = 'X';
    grid[1][0][0] = 'X';
    grid[2][0][0] = 'X';

    grid[0][0][2] = 'O';
    grid[1][1][2] = 'O';
    grid[2][1][2] = 'O';

    assertEquals(game, Game.fromString("XXXEEEEEEEEEEEEEEEOEEEOOEEE_TURN_X"));
  }
}
