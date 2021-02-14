package unibo.btclientforemulator;

public enum DamSystemState {
    NORMAL("Normal"),
    PRE_ALLARM("Pre-Allarm"),
    ALLARM("Allarm");

    private String state;

    DamSystemState(String state) {
        this.state = state;
    }

    public String toString() {
        return this.state;
    }
}
