package docTest;

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
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.util.Matrix;
import org.docx4j.Docx4J;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import tools.MyOBSClientTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;

/**
 * @author caiyongqing
 * @date 2025-02-07
 */
public class Dox4j {
    private static String obsKeyPrefix = "BID/2024-12/";

    public static void main(String[] args) throws Exception {

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
//        File file = new File("C:\\Users\\admin\\Pictures\\壁纸\\24ccae7de93f4e8c.jpg");
//
//        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, file);
//
//        Inline inline = imagePart.createImageInline( null, null,
//                wordMLPackage.getDrawingPropsIdTracker().generateId(), 1, false);
//
//        ObjectFactory factory = Context.getWmlObjectFactory();
//        P  p = factory.createP();
//        R  run = factory.createR();
//        p.getContent().add(run);
//        Drawing drawing = factory.createDrawing();
//        run.getContent().add(drawing);
//        drawing.getAnchorOrInline().add(inline);

        addImage(wordMLPackage);

        addImage(wordMLPackage);

        addImage(wordMLPackage);

        String filename = System.getProperty("user.dir") + "/dist/files/out.docx";
        Docx4J.save(wordMLPackage, new File(filename), Docx4J.FLAG_SAVE_ZIP_FILE);

        System.out.println("Saved " + filename);

    }

    private static void addImage(WordprocessingMLPackage wordMLPackage) {
        wordMLPackage.getMainDocumentPart().addParagraphOfText("目录-资料名称（会替换成真实目录和名称）");
        ObsClient obsClient = MyOBSClientTest.getObsClient();
        try (PDDocument doc = PDDocument.load(obsClient.getObject(MyOBSClientTest.bucket,
                obsKeyPrefix + "20241213-173205901-90399342-77534485-E7725098/20241213-173205902-65279579-16259765-6E773684.pdf").getObjectContent())) {
            doc.setAllSecurityToBeRemoved(true);
            PDFRenderer renderer = new PDFRenderer(doc);
            for (PDPage page : doc.getPages()) {
                PDPageContentStream cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);
                String ts = "【文本】 编号编号 仅测试时使用【文本】 编号编号 仅测试时使用【文本】 编号编号 仅测试时使用【文本】 编号编号 仅测试时使用【文本】 编号编号 仅测试时使用【文本】 编号编号 仅测试时使用";
                PDFont font = PDType0Font.load(doc, loadSystemFont(), true);

                float fontSize = 15.0f;
                PDExtendedGraphicsState r0 = new PDExtendedGraphicsState();
                r0.setNonStrokingAlphaConstant(0.3f);
                r0.setAlphaSourceFlag(true);
                cs.setGraphicsStateParameters(r0);
                cs.setNonStrokingColor(100 / 225f, 100 / 225f, 100 / 225f);

                cs.beginText();
                cs.setFont(font, fontSize);
                cs.setTextMatrix(Matrix.getRotateInstance(0.9, -10f, 50f));
                cs.showText(ts);
                cs.endText();
                cs.close();
            }
            int pages = doc.getNumberOfPages();
            for (int i = 0; i < pages; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 300);
                File tempFile = new File("dist/files/out-" + i + ".png");

                //Writing the image to a file
                ImageIO.write(image, "png", tempFile);

                BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, tempFile);

                Inline inline = imagePart.createImageInline(null, null,
                        wordMLPackage.getDrawingPropsIdTracker().generateId(), 1, false);

                ObjectFactory factory = Context.getWmlObjectFactory();
                P p = factory.createP();
                R run = factory.createR();
                p.getContent().add(run);
                Drawing drawing = factory.createDrawing();
                run.getContent().add(drawing);
                drawing.getAnchorOrInline().add(inline);

                wordMLPackage.getMainDocumentPart().addObject(p);

                tempFile.delete();
            }

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
