package be.helha.progmobile.medictime.models;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Prescription {
    private UUID mPrescriptionId;
    private Date mPrescriptionStartDate;
    private Date mPrescriptionEndDate;
    private boolean mPrescriptionMorningIntake;
    private boolean mPrescriptionNoonIntake;
    private boolean mPrescriptionEveningIntake;
    private Medicine mPresciptionMedicine;

    public Prescription(UUID id, Date startDate, Date endDate, boolean morningIntake,
                        boolean noonIntake, boolean eveningIntake, Medicine medicine) {
        this.mPrescriptionId = id;
        this.mPrescriptionStartDate = startDate;
        this.mPrescriptionEndDate = endDate;
        this.mPrescriptionMorningIntake = morningIntake;
        this.mPrescriptionNoonIntake = noonIntake;
        this.mPrescriptionEveningIntake = eveningIntake;
        this.mPresciptionMedicine = medicine;
    }

    public Prescription(Date startDate, Date endDate, boolean morningIntake,
                        boolean noonIntake, boolean eveningIntake, Medicine medicine){
        this(UUID.randomUUID(), startDate, endDate, morningIntake, noonIntake, eveningIntake, medicine);
    }

    public Prescription(){
        this(new Date(), new Date(), false, false, false, new Medicine());
    }

    public UUID getPrescriptionId() {
        return mPrescriptionId;
    }

    public void setPrescriptionId(UUID mPrescriptionId) {
        this.mPrescriptionId = mPrescriptionId;
    }

    public Date getPrescriptionStartDate() {
        return mPrescriptionStartDate;
    }

    public void setPrescriptionStartDate(Date prescriptionStartDate) {
        this.mPrescriptionStartDate = prescriptionStartDate;
    }

    public Date getPrescriptionEndDate() {
        return mPrescriptionEndDate;
    }

    public void setPrescriptionEndDate(Date prescriptionEndDate) {
        this.mPrescriptionEndDate = prescriptionEndDate;
    }

    public boolean isPrescriptionMorningIntake() {
        return mPrescriptionMorningIntake;
    }

    public void setPrescriptionMorningIntake(boolean mPrescriptionMorningIntake) {
        this.mPrescriptionMorningIntake = mPrescriptionMorningIntake;
    }

    public boolean isPrescriptionNoonIntake() {
        return mPrescriptionNoonIntake;
    }

    public void setPrescriptionNoonIntake(boolean mPrescriptionNoonIntake) {
        this.mPrescriptionNoonIntake = mPrescriptionNoonIntake;
    }

    public boolean isPrescriptionEveningIntake() {
        return mPrescriptionEveningIntake;
    }

    public void setPrescriptionEveningIntake(boolean mPrescriptionEveningIntake) {
        this.mPrescriptionEveningIntake = mPrescriptionEveningIntake;
    }

    public Medicine getPrescriptionMedicine() {
        return mPresciptionMedicine;
    }

    public void setPrescriptionMedicine(Medicine mPrescriptionMedicine) {
        this.mPresciptionMedicine = mPrescriptionMedicine;
    }

    public void setIntakes(boolean morning, boolean noon, boolean evening){
        setPrescriptionMorningIntake(morning);
        setPrescriptionNoonIntake(noon);
        setPrescriptionEveningIntake(evening);
    }
}
