package org.ncd.raspserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sound {
	
	private String name;
	/**
	 * Song
	 * Ad
	 * ScheduledSound
	 */
	private String type;
	private boolean checked;	
	
//	public String getSoundType() {
//		if("Ad".equals(type))
//			return type;
//		return "Song";
//	}

}
