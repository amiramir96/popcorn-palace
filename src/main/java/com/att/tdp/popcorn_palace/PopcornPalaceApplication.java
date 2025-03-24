package com.att.tdp.popcorn_palace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
Main of the application.
Before running the program, please check the Instructions.md
	and verify that you have up to date java version, PostgreSQL and Maven.
*/

@SpringBootApplication
public class PopcornPalaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PopcornPalaceApplication.class, args);
	}
}
