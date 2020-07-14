package sim.dto;

import java.io.Serializable;

/**
 * @author Sadiya
 *
 */
public class EnergyLogDTO implements Serializable {


	private static final long serialVersionUID = -4736513176908483711L;	
	private String coflow_max;
	private String avg_energyusage;
	private TimeLogType logtype;
	
	
	
	public EnergyLogDTO(String coflow_max, String avg_energyusage, TimeLogType logtype) {
		this.coflow_max = coflow_max;
		this.avg_energyusage = avg_energyusage;
		this.logtype = logtype;
	}
	
	
	public EnergyLogDTO() {
		
	}

	public String getValue() {
		return coflow_max;
	}
	public void setValue(String coflow_max) {
		this.coflow_max = coflow_max;
	}
	public String getAlgo() {
		return avg_energyusage;
	}
	public void setAlgo(String avg_energyusage) {
		this.avg_energyusage = avg_energyusage;
	}
	public TimeLogType getLogtype() {
		return logtype;
	}
	public void setLogtype(TimeLogType logtype) {
		this.logtype = logtype;
	}
	@Override
	public String toString() {
		return "TimelogDTO [value=" + coflow_max + ", algo=" + avg_energyusage + ", logtype=" + logtype + "]";
	}
	
}
