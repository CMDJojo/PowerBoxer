import java.util.HashSet;
import java.util.Set;

public record Point(int x, int y) {
    boolean isNextTo(Point other) {
        if (other.x == x) {
            int d = other.y - y;
            return d <= 1 && d >= -1;
        }
        if (other.y == y) {
            int d = other.x - x;
            return d <= 1 && d >= -1;
        }
        return false;
    }
    
    Set<Point> getNeighbours() {
        return new HashSet<>(4) {{
            add(new Point(x + 1, y));
            add(new Point(x - 1, y));
            add(new Point(x, y + 1));
            add(new Point(x, y - 1));
        }};
    }
    
    Point left(){
        return new Point(x-1,y);
    }
    
    Point right(){
        return new Point(x+1,y);
    }
    
    Point down(){
        return new Point(x,y+1);
    }
    Point up(){
        return new Point(x,y-1);
    }
}
