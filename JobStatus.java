/**
 * Represents the status of a job in the log: either START or END.
 */
public enum JobStatus {
    START, END;

    public static JobStatus fromString(String value) {
        return value.equalsIgnoreCase("START") ? START : END;
    }
}