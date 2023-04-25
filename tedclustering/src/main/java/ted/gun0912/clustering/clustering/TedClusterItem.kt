package ted.gun0912.clustering.clustering

import ted.gun0912.clustering.geometry.TedLatLng


interface TedClusterItem {

    val check: Boolean
    val placeName : String
    fun getTedLatLng(): TedLatLng


}