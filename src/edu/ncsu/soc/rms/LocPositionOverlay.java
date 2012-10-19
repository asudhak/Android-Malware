package edu.ncsu.soc.rms;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.location.Location;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class LocPositionOverlay extends Overlay {
  Context context;
  
  private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

  public LocPositionOverlay(Context _context) {
    this.context = _context;
  }

  /** Get the position location */
  public Location getLocation() {
    return location;
  }

  /** Set the position location */
  public void setLocation(Location location) {
    this.location = location;
  }

  
  
  Location location;

  private final int mRadius = 5;

  @Override
  public void draw(Canvas canvas, MapView mapView, boolean shadow) {
    Projection projection = mapView.getProjection();

    if (location == null)
      return;

    if (shadow == false) {
      // Get the current location
      Double latitude = location.getLatitude() * 1E6;
      Double longitude = location.getLongitude() * 1E6;
      GeoPoint geoPoint = new GeoPoint(latitude.intValue(), longitude.intValue());

      // Convert the location to screen pixels
      Point point = new Point();
      projection.toPixels(geoPoint, point);

      RectF oval = new RectF(point.x - mRadius, point.y - mRadius, point.x + mRadius, point.y
          + mRadius);

      // Setup the paint
      Paint paint = new Paint();
      paint.setARGB(255, 255, 255, 255);
      paint.setAntiAlias(true);
      paint.setFakeBoldText(true);

      Paint backPaint = new Paint();
      backPaint.setARGB(180, 50, 50, 50);
      backPaint.setAntiAlias(true);

      RectF backRect = new RectF(point.x + 2 + mRadius, point.y - 3 * mRadius, point.x + 65,
          point.y + mRadius);

      // Draw the marker
      canvas.drawOval(oval, paint);
      canvas.drawRoundRect(backRect, 5, 5, backPaint);
      canvas.drawText("Here I Am", point.x + 2 * mRadius, point.y, paint);
      
       	
      Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

      circlePaint.setColor(0xff0000ff);
//      circlePaint.setColor(BLUE);
      
      circlePaint.setStyle(Style.FILL_AND_STROKE);
      canvas.drawCircle((float)point.x, (float)point.y, 50, backPaint);

//      circlePaint.setColor(0x99004500);
//      circlePaint.setStyle(Style.STROKE);
//      canvas.drawCircle((float)point.x, (float)point.y, 60, backPaint);

      
      //
         }
    super.draw(canvas, mapView, shadow);
    
    if(shadow==false){
    	Double latitude = location.getLatitude() * 1E6;
        Double longitude = location.getLongitude() * 1E6;
    	GeoPoint globalGeoPoint = new GeoPoint(latitude.intValue(), longitude.intValue());
        Point pt = new Point();
        projection.toPixels(globalGeoPoint,pt);

        GeoPoint newGeos = new GeoPoint(latitude.intValue()+(100),longitude.intValue()); // adjust your radius accordingly
        Point pt2 = new Point();
        projection.toPixels(newGeos,pt2);
        float circleRadius = Math.abs(pt2.y-pt.y);

        Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        circlePaint.setColor(0x30000000);
        circlePaint.setStyle(Style.FILL_AND_STROKE);
        canvas.drawCircle((float)pt.x, (float)pt.y, circleRadius, circlePaint);

        circlePaint.setColor(0x99000000);
        circlePaint.setStyle(Style.STROKE);
        canvas.drawCircle((float)pt.x, (float)pt.y, circleRadius, circlePaint);


        super.draw(canvas,mapView,shadow);
    }
  }
  
  
  
  
  @Override
  public boolean onTap(GeoPoint point, MapView mapView) {
    
   LocationRepresenter.show_custom_dialog(context, point);
    return true;
  }
  
  public boolean onTap(int index)
  {
	  OverlayItem item = mOverlays.get(index);
	  AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	  dialog.setTitle(item.getTitle());
	  dialog.setMessage(item.getSnippet());
	  dialog.show();
	  return true;
  }
  
  
  
}

