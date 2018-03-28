package com.example.rumpilstilstkin.kotlintest.screens.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rumpilstilstkin.kotlintest.R
import com.example.rumpilstilstkin.kotlintest.adapters.AwesomeLayoutManager
import com.example.rumpilstilstkin.kotlintest.adapters.ExampleAdapter
import com.example.rumpilstilstkin.kotlintest.models.ExampleModel
import kotlinx.android.synthetic.main.fragment_list.listView


///////////////////////////////////////////////////////////////////////////
// Example List Fragment
///////////////////////////////////////////////////////////////////////////

class ExampleListFragment: Fragment(){

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle
    ///////////////////////////////////////////////////////////////////////////

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
            = inflater.inflate(R.layout.fragment_list, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView.layoutManager = AwesomeLayoutManager(context)
        val adapter = ExampleAdapter(context)
        listView.adapter = adapter
        adapter.setItems(getData())
    }

    private fun getData() : ArrayList<ExampleModel> {
        val res = ArrayList<ExampleModel>()

        res.add(ExampleModel("Red", Color.RED))
        res.add(ExampleModel("White", Color.WHITE))
        res.add(ExampleModel("Blue", Color.BLUE))
        res.add(ExampleModel("Gray", Color.LTGRAY))
        return res
    }

}
