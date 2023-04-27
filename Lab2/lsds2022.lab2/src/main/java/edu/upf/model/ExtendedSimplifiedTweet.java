package edu.upf.model;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Serializable;
import java.util.Optional;

public class ExtendedSimplifiedTweet implements Serializable , Comparable<ExtendedSimplifiedTweet> {
  private final long tweetId;
  private final String text;
  private final long userId;
  private final String userName;
  private final long followersCount;
  private final String language;
  private final boolean isRetweeted;
  private final Long retweetedUserId;
  private final Long retweetedTweetId;
  private final long timestampMs;

  private static JsonParser parser = new JsonParser();

  public ExtendedSimplifiedTweet(long tweetId, String text, long userId, String userName,
                                 long followersCount, String language, boolean isRetweeted,
                                 Long retweetedUserId, Long retweetedTweetId, long timestampMs) {
    this.tweetId = tweetId;
    this.text = text;
    this.userId = userId;
    this.userName = userName;
    this.followersCount = followersCount;
    this.language = language != null ? language : "";
    this.isRetweeted = isRetweeted;
    this.retweetedUserId = retweetedUserId;
    this.retweetedTweetId = retweetedTweetId;
    this.timestampMs = timestampMs;
  }

  public static Optional<ExtendedSimplifiedTweet> fromJson(String jsonStr) {
    try {
      JsonElement je = parser.parse(jsonStr);
      JsonObject jo = je.getAsJsonObject();

      long tweetId_ = 0;
      String text_ = null;
      long userId_ = 0;
      String userName_ = null;
      long followersCount_ = 0;
      String language_ = null;
      boolean isRetweeted_ = false;
      Long retweetedUserId_ = null;
      Long retweetedTweetId_ = null;
      long timestampMs_ = 0;
      int parameters = 0;

      if (jo.has("id")) {
        tweetId_ = jo.get("id").getAsLong();
      }
      if (jo.has("text")) {
        text_ = jo.get("text").getAsString();
      }

      if(jo.has("user")) {
        JsonObject userObj = jo.getAsJsonObject("user");

        if(userObj.has("id")) {
          userId_ = userObj.get("id").getAsLong();

        }
        if(userObj.has("name")) {
          userName_ = userObj.get("name").getAsString();

        }
        if (userObj.has("followers_count")) {
          followersCount_ = userObj.get("followers_count").getAsLong();
        }
      }
      if (jo.has("lang")) {
        language_ = jo.get("lang").getAsString();

      }

      if (jo.has("timestamp_ms")) {
        timestampMs_ = jo.get("timestamp_ms").getAsLong();
  
      }
      if (jo.has("retweeted_status")) {
        isRetweeted_ = true;
        JsonObject retweetedStatusObj = jo.getAsJsonObject("retweeted_status");

        if (retweetedStatusObj.has("id")) {
          retweetedTweetId_ = retweetedStatusObj.get("id").getAsLong();
        }

        if (retweetedStatusObj.has("user")) {
          JsonObject retweetedUserObj = retweetedStatusObj.getAsJsonObject("user");

          if (retweetedUserObj.has("id")) {
            retweetedUserId_ = retweetedUserObj.get("id").getAsLong();
          }
        }
      }

      return Optional.of(new ExtendedSimplifiedTweet(tweetId_, text_, userId_, userName_,
                                                     followersCount_, language_, isRetweeted_,
                                                     retweetedUserId_, retweetedTweetId_, timestampMs_));
    }
    
    catch (Exception e) {
      return Optional.empty();
    }

  }


  @Override
  public String toString() {
    // Overriding how SimplifiedTweets are printed in console or the output file
    // The following line produces valid JSON as output
    return new Gson().toJson(this);
  }

  public String getLanguage() {
    return language == null ? "" : language;
  }
  public String getText() {
    return this.text;
  }

  public Long getRetweetedUserId() {
    return retweetedUserId;
  }

  public boolean isRetweeted() {
    return retweetedTweetId != null;
  }
  
  public int getRetweetCount() {
    return this.isRetweeted() ? 1 : 0;
}
  public int compareTo(ExtendedSimplifiedTweet other) {
    if (this.getRetweetedUserId() < other.getRetweetedUserId()) {
        return -1;
    } else if (this.getRetweetedUserId() > other.getRetweetedUserId()) {
        return 1;
    } else {
        return 0;
    }
  }
  
}