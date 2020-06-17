package com.capstondesign.miraeseat;

import android.view.View;

public abstract class OnOneOffClickListener implements View.OnClickListener {
    private boolean clickable = true;

    /**
     * Override onOneClick() instead.
     */
    @Override
    public final void onClick(View v) {
        if (clickable) {
            clickable = false;
            onOneClick(v);
            //reset(); // uncomment this line to reset automatically
        }
    }

    /**
     * Override this function to handle clicks.
     * reset() must be called after each click for this function to be called
     * again.
     * @param v
     */
    public abstract void onOneClick(View v);

    /**
     * Allows another click.
     */
    public void reset() {
        clickable = true;
    }

    public void disable() {
        clickable = false;
    }
}