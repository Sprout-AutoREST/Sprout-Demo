package de.flix29.sproutdemo.entities.generated.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SproutProductEntityControllerSmokeTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getProductsRespondsOk() {
        ResponseEntity<String> response = restTemplate.getForEntity("/products", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
