package com.jiebbs.service;

import java.util.List;

import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojos.Classification;

public interface ICategoryService {

	ServerResponse<String> addCategory(String catagoryName,Integer parentId);
	
	ServerResponse<String> updataCategoryName(String newCategoryName, Integer catagoryId);
	
	ServerResponse<List<Classification>> getChildrenParallelCategory(Integer categoryId);
	
	ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}