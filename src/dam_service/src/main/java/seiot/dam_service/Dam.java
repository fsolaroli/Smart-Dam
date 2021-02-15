package seiot.dam_service;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;

import static seiot.dam_service.DamSystemState.*;

public class Dam {

    private List<Pair<String, Double>> damLevels;
    private DamSystemState status;
    private boolean isManual;
    private int damOpenPercentage;
    private int Nril;

    public Dam() {
        Nril = 20;
        damLevels = new LinkedList<>();
        damOpenPercentage = 0;
        status = NORMAL;
        isManual = false;
    }

    public DamSystemState getStatus() {
        return this.status;
    }

    public void setStatus(DamSystemState newStatus) {
        this.status = newStatus;
    }

    public int getDamOpenPercentage() {
        return this.damOpenPercentage;
    }

    public void setDamOpenPercentage(int newDamOpenPercentage) {
        this.damOpenPercentage = newDamOpenPercentage;
    }

    public void addDamLevel(Pair<String, Double> newLevel) {
        damLevels.add(newLevel);
    }

    public boolean isManual() {
        return this.isManual;
    }

    public void setManual(boolean isManual) {
        this.isManual = isManual;
    }

    public JsonObject getHTTPRequestData() {

        JsonArray levelData = new JsonArray();

        this.damLevels.subList(Math.max(0, this.damLevels.size() - this.Nril), this.damLevels.size()).forEach( pair -> {
            levelData.add(new JsonObject().put("time", pair.getLeft()).put("value", pair.getRight()));
        });

        JsonObject damJSON = new JsonObject()
                .put("Status", this.status.toString() + (this.isManual() ? " Manual" : ""))
                .put("DamOpenPercentage", this.damOpenPercentage)
                .put("DamLevelData", levelData);

        return damJSON;
    }
}
