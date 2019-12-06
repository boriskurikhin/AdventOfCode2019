import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;


public class Day5 {

	static BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
	static int[] input; 
	static final int INPUT_VALUE = 5;
	static int output = 0;
	

	public static void main(String[] args) throws Exception {
		input = Arrays.stream(r.readLine().split(",")).mapToInt(Integer::parseInt).toArray();
		
		int i = 0, length = input.length;
		outer:
		while (i < length) {
			/* Get this stuff sorted out */
			int opCode = input[i] % 100;
			int aMode = input[i] / 100 % 10;
			int bMode = input[i] / 1000 % 10;
			//int cMode = input[i] / 10000 % 10;
			/* We don't care about cMode at all */
			
			switch (opCode) {			
				case 1:
					input[input[i + 3]] = (aMode == 1 ? input[i + 1] : input[input[i + 1]]) + (bMode == 1 ? input[i + 2] : input[input[i + 2]]);
					i += 4;
					break;
				case 2:
					input[input[i + 3]] = (aMode == 1 ? input[i + 1] : input[input[i + 1]]) * (bMode == 1 ? input[i + 2] : input[input[i + 2]]);
					i += 4;
					break;
				case 3: 
					input[input[i+1]] = INPUT_VALUE; /* I think this what it means by provide input */
					i += 2;
					break;
				case 4: 
					output = input[input[i+1]];
					System.out.println("Output = " + output);
					i += 2;
					break;
				case 5:
					if ((aMode == 1) ? (input[i + 1] != 0) : (input[input[i + 1]] != 0)) 
						i = (bMode == 1 ? input[i + 2] : input[input[i + 2]]);
					else i += 3;
					break;
				case 6:
					if ((aMode == 1) ? (input[i + 1] == 0) : (input[input[i + 1]] == 0)) 
						i = (bMode == 1 ? input[i + 2] : input[input[i + 2]]);
					else i += 3;
					break;
				case 7:
					if ((aMode == 1 ? input[i + 1] : input[input[i + 1]]) < (bMode == 1 ? input[i + 2] : input[input[i + 2]]))
						input[input[i + 3]] = 1;
					else input[input[i + 3]] = 0;
					i += 4;
					break;
				case 8:
					if ((aMode == 1 ? input[i + 1] : input[input[i + 1]]) == (bMode == 1 ? input[i + 2] : input[input[i + 2]])) 
						input[input[i + 3]] = 1;
					else input[input[i + 3]] = 0;
					i += 4;
					break;
				case 99:
					break outer;
			}
		}
	}
}
