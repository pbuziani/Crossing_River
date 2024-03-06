import java.util.*;

public class State implements Comparable<State> {

	private int f, h, g;
	private State father;
	private int totalTime;
	private int members; 				// number of family members
	private int[] membersCrossingTime; 	// family's members crossing times
	private int[] membersSide;   		// family's members sides (1->Right, 0->Left)
	private String lantern; 			// lantern side (starts right)


	// CONSTRUCTORS

	// Constructor for user inputs
	public State(int members, int[] membersCrossingTime) {
		this.g = 0;
		this.father = null;
		this.totalTime = 0;
		this.lantern = "right"; 	 	// initially located on the right side
		this.members = members;

		this.membersCrossingTime = new int[this.members];
        System.arraycopy(membersCrossingTime, 0, this.membersCrossingTime, 0, this.members);

		this.membersSide = new int[this.members];
		for (int i = 0; i < this.members; i++)
			this.membersSide[i] = 1;

		this.heuristic();
		this.f = this.h;
	}

	// Copy constructor
	public State(State s) {
		this.f = s.f;
		this.h = s.h;
		this.g = s.g;
		this.father = s.father;
		this.totalTime = s.totalTime;
		this.members = s.members;
		this.lantern = s.lantern;
		this.membersCrossingTime = Arrays.copyOf(s.membersCrossingTime, s.membersCrossingTime.length);
		this.membersSide = Arrays.copyOf(s.membersSide, s.membersSide.length);
	}


	// GETTERS - SETTERS

	public int getF() {
		return this.f;
	}

	public int getG() {
		return this.g;
	}

	public int getH() {
		return this.h;
	}

	public State getFather() {
		return this.father;
	}

	public int getTotalTime() {
		return this.totalTime;
	}

	public void setF(int f) {
		this.f = f;
	}

	public void setG(int g) {
		this.g = g;
	}

	public void setH(int h) {
		this.h = h;
	}

	public void setFather(State f) {
		this.father = f;
	}

	public void setTotalTime(int time) {
		this.totalTime = time;
	}


	// METHODS

	/**
	 * Updates the 'f' attribute by combining the cost from the start state (g) and the estimated
	 * cost to the goal state (h) using the evaluation function f = g + h.
	 */
	public void evaluate() {
		this.f = this.g + this.h;
	}


	/**
	 * Moves two family members from the right to the left side of the river, updating
	 * member's sides, lantern side and total time.
	 *
	 * @param firstMember Index of the first family member to move.
	 * @param secondMember Index of the second family member to move.
	 */
	public void moveLeft(int firstMember, int secondMember) {
		this.membersSide[firstMember] = 0;
		this.membersSide[secondMember] = 0;
		this.lantern = "left";
        this.totalTime += Math.max(this.membersCrossingTime[firstMember], this.membersCrossingTime[secondMember]);
	}


	/**
	 * Moves one family member from the left side to the right side of the river, updating
	 * member side, lantern side and total time.
	 *
	 * @param member Index of the family member to move.
	 */
	public void moveRight(int member) {
		this.membersSide[member] = 1;
		this.lantern = "right";
		this.totalTime += this.membersCrossingTime[member];
	}


	/**
	 * Calculates the heuristic value for the current state, guiding the A* algorithm in cost estimation.
	 * Heuristic idea: Remove the 2-member trunk restriction, allowing more than 2 members to fit during the crossing.
	 * If the lantern is on the right side, the heuristic is the maximum time among family members on the right.
	 * If the lantern is on the left side, the heuristic is the sum of the minimum time among family members on
	 * the left and the maximum time among family members on the right.
	 */
	public void heuristic() {
		int minTime = Integer.MAX_VALUE;
		int maxTime = Integer.MIN_VALUE;

		// Check if the current state is the final state.
		if (isFinal())
			this.h = 0; // h=0 if it's the final state.
		else {
			// If the lantern is on the right side:
			if (this.lantern.equals("right")) {
				for (int i = 0; i < this.members; i++) {
					if (this.membersSide[i] == 1)
						// Keep track of the maximum time among members on the right.
						maxTime = Math.max(maxTime, this.membersCrossingTime[i]);
				}
				this.h = maxTime;
			}
			// If the lantern is on the left side:
			else if (this.lantern.equals("left")) {
				for (int i = 0; i < this.members; i++) {
					if (this.membersSide[i] == 0)
						// Keep track of the minimum time among members on the left.
						minTime = Math.min(minTime, this.membersCrossingTime[i]);
				}

				for (int i = 0; i < this.members; i++) {
					if (this.membersSide[i] == 1)
						// Keep track of the maximum time among members on the right.
						maxTime = Math.max(maxTime, this.membersCrossingTime[i]);
				}
				this.h = minTime + maxTime;
			}
		}
	}


