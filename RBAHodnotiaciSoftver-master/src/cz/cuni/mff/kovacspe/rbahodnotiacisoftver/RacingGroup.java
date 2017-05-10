/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.kovacspe.rbahodnotiacisoftver;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * Skupina timov na pretek. V hlavnom preteku vzdy sutazi skupina timov, ktore sa medzi sebou pretekaju.
 * Obsahuje funkcie na ovladanie preteku a postup najlepsich pretekarov do lepsej skupiny
 * @author Peter
 */
public class RacingGroup implements Serializable {

    private List<TeamRC> teams;
    boolean isRunning;
    boolean isFinal;
    String groupLabel;
    RacingGroup betterGroup; // null ak je to prva skupina
    private int maxRounds;
    private int counter;
    int[] roundEntryNumbers;

    public String[][] WrapGroupData() {
        SortTeamsByRace();
        
        String[][] data = new String[teams.size()][4];
        for (int i = 0; i < teams.size(); i++) {
            data[i][0] = (i + 1) + ".";
            data[i][1] = teams.get(i).toStringInRace();
            data[i][2] = teams.get(i).getRaceRound() + "";
            if (teams.get(i).finished) {
                data[i][3] = "FINISHED";
            } else {
                data[i][3] = "";
            }
        }
        return data;
    }

    public RacingGroup(String groupLabel, List<TeamRC> teams, int maxRounds) {
        this.groupLabel = groupLabel;
        this.maxRounds = maxRounds;
        this.teams = teams;
        isRunning = false;
        isFinal = false;
        counter = teams.size() - 1;
        ResetResults();
    }

    /**
     * Zoradi timy podla poradia v tomto preteku
     */
    private void SortTeamsByRace() {
        teams.sort(new Comparator<TeamRC>() {
            @Override
            public int compare(TeamRC t, TeamRC t1) {
                if (t.getRaceRound() > t1.getRaceRound()) {
                    return -1;
                } else if (t.getRaceRound() < t1.getRaceRound()) {
                    return 1;
                } else if (t.getRaceRoundEntry() > t1.getRaceRoundEntry()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    /**
     * Ulozi tuto skupinu ako finalnu
     */
    public void SaveFinalResults() {
        SortTeamsByRace();
        
        MoveWinnersToBetterGroup();
        isFinal=true;
        RBAHodnotiaciSoftver.SaveTeams(teams, groupLabel + "_FINAL.res");
    }

    /**
     * Pripravy ciste vysledky pre kazdy tim
     */
    public final void ResetResults() {
        for (int i = 0; i < teams.size(); i++) {
            teams.get(i).RaceNumber = i + 1;
            teams.get(i).resetRaceStatus();
        }
        roundEntryNumbers = new int[maxRounds];
        for (int i = 0; i < maxRounds; i++) {
            roundEntryNumbers[i] = 0;
        }
        
    }
    public void run(){
        isRunning=true;
    }
    /**
     * vrati timy v skupine
     * @return 
     */
    
public List<TeamRC> readTeams() {
        return teams;
    }

    /**
     * prida tim do skupiny
     * @param team 
     */
    public void addTeam(TeamRC team) {
        counter++;
        team.RaceNumber = counter;
        teams.add(team);
    }

    /**
     * presunie ak mozno dva vitzne timy do lepsej skupiny
     */
    public void MoveWinnersToBetterGroup() {
        if (betterGroup != null) {
            SortTeamsByRace();
            if (teams.size() > 0) {

                betterGroup.addTeam(teams.get(0));
                teams.remove(0);
                if (teams.size() > 0) {
                    betterGroup.addTeam(teams.get(1));
                    teams.remove(0);
                }
            }
        }
    }

    /**
     * Vrati poradie v ktorom vosiel pretekar do daneho kola
     * @param round kolo do ktoreho vosiel
     * @return poradie v danom kole
     */
    private int getRoundEntryNumber(int round) {
        roundEntryNumbers[round]++;
        return roundEntryNumbers[round];
    }

    /**
     * Spusta sa ked tim prejde cielovou ciarov, teda mu treba zapocitat jeho kolo a prepocitat polohu v poradi
     * @param team tim ktorz prekrocil check point
     */
    public void finishRound(TeamRC team) {

        if (teams.contains(team)) {
            if (!team.finished) {
                team.passFinishLine(getRoundEntryNumber(team.getRaceRound()));
            }
            if (team.getRaceRound() >= maxRounds) {
                team.finished = true;
            }

        }
    }

    @Override
    public String toString() {
        return groupLabel;
    }

    class TeamNotFoundException extends Exception {

    }

}
