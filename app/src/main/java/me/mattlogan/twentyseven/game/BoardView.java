package me.mattlogan.twentyseven.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

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
  private Paint rainbowPaint; // Yes.

  private RectF gridLeftLine;
  private RectF gridRightLine;
  private RectF gridTopLine;
  private RectF gridBottomLine;

  private float roundingRadius;

  private PointF oCenters[] = new PointF[9];
  private float oRadius;

  private float[][][] xs = new float[9][2][4]; // 9 Xs of 2 lines with 2 points (x and y) each

  private char turn;
  private char[] planeState = new char[9];

  private boolean[] winSpaces = new boolean[9];

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
  }

  private void initWinPaint(float width) {
    rainbowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    rainbowPaint.setStyle(Paint.Style.STROKE);

    int[] rainbow = new int[]{
        getResources().getColor(R.color.rainbow_red),
        getResources().getColor(R.color.rainbow_yellow),
        getResources().getColor(R.color.rainbow_green),
        getResources().getColor(R.color.rainbow_blue),
        getResources().getColor(R.color.rainbow_purple)
    };
    Shader shader = new LinearGradient(width / 2, 0, 1.5f * width, 0, rainbow, null,
        Shader.TileMode.MIRROR);
    rainbowPaint.setShader(shader);
  }

  @Override public void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    float third = w / 3f;
    float sixth = w / 6f;

    float thickness = 10f * getResources().getDisplayMetrics().density;
    oPaint.setStrokeWidth(thickness);
    xPaint.setStrokeWidth(thickness);

    initWinPaint(sixth);
    rainbowPaint.setStrokeWidth(thickness);

    // The grid
    gridTopLine = new RectF(0, third - thickness / 2, w, third + thickness / 2);
    gridBottomLine = new RectF(0, 2 * third - thickness / 2, w, 2 * third + thickness / 2);
    gridLeftLine = new RectF(third - thickness / 2, 0, third + thickness / 2, h);
    gridRightLine = new RectF(2 * third - thickness / 2, 0, 2 * third + thickness / 2, h);

    // Xs and the oh oh ohs
    for (int i = 0; i < 9; i++) {
      int x = i % 3; // 0, 1, 2, 0, 1, 2, 0, 1, 2
      int y = i / 3; // 0, 0, 0, 1, 1, 1, 2, 2, 2

      float offset = third / 4;
      float leftX = x * third + offset;
      float topY = y * third + offset;
      float rightX = x * third + third - offset;
      float bottomY = y * third + third - offset;

      float[] firstLine = xs[i][0];
      firstLine[0] = leftX;
      firstLine[1] = topY;
      firstLine[2] = rightX;
      firstLine[3] = bottomY;

      float[] secondLine = xs[i][1];
      secondLine[0] = leftX;
      secondLine[1] = bottomY;
      secondLine[2] = rightX;
      secondLine[3] = topY;

      oCenters[i] = new PointF(x * third + sixth, y * third + sixth);
    }

    oRadius = third / 4;
  }

  @Override public boolean onTouchEvent(MotionEvent e) {
    if (e.getAction() != MotionEvent.ACTION_DOWN) {
      // Only handle action_down touch events
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

  public void showWin(boolean[][][] grid, int zIndex) {
    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 3; x++) {
        winSpaces[y * 3 + x] = grid[x][y][zIndex]; // True if space involved in win
      }
    }
    invalidate();
  }

  public void clearWin() {
    winSpaces = new boolean[9];
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
        Paint paint = winSpaces[i] ? rainbowPaint : xPaint;
        float[][] lines = xs[i];
        float[] firstLine = lines[0];
        float[] secondLine = lines[1];
        canvas.drawLine(firstLine[0], firstLine[1], firstLine[2], firstLine[3], paint);
        canvas.drawLine(secondLine[0], secondLine[1], secondLine[2], secondLine[3], paint);
      } else if (mark == 'O') {
        Paint paint = winSpaces[i] ? rainbowPaint : oPaint;
        PointF center = oCenters[i];
        canvas.drawCircle(center.x, center.y, oRadius, paint);
      }
    }
  }
}
