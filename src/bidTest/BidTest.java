package bidTest;

import com.obs.services.ObsClient;
import com.spire.data.table.DataColumnCollection;
import com.spire.data.table.DataRowCollection;
import com.spire.data.table.DataTable;
import com.spire.xls.CellRange;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import loadExcel.FileInfoClass;
import tools.MyOBSClientTest;
import tools.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author caiyongqing
 * @date 2024-12-13
 */
public class BidTest {
    private static String obsKeyPrefix = "BID/2024-12/";
    private static ObsClient obsClient = MyOBSClientTest.getObsClient();

    public static void main(String[] args) throws Exception {

        List<FileInfoClass> fileInfoClassList = new ArrayList<>();

        File folder = new File("F:\\华为家庭存储\\我的文件\\天健工作文件\\投标相关\\投标资料\\样本\\附件");
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        getAllFiles(file, fileInfoClassList);
                    } else {
                        FileInfoClass fileInfoClass = new FileInfoClass()
                                .setFilePathInfo(file.getAbsolutePath())
                                .setFileNameString(file.getName())
                                .setFile(file);
                        fileInfoClassList.add(fileInfoClass);
                    }
                }
            }
        }

        Workbook workbook = new Workbook();
        workbook.loadFromFile("dist/files/lists.xlsx");
        //获取第一张工作表
        Worksheet sheet = workbook.getWorksheets().get(0);
        //导出文档数据
        DataTable dataTable = sheet.exportDataTable();
        DataRowCollection rowCollection = dataTable.getRows();
        DataColumnCollection colCollection = dataTable.getColumns();
        for (int i = 0; i < rowCollection.size(); i++) {
            String attachmentId = StringUtil.GetTableUUID_new();
            String fileName = StringUtil.GetTableUUID_new();
            AtomicReference<String> fileExtension = new AtomicReference<>();
            for (int j = 0; j < colCollection.size(); j++) {
                if (colCollection.get(j).getLabel().equals("命名")) {
                    String fileMatchName = rowCollection.get(i).getString(j);
                    fileInfoClassList.forEach(infoClass -> {
                        String fileOriginalName = infoClass.getFileNameString();
                        fileExtension.set(fileOriginalName.substring(fileOriginalName.lastIndexOf(".")));
                        if (infoClass.getFileNameString().contains(fileMatchName)) {
                            infoClass.setObsKeyString(obsKeyPrefix + attachmentId + "/" + fileName + fileExtension.get());
                        }
                    });
                }
                if (colCollection.get(j).getLabel().equals("attachment_id")) {
                    CellRange cell = sheet.getCellRange(i+2, j+1);
                    cell.setValue(attachmentId);
                }
                if (colCollection.get(j).getLabel().equals("file_id")) {
                    CellRange cell = sheet.getCellRange(i+2, j+1);
                    cell.setValue(fileName);
                }
                if (colCollection.get(j).getLabel().equals("file_name")) {
                    CellRange cell = sheet.getCellRange(i+2, j+1);
                    cell.setValue(fileName + fileExtension.get());
                }
            }
        }
        workbook.save();
        fileInfoClassList
                .stream()
                .filter(infoClass -> !StringUtil.isEmpty(infoClass.getObsKeyString()))
                .forEach(infoClass -> {
                    obsClient.putObject(MyOBSClientTest.bucket, infoClass.getObsKeyString(), infoClass.getFile());
                });
        System.out.println("sdfasdfasdf");
    }

    private static void getAllFiles(File dir, List<FileInfoClass> fileInfoClassList) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    getAllFiles(file, fileInfoClassList);
                } else {
                    FileInfoClass fileInfoClass = new FileInfoClass()
                            .setFilePathInfo(file.getAbsolutePath())
                            .setFileNameString(file.getName());
                    fileInfoClassList.add(fileInfoClass);
                }
            }
        }
    }

    public static String getObsKeyPrefix() {
        return obsKeyPrefix;
    }

    public static void setObsKeyPrefix(String obsKeyPrefix) {
        BidTest.obsKeyPrefix = obsKeyPrefix;
    }
}
