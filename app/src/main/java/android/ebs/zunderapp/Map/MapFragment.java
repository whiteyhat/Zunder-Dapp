package android.ebs.zunderapp.Map;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.ebs.zunderapp.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private View mView;
    private FloatingActionButton btnMap, btnAdd, btnSearch;
    private Animation open, close, rotationright, rotationleft;
    private boolean isOpen = false, isSearch = false;
    private AppBarLayout search;
    private SearchView searchView;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        return mView;


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Instantiate elemnts from XML
        linkElements();

        //Action listeners for buttons
        actionListeners();


    }

    private void actionListeners() {
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen) {
                    btnMap.startAnimation(rotationleft);
                    btnAdd.startAnimation(close);
                    btnSearch.startAnimation(close);

                    btnSearch.setClickable(false);
                    btnAdd.setClickable(false);
                    isOpen = false;
                }
                if (isSearch) {
                    search.setVisibility(View.GONE);
                    isSearch = false;
                } else {

                    btnMap.startAnimation(rotationright);
                    btnAdd.startAnimation(open);
                    btnSearch.startAnimation(open);

                    btnSearch.setClickable(true);
                    btnAdd.setClickable(true);
                    btnMap.setClickable(true);
                    isOpen = true;
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSearch) {
                    search.setVisibility(View.GONE);
                    isSearch = false;
                } else {
                    search.setVisibility(View.VISIBLE);
                    searchView.setIconified(false);
                    searchView.setFocusable(true);
                    searchView.requestFocusFromTouch();
                    isSearch = true;
                }
            }
        });
    }

    private void linkElements() {
        btnMap = (FloatingActionButton) getView().findViewById(R.id.btnMap);
        btnAdd = (FloatingActionButton) getView().findViewById(R.id.btnAdd);
        btnSearch = (FloatingActionButton) getView().findViewById(R.id.btnSearch);
        open = AnimationUtils.loadAnimation(getContext(), R.anim.open);
        close = AnimationUtils.loadAnimation(getContext(), R.anim.close);
        rotationright = AnimationUtils.loadAnimation(getContext(), R.anim.rotation);
        rotationleft = AnimationUtils.loadAnimation(getContext(), R.anim.rotationleft);
        search = (AppBarLayout) getView().findViewById(R.id.search);
        searchView = (SearchView) getView().findViewById(R.id.searchit);
        mMapView = (MapView) mView.findViewById(R.id.mapa);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }


    private void createAlert(String title, String message) {
        ImageView image = new ImageView(getContext());

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        image.setImageBitmap(bitmap);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getContext())
                        .setTitle(title)
                        .setMessage(message)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        })
                        .setPositiveButton("View Profile", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getContext(), CompanyProfile.class);
                                startActivity(intent);
                            }
                        })

                        .setView(image);
        builder.create().show();

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());

        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        googleMap.addMarker(new MarkerOptions().position(new LatLng(52.415834, -4.065656))
                .title("European Blockchain Solutions").visible(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo))
                .snippet("The place where the Blockchain revolution for IoT was born."));
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                createAlert("European Blockchain Solutions", "Research center " +
                        "focused on providing the next generation of digital revolution" +
                        " based on Peer-to-Peer protocols");
                return false;
            }
        });


        CameraPosition ebs = CameraPosition.builder().target(new LatLng(52.415834, -4.065656)).zoom(16).bearing(0).tilt(45).build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(ebs));


//          ADD A NEW MARKER WHEN TOUCHING

//        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng point) {
//                MarkerOptions marker = new MarkerOptions().position(
//                        new LatLng(point.latitude, point.longitude)).title("New Marker");
//                googleMap.addMarker(marker);
//            }
//        });
    }
}