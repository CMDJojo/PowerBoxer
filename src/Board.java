import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

public class Board {
    private final Set<Point> walls;
    private final Set<Point> sources;
    private final Set<Point> conducting; //including sources
    private final Set<Point> isolating;
    private final Set<Point> outlets;
    private final Set<Board> exploredBoards;
    private final TraceLink<Move> moves;
    private final int BOTTOM_LIMIT = 30;
    
    public Board(Set<Point> walls, Set<Point> sources, Set<Point> conducting, Set<Point> isolating,
                 Set<Point> outlets) {
        this(walls, sources, conducting, isolating, outlets, new HashSet<>(), null);
    }
    
    public Board(Set<Point> walls, Set<Point> sources, Set<Point> conducting, Set<Point> isolating,
                 Set<Point> outlets, Set<Board> exploredBoards, TraceLink<Move> moves) {
        this.walls = walls;
        this.sources = sources;
        this.conducting = conducting;
        this.isolating = isolating;
        this.outlets = outlets;
        this.exploredBoards = exploredBoards;
        this.moves = moves;
    }
    
    private Set<Point> conduct(Point source) {
        Set<Point> ret = new HashSet<>();
        Set<Point> toAdd = new HashSet<>();
        Set<Point> toAdd2 = new HashSet<>();
        toAdd.add(source);
        do {
            toAdd.forEach(p -> toAdd2.addAll(p.getNeighbours()));
            toAdd2.retainAll(conducting);
            ret.addAll(toAdd);
            toAdd2.removeAll(ret);
            toAdd.clear();
            toAdd.addAll(toAdd2);
            toAdd2.clear();
        } while (toAdd.size() > 0);
        return ret;
    }
    
    private void compile() {
        Set<Point> superconducting = new HashSet<>();
        for (Point source : sources) {
            if(superconducting.contains(source)) continue;
            Set<Point> conducts = conduct(source);
            for (Point point : sources) {
                if (point != source && conducts.contains(point)) {
                    superconducting.add(point);
                    superconducting.add(source);
                }
            }
        }
        sources.removeAll(superconducting);
    }
    
    Set<Board> step() {
        Set<Board> ret = new HashSet<>();
        
        outer:
        for (Point src : conducting) {
            Point dest = src.left();
            if (walls.contains(dest) || conducting.contains(dest) || isolating.contains(dest)) continue;
            
            while (!(walls.contains(dest) || conducting.contains(dest) || isolating.contains(dest))) {
                dest = dest.down();
                if (dest.y() > BOTTOM_LIMIT) continue outer;
            }
            dest = dest.up();
            
            Set<Point> newSources = new HashSet<>(sources);
            if (newSources.remove(src)) newSources.add(dest);
            
            Set<Point> newConducting = new HashSet<>(conducting);
            newConducting.remove(src);
            newConducting.add(dest);
            
            Set<Point> newIsolating = new HashSet<>(isolating);
            
            //move everything above down
            Point hole = src;
            Point aboveHole = src.up();
            while (newConducting.contains(aboveHole) || newIsolating.contains(aboveHole)) {
                if (newConducting.remove(aboveHole)) newConducting.add(hole);
                if (newIsolating.remove(aboveHole)) newIsolating.add(hole);
                if (newSources.remove(aboveHole)) newSources.add(hole);
                hole = hole.up();
                aboveHole = aboveHole.up();
            }
            
            //create new object
            Board newBoard = new Board(walls, newSources, newConducting, newIsolating, outlets,
                    exploredBoards, new TraceLink<>(moves, new Move(src, dest)));
            newBoard.compile();
            if (!newBoard.sources.isEmpty() && exploredBoards.add(newBoard)) ret.add(newBoard);
        }
        
        
        outer:
        for (Point src : conducting) {
            Point dest = src.right();
            if (walls.contains(dest) || conducting.contains(dest) || isolating.contains(dest)) continue;
            
            while (!(walls.contains(dest) || conducting.contains(dest) || isolating.contains(dest))) {
                dest = dest.down();
                if (dest.y() > BOTTOM_LIMIT) continue outer;
            }
            
            dest = dest.up();
            
            Set<Point> newSources = new HashSet<>(sources);
            if (newSources.remove(src)) newSources.add(dest);
            
            Set<Point> newConducting = new HashSet<>(conducting);
            newConducting.remove(src);
            newConducting.add(dest);
            
            Set<Point> newIsolating = new HashSet<>(isolating);
            
            //move everything above down
            Point hole = src;
            Point aboveHole = src.up();
            while (newConducting.contains(aboveHole) || newIsolating.contains(aboveHole)) {
                if (newConducting.remove(aboveHole)) newConducting.add(hole);
                if (newIsolating.remove(aboveHole)) newIsolating.add(hole);
                if (newSources.remove(aboveHole)) newSources.add(hole);
                hole = hole.up();
                aboveHole = aboveHole.up();
            }
            
            //create new object
            Board newBoard = new Board(walls, newSources, newConducting, newIsolating, outlets,
                    exploredBoards, new TraceLink<>(moves, new Move(src, dest)));
            newBoard.compile();
            if (!newBoard.sources.isEmpty() && exploredBoards.add(newBoard)) ret.add(newBoard);
        }
        
        outer:
        for (Point src : isolating) {
            Point dest = src.left();
            if (walls.contains(dest) || conducting.contains(dest) || isolating.contains(dest)) continue;
            
            while (!(walls.contains(dest) || conducting.contains(dest) || isolating.contains(dest))) {
                dest = dest.down();
                if (dest.y() > BOTTOM_LIMIT) continue outer;
            }
            dest = dest.up();
            
            Set<Point> newSources = new HashSet<>(sources);
            Set<Point> newConducting = new HashSet<>(conducting);
            Set<Point> newIsolating = new HashSet<>(isolating);
            newIsolating.remove(src);
            newIsolating.add(dest);
            
            //move everything above down
            Point hole = src;
            Point aboveHole = src.up();
            while (newConducting.contains(aboveHole) || newIsolating.contains(aboveHole)) {
                if (newConducting.remove(aboveHole)) newConducting.add(hole);
                if (newIsolating.remove(aboveHole)) newIsolating.add(hole);
                if (newSources.remove(aboveHole)) newSources.add(hole);
                hole = hole.up();
                aboveHole = aboveHole.up();
            }
            
            //create new object
            Board newBoard = new Board(walls, newSources, newConducting, newIsolating, outlets,
                    exploredBoards, new TraceLink<>(moves, new Move(src, dest)));
            newBoard.compile();
            if (!newBoard.sources.isEmpty() && exploredBoards.add(newBoard)) ret.add(newBoard);
        }
        
        outer:
        for (Point src : isolating) {
            Point dest = src.right();
            if (walls.contains(dest) || conducting.contains(dest) || isolating.contains(dest)) continue;
            
            while (!(walls.contains(dest) || conducting.contains(dest) || isolating.contains(dest))) {
                dest = dest.down();
                if (dest.y() > BOTTOM_LIMIT) continue outer;
            }
            dest = dest.up();
            
            Set<Point> newSources = new HashSet<>(sources);
            Set<Point> newConducting = new HashSet<>(conducting);
            Set<Point> newIsolating = new HashSet<>(isolating);
            newIsolating.remove(src);
            newIsolating.add(dest);
            
            //move everything above down
            Point hole = src;
            Point aboveHole = src.up();
            while (newConducting.contains(aboveHole) || newIsolating.contains(aboveHole)) {
                if (newConducting.remove(aboveHole)) newConducting.add(hole);
                if (newIsolating.remove(aboveHole)) newIsolating.add(hole);
                if (newSources.remove(aboveHole)) newSources.add(hole);
                hole = hole.up();
                aboveHole = aboveHole.up();
            }
            
            //create new object
            Board newBoard = new Board(walls, newSources, newConducting, newIsolating, outlets,
                    exploredBoards, new TraceLink<>(moves, new Move(src, dest)));
            newBoard.compile();
            if (!newBoard.sources.isEmpty() && exploredBoards.add(newBoard)) ret.add(newBoard);
        }
        return ret;
    }
    
