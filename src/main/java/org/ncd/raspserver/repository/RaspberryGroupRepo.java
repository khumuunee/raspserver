package org.ncd.raspserver.repository;

import org.ncd.raspserver.entity.RaspberryGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RaspberryGroupRepo extends JpaRepository<RaspberryGroup, String> {
	
	void deleteByName(String name);
		

}	
