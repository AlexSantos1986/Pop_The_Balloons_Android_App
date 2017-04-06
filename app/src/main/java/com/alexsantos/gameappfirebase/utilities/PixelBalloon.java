package com.alexsantos.gameappfirebase.utilities;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Alex on 06/04/2017.
 */

public class PixelBalloon {

    public static int pixelToBalloons(int px , Context context){

        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,px,context.getResources().getDisplayMetrics());
    }


}
