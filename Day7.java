import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/*
 * Okay, so my code is not entirely original. I looked at the following solution
 * ----> https://github.com/akaritakai/AdventOfCode2019/blob/master/src/main/java/net/akaritakai/aoc2019/Puzzle07.java
 * 
 * This guy is really talented, and did an amazing job multithreading and using concurrent hashmaps.
 * The way he set up his code is amazing. I tried my best understanding his solution and implementing my own
 * on top of it. It seems like he was using Lombok to write his code which I don't have. 
 * 
 * So this is vanilla Java. But very functional. 
 * 
 */


public class Day7 {
	public static String programInstructions;
	public static final long SETTINGS[] = {5L,6L,7L,8L,9L};
	public static long result = Long.MIN_VALUE;

	
	public static void main(String[] args) throws Exception {
		System.out.println(new Day7().getAnswer());
	}
	
	public long getAnswer() throws Exception {
		BufferedReader r = new BufferedReader(new FileReader("src/input.txt"));
		programInstructions = r.readLine();
		r.close();
		computePhaseSettings(new ArrayList<Long>());
		return result;
	}
	
	private void computePhaseSettings (List<Long> phaseSettings) throws Exception {
		if (phaseSettings.size() == SETTINGS.length) {
			result = Math.max(result, runAmplifiers(phaseSettings));
			return;
		}
		
		for (long setting : SETTINGS) {
			if (!phaseSettings.contains(setting)) {
				phaseSettings.add(setting);
				computePhaseSettings(phaseSettings);
				phaseSettings.remove(phaseSettings.indexOf(setting));
			}
		}
	}
	
	
	private long runAmplifiers(List<Long> phaseSettings) throws Exception {
		System.out.println(phaseSettings);
		List<LinkedBlockingQueue<Long>> ioStreams = Stream.generate((Supplier<LinkedBlockingQueue<Long>>) LinkedBlockingQueue::new)
		        .limit(phaseSettings.size())
		        .collect(Collectors.toList());
		
		List<Thread> amplifiers = IntStream.range(0, phaseSettings.size())
				.mapToObj(i -> {
					System.out.println(i);
					Long phaseSetting = phaseSettings.get(i);
					LinkedBlockingQueue<Long> currentAmplifier = ioStreams.get(i);
					LinkedBlockingQueue<Long> nextAmplifier = ioStreams.get((i + 1) % phaseSettings.size());
					try {
						return runSingleAmplifier(phaseSetting, currentAmplifier, nextAmplifier);
					} catch (InterruptedException e) { 
						return null; 
					}
				})
				.collect(Collectors.toList());
		ioStreams.get(0).add(0L); //We send that initial 0 to the first amplifier
		//wait for all threads to die
		amplifiers.forEach(t -> {
			try {
				t.join();
			} catch (InterruptedException e) {}
		});
		//Whatever was sent as the input to the first amplifier is the result of the last amplifier
		return ioStreams.get(0).remove();
	}
	
	/* Returns a thread which in turns runs an instance of an amplifier. This thread takes 2 streams as parameter */
	private Thread runSingleAmplifier (long phaseSetting, BlockingQueue<Long> inputStream, BlockingQueue<Long> outputStream) throws InterruptedException {
		inputStream.put(phaseSetting);
		Thread thread = new Thread(() -> {
			Computer intComputer = new Computer(
					programInstructions,
					() -> {
						try {
							//Passes function as argument
							return inputStream.take();
						} catch (InterruptedException e) { return null; }
					},
					t -> {
						try {
							outputStream.put(t);
						} catch (InterruptedException e) { }
					});
			
			intComputer.run();
		});
		thread.start();
		return thread;
	}
}

class Computer {
	long ip = 0; /* Program Pointer */
	private Map<Long, Long> memory;
	private Supplier<Long> _input; //stream of inputs
	private Consumer<Long> _output; //stream of outputs
	private enum OpCode {
		HALT 	(99),
		ADD 	(1),
		MULTIPLY(2),
		INPUT 	(3),
		OUTPUT	(4),
		JUMP_T	(5),
		JUMP_F 	(6),
		LESS	(7),
		EQ		(6);
		
		private final long op_code;
		
		OpCode (long code) {
			op_code = code;
		}
		
		/* Returns the enum value of an OP_CODE */
		private static OpCode codeName (long code) {
			return Arrays.stream(OpCode.values())
					.filter(opcode -> opcode.op_code == code)
					.findAny()
					.orElseThrow(() -> new UnsupportedOperationException("Unknown Code: " + code));
		}
		
	}
	private enum Mode {
		POSITION (0),
		IMMEDIATE(1);
		
		private final long mode;
		
		Mode (long _mode) {
			mode = _mode;
		}
		
		/* Returns the enum value of an OP_CODE */
		private static Mode modeName (long _mode) {
			return Arrays.stream(Mode.values())
					.filter(m -> m.mode == _mode)
					.findAny()
					.orElseThrow(() -> new UnsupportedOperationException("Unknown Mode: " + _mode));
		}
	}
	
	public Computer(String instructions, Supplier<Long> supplier, Consumer<Long> consumer) {
		this.memory = allocateMemory(instructions);
		this._input = supplier;
		this._output = consumer;
	}
	
	/* Retrieves current opCode as an ENUM */
	private OpCode opCode () {
		return OpCode.codeName(memory.get(ip) % 100L);
	}
	
	private Mode mode (int parameterNumber) {
		long operation = memory.getOrDefault(ip, 0L); //0 by default
		return Mode.modeName( operation / (long) Math.pow(10, 1 + parameterNumber) % 10);
		
	}
	
	private Parameter p (int parameterNumber) {
		return new Parameter(parameterNumber);
	}
	
	private Map<Long, Long> allocateMemory (String instructions) {
		Map<Long, Long> memoryMap = new ConcurrentHashMap<Long, Long>();
		Iterator<Long> data = Arrays.stream(instructions.split(",")).map(Long::parseLong).iterator();
		for (long i = 0; data.hasNext(); i++)
			memoryMap.put(i, data.next());
		return memoryMap;
	}
	
	public synchronized void run() {
		while (true) {
			switch (opCode()) {
				case ADD:
					memory.put(p(3).address, p(1).value + p(2).value);
					ip += 4;
					break;
				case MULTIPLY:
					memory.put(p(3).address, p(1).value * p(2).value);
					ip += 4;
					break;
				case INPUT:
					memory.put(p(1).address, _input.get());
					ip += 2;
					break;
				case OUTPUT:
					_output.accept(p(1).value);
					ip += 2;
					break;
				case JUMP_T:
					ip = p(1).value != 0 ? p(2).value : ip + 3;
					break;
				case JUMP_F:
					ip = p(1).value == 0 ? p(2).value : ip + 3;
					break;
				case LESS:
					memory.put(p(3).address, p(1).value < p(2).value ? 1L : 0L);
					ip += 4;
					break;
				case EQ:
					memory.put(p(3).address, p(1).value == p(2).value ? 1L : 0L);
					ip += 4;
					break;
				case HALT:
					return;
			}
		}
	}
	
	private final class Parameter { 
		public long address;
		public long value;
		public Parameter (int parameterNumber) {
			switch (mode(parameterNumber)) {
				case POSITION:
					address = memory.getOrDefault(ip + parameterNumber, 0L);
					break;
				case IMMEDIATE:
					address = ip + parameterNumber;
					break;
				default: throw new UnsupportedOperationException("Unknown Mode for: " + parameterNumber);
			}
			value = memory.getOrDefault(address, 0L);
		}
	}
}
