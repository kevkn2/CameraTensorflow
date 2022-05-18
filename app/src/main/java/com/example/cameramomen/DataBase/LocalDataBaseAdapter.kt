package com.example.cameramomen.DataBase

import android.app.AlertDialog
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cameramomen.Classifier
import com.example.cameramomen.R
import com.example.cameramomen.R.layout
import java.security.AccessController.getContext
import java.util.*

class LocalDataBaseAdapter(
        context: Context,
        singleRowArrayList: ArrayList<LocalResponse>,
        db: SQLiteDatabase,
        myDatabase: DataBaseHandler,
) :
    RecyclerView.Adapter<LocalDataBaseAdapter.MyViewHolder>() {
    var context: Context = context
    var singleRowArrayList: ArrayList<LocalResponse> = singleRowArrayList
    var db: SQLiteDatabase = db
    var myDatabase: DataBaseHandler = myDatabase

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var newsImage: ImageView = itemView.findViewById(R.id.newsImage) as ImageView
        var delete: ImageView = itemView.findViewById(R.id.delete) as ImageView
        var mInputSize = 224
        val mModelPath = "convert_model.tflite"
        val mLabelPath = "labels.txt"
        var classifier: Classifier = Classifier(context.assets, mModelPath, mLabelPath, mInputSize)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val view: View = LayoutInflater.from(context).inflate(
            layout.local_database_items,
            null)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.newsImage.setImageBitmap(getBitmapFromEncodedString(singleRowArrayList[position].image.toString()))
        holder.newsImage.setOnClickListener{ analyzeImage(holder.newsImage, holder.classifier) }
        holder.delete.setOnClickListener { deletedata(position, singleRowArrayList) }
    }

    override fun getItemCount(): Int = singleRowArrayList.size

    private fun deletedata(position: Int, singleRowArrayList: ArrayList<LocalResponse>) {
        AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_launcher_foreground)
            .setTitle("Delete result")
            .setMessage("Are you sure you want delete this result?")
            .setPositiveButton("Yes") { _, _ ->
                myDatabase.deleteEntry(singleRowArrayList[position].uid)
                singleRowArrayList.removeAt(position)
                notifyItemRemoved(position)
                notifyDataSetChanged()
                myDatabase.close()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun analyzeImage(view: ImageView, classifier: Classifier) {
        val bitmap = (view.drawable as BitmapDrawable).bitmap
        val result = classifier.recognizeImage(bitmap)
        Toast.makeText(context, result.get(0).title, Toast.LENGTH_SHORT).show()
    }

    private fun getBitmapFromEncodedString(encodedString: String): Bitmap {
        val arr: ByteArray = Base64.decode(encodedString, Base64.URL_SAFE)
        return BitmapFactory.decodeByteArray(arr, 0, arr.size)
    }
}