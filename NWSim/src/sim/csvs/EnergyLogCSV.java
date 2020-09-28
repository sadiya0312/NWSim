package sim.csvs;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.StringJoiner;

import sim.utils.Constants;

public class EnergyLogCSV implements CsvContractNoOfRuns{

	private static final long serialVersionUID = -8841205687658610464L;
	private static final DateTimeFormatter DATE_TIME_FORMATER = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
	

	private String algo;
	private String numberOfRuns;
	private String coflow_max;
	private String flow_max;
	private String avg_energyusage;
	private String efficiency;
	private String reducer;
	private String gigaflops;
	private String watts;



	/**
	 * @param algo
	 * @param coflow_max
	 * @param avg_energyusage
	 * @param efficiency
	 * @param reducer
	 * @param reducerprop
	 */
	public EnergyLogCSV(ArrayList<Float> reducerprop, String algo, String numberOfRuns , String coflow_max, String flow_max, String avg_energyusage, String efficiency, String reducer) {
		this.algo = algo;
		this.numberOfRuns = numberOfRuns;
		this.coflow_max = coflow_max;
		this.flow_max=flow_max;
		this.avg_energyusage = avg_energyusage;
		this.efficiency = efficiency;
		this.reducer = reducer;
		this.gigaflops=String.valueOf(reducerprop.get(0));
		this.watts=String.valueOf(reducerprop.get(1));
	}

	public EnergyLogCSV(String algo, String coflow_max, String flow_max, String avg_energyusage, String efficiency, String reducer) {
		this.algo = algo;
		this.coflow_max = coflow_max;
		this.flow_max = flow_max;
		this.avg_energyusage = avg_energyusage;
		this.efficiency = efficiency;
		this.reducer = reducer;
	}

	public EnergyLogCSV() {
		
	}


	public String getAwt() {
		return coflow_max;
	}
	public void setAwt(String coflow_max) {
		this.coflow_max = coflow_max;
	}
	public String getAct() {
		return avg_energyusage;
	}
	public void setAct(String avg_energyusage) {
		this.avg_energyusage = avg_energyusage;
	}
	public String getAlgo() {
		return algo;
	}
	public void setAlgo(String algo) {
		this.algo = algo;
	}

	public String getGigaflops() {
		return gigaflops;
	}
	public void setGigaflops(String gigaflops) {
		this.gigaflops = gigaflops;
	}

	public String getWatts() {
		return watts;
	}
	public void setWatts(String watts) {
		this.watts = watts;
	}

	public String getNumberOfRuns() {
		return numberOfRuns;
	}

	public void setNumberOfRuns(String numberOfRuns) {
		this.numberOfRuns = numberOfRuns;
	}

	public String toCsvString(){
		StringJoiner stringJoiner = new StringJoiner(",");
		stringJoiner.add(LocalDateTime.now().format(DATE_TIME_FORMATER));
		stringJoiner.add(algo);
		stringJoiner.add(coflow_max);
		stringJoiner.add(flow_max);
		stringJoiner.add(avg_energyusage);
		stringJoiner.add(efficiency);
		stringJoiner.add(reducer);
		stringJoiner.add(gigaflops);
		stringJoiner.add(watts);
		return stringJoiner.toString();
	}



	public String toCsvStringWithNoOfRuns(){
		StringJoiner stringJoiner = new StringJoiner(",");
		stringJoiner.add(LocalDateTime.now().format(DATE_TIME_FORMATER));
		stringJoiner.add(algo);
		stringJoiner.add(coflow_max);
		stringJoiner.add(flow_max);
		stringJoiner.add(avg_energyusage);
		stringJoiner.add(efficiency);
		stringJoiner.add(reducer);
		stringJoiner.add(numberOfRuns);
		stringJoiner.add(gigaflops);
		stringJoiner.add(watts);
		return stringJoiner.toString();
	}
	
	public static String getHeaderCSV(){
		StringJoiner stringJoiner = new StringJoiner(",");
		stringJoiner.add(TIMESTAMP);
		stringJoiner.add(ALGORITHM);
		stringJoiner.add(COFLOW);
		stringJoiner.add(FLOW);
		stringJoiner.add(ENERGY);
		stringJoiner.add(EFFICIENCY);
		stringJoiner.add(REDUCER);
		return stringJoiner.toString();
	}

	public static String getHeaderCSVWithNoOfRuns(){
		StringJoiner stringJoiner = new StringJoiner(",");
		stringJoiner.add(TIMESTAMP);
		stringJoiner.add(ALGORITHM);
		stringJoiner.add(COFLOW);
		stringJoiner.add(FLOW);
		stringJoiner.add(ENERGY);
		stringJoiner.add(EFFICIENCY);
		stringJoiner.add(REDUCER);
		stringJoiner.add(NUMBER_OF_RUNS);
		stringJoiner.add(GIGAFLOPS);
		stringJoiner.add(WATTS);
		return stringJoiner.toString();
	}

	@Override
	public String toString() {
		return "EnergyLogCSV{" +
				"algo='" + algo + '\'' +
				", numberOfRuns='" + numberOfRuns + '\'' +
				", coflow_max='" + coflow_max + '\'' +
				", flow_max='" + flow_max + '\'' +
				", avg_energyusage='" + avg_energyusage + '\'' +
				", efficiency='" + efficiency + '\'' +
				", reducer='" + reducer + '\'' +
				", gigaflops='" + gigaflops + '\'' +
				", watts='" + watts + '\'' +
				'}';
	}

	@Override
	public String toCsvNoOfRunsString() {
		StringJoiner stringJoiner = new StringJoiner(",");
		stringJoiner.add(LocalDateTime.now().format(DATE_TIME_FORMATER));
		stringJoiner.add(algo);
		stringJoiner.add(coflow_max);
		stringJoiner.add(flow_max);
		stringJoiner.add(avg_energyusage);
		stringJoiner.add(efficiency);
		stringJoiner.add(reducer);
		stringJoiner.add(numberOfRuns);
		stringJoiner.add(gigaflops);
		stringJoiner.add(watts);
		return stringJoiner.toString();
	}
}