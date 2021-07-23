package io.iskaldvind.pictureoftheday.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.iskaldvind.pictureoftheday.R
import io.iskaldvind.pictureoftheday.api.BaseViewHolder
import io.iskaldvind.pictureoftheday.api.Change
import io.iskaldvind.pictureoftheday.api.createCombinedPayload
import io.iskaldvind.pictureoftheday.ui.picture.PictureOfTheDayFragment
import kotlinx.android.synthetic.main.task_element.view.*

class MainActivity : AppCompatActivity() {

    lateinit var adapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sp = getSharedPreferences("preferences", Context.MODE_PRIVATE)
        //val themeId = sp.getInt("theme", R.style.Light)
        //val theme = if (themeId == R.style.Light) themeId else R.style.Dark
        setTheme(R.style.AppTheme)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PictureOfTheDayFragment.newInstance())
                .commitNow()
        }

        adapter = RecyclerAdapter(mutableListOf())
    }


    object ThemeHolder {
        var theme: Int = R.style.Light
    }


    fun changeTheme() {
        ThemeHolder.theme = if (ThemeHolder.theme == R.style.Light) R.style.Dark else R.style.Light
        val sp = getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt("theme", ThemeHolder.theme)
        editor.apply()
        this.recreate()
    }


    data class Data(
        val id: Int = 0,
        var someText: String = "Text",
        var someDescription: String? = "Description"
    )

    interface ItemTouchHelperAdapter {
        fun onItemMove(fromPosition: Int, toPosition: Int)
        fun onItemDismiss(position: Int)
    }

    class RecyclerAdapter(
        private var data: MutableList<Data>,
    ) :
        RecyclerView.Adapter<BaseViewHolder>(), ItemTouchHelperAdapter {

        private val titlesPoolFirst = listOf("Clear", "Feed", "Wash", "Buy", "Kick", "Paint", "Watch", "Find")
        private val titlesPoolSecond = listOf("the tree", "the cat", "the car", "the bread", "the pot", "the bus", "the bee", "the papers")
        private val descriptionsPoolFirst = listOf("Nobody must see it", "Hurry", "Call the police if something goes wrong", "Do not forget to turn lights off in the end", "Your grandfather will be happy", "Do it as soon as possible", "Try not to dirty your hands")

        private fun randomTitle() = "${titlesPoolFirst.shuffled()[0]} ${titlesPoolSecond.shuffled()[0]}"
        private fun randomDescription() = descriptionsPoolFirst.shuffled()[0]

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return when (viewType) {
                TYPE_TASK -> TaskViewHolder(
                    inflater.inflate(R.layout.task_element, parent, false) as View
                )
                else -> TaskViewHolder(
                    inflater.inflate(R.layout.task_element, parent, false) as View
                )
            }
        }

        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
            holder.bind(data[position])
        }

        override fun onBindViewHolder(
            holder: BaseViewHolder,
            position: Int,
            payloads: MutableList<Any>
        ) {
            if (payloads.isEmpty())
                super.onBindViewHolder(holder, position, payloads)
            else {
                val combinedChange =
                    createCombinedPayload(payloads as List<Change<Data>>)
                val oldData = combinedChange.oldData
                val newData = combinedChange.newData

                if (newData.someText != oldData.someText) {
                    holder.itemView.task_title.text = newData.someText
                }
            }
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun getItemViewType(position: Int): Int {
            return TYPE_TASK
        }

        override fun onItemMove(fromPosition: Int, toPosition: Int) {
            data.removeAt(fromPosition).apply {
                data.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, this)
            }
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onItemDismiss(position: Int) {
            data.removeAt(position)
            notifyItemRemoved(position)
        }

        fun setItems(newItems: List<Data>) {
            val result = DiffUtil.calculateDiff(DiffUtilCallback(data, newItems))
            result.dispatchUpdatesTo(this)
            data.clear()
            data.addAll(newItems)
        }

        fun appendItem() {
            Log.d("MAIN", "Append")
            data.add(generateItem())
            notifyItemInserted(itemCount - 1)
        }

        private fun generateItem() = Data(1, randomTitle(), randomDescription())

        inner class DiffUtilCallback(
            private var oldItems: List<Data>,
            private var newItems: List<Data>
        ) : DiffUtil.Callback() {

            override fun getOldListSize(): Int = oldItems.size

            override fun getNewListSize(): Int = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                oldItems[oldItemPosition].id == newItems[newItemPosition].id

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                oldItems[oldItemPosition].someText == newItems[newItemPosition].someText

            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                val oldItem = oldItems[oldItemPosition]
                val newItem = newItems[newItemPosition]

                return Change(
                    oldItem,
                    newItem
                )
            }
        }

        inner class TaskViewHolder(view: View) : BaseViewHolder(view) {

            override fun bind(dataItem: Data) {
                if (layoutPosition != RecyclerView.NO_POSITION) {
                    itemView.task_title.text = dataItem.someText
                    itemView.task_body.text = dataItem.someDescription
                    itemView.task_delete.setOnClickListener { removeItem() }
                    itemView.task_change.setOnClickListener { change() }
                    itemView.task_up.setOnClickListener { moveUp() }
                    itemView.task_down.setOnClickListener { moveDown() }
                }
            }

            private fun removeItem() {
                data.removeAt(layoutPosition)
                notifyItemRemoved(layoutPosition)
            }

            private fun moveUp() {
                layoutPosition.takeIf { it > 1 }?.also { currentPosition ->
                    data.removeAt(currentPosition).apply {
                        data.add(currentPosition - 1, this)
                    }
                    notifyItemMoved(currentPosition, currentPosition - 1)
                }
            }

            private fun moveDown() {
                layoutPosition.takeIf { it < data.size - 1 }?.also { currentPosition ->
                    data.removeAt(currentPosition).apply {
                        data.add(currentPosition + 1, this)
                    }
                    notifyItemMoved(currentPosition, currentPosition + 1)
                }
            }

            private fun change() {
                data[layoutPosition] = data[layoutPosition].let {
                    val newData = generateItem()
                    it.someText = newData.someText
                    it.someDescription = newData.someDescription
                    it
                }
                notifyItemChanged(layoutPosition)
            }
        }

        companion object {
            private const val TYPE_TASK = 0
            private const val TYPE_ELSE = 1
        }
    }
}
