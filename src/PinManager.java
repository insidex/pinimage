import com.www1develop.threads.ClassicRunner;
import com.www1develop.util.ZFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * PinManager manipulates PinRunner's objects in different threads.
 *
 * @author Ilya Zukhta (mail*AT*1develop.com)
 * @since 1.0
 */
public class PinManager {
    public static List<Pins> pins = new ArrayList<Pins>();
    private ClassicRunner runner;

    /**
     * Construct manager of pins to run it in multiple threads
     */
    public PinManager() {
        pins = genPins(PinConfig.Config.pdfDirectory);
    }

    /**
     * Create number of PinRunner objects that will be run in different threads
     */
    public void runAll() {
        runner = new ClassicRunner();
        int end;
        int start;
        int perThread = pins.size() / adjustThreadNumber();
        for (int i = 0; i < pins.size(); i += perThread) {
            start = i;
            if (i + perThread < pins.size())
                end = i + perThread - 1;
            else
                end = pins.size() - 1;
            runner.add(new PinRunner(start, end));
        }
        runner.runAll();
    }

    /**
     * Create timer that will kill all started threads after specific time
     */
    public void killTimer(){
        if(PinConfig.Config.pdfMaxTimeExecute > 0)
            runner.killTimer(PinConfig.Config.pdfMaxTimeExecute);
    }

    /**
     * Suspend main thread until all tasks finish the job
     * @throws InterruptedException
     */
    public void waitAll() throws InterruptedException{
        int complete;
        int percent;
        char[] progress = {'|', '/', '-', '|', '/', '-', '\\'};
        int n = 0;
        while(!runner.allDone()){
            TimeUnit.MILLISECONDS.sleep(300);
            complete = 0;
            for(Pins pin : pins)
                if(pin.isProcessed)
                    complete++;

            percent = (complete * 100) / pins.size();
            n = (n > 5 ? 0 : n+1);
            System.out.format("[%c] Done: %4d%%\r", progress[n],percent);
        }
        System.out.println();
        System.out.flush();
        TimeUnit.MILLISECONDS.sleep(200);
    }

    /**
     * Cleaning in the end of all jobs
     */
    public void close() {
        //
    }

    /**
     * Return size of the tasks
     * @return number of pins
     */
    public static int size() {
        return pins.size();
    }

    /**
     * Return duration of all task executing
     * @return number of nanoseconds after job finish
     */
    public long getExecutionTime() {
        return runner.getStopTime() - runner.getStartTime();
    }

    /**
     * Correct number of threads to be launched (N - number of runnable objects)
     * @return threads > N/2 ? N/2 : threads
     */
    public int adjustThreadNumber() {
        int pdfMaxThreadNumber = PinConfig.Config.pdfMaxThreadNumber;
        int sizeData = pins.size();
        if(pdfMaxThreadNumber < 1)
            return 1;
        else if(pdfMaxThreadNumber > (sizeData / 2 + 1 ))
            return sizeData / 2;
        else
            return pdfMaxThreadNumber;
    }

    /**
     * Create list of pins with initial information.
     * @param directory pdf input directory
     * @return list of pins
     */
    private List<Pins> genPins(String directory) {
        int max = PinConfig.Config.pdfMaxFilesProcess;
        for(String file : ZFile.getFileList(directory, ".*\\.pdf$"))
            if(PinConfig.Config.pdfMaxFilesProcess != 0 && --max < 0 ) break;
            else pins.add(new Pins(file));
        return pins;
    }

}
