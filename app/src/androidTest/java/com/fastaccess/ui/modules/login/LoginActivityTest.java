package com.fastaccess.ui.modules.login;


import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.fastaccess.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.fastaccess.helper.TestHelper.textInputLayoutHasError;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class) @LargeTest
public class LoginActivityTest {

    @Rule public ActivityTestRule<LoginActivity> testRule = new ActivityTestRule<>(LoginActivity.class);

    @Test public void successLoginClickSuccessTest() {
        String username = "username";
        String password = "password";
        onView(withId(R.id.usernameEditText)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.progress)).check(matches(isDisplayed()));
    }

    @Test public void usernameErrorTest() {
        String password = "password";
        onView(withId(R.id.passwordEditText)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())));
        onView(withId(R.id.username)).check(matches(textInputLayoutHasError(testRule.getActivity().getString(R.string.required_field))));
    }

    @Test public void passwordErrorTest() {
        String username = "username";
        onView(withId(R.id.usernameEditText)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())));
        onView(withId(R.id.password)).check(matches(textInputLayoutHasError(testRule.getActivity().getString(R.string.required_field))));
    }

}