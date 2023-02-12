package org.ncd.raspserver.repository;

import java.util.List;

import org.ncd.raspserver.entity.Raspberry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RaspberryRepo extends JpaRepository<Raspberry, String> {
	
	List<Raspberry> findByName(String name);
	
	List<Raspberry> findByGroupId(String groupId);
	List<Raspberry> findByGroupIdIn(List<String> groupIds);
}	
