package com.velialiyev.twitterclone.service;

import com.velialiyev.twitterclone.dto.LikeRetweetBookmarkDto;
import com.velialiyev.twitterclone.dto.TweetDto;
import com.velialiyev.twitterclone.dto.TweetResponseDto;
import com.velialiyev.twitterclone.dto.UserDto;
import com.velialiyev.twitterclone.entity.*;
import com.velialiyev.twitterclone.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TweetServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private RetweetRepository retweetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @InjectMocks
    private TweetService tweetService;

    private UserEntity userEntity;
    private TweetEntity tweetEntity;
    private TweetDto tweetDto;
    private LikeRetweetBookmarkDto likeRetweetBookmarkDto;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john@example.com")
                .build();

        tweetEntity = TweetEntity.builder()
                .id(1L)
                .user(userEntity)
                .text("Test tweet content")
                .replyCounter(0)
                .retweetCounter(0)
                .likeCounter(0)
                .type(TweetType.TWEET)
                .createdDate(Instant.now())
                .build();

        tweetDto = TweetDto.builder()
                .text("Test tweet content")
                .type("TWEET")
                .build();

        likeRetweetBookmarkDto = LikeRetweetBookmarkDto.builder()
                .tweetId(1L)
                .build();
    }

    @Test
    void tweet_ShouldCreateTweet_WhenValidTweetDto() {
        // Given
        when(authenticationService.getUserFromJwt()).thenReturn(userEntity);
        when(tweetRepository.save(any(TweetEntity.class))).thenReturn(tweetEntity);

        // When
        tweetService.tweet(tweetDto);

        // Then
        verify(authenticationService, times(1)).getUserFromJwt();
        verify(tweetRepository, times(1)).save(any(TweetEntity.class));
    }

    @Test
    void tweet_ShouldCreateReply_WhenValidReplyDto() {
        // Given
        TweetDto replyDto = TweetDto.builder()
                .text("Reply content")
                .type("REPLY")
                .tweetId(1L)
                .build();

        TweetEntity parentTweet = TweetEntity.builder()
                .id(1L)
                .replyCounter(0)
                .build();

        when(authenticationService.getUserFromJwt()).thenReturn(userEntity);
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(parentTweet));
        when(tweetRepository.save(any(TweetEntity.class))).thenReturn(tweetEntity);

        // When
        tweetService.tweet(replyDto);

        // Then
        verify(authenticationService, times(1)).getUserFromJwt();
        verify(tweetRepository, times(2)).save(any(TweetEntity.class));
    }

    @Test
    void deleteTweet_ShouldDeleteTweet_WhenValidId() {
        // Given
        Long tweetId = 1L;
        when(tweetRepository.findById(tweetId)).thenReturn(Optional.of(tweetEntity));
        doNothing().when(tweetRepository).deleteById(tweetId);

        // When
        tweetService.deleteTweet(tweetId);

        // Then
        verify(tweetRepository, times(1)).findById(tweetId);
        verify(tweetRepository, times(1)).deleteById(tweetId);
    }

    @Test
    void like_ShouldCreateLike_WhenNotAlreadyLiked() {
        // Given
        when(authenticationService.getUserFromJwt()).thenReturn(userEntity);
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweetEntity));
        when(likeRepository.findByUserAndTweet(userEntity, tweetEntity)).thenReturn(Optional.empty());
        when(tweetRepository.save(any(TweetEntity.class))).thenReturn(tweetEntity);
        when(likeRepository.save(any(LikeEntity.class))).thenReturn(new LikeEntity());

        // When
        tweetService.like(likeRetweetBookmarkDto);

        // Then
        verify(authenticationService, times(1)).getUserFromJwt();
        verify(tweetRepository, times(1)).save(any(TweetEntity.class));
        verify(likeRepository, times(1)).save(any(LikeEntity.class));
    }

    @Test
    void like_ShouldRemoveLike_WhenAlreadyLiked() {
        // Given
        LikeEntity existingLike = LikeEntity.builder()
                .user(userEntity)
                .tweet(tweetEntity)
                .build();

        when(authenticationService.getUserFromJwt()).thenReturn(userEntity);
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweetEntity));
        when(likeRepository.findByUserAndTweet(userEntity, tweetEntity)).thenReturn(Optional.of(existingLike));
        when(tweetRepository.save(any(TweetEntity.class))).thenReturn(tweetEntity);

        // When
        tweetService.like(likeRetweetBookmarkDto);

        // Then
        verify(authenticationService, times(1)).getUserFromJwt();
        verify(tweetRepository, times(1)).save(any(TweetEntity.class));
        verify(likeRepository, times(1)).delete(existingLike);
    }

    @Test
    void retweet_ShouldCreateRetweet_WhenNotAlreadyRetweeted() {
        // Given
        when(authenticationService.getUserFromJwt()).thenReturn(userEntity);
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweetEntity));
        when(retweetRepository.findByUserAndTweet(userEntity, tweetEntity)).thenReturn(Optional.empty());
        when(tweetRepository.save(any(TweetEntity.class))).thenReturn(tweetEntity);
        when(retweetRepository.save(any(RetweetEntity.class))).thenReturn(new RetweetEntity());

        // When
        tweetService.retweet(likeRetweetBookmarkDto);

        // Then
        verify(authenticationService, times(1)).getUserFromJwt();
        verify(tweetRepository, times(1)).save(any(TweetEntity.class));
        verify(retweetRepository, times(1)).save(any(RetweetEntity.class));
    }

    @Test
    void retweet_ShouldRemoveRetweet_WhenAlreadyRetweeted() {
        // Given
        RetweetEntity existingRetweet = RetweetEntity.builder()
                .user(userEntity)
                .tweet(tweetEntity)
                .build();

        when(authenticationService.getUserFromJwt()).thenReturn(userEntity);
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweetEntity));
        when(retweetRepository.findByUserAndTweet(userEntity, tweetEntity)).thenReturn(Optional.of(existingRetweet));
        when(tweetRepository.save(any(TweetEntity.class))).thenReturn(tweetEntity);

        // When
        tweetService.retweet(likeRetweetBookmarkDto);

        // Then
        verify(authenticationService, times(1)).getUserFromJwt();
        verify(tweetRepository, times(1)).save(any(TweetEntity.class));
        verify(retweetRepository, times(1)).delete(existingRetweet);
    }

    @Test
    void getAllTweets_ShouldReturnAllTweets() {
        // Given
        List<TweetEntity> tweets = Arrays.asList(tweetEntity);
        when(tweetRepository.findAllByType(TweetType.TWEET)).thenReturn(Optional.of(tweets));

        // When
        List<TweetResponseDto> result = tweetService.getAllTweets();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tweetRepository, times(1)).findAllByType(TweetType.TWEET);
    }

    @Test
    void getTweetsByUsername_ShouldReturnUserTweets() {
        // Given
        String username = "johndoe";
        List<TweetEntity> tweets = Arrays.asList(tweetEntity);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
        when(tweetRepository.findAllByUserAndType(userEntity, TweetType.TWEET)).thenReturn(Optional.of(tweets));

        // When
        List<TweetResponseDto> result = tweetService.getTweetsByUsername(username);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findByUsername(username);
        verify(tweetRepository, times(1)).findAllByUserAndType(userEntity, TweetType.TWEET);
    }

    @Test
    void isLiked_ShouldReturnTrue_WhenTweetIsLiked() {
        // Given
        LikeEntity likeEntity = LikeEntity.builder()
                .user(userEntity)
                .tweet(tweetEntity)
                .build();

        when(authenticationService.getUserFromJwt()).thenReturn(userEntity);
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweetEntity));
        when(likeRepository.findByUserAndTweet(userEntity, tweetEntity)).thenReturn(Optional.of(likeEntity));

        // When
        Boolean result = tweetService.isLiked(likeRetweetBookmarkDto);

        // Then
        assertTrue(result);
        verify(authenticationService, times(1)).getUserFromJwt();
        verify(tweetRepository, times(1)).findById(1L);
        verify(likeRepository, times(1)).findByUserAndTweet(userEntity, tweetEntity);
    }

    @Test
    void isLiked_ShouldReturnFalse_WhenTweetIsNotLiked() {
        // Given
        when(authenticationService.getUserFromJwt()).thenReturn(userEntity);
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweetEntity));
        when(likeRepository.findByUserAndTweet(userEntity, tweetEntity)).thenReturn(Optional.empty());

        // When
        Boolean result = tweetService.isLiked(likeRetweetBookmarkDto);

        // Then
        assertFalse(result);
        verify(authenticationService, times(1)).getUserFromJwt();
        verify(tweetRepository, times(1)).findById(1L);
        verify(likeRepository, times(1)).findByUserAndTweet(userEntity, tweetEntity);
    }

    @Test
    void bookmark_ShouldCreateBookmark_WhenNotAlreadyBookmarked() {
        // Given
        when(authenticationService.getUserFromJwt()).thenReturn(userEntity);
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweetEntity));
        when(bookmarkRepository.findByUserAndTweet(userEntity, tweetEntity)).thenReturn(Optional.empty());
        when(bookmarkRepository.save(any(BookmarkEntity.class))).thenReturn(new BookmarkEntity());

        // When
        tweetService.bookmark(likeRetweetBookmarkDto);

        // Then
        verify(authenticationService, times(1)).getUserFromJwt();
        verify(bookmarkRepository, times(1)).save(any(BookmarkEntity.class));
    }

    @Test
    void bookmark_ShouldRemoveBookmark_WhenAlreadyBookmarked() {
        // Given
        BookmarkEntity existingBookmark = BookmarkEntity.builder()
                .user(userEntity)
                .tweet(tweetEntity)
                .build();

        when(authenticationService.getUserFromJwt()).thenReturn(userEntity);
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweetEntity));
        when(bookmarkRepository.findByUserAndTweet(userEntity, tweetEntity)).thenReturn(Optional.of(existingBookmark));

        // When
        tweetService.bookmark(likeRetweetBookmarkDto);

        // Then
        verify(authenticationService, times(1)).getUserFromJwt();
        verify(bookmarkRepository, times(1)).delete(existingBookmark);
    }

    @Test
    void likeCounter_ShouldReturnLikeCount() {
        // Given
        Integer expectedCount = 5;
        tweetEntity.setLikeCounter(expectedCount);
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweetEntity));

        // When
        Integer result = tweetService.likeCounter(1L);

        // Then
        assertEquals(expectedCount, result);
        verify(tweetRepository, times(1)).findById(1L);
    }
}
