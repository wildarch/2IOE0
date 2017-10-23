package nl.tue.c2IOE0.group5.util;

import org.joml.Vector3f;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

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

    /**
     * turns a vector along an angle
     * @param originalVector the vector that has to be turned
     * @param turnVector the vector that is orthogonal to the turned plane
     * @param angle the angle in DEGREES that has to be turned
     * @return
     */
    public static Vector3f rotateVector(Vector3f originalVector, Vector3f turnVector, float angle){
        double x = turnVector.x();
        double y = turnVector.y();
        double z = turnVector.z();
        double angleRad = toRadians(angle);

        double Mcos = (1 - cos(angleRad));
        double[] mat =  new double[]{
                (x * x * Mcos + cos(angleRad)),
                (x * y * Mcos - z * sin(angleRad)),
                (x * z * Mcos + y * sin(angleRad)),
                (y * x * Mcos + z * sin(angleRad)),
                (y * y * Mcos + cos(angleRad)),
                (y * z * Mcos - x * sin(angleRad)),
                (z * x * Mcos - y * sin(angleRad)),
                (z * y * Mcos + x * sin(angleRad)),
                (z * z * Mcos + cos(angleRad))
        };

        return new Vector3f(
                (float) (originalVector.x() * mat[0] + originalVector.y() * mat[1] + originalVector.z() * mat[2]),
                (float) (originalVector.x() * mat[3] + originalVector.y() * mat[4] + originalVector.z() * mat[5]),
                (float) (originalVector.x() * mat[6] + originalVector.y() * mat[7] + originalVector.z() * mat[8])
        );
    }
}
