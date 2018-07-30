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
			return ServerResponse.createByErrorMessage("添加品类参数输入有误");
		}
		Classification classification = new Classification();
		classification.setName(categoryName);
		classification.setParentId(parentId);
		classification.setStatus(true);//设置此分类状态可用
		int resultCount = classificationMapper.insert(classification);
		if(resultCount > 0) {
			return ServerResponse.createBySuccess("分类创建成功");
		}
		return ServerResponse.createByErrorMessage("分类创建错误");
	}

	public ServerResponse<String> updataCategoryName(String newCategoryName, Integer categoryId){
		if(categoryId == null||!StringUtils.isNotBlank(newCategoryName)) {
			return ServerResponse.createByErrorMessage("添加品类参数输入有误");
		}
		Classification category = new Classification();
		category.setId(categoryId);
		category.setName(newCategoryName);
		int resultCount = classificationMapper.updateByPrimaryKeySelective(category);
		if(resultCount > 0) {
			return ServerResponse.createBySuccessMessage("品类名称已更新");
		}
		return ServerResponse.createByErrorMessage("品类名称更新失败");
	}
	
	public ServerResponse<List<Classification>> getChildrenParallelCategory(Integer categoryId){
		List<Classification> list = classificationMapper.getCategoryChildrenByParentId(categoryId);
		if(CollectionUtils.isEmpty(list)) {
			logger.info("未找到该品类的子品类");
		}
		return ServerResponse.createBySuccess(list);
	}
	/**
	 * 递归查询节点ID以及子节点ID
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
	
	//递归寻找子品类(方法只供内部使用)
	private Set<Classification> findChildrenCategory(Set<Classification> classificationSet,Integer categoryId){
		//查找该分类信息
		Classification classification = classificationMapper.selectByPrimaryKey(categoryId);
		//查询的分类信息是否存在
		if(classification != null) {
			classificationSet.add(classification);
		}
		//以该分类ID为父ID,然后使用递归算法查找有共同父ID的子节点
		List<Classification> classificationList = classificationMapper.getCategoryChildrenByParentId(categoryId);
		for(Classification classificationTemp:classificationList) {
			findChildrenCategory(classificationSet, classificationTemp.getId());
			classificationSet.add(classificationTemp);
		}
		return classificationSet;
	}

}
