import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.polary.R
import com.example.polary.constant.IconDrawable

data class AppIcon(val aliasName: String) {
    val resourceId: Int
        get() = IconDrawable.map[aliasName] ?: R.mipmap.ic_launcher
}
class AppIconAdapter(
    private val icons: List<AppIcon>,
    private val onIconClicked: (AppIcon) -> Unit
) : RecyclerView.Adapter<AppIconAdapter.IconViewHolder>() {

    inner class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(icon: AppIcon) {
            itemView.findViewById<ImageView>(R.id.iconImageView).setImageResource(icon.resourceId)
            itemView.setOnClickListener { onIconClicked(icon) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.icon_item, parent, false)
        return IconViewHolder(view)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.bind(icons[position])
    }

    override fun getItemCount() = icons.size
}