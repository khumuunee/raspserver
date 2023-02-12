package org.ncd.raspserver.entity;

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
@Table(name = "systemconstants")
public class SystemConstants {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "value")
	private String value;

}
