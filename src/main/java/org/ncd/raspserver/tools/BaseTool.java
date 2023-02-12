package org.ncd.raspserver.tools;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

public class BaseTool {

	private static ObjectMapper om;

	public static ObjectMapper objectMapper() {
		if (om == null) {
			om = new ObjectMapper();
			om.setDateFormat(new SimpleDateFormat(Constants.OgnooniiFormat));
			om.setTimeZone(TimeZone.getTimeZone(Constants.TimeZone));
			// Ene zaaval baikh kheregtei baina shuu. Abstact class deer asuudal uusch
			// baina.
			om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			om.findAndRegisterModules();
		}
		return om;
	}

	/*****************************************
	 * Yurunkhii methoduud
	 *****************************************/

	public static String utgataiBolgoyo(String value) {
		return value == null ? "" : value.trim();
	}

	public static String utgataiBolgoyo(Object value) {
		return value == null ? "" : value.toString();
	}

	public static BigDecimal utgataiBolgoyo(BigDecimal utga) {
		return utga == null ? BigDecimal.ZERO : utga;
	}

	public static BigDecimal toonUtgataiBolgoyo(Object utga) {
		return utga == null ? BigDecimal.ZERO : new BigDecimal(utga.toString());
	}

	public static Date utgataiBolgoyo(Date value) {
		return value == null ? ognooUusgeye(1900, 1, 1) : value;
	}

	public static short shortUtgaAvya(Object object) {
		return object == null ? 0 : Short.parseShort(object.toString());
	}

	public static <T> List<T> khoosonBolTseverleye(List<T> jagsaalt) {
		return khoosonJagsaaltEsekh(jagsaalt) ? null : jagsaalt;
	}

	public static boolean khoosonStringEsekh(String str) {
		return str == null || str.trim().isEmpty();
	}

	public static boolean khoosonStringuudEsekh(String... stringuud) {
		boolean khooson = false;
		for (String str : stringuud) {
			if (khoosonStringEsekh(str)) {
				khooson = true;
				break;
			}
		}
		return khooson;
	}

	public static boolean khoosonBigDecimalEsekh(BigDecimal utga) {
		return utga == null || utga.compareTo(BigDecimal.ZERO) == 0;
	}

	public static <T> boolean khoosonJagsaaltEsekh(List<T> jagsaalt) {
		return jagsaalt == null || jagsaalt.isEmpty();
	}

	public static <K, V> boolean khoosonMapEsekh(Map<K, V> map) {
		return map == null || map.isEmpty();
	}

	public static boolean containsString(String ekhniiUtga, String khoyordokhUtga) {
		return ekhniiUtga != null && ekhniiUtga.contains(khoyordokhUtga);
	}

	public static int jishiltBigDecimal(BigDecimal ekhniiUtga, BigDecimal khoyordokhUtga) {
		return utgataiBolgoyo(ekhniiUtga).compareTo(utgataiBolgoyo(khoyordokhUtga));
	}

	public static boolean jishiltBigDecimalTentsuu(BigDecimal ekhniiUtga, BigDecimal khoyordokhUtga) {
		return utgataiBolgoyo(ekhniiUtga).compareTo(utgataiBolgoyo(khoyordokhUtga)) == 0;
	}

	public static boolean jishiltStringTentsuu(String ekhniiUtga, String khoyordokhUtga) {
		return utgataiBolgoyo(ekhniiUtga).equalsIgnoreCase(utgataiBolgoyo(khoyordokhUtga));
	}

	public static boolean jishiltObjectTentsuu(Object ekhniiUtga, Object khoyordokhUtga) {
		return utgataiBolgoyo(ekhniiUtga).equalsIgnoreCase(utgataiBolgoyo(khoyordokhUtga));
	}

	public static boolean jishiltOgnooTentsuu(Date ekhniiUtga, Date khoyordokhUtga) {
		return utgataiBolgoyo(ekhniiUtga).compareTo(utgataiBolgoyo(khoyordokhUtga)) == 0;
	}

	public static boolean jagsaaltEsekh(Object ugugdul) {
		return ugugdul instanceof List<?>;
	}

	/*********************************** Too ***********************************/

