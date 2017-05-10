/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.kovacspe.rbahodnotiacisoftver;

import java.io.Serializable;

/**
 * Trieda predstavuje duel dvoch timov v pavukovom hernom systeme.
 * Vie urcit vitaza a posunut ho na dalsi duel
 * @author kovacspe
 */
public class RVTournamentPair implements Serializable {

    TeamRV team1;
    TeamRV team2;
    private final int spiderLevel;
    private final RVTournamentPair groupForWinner;

    public int getSpiderLevel() {
        return spiderLevel;
    }

    public RVTournamentPair(TeamRV team1, TeamRV team2, RVTournamentPair groupforWinner, int spiderLevel) {
        this.team1 = team1;
        this.team2 = team2;
        this.groupForWinner = groupforWinner;
        this.spiderLevel = spiderLevel;
    }

    public RVTournamentPair(RVTournamentPair groupForWinner, int spiderLevel) {
        this(null, null, groupForWinner, spiderLevel);
    }

    /**
     * Vyhodnoti, kto vyhral kolo.
     * @return vyherny tim
     * @throws NoResultsFoundException ak nejaky tim nema na dane kolo nastavene body
     */
    public TeamRV chooseWinner() throws NoResultsFoundException {
        if (team1.tournamentRoundPoints[spiderLevel] != -1 && team2.tournamentRoundPoints[spiderLevel] != -1) {
            if (team1.tournamentRoundPoints[spiderLevel] > team2.tournamentRoundPoints[spiderLevel]) {
                return team1;
            } else if (team1.tournamentRoundPoints[spiderLevel] < team2.tournamentRoundPoints[spiderLevel]) {
                return team2;
            } else {
                return team1; // DOPLNIT POROVNANIE S CASOM
            }
        } else {
            throw new NoResultsFoundException();
        }
    }

    /**
     * Posunie vitaza duelu do dalsieho duelu, ak je to mozne.
     * Znizi vitazovi atribut Tier, cim ho klasifikuje ako lepsieho
     * @throws NoResultsFoundException ak nejaky z timov nema nastavena body za toto kolo 
     */
    public void moveToGroupForWinner() throws NoResultsFoundException {
        if (groupForWinner != null) {
            if (groupForWinner.team1 == null) {
                groupForWinner.team1 = chooseWinner();
            } else {
                groupForWinner.team2 = chooseWinner();
            }
        }
        chooseWinner().setTier(spiderLevel - 1);
    }

    @Override
    public String toString() {
        return "U" + spiderLevel + ": " + team1 + " vs. " + team2;
    }

}

class NoResultsFoundException extends Exception {

}
