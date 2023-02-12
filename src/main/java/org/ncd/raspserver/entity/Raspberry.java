package org.ncd.raspserver.entity;

import java.util.Date;
import java.util.List;

import org.ncd.raspserver.tools.Constants;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "raspberry")
public class Raspberry implements Cloneable{
	
	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "name")
	private String name;
	
	@Column(name = "ipaddress")
	private String ipAddress;
	
	@Column(name = "groupid")
	private String groupId;

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
	
	@Transient
	private String currentStatus;
	
	@Transient
	private String statusMessage;
	
	@Transient
	private List<String> listDownloadSoundName;
	
	@Transient
	private List<String> listDownloadAdSoundName;	
	
	@Transient
	private List<String> listDownloadScheduledSoundName;
	
	@Transient
	private int songCount;
	
	@Transient
	private int adCount;
	
	@Transient
	private int scheduledSoundCount;
	
	
	public Raspberry(String id, String name, String ipAddress, Date createdDate, Date updatedDate, String createdUser, String updatedUser) {
		this.id = id;
		this.name = name;
		this.ipAddress = ipAddress;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.createdUser = createdUser;
		this.updatedUser = updatedUser;

	}
	
	public Raspberry(String id, String currentStatus) {
		this.id = id;
		this.currentStatus = currentStatus;
	}
	
}
