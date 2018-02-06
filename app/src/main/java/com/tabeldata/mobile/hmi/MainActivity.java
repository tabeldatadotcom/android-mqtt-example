package com.tabeldata.mobile.hmi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tabeldata.mobile.hmi.helper.MqttHelper;
import com.tabeldata.mobile.hmi.model.Temperatur;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    MqttHelper mqttHelper;
    private final Temperatur temp = new Temperatur();

    private EditText txtTemp;
    private EditText txtHumidity;
    private EditText inputTemp;
    private EditText inputKelembaban;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.txtTemp = findViewById(R.id.txtTemp);
        this.txtHumidity = findViewById(R.id.txtHumidity);
        this.inputTemp = findViewById(R.id.inputTemp);
        this.inputKelembaban = findViewById(R.id.inputKelembaban);

        this.txtTemp.setText("0.0");
        this.txtHumidity.setText("0.0");
        this.inputTemp.setText("0.0");
        this.inputKelembaban.setText("0.0");
        startMqtt();
    }

    private void startMqtt() {
        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                JSONObject json = new JSONObject(mqttMessage.toString());
                if (topic.equalsIgnoreCase(mqttHelper.getTopicTemp())) {
                    JSONObject detailTemp = new JSONObject(json.getString("d"));
                    temp.setTemp(detailTemp.getJSONArray("temp").getDouble(0));
                    temp.setHumidity(detailTemp.getJSONArray("humidity").getDouble(0));

                    txtTemp.setText(temp.getTempDecimal());
                    txtHumidity.setText(temp.getHumidityDecimal());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                try {
                    MqttMessage message = iMqttDeliveryToken.getMessage();
                    Log.i("publisher", "deliveryComplete: " + new String(message.getPayload()));
                    Toast.makeText(MainActivity.this, "Pesan terkirim", Toast.LENGTH_SHORT).show();
                } catch (MqttException e) {
                    Log.e("pesan terkirim", "deliveryComplete: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void connectComplete(boolean b, String s) {
                Toast.makeText(MainActivity.this, "Koneksi berhasil", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void connectionLost(Throwable throwable) {
                Log.i("connection", "connectionLost: connection terputus!");
                Toast.makeText(MainActivity.this, "Koneksi internet terputus", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void kirimData(View view) {
        temp.setTemp(Double.valueOf(inputTemp.getText().toString()));
        temp.setHumidity(Double.valueOf(inputKelembaban.getText().toString()));
        mqttHelper.publish(temp);
    }
}
