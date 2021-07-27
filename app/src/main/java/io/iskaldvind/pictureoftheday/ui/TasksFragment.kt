package io.iskaldvind.pictureoftheday.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import io.iskaldvind.pictureoftheday.R


class TasksFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = requireActivity() as MainActivity

        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        recycler.adapter = parent.adapter

        val addButton = view.findViewById<MaterialButton>(R.id.task_make)
        addButton.setOnClickListener {
            parent.adapter.appendItem()
        }
    }
}