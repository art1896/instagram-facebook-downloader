package com.example.inst

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.ref.WeakReference

@Suppress("DEPRECATION")
class SaveImageHelper(val context: Context, contentResolver: ContentResolver, val name: String, val desc: String) : Target{

    val contentResolverWeakReference: WeakReference<ContentResolver>

    init {
        this.contentResolverWeakReference = WeakReference<ContentResolver>(contentResolver)
    }


    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
    }

    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
    }

    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
        val r = contentResolverWeakReference.get()
        if (r != null)
            MediaStore.Images.Media.insertImage(r, bitmap, name, desc)
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}