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
 * @author Rob Godfrey
 * @version 1.5.0 - GMT calcs
 *
 */

public class AboutFrag extends Fragment {

    Button emailButton, buyCoffeeButton, kritButton, karlButton, stepnGuideButton,  stepnGuideAuthorButton,
            stepnWikiButton, stepnWikiAuthorButton, stepnAssistButton, stepnAssistAuthorButton,
            stepnFpButton, stepnFpAuthorButton, stepnStatsButton, stepnStatsAuthorOneButton,
            stepnStatsAuthorTwoButton, stepnMarketGuideButton, stepnMarketGuideAuthorButton, otikxButton;
    ImageButton removeAdsButton;
    ImageView removeAdsShadow, buyCoffeeLogo, stepnGuideIv, stepnWikiIv, stepnAssistIv, stepnFpIv,
            stepnEfIv, stepnStatsIv, stepnMarketIv;
    TextView emailTextView, removeAdsTextView, removeAdsShadowTextView, plzSupportTextView, orTv, kritTv,
            karlTv, stepnGuideAuthorTv, stepnWikiAuthorTv, stepnAssistAuthorTv, stepnFpAuthorTv,
            stepnStatsAuthorOneTv, stepnStatsAuthorTwoTv, stepnMarketGuideAuthorTv, otikxTv;
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

        kritButton = view.findViewById(R.id.kritButton);
        karlButton = view.findViewById(R.id.karlButton);
        otikxButton = view.findViewById(R.id.otikxButton);
        stepnGuideButton = view.findViewById(R.id.stepnGuideButton);
        stepnGuideAuthorButton = view.findViewById(R.id.stepnGuideAuthorButton);
        stepnWikiButton = view.findViewById(R.id.stepnWikiButton);
        stepnWikiAuthorButton = view.findViewById(R.id.stepnWikiAuthorButton);
        stepnAssistButton = view.findViewById(R.id.stepnAssistButton);
        stepnAssistAuthorButton = view.findViewById(R.id.stepnAssistAuthorButton);
        stepnFpButton = view.findViewById(R.id.stepnFpButton);
        stepnFpAuthorButton = view.findViewById(R.id.stepnFpAuthorButton);
        stepnStatsButton = view.findViewById(R.id.stepnStatsButton);
        stepnStatsAuthorOneButton = view.findViewById(R.id.stepnStatsAuthor1Button);
        stepnStatsAuthorTwoButton = view.findViewById(R.id.stepnStatsAuthor2Button);
        stepnMarketGuideButton = view.findViewById(R.id.stepnMarketGuideButton);
        stepnMarketGuideAuthorButton = view.findViewById(R.id.stepnMarketGuideAuthorButton);

        kritTv = view.findViewById(R.id.kritThanksTextView);
        karlTv = view.findViewById(R.id.karlThanksTextView);
        otikxTv = view.findViewById(R.id.otikxThanksTextView);
        stepnGuideAuthorTv = view.findViewById(R.id.stepnGuideAuthor);
        stepnWikiAuthorTv = view.findViewById(R.id.stepnWikiAuthor);
        stepnAssistAuthorTv = view.findViewById(R.id.stepnAssistAuthor);
        stepnFpAuthorTv = view.findViewById(R.id.stepnFpAuthor);
        stepnStatsAuthorOneTv = view.findViewById(R.id.stepnStatsAuthor1);
        stepnStatsAuthorTwoTv = view.findViewById(R.id.stepnStatsAuthor2);
        stepnMarketGuideAuthorTv = view.findViewById(R.id.stepnMarketGuideAuthor);

        stepnGuideIv = view.findViewById(R.id.stepnGuide);
        stepnWikiIv = view.findViewById(R.id.stepnWiki);
        stepnAssistIv = view.findViewById(R.id.stepnAssist);
        stepnFpIv = view.findViewById(R.id.stepnFp);
        stepnStatsIv = view.findViewById(R.id.stepnStats);
        stepnMarketIv = view.findViewById(R.id.stepnMarketGuide);

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
                        removeAdsShadow.setImageResource(R.drawable.button_start);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        removeAdsButton.setVisibility(View.VISIBLE);
                        removeAdsTextView.setVisibility(View.VISIBLE);
                        removeAdsShadow.setImageResource(R.drawable.button_start_shadow);
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

        textTouchies(emailButton, emailTextView);

        buyCoffeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.buymeacoffee.com/robgodfrey");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        imageTouchies(buyCoffeeButton, buyCoffeeLogo);

        kritButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://twitter.com/Krit_STEPNstats");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        textTouchies(kritButton, kritTv);

        karlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://twitter.com/Karl_Khader");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        textTouchies(karlButton, karlTv);

        otikxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://twitter.com/otik_x");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        textTouchies(otikxButton, otikxTv);

        stepnGuideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://stepn.guide/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        imageTouchies(stepnGuideButton, stepnGuideIv);

        stepnGuideAuthorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://twitter.com/StepNGuide_");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        textTouchies(stepnGuideAuthorButton, stepnGuideAuthorTv);

        stepnWikiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://stepn.wiki/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        imageTouchies(stepnWikiButton, stepnWikiIv);

        stepnWikiAuthorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://twitter.com/STEPNwiki");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        textTouchies(stepnWikiAuthorButton, stepnWikiAuthorTv);

        stepnAssistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://stepn.vanxh.dev/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        imageTouchies(stepnAssistButton, stepnAssistIv);

        stepnAssistAuthorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://twitter.com/Vanxhh");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        textTouchies(stepnAssistAuthorButton, stepnAssistAuthorTv);

        stepnFpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://stepnfp.com/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        imageTouchies(stepnFpButton, stepnFpIv);

        stepnFpAuthorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://twitter.com/StepnFP");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        textTouchies(stepnFpAuthorButton, stepnFpAuthorTv);

        stepnStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireActivity(),"Coming soon! Follow Krit on Twitter for more info", Toast.LENGTH_SHORT).show();
            }
        });

        imageTouchies(stepnStatsButton, stepnStatsIv);

        stepnStatsAuthorOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://twitter.com/Krit_STEPNstats");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        textTouchies(stepnStatsAuthorOneButton, stepnStatsAuthorOneTv);

        stepnStatsAuthorTwoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://twitter.com/lyesbcb");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        textTouchies(stepnStatsAuthorTwoButton, stepnStatsAuthorTwoTv);

        stepnMarketGuideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://stepn-market.guide/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        imageTouchies(stepnMarketGuideButton, stepnMarketIv);

        stepnMarketGuideAuthorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://twitter.com/t2_stepn");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        textTouchies(stepnMarketGuideAuthorButton, stepnMarketGuideAuthorTv);

        return view;
    }

    // changes text color on touch (for clickable links)
    @SuppressLint("ClickableViewAccessibility")
    private void textTouchies(Button button, TextView text) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        text.setTextColor(ContextCompat.getColor(requireActivity(), R.color.energy_blue_lighter));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        text.setTextColor(ContextCompat.getColor(requireActivity(), R.color.luck_socket_border));
                        break;
                }
                return false;
            }
        });
    }

    // changes image color on touch (for clickable links)
    @SuppressLint("ClickableViewAccessibility")
    private void imageTouchies(Button button, ImageView image) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        image.setAlpha(0.5f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        image.setAlpha(1.0f);
                        break;
                }
                return false;
            }
        });
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