package bidTest;

import cn.hutool.core.io.FileUtil;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.ObsObject;
import com.spire.data.table.DataColumnCollection;
import com.spire.data.table.DataRowCollection;
import com.spire.data.table.DataTable;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import loadExcel.ExclForFile;
import loadExcel.FileInfoClass;
import tools.MyOBSClientRelease;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author caiyongqing
 * @date 2024-12-30
 */
public class ExportFile {
    public static void main(String[] args) {
        String prefix = "归档服务器/OAFileServer_TJ_2020_2021/BID/";
        ObsClient obsClient = MyOBSClientRelease.getObsClient();

        try {
            // TODO code application logic here
            // 获取正式数据
            Workbook workbook = new Workbook();
            workbook.loadFromFile("F:\\test\\lists.xlsx");
            //获取第一张工作表
            Worksheet sheet = workbook.getWorksheets().get(1);
            //导出文档数据
            DataTable dataTable = sheet.exportDataTable();
            DataRowCollection rowCollection = dataTable.getRows();
            DataColumnCollection colCollection = dataTable.getColumns();
            List<FileInfoClass> result = new ArrayList<>();
            String lString = "end";
            for (int i = 0; i < rowCollection.size(); i++) {
                FileInfoClass fic = new FileInfoClass();
                for (int j = 0; j < colCollection.size(); j++) {
                    if (colCollection.get(j).getLabel().equals("fileName")) {
                        fic.setFileNameString(rowCollection.get(i).getString(j));
                    }
                    if (colCollection.get(j).getLabel().equals("file_original_name")) {
                        fic.setFileOriginalName(rowCollection.get(i).getString(j));
                    }
                    if (colCollection.get(j).getLabel().equals("file_folder")) {
                        fic.setFilePathInfo(rowCollection.get(i).getString(j));
                    }
                    if (colCollection.get(j).getLabel().equals("obsKey")) {
                        fic.setObsKeyString(prefix + rowCollection.get(i).getString(j));
                    }
                }
                result.add(fic);
            }

            // 创建ObsClient实例
            result.forEach(item -> {
                try {
                    ObsObject obsObject = obsClient.getObject(MyOBSClientRelease.bucket, item.getObsKeyString());
                    FileUtil.writeFromStream(obsObject.getObjectContent(), new File("test\\" + item.getFilePathInfo() + "\\" + item.getFileOriginalName()));
                    System.out.println("Success key: " + item.getObsKeyString());
                } catch (ObsException e) {
                    System.out.println("Error Message: " + e.getErrorMessage());
                    System.out.println("Error key: " + item.getObsKeyString());
                }
            });
            System.out.println(lString);
        } catch (ObsException e) {
            System.out.println("HTTP Code: " + e.getResponseCode());
            System.out.println("Error Code:" + e.getErrorCode());
            System.out.println("Error Message: " + e.getErrorMessage());
            System.out.println("Request ID:" + e.getErrorRequestId());
            System.out.println("Host ID:" + e.getErrorHostId());
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getMessage());
        } finally {
            if (obsClient != null) {
                try {
                    obsClient.close();
                } catch (IOException ex) {
                    Logger.getLogger(ExclForFile.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
