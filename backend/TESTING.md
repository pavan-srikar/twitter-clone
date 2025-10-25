# Testing Documentation

This document describes the comprehensive testing setup for the Twitter Clone backend.

## Test Structure

The testing is organized into several layers:

### 1. Unit Tests
- **Controller Tests**: Test REST endpoints using MockMvc
- **Service Tests**: Test business logic with mocked dependencies
- **Repository Tests**: Test data access layer with in-memory database

### 2. Integration Tests
- **End-to-End Tests**: Test complete workflows
- **Database Integration**: Test with real database operations

## Test Files

### Controller Tests
- `TweetControllerTest.java` - Tests for tweet-related endpoints
- `AuthenticationControllerTest.java` - Tests for authentication endpoints

### Service Tests
- `TweetServiceTest.java` - Tests for tweet business logic
- `AuthenticationServiceTest.java` - Tests for authentication business logic

### Repository Tests
- `TweetRepositoryTest.java` - Tests for tweet data access
- `UserRepositoryTest.java` - Tests for user data access

### Integration Tests
- `TwitterCloneIntegrationTest.java` - End-to-end workflow tests

### Test Utilities
- `TestDataSetup.java` - Utility for creating test data
- `TestSuite.java` - Test suite runner

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Classes
```bash
mvn test -Dtest=TweetControllerTest
mvn test -Dtest=AuthenticationServiceTest
```

### Run Tests with Coverage
```bash
mvn test jacoco:report
```

## Test Configuration

### Test Profile
Tests use the `test` profile with the following configuration:
- In-memory H2 database
- Test-specific JWT secrets
- Debug logging enabled

### Test Data
- Tests use `TestDataSetup` utility for consistent test data
- Each test method is isolated with `@Transactional`
- Database is reset between tests

## Test Categories

### 1. Controller Tests
- Test HTTP status codes
- Test request/response mapping
- Test validation
- Test security

### 2. Service Tests
- Test business logic
- Test error handling
- Test transaction boundaries
- Test service interactions

### 3. Repository Tests
- Test CRUD operations
- Test custom queries
- Test data relationships
- Test constraints

### 4. Integration Tests
- Test complete user workflows
- Test authentication flows
- Test data persistence
- Test API contracts

## Test Data Management

### Creating Test Data
```java
@Autowired
private TestDataSetup testDataSetup;

@Test
void testWithData() {
    UserEntity user = testDataSetup.createTestUser("testuser", "test@example.com");
    TweetEntity tweet = testDataSetup.createTestTweet(user, "Test tweet", TweetType.TWEET);
    // ... test logic
}
```

### Cleanup
Tests automatically clean up data using `@Transactional` and `@DataJpaTest`.

## Best Practices

### 1. Test Isolation
- Each test is independent
- No shared state between tests
- Clean database for each test

### 2. Test Naming
- Use descriptive test method names
- Follow pattern: `methodName_shouldReturnExpectedResult_whenCondition`

### 3. Test Data
- Use realistic test data
- Keep test data minimal
- Use builders for complex objects

### 4. Assertions
- Use specific assertions
- Test both positive and negative cases
- Verify all important outcomes

## Coverage Goals

- **Controllers**: 100% endpoint coverage
- **Services**: 90%+ business logic coverage
- **Repositories**: 100% method coverage
- **Integration**: Key workflow coverage

## Debugging Tests

### Enable Debug Logging
```properties
logging.level.com.velialiyev.twitterclone=DEBUG
logging.level.org.springframework.security=DEBUG
```

### H2 Console
Access H2 console at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

### Test Reports
Test reports are generated in `target/surefire-reports/`

## Continuous Integration

Tests are designed to run in CI/CD pipelines:
- No external dependencies
- Fast execution
- Reliable and repeatable
- Clear failure messages

## Troubleshooting

### Common Issues

1. **Test Database Issues**
   - Ensure H2 dependency is included
   - Check test profile configuration

2. **Authentication Issues**
   - Verify security configuration
   - Check JWT token generation

3. **Transaction Issues**
   - Use `@Transactional` for data tests
   - Ensure proper cleanup

4. **Mock Issues**
   - Verify mock setup
   - Check method signatures

### Debug Commands
```bash
# Run tests with debug output
mvn test -X

# Run specific test with debug
mvn test -Dtest=TweetServiceTest -X

# Generate test report
mvn surefire-report:report
```
