package org.ncd.raspserver.repository;

import java.util.List;

import org.ncd.raspserver.entity.RaspberryGroupsPlaylist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RaspberryGroupsPlaylistRepo extends JpaRepository<RaspberryGroupsPlaylist, String> {
	
	List<RaspberryGroupsPlaylist> findByRaspberryGroupId(String raspberryGroupId);
	
	List<RaspberryGroupsPlaylist> findByPlaylistId(String playlistId);
	
	void deleteByRaspberryGroupId(String raspberryGroupId);
	
	long countByRaspberryGroupId(String raspberryGroupId);	
	
}
