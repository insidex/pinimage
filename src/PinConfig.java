import com.www1develop.AppConfig;
import com.www1develop.MyApp;
import com.www1develop.exceptions.IncorrectConfigurationException;
import com.www1develop.util.ZFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

/**
 * PinConfig loading configuration and make checking.
 */
public class PinConfig extends AppConfig {

    public PinConfig(String[] args) {
        super(args);
    }

    static class Config{
        static String pdfDirectory = "";
        static String pdfDirectoryOut = "";
        static int pdfMaxThreadNumber = 1;
        static int pdfMaxFilesProcess = 0;
        static int pdfMaxTimeExecute = 60;
        static boolean pdfDebugEnabled = false;
        static String pdfTextMarker = "";
        static boolean pdfTextSearchExactly = true;
        static String pdfTextSearchOrder = "";
        static String pdfMaketStamp = "";
        static String pdfImagePages = "";
        static int pdfMaketTransparency = 100;
        static float pdfTextMarkerAddX = 0.0f;
        static float pdfTextMarkerAddY = 0.0f;

    }


    private void readProperties() throws IncorrectConfigurationException{
        try {
            Config.pdfDirectory         = properties.getProperty("pdfDirectory", "");
            Config.pdfDirectoryOut      = properties.getProperty("pdfDirectoryOut", "");
            Config.pdfMaxThreadNumber   = Integer.parseInt(properties.getProperty("pdfMaxThreadNumber", "1"));
            Config.pdfMaxFilesProcess   = Integer.parseInt(properties.getProperty("pdfMaxFilesProcess", "1000"));
            Config.pdfMaxTimeExecute    = Integer.parseInt(properties.getProperty("pdfMaxTimeExecute", "3600"));
            Config.pdfDebugEnabled      = properties.getProperty("pdfDebugEnabled", "false").equals("true");
            Config.pdfTextMarker        = properties.getProperty("pdfTextMarker","");
            Config.pdfTextSearchExactly = properties.getProperty("pdfTextSearchExactly", "true").equals("true");
            Config.pdfTextSearchOrder   = properties.getProperty("pdfTextSearchOrder", "last");
            Config.pdfMaketStamp        = properties.getProperty("pdfMaketStamp", "");
            Config.pdfImagePages        = properties.getProperty("pdfImagePages", "last");
            Config.pdfMaketTransparency = Integer.parseInt(properties.getProperty("pdfMaketTransparency", "100"));
            Config.pdfTextMarkerAddX    = Float.parseFloat(properties.getProperty("pdfTextMarkerAddX", "0"));
            Config.pdfTextMarkerAddY    = Float.parseFloat(properties.getProperty("pdfTextMarkerAddY", "0"));
        } catch (Exception e) {
            throw new IncorrectConfigurationException(e.getMessage());
        }
    }

    private void checkProperties() throws IncorrectConfigurationException{
        try{
            if(!ZFile.isDir(Config.pdfDirectory))
                throw new IncorrectConfigurationException("Config.pdfDirectory: " + Config.pdfDirectory + " not found");
            if(!ZFile.isDir(Config.pdfDirectoryOut))
                throw new IncorrectConfigurationException("Config.pdfDirectoryOut: " + Config.pdfDirectoryOut + " not found");
            if(Config.pdfMaxThreadNumber < 1 || Config.pdfMaxThreadNumber > 32)
                throw new IncorrectConfigurationException("Config.pdfMaxThreadNumber: " + Config.pdfMaxThreadNumber + " have to be between 1 and 32");
            if(Config.pdfMaxFilesProcess < 0 || Config.pdfMaxFilesProcess > 100000)
                throw new IncorrectConfigurationException("Config.pdfMaxFilesProcess: " + Config.pdfMaxFilesProcess + " have to be between 0 and 1e5");
            if(Config.pdfMaxTimeExecute < 0 || Config.pdfMaxTimeExecute > 28800)
                throw new IncorrectConfigurationException("Config.pdfMaxFilesProcess: " + Config.pdfMaxFilesProcess + " have to be between 0 and 28800");
            if(!ZFile.isFile(Config.pdfMaketStamp))
                throw new IncorrectConfigurationException("Config.pdfMaketStamp: " + Config.pdfMaketStamp + " not found");
            if(Config.pdfMaketTransparency < 0 || Config.pdfMaketTransparency > 100)
                throw new IncorrectConfigurationException("Config.pdfMaketTransparency: " + Config.pdfMaketTransparency + " have to be between 0 and 100");
        }catch (Exception e){
            throw new IncorrectConfigurationException(e.getMessage());
        }
    }



    @Override
    public void load() throws IOException {
        if(args.length > 0)
            super.loadPropertiesFile(args[0]);
        else
            printUsage();

        readProperties();
        checkProperties();
    }


    public void printUsage() {
        System.out.println("Usage: >java PinImage configuration");
        MyApp.exitApp("");
    }


    public static void main(String[] args) {
        PinImage pinImage = new PinImage();

        System.out.println("Test PinConfig()");
        try{
            pinImage.loadConfig(new PinConfig(args));

        }catch (Exception e){
            System.out.println(e);
            MyApp.exitApp("config load error " + e.getMessage());
        }
        System.out.println("Config loaded!");
    }

}
