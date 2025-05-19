package com.example.myapplication.Repository
import com.example.myapplication.Remote.APIservice2
import retrofit2.Response
import javax.inject.Inject
import com.example.myapplication.Remote.RetrofitInstance
import com.example.myapplication.Remote.UserDocumentResponse
import  com.example.myapplication.Remote.UserInfoRequest
import com.example.myapplication.Remote.ViewDocumentRequest
import com.example.myapplication.Remote.ViewDocumentResponse
class UserRepository @Inject constructor() {

    // Fetch user details after Aadhaar authentication
    suspend fun getUserDetails(token: String, request: UserInfoRequest): Response<UserDocumentResponse> {
        return RetrofitInstance.api.getUserDocuments("Bearer $token", request)
    }

    suspend fun viewDocument( request: ViewDocumentRequest): Response<ViewDocumentResponse> {
        return RetrofitInstance.api2.viewDocument( request)


    }



}
