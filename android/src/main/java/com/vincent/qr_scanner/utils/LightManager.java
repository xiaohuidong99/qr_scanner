package com.vincent.qr_scanner.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 亮度管理器
 * @Author: Vincent
 * @CreateAt: 2021/01/11 17:50
 */
public class LightManager implements SensorEventListener {
    private static final int INTERVAL_TIME = 200;

    protected static final float DARK_LUX = 45.0f;
    protected static final float BRIGHT_LUX = 100.0f;

    /**
     * 光线太暗时，默认：照度45 lux
     */
    private float darkLightLux = DARK_LUX;

    private final SensorManager sensorManager;
    private final Sensor lightSensor;

    private long lastTime;

    private boolean isLightSensorEnabled;

    private OnLightSensorEventListener mOnLightSensorEventListener;

    public LightManager(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        isLightSensorEnabled = true;
    }

    public void register() {
        if (sensorManager != null && lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void unregister() {
        if (sensorManager != null && lightSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(isLightSensorEnabled){
            long currentTime = System.currentTimeMillis();
            if(currentTime - lastTime < INTERVAL_TIME){//降低频率
                return;
            }
            lastTime = currentTime;

            if (mOnLightSensorEventListener != null) {
                float lightLux = sensorEvent.values[0];
                mOnLightSensorEventListener.onSensorChanged(lightLux);
                // 光线足够亮时，默认：照度450 lux
                if (lightLux <= darkLightLux) {
                    mOnLightSensorEventListener.onSensorChanged(true,darkLightLux);
                } else if (lightLux >= BRIGHT_LUX) {
                    mOnLightSensorEventListener.onSensorChanged(false,darkLightLux);
                }
            }
        }
    }

    /**
     * 设置光线足够暗的阈值（单位：lux）
     * @param lightLux
     */
    public void setDarkLightLux(float lightLux){
        this.darkLightLux = lightLux;
    }

    /**
     * 设置光线足够明亮的阈值（单位：lux）
     * @param lightLux
     */
    public void setBrightLightLux(float lightLux){
        this.darkLightLux = lightLux;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }

    public boolean isLightSensorEnabled() {
        return isLightSensorEnabled;
    }

    /**
     * 设置是否启用光线亮度传感器
     * @param lightSensorEnabled
     */
    public void setLightSensorEnabled(boolean lightSensorEnabled) {
        isLightSensorEnabled = lightSensorEnabled;
    }

    /**
     * 设置光线亮度传感器监听器，只有在 {@link #isLightSensorEnabled} 为{@code true} 才有效
     * @param listener
     */
    public void setOnLightSensorEventListener(OnLightSensorEventListener listener){
        mOnLightSensorEventListener = listener;
    }

    public interface OnLightSensorEventListener{
        /**
         *
         * @param lightLux 当前检测到的光线照度值
         */
        default void onSensorChanged(float lightLux){

        }

        /**
         * 传感器改变事件
         * @param dark 是否太暗了，当检测到的光线照度值小于{@link #darkLightLux}时，为{@code true}
         * @param lightLux 当前检测到的光线照度值
         */
        void onSensorChanged(boolean dark,float lightLux);
    }
}
