package com.sy.trucksysapp.listener;

/**
 * 排序方式选择监听器
 * @author Cool
 *
 */
public interface SortSelectedListener {
	/**
	 * 按价格升排序
	 */
	public abstract void SortByPriceUp();
	/**
	 * 按价格降排序
	 */
	public abstract void SortByPriceDown();
	/**
	 * 按销量排序
	 */
	public abstract void SortBySales();
	/**
	 * 按人气排序
	 */
	public abstract void SortByFocus();
	/**
	 * 按上架时间排序
	 */
	public abstract void SortByPutime();
}
