import com.www1develop.MyApp;

import java.io.IOException;
import java.util.Locale;

/**
 * PinImage allows you to find some pattern in PDF file and place image near with it.
 * For example if you want to place a sign or stamp layer to a lot of PDF files,
 * that could save your time and some nervous.
 *
 * Would like to say thanks to the Apache foundation for their opensource library PDFBox and other great tools.
 *
 * @author Ilya Zukhta (mail*AT*1develop.com)
 * @since 1.0
 */
public class PinImage extends MyApp{

    @Override
    protected void setLogger(boolean debug, String loggerName) {
        super.setLogger(debug, loggerName);
        if(!debug)
            System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }

    /**
     * It is a starting point of the program. Call with argument pointed to configuration file.
     * @param args  First argument should be the path to the properties file
     */
    public static void main(String[] args) {

        PinImage pinImage = new PinImage();

        try{
            pinImage.loadConfig(new PinConfig(args));
            pinImage.setLogger(PinConfig.Config.pdfDebugEnabled, "PinImage");
        }catch (IOException e){
            MyApp.exitApp("Exit. Config loading error: " + e.getMessage());
        }

        try {
            PinManager pinManager = new PinManager();
            logger.info(String.format("Try to process %d files in %d threads...", PinManager.size(), pinManager.adjustThreadNumber()));
            pinManager.runAll();
            pinManager.killTimer();
            pinManager.waitAll();
            pinManager.close();
            logger.info(String.format("Complete all tasks, %d pdf files in: %.2f s; average: %.2f pdf/s", PinManager.size(), pinManager.getExecutionTime()/1e9, (PinManager.size() / (pinManager.getExecutionTime()/1e9))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
