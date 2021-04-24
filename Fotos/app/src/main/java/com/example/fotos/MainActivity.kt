package com.example.fotos

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var imgUri: Uri? = null

    companion object {
        private const val PERMISSION_CODE_PICK = 1000
        private const val IMAGE_PICK_CODE = 1001
        private const val PERMISSION_CODE_TAKE = 2000
        private const val IMAGE_TAKE_CODE = 2002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pick_image.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {

                    val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permission, PERMISSION_CODE_PICK)
                }
                else {
                    pickImageFromGalery()
                }
            }
            else {
                pickImageFromGalery()
            }
        }

        take_image.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED) {

                    val permission = arrayOf(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)

                    requestPermissions(permission, PERMISSION_CODE_TAKE)
                }
                else {
                    takeImageFromCamera()
                }
            }
            else {
                takeImageFromCamera()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {

            PERMISSION_CODE_PICK -> {
                if (grantResults.size > 0 && grantResults[0] === PackageManager.PERMISSION_GRANTED) {

                    pickImageFromGalery()
                }
                else {
                    Toast
                        .makeText(this, "Permissão de galeria negada", Toast.LENGTH_LONG)
                        .show()
                }
            }

            PERMISSION_CODE_TAKE -> {
                if (grantResults.size > 1 &&
                        grantResults[0] === PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] === PackageManager.PERMISSION_GRANTED) {

                    takeImageFromCamera()
                }
                else {
                    Toast
                        .makeText(this, "Permissão de câmera negado", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            image_view.setImageURI(data?.data)
        }
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_TAKE_CODE) {
            image_view.setImageURI(imgUri)
        }
    }

    private fun pickImageFromGalery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun takeImageFromCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Nova Imagem")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Imagem capturada da câmera")

        imgUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)

        startActivityForResult(cameraIntent, IMAGE_TAKE_CODE)
    }
}