package com.belvinard.rbac_mfa_starter_springboot;

import org.springframework.boot.SpringApplication;

public class TestRbacMfaStarterSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.from(RbacMfaStarterSpringbootApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
