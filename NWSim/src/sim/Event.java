package sim;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Event {

	Event e;

	//This is the class that will initialize the parameters

	//Define parameters of the event
	public int arrival_time;
	public int mappers_req;
	public int mappers_location;
	public int reducers_req;
	public ArrayList<Integer> reducers_location;
	public int link_capacity;
	public float demand_capacity;
	public int deadline;
	public int job_id;
	public int n_flows;

	@Override
	public String toString() {
		return " [" + arrival_time +","  + mappers_req +","
				+ mappers_location+","  + reducers_req +"," + reducers_location
				+ "," + link_capacity + "," + demand_capacity + ","
				+ deadline + ","+job_id+","+n_flows+"]";
	}	

	Event(int arrival_time,int mappers_req,int mappers_location,int reducers_req,ArrayList<Integer> reducer_loc,int link_capacity,float demand_capacity,int deadline,int job_id,int n_flows) {
		this.arrival_time=arrival_time;
		this.mappers_req=mappers_req;
		this.mappers_location=mappers_location;
		this.reducers_req=reducers_req;
		this.reducers_location=reducer_loc;
		this.link_capacity=link_capacity;
		this.demand_capacity=demand_capacity;
		this.deadline=deadline;
		this.job_id=job_id;
		this.n_flows=n_flows;
	}

}































