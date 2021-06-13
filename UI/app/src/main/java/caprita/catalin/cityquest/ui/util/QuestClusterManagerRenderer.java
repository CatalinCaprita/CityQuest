package caprita.catalin.cityquest.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.graphics.drawable.shapes.Shape;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import javax.inject.Inject;
import javax.inject.Singleton;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.models.maps.QuestTypeMarker;

public class QuestClusterManagerRenderer extends DefaultClusterRenderer<QuestTypeMarker> {
    private final Context context;
    private final int markerWidth;
    private final int markerHeight;

    public QuestClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<QuestTypeMarker> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
        markerWidth = ((int)context.getResources().getDimension(R.dimen.marker_image_size));
        markerHeight = ((int)context.getResources().getDimension(R.dimen.marker_image_size));
    }

    @Override
    protected void onBeforeClusterItemRendered(QuestTypeMarker item, MarkerOptions markerOptions) {
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), item.getImageResId());
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, markerWidth, markerHeight, false);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
    }

    @Override
    protected void onClusterItemRendered(QuestTypeMarker clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
//        getMarker(clusterItem).showInfoWindow();
    }

    /*
            Always return false as we are not particularly interested in clustering multiple markers
        * together*/
    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return false;
    }
}
