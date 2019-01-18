package com.pix.xcwebserver.utils;

import java.util.HashMap;
import java.util.Map;

public class RSPSUtils {
    public static final String STATE_KEY = "state";
    public static final String CONTENT_KEY = "content";
    public static final String MSG_KEY = "message";
    public static final Map NullMap = new HashMap();
    public static Map<String,Object> rsp(int state,Object content,String msg) {
        Map rspMap = new HashMap<String,Object>();
        rspMap.put(STATE_KEY,state);
        if(null == content || "".equals(content)) {
            rspMap.put(CONTENT_KEY,NullMap);
        }
        else {
            rspMap.put(CONTENT_KEY,content);
        }
        rspMap.put(MSG_KEY,msg);
        return rspMap;
    }
}
