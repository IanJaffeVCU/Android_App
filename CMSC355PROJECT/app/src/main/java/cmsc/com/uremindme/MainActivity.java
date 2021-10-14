package cmsc.com.uremindme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public void hitMe(View view){

        Log.i("info", "Welcome to CMSC355 Project");

        Toast.makeText(this, "Welcome Parisy, Ian, Ashok, and MySelf", Toast.LENGTH_LONG).show();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}