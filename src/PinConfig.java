import com.www1develop.AppConfig;
import com.www1develop.MyApp;
import com.www1develop.exceptions.IncorrectConfigurationException;
import com.www1develop.util.ZFile;

import java.io.IOException;
import java.util.LinkedList;

/**
 * PinConfig try to load and check user configuration.
 *
 * @author Ilya Zukhta (mail*AT*1develop.com)
 * @since 1.0
 */
public class PinConfig extends AppConfig {

    public static enum CMD{PROPERTIES, PARSE}

    public PinConfig(String[] args) {
        super(args);
    }

    static class ConfigCMD{
        static CMD cmd;
        static LinkedList<String> options;
    }

    /**
     * Load default value if someone miss in configuration
     */
    static class Config{
        static String   pdfDirectory          = "";
        static String   pdfDirectoryOut       = "";
        static int      pdfMaxThreadNumber    = 1;
        static int      pdfMaxFilesProcess    = 0;
        static int      pdfMaxTimeExecute     = 60;
        static boolean  pdfDebugEnabled       = false;
        static String   pdfTextMarker         = "";
        static boolean  pdfTextSearchExactly  = true;
        static String   pdfTextSearchOrder    = "last";
        static String   pdfMaketStamp         = "";
        static String   pdfImagePages         = "";
        static int      pdfMaketTransparency  = 100;
        static float    pdfTextMarkerAddX     = 0.0f;
        static float    pdfTextMarkerAddY     = 0.0f;

    }

    /**
     * Tryto load properties file or print usage information in case of failure.
     * @throws IOException
     */
    @Override
    public void load() throws IOException {
        if(args.length > 0) {
            if(args.length == 1) {
                ConfigCMD.cmd = CMD.PROPERTIES;
                super.loadPropertiesFile(args[0]);
                readProperties();
                checkProperties();
            }else{
                if(args[0].equals("cmd") && args.length > 1) {
                    if (args[1].equals("parse")) {
                        ConfigCMD.cmd = CMD.PARSE;
                        ConfigCMD.cmd = CMD.PARSE;
                        ConfigCMD.options = new LinkedList<String>();
                        for (int i = 1; i < args.length; i++) {
                            ConfigCMD.options.add(args[i]);
                        }
                    }
                }else{
                    printUsage();
                }
            }

        }else
            printUsage();

    }

    /**
     * Try to read properties from file or set to default value.
     * @throws IncorrectConfigurationException
     */
    private void readProperties() throws IncorrectConfigurationException{
        try {
            Config.pdfDirectory         = properties.getProperty("pdfDirectory", Config.pdfDirectory);
            Config.pdfDirectoryOut      = properties.getProperty("pdfDirectoryOut", Config.pdfDirectoryOut);
            Config.pdfMaxThreadNumber   = Integer.parseInt(properties.getProperty("pdfMaxThreadNumber", ""+Config.pdfMaxThreadNumber));
            Config.pdfMaxFilesProcess   = Integer.parseInt(properties.getProperty("pdfMaxFilesProcess", ""+Config.pdfMaxFilesProcess));
            Config.pdfMaxTimeExecute    = Integer.parseInt(properties.getProperty("pdfMaxTimeExecute", ""+Config.pdfMaxTimeExecute));
            Config.pdfDebugEnabled      = properties.getProperty("pdfDebugEnabled", Boolean.toString(Config.pdfDebugEnabled)).equals("true");
            Config.pdfTextMarker        = properties.getProperty("pdfTextMarker",Config.pdfTextMarker );
            Config.pdfTextSearchExactly = properties.getProperty("pdfTextSearchExactly", Boolean.toString(Config.pdfTextSearchExactly)).equals("true");
            Config.pdfTextSearchOrder   = properties.getProperty("pdfTextSearchOrder", Config.pdfTextSearchOrder);
            Config.pdfMaketStamp        = properties.getProperty("pdfMaketStamp", "");
            Config.pdfImagePages        = properties.getProperty("pdfImagePages", "last");
            Config.pdfMaketTransparency = Integer.parseInt(properties.getProperty("pdfMaketTransparency", ""+Config.pdfMaketTransparency));
            Config.pdfTextMarkerAddX    = Float.parseFloat(properties.getProperty("pdfTextMarkerAddX", ""+Config.pdfTextMarkerAddX));
            Config.pdfTextMarkerAddY    = Float.parseFloat(properties.getProperty("pdfTextMarkerAddY", ""+Config.pdfTextMarkerAddY));
        } catch (Exception e) {
            throw new IncorrectConfigurationException(e.getMessage());
        }
    }

    /**
     * Make a simple checking for the properties values.
     * @throws IncorrectConfigurationException
     */
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

    /**
     * Print usage information to console.
     */
    public void printUsage() {
        System.out.println("Usage: >java PinImage [config-file] | [cmd @commands]");
        System.out.println("@commands: ");
        System.out.println("cmd parse - Try to find text by pattern and return the matching group. Return found content by line.");
        System.out.println("parse \"input.pdf\" \"(regex1)~0\" [\"(re(ge)x2)~1\" \"(r(e(g)ex)3)~3\" ...]");
        MyApp.exitApp("");
    }

    /**
     * Tests
     * @param args no use
     *//*
    public static void main(String[] args) {
        PinImage pinImage = new PinImage();

        System.out.println("Test PinConfig()");
        try{
            pinImage.loadConfig(new PinConfig(args));

        }catch (Exception e){
            System.out.println(e.getMessage());
            MyApp.exitApp("config load error " + e.getMessage());
        }
        System.out.println("Config loaded!");
    }*/

}
