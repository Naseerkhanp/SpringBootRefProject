package com.excelacom.century.apolloneoutbound.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Message {
	private String responseCode;
    private String description;
}
