package org.sp.attendance;


import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.sp.attendance.ui.ATSLoginActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class StudentNotSpice {

    private static final String studentUsername = "stud1";
    private static final String studentPassword = "student";

    @Rule
    public ActivityTestRule<ATSLoginActivity> signInActivityActivityTestRule = new ActivityTestRule<>(ATSLoginActivity.class);

    @Test
    public void fillEditText() {
        onView(withId(R.id.textEdit_userID))
                .perform(typeText(studentUsername),
                ViewActions.closeSoftKeyboard());

        onView(withId(R.id.textEdit_password))
                .perform(typeText(studentPassword),
                ViewActions.closeSoftKeyboard());

        onView(withId(R.id.button)).perform(click());
    }
}
