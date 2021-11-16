package com.iam.inventory;


import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
//import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "inventory")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Inventory {

	@Id
	@GeneratedValue
	@Type(type="org.hibernate.type.UUIDCharType") //Changes binary column to String. Default is binary you must add this anotation
	@Column(name = "id", updatable = false, nullable = false)
	//@JsonIgnore
	private UUID id;
	
	@Column(name = "product_id")
	private String productId;

	private int quantity;
	
	@Column(name = "created_at")
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
