package com.sy.trucksysapp.page.promotion;

import com.sy.trucksysapp.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * 促销活动
 * @author Administrator 2015-11-12
 *
 *
 */
public class PromotionFragment extends Fragment {
	private LayoutInflater inflater;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.promotion_fragment,
				container, false);
		return view;
	}

}
