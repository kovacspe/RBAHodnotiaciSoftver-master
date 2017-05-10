/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.kovacspe.rbahodnotiacisoftver;

import java.io.Serializable;

/**
 * Obsahuje dodatocne udaje o time, ako su clenovia, skola a kategorie v ktorych sutazi.
 * Sluzi najma na vytvorenie prehladu zucastnenych timov
 * @author Peter
 */
public class Team extends TeamBase{

    public String School;
    public String Student1;
    public String Student2;
    public String Student3;
    public boolean VM;
    public boolean RC;
    public boolean RV;
    
    
    public Team(int TeamID,String Name, String School,String Student1,String Student2, String Student3, boolean VM,boolean RC, boolean RV){
        super(TeamID,Name);
        this.RC = RC;
        this.RV = RV;
        this.School = School;
        this.Student1 = Student1;
        this.Student2 = Student2;
        this.Student3 = Student3;
        this.VM = VM;
    }

}
