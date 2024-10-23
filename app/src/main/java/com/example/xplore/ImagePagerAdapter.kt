package com.example.xplore

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class ImagePagerAdapter(private val context: Context, private val imageUrls: List<String>) : PagerAdapter() {

    override fun getCount(): Int = imageUrls.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_image, container, false)
        val imageView = view.findViewById<ImageView>(R.id.imageView)

        // Create a GradientDrawable for the border
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadius = 25f  // Adjust the radius as needed (in pixels)
        shape.setStroke(2, Color.BLACK) // Add a red border (optional)

        // Apply the GradientDrawable to the ImageView's background
        imageView.background = shape

        Glide.with(context).load(imageUrls[position])
            .apply(
                RequestOptions().transform(
                    CenterCrop(),
                    RoundedCorners(45) // Set the corner radius in pixels
                ) // Optional placeholder image while loading
            )            .into(imageView)
        container.addView(view)
        return view
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
