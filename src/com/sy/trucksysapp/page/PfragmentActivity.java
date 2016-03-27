package com.sy.trucksysapp.page;

import com.sy.trucksysapp.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class PfragmentActivity extends FragmentActivity {
	private Fragment fragment;
	private String classname;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_common_fragment);
		try {
			classname = getIntent().getStringExtra("classname");
			Class ff = Class.forName(classname);
			fragment = (Fragment) ff.newInstance();
			Bundle b = new Bundle();  
			b.putBoolean("isselect", true);
			fragment.setArguments(b);  
			android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.id_frament, fragment);
			transaction.commit();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
