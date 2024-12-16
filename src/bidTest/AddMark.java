package bidTest;

import com.obs.services.ObsClient;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.util.autodetect.FontFileFinder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;
import tools.MyOBSClientTest;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;

/**
 * @author caiyongqing
 * @date 2024-12-16
 */
public class AddMark {
    private static String obsKeyPrefix = "BID/2024-12/";

    private static final String DEFAULT_TTF_FILENAME = "simsun.ttf";
    private static final String DEFAULT_TTC_FILENAME = "simsun.ttc";
    private static final String DEFAULT_FONT_NAME = "SimSun";

    public static void main(String[] args) {
        ObsClient obsClient = MyOBSClientTest.getObsClient();
        try (PDDocument doc = PDDocument.load(obsClient.getObject(MyOBSClientTest.bucket,
                obsKeyPrefix + "20241213-173205901-90399342-77534485-E7725098/20241213-173205902-65279579-16259765-6E773684.pdf").getObjectContent())) {
            doc.setAllSecurityToBeRemoved(true);
            for(PDPage page:doc.getPages()){
                PDPageContentStream cs =new PDPageContentStream(doc,page,PDPageContentStream.AppendMode.APPEND,true,true);
                String ts ="【文本】 编号编号 仅测试时使用";
                PDFont font = PDType0Font.load(doc, loadSystemFont(), true);

                float fontSize =25.0f;
                PDExtendedGraphicsState r0 =new PDExtendedGraphicsState();
                r0.setNonStrokingAlphaConstant(0.5f);
                r0.setAlphaSourceFlag(true);
                cs.setGraphicsStateParameters(r0);
                cs.setNonStrokingColor(200 / 225f,200 / 225f,200 / 225f);

                cs.beginText();
                cs.setFont(font,fontSize);
                cs.setTextMatrix(Matrix.getRotateInstance(0.5,100f,250f));
                cs.showText(ts);
                cs.endText();

                cs.beginText();
                cs.setFont(font,fontSize);
                cs.setTextMatrix(Matrix.getRotateInstance(0.5,100f,550f));
                cs.showText(ts);
                cs.endText();
                cs.close();
            }
            doc.save(new File("dist/files/out.pdf"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载系统字体,提供默认字体
     *
     * @return
     */
    private static TrueTypeFont loadSystemFont() {
        //load 操作系统的默认字体. 宋体
        final String DEFAULT_TTF_FILENAME = "simsun.ttf";
        final String DEFAULT_TTC_FILENAME = "simsun.ttc";
        final String DEFAULT_FONT_NAME = "SimSun";
        FontFileFinder fontFileFinder = new FontFileFinder();
        for (URI uri : fontFileFinder.find()) {
            try {
                final String filePath = uri.getPath();
                if (filePath.endsWith(DEFAULT_TTF_FILENAME)) {
                    return new TTFParser(false).parse(filePath);
                } else if (filePath.endsWith(DEFAULT_TTC_FILENAME)) {
                    TrueTypeCollection trueTypeCollection = new TrueTypeCollection(new FileInputStream(filePath));
                    final TrueTypeFont font = trueTypeCollection.getFontByName(DEFAULT_FONT_NAME);
                    //复制完之后关闭ttc
                    trueTypeCollection.close();
                    return font;
                }
            } catch (Exception e) {
                throw new RuntimeException("加载操作系统字体失败", e);
            }
        }
        return null;
    }
}
