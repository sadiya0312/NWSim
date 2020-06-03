/**
 * 
 */
package sim.dto;

import java.io.Serializable;

/**
 * @author Sadiya
 *
 */
public class TimelogDTO implements Serializable {

	private static final long serialVersionUID = 4686332645878345726L;
	private String value;
	private String algo;
	private TimeLogType logtype = TimeLogType.WAITING_TIME;
	
	
	
	public TimelogDTO(String value, String algo, TimeLogType logtype) {
		this.value = value;
		this.algo = algo;
		this.logtype = logtype;
	}
	
	
	public TimelogDTO() {
		
	}


	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getAlgo() {
		return algo;
	}
	public void setAlgo(String algo) {
		this.algo = algo;
	}
	public TimeLogType getLogtype() {
		return logtype;
	}
	public void setLogtype(TimeLogType logtype) {
		this.logtype = logtype;
	}
	@Override
	public String toString() {
		return "TimelogDTO [value=" + value + ", algo=" + algo + ", logtype=" + logtype + "]";
	}
	
	
	
	

}
