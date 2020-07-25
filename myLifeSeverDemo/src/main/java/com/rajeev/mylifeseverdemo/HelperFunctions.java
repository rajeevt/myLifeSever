package com.omak.growyucrm.OmakHelpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.omak.growyucrm.BuildConfig;
import com.omak.growyucrm.Models.AlmightMainJSonModel;
import com.omak.growyucrm.Models.RealmCurrentMessage;
import com.omak.growyucrm.Models.RealmLead;
import com.omak.growyucrm.Models.ResponseContactDeleteModel;
import com.omak.growyucrm.Models.ResponseDeleteMessageModel;
import com.omak.growyucrm.Models.ResponseGeneralModel;
import com.omak.growyucrm.Models.ResponseGetContactModel;
import com.omak.growyucrm.Models.ResponseSubscriptionMainModel;
import com.omak.growyucrm.Models.ResponseTeamMembersDeleteModel;
import com.omak.growyucrm.Models.SubscriptionDataModel;
import com.omak.growyucrm.R;
import com.omak.growyucrm.Retrofit.ApiClient;
import com.omak.growyucrm.Retrofit.ApiHelper;
import com.omak.growyucrm.Retrofit.ApiInterface;
import com.razorpay.Checkout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HelperFunctions {
    private static final int MY_PERMISSION_REQUEST_CODE = 1001;
    Context context;
    Activity activity;
    private Realm realm;
    private RealmHelpers realmHelpers;

    public HelperFunctions() {
//        realm = HelperFunctions.getRealm("messages", context);
    }

    public HelperFunctions(Activity ac, String contactName, String contactNumber, String contactEmail) {
        activity = ac;
        SaveContact(contactName, contactNumber, contactEmail);
    }

    public static void setCrashlytics(String message, Context context) {
        Crashlytics.setUserIdentifier(PreferencesHelper.getPrefString("id", "currentUser", context));
        Crashlytics.log(message);
    }

    /*
        Get individual key from an object
        Returns:
            Null when object is null OR object is not a JSONObject
            String - whey key is available
            emptyString - when key does not exist
     */

    /*
        Get individual key from an object
        Returns:
            Null when object is null OR object is not a JSONObject
            String - whey key is available
            emptyString - when key does not exist
     */

    public static Spinner setSimpleAdapter(Context context, String[] options, Spinner spinner, Integer chosenPosition) {
        ArrayAdapter leadTypesAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, options);
        leadTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(leadTypesAdapter);
        spinner.setSelection(chosenPosition);

        return spinner;
    }


    /* public static JSONObject get_message(String key, Context context) {

        SharedPreferences preferences = android.preference.PreferenceManager2.getDefaultSharedPreferences(context);
        String MessageData = preferences.getString("Message", "");

        try {
            //HelperFunctions.theLogger("FromPreference", login_data);
            JSONObject jsonObj = new JSONObject(MessageData);
            //HelperFunctions.theLogger("jsonObb of login_data", "" + jsonObj);

            String data = jsonObj.getString(key);
            //HelperFunctions.theLogger("Test myData", "" + data);

            JSONObject current_user = new JSONObject(data);

            return current_user;

        } catch (final JSONException e) {
            HelperFunctions.theLogger("JSONException", "Json parsing error: " + e.getMessage());
            return null;
        }
    }
*/

    public static void exitAlertDialog(Context context) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(context.getString(R.string.app_name));
        builder.setIcon(R.drawable.calllogo);
        builder.setMessage(context.getString(R.string.dailog_message));
        // Set the positive button
        builder.setPositiveButton(context.getString(R.string.yes_exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Activity activity = ApplicationHelpers.getActivity(context);
                ActivityCompat.finishAffinity(activity);
                activity.finish();
            }
        });
        // Set the negative button
        builder.setNegativeButton(context.getString(R.string.dailog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
        setPrimaryButton(context, dialog.getButton(AlertDialog.BUTTON_NEGATIVE));
        setCancelButton(context, dialog.getButton(AlertDialog.BUTTON_POSITIVE));
    }

    /*Method For Print Log */
    public static void theLogger(String tag, String message) {
        // ToDo: Think on creating different version of theLogger [for info, error, and other verbose]
        /*if (BuildConfig.DEBUG)*/
        Log.e(tag, message);
        /* if(BuildConfig.DEBUG) Timber.e(tag + ": " + message);*/
    }

    /*Method For Print Toast */
    public static void theToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static String get_string(JSONObject obj, String key) {
        try {
            return obj.getString(key);
        } catch (final JSONException e) {
            HelperFunctions.theLogger("JSONException", "Json parsing error: " + e.getMessage());
            return null;
        }
    }

    /* GEt Date Method*/
    public static Date convertString2Date(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getFormattedDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        String dateString = formatter.format(date);

        return dateString;
    }

    public static String getFormattedDateFromString(String dateString) {
        Date date = convertString2Date(dateString);
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy HH:mm:ss");
        return formatter.format(date);
    }

    public static String getFormattedDateFromString(String dateString, String format) {
        Date date = convertString2Date(dateString);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static void create_subscription(final Context context, final String plan) {
        theLogger("RealmPlan", "RealmPlan HEre: " + plan);
        JSONObject current_user = PreferencesHelper.get_pref_data("data", context);
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(true);
        dialog.setMessage(context.getString(R.string.please_wait_message));


        ////////  Api interface here////////////
        ApiInterface apiService = new ApiClient().getClient().create(ApiInterface.class);
        AlmightMainJSonModel task = new ApiClient().prepareMainJsModel("generic", context);

        theLogger("RealmPlan", "RealmPlan HEre Again: " + plan);
        task.setPlan(plan);
        task.setBusinessId(get_string(current_user, "business_id"));
        task.setUser_id(get_string(current_user, "id"));

        HelperFunctions.theLogger("Add", "Subscription: " + new Gson().toJson(task));

        Call<ResponseSubscriptionMainModel> call = apiService.CreateSubscription(task);
        ApiHelper.enqueueWithRetry(context, call, new Callback<ResponseSubscriptionMainModel>() {
            @Override
            public void onResponse(Call<ResponseSubscriptionMainModel> call, Response<ResponseSubscriptionMainModel> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    HelperFunctions.theLogger("Success", new Gson().toJson(response.body()));
                    if (response.body().getSuccess()) {

                        if (plan != "free") {
                            SubscriptionDataModel subscription = response.body().getData();
                            startPayment(response.body().getData(), context, plan);
                        } else {

                        }
                    } else {
                        HelperFunctions.theLogger("Failure", new Gson().toJson(response.body()));
                        //Toast.makeText(context, "Please check Login" + new Gson().toJson(response.body()), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseSubscriptionMainModel> call, Throwable t) {
                dialog.dismiss();
                HelperFunctions.theLogger("API-Failure", "" + t);
                // RealmCallLogModel error here since request failed
                //Toast.makeText(contextThis, "Something failed, please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void startPayment(SubscriptionDataModel subscription, Context context, String plan) {  /**   * Instantiate Checkout   */

        JSONObject current_user = PreferencesHelper.get_pref_data("data", context);
        Checkout checkout = new Checkout();
        /**   * Set your logo here   */
        checkout.setImage(R.drawable.calllogo);
        /**   * Reference to current activity   */
        //final ChoosePlanBasic planActivity = activity;

        /**   * Pass your payment options to the Razorpay Checkout as a JSONObject   */
        try {
            JSONObject options = new JSONObject();
            options.put("subscription_id", subscription.getSubscription().getId());
            //options.put("order_id", "log_id_" + subscription.getSubscription().getLog_id());
            /**     * Merchant Name     * eg: ACME Corp || HasGeek etc.     */
            //options.put("name", "CallLoger");
            /**     * Description can be anything     * eg: Order #123123     *     Invoice Payment     *     etc.     */
            //options.put("description", "Monthly RealmPlan");
            //options.put("email", get_string(current_user, "useremail"));
            //options.put("contact", get_string(current_user, "userphone"));

            JSONObject notes = new JSONObject();
            notes.put("user_id", get_string(current_user, "id"));
            notes.put("addtional_key", "myKey");
            options.put("notes", notes);

            theLogger("Options", "" + options);

            checkout.open((Activity) context, options);
        } catch (Exception e) {
            HelperFunctions.theLogger("Error", "Error in starting Razorpay Checkout");
        }
    }

    public static void log(String Title, String message) {
        Log.e(Title, message);
    }

    public static void tost(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void serviceForGetAllLeadsData(Context context, String message, boolean forceUpdate) {
        Realm realm;
        RealmHelpers realmHelpers;
        realmHelpers = new RealmHelpers(context);
        realm = HelperFunctions.getRealm("messages", context);

        ApiInterface apiService = new ApiClient().getClient().create(ApiInterface.class);
        final AlmightMainJSonModel task = new ApiClient().prepareMainJsModel("ap-user", context);

        Call<ResponseGetContactModel> call = apiService.GetLeadList(task);
        ApiHelper.enqueueWithRetry(context, call, new Callback<ResponseGetContactModel>() {
            @Override
            public void onResponse(Call<ResponseGetContactModel> call, Response<ResponseGetContactModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_LONG).show();
                    final List<RealmLead> listRealmLeads = response.body().getData().getLogs();

                    RealmQuery<RealmLead> query = realm.where(RealmLead.class);
                    final RealmResults<RealmLead> realmLeadsRealmResults = query.findAll();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realmLeadsRealmResults.deleteAllFromRealm();
                            realm.insert(listRealmLeads);
                        }
                    });

                    realmHelpers.setBooleanFlag(RealmHelpers.areContactsUpdated, true);
                    //displayFromRealm("");
                }
            }

            @Override
            public void onFailure(Call<ResponseGetContactModel> call, Throwable t) {
            }
        });
    }

    /*Get Device Name Here*/
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            HelperFunctions.theLogger("My Device Name ", "IS" + model);
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    public static JSONObject addApiPair(String key, String value) {
        JSONObject keyValuePair = new JSONObject();
        try {
            keyValuePair.put("key", key);
            keyValuePair.put("value", value);
        } catch (JSONException e) {

        }
        return keyValuePair;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    /*Realm Configuration */
    public static Realm getRealm(String whichRealm, Context applicationContext) {
        Realm.init(applicationContext);

        switch (whichRealm) {
            case "messages":
                RealmConfiguration config = new RealmConfiguration.Builder()
                        .name("messages.realm")
                        .deleteRealmIfMigrationNeeded()
                        //.schemaVersion(1)
                        //.migration(new MyRealMigration())
                        .build();

                Realm.setDefaultConfiguration(config);
                break;
        }
        return Realm.getDefaultInstance();
    }

    // fuction for hidekey board in activity
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void setPrimaryButton(Context context, Button button) {
        button.setTextColor(Color.parseColor("#FFFFFF"));
        button.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
    }

    public static void setCancelButton(Context context, Button button) {
        button.setTextColor(context.getResources().getColor(R.color.grey));
    }

    //method for set uppre cap words useing for company name,user name
    public static String capitalizeFirstLetter(String str) {
        if (str.equals(null) || str.isEmpty()) {
            return str;
        }

        // Create a char array of given String
        char[] ch = str.toCharArray();
        for (int i = 0; i < str.length(); i++) {

            // If first character of a word is found
            if (i == 0 && ch[i] != ' ' ||
                    ch[i] != ' ' && ch[i - 1] == ' ') {

                // If it is in lower-case
                if (ch[i] >= 'a' && ch[i] <= 'z') {

                    // Convert into Upper-case
                    ch[i] = (char) (ch[i] - 'a' + 'A');
                }
            }

            // If apart from first character
            // Any one is in Upper-case
            else if (ch[i] >= 'A' && ch[i] <= 'Z')

                // Convert into Lower-Case
                ch[i] = (char) (ch[i] + 'a' - 'A');
        }

        // Convert the char array to equivalent String
        String st = new String(ch);
        return st;

    }

    //// Coustom Alert for delete message templets
    public static void coustomAlertsDeleteData(final Context context, final String... data) {
        String title = data[0];
        String message = data[1];
        final String service = data[2];
        final AlertDialog alertDialog;
        String serviceType = data[3];

        for (int i = 0; i < data.length; i++) {
            HelperFunctions.log("deleteMessageTemp", data[i]);
        }

        TextView tvAlertMessageTitle, tvAlertsMessageBody;
        Button btAlertsDecline, btAlertsJoin;
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        view = layoutInflater.inflate(R.layout.common_alert_layout, null);
        builder.setView(view);
        tvAlertMessageTitle = view.findViewById(R.id.tvAlertMessageTitle);
        tvAlertsMessageBody = view.findViewById(R.id.tvAlertsMessageBody);
        btAlertsDecline = view.findViewById(R.id.btAlertsDecline);
        btAlertsJoin = view.findViewById(R.id.btAlertsJoin);
        tvAlertMessageTitle.setText(title);
        tvAlertsMessageBody.setText(message);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        btAlertsJoin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (service.equals("DeleteMessageService")) {
                            deleteMessageTempService(context, data[3]);
                        } else if (service.equals("DeleteUserService")) {
                            deleteTeamMembers(context, data[3]);
                        } else if (service.equals("DeleteLeadService")) {
                            deleteLead(context, data[3]);
                        }
                        alertDialog.dismiss();
                    }
                });
        btAlertsDecline.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
    }

    private static void deleteMessageTempService(Context context, String message_id) {

        final AlmightMainJSonModel task = new ApiClient().prepareMainJsModel("ap-user", context);

        task.setUser_id(PreferencesHelper.getPrefString("id", "currentUser", context));
        task.setMsg_id(message_id);
        ApiInterface apiService = new ApiClient().getClient().create(ApiInterface.class);
        Call<ResponseDeleteMessageModel> call = apiService.DeleteMessage(task);
        ApiHelper.enqueueWithRetry(context, call, new Callback<ResponseDeleteMessageModel>() {
            @Override
            public void onResponse(Call<ResponseDeleteMessageModel> call, Response<ResponseDeleteMessageModel> response) {

                HelperFunctions.tost(context, response.body().getMessage());

                if (response.body().getSuccess()) {
                    new RealmHelpers(context).deleteFromRealm("id", message_id, RealmCurrentMessage.class);
                    new RealmHelpers(context).setBooleanFlag(RealmHelpers.areMessagesUpdated, false);

                    Activity activity = ApplicationHelpers.getActivity(context);
                    activity.onBackPressed();
                }

            }

            @Override
            public void onFailure(Call<ResponseDeleteMessageModel> call, Throwable t) {

            }

        });
    }

    //delete member api service
    public static void deleteTeamMembers(Context context, String teamMember_id) {

        ApiInterface apiService = new ApiClient().getClient().create(ApiInterface.class);
        AlmightMainJSonModel task = new ApiClient().prepareMainJsModel("ap-admin", context);
        task.setUser_id(teamMember_id);

        HelperFunctions.printTask("Team Members Delete", task);

        Call<ResponseTeamMembersDeleteModel> call = apiService.TeamMembersDelete(task);
        ApiHelper.enqueueWithRetry(context, call, new Callback<ResponseTeamMembersDeleteModel>() {
            @Override
            public void onResponse(Call<ResponseTeamMembersDeleteModel> call, Response<ResponseTeamMembersDeleteModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "" + new Gson().toJson(response.body().getMessage()), Toast.LENGTH_LONG).show();
                    if (response.body().getSuccess()) {
                        new RealmHelpers(context).setBooleanFlag(RealmHelpers.teamMembersDashboard, true);

                        Activity activity = ApplicationHelpers.getActivity(context);
                        activity.finish();
                        activity.onBackPressed();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseTeamMembersDeleteModel> call, Throwable t) {
            }
        });

    }

    //Delete Lead Here ..
    public static void deleteLead(Context context, String leadId) {
        ApiInterface apiService = new ApiClient().getClient().create(ApiInterface.class);
        AlmightMainJSonModel task = new ApiClient().prepareMainJsModel("ap-admin", context);
        task.setLead_id(leadId);

        HelperFunctions.printTask("Lead Delete", task);

        Call<ResponseContactDeleteModel> call = apiService.LeadDelete(task);
        ApiHelper.enqueueWithRetry(context, call, new Callback<ResponseContactDeleteModel>() {
            @Override
            public void onResponse(Call<ResponseContactDeleteModel> call, Response<ResponseContactDeleteModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "" + new Gson().toJson(response.body().getMessage()), Toast.LENGTH_LONG).show();
                    if (response.body().getSuccess()) {
                        new RealmHelpers(context).deleteFromRealm("id", leadId, RealmLead.class);
                        Activity activity = ApplicationHelpers.getActivity(context);
                        activity.finish();
                        activity.onBackPressed();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseContactDeleteModel> call, Throwable t) {
            }
        });

    }

    public static void logoutAlertDialog(Context context) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(context.getString(R.string.app_name));
        builder.setIcon(R.drawable.calllogo);
        builder.setMessage(context.getString(R.string.confirm_logout));
        builder.setPositiveButton(context.getString(R.string.yes_logout), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                serviceLogout(context);
            }
        });
        builder.setNegativeButton(context.getString(R.string.dailog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
        setPrimaryButton(context, dialog.getButton(AlertDialog.BUTTON_NEGATIVE));
        setCancelButton(context, dialog.getButton(AlertDialog.BUTTON_POSITIVE));

    }

    private static void serviceLogout(Context context) {

        ApiInterface apiService = new ApiClient().getClient().create(ApiInterface.class);
        AlmightMainJSonModel task = new ApiClient().prepareMainJsModel("ap-user", context);
        HelperFunctions.theLogger("Logout", "" + new Gson().toJson(task));

        Call<ResponseGeneralModel> call = apiService.UsersLogout(task);
        ApiHelper.enqueueWithRetry(context, call, new Callback<ResponseGeneralModel>() {
            @Override
            public void onResponse(Call<ResponseGeneralModel> call, final Response<ResponseGeneralModel> response) {

                if (response.body().getSuccess().equals(true)) {
                    Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_LONG).show();
                    AccessHelpers.actionLogout(context);

                    Activity activity = ApplicationHelpers.getActivity(context);
                    activity.finish();
                } else {
                    Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
               /* if (!response.body().getSuccess()) {
                    AccessHelpers.actionLogout(getApplicationContext());
                    finish();
                }*/
            }

            @Override
            public void onFailure(Call<ResponseGeneralModel> call, Throwable t) {

            }
        });
    }

    public static void sendEmailsMethods(Context context, String ShareData) {
        String mailto = "&body=" + Uri.encode(ShareData);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));

        try {
            context.startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            //TODO: Handle case where no email app is available
        }
    }

    public static void shareData(Context context, String ShareData) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, ShareData);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        context.startActivity(shareIntent);
    }

    public static void openEmailIntent(Context context, String businessid, String headerReceiver) {
        Intent Email = new Intent(Intent.ACTION_SEND);
        Email.setType("text/email");
        Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@growyu.com"});
        Email.putExtra(Intent.EXTRA_SUBJECT, businessid);
        Email.putExtra(Intent.EXTRA_TEXT, headerReceiver);
        context.startActivity(Intent.createChooser(Email, headerReceiver));
    }

    public static void getAllLeadsDataService(Context context) {
        Realm realm = HelperFunctions.getRealm("messages", context);
        ApiInterface apiService = new ApiClient().getClient().create(ApiInterface.class);
        final AlmightMainJSonModel task = new ApiClient().prepareMainJsModel("ap-user", context);

        Call<ResponseGetContactModel> call = apiService.GetLeadList(task);
        ApiHelper.enqueueWithRetry(context, call, new Callback<ResponseGetContactModel>() {
            @Override
            public void onResponse(Call<ResponseGetContactModel> call, Response<ResponseGetContactModel> response) {
                if (response.isSuccessful()) {


                    Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_LONG).show();
                    final List<RealmLead> listRealmLeads = response.body().getData().getLogs();

                    RealmQuery<RealmLead> query = realm.where(RealmLead.class);
                    final RealmResults<RealmLead> realmLeadsRealmResults = query.findAll();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realmLeadsRealmResults.deleteAllFromRealm();
                            realm.insert(listRealmLeads);
                        }
                    });

                    new RealmHelpers(context).setBooleanFlag(RealmHelpers.areContactsUpdated, true);
                }
            }

            @Override
            public void onFailure(Call<ResponseGetContactModel> call, Throwable t) {
            }
        });
    }

    // method for check website is valid or not
    public static boolean isWebsiteValid(TextInputEditText tietWebsite) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(tietWebsite.getText().toString().toLowerCase());
        return m.matches();
    }

    // check mail id is vaild or not
    public static boolean isEmailValid(TextInputEditText text) {
        if (isNullOrEmpty(text.getText().toString().trim())) return false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text.getText().toString().trim());
        if (!matcher.matches()) {
            text.setError("please enter valid email id.");
        }
        return matcher.matches();
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    // method for open intent for whatsapp with phone no. and message body
    public static void sendMessageToUseOnWhatApp(Context context, TextInputEditText tiedCallerId, TextInputEditText tietMessageBody) {
        try {
            String trimToNumner = tiedCallerId.getText().toString().trim(); //10 digit number
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.putExtra(Intent.EXTRA_TEXT, "");
            intent.setType("text/rtf");
            intent.setData(Uri.parse("https://wa.me/" + trimToNumner + "/?text=" + tietMessageBody.getText().toString().trim()));
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Message failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // method for open sms intent with message id and message body
    public static void openSmsIntent(Context context, TextInputEditText tiedCallerId, TextInputEditText tietMessageBody) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.setData(Uri.parse("smsto:" + tiedCallerId.getText())); // This ensures only SMS apps respond
        intent.putExtra("sms_body", tietMessageBody.getText().toString().trim());
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    // method for install update form helper class
    public static void installApk(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        // Judge version is greater than or equal to 7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "net.csdn.blog.ruancoder.fileprovider" That is authorities configured in the manifest file
            data = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
            // Apply a temporary authorization to the target
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(file);
        }

        intent.setDataAndType(data, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void printTask(String tag, AlmightMainJSonModel task) {
        theLogger(tag, new Gson().toJson(task));
    }

    // Save contact On Contact List
    public void SaveContact(String contactName, String contactNumber, String contactEmail) {
        Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        contactIntent
                .putExtra(ContactsContract.Intents.Insert.NAME, contactName)
                .putExtra(ContactsContract.Intents.Insert.PHONE, contactNumber)
                .putExtra(ContactsContract.Intents.Insert.EMAIL, contactEmail);

        activity.startActivityForResult(contactIntent, 1);
    }

    public String getDate() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        return date;
    }

    public String dateDifference(String dateStart, String dateStop) {

        //dateStart = "01/14/2012 09:29:58";
        //dateStop = "01/15/2012 10:31:48";

        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            System.out.print(diffDays + " days, ");
            System.out.print(diffHours + " hours, ");
            System.out.print(diffMinutes + " minutes, ");
            System.out.print(diffSeconds + " seconds.");
            return Long.toString(diffSeconds);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String setupTimeZoneValue() {
        TimeZone tz = TimeZone.getDefault();
        return getCurrentTimezoneOffset() + "/" + tz.getRawOffset();
    }

    public String getCurrentTimezoneOffset() {
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());

        String offset = String.format(Locale.getDefault(), "%02d:%02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
        offset = "GMT" + (offsetInMillis >= 0 ? "+" : "-") + offset;

        return offset + "/" + offsetInMillis;
    }

    public String convertUnix2Date(Long time) {
        return DateFormat.format("dd-MM-yyyy hh:mm:ss", time).toString();
    }

    // hide keyboard after it use
    public void hidekeyboard(Context context) {
     /*   InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
 */
    }

    public void deleteMessageTemp(String messageId, Context context) {
        RealmQuery<RealmCurrentMessage> query = realm.where(RealmCurrentMessage.class);
        final RealmCurrentMessage realmSingleProjectModel = query.equalTo("id", messageId, Case.INSENSITIVE).findFirst();

        if (realmSingleProjectModel != null) {
            realm.executeTransaction(
                    new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realmSingleProjectModel.deleteFromRealm();
                        }
                    });
        }
    }
}
