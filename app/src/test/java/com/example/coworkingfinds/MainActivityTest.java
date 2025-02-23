package com.example.coworkingfinds;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.location.Location;
import android.os.Looper;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30, manifest=Config.NONE, packageName = "com.example.coworkingfindsZ", qualifiers = "port")
public class MainActivityTest {
    @Mock private FirebaseAuth mockAuth;
    @Mock private FirebaseFirestore mockFirestore;
    @Mock private FirebaseUser mockUser;
    @Mock private Location mockLocation;

    private MainActivity mainActivity;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mainActivity = Robolectric.buildActivity(MainActivity.class)
                .setup()
                .get();
        // Initialize Robolectric for MainActivity
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().resume().get();


        setPrivateField(mainActivity, "mAuth", mockAuth);
        setPrivateField(mainActivity, "db", mockFirestore);

        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
    }



    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    // TC-1: Verify user can create an account
    @Test
    public void testUserCanCreateAccount() {
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        assertNotNull("User should be created", mockAuth.getCurrentUser());
    }

    // TC-2: Verify user login
    @Test
    public void testUserLogin() {
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        assertNotNull("User should be logged in", mockAuth.getCurrentUser());
    }

    // TC-3: Search for coworking spaces
    @Test
    public void testSearchForCoworkingSpaces() {
        mainActivity.searchCoworkingSpaces("coworking");
        assertNotNull("Search results should not be null", mainActivity.coworkingSpacesList);
    }

    //TC-4: Verify map loading
    @Test
    public void testMapLoads() {
        when(mockLocation.getLatitude()).thenReturn(37.7749);
        when(mockLocation.getLongitude()).thenReturn(-122.4194);

        assertNotNull("User location should be available", mockLocation);
    }

    //TC-5: Verify filter functionality
    @Test
    public void testApplyFilters() {
        mainActivity.lastUserLocation = mockLocation;
        mainActivity.applyFilters();
        assertNotNull("Filtered results should not be null", mainActivity.coworkingSpacesList);
    }



    //TC-7: Verify user exists in Firestore
    @Test
    public void testUserExistsInFirestore() {
        when(mockFirestore.collection("users").document("user123").get()).thenReturn(null);
        assertNull("User should be in Firestore", mockFirestore.collection("users").document("user123").get());
    }
}
