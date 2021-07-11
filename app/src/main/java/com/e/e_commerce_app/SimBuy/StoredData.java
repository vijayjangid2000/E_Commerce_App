package com.e.e_commerce_app.SimBuy;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;

public class StoredData {

    // transient is used to prevent serialization of non required fields
    private transient final String USER_DATA_SP = "userDataSP";
    private transient final String USER_DATA_JSON = "userDataJson";
    private transient SharedPreferences sharedPref;
    private transient SharedPreferences.Editor sPrefEditor;
    private static StoredData storedData;
    private final transient Context context;

    private static ArrayList<ProductSimCardModel> listRequestedSim;

    public void addToOrderList(ProductSimCardModel pm) {
        listRequestedSim.add(pm);
        applyUpdate(context);
    }

    public void removeFromOrderList(ProductSimCardModel productModel) {
        listRequestedSim.remove(productModel);
        applyUpdate(context);
    }

    // --------- Default things below --------- //

    private StoredData(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences(USER_DATA_SP, Context.MODE_PRIVATE);
        sPrefEditor = sharedPref.edit();
        sPrefEditor.apply();

        initializeAllStrings(); // to prevent null exceptions

        String jsonString = sharedPref.getString(USER_DATA_JSON, "");

        // if there is no data then do nothing
        assert jsonString != null;
        if (!jsonString.equals("")) {
            Gson gson = new Gson();
            StoredData userDetails = gson.fromJson(jsonString, StoredData.class);
            setUserDataFromJson(userDetails);
            // now we have assigned previous (saved) data to this object
        }
    }

    public static StoredData getInstance(Context context) {
        if (storedData == null) storedData =
                new StoredData(context.getApplicationContext());
        return storedData;
    }

    public static StoredData getInstance() {
        return storedData; // WARNING: only when you are sure that it has been created already
    }

    private void initializeAllStrings() {
        listRequestedSim = new ArrayList<>();
    }

    public void applyUpdate(Context context) {

        /* TO UPDATE DATA FOLLOW THESE STEPS
         * 1. Create the object (created by old json automatically)
         * 2. Set the data you want to change
         * 3. Now call updateData from the object in which data changed
         * 4. We will save the data from that object in SharedPref
         * 5. HOW? updated strings gets replaced and others will be there as it is
         *  */

        sharedPref = context.getSharedPreferences(USER_DATA_SP,
                Context.MODE_PRIVATE);
        sPrefEditor = sharedPref.edit();
        sPrefEditor.putString(USER_DATA_JSON, new Gson().toJson(this));
        sPrefEditor.apply();
    }

    private void setUserDataFromJson(StoredData userDetails) {
        this.listRequestedSim = userDetails.listRequestedSim;
    }

    // GETTERS AND SETTERS


    public ArrayList<ProductSimCardModel> getCartItemsList() {
        return listRequestedSim;
    }
}
