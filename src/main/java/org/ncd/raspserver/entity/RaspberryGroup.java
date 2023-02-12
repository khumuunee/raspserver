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
@Table(name = "raspberrygroup")
public class RaspberryGroup implements Cloneable{
	
	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "name")
	private String name;

	@Column(name = "raspberrycount")
	private int raspberryCount;
	
	@Column(name = "createddate")
	@JsonFormat(pattern = Constants.OgnooniiFormat, timezone = Constants.TimeZone)
	private Date createdDate;
	
	@Column(name = "updateddate")
	@JsonFormat(pattern = Constants.OgnooniiFormat, timezone = Constants.TimeZone)
	private Date updatedDate;
	
	@Column(name = "createduser")
	private String createdUser;
	
	@Column(name = "updateduser")
	private String updatedUser;
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();  
	}

}
