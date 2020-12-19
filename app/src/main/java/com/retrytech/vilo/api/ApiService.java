package com.retrytech.vilo.api;

import com.google.gson.JsonObject;
import com.retrytech.vilo.model.Explore;
import com.retrytech.vilo.model.comment.Comment;
import com.retrytech.vilo.model.follower.Follower;
import com.retrytech.vilo.model.music.Musics;
import com.retrytech.vilo.model.music.SearchMusic;
import com.retrytech.vilo.model.notification.Notification;
import com.retrytech.vilo.model.user.RestResponse;
import com.retrytech.vilo.model.user.SearchUser;
import com.retrytech.vilo.model.user.User;
import com.retrytech.vilo.model.videos.Video;
import com.retrytech.vilo.model.wallet.CoinPlan;
import com.retrytech.vilo.model.wallet.CoinRate;
import com.retrytech.vilo.model.wallet.MyWallet;
import com.retrytech.vilo.model.wallet.RewardingActions;

import java.util.HashMap;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface ApiService {


    @FormUrlEncoded
    @POST("User/registration")
    Single<User> registrationUser(@FieldMap HashMap<String, Object> hashMap);

    @POST("Post/sound_list")
    Single<Musics> getSoundList(@Header("Authorization") String token);

    @POST("Post/favourite_sound")
    Single<SearchMusic> getFavSoundList(@Header("Authorization") String token, @Body JsonObject ids);

    @FormUrlEncoded
    @POST("Post/user_list_search")
    Single<SearchUser> searchUser(@Field("keyword") String keyword,
                                  @Field("count") int count,
                                  @Field("start") int start);

    @FormUrlEncoded
    @POST("Post/sound_list_search")
    Single<SearchMusic> searchSoundList(@Header("Authorization") String token,
                                        @Field("keyword") String keyword);

    @FormUrlEncoded
    @POST("User/check_username")
    Single<RestResponse> checkUsername(@Field("user_name") String userName);


    @GET("User/logout")
    Single<RestResponse> logOutUser(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("Post/add_comment")
    Single<RestResponse> addComment(@Header("Authorization") String token,
                                    @Field("post_id") String postId,
                                    @Field("comment") String comment);

    @FormUrlEncoded
    @POST("Post/follow_unfollow")
    Single<RestResponse> followUnFollow(@Header("Authorization") String token,
                                        @Field("to_user_id") String toUserId);


    @FormUrlEncoded
    @POST("Post/delete_post")
    Single<RestResponse> deletePost(@Header("Authorization") String token,
                                    @Field("post_id") String postId);


    @FormUrlEncoded
    @POST("User/notification_setting")
    Single<RestResponse> updateToken(@Header("Authorization") String token,
                                     @Field("device_token") String deviceToken);


    @FormUrlEncoded
    @POST("Wallet/purchase_coin")
    Single<RestResponse> purchaseCoin(@Header("Authorization") String token,
                                      @Field("coin") String coinAmount);

    @FormUrlEncoded
    @POST("Wallet/redeem_request")
    Single<RestResponse> sendRedeemRequest(@Header("Authorization") String token,
                                           @Field("amount") String coinAmount,
                                           @Field("redeem_request_type") String requestTypeType,
                                           @Field("account") String account);


    @FormUrlEncoded
    @POST("Post/like_unlike")
    Single<RestResponse> likeUnlike(@Header("Authorization") String token,
                                    @Field("post_id") String postId);


    @FormUrlEncoded
    @POST("Wallet/send_coin")
    Single<RestResponse> sendCoin(@Header("Authorization") String token,
                                  @Field("coin") String coin,
                                  @Field("to_user_id") String toUserId);


    @Multipart
    @POST("User/user_update")
    Single<User> updateUser(@Header("Authorization") String token,
                            @PartMap HashMap<String, RequestBody> hasMap,
                            @Part MultipartBody.Part requestBody);


    @FormUrlEncoded
    @POST("Post/single_hash_tag_video")
    Single<Video> fetchHasTagVideo(@Field("hash_tag") String hashTag,
                                   @Field("count") int count,
                                   @Field("start") int start,
                                   @Field("my_user_id") String myUserId);

    @FormUrlEncoded
    @POST("Post/hash_tag_search_video")
    Single<Video> searchVideo(@Field("keyword") String keyword,
                              @Field("count") int count,
                              @Field("start") int start,
                              @Field("my_user_id") String myUserId);

    @FormUrlEncoded
    @POST("User/user_details")
    Single<User> getUserDetails(@Field("user_id") String userId,
                                @Field("my_user_id") String myUserId);

    @FormUrlEncoded
    @POST("Post/user_videos")
    Single<Video> getUserVideos(@Field("user_id") String userId,
                                @Field("count") int count,
                                @Field("start") int start,
                                @Field("my_user_id") String myUserId);

    @FormUrlEncoded
    @POST("Post/user_likes_videos")
    Single<Video> getUserLikedVideos(@Field("user_id") String userId,
                                     @Field("count") int count,
                                     @Field("start") int start,
                                     @Field("my_user_id") String myUserId);

    @FormUrlEncoded
    @POST("Post/explore_hash_tag_video")
    Single<Explore> getExploreVideos(@Field("count") int count,
                                     @Field("start") int start,
                                     @Field("my_user_id") String myUserId);


    @FormUrlEncoded
    @POST("Post/sound_video")
    Single<Video> getSoundVideos(@Field("count") int count,
                                 @Field("start") int start,
                                 @Field("sound_id") String soundId,
                                 @Field("my_user_id") String myUserId);


    @FormUrlEncoded
    @POST("Post/post_list")
    Single<Video> getPostVideos(@Field("type") String type,
                                @Field("count") int count,
                                @Field("start") int start,
                                @Field("user_id") String myUserId);

    @Multipart
    @POST("Post/add_post")
    Single<User> uploadPost(@Header("Authorization") String token,
                            @PartMap HashMap<String, RequestBody> hasMap,
                            @Part MultipartBody.Part postVideo,
                            @Part MultipartBody.Part postImage,
                            @Part MultipartBody.Part postSound,
                            @Part MultipartBody.Part soundImage);


    @GET("Wallet/coin_plan")
    Single<CoinPlan> getCoinPlans(@Header("Authorization") String token);


    @FormUrlEncoded
    @POST("Post/commet_list")
    Single<Comment> getPostComments(@Field("post_id") String postId,
                                    @Field("count") int count,
                                    @Field("start") int start);


    @FormUrlEncoded
    @POST("Post/delete_comment")
    Single<RestResponse> deleteComment(@Header("Authorization") String token,
                                       @Field("comments_id") String commentsId);

    @FormUrlEncoded
    @POST("Post/following_list")
    Single<Follower> getFollowingList(@Header("Authorization") String token,
                                      @Field("user_id") String userId,
                                      @Field("count") int count,
                                      @Field("start") int start);


    @FormUrlEncoded
    @POST("Post/follower_list")
    Single<Follower> getFollowerList(@Header("Authorization") String token,
                                     @Field("user_id") String userId,
                                     @Field("count") int count,
                                     @Field("start") int start);


    @FormUrlEncoded
    @POST("Post/report")
    Single<RestResponse> reportSomething(@FieldMap HashMap<String, Object> fieldMap);

    @GET("Wallet/my_wallet_coin")
    Single<MyWallet> getMyWalletDetails(@Header("Authorization") String token);

    @GET("Wallet/coin_rate")
    Single<CoinRate> getCoinRate(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("User/notification_list")
    Single<Notification> getNotificationList(@Header("Authorization") String token,
                                             @Field("user") String userId,
                                             @Field("count") int count,
                                             @Field("start") int start);

    @FormUrlEncoded
    @POST("Wallet/add_coin")
    Single<RestResponse> rewardUser(@Header("Authorization") String token,
                                    @Field("rewarding_action_id") String rewardActionId);

    @FormUrlEncoded
    @POST("Post/increase_video_view")
    Single<RestResponse> increaseView(@Field("post_id") String postId);


    @GET("Wallet/rewarding_action")
    Single<RewardingActions> getRewardingAction(@Header("Authorization") String token);

    @Multipart
    @POST("User/verify_request")
    Single<RestResponse> verifyRequest(@Header("Authorization") String token,
                                       @PartMap HashMap<String, RequestBody> hasMap,
                                       @Part MultipartBody.Part photoIdImage,
                                       @Part MultipartBody.Part photoWithIdImage);

}
