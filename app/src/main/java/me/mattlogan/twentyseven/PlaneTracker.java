package me.mattlogan.twentyseven;

import timber.log.Timber;

/**
 * Informs the rest of the application which plane this instance of the app represents in the
 * 3 dimensional tic tac toe space -- front, middle, or back
 */
public class PlaneTracker {

  private Plane plane;

  public void updatePlane(Plane plane) {
    Timber.d("updatePlane: %s", plane);
    this.plane = plane;
  }

  public Plane currentPlane() {
    return plane;
  }
}
