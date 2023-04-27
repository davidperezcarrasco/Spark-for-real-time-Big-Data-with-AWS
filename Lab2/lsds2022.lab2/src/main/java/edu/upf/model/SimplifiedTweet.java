package edu.upf.model;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Optional;

public class SimplifiedTweet {

  // All classes use the same instance
  private static JsonParser parser = new JsonParser();

  private final long tweetId;			  // the id of the tweet ('id')
  private final String text;  		      // the content of the tweet ('text')
  private final long userId;			  // the user id ('user->id')
  private final String userName;		  // the user name ('user'->'name')
  public final String language;          // the language of a tweet ('lang')
  private final long timestampMs;		  // seconduserIds from epoch ('timestamp_ms')

  public SimplifiedTweet(long tweetId, String text, long userId, String userName,
                         String language, long timestampMs) {
    this.tweetId = tweetId;
    this.text = text;
    this.userId = userId;
    this.userName = userName;
    this.language = language;
    this.timestampMs = timestampMs;

  }

  //TODO: add failing stuff

  /**
   * Returns a {@link SimplifiedTweet} from a JSON String.
   * If parsing fails, for any reason, return an {@link Optional#empty()}
   *
   * @param jsonStr
   * @return an {@link Optional} of a {@link SimplifiedTweet}
   */
  public static Optional<SimplifiedTweet> fromJson(String jsonStr) {
    try {
      // PLACE YOUR CODE HERE!
      JsonElement je = parser.parse(jsonStr);
      JsonObject jo = je.getAsJsonObject();

      String text_ = null;
      String language_ = null;
      long tweetId_ = 0;
      long userId_ = 0;
      String userName_ = null;
      long StampTime = 0;
      int parameters = 0;

      if (jo.has("id")) {
        tweetId_ = jo.get("id").getAsLong();
        parameters++;
      }

      if(jo.has("user")) {
        JsonObject userObj = jo.getAsJsonObject("user");
        parameters++;
        if(userObj.has("id")) {
          userId_ = userObj.get("id").getAsLong();
          parameters++;
        }
        if(userObj.has("name")) {
          userName_ = userObj.get("name").getAsString();
          parameters++;
        }
      }
      if (jo.has("lang")) {
        language_ = jo.get("lang").getAsString();
        parameters++;
      }
      if (jo.has("text")) {
        text_ = jo.get("text").getAsString();
        parameters++;
      }
      if (jo.has("timestamp_ms")) {
        StampTime = jo.get("timestamp_ms").getAsLong();
        parameters++;
      }
      if(parameters != 7) {
        return Optional.empty();
      }
      return Optional.ofNullable( new SimplifiedTweet(tweetId_, text_, userId_, userName_, language_,StampTime));
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
    return this.language;
  }
  public String getText() {
    return this.text;
  }
}