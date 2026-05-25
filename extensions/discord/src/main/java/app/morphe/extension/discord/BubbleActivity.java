package app.morphe.extension.discord;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class BubbleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView text = new TextView(this);
        text.setText("Hello from Morphe!");
        setContentView(text);
    }
}