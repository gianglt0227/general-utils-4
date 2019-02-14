/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GiangLT
 */
public class FtpUtil {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final FTPClient ftp = new FTPClient();
    private final Integer DEFAULT_PORT = 21;

    public void connect(String serverIp, String username, String password) throws Exception {
        connect(serverIp, DEFAULT_PORT, username, password);
    }

    public void connect(String serverIp, Integer serverPort, String username, String password) throws Exception {
        ftp.connect(serverIp, serverPort);
        logger.debug("Connected to {}:{}", serverIp, serverPort);

        if (!ftp.login(username, password)) {
            logger.error("Unable to login with U/P: {} / {}", username, password);
            ftp.logout();
            return;
        }
        logger.debug("Logged in with U/P: {} / ******", username);

        int reply = ftp.getReplyCode();
        //FTPReply stores a set of constants for FTP reply codes. 
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return;
        }
        logger.debug("Server replied with Positive Completion Code");

        //enter passive mode
        ftp.enterLocalPassiveMode();
        logger.debug("Enter Local Passive mode");
        //get system name
        logger.debug("Remote system is {}", ftp.getSystemType());
    }

    public void setBinaryMode() throws IOException {
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        logger.debug("Set tranfer mode to binary");
    }

    public void download(String remoteDir, String localDir, FTPFileFilter fileFilter) {
        try {
            //change current directory
            ftp.changeWorkingDirectory(remoteDir);
            logger.debug("Change directory to {}", ftp.printWorkingDirectory());

            FTPFile[] listedFiles = ftp.listFiles(null, fileFilter);
//            DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
            if (listedFiles != null && listedFiles.length > 0) {
                for (FTPFile listedFile : listedFiles) {
                    OutputStream os = null;
                    try {
                        File retrivedFile = new File(localDir + File.separatorChar + listedFile.getName());
                        if (!retrivedFile.exists()) {
                            os = new FileOutputStream(retrivedFile);
                            boolean isRetrived = ftp.retrieveFile(listedFile.getName(), os);
                            if (isRetrived) {
                                logger.debug("Downloaded file to: {}", retrivedFile.getAbsolutePath());
                            }
                        }
                    } catch (Exception ex) {
                        logger.error("", ex);
                    } finally {
                        IOUtils.closeQuietly(os);
                    }

                }

            }

        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    public void changeDir(String remoteDir) throws IOException {
        ftp.changeWorkingDirectory(remoteDir);
        boolean positiveCompletion = FTPReply.isPositiveCompletion(ftp.getReplyCode());
        if (!positiveCompletion) {
            logger.debug("Remote folder is not available, so creating it: {}", remoteDir);
            ftp.makeDirectory(remoteDir);
        }
        ftp.changeWorkingDirectory(remoteDir);
        logger.debug("Change remote dir to: {}", remoteDir);
    }

    public void upload(String remoteDir, String localDir, FileFilter fileFilter) throws Exception {
        File localFolder = new File(localDir);
        File[] files = localFolder.listFiles(fileFilter);
        upload(remoteDir, files);
    }

    public void upload(String remoteDir, File[] files) throws Exception {
        for (File file : files) {
            upload(remoteDir, file);
        }
    }

    public boolean upload(String remoteDir, File file) throws Exception {
        InputStream input = null;
        try {
            logger.trace("Uploading file: {}", file.getName());
            input = new FileInputStream(file);
            boolean isUploaded = this.ftp.storeFile(remoteDir + File.separator + file.getName(), input);
            if (isUploaded) {
                logger.debug("Uploaded file: {}", file.getName());
            } else {
                logger.debug("Uploaded FAILED: {}", file.getName());
            }
            return isUploaded;
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    public void disconnect() throws Exception {
        if (ftp.isConnected()) {
            ftp.logout();
            logger.debug("FTP logged out");
            ftp.disconnect();
            logger.debug("Disconnected from server");
        }
    }

    public FTPClient getFtp() {
        return ftp;
    }
}
