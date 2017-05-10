/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.kovacspe.rbahodnotiacisoftver;



/**
 * Tim v kategorii Vlastny model
 * @author Peter Kovács
 */
public class TeamVM extends TeamBase{

    boolean havePoints;
    public int SoftwarePoints;
    public int ConstructionPoints;
    public int CreativityPoints;
    public int PresentationPoints;
    public int BonusPoints;
    public Integer Tier;

    /**
     * Sčíta body v jednotlivých okruhoch.
     * @return súčet bodov
     */
    public int getSum() {
        return SoftwarePoints + ConstructionPoints + CreativityPoints + PresentationPoints + BonusPoints;
    }

    
    public TeamVM(int TeamID, String Name){
super(TeamID,Name);
        this.BonusPoints=0;
        this.ConstructionPoints=0;
        this.CreativityPoints=0;
        this.PresentationPoints=0;
        this.SoftwarePoints=0;
        this.Tier=0;
        havePoints=false;
    }

}
