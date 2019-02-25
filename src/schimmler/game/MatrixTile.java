package schimmler.game;

import schimmler.architecture.Tile;

public abstract class MatrixTile extends Tile {
    public abstract int[][] getGeometryMatrix();

    public MatrixTile(int x, int y) {
        super(x, y);
    }

    private boolean isInMatrix(int cx, int cy) {
        int[][] geometry = getGeometryMatrix();

        if (geometry == null || geometry[0] == null)
            throw new IllegalStateException("MatrixTile is used with no geometry set.");

        int yRel = cy - y;
        int xRel = cx - x;

        if (xRel < 0 || yRel < 0)
            return false;

        if (xRel > geometry[0].length - 1)
            return false;

        if (yRel > geometry.length - 1)
            return false;

        return geometry[yRel][xRel] == 1;
    }

    @Override
    public boolean fieldOccupied(int cx, int cy) {
        return isInMatrix(cx, cy);
    }

}