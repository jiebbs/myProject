package com.jiebbs.daos;

import com.jiebbs.pojos.Product;

public interface ProductMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table product_table
	 * @mbg.generated  Sat Jul 21 15:36:47 CST 2018
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table product_table
	 * @mbg.generated  Sat Jul 21 15:36:47 CST 2018
	 */
	int insert(Product record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table product_table
	 * @mbg.generated  Sat Jul 21 15:36:47 CST 2018
	 */
	int insertSelective(Product record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table product_table
	 * @mbg.generated  Sat Jul 21 15:36:47 CST 2018
	 */
	Product selectByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table product_table
	 * @mbg.generated  Sat Jul 21 15:36:47 CST 2018
	 */
	int updateByPrimaryKeySelective(Product record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table product_table
	 * @mbg.generated  Sat Jul 21 15:36:47 CST 2018
	 */
	int updateByPrimaryKey(Product record);
}