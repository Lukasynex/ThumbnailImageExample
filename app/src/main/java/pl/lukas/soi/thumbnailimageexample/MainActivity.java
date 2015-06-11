package pl.lukas.soi.thumbnailimageexample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.CopyOnWriteArrayList;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = (TextView) findViewById(R.id.addres_info);
        final EditText edit = (EditText) findViewById(R.id.edit);

        edit.setText("zuza");
        Button setter = (Button) findViewById(R.id.setter);


        final Activity activity = this;
        setter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String name = edit.getText().toString();
                            String infos = new GetContactImage(activity).getAdressInfo(name);
                            TextView textView = (TextView)findViewById(R.id.addres_info);
                            textView.setText(infos);

                            Bitmap imag = new GetContactImage(activity).getThumbnail(name);
                            ImageView m = (ImageView) findViewById(R.id.image);
                            m.setImageBitmap(imag);

                        } catch (NullPointerException ex) {
                            Log.e(TAG, "error with setter");
                            ex.printStackTrace();
                        } finally {

                        //    textView.setText("finally: Brak wyników");
                        }
                    }
                });
            }
        });
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
}
