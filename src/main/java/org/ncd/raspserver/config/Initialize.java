package org.ncd.raspserver.config;

import java.util.List;

import org.ncd.raspserver.entity.SystemConstants;
import org.ncd.raspserver.repository.ActionLogRepo;
import org.ncd.raspserver.repository.SystemConstantRepo;
import org.ncd.raspserver.tools.BaseTool;
import org.ncd.raspserver.tools.Constants;
import org.ncd.raspserver.tools.ServiceTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class Initialize implements ApplicationListener<ApplicationStartedEvent> {
	
	@Autowired SystemConstantRepo systemConstantRepo;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private ActionLogRepo actionLogRepo;

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {		
		getAllSystemConstants();
		ServiceTool.request = request;
		ServiceTool.actionLogRepo = actionLogRepo;
	}
	
	private void getAllSystemConstants(){
		List<SystemConstants> listSystemConstant = systemConstantRepo.findAll();
		if(BaseTool.khoosonJagsaaltEsekh(listSystemConstant))
			return;
		for(SystemConstants constant: listSystemConstant) {
			Constants.mainStore.put(constant.getId(), constant.getValue());
		}		
	}

}
