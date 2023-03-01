package org.ncd.raspserver.service;

import java.util.Map;

import org.ncd.raspserver.tools.BaseTool;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
	

	public String login(Map<String, String> body) throws Exception {
		String userId = body.get("userId");
		String password = body.get("password");
		if(BaseTool.khoosonStringEsekh(userId) || BaseTool.khoosonStringEsekh(password))
			return "EmptyUserInfo";
		
		userId = BaseTool.khoosonZaigShakhya(userId);
		password = BaseTool.khoosonZaigShakhya(password);
		
		if(!"Admin".equals(userId) || !"Nomin2023*".equals(password))
			return "WrongInfo";		
		return "Success";
	}
	

}
