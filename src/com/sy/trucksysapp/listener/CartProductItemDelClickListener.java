package com.sy.trucksysapp.listener;

/**
 * 购物车商品状态改变监听器
 * 
 * @author Cool
 */
public interface CartProductItemDelClickListener {
	/**
	 * 商品数量改变
	 */
	public abstract void itemDelClick(int position);

}
