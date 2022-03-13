package monster.hunter.armor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.armor_recyclerview_item.view.*
import monster.hunter.armor.R
import monster.hunter.armor.model.ArmorModel

class ArmorAdapter(private val list: ArrayList<ArmorModel>) :
    RecyclerView.Adapter<ArmorAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArmorAdapter.ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.armor_recyclerview_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ArmorAdapter.ArticleViewHolder, position: Int) {
        holder.itemView.apply {
            name_rv_item.text = list[position].name
            defense_rv_item.text = list[position].defense.base.toString()

            if (list[position].slots.isEmpty()) {
                slot_rv_item.text = "-"
            } else slot_rv_item.text = list[position].slots[0].rank.toString()
            rank_rv_item.text = list[position].rank

            type_imageView_rv_item.setImageResource(
                when (list[position].type.name) {
                    "head" -> R.drawable.ic_head
                    "chest" -> R.drawable.ic_chest
                    "gloves" -> R.drawable.ic_gloves
                    "waist" -> R.drawable.ic_waist
                    else -> R.drawable.ic_legs
                }
            )
        }
    }
}