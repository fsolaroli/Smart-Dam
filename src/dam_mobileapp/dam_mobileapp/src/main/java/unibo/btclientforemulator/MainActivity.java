package unibo.btclientforemulator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import unibo.btlib.BluetoothChannel;
import unibo.btlib.ConnectToEmulatedBluetoothServerTask;
import unibo.btlib.ConnectionTask;
import unibo.btlib.EmulatedBluetoothChannel;

public class MainActivity extends AppCompatActivity {

    private BluetoothChannel btChannel;

    private DamStatus currentDamStatus;

    private boolean alreadyConnected = false;

    // .............................................................................................

    @Override protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    @Override protected void onPause () {
        if (btChannel != null) {
//            btChannel.removeListener();
            btChannel.close();
        }
        super.onPause();
    }

    @Override protected void onResume () {
        super.onResume();
        if (this.alreadyConnected) {
            initUI();
            findViewById(R.id.connectBtn).performClick();
        }
    }

    // .............................................................................................

    private void initUI () {
        final View      content = findViewById(R.id.content);
        final View      connectButton = findViewById(R.id.connectBtn);

        final Button    manualButton  = findViewById(R.id.manualMode);
        final SeekBar   damOpeningSlider = findViewById(R.id.manualDamOpening);

        content.setVisibility(View.INVISIBLE);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick (View v) {
                connectToBTServer(content);

            }
        });

        manualButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick (View v) {
                if (currentDamStatus.getSystemState() == DamSystemState.MANUAL) {
                    sendMessage("A:");
                    updateUI(currentDamStatus.updateSystemState(DamSystemState.ALLARM));
                } else {
                    sendMessage("M:Pm=" + currentDamStatus.getDamOpenPercentage());
                    updateUI(currentDamStatus.updateSystemState(DamSystemState.MANUAL));
                }
            }
        });

        damOpeningSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged (SeekBar seekBar, int i, boolean b) { }

            @Override public void onStartTrackingTouch (SeekBar seekBar) { }

            @Override public void onStopTrackingTouch (SeekBar seekBar) {
                int currentDamOpening = seekBar.getProgress();
                updateUI(currentDamStatus.updateOpening(currentDamOpening));
                sendMessage("M:Pm=" + currentDamOpening);
            }
        });

        updateUI(this.currentDamStatus = new DamStatus(DamSystemState.NORMAL, 0, 0));
    }

    private void updateUI (DamStatus damStatus) {
        final TextView  damStatusValue = findViewById(R.id.systemState);
        final TextView  damLevelValue = findViewById(R.id.damLevel);
        final TextView  damOpeningValue = findViewById(R.id.damOpening);

        final SeekBar   damOpeningSlider = findViewById(R.id.manualDamOpening);
        final Button    manualButton  = findViewById(R.id.manualMode);

        damStatusValue.setText(damStatus.getSystemState().toString());

        damLevelValue.setVisibility(View.INVISIBLE);
        damLevelValue.setVisibility(View.VISIBLE);
        damLevelValue.setText(damStatus.getDamLevel() + "m");

        damOpeningValue.setVisibility(View.INVISIBLE);
        damOpeningValue.setVisibility(View.VISIBLE);
        damOpeningValue.setText(damStatus.getDamOpenPercentage() + "%");


        switch (damStatus.getSystemState()) {
            case NORMAL:
                damLevelValue.setVisibility(View.INVISIBLE);
                damOpeningValue.setVisibility(View.INVISIBLE);

                damOpeningSlider.setVisibility(View.INVISIBLE);

                manualButton.setEnabled(false);
                manualButton.setText("ENABLE MANUAL MODE");
                break;
            case PRE_ALLARM:
                damLevelValue.setVisibility(View.VISIBLE);
                damOpeningValue.setVisibility(View.VISIBLE);

                damOpeningSlider.setVisibility(View.INVISIBLE);

                manualButton.setEnabled(false);
                manualButton.setText("ENABLE MANUAL MODE");
                break;
            case ALLARM:
                damLevelValue.setVisibility(View.VISIBLE);
                damOpeningValue.setVisibility(View.VISIBLE);

                damOpeningSlider.setVisibility(View.INVISIBLE);

                manualButton.setEnabled(true);
                manualButton.setText("ENABLE MANUAL MODE");
                break;
            case MANUAL:
                damLevelValue.setVisibility(View.VISIBLE);
                damOpeningValue.setVisibility(View.VISIBLE);

                damOpeningSlider.setVisibility(View.VISIBLE);

                manualButton.setText("DISABLE MANUAL MODE");
                damOpeningSlider.setProgress(damStatus.getDamOpenPercentage());
                manualButton.setEnabled(true);
                break;
        }

    }

    private void connectToBTServer (final View content)  {
        new ConnectToEmulatedBluetoothServerTask(new ConnectionTask.EventListener() {
            final TextView  statusLabel = findViewById(R.id.statusLabel);

            @Override public void onConnectionActive (final BluetoothChannel channel) {
                statusLabel.setText(String.format("Status : connected to server on device %s", "host machine"));
                findViewById(R.id.connectBtn).setEnabled(false);

                alreadyConnected = true;

                content.setVisibility(View.VISIBLE);

                btChannel = channel;
                btChannel.registerListener(new EmulatedBluetoothChannel.Listener() {
                    @Override public void onMessageReceived (String receivedMessage) {
                        if (receivedMessage != null) {
                            updateUI(currentDamStatus.updateWithMessage(receivedMessage));
                        }
                    }

                    @Override public void onMessageSent (String sentMessage) { }
                });
            }

            @Override public void onConnectionCanceled () {
                ((TextView) findViewById(R.id.statusLabel)).setText(String.format("Status : unable to connect, device %s not found!", "host machine"));
                findViewById(R.id.content).setVisibility(View.INVISIBLE);
            }
        }).execute();
    }

    private void sendMessage (final String message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                btChannel.sendMessage(message);
            }
        }).start();
    }
}
