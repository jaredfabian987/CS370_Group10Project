package com.repit.model;

import com.repit.model.enums.EquipmentType;

public class Equipment {

    // id for finding the equipment in the DB
    private String equipmentID;
    // name of the equipment
    private String name;
    // time of the equipment, enum declared in a different file
    private EquipmentType type;
    // is the equipment user defined
    private boolean isCustom;
    // to find user ID
    private String userID;

    // constructors
    public Equipment() {
        this.isCustom = false;
    }

    public Equipment (String equipmentID, String name, EquipmentType type) {
        this ();
        this.equipmentID = equipmentID;
        this.name = name;
        this.type = type;
    }

    // some getters and setters
    public String getEquipmentID () { return equipmentID; }
    public void setEquipmentID (String equipmentID) { this.equipmentID = equipmentID; }
    public String getName () { return name; }
    public void setName (String name) { this.name = name; }
    public EquipmentType getType () { return type; }
    public void setType (EquipmentType type) { this.type = type; }
    public boolean isCustom () { return isCustom; }
    public void setCustom (boolean isCustom) { this.isCustom = isCustom; }
    public String getUserID () { return userID; }
    public void setUserID (String userID) { this.userID = userID; }

    // return the string of the equipment type
    @Override
    public String toString () {
        return name;
    }
}
