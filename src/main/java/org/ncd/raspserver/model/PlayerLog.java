package org.ncd.raspserver.model;

import java.util.Date;

import org.ncd.raspserver.tools.Constants;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerLog implements Cloneable{
	
	private String id;

	private String soundName;
	
	@JsonFormat(pattern = Constants.OgnooniiFormat, timezone = Constants.TimeZone)
	private Date time;
	
	private String status;
	
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();  
	}
	
}
