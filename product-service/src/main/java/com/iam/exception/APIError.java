package com.iam.exception;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class APIError {

	private int status;
	private String message;
	private List<String> errors;
	private String path;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime timeStamp;
	
	
	 public APIError(int status, String message, String error,String path,LocalDateTime timeStamp) {
	        super();
	        this.status = status;
	        this.message = message;
	        errors = Arrays.asList(error);
	        this.path= path;
	        this.timeStamp=timeStamp;
	    }
}
