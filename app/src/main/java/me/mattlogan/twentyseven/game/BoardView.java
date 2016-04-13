package me.mattlogan.twentyseven.game;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import me.mattlogan.twentyseven.R;

public class BoardView extends GridLayout {

  public interface ActionListener {
    /**
     * Called when a turn is taken on the provided space (2d to 1d mapped index)
     */
    void onActionTaken(int space, char mark);
  }

  private ActionListener listener;

  private Paint gridPaint;
  private Paint oPaint;
  private Paint xPaint;

  private RectF gridLeftLine;
  private RectF gridRightLine;
  private RectF gridTopLine;
  private RectF gridBottomLine;

  private float roundingRadius;

  private RectF[] os = new RectF[9];

  private Float[][][] xs = new Float[9][2][4]; // 9 Xs of 2 lines with 2 points (x and y) each

  private char turn;
  private char[] planeState = new char[9];

  private final List<RectF> winningOs = new ArrayList<>();
  private final List<Float[][]> winningXs = new ArrayList<>();

  private ValueAnimator winAnimator;

  public BoardView(Context context, AttributeSet attrs) {
    super(context, attrs);
    ButterKnife.bind(this);
    gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    gridPaint.setColor(context.getResources().getColor(R.color.dark_gray));
    oPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    oPaint.setColor(context.getResources().getColor(R.color.green));
    oPaint.setStyle(Paint.Style.STROKE);
    xPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    xPaint.setColor(context.getResources().getColor(R.color.red));
    xPaint.setStyle(Paint.Style.STROKE);
    roundingRadius = 5 * context.getResources().getDisplayMetrics().density;
    setWillNotDraw(false);
    initAnimator();
  }

  private void initAnimator() {
    winAnimator = ValueAnimator.ofFloat(0, (float) (2 * Math.PI));
    winAnimator.setInterpolator(new LinearInterpolator());
    winAnimator.setRepeatCount(ValueAnimator.INFINITE);
    winAnimator.setDuration(500);
  }

  @Override public void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    float third = w / 3f; // third of grid, or width of one grid space

    float thickness = 10f * getResources().getDisplayMetrics().density;
    oPaint.setStrokeWidth(thickness);
    xPaint.setStrokeWidth(thickness);

    // The grid
    gridTopLine = new RectF(0, third - thickness / 2, w, third + thickness / 2);
    gridBottomLine = new RectF(0, 2 * third - thickness / 2, w, 2 * third + thickness / 2);
    gridLeftLine = new RectF(third - thickness / 2, 0, third + thickness / 2, h);
    gridRightLine = new RectF(2 * third - thickness / 2, 0, 2 * third + thickness / 2, h);

