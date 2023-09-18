package loadExcel;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

import cn.hutool.core.io.FileUtil;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.ObsObject;
import com.spire.data.table.DataColumnCollection;
import com.spire.data.table.DataRowCollection;
import com.spire.data.table.DataTable;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author admin
 */
public class ExclForFile {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String endPoint = "https://183.134.72.90";
        String ak = "QDKU0HPLR9X40EEA7DBR";
        String sk = "eoPXA7z541ihdBSMHsxJNAkF5LTOFcePLZ3wLVQc";
        // 创建ObsClient实例
        ObsClient obsClient = new ObsClient(ak, sk, endPoint);
        try {
            // TODO code application logic here
            // 获取attachment_id 串

//            Workbook workbookPrefix = new Workbook();
//            workbookPrefix.loadFromFile("F:\\test\\2022年报公众公司审计风险评价表-调整.xlsx");
//            //获取工作表
//            Worksheet sheetPrefix = workbookPrefix.getWorksheets().get(5);
//            //导出文档数据
//            DataTable dataTablePrefix = sheetPrefix.exportDataTable();
//            DataRowCollection rowCollectionPrefix = dataTablePrefix.getRows();
//            DataColumnCollection colCollectionPrefix = dataTablePrefix.getColumns();
//            StringBuilder attachmentIdString = new StringBuilder();
//            for (int i = 0; i < rowCollectionPrefix.size(); i++) {
//                loadExcel.FileInfoClass fic = new loadExcel.FileInfoClass();
//                for (int j = 0; j < colCollectionPrefix.size(); j++) {
//                    if (colCollectionPrefix.get(j).getLabel().equals("recheck_attachment_id")) {
////                        String currentString = "\"" + rowCollection.get(i).getString(j) + "\"";
//                        attachmentIdString
//                                .append("\"")
//                                .append(rowCollectionPrefix.get(i).getString(j))
//                                .append("\"")
//                                .append(",");
////                        System.out.println(colCollection.get(j).getLabel() + "：" + rowCollection.get(i).getString(j));
//                    }
//                }
//            }
//            System.out.println(attachmentIdString.toString());


//            // 获取正式数据
            Workbook workbook = new Workbook();
            workbook.loadFromFile("F:\\test\\rpreview.xlsx");
            //获取第一张工作表
            Worksheet sheet = workbook.getWorksheets().get(0);
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
                    if (colCollection.get(j).getLabel().equals("obsKey")) {
                        fic.setObsKeyString(rowCollection.get(i).getString(j).substring(1));
                    }

                }
                result.add(fic);
            }

            // 创建ObsClient实例
            result.forEach(item -> {
                try {
                    ObsObject obsObject = obsClient.getObject("docflex", item.getObsKeyString());
                    FileUtil.writeFromStream(obsObject.getObjectContent(), new File("test\\" + item.getFilePathInfo() + "\\" + item.getFileNameString()));
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
            System.err.println(ex.getMessage());
        }
        finally {
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
