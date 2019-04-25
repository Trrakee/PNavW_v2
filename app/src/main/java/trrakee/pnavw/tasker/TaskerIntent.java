package trrakee.pnavw.tasker;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.os.Process;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class TaskerIntent extends Intent {

    // 3 Tasker versions
    private final static String TASKER_PACKAGE = "net.dinglisch.android.tasker";
    private final static String TASKER_PACKAGE_MARKET = TASKER_PACKAGE + "ft.";
    private final static String TASKER_PACKAGE_CUPCAKE = TASKER_PACKAGE + "cupcake";

    // Play Store download URLs
    private final static String MARKET_DOWNLOAD_URL_PREFIX = "market://details?id=";
    // Intent actions
    private final static String ACTION_TASK = TASKER_PACKAGE + ".ACTION_TASK";
    private final static String ACTION_TASK_COMPLETE = TASKER_PACKAGE + ".ACTION_TASK_COMPLETE";
    private final static String ACTION_TASK_SELECT = TASKER_PACKAGE + ".ACTION_TASK_SELECT";
    // Intent parameters
    private final static String EXTRA_ACTION_INDEX_PREFIX = "action";
    private final static String TASK_NAME_DATA_SCHEME = "task";
    private final static String EXTRA_TASK_NAME = "task_name";
    private final static String EXTRA_TASK_PRIORITY = "task_priority";
    public final static String EXTRA_SUCCESS_FLAG = "success";
    private final static String EXTRA_VAR_NAMES_LIST = "varNames";
    private final static String EXTRA_VAR_VALUES_LIST = "varValues";
    public final static String EXTRA_TASK_OUTPUT = "output";
    // Content provider columns
    private static final String PROVIDER_COL_NAME_EXTERNAL_ACCESS = "ext_access";
    private static final String PROVIDER_COL_NAME_ENABLED = "enabled";
    // DEPRECATED, use EXTRA_VAR_NAMES_LIST, EXTRA_VAR_VALUES_LIST
    public final static String EXTRA_PARAM_LIST = "params";
    private final static String TASK_ID_SCHEME = "id";
    public final static String DEFAULT_ENCRYPTION_KEY = "default";
    public final static String ENCRYPTED_AFFIX = "tec";

    // Intent data
    private final static int MAX_NO_ARGS = 10;

    // For particular actions
    // Bundle keys
    // Only useful for Tasker
    private final static String ACTION_CODE = "action";
    private final static String APP_ARG_PREFIX = "app:";
    public final static String ICON_ARG_PREFIX = "icn:";
    private final static String ARG_INDEX_PREFIX = "arg:";
    private static final String PARAM_VAR_NAME_PREFIX = "par";
    private final static String EXTRA_OPEN_PREFS_TAB_NO = "tno";
    private final static String TASKER_MARKET_URL = MARKET_DOWNLOAD_URL_PREFIX + TASKER_PACKAGE_MARKET;
    private final static String TASKER_MARKET_URL_CUPCAKE = MARKET_DOWNLOAD_URL_PREFIX + TASKER_PACKAGE_CUPCAKE;
    // Direct-purchase version
    private final static String TASKER_DOWNLOAD_URL = "http://tasker.dinglisch.net/download.html";
    // Misc
    private final static String PERMISSION_RUN_TASKS = TASKER_PACKAGE + ".PERMISSION_RUN_TASKS";
    private final static String ACTION_OPEN_PREFS = TASKER_PACKAGE + ".ACTION_OPEN_PREFS";
    private final static int MISC_PREFS_TAB_NO = 3; // 0 based

    // To query whether Tasker is enabled and external access is enabled
    private final static String TASKER_PREFS_URI = "content://" + TASKER_PACKAGE + "/prefs";

    private final static int CUPCAKE_SDK_VERSION = 3;
    private final static String TAG = "TaskerIntent";

    // -------------------------- PRIVATE VARS ---------------------------- //
    private final static String EXTRA_INTENT_VERSION_NUMBER = "version_number";
    private final static String INTENT_VERSION_NUMBER = "1.2.2";
    // Inclusive values
    private final static int MIN_PRIORITY = 0;
    private final static int MAX_PRIORITY = 10;
    // For generating random names
    private static Random rand = new Random();
    // Tracking state
    private int actionCount = 0;
    private int argCount;

    public TaskerIntent() {
        super(ACTION_TASK);
        setRandomData();
        putMetaExtras(getRandomString());
    }

    // -------------------------- PUBLIC METHODS ---------------------------- //

    public TaskerIntent(String taskName) {
        super(ACTION_TASK);
        setRandomData();
        putMetaExtras(taskName);
    }

    public static int getMaxPriority() {
        return MAX_PRIORITY;
    }

    // Tasker has different package names for Play Store and non- versions
    // for historical reasons

    private static boolean validatePriority(int pri) {
        return true;
    }

    // test we can send a TaskerIntent to Tasker
    // use *before* sending an intent
    // still need to test the *result after* sending intent

    private static String getInstalledTaskerPackage(Context context) {

        String foundPackage = null;

        try {
            context.getPackageManager().getPackageInfo(TASKER_PACKAGE, 0);
            foundPackage = TASKER_PACKAGE;
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        try {
            context.getPackageManager().getPackageInfo(TASKER_PACKAGE_MARKET, 0);
            foundPackage = TASKER_PACKAGE_MARKET;
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        return foundPackage;
    }

    // Check if Tasker installed

    public static Status testStatus(Context c) {

        Status result;

        if (!taskerInstalled(c))
            result = Status.NotInstalled;
        else if (!havePermission(c))
            result = Status.NoPermission;
        else if (!TaskerIntent.prefSet(c, PROVIDER_COL_NAME_ENABLED))
            result = Status.NotEnabled;
        else if (!TaskerIntent.prefSet(c, PROVIDER_COL_NAME_EXTERNAL_ACCESS))
            result = Status.AccessBlocked;
        else if (!new TaskerIntent("").receiverExists(c))
            result = Status.NoReceiver;
        else
            result = Status.OK;

        return result;
    }

    private static boolean taskerInstalled(Context context) {
        return (getInstalledTaskerPackage(context) != null);
    }

    // Use with startActivity to retrieve Tasker from Android market
    public static Intent getTaskerInstallIntent(boolean marketFlag) {

        return new Intent(Intent.ACTION_VIEW, Uri.parse(marketFlag ? ((SDKVersion() == CUPCAKE_SDK_VERSION) ? TASKER_MARKET_URL_CUPCAKE : TASKER_MARKET_URL) : TASKER_DOWNLOAD_URL));
    }

    private static int SDKVersion() {
        try {
            Field f = android.os.Build.VERSION.class.getField("SDK_INT");
            return f.getInt(null);
        } catch (Exception e) {
            return CUPCAKE_SDK_VERSION;
        }
    }

    private static IntentFilter getCompletionFilter(String taskName) {

        IntentFilter filter = new IntentFilter(TaskerIntent.ACTION_TASK_COMPLETE);

        filter.addDataScheme(TASK_NAME_DATA_SCHEME);
        filter.addDataPath(taskName, PatternMatcher.PATTERN_LITERAL);

        return filter;
    }

    public static Intent getTaskSelectIntent() {
        return new Intent(ACTION_TASK_SELECT).setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
    }

    private static boolean havePermission(Context c) {
        return c.checkPermission(PERMISSION_RUN_TASKS, Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED;
    }

    // ------------------------------------- INSTANCE METHODS ----------------------------- //

    public static Intent getExternalAccessPrefsIntent() {
        return new Intent(ACTION_OPEN_PREFS).putExtra(EXTRA_OPEN_PREFS_TAB_NO, MISC_PREFS_TAB_NO);
    }

    private static boolean prefSet(Context context, String col) {

        String[] proj = new String[]{col};

        Cursor c = context.getContentResolver().query(Uri.parse(TASKER_PREFS_URI), proj, null, null, null);

        boolean acceptingFlag = false;

        if (c == null)
            Log.w(TAG, "no cursor for " + TASKER_PREFS_URI);
        else {
            c.moveToFirst();

            if (Boolean.TRUE.toString().equals(c.getString(0)))
                acceptingFlag = true;

            c.close();
        }

        return acceptingFlag;
    }

    public TaskerIntent setTaskPriority(int priority) {

        if (validatePriority(priority))
            putExtra(EXTRA_TASK_PRIORITY, priority);
        else
            Log.e(TAG, "priority out of range: " + MIN_PRIORITY + ":" + MAX_PRIORITY);

        return this;
    }

    // Sets subsequently %par1, %par2 etc
    public TaskerIntent addParameter(String value) {

        int index = 1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.requireNonNull(getExtras()).containsKey(EXTRA_VAR_NAMES_LIST))
                index = Objects.requireNonNull(getExtras().getStringArrayList(EXTRA_VAR_NAMES_LIST)).size() + 1;
        }

        Log.d(TAG, "index: " + index);

        addLocalVariable("%" + PARAM_VAR_NAME_PREFIX + index, value);

        return this;
    }

    // Arbitrary specification of (local) variable names and values
    private void addLocalVariable(String name, String value) {

        ArrayList<String> names, values;

        if (hasExtra(EXTRA_VAR_NAMES_LIST)) {
            names = getStringArrayListExtra(EXTRA_VAR_NAMES_LIST);
            values = getStringArrayListExtra(EXTRA_VAR_VALUES_LIST);
        } else {
            names = new ArrayList<>();
            values = new ArrayList<>();

            putStringArrayListExtra(EXTRA_VAR_NAMES_LIST, names);
            putStringArrayListExtra(EXTRA_VAR_VALUES_LIST, values);
        }

        names.add(name);
        values.add(value);

    }

    public TaskerIntent addAction(int code) {

        actionCount++;
        argCount = 1;

        Bundle actionBundle = new Bundle();

        actionBundle.putInt(ACTION_CODE, code);

        // Add action bundle to intent
        putExtra(EXTRA_ACTION_INDEX_PREFIX + Integer.toString(actionCount), actionBundle);

        return this;
    }

    // string arg
    public TaskerIntent addArg(String arg) {

        Bundle b = getActionBundle();

        if (b != null)
            b.putString(ARG_INDEX_PREFIX + Integer.toString(argCount++), arg);

        return this;
    }

    // int arg
    public TaskerIntent addArg(int arg) {
        Bundle b = getActionBundle();

        if (b != null)
            b.putInt(ARG_INDEX_PREFIX + Integer.toString(argCount++), arg);

        return this;
    }

    // boolean arg
    public TaskerIntent addArg(boolean arg) {
        Bundle b = getActionBundle();

        if (b != null)
            b.putBoolean(ARG_INDEX_PREFIX + Integer.toString(argCount++), arg);

        return this;
    }

    // Application arg
    public TaskerIntent addArg(String pkg, String cls) {
        Bundle b = getActionBundle();

        if (b != null) {
            StringBuilder builder = new StringBuilder();
            builder.append(APP_ARG_PREFIX).append(pkg).append(",").append(cls);
            b.putString(ARG_INDEX_PREFIX + Integer.toString(argCount++), builder.toString()); // CHANGED: b.toString()
        }

        return this;
    }

    public IntentFilter getCompletionFilter() {
        return getCompletionFilter(getTaskName());
    }

    private String getTaskName() {
        return getStringExtra(EXTRA_TASK_NAME);
    }

    private boolean receiverExists(Context context) {
        List<ResolveInfo> recs = context.getPackageManager().queryBroadcastReceivers(this, 0);
        return ((recs != null) && (recs.size() > 0));
    }

    // -------------------- PRIVATE METHODS -------------------- //

    private String getRandomString() {
        return Long.toString(rand.nextLong());
    }

    // so that if multiple TaskerIntents are used in PendingIntents there's virtually no
    // clash chance
    private void setRandomData() {
        setData(Uri.parse(TASK_ID_SCHEME + ":" + getRandomString()));
    }
//Trrakee
    private Bundle getActionBundle() {

        Bundle toReturn = null;

        if (argCount > MAX_NO_ARGS)
            Log.e(TAG, "maximum number of arguments exceeded (" + MAX_NO_ARGS + ")");
        else {
            String key = EXTRA_ACTION_INDEX_PREFIX + Integer.toString(actionCount);

            if (this.hasExtra(key))
                toReturn = getBundleExtra(key);
            else
                Log.e(TAG, "no actions added yet");
        }

        return toReturn;
    }

    private void putMetaExtras(String taskName) {
        putExtra(EXTRA_INTENT_VERSION_NUMBER, INTENT_VERSION_NUMBER);
        putExtra(EXTRA_TASK_NAME, taskName);
    }

    // for testing that Tasker is enabled and external access is allowed

    public enum Status {
        NotInstalled,
        NoPermission,
        NotEnabled,
        AccessBlocked,
        NoReceiver,
        OK
    }
}