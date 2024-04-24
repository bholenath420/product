package com.test.product.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan("com.test.product.domain")
@EnableJpaRepositories("com.test.product.repos")
@EnableTransactionManagement
public class DomainConfig {
}
