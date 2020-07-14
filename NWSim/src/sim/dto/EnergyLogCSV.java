package sim.dto;

import java.io.Serializable;
import java.util.StringJoiner;

import sim.utils.Constants;

public class EnergyLogCSV implements Serializable{

	private static final long serialVersionUID = -8841205687658610464L;

	private String coflow_max;
	private String avg_energyusage;
	private String algo;
		
	public EnergyLogCSV(String coflow_max, String avg_energyusage, String algo) {
		this.coflow_max = coflow_max;
		this.avg_energyusage = avg_energyusage;
		this.algo = algo;
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
	
	public String toCsvString(){
		StringJoiner stringJoiner = new StringJoiner(",");
		stringJoiner.add(algo);
		stringJoiner.add(coflow_max);
		stringJoiner.add(avg_energyusage);
		return stringJoiner.toString();
	}
	
	public static String getHeaderCSV(){
		StringJoiner stringJoiner = new StringJoiner(",");
		stringJoiner.add(Constants.Output.ALGORITHM);
		stringJoiner.add(Constants.Output.COFLOW);
		stringJoiner.add(Constants.Output.ENERGY);
		return stringJoiner.toString();
	}
	
	@Override
	public String toString() {
		return "EnergyLogCSV [Coflow=" + coflow_max + ", Energy=" + avg_energyusage + ", algo=" + algo +"]";
	}
		
}