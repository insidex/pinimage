import com.www1develop.enums.ErrorLevel;
import com.www1develop.enums.StatusLevel;
import com.www1develop.io.Debugger;
import com.www1develop.util.ZFile;
import com.www1develop.util.pdfbox.PDFAddImage;
import com.www1develop.util.pdfbox.PDFExtractText;
import com.www1develop.util.pdfbox.PDFStringPosition;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * PinRunner class - Runnable class that process some portion of PDF files in separate thread.
 *
 * @author Ilya Zukhta (mail*AT*1develop.com)
 * @since 1.0
 */
public class PinRunner implements Runnable {
    private int startIndex;
    private int endIndex;
    private static List<Pins> pins = PinManager.pins;

    /**
     * Construct the object with indexes. All threads will use it to work independently from each other.
     * @param startIndex    start portion in the list of tasks
     * @param endIndex      end portion
     */
    public PinRunner(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    /**
     * Start thread with selected portion of object to process.
     */
    @Override
    public void run() {
        Debugger.message(StatusLevel.DEBUG, String.format("start Thread (%s): tasks [%d;%d]", Thread.currentThread().getName(), startIndex, endIndex));
        try {
            TimeUnit.MILLISECONDS.sleep(500);
            for (int i = startIndex; i <= endIndex; i++) {
                if(processPDF(i))
                    Debugger.message(StatusLevel.DEBUG, "pdf:processed " + pins.get(i).getStatus());
                else
                    Debugger.message(StatusLevel.DEBUG, "pdf:error " + pins.get(i).getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called from thread. Processing one pdf file with it index.
     */
    private boolean processPDF(int pos){
        Pins pin;
        try {
            pin = pins.get(pos);
            PDFExtractText pdfText = new PDFExtractText(pin.getFileIN());
            //Map<Integer, LinkedList<PDFStringPosition>> words = pdfText.readText();

            Debugger.message(StatusLevel.DEBUG, "ok:read " + pin.getFileIN());
            pin.setStatus(Pins.Status.PREPARE);

            if(placeImage(pdfText, pin)){
                Debugger.message(StatusLevel.DEBUG, "ok:image");
                pin.setStatus(Pins.Status.IMAGE_PLACED_OK);
            }else{
                Debugger.message(ErrorLevel.WARNING, pin.getFileIN());
                pin.setStatus(Pins.Status.IMAGE_PLACED_ERROR);
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return false;
    }

    /**
     * Place image to PDF file
     * @param pdfText pdf page object
     * @param pin currently pin object
     * @return true if image was placed without errors
     */
    private boolean placeImage(PDFExtractText pdfText, Pins pin) {
        String pdfImagePages = PinConfig.Config.pdfImagePages;
        int word_index;
        PDFStringPosition word;
        PDFAddImage pdfImage;
        boolean result = false;

        try {
            pdfImage = new PDFAddImage(pin.getFileIN(), PinConfig.Config.pdfDirectoryOut + ZFile.getFileName(pin.getFileIN()));

            if(pdfImagePages.equals("all")){
                for(int i=0;i<pdfText.totalPages();i++){
                    word_index = pdfText.contains(
                            PinConfig.Config.pdfTextMarker,
                            i,
                            PinConfig.Config.pdfTextSearchExactly,
                            PinConfig.Config.pdfTextSearchOrder.equals("first")
                    );

                    if (word_index != -1){
                        result = true;
                        word = pdfText.getMapWords().get(i).get(word_index);
                        pdfImage.addImage(
                                PinConfig.Config.pdfMaketStamp,
                                i, PinConfig.Config.pdfMaketTransparency,
                                word.getX_end() + PinConfig.Config.pdfTextMarkerAddX,
                                word.getY_end() + PinConfig.Config.pdfTextMarkerAddY
                        );
                    }
                }
            }else if (pdfImagePages.equals("first")) {
                word_index = pdfText.contains(
                        PinConfig.Config.pdfTextMarker,
                        0,
                        PinConfig.Config.pdfTextSearchExactly,
                        PinConfig.Config.pdfTextSearchOrder.equals("first")
                );

                if (word_index != -1){
                    result = true;
                    word = pdfText.getMapWords().get(0).get(word_index);
                    pdfImage.addImage(
                            PinConfig.Config.pdfMaketStamp,
                            0, PinConfig.Config.pdfMaketTransparency,
                            word.getX_end() + PinConfig.Config.pdfTextMarkerAddX,
                            word.getY_end() + PinConfig.Config.pdfTextMarkerAddY
                    );
                }

            }else{ // pdfImagePages.equals("last")
                word_index = pdfText.contains(
                        PinConfig.Config.pdfTextMarker,
                        pdfText.totalPages() - 1,
                        PinConfig.Config.pdfTextSearchExactly,
                        PinConfig.Config.pdfTextSearchOrder.equals("first")
                );

                if (word_index != -1){
                    result = true;
                    word = pdfText.getMapWords().get(pdfText.totalPages()-1).get(word_index);
                    pdfImage.addImage(
                            PinConfig.Config.pdfMaketStamp,
                            pdfText.totalPages() - 1, PinConfig.Config.pdfMaketTransparency,
                            word.getX_end() + PinConfig.Config.pdfTextMarkerAddX,
                            word.getY_end() + PinConfig.Config.pdfTextMarkerAddY
                    );
                }
            }

            pdfImage.savePDF();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
