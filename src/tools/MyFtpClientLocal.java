package tools;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author caiyongqing
 * @date 2025-03-07
 */
public class MyFtpClientLocal {

    private static FTPClient ftpClient = null;

    private static final String endPoint = "10.199.4.77";
    private static final String ak = "ftpServer";
    private static final String sk = "1qaz2wsx";

    public static FTPClient getFtpClient() {
        try {
            ftpClient = new FTPClient();
            // 连接FTP服务器
            ftpClient.connect(endPoint, 21);
            // 登陆FTP服务器
            ftpClient.login(ak, sk);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return ftpClient;
    }

    public static void changeWorkingPath(FTPClient ftpClient, String path) throws Exception {
        // 每次切换时，都从先切换到根路径
        String rootPath = "/";
        if (ftpClient.changeWorkingDirectory(rootPath)) {
            makeDictionary(ftpClient, path);
        }
    }

    /**
     * 循环创建文件夹
     *
     * @param ftpClient FTPClient对象
     * @param workPath  要创建的目录
     */
    private static void makeDictionary(FTPClient ftpClient, String workPath) throws IOException {
        if (!ftpClient.changeWorkingDirectory(workPath)) {
            List<String> pathNameList = Arrays.stream(workPath.split("/")).filter(item -> !item.isEmpty()).collect(Collectors.toList());
            for (String itemPath : pathNameList) {
                boolean isChangeDirectoryBefore = ftpClient.changeWorkingDirectory(itemPath);
                if (!isChangeDirectoryBefore) {
                    ftpClient.makeDirectory(itemPath);
                    boolean isChangeDirectoryAfter = ftpClient.changeWorkingDirectory(itemPath);
                    if (!isChangeDirectoryAfter) {
                        throw new FileNotFoundException();
                    }
                }
            }
        }
    }
}
