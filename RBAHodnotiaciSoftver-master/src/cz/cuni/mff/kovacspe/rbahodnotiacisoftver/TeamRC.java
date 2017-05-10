/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.kovacspe.rbahodnotiacisoftver;

/**
 * Tim v kategorii Racing 
 * @author Peter
 */
public class TeamRC extends TeamBase {

    public String note;
    public String nativeGroup;
    private Integer BestQualificationTime;
    private int RaceRound;
    private int RaceRoundEntry;
    public int RaceNumber;
    boolean finished;
    
    /**
     * vracia cislo kola v ktorom sa model nachadza
     * @return 
     */
    public int getRaceRound(){
        return RaceRound;
    }
    
    /**
     * Vracia poradie v kole
     * @return 
     */
    public int getRaceRoundEntry(){
        return RaceRoundEntry;
    }
    
    /**
     * Vyvola sa po prejdeni cielovej ciary. Prepise polohu modelu
     * @param Entry poradie v kole
     */
    public void passFinishLine(int Entry){
        RaceRound++;
        RaceRoundEntry=Entry;
    }
    /**
     * Vyresetuje vysledky preteku, teda nastavi kolo na nulu
     */
    public void resetRaceStatus(){
        RaceRound=0;
        RaceRoundEntry=RaceNumber;
        finished=false;
    }

    /**
     * Premiena cas vo formate MM:SS na sekundy
     * @param time cas vo formate MM:SS
     * @return cas v seknudach
     */
    public static Integer ConvertTimeToSeconds(String time) {
        String[] MinSec = time.split(":");
        try {
            return Integer.parseInt(MinSec[0]) * 60 + Integer.parseInt(MinSec[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
            return null;
        }
    }

    /**
     * Premiena kvalifikacny cas timu z ulozenych sekund na format MM:SS
     * @return cas vo formate MM:SS
     */
    public String getTimeConvertedToString() {
        String s;
        try {
            s = Integer.toString(getBestQualificationTime() / 60) + ":" + Integer.toString(getBestQualificationTime() % 60);
        } catch (TimeNotSetException ex) {
            s = "NULL";
        }
        return s;
    }

    /**
     * Vracia ci model uz odjazdil kvalifikacnu jazdu
     * @return 
     */
    public boolean isTimeInitialized() {
        return BestQualificationTime != null;
    }

    /**
     * Priradzuje kvalifikacny cas
     * @param time 
     */
    public void setBestQualificationTime(Integer time) {
        BestQualificationTime = time;
    }

    public int getBestQualificationTime() throws TimeNotSetException {
        if (BestQualificationTime == null) {
            throw new TimeNotSetException();
        } else {
            return BestQualificationTime;
        }
    }

    public TeamRC(int TeamID, String Name) {
        super(TeamID, Name);
        BestQualificationTime = null;
        this.note = "";
        this.RaceNumber=0;
        resetRaceStatus();
    }
    
    
    public String toStringInRace(){
        return Name+"("+RaceNumber+")";
    }

}

class TimeNotSetException extends Exception {

}
