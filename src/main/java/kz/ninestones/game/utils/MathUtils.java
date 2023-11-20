package kz.ninestones.game.utils;

public class MathUtils {

  private MathUtils() {}

  public static double ln(double x) {
    return Math.log10(x) / Math.log10(Math.E);
  }
}
