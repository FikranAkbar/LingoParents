package com.glints.lingoparents.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.glints.lingoparents.data.remote.api.ApiClient
import com.glints.lingoparents.data.remote.body.UserLogin
import com.glints.lingoparents.data.remote.response.UserLoginResponse
import com.glints.lingoparents.data.remote.vo.ApiResponse
import com.glints.lingoparents.data.remote.vo.StatusResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

class RemoteDataSource {
    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null


        fun getInstance(): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource()
            }
    }

    fun loginUser(email: String, password: String): LiveData<ApiResponse<UserLoginResponse>> {
        val result = MutableLiveData<ApiResponse<UserLoginResponse>>()
        CoroutineScope(IO).launch {
            ApiClient
                .instances
                .loginUser(UserLogin(email, password))
                .enqueue(object : Callback<UserLoginResponse> {
                    override fun onResponse(
                        call: Call<UserLoginResponse>,
                        response: Response<UserLoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            result.postValue(ApiResponse.success(response.body()!!))
                        }
                        else {
                            val jsonObjError = JSONObject(response.errorBody()!!.string())
                            val msg = jsonObjError.getString("errorMessage")
                            result.postValue(ApiResponse.error(msg, UserLoginResponse()))
                        }
                    }

                    override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                        throw t
                    }
                })
        }

        return result
    }
}