package com.example;

import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.coworkingfinds.CoworkingSpace;
import com.example.coworkingfinds.DatabaseHelper;
import com.example.coworkingfinds.DetailsActivity;
import com.example.coworkingfinds.MainActivity;
import com.example.coworkingfinds.MapActivity;
import com.example.coworkingfinds.R;
import com.example.coworkingfinds.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertTrue;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainTest {

    @Rule
    public ActivityScenarioRule<SignUpActivity> activityRule = new ActivityScenarioRule<>(SignUpActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void TC1() {
        onView(ViewMatchers.withId(R.id.email)).perform(typeText("testuser@example.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("test1234"), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(click());
    }

    @Test
    public void TC2() {
        onView(withId(R.id.email)).perform(typeText("testuser@example.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("test1234"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
    }

    @Test
    public void TC3() {
        ActivityScenario.launch(MainActivity.class);

        // Open and type into the SearchView
        onView(withId(R.id.searchView)).perform(click());
        onView(withId(androidx.appcompat.R.id.search_src_text))
                .perform(typeText("coworking"), pressKey(KeyEvent.KEYCODE_ENTER));

        // Confirm the list is visible (or results populated)
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }


    @Test
    public void TC4() {
        ActivityScenario.launch(MapActivity.class);
    }


    @Test
    public void TC5() throws InterruptedException {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.filter_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Has WiFi"))).perform(click());
        onView(withId(R.id.apply_filter_button)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }



    @Test
    public void TC7() {
        ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void TC8() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DetailsActivity.class);
        CoworkingSpace mockSpace = new CoworkingSpace("Place", "Address", "photo_ref", Arrays.asList("WiFi", "Parking"), 4.5);
        intent.putExtra("coworking_space", mockSpace);
        ActivityScenario.launch(intent);
        onView(withId(R.id.location_name)).check(matches(withText("Place")));
    }

    @Test
    public void TC9() {
        DatabaseHelper dbHelper = new DatabaseHelper(ApplicationProvider.getApplicationContext());
        CoworkingSpace space = new CoworkingSpace("Test Place", "123 Street", "", new ArrayList<>(), 4.5);
        dbHelper.addFavorite(space);
        assertTrue(dbHelper.isFavorite("Test Place"));
    }

    @Test
    public void TC10() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DetailsActivity.class);
        CoworkingSpace mockSpace = new CoworkingSpace("Place", "Address", "", Arrays.asList("WiFi", "Parking"), 4.5);
        intent.putExtra("coworking_space", mockSpace);
        ActivityScenario.launch(intent);
        onView(withId(R.id.location_name)).check(matches(withText("Place")));
    }


}
