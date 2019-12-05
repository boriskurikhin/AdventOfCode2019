import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Day4 {
	static BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
	static int[] input, inputCopy; 
	public static void main(String[] args) throws Exception {
		inputCopy = Arrays.stream(r.readLine().split(",")).mapToInt(Integer::parseInt).toArray();
		
		for (int x = 0; x < 99; x++) {
			for (int y = 0; y < 99; y++) {
				
				input = Arrays.copyOf(inputCopy, inputCopy.length);
				
				input[1] = x; //noun
				input[2] = y; //verb
				
				int i = 0, length = input.length;
				outer:
				while (i < length) {
					switch (input[i]) {
						case 1:
							input[input[i + 3]] = input[input[i + 1]] + input[input[i + 2]];
							i += 4;
							break;
						case 2:
							input[input[i + 3]] = input[input[i + 1]] * input[input[i + 2]];
							i += 4;
							break;
						case 99:
							break outer;
					}
				}
								
				if (input[0] == 19690720) {
					System.out.println(100 * x + y);
					return;
				}
			}
		}

	}
}
