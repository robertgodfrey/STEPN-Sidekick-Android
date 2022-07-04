package stepn.sidekick.stepnsidekick;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Information about the app.
 *
 * @author Bob Godfrey
 * @version 1.3.0
 *
 */

public class About extends AppCompatActivity {

    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        Button emailButton = findViewById(R.id.emailButton);
        Button solButton = findViewById(R.id.solButton);
        Button bnbButton = findViewById(R.id.bnbButton);
        TextView emailTextView = findViewById(R.id.contactEmailTextView);

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData clip = ClipData.newPlainText("email", getString(R.string.contact_email));
                clipboard.setPrimaryClip(clip);

                Toast.makeText(About.this, getString(R.string.email_copied), Toast.LENGTH_SHORT).show();
            }
        });

        emailButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        emailTextView.setTextColor(ContextCompat.getColor(About.this, R.color.energy_blue_darker));
                        break;
                    case MotionEvent.ACTION_UP:
                        emailTextView.setTextColor(ContextCompat.getColor(About.this, R.color.energy_blue_border));
                        break;
                }
                return false;
            }
        });

        solButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData clip = ClipData.newPlainText("email", getString(R.string.solana_chain));
                clipboard.setPrimaryClip(clip);

                Toast.makeText(About.this, getString(R.string.sol_address_copied), Toast.LENGTH_SHORT).show();
            }
        });

        solButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        solButton.setAlpha(0.5f);
                        break;
                    case MotionEvent.ACTION_UP:
                        solButton.setAlpha(0.0f);
                        break;
                }
                return false;
            }
        });

        bnbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData clip = ClipData.newPlainText("email", getString(R.string.bnb_chain));
                clipboard.setPrimaryClip(clip);

                Toast.makeText(About.this, getString(R.string.bnb_address_copied), Toast.LENGTH_SHORT).show();
            }
        });

        bnbButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bnbButton.setAlpha(0.5f);
                        break;
                    case MotionEvent.ACTION_UP:
                        bnbButton.setAlpha(0.0f);
                        break;
                }
                return false;
            }
        });


    }
}
