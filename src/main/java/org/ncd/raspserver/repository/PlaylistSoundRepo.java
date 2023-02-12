package org.ncd.raspserver.repository;

import java.util.List;

import org.ncd.raspserver.entity.PlaylistSound;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistSoundRepo extends JpaRepository<PlaylistSound, String> {
	
	List<PlaylistSound> findByPlaylistId(String playlistId);
	
	void deleteByPlaylistId(String playlistId);
	
	List<PlaylistSound> findBySoundName(String soundName);
	List<PlaylistSound> findBySoundNameAndSoundType(String soundName, String soundType);
	
	long countByPlaylistIdAndSoundType(String playlistId, String soundType);
}	
