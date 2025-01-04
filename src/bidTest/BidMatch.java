package bidTest;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.spire.data.table.DataColumnCollection;
import com.spire.data.table.DataRowCollection;
import com.spire.data.table.DataTable;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import loadExcel.FileInfoClass;
import tools.MyOBSClientRelease;
import tools.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author caiyongqing
 * @date 2024-12-13
 */
public class BidMatch {
    private static String obsKeyPrefix = "归档服务器/OAFileServer_TJ_2020_2021/BID/2025-02/";
    private static ObsClient obsClient = MyOBSClientRelease.getObsClient();
    private static String bucket = MyOBSClientRelease.bucket;

    public static void main(String[] args) throws Exception {

        List<FileInfoClass> fileInfoClassList = new ArrayList<>();
        List<FileInfoClass> saveFileInfoList = new ArrayList<>();

        File folder = new File("F:\\投标资料（脱敏\\合同-脱敏");
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
                                .setFileSize(file.length())
                                .setFile(file);
                        System.out.println("File Info: " + fileInfoClass.getFileNameString());
                        fileInfoClassList.add(fileInfoClass);
                    }
                }
            }
        }
        System.out.println("File Total: " + fileInfoClassList.size());

        Workbook workbook = new Workbook();
        workbook.loadFromFile("F:\\test\\lists.xlsx");
        Worksheet sheet = workbook.getWorksheets().get(6);
        //导出文档数据
        DataTable dataTable = sheet.exportDataTable();
        DataRowCollection rowCollection = dataTable.getRows();
        DataColumnCollection colCollection = dataTable.getColumns();
        AtomicInteger workbookCount = new AtomicInteger(0);
        for (int i = 0; i < rowCollection.size(); i++) {
            AtomicReference<String> fileExtension = new AtomicReference<>();
            AtomicReference<FileInfoClass> currentFileInfoClass = new AtomicReference<>();
            for (int j = 0; j < colCollection.size(); j++) {
                if (colCollection.get(j).getLabel().equals("file_original_name")) {
                    String fileMatchName = rowCollection.get(i).getString(j);
                    fileInfoClassList.forEach(infoClass -> {
                        if (infoClass.getFileNameString().contains(fileMatchName)) {
                            String fileOriginalName = infoClass.getFileNameString();
                            fileExtension.set(fileOriginalName.substring(fileOriginalName.lastIndexOf(".")));
                            currentFileInfoClass.set(infoClass);
                            saveFileInfoList.add(infoClass);
                            workbookCount.incrementAndGet();
                            System.out.println("count: " + workbookCount.get() + ";  process: " + ((double) workbookCount.get() / (rowCollection.size() - 1)) * 100.0 + "%");
                        }
                    });
                }
                if (currentFileInfoClass.get() != null) {
                    if (colCollection.get(j).getLabel().equals("obsKey")) {
                        currentFileInfoClass.get().setObsKeyString(rowCollection.get(i).getString(j));
                    }
                }
            }
        }

        AtomicInteger count = new AtomicInteger(0);
        saveFileInfoList
                .stream()
                .filter(infoClass -> !StringUtil.isEmpty(infoClass.getObsKeyString()))
                .forEach(infoClass -> {
                    try {
                        obsClient.putObject(bucket, infoClass.getObsKeyString(), infoClass.getFile());
                        count.incrementAndGet();
                        System.out.println("Success key: " + infoClass.getObsKeyString());
                        System.out.println("count: " + count.get() + ";  process: " + ((double) count.get() / saveFileInfoList.size()) * 100.0 + "%");
                    } catch (ObsException e) {
                        System.out.println("Error Message: " + e.getErrorMessage());
                        System.out.println("Error key: " + infoClass.getObsKeyString());
                    }
                });

        System.out.println("Upload File Total: " + saveFileInfoList.size());
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
                            .setFileNameString(file.getName())
                            .setFileSize(file.length())
                            .setFile(file);
                    System.out.println("File Info: " + fileInfoClass.getFileNameString());
                    fileInfoClassList.add(fileInfoClass);
                }
            }
        }
    }

    public static String getObsKeyPrefix() {
        return obsKeyPrefix;
    }
}
