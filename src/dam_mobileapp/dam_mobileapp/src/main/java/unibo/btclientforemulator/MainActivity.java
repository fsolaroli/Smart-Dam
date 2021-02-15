package unibo.btclientforemulator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import unibo.btlib.BluetoothChannel;
import unibo.btlib.CommChannel;
import unibo.btlib.ConnectToEmulatedBluetoothServerTask;
import unibo.btlib.ConnectionTask;
import unibo.btlib.EmulatedBluetoothChannel;

import static unibo.btclientforemulator.ApplicationState.*;

public class MainActivity extends AppCompatActivity {

    private BluetoothChannel btChannel;

    private DamStatus currentDamStatus = new DamStatus();
    private ApplicationState applicationState = DISCONNECTED;

    // .............................................................................................

    @Override protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    @Override protected void onPause () {
        sendMessage("EM:");
        this.currentDamStatus.updateIsManual(false);
        this.applicationState = DISCONNECTED;
        if (btChannel != null) {
            btChannel.close();
        }
        super.onPause();
    }

    @Override protected void onResume () {
        super.onResume();
        if (this.applicationState == DISCONNECTED) {
            this.connectToBTServer();
        }
        updateUI(this.currentDamStatus);
    }

    // .............................................................................................

    private void initUI () {
        final Button    manualButton  = findViewById(R.id.manualMode);
        final SeekBar   damOpeningSlider = findViewById(R.id.manualDamOpening);

        manualButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick (View v) {
                if (currentDamStatus.isManual()) {
                    sendMessage("EM:");
                    updateUI(currentDamStatus.updateIsManual(false));
                } else {
                    updateUI(currentDamStatus.updateIsManual(true));
                    sendMessage("M:Pm=" + currentDamStatus.getDamOpenPercentage());
                }
            }
        });

        damOpeningSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged (SeekBar seekBar, int i, boolean b) { }

            @Override public void onStartTrackingTouch (SeekBar seekBar) { }

            @Override public void onStopTrackingTouch (SeekBar seekBar) {
                int currentDamOpening = seekBar.getProgress();
                sendMessage("M:Pm=" + currentDamOpening);
                updateUI(currentDamStatus.updateOpening(currentDamOpening));
            }
        });
    }

    private void updateUI (DamStatus damStatus) {
        final View      content = findViewById(R.id.content);

        final TextView  statusLabel = findViewById(R.id.statusLabel);

        final TextView  damStatusValue = findViewById(R.id.systemState);
        final TextView  damLevelValue = findViewById(R.id.damLevel);
        final TextView  damOpeningValue = findViewById(R.id.damOpening);

        final SeekBar   damOpeningSlider = findViewById(R.id.manualDamOpening);
        final Button    manualButton  = findViewById(R.id.manualMode);

        damLevelValue.setVisibility(View.INVISIBLE);
        damLevelValue.setVisibility(View.VISIBLE);
        damLevelValue.setText(damStatus.getDamLevel() + "m");

        damOpeningValue.setVisibility(View.INVISIBLE);
        damOpeningValue.setVisibility(View.VISIBLE);
        damOpeningValue.setText(damStatus.getDamOpenPercentage() + "%");

        switch (applicationState) {
            case CONNECTED:
                content.setVisibility(View.INVISIBLE);
                content.setVisibility(View.VISIBLE);

                statusLabel.setText(String.format("Status : connected to server on device %s", "host machine"));

                if (currentDamStatus.isManual()) {
                    damOpeningSlider.setVisibility(View.VISIBLE);

                    manualButton.setText("DISABLE MANUAL MODE");
                    damStatusValue.setText(damStatus.getSystemState().toString() + " Manual");

                    damOpeningSlider.setProgress(damStatus.getDamOpenPercentage());
                } else {
                    damOpeningSlider.setVisibility(View.INVISIBLE);

                    manualButton.setText("ENABLE MANUAL MODE");
                    damStatusValue.setText(damStatus.getSystemState().toString());

                    switch (damStatus.getSystemState()) {
                        case NORMAL:
                            damLevelValue.setVisibility(View.INVISIBLE);
                            damOpeningValue.setVisibility(View.INVISIBLE);

                            manualButton.setEnabled(false);
                            break;
                        case PRE_ALLARM:
                            damLevelValue.setVisibility(View.VISIBLE);
                            damOpeningValue.setVisibility(View.INVISIBLE);

                            manualButton.setEnabled(false);
                            break;
                        case ALLARM:
                            damLevelValue.setVisibility(View.VISIBLE);
                            damOpeningValue.setVisibility(View.VISIBLE);

                            manualButton.setEnabled(true);
                            break;
                    }
                }
                break;
            case UNABLE_TO_CONNECT:
                statusLabel.setText(String.format("Status : unable to connect, device %s not found!", "host machine"));
                content.setVisibility(View.INVISIBLE);
                break;
            case DISCONNECTED:
                statusLabel.setText(String.format("Status : disconnected"));
                content.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void connectToBTServer ()  {
        new ConnectToEmulatedBluetoothServerTask(new ConnectionTask.EventListener() {
            @Override
            public void onConnectionActive(final BluetoothChannel channel) {
                applicationState = CONNECTED;
                Log.d("AppState", "CONNECTED");

                btChannel = channel;
                btChannel.registerListener(new EmulatedBluetoothChannel.Listener() {
                    @Override
                    public void onMessageReceived(String receivedMessage) {
                        Log.d("MESSAGE_RECEIVED", "Ricevuto messaggio da Android: " + receivedMessage + ". Stato corrente: " + currentDamStatus.getSystemState().toString());
                        if (receivedMessage != null) {
                            updateUI(currentDamStatus.updateWithMessage(receivedMessage));
                        }
                    }

                    @Override
                    public void onMessageSent(String sentMessage) { }
                });
            }

            @Override
            public void onConnectionCanceled() {
                applicationState = UNABLE_TO_CONNECT;
                Log.d("AppState", "UNABLE TO CONNECT");
                updateUI(currentDamStatus);
            }
        }).execute();
    }

    private void sendMessage (final String message){
        if (btChannel != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    btChannel.sendMessage(message);
                }
            }).start();
        }
    }
}
