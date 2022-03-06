package com.fastaccess.ui.modules.main;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import com.fastaccess.R;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.ui.modules.feeds.FeedsFragment;
import com.fastaccess.ui.modules.login.chooser.LoginChooserActivity;
import com.fastaccess.ui.modules.main.issues.pager.MyIssuesPagerFragment;
import com.fastaccess.ui.modules.main.pullrequests.pager.MyPullsPagerFragment;
import com.fastaccess.ui.modules.notification.NotificationActivity;
import com.fastaccess.ui.modules.search.SearchActivity;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.fastaccess.helper.TestHelper.bottomNavAction;
import static com.fastaccess.helper.TestHelper.bottomNavSelection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Kosh on 06 May 2017, 2:11 AM
 */

public class MainActivityTest {
    @Rule public IntentsTestRule<MainActivity> testRule = new IntentsTestRule<>(MainActivity.class);

    @Test public void noUserTest() {
        assertEquals(testRule.getActivity().isLoggedIn(), false);
        intended(hasComponent(LoginChooserActivity.class.getName()));
    }

    @Test public void onInitTest() {
        assertEquals(testRule.getActivity().isLoggedIn(), true);
        onView(withId(R.id.bottomNavigation)).check(matches(bottomNavSelection(0)));
        assertNotNull(AppHelper.getFragmentByTag(testRule.getActivity().getSupportFragmentManager(), FeedsFragment.TAG));
    }

    @Test public void onSelectIssuesTabTest() {
        assertEquals(testRule.getActivity().isLoggedIn(), true);
        onView(withId(R.id.bottomNavigation)).perform(bottomNavAction(1))
                .check(matches(bottomNavSelection(1)));
        assertNotNull(AppHelper.getFragmentByTag(testRule.getActivity().getSupportFragmentManager(), MyIssuesPagerFragment.TAG));
    }

    @Test public void onSelectPullRequestsTabTest() {
        assertEquals(testRule.getActivity().isLoggedIn(), true);
        onView(withId(R.id.bottomNavigation)).perform(bottomNavAction(2))
                .check(matches(bottomNavSelection(2)));
        assertNotNull(AppHelper.getFragmentByTag(testRule.getActivity().getSupportFragmentManager(), MyPullsPagerFragment.TAG));
    }

    @Test public void startNotificationsIntentTest() {
        onView(withId(R.id.notifications)).perform(click());
        intended(hasComponent(NotificationActivity.class.getName()));
    }

    @Test public void startSearchIntentTest() {
        onView(withId(R.id.search)).perform(click());
        intended(hasComponent(SearchActivity.class.getName()));
    }
}
