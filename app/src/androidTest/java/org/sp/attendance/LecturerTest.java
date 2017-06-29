package org.sp.attendance;


import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sp.attendance.ui.ATSLoginActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LecturerTest {

    private static final String staffUsername = "s10001";
    private static final String staffPassword = "staff";
    private static final String attendanceCode = "123456";
    private static final String broadcastDuration = "15";

    @Rule
    public ActivityTestRule<ATSLoginActivity> signInActivityActivityTestRule = new ActivityTestRule<>(ATSLoginActivity.class);

    @Test
    public void fillEditText(){
        onView(withId(R.id.textEdit_userID))
                .perform(typeText(staffUsername),
                        ViewActions.closeSoftKeyboard());

        onView(withId(R.id.textEdit_password))
                .perform(typeText(staffPassword),
                ViewActions.closeSoftKeyboard());

        onView(withId(R.id.button))
                .perform(click());

        onView(withId(R.id.textCode))
                .perform(typeText(attendanceCode),
                ViewActions.closeSoftKeyboard());

        onView(withId(R.id.textDuration))
                .perform(typeText(broadcastDuration),
                ViewActions.closeSoftKeyboard());

        onView(withId(R.id.textCode))
                .perform(click());
    }

}
