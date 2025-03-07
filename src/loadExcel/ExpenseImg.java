package loadExcel;

import com.google.common.collect.Lists;
import com.obs.services.ObsClient;
import com.spire.data.table.DataColumnCollection;
import com.spire.data.table.DataRowCollection;
import com.spire.data.table.DataTable;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.ofdrw.converter.ImageMaker;
import org.ofdrw.reader.OFDReader;
import tools.MyFtpClientRelease;
import tools.MyOBSClientRelease;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.hutool.core.util.CharsetUtil.GBK;

/**
 * @author caiyongqing
 * @date 2025-03-07
 */
public class ExpenseImg {

    public static void main(String[] args) {
        FTPClient ftpClient = MyFtpClientRelease.getFtpClient();
        ObsClient obsClient = MyOBSClientRelease.getObsClient();
        Workbook workbookPrefix = new Workbook();
        workbookPrefix.loadFromFile("F:\\test\\lists.xlsx");
        //获取工作表
        Worksheet sheetPrefix = workbookPrefix.getWorksheets().get(0);
        //导出文档数据
        DataTable dataTablePrefix = sheetPrefix.exportDataTable();
        DataRowCollection rowCollectionPrefix = dataTablePrefix.getRows();
        DataColumnCollection colCollectionPrefix = dataTablePrefix.getColumns();
        StringBuilder attachmentIdString = new StringBuilder();
        List<FileInfoClass> fileInfoClassList = Lists.newArrayList();
        for (int i = 0; i < rowCollectionPrefix.size(); i++) {
            FileInfoClass fic = new FileInfoClass();
            for (int j = 0; j < colCollectionPrefix.size(); j++) {
                if (colCollectionPrefix.get(j).getLabel().equals("file_name")) {
                    fic.setFileNameString(rowCollectionPrefix.get(i).getString(j));
                }
                if (colCollectionPrefix.get(j).getLabel().equals("file_extension")) {
                    fic.setFileExtension(rowCollectionPrefix.get(i).getString(j));
                }
                if (colCollectionPrefix.get(j).getLabel().equals("obsKey")) {
                    fic.setObsKeyString(rowCollectionPrefix.get(i).getString(j));
                }
                if (colCollectionPrefix.get(j).getLabel().equals("filePath")) {
                    fic.setFilePathInfo(rowCollectionPrefix.get(i).getString(j));
                }
            }
            fileInfoClassList.add(fic);
        }
        AtomicInteger count = new AtomicInteger(0);
        fileInfoClassList.forEach(fic -> {
            // 改变ftp的工作路径
            // 获取当前ofd发票文件
            if (fic.getFileExtension().toLowerCase(Locale.ROOT).endsWith("ofd")) {
                try (OFDReader reader = new OFDReader(obsClient.getObject(MyOBSClientRelease.bucket, fic.getObsKeyString()).getObjectContent())) {
                    for (int i = 0; i < reader.getNumberOfPages(); i++) {
                        String fileName = fic.getFileNameString()
                                .substring(0, fic.getFileNameString().lastIndexOf(".")) + "-" + i + ".png";
                        // 转换第一页
                        // 如果文件不存在，则执行转换
                        InputStream in = ftpClient.retrieveFileStream(new String(fileName.getBytes(GBK), FTP.DEFAULT_CONTROL_ENCODING));
                        if (Objects.isNull(in) || ftpClient.getReplyCode() == FTPReply.FILE_UNAVAILABLE) {
                            ImageMaker imageMaker = new ImageMaker(reader, 10);
                            BufferedImage image = imageMaker.makePage(i);
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            ImageIO.write(image, "png", out);
                            in = new ByteArrayInputStream(out.toByteArray());
                            ftpClient.storeFile(fileName, in);
                            in.close();
                            out.close();
                        } else {
                            in.close();
                            ftpClient.completePendingCommand();
                        }
                    }
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                    System.out.println("失败的obsKey: " + fic.getObsKeyString());
                }
                count.incrementAndGet();
                System.out.println("完成数量：" + count.get() + "; 完成进度：" + 1.0f * count.get() / fileInfoClassList.size() * 100 + "%");
            } else {
                // 获取发票文件
                try (PDDocument document = PDDocument.load(obsClient.getObject(MyOBSClientRelease.bucket, fic.getObsKeyString()).getObjectContent(), MemoryUsageSetting.setupTempFileOnly())) {
                    // 获取所有页数
                    int pageCount = document.getNumberOfPages();
                    for (int i = 0; i < pageCount; i++) {
                        String fileName = fic.getFileNameString()
                                .substring(0, fic.getFileNameString().lastIndexOf(".")) + "-" + i + ".png";
                        // 如果文件不存在，则执行转换
                        InputStream in = ftpClient.retrieveFileStream(new String(fileName.getBytes(GBK), FTP.DEFAULT_CONTROL_ENCODING));
                        if (Objects.isNull(in) || ftpClient.getReplyCode() == FTPReply.FILE_UNAVAILABLE) {
                            PDFRenderer renderer = new PDFRenderer(document);
                            BufferedImage image = renderer.renderImageWithDPI(i, 300);
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            ImageIO.write(image, "png", out);
                            in = new ByteArrayInputStream(out.toByteArray());
                            ftpClient.storeFile(fileName, in);
                            in.close();
                            out.close();
                        } else {
                            in.close();
                            ftpClient.completePendingCommand();
                        }
                    }
                    count.incrementAndGet();
                    System.out.println("完成数量：" + count.get() + "; 完成进度：" + 1.0f * count.get() / fileInfoClassList.size() * 100 + "%");
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                    System.out.println("失败的obsKey: " + fic.getObsKeyString());
                }
            }
        });
        System.out.println(attachmentIdString.toString());
    }
}
