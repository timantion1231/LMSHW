package com.tima.lms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class LmsApplicationTests {

	@Test
	void contextLoads() {
		System.out.println("√ Spring context loaded successfully");
	}

	@Test
	void testApplicationStarts() {
		LmsApplication.main(new String[]{});
		System.out.println("√ Application starts successfully");
	}
}