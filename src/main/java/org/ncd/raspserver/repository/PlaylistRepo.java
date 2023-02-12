package org.ncd.raspserver.repository;

import java.util.List;

import org.ncd.raspserver.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepo extends JpaRepository<Playlist, String> {
	
	void deleteByName(String name);
	
	List<Playlist> findByIdIn(List<String> ids);
	List<Playlist> findByName(String name);
	

}	
