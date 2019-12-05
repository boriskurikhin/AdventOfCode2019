import java.util.Arrays;

public class Day4 {
	
	/*
	 * When I began my solution, I lightly skimmed over the problem statement
	 * without pausing and thinking about it - at all. I thought I had it all figured out
	 * and then spent a really long time writing out really complicated logic. After about an hour of
	 * re-submitting an incorrect solution, I couldn't understand where I was going wrong.
	 * 
	 * ...I kept getting 606!!! 
	 * 
	 * Finally I re-read the problem statement, and noticed this line in one of the Part-2 examples, 
	 * > "111122 meets the criteria (even though 1 is repeated more than twice, it still contains a double 22)"
	 * 
	 * And then I came to a big realization, I've over-complicated the problem, by a mile. The complicated logic was 
	 * completely useless and solved a different problem altogether. I realized that since the order is non-decreasing,
	 * duplicate numbers can only show up next to each other. That means we can literally
	 * count the occurrence of each number and check whether or not there exists at least 1 pair.
	 * 
	 * And that's it. Awesome challenge. I learned my lesson!
	 */
	
	
	public static boolean check (int n) {
		String str = n + "";
		//decreasing condition
		for (int i = 1; i < str.length(); i++) 
			if (str.charAt(i) < str.charAt(i - 1)) 
				return false;
		int count[] = new int[10]; Arrays.fill(count, 0);
		for (int i = 0; i < str.length(); i++) count[str.charAt(i) - '0']++;
		for (int i = 0; i < 10; i++) if (count[i] == 2) return true;
		return false;
	}
	
    public static void main(String[] args) throws Exception {    	
    		int low = 245318; //start
    		int high = 765747; //finish
    		int answer = 0;
    		
    		for (; low <= high; low++)
    			if (check(low)) ++answer;
    		    		
    		System.out.println(answer);
    }
   
}
