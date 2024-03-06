import java.util.*;

public class SpaceSearcher {

    private ArrayList<State> frontier; //oi komvoi pou exoyme paragei kai den exoume epekteinei
    private HashSet<State> closedSet; //komvous pou exoume synanthsei
    private int lanternTime;

    SpaceSearcher(int lanternTime) {
        this.frontier = new ArrayList<>();
        this.closedSet = new HashSet<>();
        this.lanternTime = lanternTime;
    }

    public State aStar(State initialState) {

        // step 1: put initial state in the frontier.
        this.frontier.add(initialState);
        // step 2: check for empty frontier.
        while(!this.frontier.isEmpty()) {
            // step 3: get the first node out of the frontier.
            State currentState = this.frontier.remove(0);
            // step 4: if final state, return.
            if (currentState.isFinal())
                return currentState;

            // step 5: if lantern time is not enough for all the members to cross, return null
            if (currentState.getF() > lanternTime) {
                System.out.println("\nNeeded at least or more than: " + currentState.getF() + " minutes.");
                return null;
            }

            // step 6: if the node is not in the closed set, put the children at the frontier.
            // else go to step 2.
            if (!this.closedSet.contains(currentState)) {
                this.closedSet.add(currentState);
                this.frontier.addAll(currentState.getChildren());
                // step 7: sort the frontier based on the f=g+h score to get best as first
                Collections.sort(this.frontier); // sort the frontier to get best as first
            }
        }
        return null;
    }
}
