package de.flix29.sproutdemo;

import de.flix29.sproutdemo.entities.generated.repositories.SproutProductEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SproutDemoApplicationTests {
    @Autowired
    SproutProductEntityRepository repo;

    @Test void repoBeanExists() {
        assertThat(repo).isNotNull();
    }
}