package com.util.workingtool.jwt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.util.workingtool.dto.LoginDTO;
import com.util.workingtool.dto.TokenDTO;
import com.util.workingtool.entity.User;
import com.util.workingtool.repository.UserRepository;
import com.util.workingtool.service.UserService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TokenProviderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenProviderTest.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;


    private LoginDTO loginDTO;
    private final Gson gson = new Gson();

    @Before
    public void makeToken(){
        this.loginDTO = LoginDTO.builder()
                        .username("admin")
                        .password("admin")
                        .build();
    }

    /**
     * 토큰을 잘 생성하는지, 생성한 토큰이 유효한지 확인
     */
    @Test
    public void authenticateTest() {


        //when
        ResponseEntity<String> responseEntity = callAuthenticateApi();
        TokenDTO tokenDTO = gson.fromJson(responseEntity.getBody(), TokenDTO.class);

        assertThat(responseEntity.getBody()).contains("token");
        assertThat(tokenProvider.validateToken(tokenDTO.getToken())).isTrue();
    }


    /**
     * 생성된 토큰의 정보가 로그인한 아이디의 정보를 담고 있는지
     */
    @Test
    public void jwtTokenValidateTest() {
        //when
        ResponseEntity<String> responseEntity = callAuthenticateApi();
        TokenDTO tokenDTO = gson.fromJson(responseEntity.getBody(), TokenDTO.class);

        Authentication authentication = tokenProvider.getAuthentication(tokenDTO.getToken());
        org.springframework.security.core.userdetails.User userInToken =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        assertThat(userInToken.getUsername()).isEqualTo(loginDTO.getUsername());

    }

    private ResponseEntity<String> callAuthenticateApi(){
        return restTemplate.postForEntity("/api/authenticate", loginDTO, String.class);
    }
}