	/**
	 * Generates and returns a list of valid child states for the current state by moving family
	 * members across the river.
	 *
	 * @return ArrayList of child states based on the current state's lantern side and family member positions.
	 */
	public ArrayList<State> getChildren() {
		ArrayList<State> children = new ArrayList<>();

		if (this.lantern.equals("right")) {
			for (int i = 0; i < this.members; i++) {
				for (int j = i + 1; j < this.members; j++) {
					if (this.membersSide[i] == 1 && this.membersSide[j] == 1) {
						State child = new State(this);     // Create a new state using the copy constructor to generate children.
						child.setFather(this);                // Set the previous state as parent state.
						child.moveLeft(i, j);
						child.g = child.g + Math.max(this.membersCrossingTime[i], this.membersCrossingTime[j]);   // Update g.
						child.heuristic();                	  // Calculate the heuristic value and update h.
						child.evaluate();                     // Update f = g + h.
						children.add(child);                  // Add the new state to the list of children.
					}
				}
			}
		} else if (this.lantern.equals("left")) {
			for (int i = 0; i < this.members; i++) {
				if (this.membersSide[i] == 0) {
					State child = new State(this); 		 // Create a new state using the copy constructor to generate children.
					child.setFather(this);  				 // Set the previous state as parent state.
					child.moveRight(i);
					child.g = child.g + this.membersCrossingTime[i];  // Update g.
					child.heuristic();		     		 	 // Calculate the heuristic value and update h.
					child.evaluate();						 // Update f = g + h.
					children.add(child);					 // Add the new state to the list of children.
				}
			}
		}
		return children;
	}


	/**
	 * Checks if all family members are on the left side, indicating a final state.
	 *
	 * @return true if the state is final, false otherwise.
	 */
	public boolean isFinal() {
		for(int i = 0; i < this.members; i++) {
			if(this.membersSide[i] == 1)
				return false; // if the condition == 1 that means a member is on the right side which proves the state isn't final.
		}
		return true;
	}


	/**
	 * Prints the state, displaying member times on left and right side, lantern side,
	 * elapsed time, and the evaluation function (f = g + h).
	 */
	public void print() {
		System.out.print("Members on the left side: ");
		for (int i = 0; i < this.members; i++) {
			if (this.membersSide[i] == 0)
				System.out.print(this.membersCrossingTime[i] + " ");
		}

		System.out.print("\nMembers on the right side: ");
		for (int i = 0; i < this.members; i++) {
			if (this.membersSide[i] == 1)
				System.out.print(this.membersCrossingTime[i] + " ");
		}

		System.out.println("\nLantern's side: " + this.lantern);
		System.out.println("Elapsed time: " + this.totalTime + " minutes");
		System.out.println("f(n)=" + this.g + "+" + this.h + "=" + this.f);
	}


	/**
	 * Checks if two State objects are equal based on 'lantern', 'membersCrossingTime' and 'membersSide' attributes.
	 *
	 * @param obj The state to compare for equality.
	 * @return true if the states are equal, false otherwise.
	 */
	@Override
	public boolean equals(Object obj) {

		// If the objects are the same, they are equal.
		if (this == obj)
			return true;

		// If the object is null or of a different class, they are not equal
		if (obj == null || getClass() != obj.getClass())
			return false;

		// Compare the 'lantern' attribute
		if (!this.lantern.equals(((State) obj).lantern))
			return false;

		// Compare the 'membersCrossingTime' attribute
		if (!Arrays.equals(this.membersCrossingTime,((State) obj).membersCrossingTime))
			return false;

		// Compare the 'membersSide' attribute
		if (!Arrays.equals(this.membersSide,((State) obj).membersSide))
			return false;

		return true;  // If all attributes are equal, the states are equal.
	}


	/**
	 * Generates a unique hash code for each State object based on its attributes.
	 * Is used for the closed set in the A* algorithm.
	 *
	 * @return The hash code for the State object.
	 */
	@Override
	public int hashCode() {
		int result = 3;
		for (int i = 0; i < this.members; i++) {
			result = 31 * result + this.membersCrossingTime[i];
			result = 31 * result + this.membersSide[i];
		}
		result = 31 * result + (this.lantern.equals("right") ? 1 : 0);
		return result;
	}


	/**
	 * Compares two State objects based on their f value
	 * Is used to sort the collection of State objects in the A* algorithm.
	 *
	 * @param s The State object to compare with.
	 * @return A negative integer, zero, or a positive integer as this State is less than,
	 *         equal to, or greater than the specified State in terms of their f values.
	 */
	@Override
	public int compareTo(State s) {
		return Double.compare(this.f, s.f);
	}

}
