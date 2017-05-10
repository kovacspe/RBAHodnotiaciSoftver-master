/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.kovacspe.rbahodnotiacisoftver;

import java.io.Serializable;

/**
 * Zakladna trieda tim, od ktorej dedia triedy timov na konkretne kategorie
 * Obsahuje meno timu a ID
 * @author Peter
 */
public class TeamBase  implements Serializable{
        private final int TeamID;
    public String Name;
    
        public int getTeamID() {
        return TeamID;
    }
        
        public TeamBase(int TeamID, String Name){
            this.TeamID=TeamID;
            this.Name=Name;
        }
        
            @Override
    public String toString(){
        return Name+"(T"+TeamID+")";
    }
        
}
