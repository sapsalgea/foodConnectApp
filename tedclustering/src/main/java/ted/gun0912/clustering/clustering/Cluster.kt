package ted.gun0912.clustering.clustering

import ted.gun0912.clustering.geometry.TedLatLng


interface Cluster<T : TedClusterItem> {
    val position: TedLatLng

    val items: Collection<T>

    val size: Int


    fun clusterData(): Boolean {
        var check = false
        for (index in items) {
            if(index.check){
            check = true}
        }
        return check
    }


}