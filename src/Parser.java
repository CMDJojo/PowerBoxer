import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public class Parser {
    public static void main(String... args) {
        Set<Point> walls = new HashSet<>();
        Set<Point> sources = new HashSet<>();
        Set<Point> conducting = new HashSet<>();
        Set<Point> isolating = new HashSet<>();
        Set<Point> outlets = new HashSet<>();
        
        Map<Integer, Set<Integer>> wallMap = new HashMap<>();
        int maxX = 0, maxY = 0;
        
        System.out.println("Enter field row by row (and don't skip walls), 'end' when finished");
        System.out.println(" [LEGEND]");
        System.out.println(" s - power source");
        System.out.println(" c - conducting block");
        System.out.println(" i - isolating block");
        System.out.println(" o - power outlet (where the power needs to be, not the wall)");
        System.out.println(" w - wall");
        System.out.println(" if an outlet is superpositioned with, lets say a conducting block, use uppercase C");
        
        Scanner s = new Scanner(System.in);
        int y = 0;
        while (true) {
            String line = s.nextLine();
            if (line.toLowerCase().startsWith("end")) break;
            maxX = Math.max(line.length() - 1, maxX);
            for (int x = 0; x < line.toCharArray().length; x++) {
                Point p = new Point(x, y);
                switch (Character.toLowerCase(line.charAt(x))) {
                    case 's' -> {
                        sources.add(p);
                        conducting.add(p);
                    }
                    case 'c' -> conducting.add(p);
                    case 'i' -> isolating.add(p);
                    case 'o' -> outlets.add(p);
                    case 'w' -> {
                        walls.add(p);
                        wallMap.putIfAbsent(y, new HashSet<>());
                        wallMap.get(y).add(x);
                    }
                }
                if (Character.isUpperCase(line.charAt(x))) outlets.add(p);
            }
            maxY = y++;
        }
        
        Board b = new Board(walls, sources, conducting, isolating, outlets);
        long time = System.currentTimeMillis();
        Optional<Board> sol = Solver.solveNonParallel(b);
        time = System.currentTimeMillis() - time;
        if (sol.isPresent()) {
            Move[] moves = sol.get().getMoves();
            for (Move move : moves) {
                System.out.printf("(%d,%d) -> (%d,%d)%n", move.from().x(), move.from().y(),
                        move.to().x(), move.to().y());
                
                for (int yp = 0; yp <= maxY; yp++) {
                    for (int xp = 0; xp <= maxX; xp++) {
                        if (yp == move.from().y() && xp == move.from().x()) System.out.print('a');
                        else if (yp == move.to().y() && xp == move.to().x()) System.out.print('b');
                        else
                            System.out.print(wallMap.getOrDefault(yp, Collections.emptySet()).contains(xp) ? '#' : ' ');
                    }
                    System.out.println();
                }
                System.out.println();
            }
            System.out.printf("Solution with %d moves found in %,d ms%n", moves.length, time);
        } else {
            System.err.println("No solution found");
        }
    }
}
