package be.helha.progmobile.medictime.models;

import java.util.UUID;

public class Medicine {
    private final static String NEW_MEDICINE = "Nouveau médicament"; //Possible d'utiliser string.xml sans passer le context dans le modèle? (Interface?)
    private UUID mMedicineId;
    private String mMedicineName;
    private int mMedicineDuration;
    private boolean mMedicineMorningIntake;
    private boolean mMedicineNoonIntake;
    private boolean mMedicineEveningIntake;

    public Medicine(UUID id, String name, int duration, boolean morningIntake, boolean noonIntake, boolean eveningIntake){
        this.mMedicineId = id;
        this.mMedicineName = name;
        this.mMedicineDuration = duration;
        this.mMedicineMorningIntake = morningIntake;
        this.mMedicineNoonIntake = noonIntake;
        this.mMedicineEveningIntake = eveningIntake;
    }

    public Medicine (String name, int duration, boolean morningIntake, boolean noonIntake, boolean eveningIntake){
        this(UUID.randomUUID(), name, duration, morningIntake, noonIntake, eveningIntake);
    }

    public Medicine(){
        this(NEW_MEDICINE, 0, false, false, false);
    }

    public UUID getMedicineId() {
        return mMedicineId;
    }

    public void setMedicineId(UUID mMedicineId) {
        this.mMedicineId = mMedicineId;
    }

    public String getMedicineName() {
        return mMedicineName;
    }

    public void setMedicineName(String mMedicineName) {
        this.mMedicineName = mMedicineName;
    }

    public int getMedicineDuration() {
        return mMedicineDuration;
    }

    public void setMedicineDuration(int mMedicineDuration) {
        this.mMedicineDuration = mMedicineDuration;
    }

    public boolean isMedicineMorningIntake() {
        return mMedicineMorningIntake;
    }

    public void setMedicineMorningIntake(boolean mMedicineMorningIntake) {
        this.mMedicineMorningIntake = mMedicineMorningIntake;
    }

    public boolean isMedicineNoonIntake() {
        return mMedicineNoonIntake;
    }

    public void setMedicineNoonIntake(boolean mMedicineNoonIntake) {
        this.mMedicineNoonIntake = mMedicineNoonIntake;
    }

    public boolean isMedicineEveningIntake() {
        return mMedicineEveningIntake;
    }

    public void setMedicineEveningIntake(boolean mMedicineEveningIntake) {
        this.mMedicineEveningIntake = mMedicineEveningIntake;
    }

    public void setIntakes(boolean morning, boolean noon, boolean evening){
        setMedicineMorningIntake(morning);
        setMedicineNoonIntake(noon);
        setMedicineEveningIntake(evening);
    }
}
