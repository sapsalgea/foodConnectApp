package ted.gun0912.clustering.naver

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.MarkerIcons
import ted.gun0912.clustering.TedMarker
import ted.gun0912.clustering.geometry.TedLatLng
import kotlin.properties.Delegates


class TedNaverMarker(override var marker: Marker, val context: Context, override var check: Boolean =false) : TedMarker<OverlayImage> {

    override fun setVisible(visible: Boolean) {
        marker.isVisible = visible


    }




    override var position: TedLatLng
        get() = TedLatLng(marker.position.latitude, marker.position.longitude)
        set(value) {
            marker.position = LatLng(value.latitude, value.longitude)
        }

    override fun setImageDescriptor(imageDescriptor: OverlayImage) {
        marker.icon = imageDescriptor
    }

    override fun fromBitmap(bitmap: Bitmap): OverlayImage = OverlayImage.fromBitmap(bitmap)

}