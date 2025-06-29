package com.algo.trading.market.config;

import com.algo.trading.auth.config.AuthKiteBeanConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * TODO - Temporary class to import the auth configs so Spring sees the beans. Remove this when auth has HTTP apis ready
 */
@Configuration
@Import(AuthKiteBeanConfig.class)
public class AuthBeansImport {
}
