package com.example.openingapp.GlobalClasses;

import static android.content.Context.MODE_PRIVATE;
import static androidx.core.app.ActivityCompat.finishAffinity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.openingapp.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Response;

public class GlobalClassForFunctions {


    private static final GlobalClassForFunctions ourInstance = new GlobalClassForFunctions();
    public ViewDialog viewDialog;
    public AlertDialog alertDialog;
    private final String SHARED_PREFERENCE_NAME = "AccessTokenSP";
    private SharedPreferences mSharedPreferences;
    public final String ACCESS_TOKEN_KEY = "ACCESS_TOKEN";
    public final String ACCESS_TOKEN_VALUE = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjU5MjcsImlhdCI6MTY3NDU1MDQ1MH0" +
            ".dCkW0ox8tbjJA2GgUx2UEwNlbTZ7Rr38PVFJevYcXFI";


    // screen of the app
    public double SCREEN_SIZE = 6.0;
    // aspect ratio of screen
    public double ASPECT_RATIO = 0;

    private Context context;


    public static GlobalClassForFunctions getInstance() {
        return ourInstance;
    }


    private GlobalClassForFunctions() {

        setScreenSize();
    }

    public void launchBasicGlobalFunctions(Context context) {
        setContext(context);
        getSharedPreferenceInstance(getContext());
    }


