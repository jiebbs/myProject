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
		//获取扩展名
		String fileExtensionName = fileName.substring(fileName.lastIndexOf("."));
		//为了避免文件名重复,使用UUID创建新的文件名
		String uploadFileName = UUID.randomUUID().toString()+fileExtensionName;
		logger.info("开始上传文件，上传的文件名为:{},上传的路径为:{},新文件名:{}",fileName,path,uploadFileName);
		
		File fileDir = new File(path);
		//建立文件夹
		if(!fileDir.exists()) {
			//设置权限为可写
			fileDir.setWritable(true);
			fileDir.mkdirs();
		}
		//建立文件
		File targetFile = new File(path,uploadFileName);
		//使用springMvc封装的file类
		try {
			file.transferTo(targetFile);
			//完成以上步骤，文件已经上传成功了
			
			// 将targetFile上传到文末的FTP服务器上
			FTPUtils.uploadFile(Lists.newArrayList(targetFile));
			//经过以上步骤已经上传到了FTP服务器当中
			// 上传完之后就要删除upload文件夹中的文件
			targetFile.delete();
			
		} catch (IllegalStateException e) {
			logger.error("参数错误",e);
			return null;
		} catch (IOException e) {
			logger.error("上传文件异常",e);
			return null;
		}
		return targetFile.getName();
	}
}
