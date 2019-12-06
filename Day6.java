import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class Day6 {

	public static Map<String, HashSet<String>> map;
	public static Map<String, Integer> dist_santa;
	public static Map<String, Integer> dist_you;

	public static BufferedReader r;
	public static String[] input;
	public static final int INPUT = 1444;
	
	/*
	 * Not the most efficient solution, but it worked pretty quickly given my test-case.
	 * Note that this was written at like 2 a.m on a work night, so my head wasn't in the
	 * "let's get the most optimal solution" mode. There's a bunch of things I can think of
	 * that could be improved upon (like doing part 1 and 2 in one single recursion, or using Memoization for
	 * already visited planets) - but I'm too tired to do that at this point...
	 * 
	 * Either way, Day 6 is done. We got our 2 gold stars. On to the next.
	 */
	
	public static long getCount (String key) {
		/* We need to return direct + indirect */
		if (!map.containsKey(key)) 
			return 0;
		
		long result = 0;
		
		for (String p : map.get(key))
			result += 1 + getCount(p);
		
		return result;
	}
	
	
	//Part 2 - computes distances to each planet
	public static void visit (String key, int path, boolean isSanta) {
		if (isSanta) {
			if (!dist_santa.containsKey(key)) {
				dist_santa.put(key, path);
				if (map.containsKey(key)) {
					for (String v : map.get(key)) {
						visit(v, path + 1, true);
					}
				}
			}
		}
		else {
			if (!dist_you.containsKey(key)) {
				dist_you.put(key, path);
				if (map.containsKey(key)) {
					for (String v : map.get(key)) {
						visit(v, path + 1, false);
					}
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		r = new BufferedReader(new InputStreamReader(System.in));
		map = new HashMap<String, HashSet<String>>();

		for (int i = 0; i < INPUT; i++) {
			input = r.readLine().split("\\)");
			if (!map.containsKey(input[1])) 
				map.put(input[1], new HashSet<String>());
			
			map.get(input[1]).add(input[0]);
		}
		
		/* Part 1 - The recursion */
		long total = 0;
		
		for (String start : map.keySet())
			total += getCount (start);
		
		//Part 1 stuff
		System.out.println(total);
		
		//Part 2 stuff
		dist_santa = new HashMap<String, Integer>();
		dist_you = new HashMap<String, Integer>();
		
		visit("YOU", 0, false);
		visit("SAN", 0, true);
		
		long answer = Integer.MAX_VALUE;
		
		for (String visitedPlanet : dist_santa.keySet()) {
			if (dist_you.containsKey(visitedPlanet)) {
				answer = Math.min(dist_santa.get(visitedPlanet) + dist_you.get(visitedPlanet), answer);
			}
		}
		System.out.println(answer - 2);
	}
}
