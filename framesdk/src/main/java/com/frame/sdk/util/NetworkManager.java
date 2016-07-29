package com.frame.sdk.util;
 
import java.lang.reflect.Method;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
 
public class NetworkManager {
 
    private Context context;
    private ConnectivityManager connManager;
    
    private  static NetworkManager instance;
 
    private NetworkManager(Context context) {
        this.context = context;
        connManager = (ConnectivityManager) this.context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }
    
    public static NetworkManager getInstance(Context context){
    	if(instance==null){
    		instance=new NetworkManager(context);
    	}
    	return instance;
    }
 
    /**
     * @return 网络是否连接可用
     */
    public boolean isNetworkConnected() {
 
        NetworkInfo networkinfo = connManager.getActiveNetworkInfo();
 
        if (networkinfo != null) {
            return networkinfo.isConnected();
        }
 
        return false;
    }
 
    /**
     * @return wifi是否连接可用
     */
    public boolean isWifiConnected() {
 
        NetworkInfo mWifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
 
        if (mWifi != null) {
            return mWifi.isConnected();
        }
 
        return false;
    }
 
    /**
     * 当wifi不能访问网络时，mobile才会起作用
     * @return GPRS是否连接可用
     */
    public boolean isMobileConnected() {
 
        NetworkInfo mMobile = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
 
        if (mMobile != null) {
            return mMobile.isConnected();
        }
        return false;
    }
 
    /**
     * GPRS网络开关 反射ConnectivityManager中hide的方法setMobileDataEnabled 可以开启和关闭GPRS网络
     * 
     * @param isEnable
     * @throws Exception
     */
    public void toggleGprs(boolean isEnable) throws Exception {
        Class<?> cmClass = connManager.getClass();
        Class<?>[] argClasses = new Class[1];
        argClasses[0] = boolean.class;
 
        // 反射ConnectivityManager中hide的方法setMobileDataEnabled，可以开启和关闭GPRS网络
        Method method = cmClass.getMethod("setMobileDataEnabled", argClasses);
        method.invoke(connManager, isEnable);
    }
 
    /**
     * WIFI网络开关
     * 
     * @param enabled
     * @return 设置是否success
     */
    public boolean toggleWiFi(boolean enabled) {
        WifiManager wm = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        return wm.setWifiEnabled(enabled);
 
    }
     
    /**
     * 
     * @return 是否处于飞行模式
     */
    public boolean isAirplaneModeOn() {  
        // 返回值是1时表示处于飞行模式  
        int modeIdx = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);  
        boolean isEnabled = (modeIdx == 1);
        return isEnabled;
    }  
    /**
     * 飞行模式开关
     * @param setAirPlane
     */
    public void toggleAirplaneMode(boolean setAirPlane) {  
        Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, setAirPlane ? 1 : 0);  
        // 广播飞行模式信号的改变，让相应的程序可以处理。  
        // 不发送广播时，在非飞行模式下，Android 2.2.1上测试关闭了Wifi,不关闭正常的通话网络(如GMS/GPRS等)。  
        // 不发送广播时，在飞行模式下，Android 2.2.1上测试无法关闭飞行模式。  
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);  
        // intent.putExtra("Sponsor", "Sodino");  
        // 2.3及以后，需设置此状态，否则会一直处于与运营商断连的情况  
        intent.putExtra("state", setAirPlane);  
        context.sendBroadcast(intent);  
    }
}