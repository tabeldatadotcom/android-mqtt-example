package com.tabeldata.mobile.hmi.helper;

import android.content.Context;
import android.util.Log;

import com.tabeldata.mobile.hmi.model.Temperatur;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by dimmaryanto93 on 01/11/2018.
 */

public class MqttHelper {

    public MqttAndroidClient mqttAndroidClient;

    /**
     * generate client id with org.eclipse.paho.client.mqttv3.MqttClient;
     */
    final String clientId = MqttClient.generateClientId();
    final String serverUri = "tcp://iot.eclipse.org:1883";

    public MqttHelper(Context context) {
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Failed to connect to: " + serverUri + exception.toString());
                }
            });

        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    final String topicSubscribe = "tabeldata/temp-subcribe";

    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(topicSubscribe, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("MqttTemp", "Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Subscribed fail!");
                }
            });
        } catch (MqttException ex) {
            System.err.println("Exceptionst subscribing");
            ex.printStackTrace();
        }
    }

    final String topicPublisher = "tabeldata/temp-publisher";

    /**
     * untuk mempublish message temp
     *
     * @param temperatur
     */
    public void publish(Temperatur temperatur) {
        try {
            JSONObject detail = new JSONObject();
            JSONObject value = new JSONObject();
            value.put("temp", new JSONArray(Arrays.asList(temperatur.getTemp())));
            value.put("humidity", new JSONArray(Arrays.asList(temperatur.getHumidity())));

            detail.put("d", value);
            detail.put("ts", DateFormater.toISO8601UTC(new Date()));
            Log.i("tempPublisher", "publish: " + detail.toString());

            MqttMessage publishMessage = new MqttMessage();
            publishMessage.setQos(0);
            publishMessage.setPayload(detail.toString().getBytes());
            mqttAndroidClient.publish(
                    topicPublisher, publishMessage, null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Log.w("publish", "onSuccess: berhasil terkirim");
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Log.w("publish", "onFailure: gagal publish message");
                        }
                    });
        } catch (MqttException me) {
            me.printStackTrace();
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    public String getTopicTemp() {
        return this.topicSubscribe;
    }
}
