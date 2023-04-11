package com.UDT.outbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.jms.annotation.EnableJms;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;

@SpringBootApplication
@EnableJms
@EnableAutoConfiguration
@EnableCaching
public class OutboundApplication {
	@Autowired
	private HeadersService serv;

	public static void main(String[] args) {
		SpringApplication.run(OutboundApplication.class, args);
	}
}
