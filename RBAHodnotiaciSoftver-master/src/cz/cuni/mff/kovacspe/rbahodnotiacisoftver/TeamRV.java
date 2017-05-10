/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.kovacspe.rbahodnotiacisoftver;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Tim , ktory sutazi v kategorii Roboticka vyzva
 * @author Peter
 */
public class TeamRV extends TeamBase {

    public List<RoundPoints> allRoundPoints; 
    public int[] tournamentRoundPoints = new int[5];
    public int ActualRound;
    private Integer Tier;
   

    public void setTier(int tier) {
        Tier = tier;
    }

    public int getTier() {
        return Tier;
    }
    
    /**
     * 
     * @return 
     */
    public int getBestResult(){
        int max = 0;
        for(RoundPoints r:allRoundPoints){
            if (r.getSum()>max){
                max=r.getSum();
            }
        }
        return max;
    }

    public TeamRV(int TeamID, String Name) {
        super(TeamID, Name);
        allRoundPoints = new LinkedList<>();
        ActualRound = 1;
        Tier=10;
        
    }


}

class PointsNotSetException extends Exception {

}

class RoundPoints implements Serializable{
    private int[] points;
    private int weight; // in grams
    TeamRV team;

    /**
     * Vrati celkovy pocet ziskanych bodov
     * @return pocet bodov za dane kolo
     */
    public int getSum(){
        int sum = 0;
        try {

            for (int i = 0; i < 5; i++) {
                sum = sum + points[i];
            }
        } catch (ArrayIndexOutOfBoundsException ex) {

        }
        return sum;
    }
    
    /**
     * Uklada body pre level 5
     * @param p pocet bodov
     */
    public void setPointsForLevel(int level,int p){
        points[level-1]=p;
    }
    
    public void setWeight(int weight){
        this.weight=weight;
    }
    
    public int getWeight(){
        return weight;
    }
    
    public int getLevel(int level){
        return points[level];
    }
    
    public RoundPoints(int[] points,int weight, TeamRV team ){
        this.points=points;
        this.weight=weight;
        this.team=team;
    }
}
