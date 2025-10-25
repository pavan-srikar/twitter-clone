package com.velialiyev.twitterclone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velialiyev.twitterclone.dto.TweetDto;
import com.velialiyev.twitterclone.dto.TweetResponseDto;
import com.velialiyev.twitterclone.service.TweetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TweetController.class)
class TweetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TweetService tweetService;

    @Autowired
    private ObjectMapper objectMapper;

    private TweetDto tweetDto;
    private TweetResponseDto tweetResponseDto;

    @BeforeEach
    void setUp() {
        tweetDto = TweetDto.builder()
                .text("Test tweet content")
                .type("TWEET")
                .build();

        tweetResponseDto = TweetResponseDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .tweetText("Test tweet content")
                .replyCounter(0)
                .retweetCounter(0)
                .likeCounter(0)
                .build();
    }

    @Test
    @WithMockUser
    void createTweet_ShouldReturnOk_WhenValidTweetDto() throws Exception {
        // Given
        doNothing().when(tweetService).tweet(any(TweetDto.class));

        // When & Then
        mockMvc.perform(post("/posts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tweetDto)))
                .andExpect(status().isOk());

        verify(tweetService, times(1)).tweet(any(TweetDto.class));
    }

    @Test
    @WithMockUser
    void deleteTweet_ShouldReturnOk_WhenValidId() throws Exception {
        // Given
        Long tweetId = 1L;
        doNothing().when(tweetService).deleteTweet(anyLong());

        // When & Then
        mockMvc.perform(delete("/posts/delete/{id}", tweetId))
                .andExpect(status().isOk());

        verify(tweetService, times(1)).deleteTweet(tweetId);
    }

    @Test
    @WithMockUser
    void getTweetsByUsername_ShouldReturnTweets_WhenValidUsername() throws Exception {
        // Given
        String username = "johndoe";
        List<TweetResponseDto> tweets = Arrays.asList(tweetResponseDto);
        when(tweetService.getTweetsByUsername(username)).thenReturn(tweets);

        // When & Then
        mockMvc.perform(get("/posts/tweets-by-username/{username}", username))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("johndoe"))
                .andExpect(jsonPath("$[0].tweetText").value("Test tweet content"));

        verify(tweetService, times(1)).getTweetsByUsername(username);
    }

    @Test
    @WithMockUser
    void getRetweetsByUsername_ShouldReturnRetweets_WhenValidUsername() throws Exception {
        // Given
        String username = "johndoe";
        List<TweetResponseDto> retweets = Arrays.asList(tweetResponseDto);
        when(tweetService.getRetweetsByUsername(username)).thenReturn(retweets);

        // When & Then
        mockMvc.perform(get("/posts/retweets-by-username/{username}", username))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(tweetService, times(1)).getRetweetsByUsername(username);
    }

    @Test
    @WithMockUser
    void getRepliesByUsername_ShouldReturnReplies_WhenValidUsername() throws Exception {
        // Given
        String username = "johndoe";
        List<TweetResponseDto> replies = Arrays.asList(tweetResponseDto);
        when(tweetService.getRepliesByUsername(username)).thenReturn(replies);

        // When & Then
        mockMvc.perform(get("/posts/replies-by-username/{username}", username))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(tweetService, times(1)).getRepliesByUsername(username);
    }

    @Test
    @WithMockUser
    void getLikedByUsername_ShouldReturnLikedTweets_WhenValidUsername() throws Exception {
        // Given
        String username = "johndoe";
        List<TweetResponseDto> likedTweets = Arrays.asList(tweetResponseDto);
        when(tweetService.getLikedByUsername(username)).thenReturn(likedTweets);

        // When & Then
        mockMvc.perform(get("/posts/liked-by-username/{username}", username))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(tweetService, times(1)).getLikedByUsername(username);
    }

    @Test
    @WithMockUser
    void getAllTweets_ShouldReturnAllTweets() throws Exception {
        // Given
        List<TweetResponseDto> tweets = Arrays.asList(tweetResponseDto);
        when(tweetService.getAll()).thenReturn(tweets);

        // When & Then
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(tweetService, times(1)).getAll();
    }

    @Test
    @WithMockUser
    void getRepliesForTweet_ShouldReturnReplies_WhenValidTweetId() throws Exception {
        // Given
        Long tweetId = 1L;
        List<TweetResponseDto> replies = Arrays.asList(tweetResponseDto);
        when(tweetService.getRepliesForTweet(tweetId)).thenReturn(replies);

        // When & Then
        mockMvc.perform(get("/posts/replies-for-tweet/{id}", tweetId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(tweetService, times(1)).getRepliesForTweet(tweetId);
    }
}
