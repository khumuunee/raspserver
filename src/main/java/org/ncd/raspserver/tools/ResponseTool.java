package org.ncd.raspserver.tools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.ncd.raspserver.model.MyException;

public class ResponseTool {
	
	public enum ResCode{Success, Warning, Error}
	public final static String ResCodeStr = "RES_CODE";
	private final static String WarningMessageStr = "WARNING_MESSAGE";
	public final static String ErrorMessageStr = "ERROR_MESSAGE";
	private final static String ErrorFullMessageStr = "ERROR_FULL_MESSAGE";
	
	public static Map<String, Object> createRes() {		
		return createRes(null);
	}
	public static Map<String, Object> createRes(Map<String, Object> param) {
		Map<String, Object> res = new HashMap<>();
		res.put(ResCodeStr, ResCode.Success);
		if(param != null)
			param.forEach((k,v)-> res.put(k, v));
		return res;
	}
	
	public static Map<String, Object> createResWithError(Exception ex) {
		Map<String, Object> res = new HashMap<>();
		if(ex instanceof MyException) {
			res.put(ResCodeStr, ResCode.Warning);
			res.put(WarningMessageStr, ((MyException)ex).getWarningMessage());
		}
		else {
			res.put(ResCodeStr, ResCode.Error);
			res.put(ErrorMessageStr, ex.getMessage());
			res.put(ErrorFullMessageStr, getFullStackMessage(ex));
		}
		return res;
	}
	
	private static String getFullStackMessage(Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		return sw.toString();
	}

}
