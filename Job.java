import java.time.LocalTime;

/**
 * Represents a job with a unique process ID, description, and start/end times.
 */
public class Job {
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
    private String pid;

    public Job(String description, String pid) {
        this.description = description;
        this.pid = pid;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getPid() {
        return pid;
    }

    public String getDescription() {
        return description;
    }

    public boolean isComplete() {
        return startTime != null && endTime != null;
    }

    public long getDurationInSeconds() {
        return java.time.Duration.between(startTime, endTime).getSeconds();
    }
}