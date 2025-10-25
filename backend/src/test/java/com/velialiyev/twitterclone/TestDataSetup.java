package com.velialiyev.twitterclone;

import com.velialiyev.twitterclone.entity.*;
import com.velialiyev.twitterclone.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Test data setup utility for creating test data in integration tests
 */
@TestComponent
public class TestDataSetup {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private RetweetRepository retweetRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserEntity createTestUser(String username, String email) {
        UserEntity user = UserEntity.builder()
                .firstName("Test")
                .lastName("User")
                .username(username)
                .email(email)
                .password(passwordEncoder.encode("password123"))
                .profilePicturePath("test-profile.jpg")
                .bannerPicturePath("test-banner.jpg")
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public TweetEntity createTestTweet(UserEntity user, String text, TweetType type) {
        TweetEntity tweet = TweetEntity.builder()
                .user(user)
                .text(text)
                .replyCounter(0)
                .retweetCounter(0)
                .likeCounter(0)
                .type(type)
                .createdDate(Instant.now())
                .build();

        return tweetRepository.save(tweet);
    }

    @Transactional
    public TweetEntity createTestReply(UserEntity user, String text, TweetEntity parentTweet) {
        TweetEntity reply = TweetEntity.builder()
                .user(user)
                .text(text)
                .replyCounter(0)
                .retweetCounter(0)
                .likeCounter(0)
                .type(TweetType.REPLY)
                .tweet(parentTweet)
                .createdDate(Instant.now())
                .build();

        return tweetRepository.save(reply);
    }

    @Transactional
    public LikeEntity createTestLike(UserEntity user, TweetEntity tweet) {
        LikeEntity like = LikeEntity.builder()
                .user(user)
                .tweet(tweet)
                .build();

        return likeRepository.save(like);
    }

    @Transactional
    public RetweetEntity createTestRetweet(UserEntity user, TweetEntity tweet) {
        RetweetEntity retweet = RetweetEntity.builder()
                .user(user)
                .tweet(tweet)
                .build();

        return retweetRepository.save(retweet);
    }

    @Transactional
    public BookmarkEntity createTestBookmark(UserEntity user, TweetEntity tweet) {
        BookmarkEntity bookmark = BookmarkEntity.builder()
                .user(user)
                .tweet(tweet)
                .build();

        return bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void createTestData() {
        // Create test users
        UserEntity user1 = createTestUser("user1", "user1@example.com");
        UserEntity user2 = createTestUser("user2", "user2@example.com");
        UserEntity user3 = createTestUser("user3", "user3@example.com");

        // Create test tweets
        TweetEntity tweet1 = createTestTweet(user1, "First tweet", TweetType.TWEET);
        TweetEntity tweet2 = createTestTweet(user2, "Second tweet", TweetType.TWEET);
        TweetEntity tweet3 = createTestTweet(user3, "Third tweet", TweetType.TWEET);

        // Create replies
        createTestReply(user2, "Reply to first tweet", tweet1);
        createTestReply(user3, "Another reply to first tweet", tweet1);

        // Create likes
        createTestLike(user2, tweet1);
        createTestLike(user3, tweet1);
        createTestLike(user1, tweet2);

        // Create retweets
        createTestRetweet(user2, tweet1);
        createTestRetweet(user3, tweet2);

        // Create bookmarks
        createTestBookmark(user1, tweet2);
        createTestBookmark(user2, tweet3);
    }

    @Transactional
    public void cleanupTestData() {
        bookmarkRepository.deleteAll();
        likeRepository.deleteAll();
        retweetRepository.deleteAll();
        tweetRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Transactional
    public List<UserEntity> createMultipleUsers(int count) {
        List<UserEntity> users = Arrays.asList();
        for (int i = 1; i <= count; i++) {
            UserEntity user = createTestUser("user" + i, "user" + i + "@example.com");
            users.add(user);
        }
        return users;
    }

    @Transactional
    public List<TweetEntity> createMultipleTweets(UserEntity user, int count) {
        List<TweetEntity> tweets = Arrays.asList();
        for (int i = 1; i <= count; i++) {
            TweetEntity tweet = createTestTweet(user, "Tweet number " + i, TweetType.TWEET);
            tweets.add(tweet);
        }
        return tweets;
    }
}
