package org.ncd.raspserver.repository;

import java.util.List;

import org.ncd.raspserver.entity.RaspberrysScheduledSoundGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RaspberrysScheduledSoundGroupRepo extends JpaRepository<RaspberrysScheduledSoundGroup, String> {
	
	List<RaspberrysScheduledSoundGroup> findByRaspberryId(String raspberryId);
	
	void deleteByRaspberryId(String raspberryId);
	
	void deleteByGroupId(String groupId);
	
	long countByRaspberryId(String raspberryId);
	
}	
