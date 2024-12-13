package loadExcel;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.io.File;

/**
 *
 * @author admin
 */
public class FileInfoClass {
    
    private String filePathInfo;
    
    private String fileNameString;
    
    private String obsKeyString;

    private File file;

    public String getFilePathInfo() {
        return filePathInfo;
    }

    public FileInfoClass setFilePathInfo(String filePathInfo) {
        this.filePathInfo = filePathInfo;
        return this;
    }

    public String getFileNameString() {
        return fileNameString;
    }

    public FileInfoClass setFileNameString(String fileNameString) {
        this.fileNameString = fileNameString;
        return this;
    }

    public String getObsKeyString() {
        return obsKeyString;
    }

    public FileInfoClass setObsKeyString(String obsKeyString) {
        this.obsKeyString = obsKeyString;
        return this;
    }

    public File getFile() {
        return file;
    }

    public FileInfoClass setFile(File file) {
        this.file = file;
        return this;
    }
}
