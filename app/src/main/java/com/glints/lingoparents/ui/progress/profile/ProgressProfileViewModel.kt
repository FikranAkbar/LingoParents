package com.glints.lingoparents.ui.progress.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.StudentCharacterResponse
import com.glints.lingoparents.data.model.response.StudentProfileResponse
import com.glints.lingoparents.utils.ErrorUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProgressProfileViewModel : ViewModel() {
    private val progressProfileEventChannel = Channel<ProgressProfileEvent>()
    val progressProfileEvent = progressProfileEventChannel.receiveAsFlow()

    private val studentCharacterEventChannel = Channel<StudentCharacterEvent>()
    val studentCharacterEvent = studentCharacterEventChannel.receiveAsFlow()

    private fun onApiCallStarted() = viewModelScope.launch {
        progressProfileEventChannel.send(ProgressProfileEvent.Loading)
    }

    private fun onApiCallSuccess(result: StudentProfileResponse.Data) = viewModelScope.launch {
        progressProfileEventChannel.send(ProgressProfileEvent.Success(result))
    }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        progressProfileEventChannel.send(ProgressProfileEvent.Error(message))
    }

    private fun onApiCallLoadingCharacter() = viewModelScope.launch {
        studentCharacterEventChannel.send(StudentCharacterEvent.Loading)
    }

    private fun onApiCallSuccessCharacter(result: StudentCharacterResponse.Data) =
        viewModelScope.launch {
            studentCharacterEventChannel.send(StudentCharacterEvent.Success(result))
        }

    private fun onApiCallErrorCharacter(message: String) = viewModelScope.launch {
        studentCharacterEventChannel.send(StudentCharacterEvent.Error(message))
    }

    fun getStudentCharacter(characterId: Int) {
        onApiCallLoadingCharacter()
        APIClient
            .service
            .getStudentCharacter(characterId)
            .enqueue(object : Callback<StudentCharacterResponse> {
                override fun onResponse(
                    call: Call<StudentCharacterResponse>,
                    response: Response<StudentCharacterResponse>,
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()?.data!!
                        onApiCallSuccessCharacter(result)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallErrorCharacter(apiError.message())
                    }
                }

                override fun onFailure(call: Call<StudentCharacterResponse>, t: Throwable) {
                    onApiCallErrorCharacter("Network Failed...")
                }

            })
    }

    fun getStudentProfileByStudentId(id: Int) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getStudentProfileById(id)
            .enqueue(object : Callback<StudentProfileResponse> {
                override fun onResponse(
                    call: Call<StudentProfileResponse>,
                    response: Response<StudentProfileResponse>,
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()?.data!!
                        onApiCallSuccess(result)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<StudentProfileResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    sealed class ProgressProfileEvent {
        object Loading : ProgressProfileEvent()
        data class Success(val result: StudentProfileResponse.Data) : ProgressProfileEvent()
        data class Error(val message: String) : ProgressProfileEvent()
    }

    sealed class StudentCharacterEvent {
        object Loading : StudentCharacterEvent()
        data class Success(val result: StudentCharacterResponse.Data) : StudentCharacterEvent()
        data class Error(val message: String) : StudentCharacterEvent()
    }

}