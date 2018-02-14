package perayuth.thanyanon.kku.ac.th.adbwifistatus;

import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkStatus();
            }
        });

        checkStatus();
    }

    protected void onResume() {
        super.onResume();
        checkStatus();
    }

    protected void checkStatus(){
        TextView tvStatus = (TextView) findViewById(R.id.tvStatus);

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ssid = wm.getConnectionInfo().getSSID().replace("\"", "");
        String ipaddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        if(ipaddress.equals("0.0.0.0")) {
            tvStatus.setText(Html.fromHtml("No wifi connection"));
        } else {
            try {
                Process p = Runtime.getRuntime().exec("getprop service.adb.tcp.port");
                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = new String();
                if ((line = input.readLine()) != null) {
                    if (!line.isEmpty()) {
                        tvStatus.setText(Html.fromHtml("SSID: " + ssid + "<br>Active: adb connect " + ipaddress + ":" + line));
                    } else {
                        tvStatus.setText(Html.fromHtml("SSID: " + ssid + "<br>Inactive: adb tcpip 5555"));
                    }
                }
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        swipeRefreshLayout.setRefreshing(false);
    }
}
