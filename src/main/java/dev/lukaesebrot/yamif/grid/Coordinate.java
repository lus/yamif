package dev.lukaesebrot.yamif.grid;

/**
 * Represents a coordinate
 *
 * @author Lukas Schulte Pelkum
 * @version 1.0.0
 * @since 1.0.0
 */
public class Coordinate {

    // Define the x and y values of the coordinate
    private final int x;
    private final int y;

    /**
     * Creates a new coordinate
     *
     * @param x The x value of the coordinate
     * @param y The y value of the coordinate
     */
    public Coordinate(final int x, final int y) {
        // Validate the x value
        if (x < 0 || x > 8) {
            throw new IllegalArgumentException("x has to be in the interval [0, 8]");
        }

        // Validate the y value
        if (y < 0 || y > 5) {
            throw new IllegalArgumentException("y has to be in the interval [0, 5]");
        }

        // Set the x and y values
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a new coordinate using a slot number
     *
     * @param slot The slot number
     * @return The created coordinate
     */
    public static Coordinate fromSlot(final int slot) {
        // Validate the slot number
        if (slot < 0 || slot > 53) {
            throw new IllegalArgumentException("slot has to be in the interval [0, 53]");
        }

        // Calculate and create the coordinate
        // REMINDER: Integer division!
        final int x = slot - (slot / 9 * 9);
        final int y = slot / 9;
        return new Coordinate(x, y);
    }

    /**
     * @return The x value of the coordinate
     */
    public int getX() {
        return this.x;
    }

    /**
     * @return The y value of the coordinate
     */
    public int getY() {
        return this.y;
    }

    /**
     * @return The calculated slot of the coordinate
     */
    public int getSlot() {
        return this.y * 9 + this.x;
    }

}
