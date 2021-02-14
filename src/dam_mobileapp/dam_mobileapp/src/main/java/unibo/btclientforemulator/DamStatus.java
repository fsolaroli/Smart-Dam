package unibo.btclientforemulator;

import android.util.Log;

import java.util.regex.Pattern;

public class DamStatus {
    private double damLevel;
    private DamSystemState damSystemState;
    private int damOpenPercentage;

    public DamStatus(DamSystemState damSystemState, double damLevel, int damOpenPercentage) {
        this.damLevel = damLevel;
        this.damOpenPercentage = damOpenPercentage;
        this.damSystemState = damSystemState;
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

    public DamStatus updateOpening (int openin) {
        this.damOpenPercentage = openin;
        return this;
    }

    public DamStatus updateSystemState (DamSystemState systemState) {
        this.damSystemState = systemState;
        return this;
    }

    public DamStatus updateWithMessage (String message) {
        Pattern regex = Pattern.compile("(N|P|A|M):L=\\d(\\.\\d+)?,P=\\d+");
        if(regex.matcher(message).matches()) {

            String systemStatus = message.split(":")[0];
            String data = message.split(":")[1];

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
                case "M":
                    status = DamSystemState.MANUAL;
                    break;
            }
            if (this.damSystemState != DamSystemState.MANUAL) {
                this.damSystemState = status;
                this.damOpenPercentage = Integer.parseInt(data.split(",")[1].substring(2));
            } else {
                Log.d("DEBUG", "Modalità manuale. Ignorato messaggio: " + message);
            }

            this.damLevel = Double.parseDouble(data.split(",")[0].substring(2));

        }
        return this;
    }
}
