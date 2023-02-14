package org.ncd.raspserver.repository;

import java.util.List;

import org.ncd.raspserver.entity.RaspberryGroupScheduledSoundGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RaspberryGroupScheduledSoundGroupRepo extends JpaRepository<RaspberryGroupScheduledSoundGroup, String> {

	List<RaspberryGroupScheduledSoundGroup> findByRaspberryGroupId(String raspberryGroupId);

	void deleteByRaspberryGroupId(String raspberryGroupId);

	void deleteByGroupId(String groupId);

	long countByRaspberryGroupId(String raspberryGroupId);

}
