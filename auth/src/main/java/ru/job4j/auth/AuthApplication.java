package ru.job4j.auth;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

import static ru.job4j.auth.filter.JWTAuthenticationFilter.*;

@SpringBootApplication
public class AuthApplication {

   @Value("${api.token}")
   private String token;

    @Bean
    public RestTemplate getTemplate() {
        final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        Date expiresAt = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                .build()
                .verify(token.replace(TOKEN_PREFIX, ""))
                        .getExpiresAt();

      if (expiresAt.before(new Date())) {

          throw new InvalidClaimException(" The token's date has expired ");
        }

        restTemplate.getInterceptors().add(
                (outReq, bytes, clientHttpReqExec) -> {
                    outReq.getHeaders().set(
                            HEADER_STRING, TOKEN_PREFIX + token);

                    return clientHttpReqExec.execute(outReq, bytes);
                });

        return restTemplate;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

}
