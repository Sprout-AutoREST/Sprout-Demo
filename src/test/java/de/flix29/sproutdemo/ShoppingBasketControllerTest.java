package de.flix29.sproutdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.flix29.sprout.runtime.security.SproutMethodSecurityConfiguration;
import de.flix29.sproutdemo.entities.ProductEntity;
import de.flix29.sproutdemo.entities.ShoppingBasketEntity;
import de.flix29.sproutdemo.entities.UserEntity;
import de.flix29.sproutdemo.entities.generated.controllers.SproutShoppingBasketController;
import de.flix29.sproutdemo.entities.generated.services.SproutShoppingBasketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SproutShoppingBasketController.class)
@ImportAutoConfiguration(SproutMethodSecurityConfiguration.class)
@WithMockUser(roles = "USER")
class ShoppingBasketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SproutShoppingBasketService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllReturnsShoppingBaskets() throws Exception {
        given(service.findAll()).willReturn(List.of(sampleBasket(1)));

        mockMvc.perform(get("/baskets").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAllReturnsEmptyListWhenNoBaskets() throws Exception {
        given(service.findAll()).willReturn(List.of());

        mockMvc.perform(get("/baskets").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getByIdReturnsShoppingBasket() throws Exception {
        ShoppingBasketEntity basket = sampleBasket(1);
        given(service.findById(1)).willReturn(Optional.of(basket));

        mockMvc.perform(get("/baskets/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.username").value("jane"))
                .andExpect(jsonPath("$.products[0].name").value("Widget"));
    }

    @Test
    void getByIdReturnsNotFoundWhenMissing() throws Exception {
        given(service.findById(42)).willReturn(Optional.empty());

        mockMvc.perform(get("/baskets/42").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReturnsCreatedShoppingBasket() throws Exception {
        ShoppingBasketEntity created = sampleBasket(1);
        given(service.save(any(ShoppingBasketEntity.class))).willReturn(created);

        mockMvc.perform(post("/baskets").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleBasket(null))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.email").value("jane@example.com"));
    }

    @Test
    void createInvalidMediaTypeReturnsUnsupportedMediaType() throws Exception {
        mockMvc.perform(post("/baskets").with(csrf())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("invalid"))
                .andExpect(status().isUnsupportedMediaType());
    }

    private ShoppingBasketEntity sampleBasket(Integer id) {
        ShoppingBasketEntity basket = new ShoppingBasketEntity();
        basket.setId(id);

        ProductEntity product = new ProductEntity("Widget", BigDecimal.ONE);
        product.setId(5L);
        basket.setProducts(List.of(product));

        UserEntity user = new UserEntity();
        user.setId(3L);
        user.setUsername("jane");
        user.setEmail("jane@example.com");
        basket.setUser(user);

        return basket;
    }
}
