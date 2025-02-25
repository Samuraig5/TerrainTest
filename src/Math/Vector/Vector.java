package Math.Vector;

import Engine3d.Translatable;

import java.util.ArrayList;
import java.util.List;

public abstract class Vector implements Translatable
{
    double[] components;

    protected abstract void init();

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

    public double[] getComponents() {
        return components;
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
     * Set multiple components, starting at component 0.
     * @param newComponents new components in form of a vector.
     */
    public void setComponents(Vector newComponents) {
        setComponents(newComponents.getComponents());
    }

    /**
     * Translates the components of this vector by some deltas starting at component 0.
     * @param deltas component wise change.
     */
    public void translate(double[] deltas) {
        int maxIndex = Math.min(getDimension(), deltas.length);
        for (int i = 0; i < maxIndex; i++) {
            components[i] += deltas[i];
        }
    }

    @Override
    public void translate(Vector3D other) {
        double[] deltas = new double[3];
        deltas[0] = other.x();
        deltas[1] = other.y();
        deltas[2] = other.z();
        translate(deltas);
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

    public Vector translated(Vector3D other) {
        Vector clone = clone();
        clone.translate(other);
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

    public void scale(double[] scalar) {
        int maxIndex = Math.min(getDimension(), scalar.length);
        for (int i = 0; i < maxIndex; i++) {
            components[i] *= scalar[i];
        }
    }

    public void scale(Vector scalar) {
        scale(scalar.getComponents());
    }


    /**
     * @param scalar scalar
     * @return A scaled clone
     */
    public Vector scaled(double scalar) {
        Vector clone = clone();
        clone.scale(scalar);
        return clone;
    }

    public Vector scaled(Vector scalars) {
        Vector clone = clone();
        clone.scale(scalars);
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
            result += Math.pow(getValue(i) - other.getValue(i), 2);
        }
        return Math.abs(Math.sqrt(result));
    }

    /**
     * Calculates the dot product of the vector and another vector.
     * The dot product projects one vector onto the other.
     * The magnitude of the projected vector is returned as the result.
     *
     * The dot product is a measure for the "likeness" of two vectors.
     * A larger positive number means the vectors are alike (point in the same general direction).
     * A larger negative number means the vectors are opposing (point away from each other).
     * If the result is 0, the two vectors are orthogonal to each other.
     * @param other the other vector.
     * @return a double value of arbitrary size.
     */
    public double dotProduct(Vector other) {
        if (getDimension() != other.getDimension()) {
            throw new IllegalArgumentException("Dimension Mismatch!");
        }

        double[] c1 = getComponents();
        double[] c2 = other.getComponents();

        double result = 0;
        for (int i = 0; i < getDimension(); i++) {
            result += c1[i] * c2[i];
        }
        return result;
    }

    /**
     * Returns true if the dot product with the other vector is greater than 0.
     * @param other other vector to be tested against.
     * @return true if this.dotProduct(other) > 0.
     */
    public boolean sameDirection (Vector other) {
    return dotProduct(other) > 0;
}

    public boolean isEmpty() {
        for (double val : components) {
            if (val != 0) { return false; }
        }
        return true;
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
