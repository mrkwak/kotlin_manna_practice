package kr.manna.projects.service

import kr.manna.projects.entity.Item
import kr.manna.projects.entity.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface RetrofitService {
    //GET 예제
    @GET("study/login.php")
    fun getUserLogin(
        @Query("id") id : String,
        @Query("password") password : String
    ): Call<User>

    @GET("study/list.php")
    fun getItemList(
        @Query("page") page : Int
    ): Call<Item>

}

//    @POST()
//    fun postUserLogin(@Path("page") page: String : Call<User>)
//    @GET("posts/1")
//    fun getStudent(@Query("school_id") schoolId: Int,
//                   @Query("grade") grade: Int,
//                   @Query("classroom") classroom: Int): Call<ExampleResponse>
//
//
//    //POST 예제
//    @FormUrlEncoded
//    @POST("posts")
//    fun getContactsObject(@Field("idx") idx: String): Call<JsonObject>
