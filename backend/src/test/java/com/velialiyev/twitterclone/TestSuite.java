package com.velialiyev.twitterclone;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test suite to run all tests
 * This class can be used to run all tests at once
 */
@SpringBootTest
@ActiveProfiles("test")
class TestSuite {

    @Test
    void contextLoads() {
        // This test ensures that the Spring context loads properly
        // All other tests will be run by Maven/Gradle test runner
    }
}
