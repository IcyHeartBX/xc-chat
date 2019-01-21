package com.guagua.gofun.http;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*************************************************************************************
* Module Name: 网络接口请求帮助类
* File Name: SignHelper.java
* Description: 添加签名的网络请求帮助类
* Author: 郭晓明
* 版权 2008-2015，浙江齐聚科技有限公司 
* 所有版权保护
* 这是浙江齐聚科技有限公司 未公开的私有源代码, 本文件及相关内容未经浙江齐聚科技有限公司 
* 事先书面同意，不允许向任何第三方透露，泄密部分或全部; 也不允许任何形式的私自备份。
***************************************************************************************/
public class SignHelper {
	private static final String TAG = "SignHelper";
	private static final String KEY = "N26C@G6VH$";
	private static final String KEY_BILLING = "key=ux7YIWRVw0";
	
	public static void addSign(Map<String, String> sArray){
		Map<String, String> map = paraFilter(sArray);
		String str= createLinkString(map);
		String _sign=SHA1(str+"&"+KEY);
		sArray.put("sign", _sign);
	}

	/**
	 * 新的签名逻辑需要将请求头中的一些信息也加入签名
	 * Author : tpx
	 * @param sArray
	 * @param sHeader
     */
	public static void addSignWithHeader(Map<String, String> sArray, Map<String,String> sHeader){
		Map<String,String> signMap = new HashMap<>();
		signMap.putAll(sArray);
		// 将头中的一些参数加入签名逻辑
		String did = sHeader.get("did");
		if(!TextUtils.isEmpty(did)) {
			signMap.put("did",did);
		}

		String network = sHeader.get("network");
		if(!TextUtils.isEmpty(network)) {
			signMap.put("network",network);
		}

		String oemid = sHeader.get("oemid");
		if(!TextUtils.isEmpty(oemid)) {
			signMap.put("oemid",oemid);
		}

		String version = sHeader.get("version");
		if(!TextUtils.isEmpty(version)) {
			signMap.put("version",version);
		}

		String sy = sHeader.get("sy");
		if(!TextUtils.isEmpty(sy)) {
			signMap.put("sy",sy);
		}

		String language = sHeader.get("language");
		if(!TextUtils.isEmpty(language)) {
			signMap.put("language",language);
		}

		String channel = sHeader.get("channel");
		if(!TextUtils.isEmpty(channel)) {
			signMap.put("channel",channel);
		}

		String userid = sHeader.get("userid");
		if(!TextUtils.isEmpty(userid)) {
			signMap.put("userid",userid);
		}

		String webToken = sHeader.get("webToken");
		if(!TextUtils.isEmpty(webToken)) {
			signMap.put("webToken",webToken);
		}

		String ts = sHeader.get("ts");
		if(!TextUtils.isEmpty(ts)) {
			signMap.put("ts",ts);
		}

		Map<String, String> map = paraFilter(signMap);
		String str= createLinkString(map);
		//LogUtils.d(TAG,"createLinkString(map)=" + createLinkString(map));
		String _sign=SHA1(str+"&"+KEY);
		sArray.put("sign", _sign);
	}

	public static String getSignUseHeaders(Map<String, String> sArray, Map<String,String> sHeader) {
		Map<String,String> signMap = new HashMap<>();
		signMap.putAll(sArray);
		// 将头中的一些参数加入签名逻辑
		String did = sHeader.get("did");
		if(!TextUtils.isEmpty(did)) {
			signMap.put("did",did);
		}

		String network = sHeader.get("network");
		if(!TextUtils.isEmpty(network)) {
			signMap.put("network",network);
		}

		String oemid = sHeader.get("oemid");
		if(!TextUtils.isEmpty(oemid)) {
			signMap.put("oemid",oemid);
		}

		String version = sHeader.get("version");
		if(!TextUtils.isEmpty(version)) {
			signMap.put("version",version);
		}

		String sy = sHeader.get("sy");
		if(!TextUtils.isEmpty(sy)) {
			signMap.put("sy",sy);
		}

		String language = sHeader.get("language");
		if(!TextUtils.isEmpty(language)) {
			signMap.put("language",language);
		}

		String channel = sHeader.get("channel");
		if(!TextUtils.isEmpty(channel)) {
			signMap.put("channel",channel);
		}

		String userid = sHeader.get("userid");
		if(!TextUtils.isEmpty(userid)) {
			signMap.put("userid",userid);
		}

		String webToken = sHeader.get("webToken");
		if(!TextUtils.isEmpty(webToken)) {
			signMap.put("webToken",webToken);
		}

		String ts = sHeader.get("ts");
		if(!TextUtils.isEmpty(ts)) {
			signMap.put("ts",ts);
		}

		Map<String, String> map = paraFilter(signMap);
		String str= createLinkString(map);
		String _sign=SHA1(str+"&"+KEY);
//		sArray.put("sign", _sign);
		return _sign;
	}

	public static void addBillingSign(Map<String,String> params) {
		Map<String, String> map = paraFilter(params);
		String str = createLinkString(map);
		String md5 = MD5.getMD5(str + "&" + KEY_BILLING);
		params.put("sign", md5.toUpperCase());
	}
	
	 /** 
     * 除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    private static Map<String, String> paraFilter(Map<String, String> sArray) {
    	HashMap<String, String> result = new HashMap<String, String>();

        if (sArray == null || sArray.size() <= 0) 
            return result;

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                || key.equalsIgnoreCase("sign_type") || key.equalsIgnoreCase("signtype")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }
	
	
	/**
	 * 使用SAH1进行加密
	 *  @param str 需要加密的字符串
	 *  @return String 加密后的值
	 */
    private static String SHA1(String str){
    	if(TextUtils.isEmpty(str))
    		return "";
    	
		String value="";
		try {
    		MessageDigest md = MessageDigest.getInstance("SHA-1");
    		md.update(str.getBytes("UTF-8"));
    		byte[] result = md.digest();
    		StringBuffer sb = new StringBuffer();
    		for (byte b : result) {
    			int i = b & 0xff;
    			if (i < 0xf) {
    				sb.append(0);
    			}
    			sb.append(Integer.toHexString(i));
    		}
    		value=sb.toString().toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return value;
	}
	
	 /** 
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    private static String createLinkString(Map<String, String> params) {
    	if(params == null)
    		return null;
    	
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }
}
