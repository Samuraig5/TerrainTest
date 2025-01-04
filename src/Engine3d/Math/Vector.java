package Engine3d.Math;

import Engine3d.Translatable;

public abstract class Vector implements Translatable
{
    private double[] components;

    /**
     * Get the (actual) dimensionality of the vector.
     * @return number of components of the vector.
     */
    public int getDimension() {
        return components.length;
    }

    /**
     * Get the component at index.
     * @param index index of the component.
     * @return double
     */
    public double getValue(int index) {
        if (index < 0 || index >= getDimension()) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        return components[index];
    }

    /**
     * Set the component at index.
     * @param index index of the component.
     * @param value new value of the component.
     */
    public void setComponent(int index, double value) {
        if (index < 0 || index >= getDimension()) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        components[index] = value;
    }

    /**
     * Set multiple components, starting at component 0.
     * @param newComponents new components.
     */
    public void setComponents(double[] newComponents) {
        int maxIndex = Math.min(getDimension(), newComponents.length);
        for (int i = 0; i < maxIndex; i++) {
            setComponent(i, newComponents[i]);
        }
    }

    /**
     * Translates the components of this vector by some deltas starting at component 0.
     * @param deltas component wise change.
     * @return
     */
    public void translate(double[] deltas) {
        int maxIndex = Math.min(getDimension(), deltas.length);
        for (int i = 0; i < maxIndex; i++) {
            components[i] += deltas[i];
        }
    }

    /**
     * @param deltas component wise change
     * @return A translated clone
     */
    public Vector translated(double[] deltas) {
        Vector clone = clone();
        clone.translate(deltas);
        return clone;
    }

    /**
     * Scales the components of this vector by some scalar.
     * @param scalar scalar.
     */
    public void scale(double scalar) {
        for (int i = 0; i < getDimension(); i++) {
            components[i] *= scalar;
        }
    }

    /**
     * @param scalar scalar
     * @return A scaled clone
     */
    public Vector scaled(double scalar) {
        Vector clone = clone();
        clone.scaled(scalar);
        return clone;
    }

    /**
     * Inverts the components of this vector by multiplying them by -1.
     */
    public void invert() {
        scale(-1f);
    }

    /**
     * @return An inverted clone
     */
    public Vector inverted() {
        Vector clone = clone();
        clone.invert();
        return clone;
    }

    /**
     * Calculates the total magnitude (length) of the vector.
     * @return double
     */
    public double magnitude() {
        double sumOfSquares = 0.0;
        for (int i = 0; i < getDimension(); i++) {
            sumOfSquares += Math.pow(getValue(i), 2);
        }
        return Math.sqrt(sumOfSquares);
    }

    /**
     * Normalizes the vector by dividing each component by the vector's magnitude.
     * After this function finishes, the vector's magnitude will be 1.
     */
    public void normalize() {
        scale(1/magnitude());
    }

    /**
     * @return A normalized clone
     */
    public Vector normalized() {
        Vector clone = clone();
        clone.normalize();
        return clone;
    }

    /**
     * Calculates the distance of the points represented by the vectors.
     * @param other the other vector.
     * @return positive distance
     */
    public double distanceTo(Vector other)
    {
        if (getDimension() != other.getDimension()) {
            throw new IllegalArgumentException("Dimension Mismatch!");
        }

        double result = 0;
        for (int i = 0; i < getDimension(); i++) {
            result += Math.pow(2, getValue(i) - other.getValue(i));
        }
        return Math.sqrt(result);
    }

    /**
     * Calculates the dot product of the vector and another vector.
     * The dot product projects one vector onto the other.
     * The magnitude of the projected vector is returned as the result.
     *
     * The dot product is a measure for the "likeness" of two vectors.
     * A larger positive number means the vectors are alike (point in the same general direction).
     * A larger negative number means the vectors are opposing (point away from each other).
     * If the result is 0, the the two vectors are orthogonal to eachother.
     * @param other the other vector.
     * @return a double value of arbitrary size.
     */
    public double dotProduct(Vector other) {
        if (getDimension() != other.getDimension()) {
            throw new IllegalArgumentException("Dimension Mismatch!");
        }
        Vector thisNormalized = this.normalized();
        Vector otherNormalized = other.normalized();

        double result = 0;
        for (int i = 0; i < getDimension(); i++) {
            result += getValue(i) * other.getValue(i);
        }
        return result;
    }

    // Override the toString method for representation
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < getDimension(); i++) {
            sb.append(getValue(i));
            if (i < getDimension() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Returns a copy of this vector.
     * @return copy of vector.
     */
    public abstract Vector clone();
}
