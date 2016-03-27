package com.sy.trucksysapp.listener;

/**
 * 购物车商品状态改变监听器
 * @author Cool
 */
public interface CartProductItemClickListener {
	/**
	 * 商品数量改变
	 */
	public abstract void itemNumChanged(int position,int num);
	/**
	 * 商品选中状态
	 */
	public abstract void itemCilck(int position,boolean isCheck);
}
