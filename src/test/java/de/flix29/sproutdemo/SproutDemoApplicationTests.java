package de.flix29.sproutdemo;

import de.flix29.sproutdemo.entities.generated.controllers.SproutProductEntityController;
import de.flix29.sproutdemo.entities.generated.repositories.SproutProductEntityRepository;
import de.flix29.sproutdemo.entities.generated.services.SproutProductEntityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SproutDemoApplicationTests {

    @Autowired
    private SproutProductEntityRepository repo;
    @Autowired
    private SproutProductEntityService service;
    @Autowired
    private SproutProductEntityController controller;

    @Test
    void repoBeanExists() {
        assertThat(repo).isNotNull();
    }

    @Test
    void serviceBeanExists() {
        assertThat(service).isNotNull();
    }

    @Test
    void controllerBeanExists() {
        assertThat(controller).isNotNull();
    }
}
