package me.mattlogan.twentyseven.game;

import java.util.Arrays;

/**
 * Immutable representation of game state. 'E' is an empty space, 'X' and 'O' should be obvious!
 */
public class Game {
  
  private static final String TURN_PREFIX = "_TURN_";

  private final char[][][] grid;
  private final char turn;

  private Game(char[][][] grid, char turn) {
    this.grid = grid;
    this.turn = turn;
  }

  public static Game createNewGame() {
    return new Game(emptyGrid(), 'X');
  }

  private static char[][][] emptyGrid() {
    char[][][] grid = new char[3][3][3];
    for (int z = 0; z < 3; z++) {
      for (int y = 0; y < 3; y++) {
        for (int x = 0; x < 3; x++) {
          grid[x][y][z] = 'E';
        }
      }
    }
    return grid;
  }

  public static Game fromString(String str) {
    String gridStr = str.substring(0, str.indexOf(TURN_PREFIX));
    char[][][] grid = emptyGrid();
    for (int strPos = 0; strPos < gridStr.length(); strPos += 1) {
      int x = strPos % 3;
      int y = (strPos / 3) % 3;
      int z = (strPos / 9) % 3;
      grid[x][y][z] = gridStr.charAt(strPos);
    }

    char turn = str.charAt(str.indexOf(TURN_PREFIX) + TURN_PREFIX.length());

    return new Game(grid, turn);
  }

  public char[][][] grid() {
    return grid;
  }

  public int turn() {
    return turn;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int z = 0; z < 3; z++) {
      for (int y = 0; y < 3; y++) {
        for (int x = 0; x < 3; x++) {
          sb.append(grid[x][y][z]);
        }
      }
    }
    sb.append(TURN_PREFIX).append(turn);
    return sb.toString();
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Game game = (Game) o;

    if (turn != game.turn) return false;
    return Arrays.deepEquals(grid, game.grid);
  }

  @Override public int hashCode() {
    int result = Arrays.deepHashCode(grid);
    result = 31 * result + (int) turn;
    return result;
  }
}
