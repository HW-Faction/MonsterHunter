package monster.hunter.armor.model

import monster.hunter.armor.util.Rank
import monster.hunter.armor.util.Type

data class ArmorModel(
    val name: String,
    val type: Type,
    val rank: String,
    val defense: DefenseModel,
    val slots: ArrayList<SlotModel>
)

data class DefenseModel(
    val base: Int,
    val max: Int,
    val augmented: Int,
)

data class SlotModel(
    val rank: Int
)