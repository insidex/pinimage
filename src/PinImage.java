import com.www1develop.MyApp;
import com.www1develop.io.Debugger;
import com.www1develop.threads.ClassicRunner;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by ilya on 20.03.14.
 */
public class PinImage extends MyApp{
    public PinConfig config;
    public Properties props;

    @Override
    protected void setLogger(boolean debug, String loggerName) {
        super.setLogger(debug, loggerName);
        if(!debug)
            System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }

    public static void main(String[] args) {

        PinImage pinImage = new PinImage();

        try{
            pinImage.loadConfig(new PinConfig(args));
            pinImage.setLogger(PinConfig.Config.pdfDebugEnabled, "PinImage");
        }catch (IOException e){
            System.out.println(e);
            MyApp.exitApp("config load error " + e.getMessage());
        }

        try {
            PinManager pinManager = new PinManager();
            logger.info("Try to process: " + PinManager.size() + " bills in " + pinManager.adjustThreadNumber() + " threads");
            pinManager.runAll();
            pinManager.killTimer();
            pinManager.waitAll();
            pinManager.close();
            logger.info(String.format("Complete %d pdf making in: %.2fs", PinManager.size(), pinManager.getExecutionTime()/1e9));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
