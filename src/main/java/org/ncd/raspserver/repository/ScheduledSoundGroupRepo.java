package org.ncd.raspserver.repository;

import org.ncd.raspserver.entity.ScheduledSoundGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledSoundGroupRepo extends JpaRepository<ScheduledSoundGroup, String> {
	
	void deleteByName(String name);
	

}	
