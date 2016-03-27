package com.sy.trucksysapp.utils;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 序列化map供Bundle传递map使用
 * @author lxs 20150519
 *
 */
public class SerializableMap implements Serializable {
	private HashMap<String, String> map;
	 
    public HashMap<String, String> getMap() {
        return map;
    }
 
    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }
}
