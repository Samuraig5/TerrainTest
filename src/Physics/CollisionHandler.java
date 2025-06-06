package Physics;

public class CollisionHandler {
    /**
     * Takes two (collidable) objects and, if at least one of them is not static, translates them along the shortest paths
     * to separate them.
     * @param o1 object 1
     * @param o2 object 2
     */
    public static void handleCollision(Collidable o1, Collidable o2) {
        CollisionMove collisionMove = o1.getCollider().handleCollision(o2.getCollider());
        o1.translate(collisionMove.move1);
        o2.translate(collisionMove.move2);
    }
}
