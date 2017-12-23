package com.ventoray.bakingrecipes.ui;

import android.app.Activity;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;

import com.ventoray.bakingrecipes.R;
import com.ventoray.bakingrecipes.testing.RecipeIdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasChildCount;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static org.hamcrest.Matchers.is;

/**
 * Created by nicks on 12/20/2017.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private static final int RECIPES_COUNT = 4;
    private static final String POSITION_ONE_RECIPE = "Nutella Pie";
    private static final int POSITION_ONE_STEPS_COUNT = 7;
    private ViewInteraction recipesList;
    private Activity currentActivity;
    private RecipeIdlingResource idlingResource;


    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<MainActivity>(
            MainActivity.class
    );

    @Before
    public void registerIdlingResource() {
        idlingResource = activityRule.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(idlingResource);
    }

    @Before
    public void getRecipesList() {
        recipesList = onView(withId(R.id.recycler_recipes));
    }

    @Test
    public void launch() {
        recipesList.check(matches(isDisplayed()));
        recipesList.check(matches(hasChildCount(RECIPES_COUNT)));
    }

    @Test
    public void testClickRecipe() {
        ViewInteraction stepsList;
        recipesList.perform(RecyclerViewActions.actionOnItemAtPosition(
                0, click()
        ));

        stepsList = onView(withId(R.id.recycler_steps));
        stepsList.check(matches(isDisplayed()));
        stepsList.check(matches(hasChildCount(POSITION_ONE_STEPS_COUNT)));

        String title = getActivityInstance().getTitle().toString();
        assert title.equals(POSITION_ONE_RECIPE);

    }

    @After
    public void unregisterIdlingResource() {
        if (idlingResource != null) {
            Espresso.unregisterIdlingResources(idlingResource);
        }
    }

    private Activity getActivityInstance(){
        getInstrumentation().runOnMainSync(myRunnable);
        return currentActivity;
    }


    private Runnable myRunnable = new Runnable() {
        public void run() {
            Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
            currentActivity = (Activity) resumedActivities.iterator().next();
        }
    };


}