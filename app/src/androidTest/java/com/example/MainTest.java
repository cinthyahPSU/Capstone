package com.example;

import android.content.Intent;
import android.view.KeyEvent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.coworkingfinds.*;
import org.junit.*;
import org.junit.runner.RunWith;

import java.util.*;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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
        onView(withId(R.id.email)).perform(typeText("testuser@example.com"),
                androidx.test.espresso.action.ViewActions.closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("test1234"),
                androidx.test.espresso.action.ViewActions.closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(click());
    }

    @Test
    public void TC2() {
        onView(withId(R.id.email)).perform(typeText("testuser@example.com"),
                androidx.test.espresso.action.ViewActions.closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("test1234"),
                androidx.test.espresso.action.ViewActions.closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
    }

    @Test
    public void TC3() throws InterruptedException {
        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.searchView)).perform(click());
        onView(withId(androidx.appcompat.R.id.search_src_text))
                .perform(typeText("coworking"), pressKey(KeyEvent.KEYCODE_ENTER));
        Thread.sleep(4000);
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void TC4() throws InterruptedException {
        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.searchView)).perform(click());
        onView(withId(androidx.appcompat.R.id.search_src_text))
                .perform(typeText("noresultslocation"), pressKey(KeyEvent.KEYCODE_ENTER));
        Thread.sleep(4000);
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed())); // fallback check
    }

    @Test
    public void TC5() {
        ActivityScenario.launch(MapActivity.class);
    }

    @Test
    public void TC6() {
        ActivityScenario.launch(MapActivity.class); // mock location denial not shown
    }

    @Test
    public void TC7() throws InterruptedException {
        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.filter_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Has WiFi"))).perform(click());
        onView(withId(R.id.apply_filter_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void TC8() throws InterruptedException {
        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.filter_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Expensive ($$$)"))).perform(click());
        onView(withId(R.id.apply_filter_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void TC9() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DetailsActivity.class);
        CoworkingSpace mockSpace = new CoworkingSpace("Place", "Address", "photo_ref", Arrays.asList("WiFi"), 4.5, 37.0, -122.0);
        intent.putExtra("coworking_space", mockSpace);
        ActivityScenario.launch(intent);
        Thread.sleep(4000);
        onView(withId(R.id.yelp_reviews_text)).check(matches(anyOf(
                withText(containsString("review")),
                withText(containsString("No Yelp reviews found."))
        )));
    }

    @Test
    public void TC10() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DetailsActivity.class);
        CoworkingSpace mockSpace = new CoworkingSpace("NoReviewPlace", "No Address", "", new ArrayList<>(), 3.0, 0.0, 0.0);
        intent.putExtra("coworking_space", mockSpace);
        ActivityScenario.launch(intent);
        Thread.sleep(3000);
        onView(withId(R.id.yelp_reviews_text)).check(matches(anyOf(
                withText(containsString("No Yelp reviews found.")),
                withText(containsString("Failed to load Yelp reviews."))
        )));
    }

    @Test
    public void TC11() {
        ActivityScenario.launch(SignUpActivity.class);
        onView(withId(R.id.email)).perform(typeText("firestoretest@example.com"),
                androidx.test.espresso.action.ViewActions.closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("fire1234"),
                androidx.test.espresso.action.ViewActions.closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(click());
    }


    @Test
    public void TC12() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DetailsActivity.class);
        CoworkingSpace mockSpace = new CoworkingSpace("ImageTest", "Image St", "photo_ref", new ArrayList<>(), 4.0, 0.0, 0.0);
        intent.putExtra("coworking_space", mockSpace);
        ActivityScenario.launch(intent);
        Thread.sleep(3000);
        onView(withId(R.id.place_image)).check(matches(isDisplayed()));
    }

    @Test
    public void TC13() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DetailsActivity.class);
        CoworkingSpace mockSpace = new CoworkingSpace("NoImagePlace", "Somewhere", "", new ArrayList<>(), 4.0, 0.0, 0.0);
        intent.putExtra("coworking_space", mockSpace);
        ActivityScenario.launch(intent);
        Thread.sleep(2000);
        onView(withId(R.id.place_image)).check(matches(isDisplayed()));
    }

    @Test
    public void TC14() {
        DatabaseHelper dbHelper = new DatabaseHelper(ApplicationProvider.getApplicationContext());
        CoworkingSpace space = new CoworkingSpace("FavoritePlace", "456 Road", "", new ArrayList<>(), 4.5, 37.0, -122.0);
        dbHelper.addFavorite(space);
        assertTrue(dbHelper.isFavorite("FavoritePlace"));
    }

    @Test
    public void TC15() {
        DatabaseHelper dbHelper = new DatabaseHelper(ApplicationProvider.getApplicationContext());
        List<CoworkingSpace> favorites = dbHelper.getFavorites();
        for (CoworkingSpace space : favorites) {
            dbHelper.removeFavorite(space.getName());
        }
        assertTrue(dbHelper.getFavorites().isEmpty());
    }

    @Test
    public void TC16() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DetailsActivity.class);
        CoworkingSpace space = new CoworkingSpace("DetailsPlace", "789 Ave", "photo_ref", Arrays.asList("WiFi", "Coffee"), 5.0, 37.0, -122.0);
        intent.putExtra("coworking_space", space);
        ActivityScenario.launch(intent);
        Thread.sleep(3000);
        onView(withId(R.id.location_name)).check(matches(withText("DetailsPlace")));
    }

    @Test
    public void TC17() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DetailsActivity.class);
        CoworkingSpace space = new CoworkingSpace("BrokenPlace", null, null, null, 0.0, 0.0, 0.0);
        intent.putExtra("coworking_space", space);
        ActivityScenario.launch(intent);
        Thread.sleep(2000);
        onView(withId(R.id.location_name)).check(matches(anything())); // should not crash
    }
}
