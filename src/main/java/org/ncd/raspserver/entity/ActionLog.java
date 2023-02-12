package org.ncd.raspserver.entity;

import java.util.Date;

import org.ncd.raspserver.tools.Constants;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "actionlog")
public class ActionLog implements Cloneable{
	
	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "actionname")
	private String actionName;
	
	@Column(name = "remoteaddress")
	private String remoteAddress;
	
	@Column(name = "actiondate")
	@JsonFormat(pattern = Constants.OgnooniiFormat, timezone = Constants.TimeZone)
	private Date actionDate;
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();  
	}
	
}
