package sim.algorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import sim.Engine;
import sim.Framework;
import sim.Graph;
import sim.Mapper;
import sim.Reducer;
import sim.dto.Combination;
import sim.dto.CsvData;
import sim.dto.EnergyLogCSV;
import sim.exceptions.CsvException;
import sim.utils.Constants;

public class EEA implements Algorithm {
	/*
	 * 1. Read Efficiecy in ArrayList. 2. Calculate threshhhold over this
	 * ArrayList. 3. Eliminate the outliers from the arraylist. 4. Create nCr
	 * combinations of efficiency where n are total efficiency values and r is
	 * the number of reducers.Store all combinatons in datastructure. 5.
	 * [Optional: Give option to user to choose combinations of reducer they
	 * want] 6. Perform this over nCr : (n is length of arraylist and r is
	 * number of reducer nCr= n!/(r!*(n-r)) 6.1 Pick biggest coflow , assign the
	 * biggest flow of that coflow to most efficient reducer, energy needs to be
	 * re-calculated. 6.2 Then second biggest flow to second most efficient
	 * reducer and so on. 6.3 Now once one coflow is done , choose second
	 * biggest flow and repeat from 6.1 process. 7. Record the efficiecy
	 * produced and energy consumption for each nCr. 8. Record the timings and
	 * how much energy each task take. 9. [Optional: Give option to user to
	 * chose efficiency using toggle button]
	 */

	EEA eea;
	Combination combination = Combination.getInstance();
	float energyusage, Avg_energyusage;

	LinkedHashMap<Integer, ArrayList<Integer>> flowsize;
	ArrayList<Float> workingset;
	ArrayList<Float> inputreducer;
	public static List<Float> intlist;
	ArrayList<Integer> current_reducers;
	ArrayList<Integer> queue_max;
	ArrayList<Integer> mapper_max;

	List<Float> efficiencylist;
	List<Integer> index;

	Mapper[] mappers;

	Float maxcombination;
	int coflow_max;
	int flow_max;
	Float lt, ut;
	int n, r;
	int reducer;

	public EEA() throws FileNotFoundException, CsvException, IOException {
		index = new ArrayList<Integer>();
		mappers = Graph.all_mappers.clone();
		flowsize = Engine.flowsize;
		CsvData.extractCsv();
	}

	@Override
	public void runAlgo() throws InterruptedException, IOException {
		taskAssignment();

	}

	public void optimalThreshhold() {
		/*
		 * Median = efficiency.size/2 Q=Median/2 Q1= Qth element in first half
		 * Q3= Qth elment in second half Lower Threshhold= Q1-1.5(Q3-Q1) Upper
		 * Threshhold=Q3+1.5(Q3-Q1)
		 */

		int median = intlist.size() / 2;
		int q = median / 2;
		Float q1 = intlist.get(q);
		Float q3 = intlist.get(q * 3);
		lt = (float) (q1 - (1.5 * (q3 - q1)));
		ut = (float) (q3 + (1.5 * (q3 - q1)));

		System.out.println("Lower Threshhold: " + lt + " Upper Threshhold: " + ut);

	}

	public void removeOutlier() {
		intlist.forEach((n) -> {
			if (n <= lt || n >= ut) {
				index.add(intlist.indexOf(n));
			}
		});
		index.forEach((n) -> {
			intlist.remove(intlist.get(n));
			intlist.add(n, (float) 0);
		});
		System.out.println(intlist);
		efficiencylist = intlist.stream().filter(i -> !(i <= 0)).collect(Collectors.toList());
		nCrCombinations();

	}

	public void nCrCombinations() {

		intlist.forEach((l) -> {
			if (l != 0) {
				n++;
			}
		});
		r = Framework.Reducer;

		// Combination combination = Combination.getInstance();
		combination.buildCombination((ArrayList<Float>) efficiencylist, n, r);
		System.out.println("===================== Builded Combinations ==================================");
		combination.getColumnCombs().forEach(columnComb -> {
			System.out.println(columnComb.getCombinations());
		});

		System.out.println(combination.getColumnCombs().size());

	}

