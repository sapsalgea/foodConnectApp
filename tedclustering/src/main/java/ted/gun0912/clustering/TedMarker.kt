package ted.gun0912.clustering

import android.graphics.Bitmap

import com.naver.maps.map.overlay.Marker

import ted.gun0912.clustering.geometry.TedLatLng


interface TedMarker<ImageDescriptor> {

    fun setVisible(visible: Boolean)

    var position: TedLatLng

    var marker: Marker

    var check: Boolean

    fun setImageDescriptor(imageDescriptor: ImageDescriptor)

    fun fromBitmap(bitmap: Bitmap): ImageDescriptor
}