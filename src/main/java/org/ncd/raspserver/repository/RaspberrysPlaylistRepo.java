package org.ncd.raspserver.repository;

import java.util.List;

import org.ncd.raspserver.entity.RaspberrysPlaylist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RaspberrysPlaylistRepo extends JpaRepository<RaspberrysPlaylist, String> {
	
	List<RaspberrysPlaylist> findByRaspberryId(String raspberryId);
	
	List<RaspberrysPlaylist> findByPlaylistId(String playlistId);
	
	void deleteByRaspberryId(String raspberryId);
	
	long countByRaspberryId(String raspberryId);	
	
	
//	List<RaspberrysPlaylist> findBySoundName(String soundName);
}	