    /**
     * This function helps to calculate the screen size and store he value into the SCREEN_SIZE
     */
    public void setScreenSize() {
        try {
            //get the display metrices in application class
            DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
            double wi = displayMetrics.heightPixels / (double) displayMetrics.xdpi;
            double hi = displayMetrics.widthPixels / (double) displayMetrics.ydpi;
            double x = Math.pow(wi, 2);
            double y = Math.pow(hi, 2);
            double screenInches = Math.sqrt(x + y);
            System.out.println(screenInches + "----Screen_Size Width--:" + wi * displayMetrics.xdpi + "----Height--:" + hi * displayMetrics.ydpi);
            SCREEN_SIZE = screenInches;
            ASPECT_RATIO = hi / wi;
        } catch (Exception e) {
            System.out.println("Exception" + e);
        }
    }


    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    /**
     * get for network connection status
     *
     * @return network status
     */
    public boolean IsConnected() {
        boolean IsNetworkConnected = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) GlobalClassForFunctions.getInstance().getContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = null;
            // to get the active network only if netInfo is not null
            // connectivityManager == null means the phone is in Airplane mode
            if (connectivityManager != null) {
                netInfo = connectivityManager.getActiveNetworkInfo();
            }
            //should check null because in airplane mode it will be null
            IsNetworkConnected = (netInfo != null && netInfo.isConnected());


        } catch (NullPointerException e) {
            e.printStackTrace();

        }
        return IsNetworkConnected;
    }


    // function to get instance  of  the view dialog using context
    private void InstanceOfTheViewDialog(Context context) {
        //..initialize our custom loading dialog here with passing this activity context
        viewDialog = new ViewDialog(context);
    }


    // function to get the instance of the alert dialog
    public void GetInstanceOfAlertDialog(Context context) {
        // alertDialog  == null then only create instance of the alert dialog again
        alertDialog = new AlertDialog.Builder(context).create();
    }


    // show custom dialog
    public void showCustomLoadingDialog(Context context) {


        try {
            System.out.println("view dialog is not null" + viewDialog);
            if (viewDialog != null) {
                System.out.println("viewDialog.IsShowing()" + viewDialog.IsShowing());

                if(viewDialog.IsShowing())
                {
                    viewDialog.hideDialog();
                }


            }

            // else create mew dialog to show
            else {
                InstanceOfTheViewDialog(context);

                //..show gif
            }
            viewDialog.showDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Hide custom dialog
    public void hideProgresDialogue(Context context) {
        // run on ui thread
        ((Activity) context).runOnUiThread(() -> {
            try {
                if (viewDialog != null) {
                    viewDialog.hideDialog();
                }
            } catch (Exception e) {
                System.out.println("Exception" + e);
            }
        });

    }


    /**
     * This method shows the alert dialogue
     * Alert dialogue contains button Ok , on click dismis the alert dialogue
     *
     * @param strTitle   -- String -> Title fo alert
     * @param strMessage -- string  -> Message on Alert
     */
    public void showAlertdialog(String strTitle, String strMessage, Context context, boolean... CloseApplication) {

        Handler mainHandler;

        try {
            // Get a handler that can be used to post to the main thread
            mainHandler = new Handler(context.getMainLooper());

            Runnable showALert = () -> {


                // check for network call
                // if IsConnected() returns false that means internet is not connected or there is an exception in the method
                if (!IsConnected()) {
                    return;
                }


                // GetInstanceOfAlertDialog I have used this function to get the instance of alert dialog
                // it will return the instance if the alert dialog is null
                // this save me from null pointer exception if in any case alert dilog is null
                GetInstanceOfAlertDialog(context);
                // isCloseApplication = true means should close
                // isCloseApplication = false means should not close
                boolean isCloseApplication = false;


            /*
            CloseApplication is the array to store the that if to show the application

            * I have used this process so that I need not to update every alert dialog to send false if I need not to show close application
            * I only send true where I need to show alert for the close dialog
             */
                if (CloseApplication.length > 0) {
                    isCloseApplication = true;
                }

                String message = strMessage;


                alertDialog.setTitle(strTitle);
                alertDialog.setMessage(message);
                alertDialog.setCancelable(false);


                // isCloseApplication = true means should close
                // isCloseApplication = false means should not close
                if (isCloseApplication) {
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            (dialog1, which) -> {

                                freeMemory();


                                // method to finish the application instance completely
                                finishAffinity((Activity) context);

                                dialog1.dismiss();
                            });
                }
                // if false do as I need to do close function only as the alert is not for exiting app
                else {
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                            (dialog1, which) -> dialog1.dismiss());
                }


                System.out.println("alertDialog" + alertDialog);


                System.out.println("alert dialog is showing already donot show it again");
                // only show alert dialog if it is not already showing
                if (!alertDialog.isShowing()) {
                    alertDialog.show();
                }

            };
            mainHandler.post(showALert);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // hide progress dialog if opened
            GlobalClassForFunctions.getInstance().hideProgresDialogue(context);
        }


    }


    /**
     * This method is used to set the size of the Text in the View
     *
     * @param view1 is the array paramter to which size is need to be set.
     */
    public void setViewSize(int type, View... view1) {

        for (View view : view1) {

            // set the size of the Button
            if (view instanceof Button) {
//            if(view.getId() == R.id.input_firstname)
                ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_DIP, convertPixelsToDp((int) ReturnTextSize(type), context));
            }

            // set the size of the text view
            else if (view instanceof TextView) {
//            if(view.getId() == R.id.input_firstname)
                ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_DIP, convertPixelsToDp((int) ReturnTextSize(type), context));

                // Set the text view ellipsize programmatically
                ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
            }


        }

    }


    // to set textsize of the text in entire app
    public float ReturnTextSize(int TypeoFTextNeedToBeReturned) {
        try {
            float screenSize = (float) ScreenSize();
            System.out.println("SCREEN SIZE" + screenSize);
            /*
             * TypeoFTextNeedToBeReturned is the variable which defined which type size needs to be returned
             *
             * 1 means Heading TextSize
             * 2 means simple view TextSize
             * 3 means small view  textsize
             */

            // to check if the requested size is of Heading
            if (TypeoFTextNeedToBeReturned == 1) {
                return screenSize > 12.5 ? context.getResources().getDimension(R.dimen.text_Heading_Size_triple_Extra_Large) : screenSize >= 11.1 && screenSize <= 12.5 ? context.getResources().getDimension(R.dimen.text_Heading_Size_double_Extra_Large) : screenSize > 9.5 && screenSize <= 11.0 ? context.getResources().getDimension(R.dimen.text_Heading_Size_Extra_Large) : screenSize > 8.5 && screenSize <= 9.5 ? context.getResources().getDimension(R.dimen.text_Heading_Size_Large) : screenSize > 7.0 && screenSize <= 8.5 ? context.getResources().getDimension(R.dimen.text_Heading_Size_Medium_Large) : screenSize >= 5.6 && screenSize <= 7.0 ? context.getResources().getDimension(R.dimen.text_Heading_Size_Medium) : screenSize >= 4.5 && screenSize < 5.6 ? context.getResources().getDimension(R.dimen.text_Heading_Size_Small) : context.getResources().getDimension(R.dimen.text_Heading_Size_default);

            } else if (TypeoFTextNeedToBeReturned == 2) {
                return screenSize > 12.5 ? context.getResources().getDimension(R.dimen.text_Size_triple_Extra_Large) : screenSize >= 11.1 &&
                        screenSize <= 12.5 ? context.getResources().getDimension(R.dimen.text_Size_double_Extra_Large) : screenSize > 9.5 && screenSize <= 11.0 ? context.getResources().getDimension(R.dimen.text_Size_Extra_Large) : screenSize > 8.5 && screenSize <= 9.5 ? context.getResources().getDimension(R.dimen.text_Size_Large) : screenSize > 7.0 && screenSize <= 8.5 ? context.getResources().getDimension(R.dimen.text_Size_Medium_Large) : screenSize >= 5.6 && screenSize <= 7.0 ? context.getResources().getDimension(R.dimen.text_Size_Medium) : screenSize >= 4.5 && screenSize < 5.6 ? context.getResources().getDimension(R.dimen.text_Size_Small) : context.getResources().getDimension(R.dimen.text_Size_default);

            } else if (TypeoFTextNeedToBeReturned == 3) {
                return screenSize > 12.5 ? context.getResources().getDimension(R.dimen.text_small_Size_triple_Extra_Large) : screenSize >= 11.1 && screenSize <= 12.5 ? context.getResources().getDimension(R.dimen.text_small_Size_double_Extra_Large) : screenSize > 9.5 && screenSize <= 11.0 ? context.getResources().getDimension(R.dimen.text_small_Size_Extra_Large) : screenSize > 8.5 && screenSize <= 9.5 ? context.getResources().getDimension(R.dimen.text_small_Size_Large) : screenSize > 7.0 && screenSize <= 8.5 ? context.getResources().getDimension(R.dimen.text_small_Size_Medium_Large) : screenSize >= 5.6 && screenSize <= 7.0 ? context.getResources().getDimension(R.dimen.text_small_Size_Medium) : screenSize >= 4.5 && screenSize < 5.6 ? context.getResources().getDimension(R.dimen.text_small_Size_Small) : context.getResources().getDimension(R.dimen.text_small_Size_default);

            } else if (TypeoFTextNeedToBeReturned == 4) {
                return screenSize > 12.5 ? context.getResources().getDimension(R.dimen.text_medium_small_Size_triple_Extra_Large) : screenSize >= 11.1 && screenSize <= 12.5 ? context.getResources().getDimension(R.dimen.text_medium_small_Size_double_Extra_Large) : screenSize > 9.5 && screenSize <= 11.0 ? context.getResources().getDimension(R.dimen.text_medium_small_Size_Extra_Large) : screenSize > 8.5 && screenSize <= 9.5 ? context.getResources().getDimension(R.dimen.text_medium_small_Size_Large) : screenSize > 7.0 && screenSize <= 8.5 ? context.getResources().getDimension(R.dimen.text_medium_small_Size_Medium_Large) : screenSize >= 5.6 && screenSize <= 7.0 ? context.getResources().getDimension(R.dimen.text_medium_small_Size_Medium) : screenSize >= 4.5 && screenSize < 5.6 ? context.getResources().getDimension(R.dimen.text_medium_small_Size_Small) : context.getResources().getDimension(R.dimen.text_medium_small_Size_default);

            } else if (TypeoFTextNeedToBeReturned == -1) {
                return screenSize > 12.5 ? context.getResources().getDimension(R.dimen.heading_text_Size_triple_Extra_Large) : screenSize >= 11.1 &&
                        screenSize <= 12.5 ? context.getResources().getDimension(R.dimen.heading_text_Size_double_Extra_Large) : screenSize > 9.5 && screenSize <= 11.0 ? context.getResources().getDimension(R.dimen.heading_text_Size_Extra_Large) : screenSize > 8.5 && screenSize <= 9.5 ? context.getResources().getDimension(R.dimen.heading_text_Size_Large) : screenSize > 7.0 && screenSize <= 8.5 ? context.getResources().getDimension(R.dimen.heading_text_Size_Medium_Large) : screenSize >= 5.6 && screenSize <= 7.0 ? context.getResources().getDimension(R.dimen.heading_text_Size_Medium) : screenSize >= 4.5 && screenSize < 5.6 ? context.getResources().getDimension(R.dimen.heading_text_Size_Small) : context.getResources().getDimension(R.dimen.heading_text_Size_default);

            } else if (TypeoFTextNeedToBeReturned == -2) {
                return screenSize > 12.5 ? context.getResources().getDimension(R.dimen.large_heading_text_Size_triple_Extra_Large) : screenSize >= 11.1 &&
                        screenSize <= 12.5 ? context.getResources().getDimension(R.dimen.large_heading_text_Size_double_Extra_Large) : screenSize > 9.5 && screenSize <= 11.0 ? context.getResources().getDimension(R.dimen.large_heading_text_Size_Extra_Large) : screenSize > 8.5 && screenSize <= 9.5 ? context.getResources().getDimension(R.dimen.large_heading_text_Size_Large) : screenSize > 7.0 && screenSize <= 8.5 ? context.getResources().getDimension(R.dimen.large_heading_text_Size_Medium_Large) : screenSize >= 5.6 && screenSize <= 7.0 ? context.getResources().getDimension(R.dimen.large_heading_text_Size_Medium) : screenSize >= 4.5 && screenSize < 5.6 ? context.getResources().getDimension(R.dimen.large_heading_text_Size_Small) : context.getResources().getDimension(R.dimen.large_heading_text_Size_default);

            } else {
                return screenSize > 12.5 ? context.getResources().getDimension(R.dimen.text_small_Size_triple_Extra_Large) : screenSize >= 11.1 && screenSize <= 12.5 ? context.getResources().getDimension(R.dimen.text_small_Size_double_Extra_Large) : screenSize > 9.5 && screenSize <= 11.0 ? context.getResources().getDimension(R.dimen.text_small_Size_Extra_Large) : screenSize > 8.5 && screenSize <= 9.5 ? context.getResources().getDimension(R.dimen.text_small_Size_Large) : screenSize > 7.0 && screenSize <= 8.5 ? context.getResources().getDimension(R.dimen.text_small_Size_Medium_Large) : screenSize >= 5.6 && screenSize <= 7.0 ? context.getResources().getDimension(R.dimen.text_small_Size_Medium) : screenSize >= 4.5 && screenSize < 5.6 ? context.getResources().getDimension(R.dimen.text_small_Size_Small) : context.getResources().getDimension(R.dimen.text_small_Size_default);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which I need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which I need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public int convertPixelsToDp(int px, Context context) {

        System.out.println("pIXELS" + px);

        return (int) (px / GlobalClassForFunctions.this.context.getResources().getDisplayMetrics().density);
    }

    // to return screen size of the phone
    public double ScreenSize() {
        System.out.println("ASPECT_RATIO---" + ASPECT_RATIO);
        return SCREEN_SIZE;
    }

    // to return screen height
    public double GetHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) GlobalClassForFunctions.this.context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }


    /**
     * Free the memory by calling garbage collector
     */
    public void freeMemory() {
        try {
            //Runs the finalization methods of any objects pending finalization.
            System.runFinalization();
            //
            // The call System.gc() is effectively equivalent to the call : Runtime.getRuntime().gc()
            //request JVM to run garbage collector
            Runtime.getRuntime().gc();
            // requesting JVM for running Garbage Collector
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getSharedPreferenceInstance(Context mContext) {

        //initializing the shared preferences for storing the access tokens
        if (mSharedPreferences == null) {
            mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        }

        //we have access token in a variable still we are storing that in Shared pref.
        //thats because if this was a real app then access token would have been retrieved by api
        // and i was told to store it in local storage
        SharedPreferences.Editor objEditor = mSharedPreferences.edit();
        objEditor.putString(ACCESS_TOKEN_KEY, ACCESS_TOKEN_VALUE);

        objEditor.apply();

    }

    /**
     * This function helps to get the value from the shared Preferences for the Access Token
     * After getting values stored the values in variables
     */
    public String getAccessToken() {
        try {
            if (mSharedPreferences.contains(ACCESS_TOKEN_KEY)) {
                return mSharedPreferences.getString(ACCESS_TOKEN_KEY, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    public String executeApi(Response<String> res) {
        if (res.isSuccessful()) {
            //convert string to jsonobject
            if (res.body() != null) {
                return res.body();
            }
        }
            /*
                    when Response code = 401 , -> Invalid access Token
                    when Response code = 403 - > no scope found
             */
        else if (res.code() == 400 || res.code() == 403) {
            System.out.println("Response Code == " + res.code());
            assert res.errorBody() != null;
            try {
                System.out.println(res.errorBody().string());
                return res.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(res);
        }
        return "";
    }


    /**
     * Check the jsonobject key presence
     *
     * @param object               json object for the api return
     * @param stringToCheckForNull variable to check for null
     * @return return tRUE or False if the value is null
     */
    public boolean ReturnIfNullOrEmpty(JSONObject object, String stringToCheckForNull) {
        try {
            return !object.isNull(stringToCheckForNull);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This method is used to set properties to the views
     *
     * @param view                  is the view of the widget
     * @param CornerRadiusOfTheView is used to set the corners of the view
     * @param Shape                 is used to set the shape of the view
     *                              Shape ==0  means no shape
     *                              Shape == 1 means OVAL
     */
    public void setConstraintLayoutProperties(View view, int BackgroundColorOfTheView, float CornerRadiusOfTheView, int Shape, int strokeWidth, int i_top_or_bottom, int... StrokeColor) {


        try {
            if (view != null && Shape == 0) {
                GradientDrawable gradientDrawable = new GradientDrawable();
                // i_top_or_bottom ==1 means show corner on the top only
                if (i_top_or_bottom == 0) {
                    gradientDrawable.setCornerRadius(CornerRadiusOfTheView);
                } else if (i_top_or_bottom == 1) {
                    gradientDrawable.setCornerRadii(new float[]{CornerRadiusOfTheView, CornerRadiusOfTheView, CornerRadiusOfTheView, CornerRadiusOfTheView, 0, 0, 0, 0});
                } else if (i_top_or_bottom == 2) {
                    gradientDrawable.setCornerRadii(new float[]{0, 0, 0, 0, CornerRadiusOfTheView, CornerRadiusOfTheView, CornerRadiusOfTheView, CornerRadiusOfTheView});
                }
                for (int Color : StrokeColor) {
                    gradientDrawable.setStroke(strokeWidth, Color);
                }
                gradientDrawable.setColor(BackgroundColorOfTheView);
//                view.setBackgroundDrawable(new RippleDrawable(Objects.requireNonNull(getPressedColorSelector(BackgroundColorOfTheView, context.getResources().getColor(R.color.ripple_color))), gradientDrawable, null));
                view.setBackgroundDrawable(gradientDrawable);
            } else if (view != null) {
                GradientDrawable gradientDrawable = new GradientDrawable();
                // SHape == 1 means Oval Shape
                if (Shape == 1) {
                    gradientDrawable.setShape(GradientDrawable.OVAL);
                }
                for (int Color : StrokeColor) {
                    gradientDrawable.setStroke(strokeWidth, Color);
                }
                gradientDrawable.setColor(BackgroundColorOfTheView);
                view.setBackground(new RippleDrawable(Objects.requireNonNull(getPressedColorSelector(BackgroundColorOfTheView, context.getResources().getColor(R.color.ripple_color))), gradientDrawable, null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to set the size of the Text in the View
     *
     * @param view     is the view to which size is need to be set.
     * @param sizeType is the check for verysmall(edit buttons), small,medium imagesize
     */
    public void setImageViewSize(View view, int sizeType, int OnlyHeightorWidth) {
        /*
        OnlyHeightorWidth = 1 means only width
        OnlyHeightorWidth = 2 means only height
         */
        try {
            if (view instanceof ImageView) {
                int heightandwidth = 0;
                double screenSize = getInstance().ScreenSize();
                if (sizeType == 0) {
                    heightandwidth = (int) (screenSize > 12.5 ? context.getResources().getDimension(R.dimen.verysmall_img_Size_triple_Extra_Large)
                            : screenSize >= 11.1 && screenSize <= 12.5 ? context.getResources().getDimension(R.dimen.verysmall_img_Size_double_Extra_Large)
                            : screenSize > 9.5 && screenSize <= 11.0 ? context.getResources().getDimension(R.dimen.verysmall_img_Size_Extra_Large)
                            : screenSize > 8.5 && screenSize <= 9.5 ? context.getResources().getDimension(R.dimen.verysmall_img_Size_Large)
                            : screenSize > 7.0 && screenSize <= 8.5 ? context.getResources().getDimension(R.dimen.verysmall_img_Size_Medium_Large)
                            : screenSize >= 5.6 && screenSize <= 7.0 ? context.getResources().getDimension(R.dimen.verysmall_img_Size_Medium)
                            : screenSize >= 4.5 && screenSize < 5.6 ? context.getResources().getDimension(R.dimen.verysmall_img_Size_Small)
                            : context.getResources().getDimension(R.dimen.verysmall_img_Size_default));
                } else if (sizeType == 1) {
                    heightandwidth = (int) (screenSize > 12.5 ? context.getResources().getDimension(R.dimen.small_img_Size_triple_Extra_Large)
                            : screenSize >= 11.1 && screenSize <= 12.5 ? context.getResources().getDimension(R.dimen.small_img_Size_double_Extra_Large)
                            : screenSize > 9.5 && screenSize <= 11.0 ? context.getResources().getDimension(R.dimen.small_img_Size_Extra_Large)
                            : screenSize > 8.5 && screenSize <= 9.5 ? context.getResources().getDimension(R.dimen.small_img_Size_Large)
                            : screenSize > 7.0 && screenSize <= 8.5 ? context.getResources().getDimension(R.dimen.small_img_Size_Medium_Large)
                            : screenSize >= 5.6 && screenSize <= 7.0 ? context.getResources().getDimension(R.dimen.small_img_Size_Medium)
                            : screenSize >= 4.5 && screenSize < 5.6 ? context.getResources().getDimension(R.dimen.small_img_Size_Small)
                            : context.getResources().getDimension(R.dimen.small_img_Size_default));
                } else if (sizeType == 2) {
                    heightandwidth = (int) (screenSize > 12.5 ? context.getResources().getDimension(R.dimen.Medium_img_Size_triple_Extra_Large)
                            : screenSize >= 11.1 && screenSize <= 12.5 ? context.getResources().getDimension(R.dimen.Medium_img_Size_double_Extra_Large)
                            : screenSize > 9.5 && screenSize <= 11.0 ? context.getResources().getDimension(R.dimen.Medium_img_Size_Extra_Large)
                            : screenSize > 8.5 && screenSize <= 9.5 ? context.getResources().getDimension(R.dimen.Medium_img_Size_Large)
                            : screenSize > 7.0 && screenSize <= 8.5 ? context.getResources().getDimension(R.dimen.Medium_img_Size_Medium_Large)
                            : screenSize >= 5.6 && screenSize <= 7.0 ? context.getResources().getDimension(R.dimen.Medium_img_Size_Medium)
                            : screenSize >= 4.5 && screenSize < 5.6 ? context.getResources().getDimension(R.dimen.Medium_img_Size_Small)
                            : context.getResources().getDimension(R.dimen.Medium_img_Size_default));
                } else if (sizeType == 3) {
                    heightandwidth = (int) (screenSize > 12.5 ? context.getResources().getDimension(R.dimen.Medium_large_img_Size_triple_Extra_Large)
                            : screenSize >= 11.1 && screenSize <= 12.5 ? context.getResources().getDimension(R.dimen.Medium_large_img_Size_double_Extra_Large)
                            : screenSize > 9.5 && screenSize <= 11.0 ? context.getResources().getDimension(R.dimen.Medium_large_img_Size_Extra_Large)
                            : screenSize > 8.5 && screenSize <= 9.5 ? context.getResources().getDimension(R.dimen.Medium_large_img_Size_Large)
                            : screenSize > 7.0 && screenSize <= 8.5 ? context.getResources().getDimension(R.dimen.Medium_large_img_Size_Medium_Large)
                            : screenSize >= 5.6 && screenSize <= 7.0 ? context.getResources().getDimension(R.dimen.Medium_large_img_Size_Medium)
                            : screenSize >= 4.5 && screenSize < 5.6 ? context.getResources().getDimension(R.dimen.Medium_large_img_Size_Small)
                            : context.getResources().getDimension(R.dimen.Medium_large_img_Size_default));
                } else if (sizeType == 4) {
                    heightandwidth = (int) (screenSize > 12.5 ? context.getResources().getDimension(R.dimen.Medium_Large2_img_Size_triple_Extra_Large)
                            : screenSize >= 11.1 && screenSize <= 12.5 ? context.getResources().getDimension(R.dimen.Medium_Large2_img_Size_double_Extra_Large)
                            : screenSize > 9.5 && screenSize <= 11.0 ? context.getResources().getDimension(R.dimen.Medium_Large2_img_Size_Extra_Large)
                            : screenSize > 8.5 && screenSize <= 9.5 ? context.getResources().getDimension(R.dimen.Medium_Large2_img_Size_Large)
                            : screenSize > 7.0 && screenSize <= 8.5 ? context.getResources().getDimension(R.dimen.Medium_Large2_img_Size_Large)
                            : screenSize >= 5.6 && screenSize <= 7.0 ? context.getResources().getDimension(R.dimen.Medium_Large2_img_Size_Medium)
                            : screenSize >= 4.5 && screenSize < 5.6 ? context.getResources().getDimension(R.dimen.Medium_Large2_img_Size_Small)
                            : context.getResources().getDimension(R.dimen.Medium_Large2_img_Size_default));
                } else if (sizeType == 5) {
                    heightandwidth = (int) (screenSize > 12.5 ? context.getResources().getDimension(R.dimen.Large_img_Size_triple_Extra_Large)
                            : screenSize >= 11.1 && screenSize <= 12.5 ? context.getResources().getDimension(R.dimen.Large_img_Size_double_Extra_Large)
                            : screenSize > 9.5 && screenSize <= 11.0 ? context.getResources().getDimension(R.dimen.Large_img_Size_Extra_Large)
                            : screenSize > 8.5 && screenSize <= 9.5 ? context.getResources().getDimension(R.dimen.Large_img_Size_Large)
                            : screenSize > 7.0 && screenSize <= 8.5 ? context.getResources().getDimension(R.dimen.Large_img_Size_Large)
                            : screenSize >= 5.6 && screenSize <= 7.0 ? context.getResources().getDimension(R.dimen.Large_img_Size_Medium)
                            : screenSize >= 4.5 && screenSize < 5.6 ? context.getResources().getDimension(R.dimen.Large_img_Size_Small)
                            : context.getResources().getDimension(R.dimen.Large_img_Size_default));
                } else if (sizeType == 6) {
                    heightandwidth = (int) (screenSize > 12.5 ? context.getResources().getDimension(R.dimen.Larger_img_Size_triple_Extra_Large)
                            : screenSize >= 11.1 && screenSize <= 12.5 ? context.getResources().getDimension(R.dimen.Larger_img_Size_double_Extra_Large)
                            : screenSize > 9.5 && screenSize <= 11.0 ? context.getResources().getDimension(R.dimen.Larger_img_Size_Extra_Large)
                            : screenSize > 8.5 && screenSize <= 9.5 ? context.getResources().getDimension(R.dimen.Larger_img_Size_Large)
                            : screenSize > 7.0 && screenSize <= 8.5 ? context.getResources().getDimension(R.dimen.Larger_img_Size_Large)
                            : screenSize >= 5.6 && screenSize <= 7.0 ? context.getResources().getDimension(R.dimen.Larger_img_Size_Medium)
                            : screenSize >= 4.5 && screenSize < 5.6 ? context.getResources().getDimension(R.dimen.Larger_img_Size_Small)
                            : context.getResources().getDimension(R.dimen.Larger_img_Size_default));
                } else if (sizeType == 7) {
                    heightandwidth = (int) (screenSize > 12.5 ? context.getResources().getDimension(R.dimen.Larger7_img_Size_triple_Extra_Large)
                            : screenSize >= 11.1 && screenSize <= 12.5 ? context.getResources().getDimension(R.dimen.Larger7_img_Size_double_Extra_Large)
                            : screenSize > 9.5 && screenSize <= 11.0 ? context.getResources().getDimension(R.dimen.Larger7_img_Size_Extra_Large)
                            : screenSize > 8.5 && screenSize <= 9.5 ? context.getResources().getDimension(R.dimen.Larger7_img_Size_Large)
                            : screenSize > 7.0 && screenSize <= 8.5 ? context.getResources().getDimension(R.dimen.Larger7_img_Size_Large)
                            : screenSize >= 5.6 && screenSize <= 7.0 ? context.getResources().getDimension(R.dimen.Larger7_img_Size_Medium)
                            : screenSize >= 4.5 && screenSize < 5.6 ? context.getResources().getDimension(R.dimen.Larger7_img_Size_Small)
                            : context.getResources().getDimension(R.dimen.Larger_img_Size_default));
                } else if (sizeType == 8) {
                    heightandwidth = (int) (screenSize > 12.5 ? context.getResources().getDimension(R.dimen.Larger8_img_Size_triple_Extra_Large)
                            : screenSize >= 11.1 && screenSize <= 12.5 ? context.getResources().getDimension(R.dimen.Larger8_img_Size_double_Extra_Large)
                            : screenSize > 9.5 && screenSize <= 11.0 ? context.getResources().getDimension(R.dimen.Larger8_img_Size_Extra_Large)
                            : screenSize > 8.5 && screenSize <= 9.5 ? context.getResources().getDimension(R.dimen.Larger8_img_Size_Large)
                            : screenSize > 7.0 && screenSize <= 8.5 ? context.getResources().getDimension(R.dimen.Larger8_img_Size_Large)
                            : screenSize >= 5.6 && screenSize <= 7.0 ? context.getResources().getDimension(R.dimen.Larger8_img_Size_Medium)
                            : screenSize >= 4.5 && screenSize < 5.6 ? context.getResources().getDimension(R.dimen.Larger8_img_Size_Small)
                            : context.getResources().getDimension(R.dimen.Large_img_Size_default));
                }

//                OnlyHeightorWidth means 2 only height
                if (OnlyHeightorWidth == 2) {
                    view.getLayoutParams().height = heightandwidth;
                }
//                OnlyHeightorWidth means 1 only width
                else if (heightandwidth == 1) {
                    view.getLayoutParams().width = heightandwidth;
                } else {
                    view.getLayoutParams().height = heightandwidth;
                    view.getLayoutParams().width = heightandwidth;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Set the color or the states defined
     * state_pressed,state_focused and state_activated - given Pressed color
     *
     * @param normalColor
     * @param pressedColor
     * @return - ColorStateList
     */
    public static ColorStateList getPressedColorSelector(int normalColor, int pressedColor) {
        try {
            return new ColorStateList(
                    new int[][]
                            {
                                    new int[]{android.R.attr.state_pressed},
                                    new int[]{android.R.attr.state_focused},
                                    new int[]{android.R.attr.state_activated},
                                    new int[]{}
                            },
                    new int[]
                            {
                                    pressedColor,
                                    pressedColor,
                                    pressedColor,
                                    normalColor
                            }
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
