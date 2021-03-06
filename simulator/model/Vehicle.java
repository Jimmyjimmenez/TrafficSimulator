package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONObject;

public class Vehicle extends SimulatedObject {
	
	private List<Junction> itinerary;
	private int maxSpeed, speed, location, contClass, totalCont, totalDistance, index;
	private VehicleStatus status;
	private Road road;
	
	Vehicle(String id, int maxSpeed, int contClass, List<Junction> itinerary) {
		super(id);
		
		if (maxSpeed <= 0 || contClass < 0 || contClass > 10 || itinerary.size() < 2)
			throw new IllegalArgumentException("Error: Arguments for new Vehicle not valid");
		
		this.maxSpeed = maxSpeed;
		this.contClass = contClass;
		this.index = 0;
		this.speed = 0;
		this.location = 0;
		this.totalCont = 0;
		this.totalDistance = 0;
		this.itinerary = Collections.unmodifiableList(new ArrayList<>(itinerary));
		this.status = VehicleStatus.PENDING;
		this.road = null;
	}
	
	public int getLocation() { return location; }
	
	public int getSpeed() { return speed; }
	
	public int getMaxSpeed() { return maxSpeed; }
	
	public int getContClass() { return contClass; }
	
	public VehicleStatus getStatus() { return status; }
	
	public int getTotalCO2() { return totalCont; }
	
	public List<Junction> getItinerary() { return itinerary; }
	
	public Road getRoad() { return road; }
	
	public int getTotalDistance() { return totalDistance; }
	
	void setSpeed(int s) {
		if (s < 0)
			throw new IllegalArgumentException("Error: New speed is negative");
		if (!status.equals(VehicleStatus.WAITING) && !status.equals(VehicleStatus.PENDING))
			speed = Math.min(maxSpeed, s);
	}

	void setContaminationClass(int c) {
		if (c < 0 || c > 10)
			throw new IllegalArgumentException("Error: New contamination class is not valid");
		
		contClass = c;
	}
	
	@Override
	void advance(int time) {
		if (status == VehicleStatus.TRAVELING) {
			int previous = location;
			
			// Location update
			location = Math.min(previous + speed, road.getLength());
			totalDistance += (location - previous);
			
			// Contamination update
			int c = (location - previous) * contClass;
			totalCont += c;
			road.addContamination(c);
			
			// Update status
			if (location >= road.getLength()) {
				if (index != itinerary.size() - 1)
					status = VehicleStatus.WAITING;
				speed = 0;
				road.getDest().enter(this);
				index++;
			}
		}
	}
	
	void moveToNextRoad() {
		if (status != VehicleStatus.PENDING && status != VehicleStatus.WAITING)
			throw new IllegalArgumentException("Error: Vehicle status not valid");
		
		if (road != null)
			road.exit(this);
		
		if (itinerary.size() - 1 == index) {
			status = VehicleStatus.ARRIVED;
			road = null;
			speed = 0;
			location = 0;
		}
		else {
			road = itinerary.get(index).roadTo(itinerary.get(index + 1));
			status = VehicleStatus.TRAVELING;
			speed = 0;
			location = 0;
			road.enter(this);
		}
	}
	
	private boolean checkStatus() {
		return (status.equals(VehicleStatus.TRAVELING) || status.equals(VehicleStatus.WAITING));
	}

	@Override
	public JSONObject report() {
		JSONObject data = new JSONObject();
		
		data.put("id", _id);
		data.put("speed", speed);
		data.put("distance", totalDistance);
		data.put("co2", totalCont);
		data.put("class", contClass);
		data.put("status", status.toString());
		
		if (checkStatus()) {
			data.put("road", road.getId());
			data.put("location", location);
		}
		
		return data;
	}
	
	public String toString() { return getId(); }
	
	public static class CompareLocation implements Comparator<Vehicle> {

		@Override
		public int compare(Vehicle o1, Vehicle o2) {
			if (o1.getLocation() > o2.getLocation())
				return -1;
			else if (o1.getLocation() == o2.getLocation())
				return 0;
			return 1;
		}
	}
}