	@SuppressWarnings("unchecked")
	public void taskAssignment() {

		/*
		 * 1. Find Collections.max in every queue of one mapper and store in
		 * mapper_max arraylist 2. Find the biggest from mapper_max arraylist
		 * and store in allmapper_max arraylist 3. Pick the biggest coflow from
		 * allmapper_max arraylist and find biggest flow in it. 4. From full
		 * combine array pick first r elements and store in current_reducers
		 * arraylist 5. Send biggest flow to biggest in current_reducers. 6.
		 * Delete the biggest flow from the coflow 7. Repeat process 5-6 untill
		 * coflow empty
		 */

		// loop untill flowsize.size()==0

		optimalThreshhold();
		removeOutlier();
		
		calculateArrays();// Gives us current arrays

		combination.getColumnCombs().forEach(columnComb -> {
			//Here assign each element of columnComb to Reducer
			//Pass each element of columnCombs to graph
			inputreducer=(ArrayList<Float>) columnComb.getCombinations().clone();
			for(reducer=0;reducer<((Framework.Reducer));reducer++)
			{
			 System.out.println(inputreducer.get(reducer));
			 Graph.all_reducers[reducer+1].cpu_pw=inputreducer.get(reducer);	
			}
			//Run untill all mappers are empty
			while(mappers!=null){
			calculateArrays();
			while (Collections.frequency(mapper_max, 0) != mapper_max.size()) {

				// calculateArrays();
				coflow_max = Collections.max(mapper_max);
				workingset = (ArrayList<Float>) columnComb.getCombinations().clone();

				System.out.println("This is working set " + workingset);
				System.out.println(mapper_max);
				System.out.println(coflow_max);
				System.out.println("These are flow sizes " + flowsize.get(coflow_max));

				while ((flowsize.get(coflow_max)) != null) {
					if ((flowsize.get(coflow_max)).isEmpty()) {
						break;
					}

					current_reducers = new ArrayList<Integer>();
					// calculateArrays(); //Gives us current arrays

					// coflow_max= Collections.max(mapper_max);
					System.out.println(coflow_max);
					System.out.println(flowsize.get(coflow_max));

					flow_max = Collections.max(flowsize.get(coflow_max));
					System.out.println(flow_max);

					// Match up with full combine array and send to
					// reducer(reducer can receieve values untill size of core)
					System.out.println("Max Working  Set " + workingset);
					if (workingset.isEmpty()) {
						// wait till process finish
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						maxcombination = (Float) Collections.max(workingset);
					}
					// Assign each maxcombination flows equivalent to number of
					// cores
					List<EnergyLogCSV> energyLogs = new ArrayList<>();
					for (int assign = 0; assign <= Reducer.cores; assign++) {
						if (flowsize.get(coflow_max).isEmpty()) {
						} else {
							flow_max = Collections.max(flowsize.get(coflow_max));
							System.out.println(flow_max);
							System.out.println("Flowsizes " + flowsize.get(coflow_max));
							// Calculate how much energy of maxcombination is
							// used by the flowsize
							energyusage = (maxcombination) * (flow_max);
							Avg_energyusage = Avg_energyusage + energyusage;
							System.out.println("Energy Useage by " + flow_max + " is " + energyusage);
							flowsize.get(coflow_max).remove(flowsize.get(coflow_max).indexOf(flow_max));
							System.out.println("Assign " + flow_max + " to " + maxcombination);
							
						}
						//Find in which reducer maxcombination exist
						for(reducer=0;reducer<((Framework.Reducer));reducer++)
						{
						 System.out.println(inputreducer.get(reducer));
						 if(Graph.all_reducers[reducer+1].cpu_pw==maxcombination){
							 System.out.println(maxcombination+" energy belongs to reducer "+reducer);
							 
						 }
						}
						// Enter values coflow_max and Avg_energyusage in a csv
						// file
						// Enter to csv two values
						// AVGEnergyUasge
						// COFLOWMax					
					}
					energyLogs.add(new EnergyLogCSV(String.valueOf(coflow_max), String.valueOf(Avg_energyusage),
							Constants.Algo.EEA));
					if (!energyLogs.isEmpty()) {
						String fileName = Constants.File.getFileName(Constants.File.FILE_NAME_ENERGY);
						File file = Constants.File.getFile(Constants.File.FILE_NAME_ENERGY);
						boolean append = Constants.File.isFileExists(file);
						StringJoiner csvData = new StringJoiner(",\n");
						if(!append)
							csvData.add(EnergyLogCSV.getHeaderCSV());
						Path path = Paths.get(fileName.replace("\\", "/"));
						energyLogs.stream().map(el -> el.toCsvString()).forEach(csvData::add);
						try {
							Files.write(path, (csvData.toString()+System.lineSeparator()).getBytes(StandardCharsets.UTF_8), file.exists()?StandardOpenOption.APPEND : StandardOpenOption.CREATE_NEW);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  //Append mode
						
					}
					workingset.remove(workingset.indexOf(maxcombination));
					System.out.println(workingset);

				}

				System.out.println("Index Mapper data = " + Arrays.asList(mappers));
				System.out.println("Index Value wiht maxcoflow " + mapper_max.indexOf(coflow_max));

				/*
				 * mappers[mapper_max.indexOf(coflow_max)].mapper_queue.forEach(
				 * (q)->{ System.out.println("Looped Queue = "+q);
				 * System.out.println("Coflow Max = "+coflow_max);
				 * if(q.contains(coflow_max)){
				 * System.out.println("removed coflow = "+coflow_max);
				 * q.remove(coflow_max);
				 * System.out.println("I have deleted coflow "+coflow_max);} });
				 */
				mappers[mapper_max.indexOf(coflow_max)].mapper_queue.stream().filter(q -> q.contains(coflow_max))
						.findAny().ifPresent(q -> {
							q.remove(coflow_max);
						});
				/**
				 * List removal types 1-> index 2 -> element
				 */
				/*
				 * if(mapper_max.contains(coflow_max)){
				 * mapper_max.remove(coflow_max); }
				 */

				int in = mapper_max.indexOf(coflow_max);
				mapper_max.set(in, 0);
				// mapper_max.remove(in);
				// mapper_max.add(in,0);
			}
			//Find all queues of mapper are empty and then remove mapper from mappers
			for(n=0;n<Framework.Mapper;n++){
				for(r=0;r<Framework.queuesinmapper;r++){
					if(mappers[n].mapper_queue.get(r).isEmpty()){
						mappers[n].mapper_queue.get(r).remove();
					}
				}
				if(mappers[n].mapper_queue.isEmpty()){
			      mappers[n]=null;  
				}
			}
			}
		});
		}
	

	public void calculateArrays() {

		queue_max = new ArrayList<Integer>();
		mapper_max = new ArrayList<Integer>();

		for (int map = 0; map < Framework.Mapper; map++) { // loop for mapper

			for (int queue = 0; queue < Framework.queuesinmapper; queue++) { // loop
																				// for
																				// queue

				PriorityQueue<Integer> completequeue = mappers[map].mapper_queue.get(queue);
				queue_max.add(Collections.max(completequeue));

			}
			mapper_max.add(Collections.max(queue_max));
			queue_max.remove(queue_max.indexOf(Collections.max(queue_max)));
		}
		System.out.println("Thses are mappers highest coflows " + mapper_max);
	}

}
