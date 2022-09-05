package stepn.sidekick.stepnsidekick;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Information about the app, 'remove ads' button, option to donate.
 *
 * @author Bob Godfrey
 * @version 1.3.13 Removed crypto address donations, added thank-you message for those who removed ads
 *
 */

public class AboutFrag extends Fragment {

    Button emailButton, buyCoffeeButton;
    ImageButton removeAdsButton;
    ImageView removeAdsShadow, buyCoffeeLogo;
    TextView emailTextView, removeAdsTextView, removeAdsShadowTextView, plzSupportTextView, orTv;
    ClipboardManager clipboard;

    public AboutFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        emailButton = view.findViewById(R.id.emailButton);
        emailTextView = view.findViewById(R.id.contactEmailTextView);
        plzSupportTextView = view.findViewById(R.id.supportTextView);
        orTv = view.findViewById(R.id.orTv);
        buyCoffeeButton = view.findViewById(R.id.buyBeerButton);
        buyCoffeeLogo = view.findViewById(R.id.buyBeerLogo);

        removeAdsButton = view.findViewById(R.id.removeAdsButton);
        removeAdsTextView = view.findViewById(R.id.removeAdsTextView);
        removeAdsShadow = view.findViewById(R.id.removeAdsShadowButton);
        removeAdsShadowTextView = view.findViewById(R.id.removeAdsShadowTextView);

        if (((MainActivity) requireActivity()).ads) {
            removeAdsButton.setVisibility(View.VISIBLE);
            removeAdsShadow.setVisibility(View.VISIBLE);
            removeAdsTextView.setVisibility(View.VISIBLE);
            removeAdsShadowTextView.setVisibility(View.VISIBLE);
        } else {
            hideAdsButton();
        }

        removeAdsButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        removeAdsButton.setVisibility(View.INVISIBLE);
                        removeAdsTextView.setVisibility(View.INVISIBLE);
                        removeAdsShadow.setImageResource(R.drawable.start_button);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        removeAdsButton.setVisibility(View.VISIBLE);
                        removeAdsTextView.setVisibility(View.VISIBLE);
                        removeAdsShadow.setImageResource(R.drawable.start_button_shadow);
                        break;
                }
                return false;
            }
        });

        removeAdsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) requireActivity()).makePurchase(view);
                if (!((MainActivity) requireActivity()).ads) {
                    hideAdsButton();
                }
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData clip = ClipData.newPlainText("email", getString(R.string.contact_email));
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getContext(), getString(R.string.email_copied), Toast.LENGTH_SHORT).show();
            }
        });

        emailButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        emailTextView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.energy_blue_darker));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        emailTextView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.luck_socket_border));
                        break;
                }
                return false;
            }
        });

        buyCoffeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.buymeacoffee.com/robgodfrey");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        buyCoffeeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        buyCoffeeLogo.setAlpha(0.5f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        buyCoffeeLogo.setAlpha(1.0f);
                        break;
                }
                return false;
            }
        });

        return view;
    }

    public void hideAdsButton() {
        removeAdsButton.setVisibility(View.GONE);
        removeAdsShadow.setVisibility(View.GONE);
        removeAdsTextView.setVisibility(View.GONE);
        removeAdsShadowTextView.setVisibility(View.GONE);
        orTv.setTextColor(Color.WHITE);
        plzSupportTextView.setText("Thank you for removing ads! ❤");
    }

}