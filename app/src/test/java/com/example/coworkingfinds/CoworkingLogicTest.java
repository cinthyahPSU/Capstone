package com.example.coworkingfinds;

import com.example.coworkingfinds.CoworkingSpace;
import com.example.coworkingfinds.DatabaseHelper;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

public class CoworkingLogicTest {

    private List<CoworkingSpace> spaces;
    private DatabaseHelper dbHelper;

    @Before
    public void setUp() {
        spaces = Arrays.asList(
                new CoworkingSpace("Location 1", "123 St", "", Arrays.asList("WiFi"), 4.8, 37.1, -122.1),
                new CoworkingSpace("Location 2", "456 St", "", Arrays.asList("Parking"), 3.5, 37.0, -122.0),
                new CoworkingSpace("Location 3", "789 St", "", Arrays.asList("WiFi", "Coffee"), 4.2, 37.2, -122.2)
        );

        dbHelper = new DatabaseHelper(null) {
            private final List<CoworkingSpace> favorites = new ArrayList<>();

            @Override
            public void addFavorite(CoworkingSpace space) {
                favorites.add(space);
            }

            @Override
            public void removeFavorite(String name) {
                favorites.removeIf(space -> space.getName().equals(name));
            }

            @Override
            public boolean isFavorite(String name) {
                return favorites.stream().anyMatch(space -> space.getName().equals(name));
            }

            @Override
            public List<CoworkingSpace> getFavorites() {
                return new ArrayList<>(favorites);
            }
        };
    }

    // Heuristic scoring test
    @Test
    public void testHeuristicScoring() {
        double userLat = 37.0;
        double userLng = -122.0;

        List<ScoredCoworkingSpace> scored = new ArrayList<>();
        for (CoworkingSpace space : spaces) {
            double distance = Math.sqrt(Math.pow(space.getLatitude() - userLat, 2) +
                    Math.pow(space.getLongitude() - userLng, 2));
            double score = (5 - distance) * 0.3 + space.getRating() * 0.7;
            scored.add(new ScoredCoworkingSpace(space, score));
        }

        Collections.sort(scored, Comparator.comparingDouble(s -> -s.score));

        assertEquals("Location 1", scored.get(0).space.getName());
        assertEquals("Location 3", scored.get(1).space.getName());
        assertEquals("Location 2", scored.get(2).space.getName());
    }

    // Filter by WiFi
    @Test
    public void testFilterHasWiFi() {
        List<CoworkingSpace> filtered = new ArrayList<>();
        for (CoworkingSpace space : spaces) {
            if (space.getAmenities().contains("WiFi")) {
                filtered.add(space);
            }
        }
        assertEquals(2, filtered.size());
    }

    // Test add and remove favorite
    @Test
    public void testAddAndRemoveFavorite() {
        CoworkingSpace space = spaces.get(0);
        dbHelper.addFavorite(space);
        assertTrue(dbHelper.isFavorite(space.getName()));

        dbHelper.removeFavorite(space.getName());
        assertFalse(dbHelper.isFavorite(space.getName()));
    }

    // Test retrieving favorite list
    @Test
    public void testFavoritesList() {
        dbHelper.addFavorite(spaces.get(0));
        dbHelper.addFavorite(spaces.get(2));

        List<CoworkingSpace> favorites = dbHelper.getFavorites();
        assertEquals(2, favorites.size());
        assertEquals("Location 1", favorites.get(0).getName());
        assertEquals("Location 3", favorites.get(1).getName());
    }

    static class ScoredCoworkingSpace {
        CoworkingSpace space;
        double score;

        ScoredCoworkingSpace(CoworkingSpace space, double score) {
            this.space = space;
            this.score = score;
        }
    }
}
