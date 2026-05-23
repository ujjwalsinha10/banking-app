package com.rtr.AuthUser;

import com.commons.security.DefaultSecurityConfig;
import com.commons.security.FeignTokenRelayConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.rtr.Authuser",        // your service's own code
        "com.commons",
        "com.authcore.service" // include beans from auth-core
})
@EnableFeignClients(basePackages = "com.authcore.client")
@Import({DefaultSecurityConfig.class, FeignTokenRelayConfig.class})
public class AuthUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthUserApplication.class, args);
    }

}
