package nl.tue.c2IOE0.group5.util;

/**
 * @author Jorren Hendriks
 */
public class Angle {

    /**
     * Convert degrees to radians.
     *
     * @param deg A floating point number in degrees.
     * @return A floating point number in radians.
     */
    public static float radf(float deg) {
        return (float) Math.toRadians(deg);
    }

    /**
     * Convert radians to degrees.
     *
     * @param rad A floating point number in radians.
     * @return A floating point number in degree.
     */
    public static float degf(float rad) {
        return (float) Math.toDegrees(rad);
    }

    /**
     * Convert degrees to radians.
     *
     * @param deg A double in degrees.
     * @return A double in radians.
     */
    public static double radd(double deg) {
        return Math.toRadians(deg);
    }

    /**
     * Convert radians to degrees.
     *
     * @param rad A double in radians.
     * @return A double in degree.
     */
    public static double degd(double rad) {
        return Math.toDegrees(rad);
    }
}
