package de.flix29.sproutdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.flix29.sprout.runtime.security.SproutMethodSecurityConfiguration;
import de.flix29.sproutdemo.entities.ProductEntity;
import de.flix29.sproutdemo.entities.generated.controllers.SproutProductEntityController;
import de.flix29.sproutdemo.entities.generated.services.SproutProductEntityService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SproutProductEntityController.class)
@ImportAutoConfiguration(SproutMethodSecurityConfiguration.class)
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SproutProductEntityService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
    void getAllReturnsProducts() throws Exception {
        given(service.findAll()).willReturn(List.of(new ProductEntity("Widget", BigDecimal.ONE)));

        mockMvc.perform(get("/products").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Widget"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllReturnsEmptyListWhenNoProducts() throws Exception {
        given(service.findAll()).willReturn(List.of());

        mockMvc.perform(get("/products").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void getAllRequiresUserRole() throws Exception {
        mockMvc.perform(get("/products").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getByIdReturnsProduct() throws Exception {
        ProductEntity product = new ProductEntity("Widget", BigDecimal.ONE);
        product.setId(1L);
        given(service.findById(1L)).willReturn(Optional.of(product));

        mockMvc.perform(get("/products/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getByIdReturnsNotFoundWhenMissing() throws Exception {
        given(service.findById(99L)).willReturn(Optional.empty());

        mockMvc.perform(get("/products/99").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void getByIdRequiresUserRole() throws Exception {
        mockMvc.perform(get("/products/1").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createReturnsCreatedProduct() throws Exception {
        ProductEntity product = new ProductEntity("Widget", BigDecimal.ONE);
        product.setId(1L);
        given(service.save(any(ProductEntity.class))).willReturn(product);

        mockMvc.perform(post("/products").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductEntity("Widget", BigDecimal.ONE))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createInvalidProductReturnsBadRequest() throws Exception {
        ProductEntity invalidProduct = new ProductEntity("", BigDecimal.valueOf(-1));

        mockMvc.perform(post("/products").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createInvalidMediaTypeReturnsUnsupportedMediaType() throws Exception {
        mockMvc.perform(post("/products").with(csrf())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("Invalid content"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createRequiresAdminRole() throws Exception {
        mockMvc.perform(post("/products").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductEntity("Widget", BigDecimal.ONE))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateReturnsUpdatedProduct() throws Exception {
        ProductEntity updated = new ProductEntity("Widget", BigDecimal.TEN);
        updated.setId(1L);
        given(service.update(eq(1L), any(ProductEntity.class))).willReturn(Optional.of(updated));

        mockMvc.perform(put("/products/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Widget"))
                .andExpect(jsonPath("$.price").value(10));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateReturnsNotFoundWhenMissing() throws Exception {
        given(service.update(eq(99L), any(ProductEntity.class))).willReturn(Optional.empty());

        mockMvc.perform(put("/products/99").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductEntity("Widget", BigDecimal.TEN))))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateIgnoreIdInBody() throws Exception {
        ProductEntity updated = new ProductEntity("Widget", BigDecimal.TEN);
        updated.setId(2L);
        given(service.update(eq(1L), any(ProductEntity.class))).willReturn(Optional.of(updated));

        mockMvc.perform(put("/products/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk());

        verify(service).update(eq(1L), any(ProductEntity.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateRequiresAdminRole() throws Exception {
        mockMvc.perform(put("/products/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductEntity("Widget", BigDecimal.ONE))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteReturnsNoContentWhenSuccessful() throws Exception {
        given(service.deleteById(1L)).willReturn(true);

        mockMvc.perform(delete("/products/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteReturnsNotFoundWhenMissing() throws Exception {
        given(service.deleteById(99L)).willReturn(false);

        mockMvc.perform(delete("/products/99").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteRequiresAdminRole() throws Exception {
        mockMvc.perform(delete("/products/1").with(csrf()))
                .andExpect(status().isForbidden());
    }
}
