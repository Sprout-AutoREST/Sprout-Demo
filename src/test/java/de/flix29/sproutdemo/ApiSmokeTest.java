package de.flix29.sproutdemo;

import de.flix29.sproutdemo.entities.ProductEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiSmokeTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void list_create_get_delete() {
        var list = rest.getForEntity("/products", List.class);
        assertThat(list.getStatusCode()).isEqualTo(HttpStatus.OK);

        var p = new ProductEntity("V60 Filters", new BigDecimal("5.50"));
        var created = rest.postForEntity("/products", p, ProductEntity.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var id = created.getBody().getId();

        var got = rest.getForEntity("/products/{id}", ProductEntity.class, id);
        assertThat(got.getStatusCode()).isEqualTo(HttpStatus.OK);

        var del = rest.exchange("/products/{id}", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, id);
        assertThat(del.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}