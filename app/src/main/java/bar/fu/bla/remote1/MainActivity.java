package bar.fu.bla.remote1;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.SyncStateContract;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.math.*;
import java.security.*;
import java.util.Calendar;


public class MainActivity extends Activity {


        private InetAddress address;
        private Calendar rightNow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rightNow = Calendar.getInstance();
        try{
          address=getBroadcastAddress();
          TextView textView = (TextView) findViewById(R.id.textView);
          textView.setText(address.toString());
        } catch (IOException e) {
            address=InetAddress.getLoopbackAddress();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


 // NETZWERKFUU

    //Senden eines UDP Packetes
    public void sendBroadcast(String messageStr) {
        // Hack Prevent crash (sending should be done using an async task)
        StrictMode.ThreadPolicy policy = new   StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            //Open a random port to send the package
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            messageStr=encryptthis(messageStr);
            byte[] sendData = messageStr.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, 5555);
            //DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,address, 5555);
            socket.send(sendPacket);
            System.out.println(getClass().getName() + "Broadcast packet sent to: " + address);
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    //Herrausfinden der WIFI-Broadcastadresse.
    private InetAddress getBroadcastAddress() throws IOException {
        WifiManager myWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        DhcpInfo myDhcpInfo = myWifiManager.getDhcpInfo();
        if (myDhcpInfo == null) {
            System.out.println("Could not get broadcast address");
            return null;
        }
        int broadcast = (myDhcpInfo.ipAddress & myDhcpInfo.netmask)
                | ~myDhcpInfo.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    //Hshen des Strings + Salting
    public String encryptthis(String msg){
        int min = rightNow.get(Calendar.MINUTE);
        String toEnc = msg+Integer.toString(min); // Value to encrypt
        try {
            MessageDigest mdEnc = MessageDigest.getInstance("MD5"); // Encryption algorithm
            mdEnc.update(toEnc.getBytes(), 0, toEnc.length());
            String md5 = new BigInteger(1, mdEnc.digest()).toString(16) ;// Encrypted string
            System.out.println(md5);
            return md5;
        }catch (NoSuchAlgorithmException e){
            System.out.println("DER FICKER HAT GEFICKT !");
            return "FICKERFAIL";
        }
    }

 //App Funktionen


    //Klicken des Buttons LAST
    public void LASTbuttonOnClick(View v) {
        sendBroadcast("LAST");
    }

    //Klicken des Buttons LAST
    public void NEXTbuttonOnClick(View v) {
        sendBroadcast("NEXT");
    }

    //Klicken des Buttons PAUSE
    public void PASUEbuttonOnClick(View v) {
        sendBroadcast("PASUE");
    }

}
