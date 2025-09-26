package de.flix29.sproutdemo;

import de.flix29.sprout.runtime.error.GlobalExceptionHandler;
import de.flix29.sprout.runtime.error.SproutErrorProperties;
import de.flix29.sproutdemo.entities.generated.controllers.SproutProductEntityController;
import de.flix29.sproutdemo.entities.generated.services.SproutProductEntityService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = "ADMIN")
@WebMvcTest(SproutProductEntityController.class)
@Import({GlobalExceptionHandler.class, SproutErrorProperties.class})
class ExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SproutProductEntityService service;

    private static final String VALID_PRODUCT_JSON = """
            {
                "name": "name",
                "price": 1
            }
            """;
    private static final String PRODUCT_INVALID_ARGUMENT = """
            {
                "name": "name that is way too long for the defined maximum length of one hundred and twenty characters which should trigger a constraint violation in the validation process",
                "price": -1
            }
            """;
    private static final String PRODUCT_MESSAGE_NOT_READABLE = """
            {
                "name": ,
                "price": 
            }
            """;
    private static final String PRODUCT_PLAIN_TEXT = "Just some plain text instead of JSON";

    @Test
    void expectInvalidArgumentOnPost() throws Exception {
        mockMvc.perform(post("/products").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCT_INVALID_ARGUMENT)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("validation_failed"))
                .andExpect(jsonPath("$.message").value("Request body validation failed"))
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    void expectInvalidArgumentOnPut() throws Exception {
        mockMvc.perform(put("/products/{id}", 1L).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCT_INVALID_ARGUMENT)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/1"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("validation_failed"))
                .andExpect(jsonPath("$.message").value("Request body validation failed"))
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    void expectConstraintViolation_onPost() throws Exception {
        when(service.save(any())).thenThrow(new ConstraintViolationException(Collections.emptySet()));

        mockMvc.perform(post("/products").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("constraint_violation"))
                .andExpect(jsonPath("$.message").value("Constraint validation failed"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectConstraintViolation_onPut() throws Exception {
        when(service.update(eq(3L), any())).thenThrow(new ConstraintViolationException(Collections.emptySet()));

        mockMvc.perform(put("/products/{id}", 3L).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/3"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("constraint_violation"))
                .andExpect(jsonPath("$.message").value("Constraint validation failed"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectMessageNotReadableOnPost() throws Exception {
        mockMvc.perform(post("/products").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCT_MESSAGE_NOT_READABLE)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("malformed_json"))
                .andExpect(jsonPath("$.message").value("Malformed JSON request body"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectMessageNotReadableOnPut() throws Exception {
        mockMvc.perform(put("/products/{id}", 1L).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCT_MESSAGE_NOT_READABLE)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/1"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("malformed_json"))
                .andExpect(jsonPath("$.message").value("Malformed JSON request body"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectTypeMismatch_onGetById() throws Exception {
        mockMvc.perform(get("/products/{id}", "abc").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/abc"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("type_mismatch"))
                .andExpect(jsonPath("$.message").value(startsWith("Parameter 'id' requires type")))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectTypeMismatch_onPutById() throws Exception {
        mockMvc.perform(put("/products/{id}", "abc").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/abc"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("type_mismatch"))
                .andExpect(jsonPath("$.message").value(startsWith("Parameter 'id' requires type")))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectTypeMismatch_onDeleteById() throws Exception {
        mockMvc.perform(delete("/products/{id}", "abc").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/abc"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("type_mismatch"))
                .andExpect(jsonPath("$.message").value(startsWith("Parameter 'id' requires type")))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @Disabled("No query parameter, so this does not apply")
    void expectMissingParameter_onGetById() throws Exception {
        mockMvc.perform(get("/products/{id}").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/abc"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("missing_parameter"))
                .andExpect(jsonPath("$.message").value(startsWith("Missing required parameter")))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @Disabled("No query parameter, so this does not apply")
    void expectMissingParameter_onPutById() throws Exception {
        mockMvc.perform(put("/products/{id}").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/abc"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("missing_parameter"))
                .andExpect(jsonPath("$.message").value(startsWith("Missing required parameter")))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @Disabled("No query parameter, so this does not apply")
    void expectMissingParameter_onDeleteById() throws Exception {
        mockMvc.perform(delete("/products/{id}").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/abc"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("missing_parameter"))
                .andExpect(jsonPath("$.message").value(startsWith("Missing required parameter")))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectMediaTypeNotSupportedOnPost() throws Exception {
        mockMvc.perform(post("/products").with(csrf())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(PRODUCT_PLAIN_TEXT)
                ).andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products"))
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.error").value("Unsupported Media Type"))
                .andExpect(jsonPath("$.code").value("unsupported_media_type"))
                .andExpect(jsonPath("$.message").value("Unsupported Content-Type"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectMediaTypeNotSupportedOnPut() throws Exception {
        mockMvc.perform(put("/products/{id}", 1L).with(csrf())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(PRODUCT_PLAIN_TEXT)
                ).andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/1"))
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.error").value("Unsupported Media Type"))
                .andExpect(jsonPath("$.code").value("unsupported_media_type"))
                .andExpect(jsonPath("$.message").value("Unsupported Content-Type"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectMediaTypeNotAcceptedOnGet() throws Exception {
        mockMvc.perform(get("/products").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON)
                        .accept(MediaType.APPLICATION_XML)
                ).andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products"))
                .andExpect(jsonPath("$.status").value(406))
                .andExpect(jsonPath("$.error").value("Not Acceptable"))
                .andExpect(jsonPath("$.code").value("not_acceptable"))
                .andExpect(jsonPath("$.message").value("Requested media type not acceptable"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectMediaTypeNotAcceptedOnGetById() throws Exception {
        mockMvc.perform(get("/products/{id}", 1L).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON)
                        .accept(MediaType.APPLICATION_XML)
                ).andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/1"))
                .andExpect(jsonPath("$.status").value(406))
                .andExpect(jsonPath("$.error").value("Not Acceptable"))
                .andExpect(jsonPath("$.code").value("not_acceptable"))
                .andExpect(jsonPath("$.message").value("Requested media type not acceptable"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectMediaTypeNotAcceptedOnPost() throws Exception {
        mockMvc.perform(post("/products").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON)
                        .accept(MediaType.APPLICATION_XML)
                ).andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products"))
                .andExpect(jsonPath("$.status").value(406))
                .andExpect(jsonPath("$.error").value("Not Acceptable"))
                .andExpect(jsonPath("$.code").value("not_acceptable"))
                .andExpect(jsonPath("$.message").value("Requested media type not acceptable"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectMediaTypeNotAcceptedOnPut() throws Exception {
        mockMvc.perform(put("/products/{id}", 1L).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON)
                        .accept(MediaType.APPLICATION_XML)
                ).andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/1"))
                .andExpect(jsonPath("$.status").value(406))
                .andExpect(jsonPath("$.error").value("Not Acceptable"))
                .andExpect(jsonPath("$.code").value("not_acceptable"))
                .andExpect(jsonPath("$.message").value("Requested media type not acceptable"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectMediaTypeNotAcceptedOnDelete() throws Exception {
        mockMvc.perform(delete("/products/{id}", 1L).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON)
                        .accept(MediaType.APPLICATION_XML)
                ).andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/1"))
                .andExpect(jsonPath("$.status").value(406))
                .andExpect(jsonPath("$.error").value("Not Acceptable"))
                .andExpect(jsonPath("$.code").value("not_acceptable"))
                .andExpect(jsonPath("$.message").value("Requested media type not acceptable"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectMethodNotSupportedOnPatch() throws Exception {
        mockMvc.perform(patch("/products/{id}", 1L).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON)
                ).andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/1"))
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.error").value("Method Not Allowed"))
                .andExpect(jsonPath("$.code").value("method_not_allowed"))
                .andExpect(jsonPath("$.message").value("HTTP method not allowed"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectNotFoundWithNoSuchElementOnGetById() throws Exception {
        when(service.findById(1L)).thenThrow(NoSuchElementException.class);

        mockMvc.perform(get("/products/{id}", 1L).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/1"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.code").value("not_found"))
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectNotFoundOnGetById() throws Exception {
        when(service.findById(1L)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/products/{id}", 1L).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/1"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.code").value("not_found"))
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectDataIntegrityViolation_onPost() throws Exception {
        when(service.save(any())).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(post("/products").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.code").value("data_integrity_violation"))
                .andExpect(jsonPath("$.message").value("Data integrity violation"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectDataIntegrityViolation_onPut() throws Exception {
        when(service.update(eq(1L), any())).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(put("/products/{id}", 1L).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/1"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.code").value("data_integrity_violation"))
                .andExpect(jsonPath("$.message").value("Data integrity violation"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectUnhandledOnGet() throws Exception {
        when(service.findAll()).thenThrow(RuntimeException.class);
        mockMvc.perform(get("/products").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON)
                ).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.code").value("internal_error"))
                .andExpect(jsonPath("$.message").value("Unexpected server error"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectUnhandledOnGetById() throws Exception {
        when(service.findById(1L)).thenThrow(RuntimeException.class);
        mockMvc.perform(get("/products/{id}", 1L).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON)
                ).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/1"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.code").value("internal_error"))
                .andExpect(jsonPath("$.message").value("Unexpected server error"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectUnhandledOnPost() throws Exception {
        when(service.save(any())).thenThrow(RuntimeException.class);
        mockMvc.perform(post("/products").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON)
                ).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.code").value("internal_error"))
                .andExpect(jsonPath("$.message").value("Unexpected server error"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectUnhandledOnPut() throws Exception {
        when(service.update(eq(1L), any())).thenThrow(RuntimeException.class);
        mockMvc.perform(put("/products/{id}", 1L).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON)
                ).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/1"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.code").value("internal_error"))
                .andExpect(jsonPath("$.message").value("Unexpected server error"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void expectUnhandledOnDelete() throws Exception {
        when(service.deleteById(1L)).thenThrow(RuntimeException.class);
        mockMvc.perform(delete("/products/{id}", 1L).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON)
                ).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/products/1"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.code").value("internal_error"))
                .andExpect(jsonPath("$.message").value("Unexpected server error"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }
}
