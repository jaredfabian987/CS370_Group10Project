package com.repit.Model;

import com.repit.Model.enums.EquipmentType;
import com.repit.Model.enums.ExerciseType;

public class Equipment {

    // id for finding the equipment in the DB
    private int equipmentId;
    private int exerciseId;

    // name of the equipment
    private String name;
    // time of the equipment, enum declared in a different file
    private EquipmentType type;
    // is the equipment user defined
    private boolean isCustom;

    // constructors
    public Equipment() {}

    public Equipment (int equipmentId, int exerciseId, String name, EquipmentType type, boolean isCustom) {
        this.equipmentId = equipmentId;
        this.exerciseId = exerciseId;
        this.name = name;
        this.type = type;
        this.isCustom = isCustom;
    }

    public Equipment (int equipmentId, int exerciseId, String name, int type, int isCustom) {
        this.equipmentId = equipmentId;
        this.exerciseId = exerciseId;
        this.name = name;
        this.type = EquipmentType.values()[type];
        this.isCustom = isCustom == 1;
    }

    public Equipment (int equipmentId, int exerciseId, String name, int typeOrdinal, boolean isCustom) {
        this ();
        this.equipmentId = equipmentId;
        this.exerciseId = exerciseId;
        this.name = name;
        this.type = EquipmentType.values()[typeOrdinal];
        this.isCustom = isCustom;
    }

    public Equipment (int exerciseId, String name, EquipmentType type, boolean isCustom) {
        this.exerciseId = exerciseId;
        this.name = name;
        this.type = type;
        this.isCustom = isCustom;
    }



    // some getters and setters
    public int getEquipmentID () { return equipmentId; }
    public void setEquipmentID (int equipmentID) { this.equipmentId = equipmentId; }
    public String getName () { return name; }
    public void setName (String name) { this.name = name; }
    public EquipmentType getType () { return type; }
    public int getTypeOrdinal () { return type.ordinal(); }
    public void setType (EquipmentType type) { this.type = type; }
    public boolean isCustom () { return isCustom; }
    public int getCustomOrdinal() { return isCustom ? 1 : 0;}
    public void setCustom (boolean isCustom) { this.isCustom = isCustom; }
    public int getExerciseId () { return exerciseId; }

    // return the string of the equipment type
    @Override
    public String toString () {
        return name;
    }
}
