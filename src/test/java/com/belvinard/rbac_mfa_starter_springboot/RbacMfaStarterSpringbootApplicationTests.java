package com.belvinard.rbac_mfa_starter_springboot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class RbacMfaStarterSpringbootApplicationTests {

	@Test
	void contextLoads() {
	}

}
