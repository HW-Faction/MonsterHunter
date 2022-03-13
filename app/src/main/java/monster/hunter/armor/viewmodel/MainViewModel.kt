package monster.hunter.armor.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import monster.hunter.armor.MainApplication
import monster.hunter.armor.model.ArmorModel
import monster.hunter.armor.repository.MainRepository
import monster.hunter.armor.util.Constants.Companion.TAG
import monster.hunter.armor.util.Resource
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class MainViewModel(
    app: Application,
    private val repository: MainRepository
) : AndroidViewModel(app) {

    val armors: MutableLiveData<Resource<ArrayList<ArmorModel>>> = MutableLiveData()
    var armorResponse: ArrayList<ArmorModel>? = null

    val searchedArmors: MutableLiveData<Resource<ArrayList<ArmorModel>>> = MutableLiveData()
    var searchedArmorResponse: ArrayList<ArmorModel>? = null

    init {
        getArmors()
    }

    private fun getArmors() = viewModelScope.launch {
        safeArmorsCall()
    }

    fun searchArmor(searchQuery: String) = viewModelScope.launch {
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("name", searchQuery)
        safeSearchArmorCall(jsonObject)
    }

    // safe call for repository method getArmors()
    private suspend fun safeArmorsCall() {
        // set armor value to loading at first
        armors.postValue(Resource.Loading())
        // check for internet connection
        try {
            if (hasInternetConnection()) {
                val response = repository.getArmors()   // call for repository function
                Log.d(TAG, "repository.getArmors() called")
                Log.d(TAG, "response$response")
                armors.postValue(handleArmorsResponse(response))
            } else {
                armors.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> armors.postValue(Resource.Error("Network Failure"))
                else -> armors.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private suspend fun safeSearchArmorCall(searchQuery: JSONObject) {
        searchedArmors.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.searchArmors(searchQuery)
                searchedArmors.postValue(handleSearchNewsResponse(response))
            } else {
                searchedArmors.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchedArmors.postValue(Resource.Error("Network Failure"))
                else -> searchedArmors.postValue(Resource.Error("${t.message}"))
            }
        }
    }


    // handle response from repository
    private fun handleArmorsResponse(response: Response<ArrayList<ArmorModel>>): Resource<ArrayList<ArmorModel>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (armorResponse == null) {
                    armorResponse = resultResponse
                }
                return Resource.Success(armorResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    // handle search response from the repository
    private fun handleSearchNewsResponse(response: Response<ArrayList<ArmorModel>>): Resource<ArrayList<ArmorModel>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchedArmorResponse = resultResponse
                return Resource.Success(searchedArmorResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    // check weather the phone has internet connection or not
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<MainApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}