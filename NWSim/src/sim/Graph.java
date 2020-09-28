package sim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


class Vertex {
	String label;
	Vertex(String label) {
		this.label = label;
	}

	// equals and hashCode
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(this == obj) 
			return true;
		if(obj == null || obj.getClass()!= this.getClass()) 
			return false; 
		Vertex vertex=(Vertex)obj;
		return (vertex.label == this.label ); 

	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return label.hashCode();
	}

	@Override
	public String toString() {
		return "Vertex [label=" + label + "]";
	}  


}

public class Graph {


	Graph graph;
	public static Mapper[] all_mappers=new Mapper[Framework.Mapper];
	public static Reducer[] all_reducers=new Reducer[(Framework.Reducer)+1];
	public Map<Vertex, List<Vertex>> adjVertices;
	List<Vertex> list;

	void addList(String label){
		list.add(new Vertex(label));
		//System.out.println("Reducer is :"+label);
	}

	// standard constructor, getters, setters
	void addVertex(String label) {
		//System.out.println("Mapper is :"+ label);

		adjVertices.putIfAbsent(new Vertex(label),list);

	}

	void removeVertex(String label) {
		Vertex v = new Vertex(label);
		adjVertices.values().stream().forEach(e -> e.remove(v));
		adjVertices.remove(new Vertex(label));
	}


	public  Graph() {
		adjVertices=new LinkedHashMap<>();	
		list=new ArrayList<Vertex>();
		System.out.println("");   
	}

	public void createGraph(Graph graph) throws IOException

	{        
		int M = Framework.Mapper;
		int R = Framework.Reducer;


		for(double i=1;i<=M;i++)
		{
			graph.addVertex(Double.toString(i));
		}
		System.out.println();



		for(double j=1;j<=R;j++)
		{
			graph.addList(Double.toString(j));
		}
		System.out.println("\nGraph is created: \n");

		Set<Vertex> keySet= adjVertices.keySet();
		for (Vertex ver:keySet)
		{
			List<Vertex> list= adjVertices.get(ver);
			System.out.print("{"+ver.label+" =");

			for(int i=0;i<list.size();i++)
			{
				System.out.print(list.get(i).label);
				System.out.print(",");
			}
			System.out.print("}");
			System.out.println("\n");
		}



		//Defining Mapper Properties
		System.out.println("\n--------------------------Mapper details are --------------------------\n");
		for(int i=0;i<Framework.Mapper;i++)
		{

			Calendar calendar = Calendar.getInstance();

			all_mappers[i]=new Mapper();
			all_mappers[i].width=(int)(Math.random()*5);
			all_mappers[i].size=(int) (Math.random()*Framework.Queue_length*3);
			while(all_mappers[i].size<all_mappers[i].width)
			{
				all_mappers[i].size=(int) (Math.random()*Framework.Queue_length*3);
			}
			all_mappers[i].Next_job = calendar.get(Calendar.MINUTE);
			for(int j=1;j<=Framework.Reducer;j++)
			{
				all_mappers[i].setband((int)j,(float)Math.random());
			}
			System.out.println("For mapper "+(i+1)+" following are parameters: ");
			all_mappers[i].showdata();
			System.out.println();

		}

		//Defining Reducer properties
		System.out.println("\n--------------------------Reducer details are --------------------------");
		for(int i=1;i<((Framework.Reducer)+1);i++)

		{
			
			all_reducers[i]=new Reducer();
			all_reducers[i].cpu_pw=Framework.intlist.get(i);
			all_reducers[i].Reducerdetails();
		}

	}
}



