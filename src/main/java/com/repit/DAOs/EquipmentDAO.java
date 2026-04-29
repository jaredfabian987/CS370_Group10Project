package com.repit.DAOs;
import com.repit.Model.Equipment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/*
* private String equipmentID;
    // name of the equipment
    private String name;
    // time of the equipment, enum declared in a different file
    private EquipmentType type;
    // is the equipment user defined
    private boolean isCustom;
    // to find user ID
    private String userID;*/

public class EquipmentDAO extends BaseDAO{
    private static final String TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS equipment (" +
                    "equipmentId INTEGER PRIMARY KEY," +
                    "exerciseId INTEGER NOT NULL, "+
                    "name TEXT NOT NULL," +
                    "EquipmentType INTEGER NOT NULL, "+
                    "isCustom INTEGER NOT NULL "+
                    ")";
    private static final String INSERT_SQL =
            "INSERT INTO equipment (exerciseId, name, EquipmentType, isCustom) "+
                    "VALUES (?,?,?,?)";
    private static final String SELECT_EXERCISE_SQL =
            "SELECT * FROM equipment WHERE exerciseId = ?";
    private static final String SELECT_SQL =
            "SELECT * FROM equipment WHERE equipmentId = ?";
    private static final String DELETE_SQL =
            "DELETE FROM equipment WHERE exerciseId = ? AND userId = ?";
    private static final String UPDATE_SQL =
            "UPDATE equipment SET name=?, EquipmentType=?, isCustom=? WHERE equipmentId=?";

    public boolean saveEquipment(Equipment equipment){
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL);
            pstmt.setInt(1, equipment.getExerciseId());
            pstmt.setString(2, equipment.getName());
            pstmt.setInt(3, equipment.getTypeOrdinal());
            pstmt.setInt(4, equipment.getCustomOrdinal());
            pstmt.executeUpdate();

            return true;
        } catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Equipment> getEquipmentsFromExercise(int exerciseId){
        try {

            List<Equipment> equipments = new ArrayList<>();;
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(SELECT_EXERCISE_SQL);
            pstmt.setInt(1, exerciseId);
            //pstmt.executeUpdate();

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                equipments.add(new Equipment(
                        rs.getInt("equipmentId"),
                        rs.getInt("exerciseId"),
                        rs.getString("name"),
                        rs.getInt("EquipmentType"),
                        rs.getInt("isCustom")
                ));
            }
            return equipments;
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Equipment getEquipmentFromId(int equipmentId){
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(SELECT_SQL);
            pstmt.setInt(1, equipmentId);
            //pstmt.executeUpdate();

            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return new Equipment(
                        rs.getInt("equipmentId"),
                        rs.getInt("exerciseId"),
                        rs.getString("name"),
                        rs.getInt("EquipmentType"),
                        rs.getInt("isCustom")
                );
            }

        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean updateEquipment(Equipment equipment){
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(UPDATE_SQL);
            pstmt.setString(1, equipment.getName());
            pstmt.setInt(2, equipment.getTypeOrdinal());
            pstmt.setInt(3, equipment.getCustomOrdinal());
            pstmt.setInt(4, equipment.getExerciseId());
            pstmt.executeUpdate();

            return true;
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean deleteEquipment(int equipmentId){
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(DELETE_SQL);
            pstmt.setInt(1, equipmentId);
            pstmt.executeUpdate();
            return true;
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return  false;
    }
}
