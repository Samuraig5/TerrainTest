package Physics;

import Math.Vector.Vector3D;

public class CollisionMove {
    public Vector3D move1;
    public Vector3D move2;

    /**
     * Generates a CollisionMove consisting of two separate moves. Move1 and Move2. Together, they span a distance equal to move.
     * @param move The original move.
     * @param firstFraction The fraction of the move that should be assigned to move1 (between 0 and 1).
     */
    public CollisionMove(Vector3D move, double firstFraction) {
        firstFraction = Math.max(Math.min(firstFraction, 1), 0); // Clamp fraction between 0 and 1
        move1 = move.scaled(firstFraction); // move1 = move * firstFraction.
        move2 = move.scaled(1-firstFraction); // move2 = move * (1 - firstFraction).
        move2.invert(); // Invert move2 to point away from object1
    }

    public static CollisionMove empty() {
        return new CollisionMove(new Vector3D(0,0,0), 0.5);
    }
}
