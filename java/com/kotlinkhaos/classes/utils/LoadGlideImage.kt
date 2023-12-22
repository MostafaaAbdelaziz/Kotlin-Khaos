package com.kotlinkhaos.classes.utils

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.kotlinkhaos.R

/**
 * Extension function for ImageView to load an image from a URL using Glide.
 * The function applies default caching strategy and placeholders for loading and error states.
 *
 * @param url The URL of the image to be loaded into the ImageView.
 */
fun ImageView.loadImage(url: String) {
    Glide.with(this.context)
        .load(url)
        .apply(
            RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.account_circle_gray_24dp)
                .error(R.drawable.account_circle_gray_24dp)
        )
        .into(this)
}

/**
 * Extension function for ImageView to load an image from a URL using Glide with a ProgressBar.
 * Shows the ProgressBar while the image is loading and hides it once loading is complete or failed.
 * The function applies default caching strategy and placeholders for loading and error states.
 *
 * @param url The URL of the image to be loaded into the ImageView.
 * @param progressBar The ProgressBar to be shown while the image is loading.
 */
fun ImageView.loadImage(url: String, progressBar: ProgressBar) {
    progressBar.visibility = View.VISIBLE

    Glide.with(this.context)
        .load(url)
        .apply(
            RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.account_circle_gray_24dp)
                .error(R.drawable.account_circle_gray_24dp)
        )
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                progressBar.visibility = View.GONE
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                progressBar.visibility = View.GONE
                return false
            }
        })
        .into(this)
}
