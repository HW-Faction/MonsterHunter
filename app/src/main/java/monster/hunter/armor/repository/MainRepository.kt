package monster.hunter.armor.repository

import monster.hunter.armor.api.RetrofitInstance
import org.json.JSONObject

class MainRepository {
    suspend fun getArmors() = RetrofitInstance.api.getArmors()

    suspend fun searchArmors(jsonObject: JSONObject) = RetrofitInstance.api.searchForArmors(jsonObject)
}