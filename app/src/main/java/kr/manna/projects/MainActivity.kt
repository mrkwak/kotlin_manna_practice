package kr.manna.projects

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AlertDialog
import kr.manna.projects.activity.ListScrollActivity
import kr.manna.projects.databinding.ActivityMainBinding
import kr.manna.projects.entity.User
import kr.manna.projects.service.RetrofitService
import mannaPlanet.hermes.commonActivity.MAppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : MAppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sharedPreferences.edit()

        checkSharedPreference()
        if (binding.autoLoginButton.isChecked) {
            binding.userId.setText(sharedPreferences.getString("userId", ""))
            binding.userPassword.setText(sharedPreferences.getString("userPassword", ""))
        }

        binding.loginButton.setOnClickListener {
            val intent = Intent(this, ListScrollActivity::class.java)
            val builder = AlertDialog.Builder(this)

            val userId = binding.userId.text
            val userPassword = binding.userPassword.text
            val autoLogin = binding.autoLoginButton

            //  TODO ㅊㅔ크랑 api 따로 빼기
            if (userId.isNullOrEmpty()) {
                // 다이얼로그
                builder.setTitle("아이디를 입력해 주세요")
                builder.setIcon(R.mipmap.ic_launcher)
                builder.create()
                builder.show()
                return@setOnClickListener

            } else if (userPassword.isNullOrEmpty()) {
                // 다이얼로그
                builder.setTitle("비밀번호를 입력해 주세요")
                builder.setIcon(R.mipmap.ic_launcher)
                builder.create()
                builder.show()
                return@setOnClickListener
            }

            if (autoLogin.isChecked) {
                editor.putBoolean("autoLoginButton", true)
                editor.putString("userId", userId.toString())
                editor.putString("userPassword", userPassword.toString())
                editor.commit()
            } else {
                editor.putBoolean("autoLoginButton", false)
                editor.putString("userId", "")
                editor.putString("userPassword", "")
                editor.commit()
            }

            // api 통신으로 아이디 비밀번호 보내고 결과값 잘 오는지 테스트 필요
            val retrofit = Retrofit.Builder().baseUrl("http://211.171.200.80:9999/")
                .addConverterFactory(GsonConverterFactory.create()).build()
            val service = retrofit.create(RetrofitService::class.java)

            service.getUserLogin(userId.toString(), userPassword.toString()).enqueue(object :
                Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성고된 경우 try catch 추가
                        val result: User? = response.body()
                        // TODO result null 일경우 처리 필요
                        if (result?.outCode == 1) {
                            //화면 이동
                            startActivity(intent)

                        } else {
                            builder.setTitle("${result?.outMsg}") // result 널이면 처리 builder 도 재선언 필요
                            builder.setIcon(R.mipmap.ic_launcher)
                            builder.create()
                            builder.show()
                            return
                        }

                        Log.d("LOGIN", "onResponse 성공:$result")

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        Log.d("LOGIN", "onResponse 실패$response")
                    }
                }


                override fun onFailure(call: Call<User>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    Log.d("LOGIN", "onFailure 에러: " + t.message.toString());
                }
            })
        }

    }

    private fun checkSharedPreference() {
        binding.autoLoginButton.isChecked = sharedPreferences.getBoolean("autoLoginButton", false)
    }
}

