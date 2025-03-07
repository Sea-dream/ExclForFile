package docTest;

import com.obs.services.ObsClient;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import tools.MyOBSClientTest;

import java.io.File;

/**
 * @author caiyongqing
 * @date 2025-02-10
 */
public class DocxToDocx {

    public static void main(String[] args) throws Exception {
        String obskey = "BID/2024-01/attachment-0.5405965230525445/20240129-161819065-36057293-41506958-828945751.doc";
        ObsClient obsClient = MyOBSClientTest.getObsClient();
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackage wordMLPackageSource = WordprocessingMLPackage.load(
                obsClient.getObject(MyOBSClientTest.bucket, obskey).getObjectContent());

        // 复制内容段落到目标文档的末尾（你也可以选择插入到特定位置）
        wordMLPackage.getMainDocumentPart().getContent().addAll(wordMLPackageSource.getMainDocumentPart().getContent());
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

        String filename = System.getProperty("user.dir") + "/dist/files/out.docx";
        Docx4J.save(wordMLPackage, new File(filename), Docx4J.FLAG_SAVE_ZIP_FILE);

        System.out.println("Saved " + filename);
    }
}
