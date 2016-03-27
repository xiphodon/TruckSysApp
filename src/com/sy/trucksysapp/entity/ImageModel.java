package com.sy.trucksysapp.entity;

import java.io.File;
import java.net.URI;

import com.sy.trucksysapp.utils.CommonUtils;

import android.graphics.Bitmap;
import android.net.Uri;

public class ImageModel {
	private Bitmap bitmap;
	private Uri uri;
	private Boolean isshowdelete;

	public ImageModel(Bitmap bitmap, Uri uri, Boolean isshowdelete) {
		super();
		this.bitmap = bitmap;
		this.uri = uri;
		this.setIsshowdelete(isshowdelete);
	}

	public Bitmap getBitmap() {
		if (bitmap == null && uri != null) {
			File file;
			try {
				file = new File(new URI(uri.toString()));
				bitmap = CommonUtils.revitionImageSize(file.getAbsolutePath());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public Boolean getIsshowdelete() {
		return isshowdelete;
	}

	public void setIsshowdelete(Boolean isshowdelete) {
		this.isshowdelete = isshowdelete;
	}

}
