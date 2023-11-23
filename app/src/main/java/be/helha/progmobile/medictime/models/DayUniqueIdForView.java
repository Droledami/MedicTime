package be.helha.progmobile.medictime.models;

/**
 * A class representing unique identifiers for different components of a day view.
 * This class encapsulates identifiers for the day, morning, noon, and evening views within a day view.
 * These identifiers are used to reference specific views associated with a particular day.
 **/
public class DayUniqueIdForView {
    private int mDayID;
    private int mMorningID;
    private int mNoonID;
    private int mEveningID;

    public DayUniqueIdForView(){
        this.mDayID = -1;
        this.mMorningID = -1;
        this.mNoonID = -1;
        this.mEveningID = -1;
    }
    public DayUniqueIdForView(int mDayID, int mMorningID, int mNoonID, int mEveningID){
        this.mDayID = mDayID;
        this.mMorningID = mMorningID;
        this.mNoonID = mNoonID;
        this.mEveningID = mEveningID;
    }

    public int getDayID() {
        return mDayID;
    }

    public int getMorningID() {
        return mMorningID;
    }

    public int getNoonID() {
        return mNoonID;
    }

    public int getEveningID() {
        return mEveningID;
    }

    public void setDayID(int mDayID) {
        this.mDayID = mDayID;
    }

    public void setMorningID(int mMorningID) {
        this.mMorningID = mMorningID;
    }

    public void setNoonID(int mNoonID) {
        this.mNoonID = mNoonID;
    }

    public void setEveningID(int mEveningID) {
        this.mEveningID = mEveningID;
    }
}
