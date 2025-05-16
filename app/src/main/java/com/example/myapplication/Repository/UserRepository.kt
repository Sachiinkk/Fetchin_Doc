package com.example.myapplication.Repository
import retrofit2.Response
import javax.inject.Inject
import com.example.myapplication.Remote.RetrofitInstance
import com.example.myapplication.Remote.UserDocumentResponse
import  com.example.myapplication.Remote.UserInfoRequest
class UserRepository @Inject constructor() {

    // Fetch user details after Aadhaar authentication
    suspend fun getUserDetails(token: String, request: UserInfoRequest): Response<UserDocumentResponse> {
        return RetrofitInstance.api.getUserDocuments("Bearer $token", request)
    }


}