    calculateXsAndOs();
  }

  private void calculateXsAndOs() {
    float width = getWidth() / 3f; // width of one grid space
    // Xs and the oh oh ohs
    for (int i = 0; i < 9; i++) {
      // Xs
      int x = i % 3; // 0, 1, 2, 0, 1, 2, 0, 1, 2
      int y = i / 3; // 0, 0, 0, 1, 1, 1, 2, 2, 2

      float offset = width / 4;
      float leftX = x * width + offset;
      float topY = y * width + offset;
      float rightX = x * width + width - offset;
      float bottomY = y * width + width - offset;

      Float[] firstLine = xs[i][0];
      firstLine[0] = leftX;
      firstLine[1] = topY;
      firstLine[2] = rightX;
      firstLine[3] = bottomY;

      Float[] secondLine = xs[i][1];
      secondLine[0] = leftX;
      secondLine[1] = bottomY;
      secondLine[2] = rightX;
      secondLine[3] = topY;

      // Os
      float rad = width / 4;
      float cx = x * width + width / 2;
      float cy = y * width + width / 2;

      os[i] = new RectF(cx - rad, cy - rad, cx + rad, cy + rad);
    }
  }

  @Override public boolean onTouchEvent(MotionEvent e) {
    if (e.getAction() != MotionEvent.ACTION_DOWN || !this.isEnabled()) {
      // Only handle action_down touch events when this view is enabled
      return false;
    }

    float third = getWidth() / 3f;
    int x;
    int y;

    if (e.getX() <= third) {
      x = 0;
    } else if (e.getX() <= 2 * third) {
      x = 1;
    } else {
      x = 2;
    }

    if (e.getY() <= third) {
      y = 0;
    } else if (e.getY() <= 2 * third) {
      y = 1;
    } else {
      y = 2;
    }

    int index = 3 * y + x;

    planeState[index] = turn;

    if (listener != null) {
      listener.onActionTaken(index, turn);
    }

    invalidate();

    return true;
  }

  public void setActionListener(ActionListener listener) {
    this.listener = listener;
  }

  public void updateGrid(char[][][] grid, int zIndex) {
    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 3; x++) {
        planeState[y * 3 + x] = grid[x][y][zIndex];
      }
    }
    invalidate();
  }

  public void showWin(char winner, boolean[][][] winningGrid, int zIndex) {
    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 3; x++) {
        if (winningGrid[x][y][zIndex]) { // Space has win
          int index = y * 3 + x;
          if (planeState[index] == 'X') {
            winningXs.add(xs[index]);
          } else if (planeState[index] == 'O') {
            winningOs.add(os[index]);
          }
        }
      }
    }

    if (winner == 'X') {
      winAnimator.addUpdateListener(xUpdateListener);
    } else if (winner == 'O') {
      winAnimator.addUpdateListener(oUpdateListener);
    }
    winAnimator.start();

    this.setEnabled(false);
  }

  private ValueAnimator.AnimatorUpdateListener xUpdateListener =
      new ValueAnimator.AnimatorUpdateListener() {
        @Override public void onAnimationUpdate(ValueAnimator animation) {
          float theta = (float) animation.getAnimatedValue();
          for (Float[][] x : winningXs) {
            Float[] line1 = x[0];
            Float[] line2 = x[1];
            float cx = (line1[0] + line2[2]) / 2f;
            float rad = (line1[3] - line1[1]) / 2f;
            float newRad = (float) (rad * Math.cos(theta));
            float x0 = cx - newRad;
            float x1 = cx + newRad;
            line1[0] = Math.min(x0, x1);
            line1[2] = Math.max(x0, x1);
            line2[0] = Math.min(x0, x1);
            line2[2] = Math.max(x0, x1);
          }
          invalidate();
        }
      };

  private ValueAnimator.AnimatorUpdateListener oUpdateListener =
      new ValueAnimator.AnimatorUpdateListener() {
        @Override public void onAnimationUpdate(ValueAnimator animation) {
          float theta = (float) animation.getAnimatedValue();
          for (RectF oval : winningOs) {
            float cx = oval.centerX();
            float rad = (oval.bottom - oval.top) / 2f;
            float newRad = (float) (rad * Math.cos(theta));
            float x0 = cx - newRad;
            float x1 = cx + newRad;
            oval.left = Math.min(x0, x1);
            oval.right = Math.max(x0, x1);
          }
          invalidate();
        }
      };

  public void clearWin() {
    winAnimator.pause();
    winAnimator.removeAllUpdateListeners();
    winningOs.clear();
    winningXs.clear();
    calculateXsAndOs();
    this.setEnabled(true);
  }

  public void updateTurn(char turn) {
    this.turn = turn;
  }

  @Override public void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawRoundRect(gridTopLine, roundingRadius, roundingRadius, gridPaint);
    canvas.drawRoundRect(gridBottomLine, roundingRadius, roundingRadius, gridPaint);
    canvas.drawRoundRect(gridLeftLine, roundingRadius, roundingRadius, gridPaint);
    canvas.drawRoundRect(gridRightLine, roundingRadius, roundingRadius, gridPaint);

    for (int i = 0; i < planeState.length; i++) {
      char mark = planeState[i];
      if (mark == 'X') {
        Float[][] lines = xs[i];
        Float[] firstLine = lines[0];
        Float[] secondLine = lines[1];
        canvas.drawLine(firstLine[0], firstLine[1], firstLine[2], firstLine[3], xPaint);
        canvas.drawLine(secondLine[0], secondLine[1], secondLine[2], secondLine[3], xPaint);
      } else if (mark == 'O') {
        canvas.drawOval(os[i], oPaint);
      }
    }
  }
}
