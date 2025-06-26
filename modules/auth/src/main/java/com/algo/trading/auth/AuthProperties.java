//package com.algo.trading.auth;
//
//import lombok.Data;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@ConfigurationProperties(prefix = "app.auth")
//@Data
//public class AuthProperties {
//    private String apiKey;
//    private String apiSecret;
//    private String redirectUrl;
//}

package com.algo.trading.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.auth")
@Data
public class AuthProperties {
    private String apiKey;
    private String apiSecret;
    // no requestToken here—you accept it dynamically in generateSession(...)
}
