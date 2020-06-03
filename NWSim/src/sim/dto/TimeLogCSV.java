package sim.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

import sim.utils.Constants;

public class TimeLogCSV implements Serializable{

	private static final long serialVersionUID = 4554123994899336646L;
	private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
	private String awt;
	private String act;
	private String algo;
	
	
	public TimeLogCSV(String awt, String act, String algo) {
		this.awt = awt;
		this.act = act;
		this.algo = algo;
	}
	
	
	public TimeLogCSV() {
		
	}


	public String getAwt() {
		return awt;
	}
	public void setAwt(String awt) {
		this.awt = awt;
	}
	public String getAct() {
		return act;
	}
	public void setAct(String act) {
		this.act = act;
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
		stringJoiner.add(awt);
		stringJoiner.add(act);
		stringJoiner.add(LocalDateTime.now().format(dateTimeFormat));
		return stringJoiner.toString();
	}
	
	public static String getHeaderCSV(){
		StringJoiner stringJoiner = new StringJoiner(",");
		stringJoiner.add(Constants.Output.ALGORITHM);
		stringJoiner.add(Constants.Output.AWT);
		stringJoiner.add(Constants.Output.ACT);
		stringJoiner.add(Constants.Output.DATE_TIME);
		return stringJoiner.toString();
	}
	
	@Override
	public String toString() {
		return "TimeLogCSV [awt=" + awt + ", act=" + act + ", algo=" + algo +"]";
	}
		
}
