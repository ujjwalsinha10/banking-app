package com.account;

import com.commons.security.DefaultSecurityConfig;
import com.commons.security.FeignTokenRelayConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import({DefaultSecurityConfig.class, FeignTokenRelayConfig.class})
@SpringBootApplication(scanBasePackages = {"com.account", "com.commons"})
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

}
