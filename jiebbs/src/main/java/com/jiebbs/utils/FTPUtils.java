package com.jiebbs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPUtils {
	
	private static Logger logger = LoggerFactory.getLogger(FTPUtils.class);
	
	private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
	
	private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
	
	private static String ftpPassword = PropertiesUtil.getProperty("ftp.pass");
	
	private String ip;
	private int port;
	private String user;
	private String password;
	
	
	public static boolean uploadFile(List<File> fileList)throws IOException {
		FTPUtils ftpUtils = new FTPUtils(ftpIp,21,ftpUser,ftpPassword);
		logger.info("��ʼ����FTP������");
		boolean result = ftpUtils.uploadFile("image",fileList);
		logger.info("�����ϴ����ϴ������{}");
		return result;
	}
	
	//ftp�ϴ����߼�д������ط�
	/**
	 * @param remotePath Զ��·��
	 * @param fileList �ϴ����ļ�
	 * @return
	 */
	private boolean uploadFile(String remotePath,List<File> fileList)throws IOException {
		boolean uploaded = true;
		FileInputStream is = null;
		
		//����Ftp������
		if(connectServerFTP(this.ip,this.port,this.user,this.password)) {
			try {
				ftpClient.changeWorkingDirectory(remotePath);
				ftpClient.setBufferSize(1024);
				ftpClient.setControlEncoding("UTF-8");
				//��ֹ�ļ�����
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				//�������ر���ģʽ
				ftpClient.enterLocalPassiveMode();
				
				//��ʼ�ϴ��ļ�
				for(File temp:fileList){
					is = new FileInputStream(temp);
					ftpClient.storeFile(temp.getName(),is);
				}
			}catch(Exception e) {
				logger.error("�ϴ��ļ��쳣",e);
				uploaded = false;
			}finally {
				is.close();
				ftpClient.disconnect();
			}
		}
		return uploaded;
	}
	
	private boolean connectServerFTP(String ip, int port, String user, String password) {
		ftpClient = new FTPClient();
		boolean isSuccess = false;
		try {
			ftpClient.connect(ip);
			isSuccess = ftpClient.login(user, password);
		} catch (SocketException e) {
			logger.error("FTP�����������쳣",e);
		} catch (IOException e) {
			logger.error("FTP�����������쳣",e);
		}
		return isSuccess;
	}
	
	public FTPUtils(String ip, int port, String user, String password) {
		super();
		this.ip = ip;
		this.port = port;
		this.user = user;
		this.password = password;
	}
	private FTPClient ftpClient;
	
	
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
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPasswoed() {
		return password;
	}
	public void setPasswoed(String passwoed) {
		this.password = passwoed;
	}
	public FTPClient getFtpClient() {
		return ftpClient;
	}
	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}
}
