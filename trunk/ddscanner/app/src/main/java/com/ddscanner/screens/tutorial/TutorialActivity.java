package com.ddscanner.screens.tutorial;


import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.utils.SharedPreferenceHelper;

import java.util.List;

import za.co.riggaroo.materialhelptutorial.MaterialTutorialFragment;
import za.co.riggaroo.materialhelptutorial.R;
import za.co.riggaroo.materialhelptutorial.TutorialItem;
import za.co.riggaroo.materialhelptutorial.adapter.MaterialTutorialAdapter;
import za.co.riggaroo.materialhelptutorial.tutorial.MaterialTutorialContract;
import za.co.riggaroo.materialhelptutorial.tutorial.MaterialTutorialPresenter;
import za.co.riggaroo.materialhelptutorial.view.CirclePageIndicator;


public class TutorialActivity extends AppCompatActivity implements TutorialContract.View {

    private static final String TAG = "MaterialTutActivity";
    public static final String MATERIAL_TUTORIAL_ARG_TUTORIAL_ITEMS = "tutorial_items";
    private ViewPager mHelpTutorialViewPager;
    private View mRootView;
    private TextView mTextViewSkip;
    private Button mNextButton;
    private Button mDoneButton;
    private TutorialPresenter materialTutorialPresenter;

    private View.OnClickListener finishTutorialClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            materialTutorialPresenter.doneOrSkipClick();
            if (v.getId() == R.id.activity_help_skip_textview) {
                if (mHelpTutorialViewPager.getCurrentItem() == 0) {
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setTutorialState(SharedPreferenceHelper.TutorialState.SKIPPED);
                } else {
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setTutorialState(SharedPreferenceHelper.TutorialState.PARTLY_WATCHED);
                }
                EventsTracker.trackSkipTutorial(String.valueOf(mHelpTutorialViewPager.getCurrentItem()));
            } else {
                DDScannerApplication.getInstance().getSharedPreferenceHelper().setTutorialState(SharedPreferenceHelper.TutorialState.WATCHED);
                EventsTracker.trackWatchTutorial();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ddscanner.R.layout.activity_onboarding);

        materialTutorialPresenter = new TutorialPresenter(this, this);
        setStatusBarColor();
        ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            getActionBar().hide();
        }
        mRootView = findViewById(R.id.activity_help_root);
        mHelpTutorialViewPager = findViewById(R.id.activity_help_view_pager);
        mTextViewSkip = findViewById(R.id.activity_help_skip_textview);
        mNextButton = findViewById(R.id.activity_next_button);
        mDoneButton = findViewById(R.id.activity_tutorial_done);

        mTextViewSkip.setOnClickListener(finishTutorialClickListener);
        mDoneButton.setOnClickListener(finishTutorialClickListener);



        mNextButton.setOnClickListener(v -> materialTutorialPresenter.nextClick());
        List<TutorialItem> tutorialItems = getIntent().getParcelableArrayListExtra(MATERIAL_TUTORIAL_ARG_TUTORIAL_ITEMS);
        materialTutorialPresenter.loadViewPagerFragments(tutorialItems);
    }

    private void setStatusBarColor() {
//        if (isFinishing()) {
//            return;
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
    }

    @Override
    public void showNextTutorial() {
        int currentItem = mHelpTutorialViewPager.getCurrentItem();
        if (currentItem < materialTutorialPresenter.getNumberOfTutorials()) {
            mHelpTutorialViewPager.setCurrentItem(mHelpTutorialViewPager.getCurrentItem() + 1);
        }
    }

    @Override
    public void showEndTutorial() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void setBackgroundColor(int color) {
//        mRootView.setBackgroundColor(color);
    }

    @Override
    public void showDoneButton() {
        mTextViewSkip.setVisibility(View.INVISIBLE);
        mNextButton.setVisibility(View.GONE);
        mDoneButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSkipButton() {
        mTextViewSkip.setVisibility(View.VISIBLE);
        mNextButton.setVisibility(View.VISIBLE);
        mDoneButton.setVisibility(View.GONE);
    }

    @Override
    public void setViewPagerFragments(List<TutorialFragment> materialTutorialFragments) {
        mHelpTutorialViewPager.setAdapter(new TutorialAdapter(getSupportFragmentManager(), materialTutorialFragments));
        CirclePageIndicator mCirclePageIndicator = findViewById(R.id.activity_help_view_page_indicator);

        mCirclePageIndicator.setViewPager(mHelpTutorialViewPager);
        mCirclePageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                materialTutorialPresenter.onPageSelected(mHelpTutorialViewPager.getCurrentItem());

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mHelpTutorialViewPager.setPageTransformer(true, (page, position) -> materialTutorialPresenter.transformPage(page, position)

        );
    }

    @Override
    public void onBackPressed() {

    }
}
