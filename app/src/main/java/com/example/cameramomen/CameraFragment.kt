package com.example.cameramomen

import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.example.cameramomen.DataBase.DataBaseHandler
import java.io.ByteArrayOutputStream

class CameraFragment : Fragment() {
    var text: TextView? = null
    var text1: TextView? = null
    var photo: String? = null
    var databaseHandler: DataBaseHandler? = null
    private var db: SQLiteDatabase? = null
    var theImage: Bitmap? = null
    @RequiresApi(Build.VERSION_CODES.M)
    @Nullable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(
            R.layout.fragment_camera,
            container,
            false
        )
        text = view.findViewById(R.id.text2)
        text1 = view.findViewById(R.id.text1)
        databaseHandler = DataBaseHandler(requireContext())
        text.run {
            this?.setOnClickListener {
                if (requireActivity().checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(CAMERA), MY_CAMERA_PERMISSION_CODE)
                } else {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, CAMERA_REQUEST)
                }
            }
        }

        text1.run {
            this?.setOnClickListener {
                (activity as MainActivity?)!!.loadFragment(
                    LocalFragment(),
                    true
                )
            }
        }
        return view
    }

    private fun setDataToDataBase() {
        db = databaseHandler?.writableDatabase
        val cv = ContentValues()
        cv.put(DataBaseHandler.KEY_IMG_URL, photo)
        val id = db!!.insert(DataBaseHandler.TABLE_NAME, null, cv)
        if (id < 0) {
            Toast.makeText(
                    context,
                "Something went wrong. Please try again later...",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(context, "Add successful", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Reuqesting for premissons
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "camera permission granted", Toast.LENGTH_LONG).show()
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                ActivityCompat.startActivityForResult(
                    requireActivity(),
                    cameraIntent, CAMERA_REQUEST,
                    ActivityOptionsCompat.makeTaskLaunchBehind().toBundle())
            } else {
                Toast.makeText(activity, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            theImage = data?.extras!!["data"] as Bitmap?
            photo = getEncodedString(theImage)
            setDataToDataBase()
        }
    }

    private fun getEncodedString(bitmap: Bitmap?): String {
        val os = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, os)
        val imageArr = os.toByteArray()
        return Base64.encodeToString(imageArr, Base64.URL_SAFE)
    }

    companion object {
        private const val CAMERA_REQUEST = 1888
        private const val MY_CAMERA_PERMISSION_CODE = 100
    }
}