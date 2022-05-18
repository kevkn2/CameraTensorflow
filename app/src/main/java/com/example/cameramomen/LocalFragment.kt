package com.example.cameramomen

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cameramomen.DataBase.DataBaseHandler
import com.example.cameramomen.DataBase.LocalDataBaseAdapter
import com.example.cameramomen.DataBase.LocalResponse
import java.util.*

class LocalFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    private var myDatabase: DataBaseHandler? = null
    private var db: SQLiteDatabase? = null
    private lateinit var singleRowArrayList: ArrayList<LocalResponse>
    private var singleRow: LocalResponse? = null
    var image: String? = null
    var uid = 0
    var cursor: Cursor? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.fragment_local,
            container,
            false)
        recyclerView = view.findViewById(R.id.recyclerview)
        myDatabase = DataBaseHandler(requireContext())
        db = myDatabase!!.writableDatabase
        setData()
        return view
    }

    private fun setData() {
        db = myDatabase!!.writableDatabase
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        recyclerView!!.layoutManager = layoutManager
        singleRowArrayList = ArrayList()
        val columns = arrayOf(DataBaseHandler.KEY_ID, DataBaseHandler.KEY_IMG_URL)
        cursor = db?.query(
            DataBaseHandler.TABLE_NAME,
            columns,
            null,
            null,
            null,
            null,
            null)!!
        while (cursor?.moveToNext()!!) {
            val index1: Int = cursor?.getColumnIndex(DataBaseHandler.KEY_ID)!!
            val index2: Int = cursor?.getColumnIndex(DataBaseHandler.KEY_IMG_URL)!!
            uid = cursor?.getInt(index1)!!
            image = cursor?.getString(index2)!!
            singleRow = LocalResponse(image, uid)
            singleRowArrayList.add(singleRow!!)
        }
        if (singleRowArrayList.size === 0) {
            recyclerView!!.visibility = View.GONE
        } else {
            val localDataBaseResponse =
                LocalDataBaseAdapter(requireContext(), singleRowArrayList, db!!, myDatabase!!)
            recyclerView!!.adapter = localDataBaseResponse
        }
    }
}