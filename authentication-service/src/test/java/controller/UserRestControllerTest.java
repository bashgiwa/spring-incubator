package controller;

import entelect.training.incubator.spring.authentication.AuthenticationServiceApplication;
import entelect.training.incubator.spring.authentication.controller.LoginRequest;
import entelect.training.incubator.spring.authentication.controller.RegisterRequest;
import entelect.training.incubator.spring.authentication.model.Role;
import entelect.training.incubator.spring.authentication.model.User;
import entelect.training.incubator.spring.authentication.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = AuthenticationServiceApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserRestControllerTest {
    private static final String TEST_USERNAME = "user";

    private static final String TEST_PASSWORD = "password";
    private static final String TEST_EMAIL = "user@gmail.com";
    private static final String TEST_SUCCESSFUL_MESSAGE = "Login Successful";
    private static final Boolean TEST_CREATED = true;
    private static final Role TEST_ROLE = Role.USER;

    @Autowired
    private MockMvc mvc;
    @Autowired
    UserService userService;

    @Test
    public void whenValidInput_ThenRegisterUser() throws Exception {
        RegisterRequest newRequest = getRegisterRequest();
        mvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON).content(toJson(newRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enabled").value(TEST_CREATED))
                .andDo(print());
    }

    @Test
    public void whenValidInput_ThenLoginUser() throws Exception {
        User newUser =  createMockUser();
        LoginRequest request =  getLoginRequest();
        mvc.perform(post("/user/login").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(TEST_SUCCESSFUL_MESSAGE))
                .andDo(print());
    }

    private RegisterRequest getRegisterRequest() {
        RegisterRequest request =  new RegisterRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);
        request.setEmail(TEST_EMAIL);
        request.setRole(TEST_ROLE);
        return request;
    }

    private LoginRequest getLoginRequest() {
        LoginRequest request =  new LoginRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);
        return request;
    }

    private User createMockUser() {
        RegisterRequest request = getRegisterRequest();
        return userService.saveUser(request);
    }

    private static byte[] toJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }
}
