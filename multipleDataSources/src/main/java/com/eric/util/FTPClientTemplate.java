package com.eric.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.SocketException;

/**
 * Title: jobplus <br>
 * Description: FTP客户端 <br>
 */
@Service
@ConfigurationProperties(prefix = "ftp")
public class FTPClientTemplate {

    private static org.slf4j.Logger log = LoggerFactory.getLogger(FTPClientTemplate.class);
    private ThreadLocal<FTPClient> ftpClientThreadLocal = new ThreadLocal<FTPClient>();

    public String ip;

    public int port;

    public String username;

    public String passwd;

    public String fileDir;

    public String imgDir;

    public String headIconDir;

    private boolean binaryTransfer = true;
    private boolean passiveMode = true;
    private String encoding = "UTF-8";
    private int clientTimeout = 1000 * 30;

    /**
     * 返回一个FTPClient实例
     *
     * @throws Exception
     */
    private FTPClient getFTPClient() throws Exception {
        if (ftpClientThreadLocal.get() != null && ftpClientThreadLocal.get().isConnected()) {
            return ftpClientThreadLocal.get();
        } else {
            FTPClient ftpClient = new FTPClient(); // 构造一个FtpClient实例
            ftpClient.setControlEncoding(encoding); // 设置字符集

            connect(ftpClient); // 连接到ftp服务器

            // 设置为passive模式
            if (passiveMode) {
                ftpClient.enterLocalPassiveMode();
            }
            setFileType(ftpClient); // 设置文件传输类型

            try {
                ftpClient.setSoTimeout(clientTimeout);
            } catch (SocketException e) {
                throw new Exception("Set timeout error.", e);
            }
            ftpClientThreadLocal.set(ftpClient);
            return ftpClient;
        }
    }

