package cmd.parser;

import com.www1develop.util.pdfbox.PDFExtractText;
import com.www1develop.util.pdfbox.PDFStringPosition;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ilya on 27.03.2014.
 */
public class Parser {

    private String fileName;
    private String cmd;
    private List<String> options;
    private PDFExtractText pdfText;
    private Map<Integer, String> mapText;

    public Parser(List<String> options) {
        this.fileName = options.get(1);
        this.cmd = options.get(0);
        this.options = options;
    }


    public Map<Integer, String> getMapText() throws IOException {
        pdfText = new PDFExtractText(fileName);
        Map<Integer, LinkedList<PDFStringPosition>> words = pdfText.readText();
        mapText = pdfText.getRawText();
        System.out.println(fileName);
        return mapText;
    }

    public void processText() throws IOException{
        if(pdfText == null)
            throw new IOException("Call getMapText() first!");
        // UTF-8 output fix
        PrintStream out = new PrintStream(System.out, true, "UTF-8");

        for (int i = 2; i < options.size(); i++) {
            String s = options.get(i);
            String regEx = s.substring(0, s.length() - 2);
            int group = Integer.parseInt(s.substring(s.length()-1, s.length()));

            out.println("\"" + regEx + "\" :#: \"" + findMatch(regEx, group) + "\"");
        }
    }

    private String findMatch(String regEx, int group){
        Pattern p = Pattern.compile(regEx);
        String result = "";
        for (int i = 0; i < mapText.size(); i++) {
            Matcher m = p.matcher(mapText.get(i));
            if(m.find() && m.groupCount() >= group){
                result = m.group(group);
                break;
            }
        }
        return result;
    }

    public static void main(String[] args) {


    }
}
