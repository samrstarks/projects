/**
 * Created by samrstarks on 4/13/16.
 */
public class Rectangle {
    double left;
    double right;
    double top;
    double bottom;

    public Rectangle(Point ul, Point lr) {
        this.left = ul.getLon();
        this.right = lr.getLon();
        this.top = ul.getLat();
        this.bottom = lr.getLat();
    }

    public boolean intersects(Rectangle tile, Rectangle query) {
        if ((query.left <= tile.left && tile.left <= query.right) 
            || (query.left <= tile.right && tile.left <= query.left)) {
            if ((query.bottom <= tile.top && tile.top <= query.top) 
                || (query.bottom <= tile.bottom && tile.bottom <= query.top)) {
                return true;
            }
        }
        if ((query.left >= tile.left && tile.left >= query.right) 
            || (query.left >= tile.right && tile.left >= query.left)) {
            if ((query.bottom >= tile.top && tile.top >= query.top) 
                || (query.bottom >= tile.bottom && tile.bottom >= query.top)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Rectangle{"
                + "left=" + left
                + ", right=" + right
                + ", top=" + top
                + ", bottom=" + bottom
                + '}';
    }
}
