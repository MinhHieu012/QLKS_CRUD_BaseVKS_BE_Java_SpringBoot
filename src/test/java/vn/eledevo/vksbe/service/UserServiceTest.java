package vn.eledevo.vksbe.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static vn.eledevo.vksbe.constant.ResponseMessage.USER_EXIST;

import java.util.Date;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;

import vn.eledevo.vksbe.constant.Role;
import vn.eledevo.vksbe.constant.UserStatus;
import vn.eledevo.vksbe.dto.request.user.UserAddRequest;
import vn.eledevo.vksbe.dto.request.user.UserUpdateRequest;
import vn.eledevo.vksbe.dto.response.user.UserResponse;
import vn.eledevo.vksbe.entity.User;
import vn.eledevo.vksbe.exception.ValidationException;
import vn.eledevo.vksbe.repository.UserRepository;
import vn.eledevo.vksbe.service.user.UserService;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private UserAddRequest userRequest;
    private UserUpdateRequest userUpdateRequest;
    private UserResponse userResponse;
    private User user;
    private Date dateOfBirth;

    @BeforeEach
    void initData() {
        dateOfBirth = java.sql.Date.valueOf("2002-11-09");

        userRequest = UserAddRequest.builder()
                .username("guest")
                .email("guest@gmail.com")
                .password("123456")
                .phone("0967710509")
                .identificationNumber("1202034")
                .dateOfBirth(dateOfBirth)
                .build();

        userResponse = UserResponse.builder()
                .username("guest")
                .email("guest@gmail.com")
                .dateOfBirth(dateOfBirth)
                .phone("0967710509")
                .identificationNumber(Long.valueOf("1202034"))
                .build();

        user = User.builder()
                .username("guest")
                .email("guest@gmail.com")
                .phone("0967710509")
                .identificationNumber("001202034")
                .dateOfBirth(dateOfBirth)
                .build();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserDetailsService testUserDetailsService() {
            User userMock = new User();
            userMock.setRole(Role.ADMIN);
            return username -> userMock;
        }
    }

    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "testUserDetailsService")
    void addUser_validRequest_success() throws ValidationException {
        Mockito.when(userRepository.existsByUsername(any())).thenReturn(false);
        Mockito.when(userRepository.existsByPhone(any())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(any())).thenReturn(false);
        Mockito.when(userRepository.existsByIdentificationNumber(any())).thenReturn(false);
        Mockito.when(userRepository.save(any())).thenReturn(user);

        var userResponse = userService.createUser(userRequest);
        userResponse.setRole(Role.USER);
        userResponse.setStatus(UserStatus.ACTIVE);

        Assertions.assertThat(userResponse.getUsername()).isEqualTo("guest");
        Assertions.assertThat(userResponse.getEmail()).isEqualTo("guest@gmail.com");
        Assertions.assertThat(userResponse.getDateOfBirth()).isEqualTo("2002-11-09");
        Assertions.assertThat(userResponse.getPhone()).isEqualTo("0967710509");
        Assertions.assertThat(userResponse.getIdentificationNumber().toString()).isEqualTo("1202034");
        Assertions.assertThat(userResponse.getRole().toString()).isEqualTo("USER");
        Assertions.assertThat(userResponse.getStatus().toString()).isEqualTo("ACTIVE");
    }

    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "testUserDetailsService")
    void addUser_invalidRequest_UsernameExisted_failed() throws ValidationException {
        Mockito.when(userRepository.existsByUsername(anyString())).thenReturn(true);

        var exceptionUsernameExisted =
                assertThrows(ValidationException.class, () -> userService.createUser(userRequest));

        Assertions.assertThat(exceptionUsernameExisted.getMessage()).isEqualTo("username: " + USER_EXIST);
    }
}
