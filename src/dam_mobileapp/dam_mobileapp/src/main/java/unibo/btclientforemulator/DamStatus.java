package unibo.btclientforemulator;

import android.util.Log;

import java.util.regex.Pattern;

public class DamStatus {
    private double damLevel;
    private DamSystemState damSystemState;
    private boolean isManual;
    private int damOpenPercentage;

    public DamStatus() {
        this.damSystemState = DamSystemState.NORMAL;
        this.damLevel = 0;
        this.damOpenPercentage = 0;
        this.isManual = false;
    }

    public DamSystemState getSystemState() {
        return this.damSystemState;
    }

    public int getDamOpenPercentage() {
        return this.damOpenPercentage;
    }

    public double getDamLevel() {
       return this.damLevel;
    }

    public boolean isManual() {
        return isManual;
    }

    public DamStatus updateOpening (int openin) {
        this.damOpenPercentage = openin;
        return this;
    }

    public DamStatus updateSystemState (DamSystemState systemState) {
        this.damSystemState = systemState;
        return this;
    }

    public DamStatus updateIsManual (boolean isManual) {
        this.isManual = isManual;
        return this;
    }

    public DamStatus updateWithMessage (String message) {
        if(message != null && message != "") {
            String systemStatus = message.split(":")[0];

            DamSystemState status = DamSystemState.NORMAL;

            switch (systemStatus) {
                case "N":
                    status = DamSystemState.NORMAL;
                    break;
                case "P":
                    status = DamSystemState.PRE_ALLARM;
                    break;
                case "A":
                    status = DamSystemState.ALLARM;
                    break;
            }
            this.damSystemState = status;

            if (message.split(":").length > 1) {
                String data = message.split(":")[1];

                this.damLevel = Double.parseDouble(data.split(",")[0].substring(2));

                if (!this.isManual()) {
                    this.damOpenPercentage = Integer.parseInt(data.split(",")[1].substring(2));
                } else {
                    Log.d("DEBUG", "Modalità manuale. Ignorato messaggio: " + message);
                }
            }
        }
        return this;
    }
}
