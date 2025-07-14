import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.starling.LogMonitor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LogMonitorTest {

    private static final String TEST_LOG = "test_logs.log";
    private static final String OUTPUT_LOG = TEST_LOG + "_output.log";

    @BeforeEach
    public void setup() throws IOException {

        // A log file with one job complete and one in complete
        String logContent = String.join("\n",
                "12:00:00, JobA, START, 1001",
                "12:04:00, JobA, END, 1001",
                "12:10:00, JobB, START, 1002"
        );
        // creates test log
        Files.write(Paths.get(TEST_LOG), logContent.getBytes());
    }

    @AfterEach
    public void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_LOG));
        Files.deleteIfExists(Paths.get(OUTPUT_LOG));
    }

    @Test
    public void testGenerateReport_createsCorrectOutput() throws IOException {
        LogMonitor monitor = new LogMonitor();

        // To process the log file
        monitor.processLogFile(TEST_LOG);
        // To generate the output log file
        monitor.generateReport(TEST_LOG);

        assertTrue(Files.exists(Paths.get(OUTPUT_LOG)), "Output file should be created");

        String output = String.join("\n", Files.readAllLines(Paths.get(OUTPUT_LOG)));

        // Check output contains expected job info lines
        assertTrue(output.contains("OK: Job 'JobA' (PID: 1001) took 04:00"), "Should report JobA as OK");
        assertTrue(output.contains("Incomplete job JobB (PID: 1002)"), "Should report JobB as incomplete");
    }
}