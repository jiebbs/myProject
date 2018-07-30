package com.jiebbs.service.Impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.daos.ClassificationMapper;
import com.jiebbs.pojos.Classification;
import com.jiebbs.service.ICategoryService;

@Service("icategoryService")
public class CategoryServiceImpl implements ICategoryService {
	private static Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
	
	@Autowired
	private ClassificationMapper classificationMapper;

	public ServerResponse<String> addCategory(String categoryName,Integer parentId) {
		if(parentId == null||!StringUtils.isNotBlank(categoryName)) {
			return ServerResponse.createByErrorMessage("���Ʒ�������������");
		}
		Classification classification = new Classification();
		classification.setName(categoryName);
		classification.setParentId(parentId);
		classification.setStatus(true);//���ô˷���״̬����
		int resultCount = classificationMapper.insert(classification);
		if(resultCount > 0) {
			return ServerResponse.createBySuccess("���ഴ���ɹ�");
		}
		return ServerResponse.createByErrorMessage("���ഴ������");
	}

	public ServerResponse<String> updataCategoryName(String newCategoryName, Integer categoryId){
		if(categoryId == null||!StringUtils.isNotBlank(newCategoryName)) {
			return ServerResponse.createByErrorMessage("���Ʒ�������������");
		}
		Classification category = new Classification();
		category.setId(categoryId);
		category.setName(newCategoryName);
		int resultCount = classificationMapper.updateByPrimaryKeySelective(category);
		if(resultCount > 0) {
			return ServerResponse.createBySuccessMessage("Ʒ�������Ѹ���");
		}
		return ServerResponse.createByErrorMessage("Ʒ�����Ƹ���ʧ��");
	}
	
	public ServerResponse<List<Classification>> getChildrenParallelCategory(Integer categoryId){
		List<Classification> list = classificationMapper.getCategoryChildrenByParentId(categoryId);
		if(CollectionUtils.isEmpty(list)) {
			logger.info("δ�ҵ���Ʒ�����Ʒ��");
		}
		return ServerResponse.createBySuccess(list);
	}
	/**
	 * �ݹ��ѯ�ڵ�ID�Լ��ӽڵ�ID
	 * @param categoryId
	 * @return
	 */
	public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
		Set<Classification> classificationSet = Sets.newHashSet();
		findChildrenCategory(classificationSet, categoryId);
		
		List<Integer> classificationList = Lists.newArrayList();
		if(categoryId!=null) {
			for(Classification classificationtemp:classificationSet) {
				classificationList.add(classificationtemp.getId());
			}
		}
		return ServerResponse.createBySuccess(classificationList);
	}
	
	//�ݹ�Ѱ����Ʒ��(����ֻ���ڲ�ʹ��)
	private Set<Classification> findChildrenCategory(Set<Classification> classificationSet,Integer categoryId){
		//���Ҹ÷�����Ϣ
		Classification classification = classificationMapper.selectByPrimaryKey(categoryId);
		//��ѯ�ķ�����Ϣ�Ƿ����
		if(classification != null) {
			classificationSet.add(classification);
		}
		//�Ը÷���IDΪ��ID,Ȼ��ʹ�õݹ��㷨�����й�ͬ��ID���ӽڵ�
		List<Classification> classificationList = classificationMapper.getCategoryChildrenByParentId(categoryId);
		for(Classification classificationTemp:classificationList) {
			findChildrenCategory(classificationSet, classificationTemp.getId());
			classificationSet.add(classificationTemp);
		}
		return classificationSet;
	}

}
