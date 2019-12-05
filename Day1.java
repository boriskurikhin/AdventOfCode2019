import java.util.Scanner;

public class Day1 {
  
  //Nothing crazy.
  public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		
		long total = 0;

		for (int i = 0; i < 100; i++) {
			int m = scan.nextInt(), toAdd = m;
			while (toAdd >= 0) {
				toAdd = (int) (toAdd / 3) - 2;
				total += Math.max(0, toAdd);
			}
		}
	
		System.out.println(total);
		scan.close();
	}
}
