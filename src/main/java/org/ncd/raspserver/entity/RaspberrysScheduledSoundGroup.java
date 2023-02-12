package org.ncd.raspserver.entity;

import java.util.Date;

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
@Table(name = "raspberrysscheduledsoundgroup")
public class RaspberrysScheduledSoundGroup implements Cloneable{
	
	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "raspberryId")
	private String raspberryId;
	
	@Column(name = "groupid")
	private String groupId;

	@Column(name = "createddate")
	@JsonFormat(pattern = Constants.OgnooniiFormat, timezone = Constants.TimeZone)
	private Date createdDate;
	
	@Column(name = "createduser")
	private String createdUser;
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();  
	}
	
	@Transient
	private String groupName;
	
}
