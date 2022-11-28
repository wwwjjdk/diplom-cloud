package com.example.refactordip;

import com.example.refactordip.repository.AuthRepositoryIml;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class AuthRepositoryImlTest {
    private AuthRepositoryIml authRepositoryIml;
    public static final String TOKEN_1 = "token1";
    public static final String LOGIN_1 = "login1";


    @BeforeEach
    void setUp() {
        authRepositoryIml = new AuthRepositoryIml();
        authRepositoryIml.putMap(TOKEN_1, LOGIN_1);
    }

    @Test
    void getMapTest() {
        assertEquals(LOGIN_1, authRepositoryIml.getMap(TOKEN_1));
    }

    @Test
    void deleteMapTest() {
        authRepositoryIml.deleteMap(TOKEN_1);
        var answer = authRepositoryIml.getMap(TOKEN_1);
        assertNull(answer);
    }
}
