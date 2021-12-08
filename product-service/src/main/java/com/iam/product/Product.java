package com.iam.product;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("deprecation")
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "product")
public class Product {
	
	@Id
	@GeneratedValue
	@Type(type="org.hibernate.type.UUIDCharType") //Changes binary column to String. Default is binary you must add this anotation
	@Column(name = "id", updatable = false, nullable = false)
	//@JsonIgnore
	private UUID id;
	
	private String name;
	
	private BigDecimal price;
	
	private String description;
	

}
