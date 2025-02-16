package com.example.coworkingfinds;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class FirebaseAuthTest {

    @Mock
    private FirebaseAuth mockAuth;

    @Mock
    private FirebaseUser mockUser;

    @Mock
    private Task<AuthResult> mockAuthResultTask;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUserLogin_Success() {
        // Simulate successful login
        when(mockAuth.signInWithEmailAndPassword("test@example.com", "password123"))
                .thenReturn(Tasks.forResult(mock(AuthResult.class)));

        // Mock getCurrentUser to return a user
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("fakeUserId");

        // Perform login
        Task<AuthResult> resultTask = mockAuth.signInWithEmailAndPassword("test@example.com", "password123");

        // Verify success
        assertTrue(resultTask.isSuccessful());
        assertNotNull(mockAuth.getCurrentUser());
        assertEquals("fakeUserId", mockAuth.getCurrentUser().getUid());
    }
}