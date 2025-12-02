package com.pradumcodes.notes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
public class NotesApplication {

    @RequestMapping("/")
    public String Home() {
        return "Hello World!";
    }

	public static void main(String[] args) {
		SpringApplication.run(NotesApplication.class, args);
	}

}
