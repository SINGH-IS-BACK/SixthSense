package singh.com.sixthsense.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingsManager {
    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "Sixth_Setting";

    // All Shared Preferences Keys
    private static final String FINAL_LOCATION = "finalLocation";

    private static SettingsManager instance = null;

    // Constructor
    private SettingsManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static synchronized SettingsManager getInstance(Context context) {
        if(instance == null) {
            instance = new SettingsManager(context);
        }
        return instance;
    }

    public void setFinalLocation(String finalLocation){
        editor.putString(FINAL_LOCATION, finalLocation);
        editor.commit();
    }
    public String getFinalLocation(){
        return pref.getString(FINAL_LOCATION, "");
    }


    public void delete(){
        editor.clear();
        editor.commit();
    }
}
