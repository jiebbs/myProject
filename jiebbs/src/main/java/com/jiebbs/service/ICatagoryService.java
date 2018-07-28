package com.jiebbs.service;

import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojos.Classification;

public interface ICatagoryService {

	ServerResponse<String> addCatagory(String catagoryName,Integer parentId);
	
}