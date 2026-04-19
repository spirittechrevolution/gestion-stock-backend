package com.africa.samba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SambaApplication {

  public static void main(String[] args) {
    SpringApplication.run(SambaApplication.class, args);
  }
}
