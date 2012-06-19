
package br.com.utfpr.mapas;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * 
 * @author Gabriel Centenaro
 * @author Lucas Del Castanhel Dias
 *
 */

public class MapsTestActivity extends MapActivity implements LocationListener{
    /** Called when the activity is first created. */
	MapController mapController;
	MapView mapView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        //Seta a posição inicial para Curitiba/Pr
        mapController = mapView.getController();
        mapController.setCenter(new GeoPoint(-25439356,-49268816));
        mapController.zoomToSpan(5000, 5000);
        //Adiciona o listener para tratar o geo fix
        LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener mlocListener = this;
        mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        //traça uma rota para ver se funciona
        ArrayList<GeoPoint> all_geo_points = GeoPointsUtils.getDirections(10.154929, 76.390316, 10.015861, 76.341867);
        GeoPoint moveTo = all_geo_points.get(0);
        mapController.animateTo(moveTo);
        mapController.setZoom(12);
        mapView.getOverlays().add(new MyOverlay(all_geo_points));

    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

	@Override
	public void onLocationChanged(Location location) {
		// traça a rota entre o ponto fornecido e a UTFPR
//		ArrayList<GeoPoint> all_geo_points = GeoPointsUtils.getDirections(location.getLatitude(), location.getLongitude(),
//				-25.438874, -49.269706);
//        GeoPoint moveTo = all_geo_points.get(0);
//        mapController.animateTo(moveTo);
//        mapController.setZoom(12);
//        mapView.getOverlays().add(new MyOverlay(all_geo_points));
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
}