package me.mattlogan.twentyseven;

public enum Plane {
  FRONT(0),
  MIDDLE(1),
  BACK(2);

  private final int zValue;

  Plane(int zValue) {
    this.zValue = zValue;
  }

  public int zValue() {
    return zValue;
  }

  public String toDisplayString() {
    String uppercase = this.toString();
    return uppercase.charAt(0) + uppercase.substring(1).toLowerCase();
  }
}