	public static BigDecimal khuvaaya(BigDecimal khuvaagdagch, BigDecimal khuvaagch) {
		return khuvaagdagch.divide(khuvaagch, Constants.ORONGIIN_NARIIVCHLAL, Constants.ORON_BODOKH_ARGA);
	}

	/*********************************** Ognoo ***********************************/

	public static Date ognooUusgeye(int jil, int sar, int udur) {
		// Sariig 0-ees ekhlej toolno.
		sar--;
		return new GregorianCalendar(jil, sar, udur).getTime();
	}

	public static short ognoonoosJilAvya(Date ognoo) {
		if (ognoo == null)
			return (short) 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(ognoo);
		return (short) cal.get(Calendar.YEAR);
	}

	public static Date ognoonoosTsagiigKhoosloyo(Date ognoo) {
		if (ognoo == null)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(ognoo);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	public static Date ognoondTsagOnooy(Date ognoo, int hour, int minute, int second) {
		if (ognoo == null)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(ognoo);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		return cal.getTime();
	}

	public static Date ognoonooniiOnSarUdriigUnuudriinkhuurSoliyo(Date ognoo) {
		if (ognoo == null)
			return null;
		Calendar unuudriinCalendar = Calendar.getInstance();
		unuudriinCalendar.setTime(new Date());
		Calendar cal = Calendar.getInstance();
		cal.setTime(ognoo);
		cal.set(Calendar.YEAR, unuudriinCalendar.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, unuudriinCalendar.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, unuudriinCalendar.get(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	public static Date ognoonooniiOnSarUdriigUurOgnooniikhoorSoliyo(Date soligdokhOgnoo, Date solikhOgnoo) {
		if (soligdokhOgnoo == null)
			return null;
		Calendar solikhOgnooniiCalendar = Calendar.getInstance();
		solikhOgnooniiCalendar.setTime(solikhOgnoo);
		Calendar cal = Calendar.getInstance();
		cal.setTime(soligdokhOgnoo);
		cal.set(Calendar.YEAR, solikhOgnooniiCalendar.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, solikhOgnooniiCalendar.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, solikhOgnooniiCalendar.get(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	public static byte ognoonoosSarAvya(Date ognoo) {
		if (ognoo == null)
			return (byte) 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(ognoo);
		// Sariig 0-s ehelj toolj baigaa uchir 1-r nemegduulev
		int month = cal.get(Calendar.MONTH) + 1;
		return (byte) month;
	}

	public static byte ognoonoosUdurAvya(Date ognoo) {
		if (ognoo == null)
			return (byte) 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(ognoo);
		return (byte) cal.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Nyam-1, Davaa-2, ..., Byamba-7
	 */
	public static byte ognoonoosGarigAvya(Date ognoo) {
		if (ognoo == null)
			return (byte) 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(ognoo);
		return (byte) cal.get(Calendar.DAY_OF_WEEK);
	}

	public static byte ognoonoosTsagAvya(Date ognoo) {
		if (ognoo == null)
			return (byte) 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(ognoo);
		return (byte) cal.get(Calendar.HOUR_OF_DAY);
	}

	public static byte ognoonoosUliralAvya(Date ognoo) {
		int month = ognoonoosSarAvya(ognoo);
		int season;
		switch (month) {
		case 12:
		case 11:
		case 10:
			season = 4;
			break;
		case 9:
		case 8:
		case 7:
			season = 3;
			break;
		case 6:
		case 5:
		case 4:
			season = 2;
			break;
		default:
			season = 1;
			break;
		}
		return (byte) season;
	}

	public static Date ognoondUdurNemye(Date ognoo, int udur) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(ognoo);
		cal.add(Calendar.DATE, udur);
		return cal.getTime();
	}

	public static Date ognoonoosMinuteKhasya(Date ognoo, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(ognoo);
		cal.add(Calendar.MINUTE, -minute);
		return cal.getTime();
	}

	public static Date ognoonoosSekundKhasya(Date ognoo, int sekund) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(ognoo);
		cal.add(Calendar.SECOND, -sekund);
		return cal.getTime();
	}

	public static Date ognoonoosSarKhasya(Date ognoo, int khasakhUtga) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(ognoo);
		cal.add(Calendar.MONTH, (-1) * khasakhUtga);
		return cal.getTime();
	}

	public static int ognoonoosSariinSuuliinUdriigAvya(Date ognoo) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(ognoo);
		return cal.getActualMaximum(Calendar.DATE);
	}

	public static int ognooKhoorondokhUdurAvya(Date ekhniiOgnoo, Date daraagiinOgnoo) {
		long yalgaa = ekhniiOgnoo.getTime() - daraagiinOgnoo.getTime();
		return (int) TimeUnit.DAYS.convert(Math.abs(yalgaa), TimeUnit.MILLISECONDS);
	}

	public static boolean ognooKhoorondBaigaaEsekh(Date ekhniiOgnoo, Date daraagiinOgnoo, Date shalgakhOgnoo) {
		return ekhniiOgnoo.compareTo(shalgakhOgnoo) * shalgakhOgnoo.compareTo(daraagiinOgnoo) >= 0;
	}

	public static int tukhainSarAvya() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	public static String ognooniiYalgavar(Date ekhlel) {
		return ognooniiYalgavar(ekhlel, new Date());
	}

	public static String ognooniiYalgavar(Date ekhlel, Date tugsgul) {
		System.out.println("ekhlel : " + ekhlel + "    tugsgul : " + tugsgul);

//        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		if (ekhlel == null)
			ekhlel = new Date();
		if (tugsgul == null)
			tugsgul = new Date();
		long yalgavar = tugsgul.getTime() - ekhlel.getTime();

		long second = yalgavar / 1000 % 60;
		long minute = yalgavar / (60 * 1000) % 60;
		long tsag = yalgavar / (60 * 60 * 1000) % 24;
		long udor = yalgavar / (24 * 60 * 60 * 1000);
		long millisecond = yalgavar;
		if (udor > 0)
			millisecond -= udor * 24 * 60 * 60 * 1000;
		if (tsag > 0)
			millisecond -= tsag * 60 * 60 * 1000;
		if (minute > 0)
			millisecond -= minute * 60 * 1000;
		millisecond -= second * 1000;

		return (udor > 0 ? udor + " өдөр " : "") + (tsag > 0 ? tsag + " цаг " : "")
				+ (minute > 0 ? minute + " минут " : "") + (second > 0 ? second + " секунд " : "")
				+ (millisecond > 0 ? millisecond + " доль " : "");
	}

	/*****************************************
	 * Khurvuulelt
	 *****************************************/

	/*
	 * LinkedHashMap -aas uuriin class-ruu khurvuulekh
	 */
	public static <T> T convertMapToObject(Class<T> className, Object object) {
		return objectMapper().convertValue(object, className);
	}

	/*
	 * LinkedHashMap -aas uuriin class-ruu khurvuulekh
	 */
	public static <T> List<T> convertLinkedToList(Class<T> className, List<?> object) {
		CollectionType constructCollectionType = objectMapper().getTypeFactory().constructCollectionType(List.class,
				className);
		return objectMapper().convertValue(object, constructCollectionType);
	}

	public static <T> T convertStringToObject(Class<T> classType, String jsonString) throws IOException {
		return objectMapper().readValue(jsonString, classType);
	}

	public static <T> String convertObjectToString(T t) throws JsonProcessingException {
		if (t == null)
			return null;
		return objectMapper().writeValueAsString(t);
	}

	public static <T> List<T> convertStringToList(Class<T> className, String object) throws Exception {
		CollectionType constructCollectionType = objectMapper().getTypeFactory().constructCollectionType(List.class,
				className);
		return objectMapper().readValue(object, constructCollectionType);
	}

	public static <T> List<T> convertIterableToList(Iterable<T> source) {
		List<T> jagsaalt = new ArrayList<>();
		source.forEach(jagsaalt::add);
		return jagsaalt;
	}

	public static Date convertStringToDate(String dateInString) {
		List<String> formatuud = Arrays.asList(Constants.OgnooniiFormat, Constants.EngiinOgnooniiFormat, "yyyy/MM/dd",
				"yyyy.MM.dd", "yy-MM-dd", "yy/MM/dd", "yy.MM.dd");
		Date butsakhOgnoo = null;
		for (String format : formatuud) {
			try {
				butsakhOgnoo = convertStringToDate(dateInString, format);
				break;
			} catch (Exception ex) {
				if (formatuud.indexOf(format) == formatuud.size() - 1)
					throw new RuntimeException(ex);
			}
		}
		return butsakhOgnoo;
	}

	@SuppressWarnings("unused")
	public static boolean ognooEsekh(Object utga) {
		boolean ognooEsekh = false;
		try {
			Date date = convertStringToDate(utga.toString(), Constants.OgnooniiFormat);
			ognooEsekh = true;
		} catch (Exception ignored) {
		}
		return ognooEsekh;
	}

	public static Date convertStringToDate(String dateInString, String pattern) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.parse(dateInString);
	}

	public static String convertDateToString(Date ognoo) {
		return convertDateToString(ognoo, Constants.OgnooniiFormat);
	}

//    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean ognooniiFormatZuvEsekh(String ognoo, String pattern) {
		try {
			convertStringToDate(ognoo, pattern);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static String convertDateToString(Date ognoo, String pattern) {
		if (ognoo == null)
			return null;
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(ognoo);
	}

	public static BigDecimal convertStringToBigDecimal(String str) {
		str = toonTemdegtiigYalgajAvya(str);
		return new BigDecimal(str);
	}

	@SuppressWarnings("deprecation")
	public static Object cloneObject(Object obj) {
		try {
			Object clone = obj.getClass().newInstance();
			for (Field field : obj.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				if (field.get(obj) == null || Modifier.isFinal(field.getModifiers())) {
					continue;
				}
				if (field.getType().equals(List.class) || (field.getType().isPrimitive()
						|| field.getType().equals(String.class) || field.getType().getSuperclass().equals(Number.class)
						|| field.getType().equals(Boolean.class))) {
					field.set(clone, field.get(obj));
				} else {
					Object childObj = field.get(obj);
					if (childObj == obj) {
						field.set(clone, clone);
					} else {
						field.set(clone, cloneObject(field.get(obj)));
					}
				}
			}
			return clone;
		} catch (Exception e) {
			return null;
		}
	}

	public static String khoosonZaigShakhya(String str) {
		return str.replaceAll("\\s+", "");
	}

	public static String toonTemdegtiigYalgajAvya(String str) {
		if (khoosonStringEsekh(str))
			return str;
		str = khoosonZaigShakhya(str);
		if (str.contains(","))
			str = str.replaceAll(",", "");
		return str;
	}

	public static String jagsaaltStringuudiigNiiluulye(List<String> jagsaaltString) {
		return "\"" + StringUtils.join(jagsaaltString, "\",\"") + "\"";
	}

	public static String kavichkandakhStringiigYalgajAvya(String string) {
		Pattern p = Pattern.compile("\"([^\"]*)\"");
		Matcher m = p.matcher(string);
		String yalgagdsanString = "";
		while (m.find()) {
			yalgagdsanString = m.group(1);
		}
		return yalgagdsanString;
	}

	public static String textiinTemdegtiigSoliyo(String text, String soligdokhTemdegt, String solikhTemdegt) {
		if (khoosonStringEsekh(text) || khoosonStringEsekh(soligdokhTemdegt) || solikhTemdegt == null)
			return text;
		return text.replaceAll(soligdokhTemdegt, solikhTemdegt);
	}

	public static List<String> textiigIjilUrttaiKhuvaaya(String text, int temdegtiinUrt) {
		List<String> jagsaalt = new ArrayList<>((text.length() + temdegtiinUrt - 1) / temdegtiinUrt);
		for (int i = 0; i < text.length(); i += temdegtiinUrt) {
			jagsaalt.add(text.substring(i, Math.min(text.length(), i + temdegtiinUrt)));
		}
		return jagsaalt;
	}
	
	public static void logKhevleye(String khevlekhUgs) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String s = formatter.format(Calendar.getInstance().getTime());
        System.out.println(s + " => " + khevlekhUgs);
    }
}
