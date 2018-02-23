package com.media.mobile.elin.wishwidemobile.Activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import com.media.mobile.elin.wishwidemobile.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by USER on 2017-11-21.
 */

public class ActivityBeacon  extends AppCompatActivity {
    private static final String TAG = "ActivityBeacon";
    private String[] tvStr = {"위치정보 가져오는 중","위치에 따른 데이터 요청 중"};
    LocationManager locationManager;
    boolean isGPSEnabled, isNetworkEnabled;
    LocationListener locationListener;
    int count = 0;
    int beacondown,beaconmax;

    TextView textview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);
        textview=(TextView)findViewById(R.id.textView);
        textview.setText(tvStr[0]);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d(TAG, "데이터 저장 경로: " + getFilesDir().getAbsolutePath());

        if(!isGPSEnabled)
        {
            //GPS가 꺼져있을 경우 setting 창으로 이동
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //위치가 잡히면 Web 서버에게 데이터 요청 => 위도 경도 데이터 줌
                count++;
                if(count>0)
                {
                    textview.setText(tvStr[1]);
                    new WebGET().execute(location.getLatitude(),location.getLongitude());
                    locationManager.removeUpdates(this);
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
                //GPS가 꺼져있을 경우 setting 창으로 이동
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        Log.d("main","isGPSEnabled="+isGPSEnabled);
        Log.d("main","isNetworkEnabled="+isNetworkEnabled);
        //locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,  1000,  10, this);
        //count = 0;
/*
        String locationProvider = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        if (lastKnownLocation != null) {
            double lng = lastKnownLocation.getLatitude();
            double lat = lastKnownLocation.getLatitude();
            Log.d("Main", "longtitude=" + lng + ", latitude=" + lat);
        }

        CentralManager centralManager = CentralManager.getInstance();
        centralManager.init(getApplicationContext());
        centralManager.setPeripheralScanListener(new PeripheralScanListener() {
            @Override
            public void onPeripheralScan(Central central, Peripheral peripheral) {

            }
        });

        centralManager.startScanning();
        */
    }

    @Override
    protected void onPause() {
        super.onPause();
        count = 0;
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        count =0;
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10, locationListener);

    }

    public class WebGET extends AsyncTask<Double,Void,String>
    {
        @Override
        protected String doInBackground(Double... params) {
            double minla = params[0]-1,
                    maxla = params[0]+1,
                    minlo = params[1]-1,
                    maxlo= params[1]+1;

            Log.d(TAG, "위도: " + params[0]);
            Log.d(TAG, "최소 위도: " + minla);
            Log.d(TAG, "최대 위도: " + maxla);
            Log.d(TAG, "경도: " + params[1]);
            Log.d(TAG, "최소 경도: " + minlo);
            Log.d(TAG, "최대 경도: " + maxlo);

            HttpURLConnection urlConn = null;
            StringBuffer sbParams = new StringBuffer();

            //최소, 최대 경도와 위도를 Web서버에 get 방식으로 줄 params 생성
            sbParams.append("minla").append("=").append(minla).append("&");
            sbParams.append("maxla").append("=").append(maxla).append("&");
            sbParams.append("minlo").append("=").append(minlo).append("&");
            sbParams.append("maxlo").append("=").append(maxlo);

            try {
                URL url = new URL("http://192.168.0.23:3000/databeacon");
                urlConn = (HttpURLConnection)url.openConnection();

                urlConn.setRequestMethod("POST");
                urlConn.setRequestProperty("Accept-Charset", "UTF-8");

                String strParams = sbParams.toString();
                OutputStream os = urlConn.getOutputStream();
                os.write(strParams.getBytes("UTF-8"));
                os.flush();
                os.close();
                if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                    return null;

                // [2-4]. 읽어온 결과물 리턴.
                // 요청한 URL의 출력물을 BufferedReader로 받는다.
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

                // 출력물의 라인과 그 합에 대한 변수.
                String line;
                String page = "";

                // 라인을 받아와 합친다.
                while ((line = reader.readLine()) != null){
                    page += line;
                }

                Log.d(TAG, "비콘 리스트: " + page);
                return page;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null){
                Log.e("result",s);
                try {
                    //현재 위치 근처의 비콘 리스트를 받아 txt 파일에 저장(json 형식)
                    FileOutputStream fos = new FileOutputStream(getFilesDir().getAbsolutePath()+"/Beacon.txt");
                    fos.write(s.getBytes());
                    fos.close();

                    //json 파싱
                    JSONArray ja = new JSONArray(s) ;
                    beaconmax = ja.length();
                    beacondown=0;
                    for(int i=0; i<ja.length();i++)
                    {
                        JSONObject jo = ja.getJSONObject(i);
                        new WEBMarker().execute(jo.getString("ww_beacon_macAddress"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class WEBMarker extends AsyncTask<String,Void,String>
    {
        String macAddress;
        @Override
        protected String doInBackground(String... params) {
            //비콘 리스트에서 받은 mac address로 marker 정보를 가져옴
            macAddress = params[0];
            HttpURLConnection urlConn = null;
            StringBuffer sbParams = new StringBuffer();
            Log.d(TAG, "macAddress 확인 : " + macAddress);

            sbParams.append("macAddress").append("=").append(macAddress);

            try {
                URL url = new URL("http://192.168.0.23:3000/datamarker");
                urlConn = (HttpURLConnection)url.openConnection();

                urlConn.setRequestMethod("POST");
                urlConn.setRequestProperty("Accept-Charset", "UTF-8");

                String strParams = sbParams.toString();
                OutputStream os = urlConn.getOutputStream();
                os.write(strParams.getBytes("UTF-8"));
                os.flush();
                os.close();
                if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                    return null;

                // [2-4]. 읽어온 결과물 리턴.
                // 요청한 URL의 출력물을 BufferedReader로 받는다.
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

                // 출력물의 라인과 그 합에 대한 변수.
                String line;
                String page = "";

                // 라인을 받아와 합친다.
                while ((line = reader.readLine()) != null){
                    page += line;
                }

                Log.d(TAG, "맥주소 리스트: " + page);
                return page;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null){
                Log.e("result",s);

                //mac address 리스트를 받아 txt 파일에 저장
                try {
                    FileOutputStream fos = new FileOutputStream(getFilesDir().getAbsolutePath()+macAddress+".txt");
                    fos.write(s.getBytes());
                    fos.close();
                    beacondown++;
                    //잡힌 비콘이 있으면 AR Activity로 이동
                    if(beacondown>=beaconmax)
                    {
                        Log.d(TAG, "beacondown: " + beacondown);
                        Log.d(TAG, "beaconmax: " + beaconmax);
                        m_Handler.obtainMessage().sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //AR로 이동!
    Handler m_Handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
//            Intent intent = new Intent(ActivityBeacon.this,VideoPlayback.class);
//            startActivity(intent);

            Intent intent = new Intent(ActivityBeacon.this, MainActivity.class);
            intent.putExtra("loadUrl", "http://220.230.113.159:8080/");
            startActivity(intent);
        }
    };
}
