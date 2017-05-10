/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.kovacspe.rbahodnotiacisoftver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

/**
 * Konvertuje vysledky do TeX
 *
 * @author Peter
 */
public class TeXConvertor {

    /**
     * Skonvertuje zoradeny list timov do tabulky v TeX formate
     *
     * @param teams zoradeny list timov
     */
    public static void ConvertVMResultsToTeX(List<TeamVM> teams) {
        try (PrintWriter out = new PrintWriter(new FileOutputStream("VMresults.tex"))) {
            out.println("\\begin{tabular}{|l|l|l|l|}");
            out.println("Umiestnenie&Kód tímu&Názov tímu&Počet bodov\\\\");
            int i = 1;
            int currTier = 1;
            for (TeamVM t : teams) {
                if (t.Tier == currTier) {
                    if (currTier == 1) {
                        out.println(i + "&T" + t.getTeamID() + "&" + t.Name + "&" + t.getSum() + "\\\\");
                        i++;
                    } else {
                        out.println(i + "&T" + t.getTeamID() + "&" + t.Name + "\\\\");
                    }

                } else {
                    currTier = t.Tier;
                    i++;
                    out.println("\\end{tabular}");
                    out.println("\\bf{" + currTier + ". úroveň}");
                    out.println("\\begin{tabular}{|l|l|l|}");
                    out.println("Umiestnenie&Kód tímu&Názov tímu\\\\");
                    out.println(i + "&T" + t.getTeamID() + "&" + t.Name + "\\\\");

                }

            }
            out.println("\\end{tabular}");
        } catch (IOException ex) {
            System.err.println("Nepodarilo sa zapisat");
        }
    }

    /**
     * Skonvertuje zoradeny list timov do tabulky v TeX formate
     *
     * @param teams zoradeny list timov
     */
    public static void ConvertRCQualifiactionResultsToTeX(List<TeamRC> teams) {
        try (PrintWriter out = new PrintWriter(new FileOutputStream("RCQualif.tex"))) {

            out.println("\\begin{tabular}{|l|l|l|l|}");
            int i = 1;
            for (TeamRC t : teams) {
                try {
                    out.println(i + "& T" + t.getTeamID() + "&" + t.Name + "&" + t.getBestQualificationTime() + "\\\\");
                } catch (TimeNotSetException ex) {
                    out.println(i + "& T" + t.getTeamID() + "&" + t.Name + "&-\\\\");
                }
                i++;
            }
            out.println("\\end{tabular}");
        } catch (IOException ex) {
            System.err.println("Nepodarilo sa zapisat");
        }
    }

    /**
     * Skonvertuje zoradeny list skupin na pretek do tabulky v TeX formate
     *
     * @param teams zoradeny list skupin na pretek
     */
    public static void ConvertRCResultsToTeX(List<RacingGroup> groups) {
        try (PrintWriter out = new PrintWriter(new FileOutputStream("RCraces.tex"))) {
            out.println("\\begin{tabular}{|l|l|l|l|}");
            int i = 1;
            for (RacingGroup g : groups) {
                for (TeamRC t : g.readTeams()) {
                    out.println(i + "& T" + t.getTeamID() + "&" + t.Name + "&" + t.nativeGroup + "\\\\");
                    i++;
                }
            }
            out.println("\\end{tabular}");
        } catch (IOException ex) {
            System.err.println("Nepodarilo sa zapisat");
        }
    }

        /**
     * Skonvertuje zoradeny list timov do tex tabulky
     *
     * @param teams zoradeny list skupin na pretek
     */
    public static void ConvertRVQualifResultsToTeX(List<TeamRV> teams) {

        try (PrintWriter out = new PrintWriter(new FileOutputStream("RVQualifResults.tex"))) {
            out.println("\\begin{tabular}{|l|l|l|l|}");
            out.println("Umiestnenie&Kód tímu&Názov tímu&Maximálny počet bodov v kvalifikácií\\\\");
            int i = 1;
            int currTier = 1;
            for (TeamRV t : teams) {
                if (t.getTier() < 10) {

                    out.println(i + "&T{\\bf" + t.getTeamID() + "}&{\\bf" + t.Name + "}&" + t.getBestResult() + "\\\\");
                    i++;
                } else {
                    out.println(i + "&T" + t.getTeamID() + "&" + t.Name + "&" + t.getBestResult() + "\\\\");
                }

            }
            out.println("\\end{tabular}");
        } catch (IOException ex) {
            System.err.println("Nepodarilo sa zapisat");
        }
    }

    /**
     * Skonvertuje zoradeny list timov do tex tabulky
     * @param teamsAfterSpider 
     */
    public static void ConvertRVResultsToTeX(List<TeamRV> teamsAfterSpider) {
        try (PrintWriter out = new PrintWriter(new FileOutputStream("RVResults.tex"))) {
            out.println("\\begin{tabular}{|l|l|l|l|}");
            out.println("Umiestnenie&Kód tímu&Názov tímu\\\\");
            int i = 1;
            int currTier = 1;
            for (TeamRV t : teamsAfterSpider) {
                out.println(i + "&T" + t.getTeamID() + "&" + t.Name + "\\\\");

            }
            out.println("\\end{tabular}");
        } catch (IOException ex) {
            System.err.println("Nepodarilo sa zapisat");
        }
    }
}
