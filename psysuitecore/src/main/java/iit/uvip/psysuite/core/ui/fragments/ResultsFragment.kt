package iit.uvip.psysuite.core.ui.fragments

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import iit.uvip.psysuite.core.R
import kotlinx.android.synthetic.main.fragment_results_list.*
import kotlinx.android.synthetic.main.fragment_results_list.view.*
import org.albaspazio.core.filesystem.getFileNamesList

/**
 * A fragment representing a list of Items.
 */
class ResultsFragment : Fragment() {

    var relPath:String = Environment.DIRECTORY_DOWNLOADS
    var filesList:MutableList<ResultFileEntry> = mutableListOf()

    lateinit var listAdapter:ResultsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            relPath = it.getString("RES_RELPATH").toString()
        }

        getFileNamesList(relPath, listOf(".txt")).map{
            filesList.add(ResultFileEntry(it, false))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_results_list, container, false)
        // Set the adapter
        listAdapter             = ResultsRecyclerViewAdapter(filesList)
        view.list.adapter       = listAdapter
        view.list.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onResume() {
        super.onResume()

        bt_send_results.setOnClickListener {

        }

        swSelectAll.setOnCheckedChangeListener { _, b ->
            listAdapter.selectAll(b)
        }
    }
}