import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


class Computer {
	long input[], INPUT_VALUE_1, output;
	int i; /* Program Pointer */
	boolean isHalted, firstInput;
	List<Long> outputArr;
	Computer send;
	
	public Computer(long input[], long INPUT_VALUE_1) {
		this.input = input;
		this.INPUT_VALUE_1 = INPUT_VALUE_1;
		this.isHalted = false;
		this.i = 0; /* IP -> instruction pointer */
		this.outputArr = new ArrayList<Long>();
		this.firstInput = false;
	}
	
	public void setup(Computer send) {
		this.send = send;
	}
	
	public long run(long INPUT_VALUE_2) {
		int length = input.length;
		outer:
		while (i < length) {
			/* Get this stuff sorted out */
			int opCode = (int) input[i] % 100;
			long aMode = input[i] / 100 % 10;
			long bMode = input[i] / 1000 % 10;
			//int cMode = input[i] / 10000 % 10;
			/* We don't care about cMode at all */
			
			switch (opCode) {			
				case 1:
					input[(int)input[i + 3]] = (aMode == 1 ? input[i + 1] : input[(int)input[i + 1]]) + (bMode == 1 ? input[i + 2] : input[(int)input[i + 2]]);
					i += 4;
					break;
				case 2:
					input[(int)input[i + 3]] = (aMode == 1 ? input[i + 1] : input[(int)input[i + 1]]) * (bMode == 1 ? input[i + 2] : input[(int)input[i + 2]]);
					i += 4;
					break;
				case 3: 
					if (!firstInput) {
						input[(int)input[i+1]] = INPUT_VALUE_1; /* I think this what it means by provide input */
						firstInput = true;
					} else {
						input[(int)input[i+1]] = INPUT_VALUE_2;
					}
					i += 2;
					break;
				case 4: 
					output = input[(int)input[i+1]];
					/* We need to send it to the next amplifier */
					i += 2;
					break outer;
				case 5:
					if ((aMode == 1) ? (input[i + 1] != 0) : (input[(int)input[i + 1]] != 0)) 
						i = (int) (bMode == 1 ? input[i + 2] : input[(int)input[i + 2]]);
					else i += 3;
					break;
				case 6:
					if ((aMode == 1) ? (input[i + 1] == 0) : (input[(int)input[i + 1]] == 0)) 
						i = (int) (bMode == 1 ? input[i + 2] : input[(int)input[i + 2]]);
					else i += 3;
					break;
				case 7:
					if ((aMode == 1 ? input[i + 1] : input[(int)input[i + 1]]) < (bMode == 1 ? input[i + 2] : input[(int)input[i + 2]]))
						input[(int)input[i + 3]] = 1;
					else input[(int)input[i + 3]] = 0;
					i += 4;
					break;
				case 8:
					if ((aMode == 1 ? input[i + 1] : input[(int)input[i + 1]]) == (bMode == 1 ? input[i + 2] : input[(int)input[i + 2]])) 
						input[(int)input[i + 3]] = 1;
					else input[(int)input[i + 3]] = 0;
					i += 4;
					break;
				case 99:
					this.isHalted = true;
					break;
			}
		}
		return output;
	}
}


public class Day7 {

	static BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
	static long[] input; 
	static Computer[] amplifiers;
	static long bestValue = Long.MIN_VALUE;
	

	public static void main(String[] args) throws Exception {
		input = Arrays.stream(r.readLine().split(",")).mapToLong(Long::parseLong).toArray();
		trySettings(new ArrayList<Integer>());
		System.out.println(bestValue);
	}
		
	//Works for part 1
	public static void trySettings (List<Integer> setting) {
		if (setting.size() == 5) {
			Computer A = new Computer(input, setting.get(0));
			long result = A.run(0);
			for (int i = 1; i < 5; i++) 
				result = new Computer(input, setting.get(i)).run(result);
			bestValue = Math.max(bestValue, result);
		}
		
		/* Combinatorics */
		for (int i = 0; i < 5; i++) {
			if (!setting.contains(i)) {
				setting.add(i);
				trySettings(setting);
				setting.remove(setting.indexOf(i));
			}
		}
	}
}
