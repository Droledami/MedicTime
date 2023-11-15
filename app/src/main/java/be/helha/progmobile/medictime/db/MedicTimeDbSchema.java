package be.helha.progmobile.medictime.db;

public abstract class MedicTimeDbSchema {
    public static final class MedicineTable{
        public static final String NAME = "Medicine";
        public static final class cols{
            public static final String MEDICINE_ID = "MedicineId";
            public static final String NAME = "Name";
            public static final String DURATION = "Duration";
            public static final String MORNING = "Morning";
            public static final String NOON = "Noon";
            public static final String EVENING = "Evening";
        }
    }
    public static final class PrescriptionTable{
        public static final String NAME = "Prescription";
        public static final class cols{
            public static final String PRESCRIPTION_ID = "PrescriptionId";
            public static final String START_DATE = "StartDate";
            public static final String END_DATE = "EndDate";
            public static final String MORNING = "Morning";
            public static final String NOON = "Noon";
            public static final String EVENING = "Evening";
            public static final String MEDICINE_ID_FK = "MedicineId";
        }
    }
}
