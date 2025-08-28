package de.flix29.sproutdemo;

import de.flix29.sproutdemo.entities.ProductEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SmokeTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getProducts_Response_OK() {
        save();

        ResponseEntity<String> response = restTemplate.getForEntity("/products", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void postProducts_Response_Created() {
        var product = new ProductEntity("V60 Filters", new java.math.BigDecimal("5.50"));
        ResponseEntity<String> response = restTemplate.postForEntity("/products", product, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void getProductsId_Response_OK() {
        long id = save();

        ResponseEntity<String> response = restTemplate.getForEntity("/products/{id}", String.class, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void deleteProducts_Response_OK() {
        long id = save();

        ResponseEntity<String> deleteResponse = restTemplate.exchange("/products/{id}", HttpMethod.DELETE, HttpEntity.EMPTY, String.class, id);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> response = restTemplate.getForEntity("/products/{id}", String.class, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private long save() {
        var product = new ProductEntity("V60 Filters", new BigDecimal("5.50"));
        var created = restTemplate.postForEntity("/products", product, ProductEntity.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        return created.getBody().getId();
    }
}
