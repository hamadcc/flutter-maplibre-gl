package org.maplibre.maplibregl

import android.content.Context
import org.maplibre.android.location.LocationComponent
import org.maplibre.android.location.engine.LocationEngine
import org.maplibre.android.location.engine.LocationEngineDefault.getDefaultLocationEngine
import org.maplibre.android.location.engine.LocationEngineProxy
import org.maplibre.android.location.engine.LocationEngineRequest

class LocationEngineFactory {

    private var locationEngineRequest: LocationEngineRequest? = null

    fun getLocationEngine(context: Context): LocationEngine {
        if (locationEngineRequest?.priority == LocationEngineRequest.PRIORITY_HIGH_ACCURACY) {
            // Always use GPS_PROVIDER (never GMS Fused) on high accuracy so external USB GPS
            // can update the location puck.
            return LocationEngineProxy(MapLibreGPSLocationEngine(context))
        }
        return getDefaultLocationEngine(context)
    }

    fun initLocationComponent(
        context: Context,
        locationComponent: LocationComponent?,
        locationEngineRequest: LocationEngineRequest?
    ) {
        if (locationEngineRequest != null) {
            this.locationEngineRequest = locationEngineRequest
        }
        // Only swap when activated — setLocationEngine can throw otherwise.
        // enableLocationComponent() still picks up the stored request via getLocationEngine().
        if (locationComponent != null && locationComponent.isLocationComponentActivated) {
            locationComponent.locationEngine = getLocationEngine(context)
            this.locationEngineRequest?.let { request ->
                locationComponent.locationEngineRequest = request
            }
        }
    }
}
