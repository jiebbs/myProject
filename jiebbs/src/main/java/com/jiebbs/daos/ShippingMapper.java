package com.jiebbs.daos;

import com.jiebbs.pojos.Shipping;

public interface ShippingMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table shipping_table
	 * @mbg.generated  Sat Jul 21 15:36:47 CST 2018
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table shipping_table
	 * @mbg.generated  Sat Jul 21 15:36:47 CST 2018
	 */
	int insert(Shipping record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table shipping_table
	 * @mbg.generated  Sat Jul 21 15:36:47 CST 2018
	 */
	int insertSelective(Shipping record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table shipping_table
	 * @mbg.generated  Sat Jul 21 15:36:47 CST 2018
	 */
	Shipping selectByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table shipping_table
	 * @mbg.generated  Sat Jul 21 15:36:47 CST 2018
	 */
	int updateByPrimaryKeySelective(Shipping record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table shipping_table
	 * @mbg.generated  Sat Jul 21 15:36:47 CST 2018
	 */
	int updateByPrimaryKey(Shipping record);
}