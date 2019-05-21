package com.avit.xhttp.portal;


import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MockService {
    public String getJsonFromLocal(Context context, String file_name) {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream is = assetManager.open(file_name);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer stringBuffer = new StringBuffer();
            String str = null;
            while ((str = br.readLine()) != null) {
                stringBuffer.append(str);
            }
            return stringBuffer.toString();

        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
    }


}
