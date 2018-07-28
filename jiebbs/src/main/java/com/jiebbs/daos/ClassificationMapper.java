package com.jiebbs.daos;

import org.apache.ibatis.annotations.Param;

import com.jiebbs.pojos.Classification;

public interface ClassificationMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table classification_table
	 * @mbg.generated  Sat Jul 21 15:36:47 CST 2018
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table classification_table
	 * @mbg.generated  Sat Jul 21 15:36:47 CST 2018
	 */
	int insert(Classification record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table classification_table
	 * @mbg.generated  Sat Jul 21 15:36:47 CST 2018
	 */
	int insertSelective(Classification record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table classification_table
	 * @mbg.generated  Sat Jul 21 15:36:47 CST 2018
	 */
	Classification selectByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table classification_table
	 * @mbg.generated  Sat Jul 21 15:36:47 CST 2018
	 */
	int updateByPrimaryKeySelective(Classification record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table classification_table
	 * @mbg.generated  Sat Jul 21 15:36:47 CST 2018
	 */
	int updateByPrimaryKey(Classification record);
	
	/**
	 * 根据分类名和类级创建分类
	 * @param catagoryName
	 * @param parentId
	 * @return
	 */
	int createCatagory(@Param("catagoryName")String catagoryName,@Param("parentId")Integer parentId);
}