package de.flix29.sproutdemo.entities.generated.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.flix29.sproutdemo.entities.ProductEntity;
import de.flix29.sproutdemo.entities.generated.services.SproutProductEntityService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SproutProductEntityController.class)
class SproutProductEntityControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SproutProductEntityService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllReturnsProducts() throws Exception {
        given(service.findAll()).willReturn(List.of(new ProductEntity("Widget", BigDecimal.ONE)));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Widget"));
    }

    @Test
    void getByIdReturnsProduct() throws Exception {
        ProductEntity product = new ProductEntity("Widget", BigDecimal.ONE);
        product.setId(1L);
        given(service.findById(1L)).willReturn(Optional.of(product));

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getByIdReturnsNotFoundWhenMissing() throws Exception {
        given(service.findById(99L)).willReturn(Optional.empty());

        mockMvc.perform(get("/products/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReturnsCreatedProduct() throws Exception {
        ProductEntity product = new ProductEntity("Widget", BigDecimal.ONE);
        product.setId(1L);
        given(service.save(any(ProductEntity.class))).willReturn(product);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductEntity("Widget", BigDecimal.ONE))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateReturnsUpdatedProduct() throws Exception {
        ProductEntity updated = new ProductEntity("Widget", BigDecimal.TEN);
        updated.setId(1L);
        given(service.update(eq(1L), any(ProductEntity.class))).willReturn(Optional.of(updated));

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(10));
    }

    @Test
    void updateReturnsNotFoundWhenMissing() throws Exception {
        given(service.update(eq(99L), any(ProductEntity.class))).willReturn(Optional.empty());

        mockMvc.perform(put("/products/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductEntity("Widget", BigDecimal.TEN))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturnsNoContentWhenSuccessful() throws Exception {
        given(service.deleteById(1L)).willReturn(true);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReturnsNotFoundWhenMissing() throws Exception {
        given(service.deleteById(99L)).willReturn(false);

        mockMvc.perform(delete("/products/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReturnsBadRequestForInvalidInput() throws Exception {
        ProductEntity invalid = new ProductEntity("", BigDecimal.valueOf(-1));

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}
