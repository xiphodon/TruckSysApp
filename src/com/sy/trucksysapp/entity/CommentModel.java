package com.sy.trucksysapp.entity;
/***
 * 评论
 * @author Administrator
 *
 */
public class CommentModel {
	private String imgurl;
	private float starnum;
	private String commentcontent;
	private String datestr;
	private String personname;

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public float getStarnum() {
		return starnum;
	}

	public void setStarnum(float starnum) {
		this.starnum = starnum;
	}

	public String getCommentcontent() {
		return commentcontent;
	}

	public void setCommentcontent(String commentcontent) {
		this.commentcontent = commentcontent;
	}

	public String getDatestr() {
		return datestr;
	}

	public void setDatestr(String datestr) {
		this.datestr = datestr;
	}

	public String getPersonname() {
		return personname;
	}

	public void setPersonname(String personname) {
		this.personname = personname;
	}
}
