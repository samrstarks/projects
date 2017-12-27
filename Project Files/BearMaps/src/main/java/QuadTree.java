import java.util.ArrayList;

/**
 * Created by samrstarks on 4/6/16.
 */
public class QuadTree {
    public static final double ROOT_ULLAT = 37.892195547244356, ROOT_ULLON = -122.2998046875,
            ROOT_LRLAT = 37.82280243352756, ROOT_LRLON = -122.2119140625;
    private QTreeNode root;

    public QuadTree() {
        int depth = 0;
        Point rootUL = new Point(ROOT_ULLON, ROOT_ULLAT);
        Point rootLR = new Point(ROOT_LRLON, ROOT_LRLAT);
        root = new QTreeNode(rootLR, rootUL, depth, "");
    }

    public QTreeNode getRoot() {
        return root;
    }

    public ArrayList<QTreeNode> traverseDepth(int depth, QuadTree tree) {
        QTreeNode curr = tree.getRoot();
        ArrayList<QTreeNode> list = new ArrayList<>();
        return traverser(depth, curr, list);
    }

    public static ArrayList<QTreeNode> traverser(int depth, 
        QTreeNode curr, ArrayList<QTreeNode> lst) {
        if (depth == 1) {
            lst.add(curr.getNw());
            lst.add(curr.getNe());
            lst.add(curr.getSw());
            lst.add(curr.getSe());
        } else {
            traverser(depth - 1, curr.getNw(), lst);
            traverser(depth - 1, curr.getNe(), lst);
            traverser(depth - 1, curr.getSw(), lst);
            traverser(depth - 1, curr.getSe(), lst);
        }
        return lst;
    }

    @Override
    public String toString() {
        return super.toString();
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

    public ArrayList<QTreeNode> queryIntersections(ArrayList<QTreeNode> depthNodes, 
        Rectangle query) {
        ArrayList<QTreeNode> queryIntersections = new ArrayList<>();
        for (QTreeNode tile : depthNodes) {
            if (tile != null) {
                Rectangle curTile = new Rectangle(tile.getUl(), tile.getLr());
                if (this.intersects(curTile, query)) {
                    queryIntersections.add(tile);
                }
            }
        }
        return queryIntersections;
    }
}
