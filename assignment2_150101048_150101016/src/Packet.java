import java.util.Comparator;

public class Packet {
	
	private int transmittedTime;
	
	private int generatedTime;
	
	private float deltaTime;
	
	private int outputPort;
	
	private int inputPort;
	
	private boolean dropped;

	public int getTransmittedTime() {
		return transmittedTime;
	}

	public int getOutputPort() {
		return outputPort;
	}

	public void setOutputPort(int outputPort) {
		this.outputPort = outputPort;
	}

	public int getInputPort() {
		return inputPort;
	}

	public void setInputPort(int inputPort) {
		this.inputPort = inputPort;
	}

	public void setTransmittedTime(int transmittedTime) {
		this.transmittedTime = transmittedTime;
	}

	public int getGeneratedTime() {
		return generatedTime;
	}

	public void setGeneratedTime(int generatedTime) {
		this.generatedTime = generatedTime;
	}

	public float getDeltaTime() {
		return deltaTime;
	}

	public void setDeltaTime(float deltaTime) {
		this.deltaTime = deltaTime;
	}

	public boolean isDropped() {
		return dropped;
	}

	public void setDropped(boolean dropped) {
		this.dropped = dropped;
	}
}


class SortbyDeltaTime implements Comparator<Packet> { 
    public int compare(Packet a, Packet b) { 
        return (int) (a.getDeltaTime() - b.getDeltaTime()); 
    } 
} 
