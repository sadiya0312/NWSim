package sim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.*;



public class Collectdata {

	Collectdata cd;

	static ArrayList Coflow_All=new ArrayList();

	static ArrayList<Integer> flows=new ArrayList();
	static ArrayList<Integer> location_red=new ArrayList();
	static Map<Integer, Float> Ratio=new LinkedHashMap<Integer, Float>();
	static Map<Integer, Float> Ratio_location=new LinkedHashMap<Integer, Float>();
	static  Map<Integer, Integer> frequency = new LinkedHashMap<Integer, Integer>();
	static  Map<Integer, Integer> frequency_location = new LinkedHashMap<Integer, Integer>();
	static float highest,highest_loc;

	public Collectdata() {
		// TODO Auto-generated constructor stub
	}

	public /*static*/ void main(String args[]) throws NumberFormatException, IOException
	{   System.out.println("\n---------------------------Sample Data------------------------------------------\n");
	readparameters();
	flow_frequency();
	location_frequency();

	int i=0;
	JFrame frame = new JFrame();
	frame.setSize(350, 300);		  
	Integer[] value= new Integer[Ratio.size()];
	Float[] languages = new Float[Ratio.size()];

	for (Entry<Integer, Float> val : Ratio.entrySet()) { 		    
		value[i]=val.getKey();
		languages[i]=val.getValue();
		i++;
	} 

	frame.getContentPane().add(new SimpleBarChart(value, languages,
			"Degree Distribution"));

	/* WindowListener winListener = new WindowAdapter() {
			  public void windowClosing(WindowEvent event) {
			  System.exit(0);
			  }
			  };
			  frame.addWindowListener(winListener);
			  frame.setVisible(true);*/	

	}

	static void readparameters() throws NumberFormatException, IOException
	{

		try (BufferedReader br = new BufferedReader(new FileReader("FB2010-1Hr-150-0.txt"))) {
			while (br.ready()) {
				Coflow_All.add(br.readLine());
			}
		}
		//Framework.size = Coflow_All.size();
		//System.out.println("\nThere are "+Framework.size+" coflows entries\n");

		//This loop will extract each Coflow and create queue for it.
		//Processing will be inside the loop since Coflow will be discarded once scheduled and ArrayList will be empty again. 

		for(int queue_entry=1;queue_entry<Coflow_All.size();queue_entry++)
		{
			//Extract a Coflow and satisfy conditions

			ArrayList<Integer> Coflow_each = new ArrayList<Integer>();
			ArrayList<Integer> Coflow_colon_rem=new ArrayList<Integer>();

			//Coflow extraction

			String input = (String) Coflow_All.get(queue_entry);
			String colonremoved=input.replaceAll(":", " ");
			colonremoved=colonremoved.replaceAll("\\.0","");
			System.out.println(colonremoved);

			int find=input.indexOf(":");
			if (find != -1) 
			{   
				//this will give substring
				input= input.substring(0 , find); 
			}

			String[] numbers = input.split(" "); 
			String[] colon_rem = colonremoved.split(" "); 
			for (String s : numbers) 
			{
				Coflow_each.add(Integer.parseInt(s));	       
			}

			for (String p : colon_rem) 
			{
				Coflow_colon_rem.add(Integer.parseInt(p));	       
			}	
			System.out.println(Coflow_colon_rem);
			System.out.println(Coflow_each);

			int index=3, index_red,loc_red;
			for(int redu=0;redu<=Coflow_each.get(2);redu++)
			{   index++;
			if(redu==Coflow_each.get(2))
			{
				flows.add(Coflow_each.get(index-1));

			}

			}
			index_red =3+(int) (Coflow_colon_rem.get(2));
			loc_red=(index_red+1);
			//System.out.println(Coflow_colon_rem.get(2));
			//System.out.println(index_red);
			//System.out.println(loc_red);
			for(int j=1;j<=(int)Coflow_colon_rem.get(index_red);j++){
				if(j<Coflow_colon_rem.size()){
					//System.out.println(Coflow_colon_rem.get(index_red));
					//System.out.println(Coflow_colon_rem.get(loc_red));
					location_red.add(Coflow_colon_rem.get(loc_red));
					loc_red=loc_red+2;
				}
			}
		}
		System.out.println("\nLocation of all reducers "+location_red);
		System.out.println("\nAll identified flows from sample data are :"+ flows);
		System.out.println("\nAll identified reducer locations from sample data are :"+ location_red);

	}
	static void location_frequency(){
		for (Integer i : location_red) { 
			Integer j = frequency_location.get(i); 
			frequency_location.put(i, (j == null) ? 1 : j + 1);
		} 
		System.out.println("\nFrequency of occurance of each reducer "+frequency_location);

		highest_loc= location_red.size();

		for (Map.Entry<Integer, Integer> val : frequency_location.entrySet()) {    
			Ratio_location.put(val.getKey(), (float) (val.getValue()/highest_loc)*100);
		}


		//Sort Ratio_location

		Ratio_location=Ratio_location.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(
						Map.Entry::getKey, 
						Map.Entry::getValue, 
						(x,y)-> {throw new AssertionError();},
						LinkedHashMap::new
						));
		System.out.println("\n\nReducer Location and ratio(freq/highest freq):\n"+Ratio_location); 

	}
	static void flow_frequency()
	{

		for (Integer i : flows) { 
			Integer j = frequency.get(i); 
			frequency.put(i, (j == null) ? 1 : j + 1);
		} 
		highest=flows.size();
		//highest=Collections.max(frequency.values());
		System.out.println("\nHighest value(Total flows):"+highest); 

		for (Map.Entry<Integer, Integer> val : frequency.entrySet()) {    
			Ratio.put(val.getKey(), (float) ((val.getValue()/highest)*100));
		}

		System.out.println("\nFlows		   frequencies		Ratio(frequency/number of rows in table)");
		Iterator<Entry<Integer, Float>> val2 = Ratio.entrySet().iterator();
		// displaying the occurrence of elements in the arraylist 
		for (Map.Entry<Integer, Integer> val : frequency.entrySet()) { 
			System.out.print("\n" + val.getKey()
			+ "			" + val.getValue() + "		"+ (val2.next().getValue()) +"  %");              
		}

		System.out.println("\n\nFlows and ratio(freq/highest freq):"); 

		for (Entry<Integer, Float> val : Ratio.entrySet()) { 
			System.out.print("[" + val.getKey() 
			+ ": " + val.getValue()+"],");       

		}        		
	}

}


