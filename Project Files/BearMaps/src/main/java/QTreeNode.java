/**
 * Created by samrstarks on 4/6/16.
 */
public class QTreeNode implements Comparable<QTreeNode> {
    private Point ul, lr;
    private QTreeNode nw, ne, sw, se;
    private String name;

    public QTreeNode(Point lr, Point ul, int depth, String name) {
        this.lr = lr;
        this.ul = ul;
        this.name = name;

        if (depth <= 7) {
            Point nwul = new Point(this.ul.getLon(), this.ul.getLat());
            Point nwlr = new Point((this.ul.getLon() + this.lr.getLon()) / 2, (this.ul.getLat()
                    + this.lr.getLat()) / 2);
            this.nw = (new QTreeNode(nwlr, nwul, depth + 1, this.name + "1"));

            Point neul = new Point((this.ul.getLon() + this.lr.getLon()) / 2, this.ul.getLat());
            Point nelr = new Point(this.lr.getLon(), (this.ul.getLat() + this.lr.getLat()) / 2);
            this.ne = (new QTreeNode(nelr, neul, depth + 1, this.name + "2"));

            Point swul = new Point(this.ul.getLon(), (this.ul.getLat() + this.lr.getLat()) / 2);
            Point swlr = new Point((this.ul.getLon() + this.lr.getLon()) / 2, this.lr.getLat());
            this.sw = (new QTreeNode(swlr, swul, depth + 1, this.name + "3"));

            Point seul = new Point((this.ul.getLon() + this.lr.getLon()) / 2, (this.ul.getLat()
                    + this.lr.getLat()) / 2);
            Point selr = new Point(this.lr.getLon(), this.lr.getLat());
            this.se = (new QTreeNode(selr, seul, depth + 1, this.name + "4"));
        }
    }

    public Point getUl() {
        return this.ul;
    }

    public Point getLr() {
        return this.lr;
    }

    public QTreeNode getNe() {
        return ne;
    }

    public QTreeNode getNw() {
        return nw;
    }

    public QTreeNode getSw() {
        return sw;
    }

    public QTreeNode getSe() {
        return se;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compareTo(QTreeNode node) {
        if (this.getUl().getLat() > node.getUl().getLat()) {
            return -1;
        } else if (this.getUl().getLat() < node.getUl().getLat()) {
            return 1;
        } else {
            if (this.getUl().getLon() < node.getUl().getLon()) {
                return -1;
            } else if (this.getUl().getLon() > node.getUl().getLon()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
