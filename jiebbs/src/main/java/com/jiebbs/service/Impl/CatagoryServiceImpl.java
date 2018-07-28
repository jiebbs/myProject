package com.jiebbs.service.Impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiebbs.common.ServerResponse;
import com.jiebbs.daos.ClassificationMapper;
import com.jiebbs.pojos.Classification;
import com.jiebbs.service.ICatagoryService;

@Service("iCatagoryService")
public class CatagoryServiceImpl implements ICatagoryService {
	
	@Autowired
	private ClassificationMapper classificationMapper;

	public ServerResponse<String> addCatagory(String catagoryName,Integer parentId) {
		if(parentId == null||!StringUtils.isNotBlank(catagoryName)) {
			return ServerResponse.createByErrorMessage("���Ʒ�������������");
		}
		Classification classification = new Classification();
		classification.setName(catagoryName);
		classification.setParentId(parentId);
		classification.setStatus(true);//���ô˷���״̬����
		int resultCount = classificationMapper.createCatagory(catagoryName, parentId);
		if(resultCount > 0) {
			return ServerResponse.createBySuccess("���ഴ���ɹ�");
		}
		return ServerResponse.createByErrorMessage("���ഴ������");
	}

}
