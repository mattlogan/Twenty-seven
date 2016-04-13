package me.mattlogan.twentyseven.game;

import android.support.annotation.Nullable;

public final class WinChecker {

  private WinChecker() {
    // No instances
  }

  public static class Win {
    private final char winner;
    private final boolean[][][] spaces;

    public Win(char winner, boolean[][][] spaces) {
      this.winner = winner;
      this.spaces = spaces;
    }

    public char winner() {
      return winner;
    }

    public boolean[][][] spaces() {
      return spaces;
    }
  }

  /**
   * Checks for a winner. Perhaps a more elegant solution exists...
   *
   * @return a Result object with winner and winning spaces if there is a winner, or null if not
   */
  @Nullable public static Win checkForWinner(char[][][] grid) {
    // iterates through each x-y plane along the z-axis
    for (int i = 0; i < 3; i++) {

      // checks for vertical wins in the current x-y plane
      for (int j = 0; j < 3; j++) {
        char top = grid[j][0][i];
        char mid = grid[j][1][i];
        char bottom = grid[j][2][i];

        if (top != 'E' && top == mid && top == bottom) {
          return new Win(top, winningSpaces(j, 0, i, j, 1, i, j, 2, i));
        }
      }

      // front plane, checks for horizontal wins in the current x-y plane
      for (int j = 0; j < 3; j++) {
        char left = grid[0][j][i];
        char mid = grid[1][j][i];
        char right = grid[2][j][i];

        if (left != 'E' && left == mid && left == right) {
          return new Win(left, winningSpaces(0, j, i, 1, j, i, 2, j, i));
        }
      }

      // front plane, checks for diagonal wins in the current x-y plane
      char topLeft = grid[0][0][i];
      char topRight = grid[2][0][i];
      char mid = grid[1][1][i];
      char bottomLeft = grid[0][2][i];
      char bottomRight = grid[2][2][i];

      // top-left to bottom-right win in the current x-y plane
      if (topLeft != 'E' && topLeft == mid && topLeft == bottomRight) {
        return new Win(topLeft, winningSpaces(0, 0, i, 1, 1, i, 2, 2, i));
      }

      // top-right to bottom-left win in the current x-y plane
      if (topRight != 'E' && topRight == mid && topRight == bottomLeft) {
        return new Win(topRight, winningSpaces(2, 0, i, 1, 1, i, 0, 2, i));
      }
    }

    // check for wins straight along z-axis
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        char front = grid[i][j][0];
        char mid = grid[i][j][1];
        char back = grid[i][j][2];

        if (front != 'E' && front == mid && front == back) {
          return new Win(front, winningSpaces(i, j, 0, i, j, 1, i, j, 2));
        }
      }
    }

    // iterate through y values, or x-z planes
    for (int i = 0; i < 3; i++) {
      char frontLeft = grid[0][i][0];
      char frontRight = grid[2][i][0];
      char mid = grid[1][i][1];
      char backLeft = grid[0][i][2];
      char backRight = grid[2][i][2];

      // front-left to back-right diagonal in current x-z plane
      if (frontLeft != 'E' && frontLeft == mid && frontLeft == backRight) {
        return new Win(frontLeft, winningSpaces(0, i, 0, 1, i, 1, 2, i, 2));
      }

      // front-right to back-left diagonal in current x-z plane
      if (frontRight != 'E' && frontRight == mid && frontRight == backLeft) {
        return new Win(frontRight, winningSpaces(2, i, 0, 1, i, 1, 0, i, 2));
      }
    }

    // iterate through x values, or y-z planes
    for (int i = 0; i < 3; i++) {
      char frontTop = grid[i][0][0];
      char frontBottom = grid[i][2][0];
      char mid = grid[i][1][1];
      char backTop = grid[i][0][2];
      char backBottom = grid[i][2][2];

      // front-top to back-bottom diagonal in current y-z plane
      if (frontTop != 'E' && frontTop == mid && frontTop == backBottom) {
        return new Win(frontTop, winningSpaces(i, 0, 0, i, 1, 1, i, 2, 2));
      }

      // front-bottom to back-top diagonal in current y-z plane
      if (frontBottom != 'E' && frontBottom == mid && frontBottom == backTop) {
        return new Win(frontBottom, winningSpaces(i, 2, 0, i, 1, 1, i, 0, 2));
      }
    }

    // check for 3-d diagonals!!!!
    char topLeftFront = grid[0][0][0];
    char topRightFront = grid[2][0][0];
    char bottomLeftFront = grid[0][2][0];
    char bottomRightFront = grid[2][2][0];
    char mid = grid[1][1][1];
    char topLeftBack = grid[0][0][2];
    char topRightBack = grid[2][0][2];
    char bottomLeftBack = grid[0][2][2];
    char bottomRightBack = grid[2][2][2];

    if (topLeftFront != 'E' && topLeftFront == mid && topLeftFront == bottomRightBack) {
      return new Win(topLeftFront, winningSpaces(0, 0, 0, 1, 1, 1, 2, 2, 2));
    }

    if (topRightFront != 'E' && topRightFront == mid && topRightFront == bottomLeftBack) {
      return new Win(topRightFront, winningSpaces(2, 0, 0, 1, 1, 1, 0, 2, 2));
    }

    if (bottomLeftFront != 'E' && bottomLeftFront == mid && bottomLeftFront == topRightBack) {
      return new Win(bottomLeftFront, winningSpaces(0, 2, 0, 1, 1, 1, 2, 0, 2));
    }

    if (bottomRightFront != 'E' && bottomRightFront == mid && bottomRightFront == topLeftBack) {
      return new Win(bottomRightFront, winningSpaces(2, 2, 0, 1, 1, 1, 0, 0, 2));
    }

    return null;
  }

  private static boolean[][][] winningSpaces(int x0, int y0, int z0,
                                             int x1, int y1, int z1,
                                             int x2, int y2, int z2) {
    boolean[][][] array = new boolean[3][3][3];
    array[x0][y0][z0] = true;
    array[x1][y1][z1] = true;
    array[x2][y2][z2] = true;
    return array;
  }
}
