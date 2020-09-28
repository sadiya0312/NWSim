package sim.dto;


import java.io.Serializable;
import java.util.Objects;

public class JobLogDTO implements Serializable {

    private String jobId;
    private String jobTime;
    private String  algorithm;

    public JobLogDTO(String jobId, String jobTime, String algorithm) {
        this.jobId = jobId;
        this.jobTime = jobTime;
        this.algorithm = algorithm;
    }

    public JobLogDTO() {
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobTime() {
        return jobTime;
    }

    public void setJobTime(String jobTime) {
        this.jobTime = jobTime;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobLogDTO jobLogDTO = (JobLogDTO) o;
        return Objects.equals(jobId, jobLogDTO.jobId) &&
                Objects.equals(jobTime, jobLogDTO.jobTime) &&
                Objects.equals(algorithm, jobLogDTO.algorithm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId, jobTime, algorithm);
    }

    @Override
    public String toString() {
        return "JobLogDTO{" +
                "jobId='" + jobId + '\'' +
                ", jobTime='" + jobTime + '\'' +
                ", algorithm='" + algorithm + '\'' +
                '}';
    }
}
