package simulator.model;

import java.util.List;

public class MostCrowdedStrategy implements LightSwitchingStrategy {

	private int timeSlot;
	
	public MostCrowdedStrategy(int timeSlot) {
		this.timeSlot = timeSlot;
	}
	
	private int getNextGreen(List<List<Vehicle>> vehicles, int position) {
		int maxSize = -1;
		int nextGreen = 0;
			
		int index = position;
		
		for (int i = 0; i < vehicles.size(); i++) 
			if (vehicles.get((index + i) % vehicles.size()).size() > maxSize) {
				nextGreen = (index + i) % vehicles.size();
				maxSize = vehicles.get((index + i) % vehicles.size()).size();
			}
		
		return nextGreen;
	}
	
	@Override
	public int chooseNextGreen(List<Road> roads, List<List<Vehicle>> qs, int currGreen, int lastSwitchingTime, int currTime) {
		
		if (roads.isEmpty())
			return -1;
		else if (currGreen == -1)
			return getNextGreen(qs, 0);
		else if (currTime - lastSwitchingTime < timeSlot)
			return currGreen;
		
		return getNextGreen(qs, (currGreen + 1) % roads.size());
	}

}
