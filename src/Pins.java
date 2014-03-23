/**
 * Pins class is a basic holder for pdf file.
 *
 * @author Ilya Zukhta (mail*AT*1develop.com)
 * @since 1.0
 */
public class Pins {
    private String fileIN;
    private Status status;
    public static enum Status {PREPARE, IMAGE_PLACED_OK, IMAGE_PLACED_ERROR}
    private static int counter_class = 0;
    private final int id = counter_class++;

    /**
     * Construct base pdf container objects
     * @param fileIN Input pdf file
     */
    public Pins(String fileIN) {
        this.fileIN = fileIN;
    }

    /**
     * Return status of currently processed holder
     * @return enum Status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set status of currently holder
     * @param status    enum Status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Return input pdf file
     * @return input pdf file
     */
    public String getFileIN() {
        return fileIN;
    }

    @Override
    public String toString() {
        return ("pin id: " + id + ", file: " + fileIN);
    }
}
