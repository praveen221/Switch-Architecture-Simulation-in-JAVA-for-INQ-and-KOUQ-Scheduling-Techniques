// PRAVEEN JANGID 150101048
//BHOLA SHANKAR RATHIA 150101016
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class assignment2_150101048_150101016 {  
	
	private static List<Queue<Packet>> inputPortList;     // intialsing initial input and output ports 
	
	private static List<Queue<Packet>> outputPortList;
	
	private static List<Packet> transmittedList = new ArrayList<Packet>();   // intialising a transmitted packet and dropped packet list
	
	private static List<Packet> droppedPacketList = new ArrayList<Packet>();
	
	private static float sumOfDroppedProbablity = 0.0f;


	public static void main(String[] args) {    // section for taking command line inputs or default inputs 
		int numberOfPorts = 8;
		int buffersize = 4;
		double packetGenProbablity = 0.5;
		int maxTimeSlots = 10000;
		int timeslots = 0;
		String queue = "INQ";
		int k = (int) (0.6 * numberOfPorts);
		String outputFileName = "output.txt";
		
		if (args.length >= 1) {
			String numberOfPortsStr = args[0];
			try {
				numberOfPorts = Integer.parseInt(numberOfPortsStr);
			} catch(NumberFormatException nfe) {
                numberOfPorts = 8;
            }
		}
		
		if (args.length >= 2) {
			String bufferSizeStr = args[1];
			try {
				buffersize = Integer.parseInt(bufferSizeStr);
			} catch(NumberFormatException nfe) {
                buffersize = 4;
            }
		}
		
		if (args.length >= 3) {
			String packetGenProbablityStr = args[2];
			try {
				packetGenProbablity = Double.parseDouble(packetGenProbablityStr);
			} catch(NumberFormatException nfe) {
				packetGenProbablity = 0.5;
            }
		}
		
		if (args.length >= 4) {
			String queueStr = args[3];
			if (null != queueStr) {
				queue = queueStr;
			} else {
				queue = "INQ";
			}

		}
		
		if (args.length >= 5) {
			String kStr = args[4];
			try {
				k = (int)(Double.parseDouble(kStr) * numberOfPorts);
			} catch(NumberFormatException nfe) {
				k = (int) (0.6 * numberOfPorts);
            }
		}
		
		if (args.length >= 6) {
			String outPutFileStr = args[5];
			if (null != outPutFileStr) {
				outputFileName = outPutFileStr;
			} else {
				outputFileName = "output.txt";
			}
		}
		
		if (args.length >= 7) {
			String maxTimeSlotsStr = args[6];
			try {
				maxTimeSlots = Integer.parseInt(maxTimeSlotsStr);
			} catch(NumberFormatException nfe) {
                maxTimeSlots = 10000;
            }
		}
		
				
		inputPortList = new ArrayList<Queue<Packet>>(numberOfPorts);   // initialising every port with an empty queue. 
		outputPortList = new ArrayList<Queue<Packet>>(numberOfPorts);
		
		for (int i = 0 ; i < numberOfPorts ; i++) {
			inputPortList.add(new LinkedList<Packet>());
			outputPortList.add(new LinkedList<Packet>());
		}
		
		while(timeslots < maxTimeSlots) {   // the main loop which runs the code for maxtimeslots
			
			Map<Integer, List<Integer>> tempAllocation = new HashMap<>();
			
			for (int i = 0 ; i < inputPortList.size(); i++) {       // traffic generation begins here 
				
				Queue<Packet> bufferQueue = inputPortList.get(i);
				if (bufferQueue == null) {
					bufferQueue = new LinkedList<Packet>();
					inputPortList.add(i, bufferQueue);
				}
				
				double tempProbablity = Math.random();  // generating random number to generate traffic 
				if (tempProbablity <= packetGenProbablity && bufferQueue.size() <= buffersize - 1) {
					
					Packet newPacket = new Packet();
					newPacket.setGeneratedTime(timeslots);  // setting generation time 
					newPacket.setInputPort(i);
					newPacket.setDeltaTime(0.001F + new Random().nextFloat() * (0.01F - 0.001F));
					
					bufferQueue.add(newPacket);
					inputPortList.set(i, bufferQueue);
				}
				
				if (bufferQueue.size() > 0) {   // finding contenders for every output port 
					int tempoutPutPort = new Random().nextInt(((numberOfPorts - 1) - 0) + 1) + 0;
					
					List<Integer> contenders = tempAllocation.get(tempoutPutPort);
					if (contenders == null) {
						contenders = new ArrayList<Integer>();
					}
					contenders.add(i);
					tempAllocation.put(tempoutPutPort, contenders);
				}
			}
			
			if ("INQ".equals(queue)) {
				INQScheduler(tempAllocation, timeslots);
			} else if ("KOUQ".equals(queue)) {
				KOUQScheduler(tempAllocation, timeslots, k, buffersize, numberOfPorts);
			} else {
				System.exit(0);
			}
			
			transmit(timeslots);
				
			timeslots++;
		}
		
		printToFile(numberOfPorts, packetGenProbablity, queue, maxTimeSlots, outputFileName);

	}
	
	// KOUQ PACKET SCHEDULING FUNCTION 
	public static void KOUQScheduler(Map<Integer, List<Integer>> tempAllocation, int timeslots, int k, int outputBufferSize, int numberOfPorts) {
		int counter = 0;
		for (Map.Entry<Integer, List<Integer>> entry : tempAllocation.entrySet()) {
			List<Integer> contenderList = entry.getValue();
		    int outputPort = entry.getKey();
			
			
			
			Queue<Packet> outputBufferQueue = outputPortList.get(outputPort);
			
			Set<Integer> selectedContender = new HashSet<>();
			
			if (contenderList.size() > k) {
				counter++;
			}
			
			int bufferSpace = (outputBufferSize - outputBufferQueue.size());  // calculation of available space in the buffer 
			int maxAllowed = bufferSpace > k ? k : bufferSpace;
		    

		    if (contenderList.size() <= maxAllowed) {
		    	selectedContender.addAll(contenderList);
		    } else {
		    	
		    	int contenderSize = contenderList.size();
		    	
		    	Set<Integer> selectedContenderIndicesSet = new LinkedHashSet<Integer>();
		    	while (selectedContenderIndicesSet.size() < maxAllowed) {
		    	    Integer next = new Random().nextInt(((contenderSize - 1) - 0) + 1) + 0;
		    	    selectedContenderIndicesSet.add(next);
		    	}
		    	
		    	Iterator<Integer> iter = selectedContenderIndicesSet.iterator();
		    	while (iter.hasNext()) {
		    	    selectedContender.add(contenderList.get(iter.next()));
		    	}
		    }
		        
		    List<Packet> packetsToBeAdded = new ArrayList<Packet>(); // findind packets that can be put in the trasnmission queue at output port or to be dropped
		
		    for (int i = 0 ; i< contenderList.size() ; i++ ) {
		    	
		    	Packet packetToBeAddedOrDeleted = inputPortList.get(contenderList.get(i)).poll();
		    	if (selectedContender.contains(contenderList.get(i))) {
		    		packetToBeAddedOrDeleted.setOutputPort(outputPort);
		    	
				    packetsToBeAdded.add(packetToBeAddedOrDeleted);
		    	} else {
		    		packetToBeAddedOrDeleted.setDropped(true);
		    		
		    		droppedPacketList.add(packetToBeAddedOrDeleted);
		    	}
		    	
		    }
		    
		    //Collections.sort(packetsToBeAdded, new SortbyDeltaTime());
		    
		    for (int i=0;i<packetsToBeAdded.size();i++) {
		    	outputBufferQueue.add(packetsToBeAdded.get(i));
		    } 
		}
		
		sumOfDroppedProbablity += ((counter * 1.0) /numberOfPorts);
	}

	// INQ PACKET SCHEDULING FUNCTION 
	public static void INQScheduler(Map<Integer, List<Integer>> tempAllocation, int timeslots) {
	
		for (Map.Entry<Integer, List<Integer>> entry : tempAllocation.entrySet()) {
			List<Integer> contenderList = entry.getValue();
		    int outputPort = entry.getKey();
			
		    int selectedContender;
		    
		    if (contenderList.size() == 1) {
		    	selectedContender = contenderList.get(0);
		    } else {
		    	int contenderSize = contenderList.size();
		    	int selectedContenderIndex = new Random().nextInt(((contenderSize - 1) - 0) + 1) + 0;
		    	selectedContender = contenderList.get(selectedContenderIndex);
		    }
		    
		    Packet packetToBeAdded = inputPortList.get(selectedContender).poll();
		    packetToBeAdded.setOutputPort(outputPort);
		    packetToBeAdded.setTransmittedTime(timeslots);  // setting transmission time 
		     
		    Queue<Packet> outputBufferQueue = outputPortList.get(outputPort);
		    outputBufferQueue.clear();
		    outputBufferQueue.add(packetToBeAdded);
		    
		}
	}
	// function to transmit a packet 
	public static void transmit(int timeslot) {
		for (int i = 0; i < outputPortList.size() ; i++) {
			Packet packetToBeTransmitted = outputPortList.get(i).poll();
			
			if (packetToBeTransmitted != null) {
				packetToBeTransmitted.setTransmittedTime(timeslot);
				transmittedList.add(packetToBeTransmitted);
			}
		}
	}
	// function to calculate AVG PKT DELAY
	public static double calculateAvgTimeDelay() {
		float timeDelay = 0;
		for (int i = 0 ; i < transmittedList.size() ; i++) {
			Packet packet = transmittedList.get(i);
			timeDelay = timeDelay + (packet.getTransmittedTime() - packet.getGeneratedTime() + 1); 
		}
		
		return timeDelay/transmittedList.size();
	}
	// function to calculate AVG PKT DELAY STD DEVIATION
	public static double calcStdDeviation() {
		double squareSum = 0;
		double mean = calculateAvgTimeDelay();
		for (int i = 0 ; i < transmittedList.size() ; i++) {
			Packet packet = transmittedList.get(i);
			double diff = (packet.getTransmittedTime() - packet.getGeneratedTime() + 1) - mean;
			squareSum = squareSum + diff * diff; 
		}
		
		return Math.sqrt(squareSum/transmittedList.size());
	}
	// function to calculate AVG LINK UTILIZATION
	public static double calcAvgLinkUtilization(int numberOfPorts, int maxTimeslot) {
		double value = (transmittedList.size()/(numberOfPorts * maxTimeslot * 1.0));
		return value;
	}
	// function to PRINT OUTPUT 
	public static void print(int numberOfPorts, double prob, String queue, int maxTimeSlot) {
		System.out.println("N\tp\tQueue type\tAvgPD\t\t\tStd Dev of PD\t\tAvg link utilization\t Dropped Probablity");
		System.out.println(numberOfPorts + "\t" +
							prob + "\t" +
							queue + "\t\t" +
							calculateAvgTimeDelay() + "\t" +
							calcStdDeviation() + "\t\t" +
							calcAvgLinkUtilization(numberOfPorts, maxTimeSlot) + "\t\t" +
							((sumOfDroppedProbablity * 1.0) /maxTimeSlot) );
	}
	// Function to append output to the file 
	public static void printToFile(int numberOfPorts, double prob, String queue, int maxTimeSlot, String outputFile) {		
		try(FileWriter fw = new FileWriter(outputFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw)) {
			
			print(numberOfPorts, prob, queue, maxTimeSlot);
			    
			out.println("N\tp\tQueue type\t\tAvgPD\t\t\tStd Dev of PD\t\tAvg link utilization\tDropped Probablity");
			out.println(numberOfPorts + "\t" +
								prob + "\t" +
								queue + "\t\t\t" +
								calculateAvgTimeDelay() + "\t" +
								calcStdDeviation() + "\t\t" +
								calcAvgLinkUtilization(numberOfPorts, maxTimeSlot) + "\t\t\t" +
								((sumOfDroppedProbablity * 1.0) /maxTimeSlot) );			    
			} catch (IOException e) {
			    System.out.println(e);
			}

	}
	

}
