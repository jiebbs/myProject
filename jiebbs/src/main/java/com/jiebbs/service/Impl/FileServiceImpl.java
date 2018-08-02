package com.jiebbs.service.Impl;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.jiebbs.service.IFileService;
import com.jiebbs.utils.FTPUtils;

@Service("iFileService")
public class FileServiceImpl implements IFileService {
	
	private static Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
	
	public String upload(MultipartFile file,String path) {
		String fileName = file.getOriginalFilename();
		//��ȡ��չ��
		String fileExtensionName = fileName.substring(fileName.lastIndexOf("."));
		//Ϊ�˱����ļ����ظ�,ʹ��UUID�����µ��ļ���
		String uploadFileName = UUID.randomUUID().toString()+fileExtensionName;
		logger.info("��ʼ�ϴ��ļ����ϴ����ļ���Ϊ:{},�ϴ���·��Ϊ:{},���ļ���:{}",fileName,path,uploadFileName);
		
		File fileDir = new File(path);
		//�����ļ���
		if(!fileDir.exists()) {
			//����Ȩ��Ϊ��д
			fileDir.setWritable(true);
			fileDir.mkdirs();
		}
		//�����ļ�
		File targetFile = new File(path,uploadFileName);
		//ʹ��springMvc��װ��file��
		try {
			file.transferTo(targetFile);
			//������ϲ��裬�ļ��Ѿ��ϴ��ɹ���
			
			// ��targetFile�ϴ�����ĩ��FTP��������
			FTPUtils.uploadFile(Lists.newArrayList(targetFile));
			//�������ϲ����Ѿ��ϴ�����FTP����������
			// �ϴ���֮���Ҫɾ��upload�ļ����е��ļ�
			targetFile.delete();
			
		} catch (IllegalStateException e) {
			logger.error("��������",e);
			return null;
		} catch (IOException e) {
			logger.error("�ϴ��ļ��쳣",e);
			return null;
		}
		return targetFile.getName();
	}
}
