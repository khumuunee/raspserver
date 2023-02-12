package org.ncd.raspserver.repository;

import java.util.Date;
import java.util.List;

import org.ncd.raspserver.entity.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionLogRepo extends JpaRepository<ActionLog, String> {
	
	List<ActionLog> findAllByActionDateBetweenOrderByActionDate(Date actionDateStart, Date actionDateEnd);
}	
