package com.velialiyev.twitterclone.repository;

import com.velialiyev.twitterclone.entity.TweetEntity;
import com.velialiyev.twitterclone.entity.TweetType;
import com.velialiyev.twitterclone.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TweetRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TweetRepository tweetRepository;

    private UserEntity userEntity;
    private TweetEntity tweetEntity;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder()
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john@example.com")
                .password("encodedPassword")
                .profilePicturePath("profile.jpg")
                .bannerPicturePath("banner.jpg")
                .build();

        tweetEntity = TweetEntity.builder()
                .user(userEntity)
                .text("Test tweet content")
                .replyCounter(0)
                .retweetCounter(0)
                .likeCounter(0)
                .type(TweetType.TWEET)
                .createdDate(Instant.now())
                .build();
    }

    @Test
    void save_ShouldPersistTweet_WhenValidTweetEntity() {
        // Given
        entityManager.persistAndFlush(userEntity);

        // When
        TweetEntity savedTweet = tweetRepository.save(tweetEntity);

        // Then
        assertNotNull(savedTweet.getId());
        assertEquals(tweetEntity.getText(), savedTweet.getText());
        assertEquals(tweetEntity.getType(), savedTweet.getType());
        assertEquals(tweetEntity.getUser().getUsername(), savedTweet.getUser().getUsername());
    }

    @Test
    void findById_ShouldReturnTweet_WhenTweetExists() {
        // Given
        entityManager.persistAndFlush(userEntity);
        TweetEntity savedTweet = tweetRepository.save(tweetEntity);

        // When
        Optional<TweetEntity> foundTweet = tweetRepository.findById(savedTweet.getId());

        // Then
        assertTrue(foundTweet.isPresent());
        assertEquals(savedTweet.getId(), foundTweet.get().getId());
        assertEquals(savedTweet.getText(), foundTweet.get().getText());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenTweetDoesNotExist() {
        // When
        Optional<TweetEntity> foundTweet = tweetRepository.findById(999L);

        // Then
        assertFalse(foundTweet.isPresent());
    }

    @Test
    void findAllByType_ShouldReturnTweets_WhenTweetsOfTypeExist() {
        // Given
        entityManager.persistAndFlush(userEntity);
        
        TweetEntity tweet1 = TweetEntity.builder()
                .user(userEntity)
                .text("Tweet 1")
                .type(TweetType.TWEET)
                .createdDate(Instant.now())
                .build();

        TweetEntity tweet2 = TweetEntity.builder()
                .user(userEntity)
                .text("Tweet 2")
                .type(TweetType.TWEET)
                .createdDate(Instant.now())
                .build();

        TweetEntity reply = TweetEntity.builder()
                .user(userEntity)
                .text("Reply")
                .type(TweetType.REPLY)
                .createdDate(Instant.now())
                .build();

        tweetRepository.save(tweet1);
        tweetRepository.save(tweet2);
        tweetRepository.save(reply);

        // When
        Optional<List<TweetEntity>> tweets = tweetRepository.findAllByType(TweetType.TWEET);

        // Then
        assertTrue(tweets.isPresent());
        assertEquals(2, tweets.get().size());
        assertTrue(tweets.get().stream().allMatch(tweet -> tweet.getType() == TweetType.TWEET));
    }

    @Test
    void findAllByUserAndType_ShouldReturnUserTweets_WhenUserHasTweetsOfType() {
        // Given
        entityManager.persistAndFlush(userEntity);
        
        TweetEntity tweet1 = TweetEntity.builder()
                .user(userEntity)
                .text("User Tweet 1")
                .type(TweetType.TWEET)
                .createdDate(Instant.now())
                .build();

        TweetEntity tweet2 = TweetEntity.builder()
                .user(userEntity)
                .text("User Tweet 2")
                .type(TweetType.TWEET)
                .createdDate(Instant.now())
                .build();

        tweetRepository.save(tweet1);
        tweetRepository.save(tweet2);

        // When
        Optional<List<TweetEntity>> userTweets = tweetRepository.findAllByUserAndType(userEntity, TweetType.TWEET);

        // Then
        assertTrue(userTweets.isPresent());
        assertEquals(2, userTweets.get().size());
        assertTrue(userTweets.get().stream().allMatch(tweet -> 
            tweet.getUser().getUsername().equals(userEntity.getUsername()) &&
            tweet.getType() == TweetType.TWEET
        ));
    }

    @Test
    void findAllByTweetAndType_ShouldReturnReplies_WhenTweetHasReplies() {
        // Given
        entityManager.persistAndFlush(userEntity);
        
        TweetEntity parentTweet = TweetEntity.builder()
                .user(userEntity)
                .text("Parent Tweet")
                .type(TweetType.TWEET)
                .createdDate(Instant.now())
                .build();

        TweetEntity savedParent = tweetRepository.save(parentTweet);

        TweetEntity reply1 = TweetEntity.builder()
                .user(userEntity)
                .text("Reply 1")
                .type(TweetType.REPLY)
                .tweet(savedParent)
                .createdDate(Instant.now())
                .build();

        TweetEntity reply2 = TweetEntity.builder()
                .user(userEntity)
                .text("Reply 2")
                .type(TweetType.REPLY)
                .tweet(savedParent)
                .createdDate(Instant.now())
                .build();

        tweetRepository.save(reply1);
        tweetRepository.save(reply2);

        // When
        Optional<List<TweetEntity>> replies = tweetRepository.findAllByTweetAndType(savedParent, TweetType.REPLY);

        // Then
        assertTrue(replies.isPresent());
        assertEquals(2, replies.get().size());
        assertTrue(replies.get().stream().allMatch(tweet -> 
            tweet.getType() == TweetType.REPLY &&
            tweet.getTweet().getId().equals(savedParent.getId())
        ));
    }

    @Test
    void deleteById_ShouldRemoveTweet_WhenTweetExists() {
        // Given
        entityManager.persistAndFlush(userEntity);
        TweetEntity savedTweet = tweetRepository.save(tweetEntity);
        Long tweetId = savedTweet.getId();

        // When
        tweetRepository.deleteById(tweetId);

        // Then
        Optional<TweetEntity> deletedTweet = tweetRepository.findById(tweetId);
        assertFalse(deletedTweet.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllTweets_WhenTweetsExist() {
        // Given
        entityManager.persistAndFlush(userEntity);
        
        TweetEntity tweet1 = TweetEntity.builder()
                .user(userEntity)
                .text("Tweet 1")
                .type(TweetType.TWEET)
                .createdDate(Instant.now())
                .build();

        TweetEntity tweet2 = TweetEntity.builder()
                .user(userEntity)
                .text("Tweet 2")
                .type(TweetType.TWEET)
                .createdDate(Instant.now())
                .build();

        tweetRepository.save(tweet1);
        tweetRepository.save(tweet2);

        // When
        List<TweetEntity> allTweets = tweetRepository.findAll();

        // Then
        assertTrue(allTweets.size() >= 2);
        assertTrue(allTweets.stream().anyMatch(tweet -> tweet.getText().equals("Tweet 1")));
        assertTrue(allTweets.stream().anyMatch(tweet -> tweet.getText().equals("Tweet 2")));
    }
}