    Move[] getMoves() {
        return moves.toArray();
    }
    
    boolean hasWon() {
        Set<Point> allConducting = new HashSet<>();
        sources.forEach(p -> allConducting.addAll(conduct(p)));
        return allConducting.containsAll(outlets);
    }
    
    public String toString(){
        return toStringSlow();
    }
    
    String toStringSlow(){
        Map<Integer, Map<Integer, Character>> rows = new HashMap<>();
        int maxX = 0, maxY = 0;
        for (Point wall : walls) {
            rows.putIfAbsent(wall.y(), new HashMap<>());
            rows.get(wall.y()).put(wall.x(), 'w');
            maxX = Math.max(maxX, wall.x());
            maxY = Math.max(maxY, wall.y());
        }
        for (Point p : outlets) {
            rows.putIfAbsent(p.y(), new HashMap<>());
            rows.get(p.y()).put(p.x(), 'o');
            maxX = Math.max(maxX, p.x());
            maxY = Math.max(maxY, p.y());
        }
    
        for (Point p : conducting) {
            rows.putIfAbsent(p.y(), new HashMap<>());
            rows.get(p.y()).put(p.x(), 'c');
            maxX = Math.max(maxX, p.x());
            maxY = Math.max(maxY, p.y());
        }
        for (Point p : sources) {
            rows.putIfAbsent(p.y(), new HashMap<>());
            rows.get(p.y()).put(p.x(), 's');
            maxX = Math.max(maxX, p.x());
            maxY = Math.max(maxY, p.y());
        }
        for (Point p : isolating) {
            rows.putIfAbsent(p.y(), new HashMap<>());
            rows.get(p.y()).put(p.x(), 'i');
            maxX = Math.max(maxX, p.x());
            maxY = Math.max(maxY, p.y());
        }
        StringBuilder sb = new StringBuilder(maxY*maxX);
        for (int y = 0; y <= maxY; y++) {
            if(sb.length() > 0) sb.append('\n');
            for (int x = 0; x <= maxX; x++) {
                sb.append(rows.getOrDefault(y, Collections.emptyMap()).getOrDefault(x, ' '));
            }
        }
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Board board = (Board) o;
        
        if (!sources.equals(board.sources)) return false;
        if (!conducting.equals(board.conducting)) return false;
        return isolating.equals(board.isolating);
    }
    
    @Override
    public int hashCode() {
        int result = sources.hashCode();
        result = 31 * result + conducting.hashCode();
        result = 31 * result + isolating.hashCode();
        return result;
    }
}
