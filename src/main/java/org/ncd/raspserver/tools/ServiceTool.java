package org.ncd.raspserver.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.ncd.raspserver.entity.ActionLog;
import org.ncd.raspserver.repository.ActionLogRepo;

import jakarta.servlet.http.HttpServletRequest;

public class ServiceTool {
	
	public static HttpServletRequest request;
	public static ActionLogRepo actionLogRepo;

	public static String getSoundFolderPath() throws Exception {
		String soundFolderPath = (String) Constants.mainStore.get("SOUND_FOLDER_PATH");
		if (BaseTool.khoosonStringEsekh(soundFolderPath))
			throw new Exception("SOUND_FOLDER_PATH not found from database");
		return soundFolderPath;
	}

	public static String getFolderPathSlash() throws Exception {
		String soundFolderPath = (String) Constants.mainStore.get("FOLDER_PATH_SLASH");
		if (BaseTool.khoosonStringEsekh(soundFolderPath))
			throw new Exception("FOLDER_PATH_SLASH not found from database");
		return soundFolderPath;
	}

	public static String getRaspRestServiceUrl() throws Exception {
		String raspRestServiceUrl = (String) Constants.mainStore.get("RASPBERRY_RESTSERVICE_URL");
		if (BaseTool.khoosonStringEsekh(raspRestServiceUrl))
			throw new Exception("RASPBERRY_RESTSERVICE_URL not found from database");
		return raspRestServiceUrl;
	}

	public static String generateId() {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
		String id = ft.format(dNow);
		id = id + String.format("%03d", 0);
		String lastGeneratedId = (String) Constants.mainStore.get("LastGeneratedId");
		if (lastGeneratedId != null && BaseTool.jishiltStringTentsuu(id, lastGeneratedId)) {
			int number = Integer.parseInt(lastGeneratedId.substring(lastGeneratedId.length() - 3));
			number++;
			id = lastGeneratedId.substring(0, lastGeneratedId.length() - 3) + String.format("%03d", number);
		}
		Constants.mainStore.put("LastGeneratedId", id);
		return id;
	}

	public static String increaseIdByOne(String id) {
		int number = Integer.parseInt(id.substring(id.length() - 3));
		number++;
		id = id.substring(0, id.length() - 3) + String.format("%03d", number);
		return id;
	}

	public static void runTaskWithDelay(Runnable runnable, long delay) {
		new java.util.Timer().schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				runnable.run();
			}
		}, delay);
	}
	
	public static void createActionLog(String actionName) {
		ActionLog actionLog = new ActionLog();
		actionLog.setId(generateId());
		actionLog.setActionDate(new Date());
		actionLog.setActionName(actionName);
		actionLog.setRemoteAddress(request.getRemoteAddr());
		actionLogRepo.save(actionLog);
	}

}
