/*
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            // 1.加载驱动
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            // 2.用户信息和url
//            String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=true";
//            String username="root";
//            String password="Cc26070119";
//            Connection connection = DriverManager.getConnection(url,username,password);
//            Statement statement = connection.createStatement();
//            // 5.执行SQL的对象去执行SQL，返回结果集
//            String sql = "SELECT * FROM test;";
//            ResultSet resultSet = statement.executeQuery(sql);
            Workbook workbook = new Workbook();
            workbook.loadFromFile("F:\\test\\2022年报公众公司审计风险评价表.xlsx");
            //获取第一张工作表
            Worksheet sheet = workbook.getWorksheets().get(3);
            //导出文档数据
            DataTable dataTable = sheet.exportDataTable();
            DataRowCollection rowCollection = dataTable.getRows();
            DataColumnCollection colCollection = dataTable.getColumns();
            Map<String, FileInfoClass> fileInfoClasses = new HashMap<>();
            List<FileInfoClass> result = new ArrayList<>();
            String lString = "end";
            for (int i = 0; i < rowCollection.size(); i++) {
                FileInfoClass fic = new FileInfoClass();
                StringBuilder filepatahString = new StringBuilder();
                String atString = "";
                for (int j = 0; j < colCollection.size(); j++) {
                    if (colCollection.get(j).getLabel().equals("id")) {
//                        String currentString = "\"" + rowCollection.get(i).getString(j) + "\"";
                        filepatahString.append(rowCollection.get(i).getString(j)).append("-");
//                        System.out.println(colCollection.get(j).getLabel() + "：" + rowCollection.get(i).getString(j));
                    }
                    if (colCollection.get(j).getLabel().equals("cus_name")) {
                        filepatahString.append(rowCollection.get(i).getString(j));
                    }
                    if (colCollection.get(j).getLabel().equals("recheck_attachment_id")) {
                        atString = rowCollection.get(i).getString(j);
                    }

                }
                fic.setFilePathInfo(filepatahString.toString());
                fileInfoClasses.put(atString,fic);
            }

            Workbook workbook2 = new Workbook();
            workbook2.loadFromFile("F:\\test\\obsKey.xlsx");
            Worksheet sheet2 = workbook2.getWorksheets().get(0);
            //导出文档数据
            DataTable dataTable2 = sheet2.exportDataTable();
            DataRowCollection rowCollection2 = dataTable2.getRows();
            DataColumnCollection colCollection2 = dataTable2.getColumns();
            for (int i = 0; i < rowCollection2.size(); i++) {
                FileInfoClass currentFileInfoClass = new FileInfoClass();
                String atString = "";
                String nameString = "";
                String obsString = "";
                for (int j = 0; j < colCollection2.size(); j++) {
                    if (colCollection2.get(j).getLabel().equals("attachment_id")) {
                        atString = rowCollection2.get(i).getString(j);
//                        System.out.println(colCollection2.get(j).getLabel() + "：" + rowCollection2.get(i).getString(j));
                    }
                    if (colCollection2.get(j).getLabel().equals("obsKey")) {
                        obsString = rowCollection2.get(i).getString(j).substring(1);
//                        System.out.println(colCollection2.get(j).getLabel() + "：" + rowCollection2.get(i).getString(j));
                    }
                    if (colCollection2.get(j).getLabel().equals("file_original_name")) {
                        nameString = rowCollection2.get(i).getString(j);
//                        System.out.println(colCollection2.get(j).getLabel() + "：" + rowCollection2.get(i).getString(j));
                    }
                }
                currentFileInfoClass.setFilePathInfo(fileInfoClasses.get(atString).getFilePathInfo());
                currentFileInfoClass.setObsKeyString(obsString);
                currentFileInfoClass.setFileNameString(nameString);
                result.add(currentFileInfoClass);
            }

            // 创建ObsClient实例
            result.forEach(item -> {
                try {
                    ObsObject obsObject = obsClient.getObject("docflex", item.getObsKeyString());
                    FileUtil.writeFromStream(obsObject.getObjectContent(), new File("test\\" + item.getFilePathInfo() + "\\" + item.getFileNameString()));
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
