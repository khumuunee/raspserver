package org.ncd.raspserver.repository;

import org.ncd.raspserver.entity.SystemConstants;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemConstantRepo extends JpaRepository<SystemConstants, String> {

}	
