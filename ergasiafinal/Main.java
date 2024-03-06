import java.util.*;

public class Main {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

	    System.out.println("\n------ RIVER CROSSING PROBLEM ------");

        System.out.print("\nPlease enter number of family members: ");
        int members = in.nextInt();

        Set<Integer> uniqueCrossingTimes = new HashSet<>();

        int[] crossingTimes = new int[members];
        for (int i = 0; i < members; i++) {
            System.out.print("Enter the crossing time for member " + (i + 1) + ": ");
            int crossingTime = in.nextInt();

            // Check if the entered number for member's crossing time is unique
            while (uniqueCrossingTimes.contains(crossingTime)) {
                System.out.println("This crossing time has already been entered. Please enter a unique crossing time.");
                System.out.print("Enter the crossing time for member " + (i + 1) + ": ");
                crossingTime = in.nextInt();
            }

            crossingTimes[i] = crossingTime;
            uniqueCrossingTimes.add(crossingTime);
        }

        System.out.print("Enter the lantern time: ");
        int lanternTime = in.nextInt();

        in.close();

        State initialState = new State(members, crossingTimes);
        System.out.println("\n\n---------- INITIAL STATE ----------");
        initialState.print();
        SpaceSearcher searcher = new SpaceSearcher(lanternTime);
        long start = System.currentTimeMillis();
        State terminalState = searcher.aStar(initialState);
        long end = System.currentTimeMillis();

        if (terminalState == null)
            System.out.println("Could not find a solution.");
        else {
            // print the path from start to end.
            State temp = terminalState; // begin from the end.
            ArrayList<State> path = new ArrayList<>();
            path.add(terminalState);
            while (temp.getFather() != null) { // if father is null, then we are at the root.
                path.add(temp.getFather());
                temp = temp.getFather();
            }
            // reverse the path and print.
            System.out.println("\n\n------ RIVER CROSSING SOLUTION ------");
            Collections.reverse(path);

            for (int i = 1; i < path.size(); i++) {
                State item = path.get(i);
                System.out.println("\nStep " + i + ":");
                item.print();
            }

            System.out.println("\n\n------------ SUMMARY ------------");
            System.out.println("\nTotal time required: " + terminalState.getTotalTime() + " minutes.");
            System.out.println("Time left: " + (lanternTime - terminalState.getTotalTime()) + " minutes.");
            System.out.println("Finished in " + (path.size() - 1) + " steps.");
            System.out.println("A* algorithm search time: " + (double) (end - start) / 1000 + " seconds.");  // total time of searching in seconds.
        }
    }
}
