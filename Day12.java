import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Day12 {
	
	public static void main(String[] args) throws Exception {
		BufferedReader r = new BufferedReader(new FileReader("src/input.txt"));
		int [][] moon_pos = new int[4][3];
		int [][] moon_vel = new int[4][3];
		int [][] grav = new int[4][3];
		Map<String, Long> state = new HashMap<String, Long>();
		
		for (int i = 0; i < 4; i++) {
			String[] in = r.readLine().split(",");
			moon_pos[i][0] = Integer.parseInt(in[0]);
			moon_pos[i][1] = Integer.parseInt(in[1]);
			moon_pos[i][2] = Integer.parseInt(in[2]);
			moon_vel[i][0] = moon_vel[i][1] = moon_vel[i][2] = 0;
			grav[i][0] = grav[i][1] = grav[i][2] = 0;
 		}
		
		//print(moon_pos, moon_vel);
		
		r.close();
		
		long steps = 0;	
		while (steps < 1000) {
			for (int m2 = 0; m2 < 4; m2++) 
				for (int g = 0; g < 3; g++) 
					grav[m2][g] = 0;
			for (int m1 = 0; m1 < 4; m1++) {
				for (int m2 = m1 + 1; m2 < 4; m2++) {
					if (moon_pos[m1][0] < moon_pos[m2][0]) {
						grav[m1][0]++;
						grav[m2][0]--;
					} else if (moon_pos[m1][0] > moon_pos[m2][0]) {
						grav[m1][0]--;
						grav[m2][0]++;
					}
					
					if (moon_pos[m1][1] < moon_pos[m2][1]) {
						grav[m1][1]++;
						grav[m2][1]--;
					} else if (moon_pos[m1][1] > moon_pos[m2][1]) {
						grav[m1][1]--;
						grav[m2][1]++;
					}
					
					if (moon_pos[m1][2] < moon_pos[m2][2]) {
						grav[m1][2]++;
						grav[m2][2]--;
					} else if (moon_pos[m1][2] > moon_pos[m2][2]) {
						grav[m1][2]--;
						grav[m2][2]++;
					}
				}
			}
			
			for (int m = 0; m < 4; m++) {
				for (int gv = 0; gv < 3; gv++) {
					moon_vel[m][gv] += grav[m][gv];
				}
			}
			for (int m = 0; m < 4; m++) {
				for (int v = 0; v < 3; v++) {
					moon_pos[m][v] += moon_vel[m][v];
				}
			}
			
			String key = generateKey(moon_pos, moon_vel);
			
			if (state.containsKey(key)) {
				System.out.println(steps - state.get(key));
				return;
			}
			
			state.put(key, steps);
			steps++;
		}
		
		System.out.println(state);
		
		int result = 0;
		
		for (int i = 0; i < 4; i++) {
			int a = 0, b = 0;
			for (int j = 0; j < 3; j++) {
				a += Math.abs(moon_pos[i][j]);
				b += Math.abs(moon_vel[i][j]);
			}
			result += (a*b);
		}
		
		System.out.println(result);
		
	}
	
	public static String generateKey(int[][] pos, int[][] vel) {
		String key = "";
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) 
				key += pos[i][j] + ",";
		}
		return key;
	}
	
	public static void print (int[][] pos, int [][] vel) {
		for (int i = 0; i < 4; i++) {
			System.out.print("pos=<");
			for (int j = 0; j < 3; j++)
				System.out.print((char)('x' + j) + "= " + pos[i][j] + (j != 2 ? ", " : ""));
			System.out.print(">,  vel=<");
			for (int j = 0; j < 3; j++)
				System.out.print((char)('x' + j) + "=" + vel[i][j] + (j != 2 ? ", " : ""));
			System.out.println(">");
		}
	}
}
