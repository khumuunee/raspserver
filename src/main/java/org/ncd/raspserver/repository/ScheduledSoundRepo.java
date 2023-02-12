package org.ncd.raspserver.repository;

import java.util.List;

import org.ncd.raspserver.entity.ScheduledSound;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledSoundRepo extends JpaRepository<ScheduledSound, String> {
	
	List<ScheduledSound> findByGroupId(String groupId);
	
	void deleteByGroupId(String groupId);
	
	List<ScheduledSound> findBySoundName(String soundName);
	
	long countByGroupId(String groupId);
}	