    /**
     * 设置文件传输类型
     *
     * @throws Exception
     * @throws IOException
     */
    private void setFileType(FTPClient ftpClient) throws Exception {
        try {
            if (binaryTransfer) {
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            } else {
                ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);
            }
        } catch (IOException e) {
            throw new Exception("Could not to set file type.", e);
        }
    }

    /**
     * 连接到ftp服务器
     *
     * @param ftpClient
     * @return 连接成功返回true，否则返回false
     * @throws Exception
     */
    private boolean connect(FTPClient ftpClient) throws Exception {
        try {
            ftpClient.connect(ip, port);

            // 连接后检测返回码来校验连接是否成功
            int reply = ftpClient.getReplyCode();

            if (FTPReply.isPositiveCompletion(reply)) {
                // 登陆到ftp服务器
                if (ftpClient.login(username, passwd)) {
                    setFileType(ftpClient);
                    return true;
                }
            } else {
                ftpClient.disconnect();
                throw new Exception("FTP server refused connection.");
            }
        } catch (IOException e) {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect(); // 断开连接
                } catch (IOException e1) {
                    throw new Exception("Could not disconnect from server.", e1);
                }

            }
            throw new Exception("Could not connect to server.", e);
        }
        return false;
    }

    // ---------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------

    /**
     * 断开ftp连接
     *
     * @throws Exception
     */
    public void disconnect() throws Exception {
        try {
            FTPClient ftpClient = getFTPClient();
            ftpClient.logout();
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
                ftpClient = null;
            }
        } catch (IOException e) {
            throw new Exception("Could not disconnect from server.", e);
        }
    }

    public boolean mkdir(String pathname) throws Exception {
        return mkdir(pathname, null);
    }

    /**
     * 在ftp服务器端创建目录（不支持一次创建多级目录）
     * <p>
     * 该方法执行完后将自动关闭当前连接
     *
     * @param pathname
     * @return
     * @throws Exception
     */
    public boolean mkdir(String pathname, String workingDirectory) throws Exception {
        return mkdir(pathname, workingDirectory, true);
    }

    /**
     * 在ftp服务器端创建目录（不支持一次创建多级目录）
     *
     * @param pathname
     * @param autoClose 是否自动关闭当前连接
     * @return
     * @throws Exception
     */
    public boolean mkdir(String pathname, String workingDirectory, boolean autoClose) throws Exception {
        try {
            getFTPClient().changeWorkingDirectory(workingDirectory);
            return getFTPClient().makeDirectory(pathname);
        } catch (IOException e) {
            throw new Exception("Could not mkdir.", e);
        } finally {
            if (autoClose) {
                disconnect(); // 断开连接
            }
        }
    }

    /**
     * 上传一个本地文件到远程指定文件
     *
     * @param remoteAbsoluteFile 远程文件名(包括完整路径)
     * @param localAbsoluteFile  本地文件名(包括完整路径)
     * @return 成功时，返回true，失败返回false
     * @throws Exception
     */
    public boolean put(String remoteAbsoluteFile, String localAbsoluteFile) throws Exception {
        return put(remoteAbsoluteFile, localAbsoluteFile, true);
    }

    /**
     * 上传一个本地文件到远程指定文件
     *
     * @param remoteAbsoluteFile 远程文件名(包括完整路径)
     * @param localAbsoluteFile  本地文件名(包括完整路径)
     * @param autoClose          是否自动关闭当前连接
     * @return 成功时，返回true，失败返回false
     * @throws Exception
     */
    public boolean put(String remoteAbsoluteFile, String localAbsoluteFile, boolean autoClose) throws Exception {
        InputStream input = null;
        try {
            // 处理传输
            input = new FileInputStream(localAbsoluteFile);
            getFTPClient().storeFile(remoteAbsoluteFile, input);
            log.debug("put " + localAbsoluteFile);
            return true;
        } catch (FileNotFoundException e) {
            throw new Exception("local file not found.", e);
        } catch (IOException e) {
            throw new Exception("Could not put file to server.", e);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception e) {
                throw new Exception("Couldn't close FileInputStream.", e);
            }
            if (autoClose) {
                disconnect(); // 断开连接
            }
        }
    }

    /**
     * 上传一个文件流到远程指定文件
     *
     * @param is        文件流
     * @param remote    远程文件名(包括完整路径)
     * @param autoClose 是否自动关闭当前连接
     * @return FTPStatus
     * @throws Exception
     */
    public FTPStatus upload(InputStream is, String remote, boolean autoClose) throws Exception {

        // 设置PassiveMode传输
        FTPClient ftpClient = getFTPClient();
        ftpClient.enterLocalPassiveMode();

        // 设置以二进制流的方式传输
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        FTPStatus result = null;
        try {
            // 对远程目录的处理
            String remoteFileName = remote;

            if (remote.contains("/")) {

                remoteFileName = remote.substring(remote.lastIndexOf("/") + 1);

                String directory = remote.substring(0, remote.lastIndexOf("/") + 1);

                if (!directory.equalsIgnoreCase("/") && !ftpClient.changeWorkingDirectory(directory)) {

                    // 如果远程目录不存在，则递归创建远程服务器目录

                    int start = 0;

                    int end = 0;

                    if (directory.startsWith("/")) {

                        start = 1;

                    } else {

                        start = 0;

                    }

                    end = directory.indexOf("/", start);

                    while (true) {

                        String subDirectory = remote.substring(start, end);

                        if (!ftpClient.changeWorkingDirectory(subDirectory)) {

                            if (ftpClient.makeDirectory(subDirectory)) {

                                ftpClient.changeWorkingDirectory(subDirectory);

                            } else {

                                log.info("创建目录失败");

                                return FTPStatus.Create_Directory_Fail;

                            }

                        }

                        start = end + 1;

                        end = directory.indexOf("/", start);

                        // 检查所有目录是否创建完毕

                        if (end <= start) {

                            break;

                        }

                    }

                }

            }

            // 检查远程是否存在文件

            FTPFile[] files = ftpClient.listFiles(remoteFileName);

            if (files.length == 1) {

                long remoteSize = files[0].getSize();

                long localSize = is.available();

                if (remoteSize == localSize) {

                    return FTPStatus.File_Exits;

                } else if (remoteSize > localSize) {

                    return FTPStatus.Remote_Bigger_Local;

                }

                // 尝试移动文件内读取指针,实现断点续传

                if (is.skip(remoteSize) == remoteSize) {

                    ftpClient.setRestartOffset(remoteSize);

                    if (ftpClient.storeFile(new String(remote.getBytes("UTF-8"), "iso-8859-1"), is)) {

                        return FTPStatus.Upload_From_Break_Success;

                    }

                }

                // 如果断点续传没有成功，则删除服务器上文件，重新上传

                if (!ftpClient.deleteFile(remoteFileName)) {

                    return FTPStatus.Delete_Remote_Faild;

                }

                if (ftpClient.storeFile(new String(remote.getBytes("UTF-8"), "iso-8859-1"), is)) {

                    result = FTPStatus.Upload_New_File_Success;

                } else {

                    result = FTPStatus.Upload_New_File_Failed;

                }

            } else {

                if (ftpClient.storeFile(new String(remoteFileName.getBytes("UTF-8"), "iso-8859-1"), is)) {

                    result = FTPStatus.Upload_New_File_Success;

                } else {

                    result = FTPStatus.Upload_New_File_Failed;

                }

            }

        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                throw new Exception("Couldn't close FileInputStream.", e);
            }
            if (autoClose) {
                disconnect(); // 断开连接
            }
        }
        log.info("FTP操作状态码:" + result);

        return result;

    }


    /**
     * 上传一个本地文件到远程指定文件
     *
     * @param local     本地文件(包括完整路径)
     * @param remote    远程文件名(包括完整路径)
     * @param autoClose 是否自动关闭当前连接
     * @return FTPStatus
     * @throws Exception
     */
    public FTPStatus upload(String local, String remote, boolean autoClose) throws Exception {

        // 设置PassiveMode传输
        InputStream is = new FileInputStream(local);
        FTPClient ftpClient = getFTPClient();
        ftpClient.enterLocalPassiveMode();

        // 设置以二进制流的方式传输
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        FTPStatus result = null;
        try {
            // 对远程目录的处理
            String remoteFileName = remote;

            if (remote.contains("/")) {

                remoteFileName = remote.substring(remote.lastIndexOf("/") + 1);

                String directory = remote.substring(0, remote.lastIndexOf("/") + 1);

                if (!directory.equalsIgnoreCase("/") && !ftpClient.changeWorkingDirectory(directory)) {

                    // 如果远程目录不存在，则递归创建远程服务器目录

                    int start = 0;

                    int end = 0;

                    if (directory.startsWith("/")) {

                        start = 1;

                    } else {

                        start = 0;

                    }

                    end = directory.indexOf("/", start);

                    while (true) {

                        String subDirectory = remote.substring(start, end);

                        if (!ftpClient.changeWorkingDirectory(subDirectory)) {

                            if (ftpClient.makeDirectory(subDirectory)) {

                                ftpClient.changeWorkingDirectory(subDirectory);

                            } else {

                                log.info("创建目录失败");

                                return FTPStatus.Create_Directory_Fail;

                            }

                        }

                        start = end + 1;

                        end = directory.indexOf("/", start);

                        // 检查所有目录是否创建完毕

                        if (end <= start) {

                            break;

                        }

                    }

                }

            }

            // 检查远程是否存在文件

            FTPFile[] files = ftpClient.listFiles(remoteFileName);

            if (files.length == 1) {

                long remoteSize = files[0].getSize();

                long localSize = is.available();

                if (remoteSize == localSize) {

                    return FTPStatus.File_Exits;

                } else if (remoteSize > localSize) {

                    return FTPStatus.Remote_Bigger_Local;

                }

                // 尝试移动文件内读取指针,实现断点续传

                if (is.skip(remoteSize) == remoteSize) {

                    ftpClient.setRestartOffset(remoteSize);

                    if (ftpClient.storeFile(new String(remote.getBytes("UTF-8"), "iso-8859-1"), is)) {

                        return FTPStatus.Upload_From_Break_Success;

                    }

                }

                // 如果断点续传没有成功，则删除服务器上文件，重新上传

                if (!ftpClient.deleteFile(remoteFileName)) {

                    return FTPStatus.Delete_Remote_Faild;

                }

                if (ftpClient.storeFile(new String(remote.getBytes("UTF-8"), "iso-8859-1"), is)) {

                    result = FTPStatus.Upload_New_File_Success;

                } else {

                    result = FTPStatus.Upload_New_File_Failed;

                }

            } else {

                if (ftpClient.storeFile(new String(remoteFileName.getBytes("UTF-8"), "iso-8859-1"), is)) {

                    result = FTPStatus.Upload_New_File_Success;

                } else {

                    result = FTPStatus.Upload_New_File_Failed;

                }

            }

        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                throw new Exception("Couldn't close FileInputStream.", e);
            }
            if (autoClose) {
                disconnect(); // 断开连接
            }
        }
        log.info("FTP操作状态码:" + result);

        return result;

    }

    /**
     * 下载一个远程文件到本地的指定文件
     *
     * @param remoteAbsoluteFile 远程文件名(包括完整路径)
     * @param localAbsoluteFile  本地文件名(包括完整路径)
     * @return 成功时，返回true，失败返回false
     * @throws Exception
     */
    public boolean get(String remoteAbsoluteFile, String localAbsoluteFile) throws Exception {
        return get(remoteAbsoluteFile, localAbsoluteFile, true);
    }

    /**
     * 下载一个远程文件到本地的指定文件
     *
     * @param remoteAbsoluteFile 远程文件名(包括完整路径)
     * @param localAbsoluteFile  本地文件名(包括完整路径)
     * @param autoClose          是否自动关闭当前连接
     * @return 成功时，返回true，失败返回false
     * @throws Exception
     */
    public boolean get(String remoteAbsoluteFile, String localAbsoluteFile, boolean autoClose) throws Exception {
        OutputStream output = null;
        try {
            output = new FileOutputStream(localAbsoluteFile);
            return get(remoteAbsoluteFile, output, autoClose);
        } catch (FileNotFoundException e) {
            throw new Exception("local file not found.", e);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                throw new Exception("Couldn't close FileOutputStream.", e);
            }
        }
    }

    /**
     * 下载一个远程文件到指定的流 处理完后记得关闭流
     *
     * @param remoteAbsoluteFile
     * @param output
     * @return
     * @throws Exception
     */
    public boolean get(String remoteAbsoluteFile, OutputStream output) throws Exception {
        return get(remoteAbsoluteFile, output, true);
    }

    /**
     * 下载一个远程文件到指定的流 处理完后记得关闭流
     *
     * @param remoteAbsoluteFile
     * @param output
     * @param autoClose
     * @return
     * @throws Exception
     */
    public boolean get(String remoteAbsoluteFile, OutputStream output, boolean autoClose) throws Exception {
        try {
            FTPClient ftpClient = getFTPClient();
            // 处理传输
            return ftpClient.retrieveFile(remoteAbsoluteFile, output);
        } catch (IOException e) {
            throw new Exception("Couldn't get file from server.", e);
        } finally {
            if (autoClose) {
                disconnect(); // 关闭链接
            }
        }
    }

    /**
     * 从ftp服务器上删除一个文件 该方法将自动关闭当前连接
     *
     * @param delFile
     * @return
     * @throws Exception
     */
    public boolean delete(String delFile) throws Exception {
        return delete(delFile, true);
    }

    /**
     * 从ftp服务器上删除一个文件
     *
     * @param delFile
     * @param autoClose 是否自动关闭当前连接
     * @return
     * @throws Exception
     */
    public boolean delete(String delFile, boolean autoClose) throws Exception {
        try {
            getFTPClient().deleteFile(delFile);
            return true;
        } catch (IOException e) {
            throw new Exception("Couldn't delete file from server.", e);
        } finally {
            if (autoClose) {
                disconnect(); // 关闭链接
            }
        }
    }

    /**
     * 批量删除 该方法将自动关闭当前连接
     *
     * @param delFiles
     * @return
     * @throws Exception
     */
    public boolean delete(String[] delFiles) throws Exception {
        return delete(delFiles, true);
    }

    /**
     * 批量删除
     *
     * @param delFiles
     * @param autoClose 是否自动关闭当前连接
     * @return
     * @throws Exception
     */
    public boolean delete(String[] delFiles, boolean autoClose) throws Exception {
        try {
            FTPClient ftpClient = getFTPClient();
            for (String s : delFiles) {
                ftpClient.deleteFile(s);
            }
            return true;
        } catch (IOException e) {
            throw new Exception("Couldn't delete file from server.", e);
        } finally {
            if (autoClose) {
                disconnect(); // 关闭链接
            }
        }
    }

    /**
     * 列出远程默认目录下所有的文件
     *
     * @return 远程默认目录下所有文件名的列表，目录不存在或者目录下没有文件时返回0长度的数组
     * @throws Exception
     */
    public String[] listNames() throws Exception {
        return listNames(null, true);
    }

    public String[] listNames(boolean autoClose) throws Exception {
        return listNames(null, autoClose);
    }

    /**
     * 列出远程目录下所有的文件
     *
     * @param remotePath 远程目录名
     * @param autoClose  是否自动关闭当前连接
     * @return 远程目录下所有文件名的列表，目录不存在或者目录下没有文件时返回0长度的数组
     * @throws Exception
     */
    public String[] listNames(String remotePath, boolean autoClose) throws Exception {
        try {
            String[] listNames = getFTPClient().listNames(remotePath);
            return listNames;
        } catch (IOException e) {
            throw new Exception("列出远程目录下所有的文件时出现异常", e);
        } finally {
            if (autoClose) {
                disconnect(); // 关闭链接
            }
        }
    }


    public ThreadLocal<FTPClient> getFtpClientThreadLocal() {
        return ftpClientThreadLocal;
    }

    public void setFtpClientThreadLocal(ThreadLocal<FTPClient> ftpClientThreadLocal) {
        this.ftpClientThreadLocal = ftpClientThreadLocal;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public String getImgDir() {
        return imgDir;
    }

    public void setImgDir(String imgDir) {
        this.imgDir = imgDir;
    }

    public String getHeadIconDir() {
        return headIconDir;
    }

    public void setHeadIconDir(String headIconDir) {
        this.headIconDir = headIconDir;
    }

    public boolean isBinaryTransfer() {
        return binaryTransfer;
    }

    public void setBinaryTransfer(boolean binaryTransfer) {
        this.binaryTransfer = binaryTransfer;
    }

    public boolean isPassiveMode() {
        return passiveMode;
    }

    public void setPassiveMode(boolean passiveMode) {
        this.passiveMode = passiveMode;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public int getClientTimeout() {
        return clientTimeout;
    }

    public void setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
    }

    public static void main(String[] args) throws Exception {
        FTPClientTemplate ftp = new FTPClientTemplate();
        ftp.setIp("192.168.0.39");
        ftp.setPort(21);
        ftp.setUsername("jobftp");
        ftp.setPasswd("kS@O$J!B.");
        ftp.setBinaryTransfer(false);
        ftp.setPassiveMode(false);
        ftp.setEncoding("utf-8");
        try {
            String remotePath = "imgDir/2017/09/28/88_9d6ef69c52ca4f3c9c1726005dd2cbe2.jpg";
            FTPStatus result = ftp.upload("C:\\Users\\eric\\Pictures\\1.jpg", remotePath, true);
            System.out.println(result);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
