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

    private String fileOriginalName;
    
    private String obsKeyString;

    private Long fileSize;

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

    public String getFileOriginalName() {
        return fileOriginalName;
    }

    public FileInfoClass setFileOriginalName(String fileOriginalName) {
        this.fileOriginalName = fileOriginalName;
        return this;
    }

    public String getObsKeyString() {
        return obsKeyString;
    }

    public FileInfoClass setObsKeyString(String obsKeyString) {
        this.obsKeyString = obsKeyString;
        return this;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public FileInfoClass setFileSize(Long fileSize) {
        this.fileSize = fileSize;
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
