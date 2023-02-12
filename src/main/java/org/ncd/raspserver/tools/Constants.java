package org.ncd.raspserver.tools;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class Constants {
	
	public static final String TimeZone = "Asia/Ulaanbaatar";
    public static final String OgnooniiFormat = "yyyy-MM-dd HH:mm:ss";
    public static final String TsagiinFormat = "HH:mm:ss";
    public static final String EngiinOgnooniiFormat = "yyyy-MM-dd";

    public static final String TusgaiTemdegtuud = "[!@#$%^&*()--+=,?\":;`~'/\\{} |<>]";
    public static final String KhoriglokhTemdegtuud = "[!@#$%^&*()--+=,?\":;`~'/\\{}|<>]";

    public static final int ORONGIIN_NARIIVCHLAL = 2;
    public static final RoundingMode ORON_BODOKH_ARGA = RoundingMode.HALF_UP;
    
    public static final Map<String, Object> mainStore = new HashMap<>();

}
