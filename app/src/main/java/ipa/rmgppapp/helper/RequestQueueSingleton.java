package ipa.rmgppapp.helper;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

public class RequestQueueSingleton {

    private static RequestQueueSingleton mInstance;
    private RequestQueue mRequestQueue;
    private Context mContext;

    public RequestQueueSingleton(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    /**
     * Returns a instance of this singleton
     */
    public static synchronized RequestQueueSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestQueueSingleton(context);
        }
        return mInstance;
    }

    /**
     * Returns instance of the request queue
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            //mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext(), new HurlStack(null, ClientSSLSocketFactory.getSocketFactory()));
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }
}
