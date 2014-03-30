package cmd.parser;

import com.www1develop.util.pdfbox.PDFAddImage;
import com.www1develop.util.pdfbox.PDFExtractText;
import com.www1develop.util.pdfbox.PDFStringPosition;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by ilya on 29.03.2014.
 */
public class Sign {
    private List<String> options;
    private PrintStream out;
    private String pdfFileName;
    private String pdfMarkerImage;
    private String pdfMarkerText;
    private float pdfMarkerAddX;
    private float pdfMarkerAddY;
    private Status code;
    public enum Status{ PROCESSED_TEXT_OK, PROCESSED_TEXT_ERROR, MARKER_NOT_FOUND, MARKER_FOUND, IMAGE_PLACED_OK, IMAGE_PLACED_ERROR}
    private Map<Integer, LinkedList<PDFStringPosition>> words;
    private PDFExtractText pdfText;

    public Sign(List<String> options) {
        this.options = options;
        this.pdfFileName = options.get(1);
        this.pdfMarkerText = options.get(2);
        this.pdfMarkerImage = options.get(3);
        this.pdfMarkerAddX = Float.parseFloat(options.get(4));
        this.pdfMarkerAddY = Float.parseFloat(options.get(5));
    }

    public Map<Integer, LinkedList<PDFStringPosition>> obtainTextPos() throws IOException {

        Map<Integer, LinkedList<PDFStringPosition>> words = null;
        try {
            pdfText = new PDFExtractText(pdfFileName);
            words = pdfText.readText();
            code = Status.PROCESSED_TEXT_OK;
        } catch (IOException e) {
            code = Status.PROCESSED_TEXT_ERROR;
            throw e;
        }
        return words;
    }

    public void processImage() {
        String error = "";
        try{
            placeImage();
        } catch (Exception e) {
            error = e.getMessage();
        } finally{
            out.println("\"" + pdfFileName + "\"" + " :#: " + code.toString());
        }
        if(!error.isEmpty())
            out.println(error);
    }

    public void placeImage() throws Exception {

        int word_index;
        PDFStringPosition word;
        PDFAddImage pdfImage = null;

        try {
            pdfImage = new PDFAddImage(pdfFileName, pdfFileName);
            word_index = pdfText.contains(
                    pdfMarkerText,
                    pdfText.totalPages() - 1,
                    true,
                    false
            );

            if (word_index != -1){ // place image on last page
                code = Status.MARKER_FOUND;
                word = pdfText.getMapWords().get(pdfText.totalPages()-1).get(word_index);
                pdfImage.addImage(
                        pdfMarkerImage,
                        pdfText.totalPages() - 1,
                        100,
                        word.getX_end() + pdfMarkerAddX,
                        word.getY_end() + pdfMarkerAddY
                );
                pdfImage.savePDF();
                code = Status.IMAGE_PLACED_OK;
            }else{
                code = Status.MARKER_NOT_FOUND;
                throw new UnsupportedOperationException("Marker " + pdfMarkerText + " not found");
            }

        }catch (Exception e){
            code = Status.IMAGE_PLACED_ERROR;
            throw e;
        }finally {
            if(pdfImage != null)
                pdfImage.close();
        }

    }


    public Status getCode() {
        return code;
    }

    public void setOut(PrintStream out) {
        this.out = out;
    }
}
