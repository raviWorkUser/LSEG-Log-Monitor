import java.io.*;
import java.time.LocalTime;
import java.util.*;

/**
 * The LogMonitor class processes a log file containing job start and end events,
 * tracks job durations, and generates a report.
 */
 public class LogMonitor {

    private static final String FILE_NAME = "logs.log";
    private static final int WARNING_THRESHOLD_SECONDS = 300;  // 5 minutes
    private static final int ERROR_THRESHOLD_SECONDS = 600;    // 10 minutes

    private Map<String, Job> jobMap = new HashMap<>(); // a map to store all the jobs by pid

    public static void main(String[] args) {
        LogMonitor monitor = new LogMonitor();
        monitor.processLogFile(FILE_NAME);
        monitor.generateReport(FILE_NAME);
    }

    /**
     * To process log file
     * @param fileName
     */
    public void processLogFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 4);

                // if the log file is not correctly formatted
                if (parts.length < 4) continue;

                LocalTime time = LocalTime.parse(parts[0]);
                String description = parts[1].trim();
                JobStatus status = JobStatus.fromString(parts[2].trim());
                String pid = parts[3].trim();

                // if a job already exists then get it or create a new job
                Job job = jobMap.getOrDefault(pid, new Job(description, pid));

                if (status == JobStatus.START) {
                    job.setStartTime(time);
                } else {
                    job.setEndTime(time);
                }

                jobMap.put(pid, job);
            }
        } catch (IOException e) {
            System.err.println("Error reading the log file: " + e.getMessage());
        }
    }

    /**
     * To generate report that:
     *  Logs a warning if a job took longer than 5 minutes.
     *  Logs an error if a job took longer than 10 minutes.
     */
    public void generateReport(String inputFileName) {
        String outputFileName = inputFileName + "_output.log";
        File outputFile = new File(outputFileName);

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFileName))) {
            for (Job job : jobMap.values()) {
                // The job does not have end time or start time
                if (!job.isComplete()) {
                    writer.printf("Incomplete job %s (PID: %s)%n", job.getDescription(), job.getPid());
                    continue;
                }

                long duration = job.getDurationInSeconds();
                String durationStr = formatDuration(duration);

                if (duration > ERROR_THRESHOLD_SECONDS) {
                    writer.printf("ERROR: Job '%s' (PID: %s) took %s%n", job.getDescription(), job.getPid(), durationStr);
                } else if (duration > WARNING_THRESHOLD_SECONDS) {
                    writer.printf("WARNING: Job '%s' (PID: %s) took %s%n", job.getDescription(), job.getPid(), durationStr);
                } else {
                    writer.printf("OK: Job '%s' (PID: %s) took %s%n", job.getDescription(), job.getPid(), durationStr);
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing the report file: " + e.getMessage());
        }
    }

    private String formatDuration(long seconds) {
        long mins = seconds / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }
}
