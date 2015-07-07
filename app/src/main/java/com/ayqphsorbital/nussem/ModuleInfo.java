package com.ayqphsorbital.nussem;


/**
 * Created by HanSiang on 07/07/2015.
 * http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
 */
public class ModuleInfo {

    //private variables
    int _id;
    String _ModuleCode;
    String _ModuleTitle;

    // Empty constructor
    public ModuleInfo(){

    }
    // constructor
    public ModuleInfo(int id, String ModuleCode, String ModuleTitle){
        this._id = id;
        this._ModuleCode = ModuleCode;
        this._ModuleTitle = ModuleTitle;
    }

    // constructor
    public ModuleInfo(String ModuleCode, String ModuleTitle){
        this._ModuleCode = ModuleCode;
        this._ModuleTitle = ModuleTitle;
    }
    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting name
    public String getModuleCode(){
        return this._ModuleCode;
    }

    // setting name
    public void setModuleCode(String name){
        this._ModuleCode = name;
    }

    // getting phone number
    public String getModuleTitle(){
        return this._ModuleTitle;
    }

    // setting phone number
    public void setModuleTitle(String phone_number){
        this._ModuleTitle = phone_number;
    }
}