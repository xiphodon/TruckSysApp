package com.sy.trucksysapp.utils;

import java.util.List;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveStep;

public class DrivingRouteOverlay extends RouteOverlay {
	private DrivePath drivePath;

	public DrivingRouteOverlay(Context context, AMap amap, DrivePath path,
			LatLonPoint start, LatLonPoint end) {
		super(context);
		this.mAMap = amap;
		this.drivePath = path;
		startPoint = AMapServicesUtil.convertToLatLng(start);
		endPoint = AMapServicesUtil.convertToLatLng(end);
	}

	/**
	 * 缁樺埗鑺傜偣鍜岀嚎锟�
	 */
	public void addToMap() {
		List<DriveStep> drivePaths = drivePath.getSteps();
		for (int i = 0; i < drivePaths.size(); i++) {
			DriveStep driveStep = drivePaths.get(i);
			LatLng latLng = AMapServicesUtil.convertToLatLng(driveStep
					.getPolyline().get(0));
			if (i < drivePaths.size() - 1) {
				if (i == 0) {
					Polyline oneLine = mAMap.addPolyline(new PolylineOptions()
							.add(startPoint, latLng).color(getDriveColor())
							.width(getBuslineWidth()));// 鎶婅捣濮嬬偣鍜岀锟�锟斤拷姝ヨ鐨勮捣鐐硅繛鎺ヨ捣锟�
					allPolyLines.add(oneLine);
				}
				LatLng latLngEnd = AMapServicesUtil.convertToLatLng(driveStep
						.getPolyline().get(driveStep.getPolyline().size() - 1));
				LatLng latLngStart = AMapServicesUtil
						.convertToLatLng(drivePaths.get(i + 1).getPolyline()
								.get(0));
				if (!(latLngEnd.equals(latLngStart))) {
					Polyline breakLine = mAMap
							.addPolyline(new PolylineOptions()
									.add(latLngEnd, latLngStart)
									.color(getDriveColor())
									.width(getBuslineWidth()));// 鎶婂墠锟�锟斤拷姝ヨ娈电殑缁堢偣鍜屽悗锟�锟斤拷姝ヨ娈电殑璧风偣杩炴帴璧锋潵
					allPolyLines.add(breakLine);
				}
			} else {
				LatLng latLng1 = AMapServicesUtil.convertToLatLng(driveStep
						.getPolyline().get(driveStep.getPolyline().size() - 1));
				Polyline endLine = mAMap.addPolyline(new PolylineOptions()
						.add(latLng1, endPoint).color(getDriveColor())
						.width(getBuslineWidth()));// 鎶婃渶缁堢偣鍜屾渶鍚庝竴涓琛岀殑缁堢偣杩炴帴璧锋潵
				allPolyLines.add(endLine);
			}

//			Marker driveMarker = mAMap.addMarker(new MarkerOptions()
//					.position(latLng)
//					.title("\u65B9\u5411:" + driveStep.getAction()
//							+ "\n\u9053\u8DEF:" + driveStep.getRoad())
//					.snippet(driveStep.getInstruction()).anchor(0.5f, 0.5f)
//					.icon(getDriveBitmapDescriptor()));
//			stationMarkers.add(driveMarker);
			Polyline driveLine = mAMap.addPolyline(new PolylineOptions()
					.addAll(AMapServicesUtil.convertArrList(driveStep
							.getPolyline())).color(getDriveColor())
					.width(getBuslineWidth()));
			allPolyLines.add(driveLine);
		}
		addStartAndEndMarker();
	}

	protected float getBuslineWidth() {
		return 10;
	}
}
