import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

import javax.swing.JFrame;

public class Day8 extends Component {
	
	public static int part1(String line, int w, int h) {
		int len = line.length();
		int layerSize = w * h;
		int best = Integer.MAX_VALUE, answer = 0;
		for (int i = 0; i < len; i += layerSize) {
			int count[] = new int[3]; Arrays.fill(count, 0);
			for (int j = i; j < i + (w * h); j++)
				count[line.charAt(j) - '0']++;
			if (count[0] < best) {
				best = count[0];
				answer = count[1] * count[2];
			}
		}
		return answer;
	}
	
	public static int[] part2(String line, int w, int h) {
		int len = line.length();
		int layerSize = w * h;
		int[] answer = new int[layerSize];
		for (int i = 0; i < h; i++)
			for (int j = 0; j < w; j++) 
				answer[i * w + j] = dig(line, w * h, len / (w * h), 1, i * w + j);
		return answer;
	}
	
	//Recursive exploration
	public static int dig ( String line, int layerSize, int numLayers, int currentLayer, int index ) {
		if ( currentLayer == numLayers )
			return line.charAt(index) - '0';
		if ( line.charAt(index) == '2')
			return dig(line, layerSize, numLayers, currentLayer + 1, index + layerSize);
		else return line.charAt(index) - '0';
	}
	
	static int[] pixels;
	static final int width = 25;
	static final int height = 6;
	static final int SCALE = 32;
	static final int layerSize = width * height;
	
	public static void main(String[] args) throws Exception{
		BufferedReader r = new BufferedReader(new FileReader("src/input.txt"));
		//System.out.println(part1(r.readLine(), width, height));
		pixels = part2(r.readLine(), width, height);
		
		JFrame frame = new JFrame("Output");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().add(new Day8());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setSize(new Dimension(width * SCALE, height * SCALE));
        frame.setVisible(true);
	}
	
	public void paint (Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				
				int iScale = i * SCALE;
				int jScale = j * SCALE;
				
				if (pixels[i * width + j] == 0) g2d.setColor(Color.BLACK);
				if (pixels[i * width + j] == 1) g2d.setColor(Color.WHITE);
				
				g2d.fillRect(jScale, iScale, SCALE, SCALE);
			}
		}
	}
}
