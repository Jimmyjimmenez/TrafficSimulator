package simulator.model;

import java.util.List;

import simulator.misc.Pair;

public class SetWeatherEvent extends Event {

	private List<Pair<String, Weather>> ws;
	
	public SetWeatherEvent(int time, List<Pair<String, Weather>> ws) {
		super(time);
		if (ws.isEmpty())
			throw new IllegalArgumentException("List empty");
		this.ws = ws;
	}

	@Override
	void execute(RoadMap map) {

		for (Pair<String, Weather> w : ws) 
			if (map.getRoad(w.getFirst()) != null)
				map.getRoad(w.getFirst()).setWeather(w.getSecond());
	}

	@Override
	public String toString() { return "Change Weather: " + ws.toString(); }
}
