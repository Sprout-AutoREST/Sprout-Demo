package de.flix29.sproutdemo;

import de.flix29.sproutdemo.entities.generated.controllers.SproutProductEntityController;
import de.flix29.sproutdemo.entities.generated.controllers.SproutShoppingBasketController;
import de.flix29.sproutdemo.entities.generated.repositories.SproutProductEntityRepository;
import de.flix29.sproutdemo.entities.generated.repositories.SproutShoppingBasketRepository;
import de.flix29.sproutdemo.entities.generated.services.SproutProductEntityService;
import de.flix29.sproutdemo.entities.generated.services.SproutShoppingBasketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SproutDemoApplicationTests {

    @Autowired
    private SproutProductEntityRepository sproutProductEntityRepository;
    @Autowired
    private SproutProductEntityService sproutProductEntityService;
    @Autowired
    private SproutProductEntityController sproutProductEntityController;

    @Autowired
    private SproutShoppingBasketRepository sproutShoppingBasketEntityRepository;
    @Autowired
    private SproutShoppingBasketService sproutShoppingBasketEntityService;
    @Autowired
    private SproutShoppingBasketController sproutShoppingBasketEntityController;

    @Test
    void repoBeanExists() {
        assertThat(sproutProductEntityRepository).isNotNull();
        assertThat(sproutShoppingBasketEntityRepository).isNotNull();
    }

    @Test
    void serviceBeanExists() {
        assertThat(sproutProductEntityService).isNotNull();
        assertThat(sproutShoppingBasketEntityService).isNotNull();
    }

    @Test
    void controllerBeanExists() {
        assertThat(sproutProductEntityController).isNotNull();
        assertThat(sproutShoppingBasketEntityController).isNotNull();
    }
}
