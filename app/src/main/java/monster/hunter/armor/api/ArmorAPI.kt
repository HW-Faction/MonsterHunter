package monster.hunter.armor.api

import monster.hunter.armor.model.ArmorModel
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ArmorAPI {

    @GET("/armor")
    suspend fun getArmors(): Response<ArrayList<ArmorModel>>

    @GET("/armor")
    suspend fun searchForArmors(
        @Query("q")
        searchQuery: JSONObject
    ): Response<ArrayList<ArmorModel>>
}