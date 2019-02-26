package schimmler.game;

import schimmler.architecture.Tile;

public abstract class MatrixTile extends Tile {

    /**
     * should return the desired geometry of this tile
     */
    public abstract int[][] getGeometryMatrix();

    /**
     * Create a new tile with given coordinates.
     * 
     * @param x the x position.
     * @param y the y position.
     */
    public MatrixTile(int x, int y) {
        super(x, y);
    }

    /**
     * checks whether an coordinate is inside the geometry of a tile, and if so if
     * this coordinate is occupied
     * 
     * @param cx the x position to check.
     * @param cy the y position to check.
     * @return whether this tile is at the given position.
     */
    private boolean isInMatrix(int cx, int cy) {
        int[][] geometry = getGeometryMatrix();

        // check of geometry is properly set
        if (geometry == null || geometry[0] == null)
            throw new IllegalStateException("MatrixTile is used with no geometry set.");

        // caluculate coordinates relative to tile
        int yRel = cy - y;
        int xRel = cx - x;

        // check bounding box
        if (xRel < 0 || yRel < 0)
            return false;

        if (xRel > geometry[0].length - 1)
            return false;

        if (yRel > geometry.length - 1)
            return false;

        // check matrix itself
        return geometry[yRel][xRel] == 1;
    }

    @Override
    public boolean fieldOccupied(int cx, int cy) {
        return isInMatrix(cx, cy);
    }

}