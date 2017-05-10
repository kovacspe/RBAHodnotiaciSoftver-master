/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.kovacspe.rbahodnotiacisoftver;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 * Form pre hodnotenie kategorie Roboticka vyzva
 *
 * @author Peter
 */
public class RBARobotickaVyzva extends javax.swing.JFrame {

    List<JSpinner> Team1Spinners;
    List<JSpinner> Team2Spinners;
    List<RVTournamentPair> tournamentSpider;
    JComboBox selectTournamentPair;
    JComboBox selectType;
    JComboBox team1Chooser;
    JComboBox team2Chooser;
    JButton saveTeam1, saveTeam2, saveTournament;
    List<TeamRV> RVTeams;

    /**
     * Pridáva do container jedno prázne pole. Služi ako výplň GridLayoutu.
     *
     * @param cont - container
     * @param count - počet prázdnych polí
     */
    private void AddEmptyCell(Container cont, int count) {
        for (int i = 0; i < count; i++) {
            cont.add(new JLabel());
        }
    }

    /**
     * Spočíta koľko bodov získal tím. Zohľadňuje aj mód hry.
     *
     * @param mySpinners
     * @param opponentSpinners
     * @return
     */
    private int CountResults(List<JSpinner> mySpinners, List<JSpinner> opponentSpinners) {
        int result = 0;
        for (JSpinner s : mySpinners) {
            result = result + ((Integer) s.getValue());
        }

        return result;
    }

    public void LoadTeams() {
        team1Chooser.removeAllItems();
        team2Chooser.removeAllItems();
        RVTeams = RBAHodnotiaciSoftver.LoadTeams("RV.res");
        RVTeams.forEach(t -> team1Chooser.addItem(t));
        RVTeams.forEach(t -> team2Chooser.addItem(t));

    }

    /**
     * Udeli bonusove body pre timy, ktore odtlacili najvacsie zavazia Ulozi
     * pridelene body v prehladnom vypise do suboru
     */
    public void grantPointsForLevel5() {
        List<RoundPoints> allRoundPoints = new ArrayList<>();
        List<String> logs = new LinkedList<>();
        for (TeamRV t : RVTeams) {
            for (RoundPoints rp : t.allRoundPoints) {
                allRoundPoints.add(rp);
            }
        }
        allRoundPoints.sort(new Comparator<RoundPoints>() {

            @Override
            public int compare(RoundPoints t1, RoundPoints t2) {
                return t2.getWeight() - t1.getWeight();
            }
        });
        int max = 0;
        for (int i = 0; i < 3; i++) {
            if (!allRoundPoints.isEmpty()) {
                max = allRoundPoints.get(0).getWeight();
            }
            while (!allRoundPoints.isEmpty() && allRoundPoints.get(0).getWeight() == max) {
                allRoundPoints.get(0).setPointsForLevel(5, 5 - (i * 2));
                logs.add(allRoundPoints.get(0).team.Name + "(váha: " + max + ")" + "-->" + (5 - (i * 2)));
                allRoundPoints.remove(0);
            }
        }
        allRoundPoints.forEach(rp -> {
            rp.setPointsForLevel(5, 0);
        });
        try (PrintWriter out = new PrintWriter(new FileOutputStream("PridelenieBodovZaZavazia.txt"))) {
            logs.forEach((s) -> {
                out.println(s);
            });
        } catch (IOException ex) {

        }
        SaveTeams();
    }

    public void SortTeamsByBestResult() {
        RVTeams.sort(new Comparator<TeamRV>() {

            @Override
            public int compare(TeamRV t1, TeamRV t2) {
                return t2.getBestResult() - t1.getBestResult();
            }
        });
    }

    /**
     * Sortuje list RVTeams najprv podla Tier a potom podla poctu ziskanych
     * bodov
     */
    void SortTeamsByTierAndNumberOfPoints() {
        Comparator<TeamRV> cmprtr = new Comparator<TeamRV>() {

            @Override
            public int compare(TeamRV t1, TeamRV t2) {
                if (t2.getTier() == t1.getTier()) {
                    return t2.getBestResult() - t1.getBestResult();
                } else {
                    return t1.getTier() - t2.getTier();
                }
            }
        };
        RVTeams.sort(cmprtr);
    }

    /**
     * Pocita kolko riadkov ma mat tabulka, ktora sa ukaze v prehlade bodov
     *
     * @return
     */
    private int countTableSize() {
        int size = 0;
        for (TeamRV t : RVTeams) {
            size = size + t.allRoundPoints.size();
        }
        return size;
    }

    private String[][] WrapData() {
        String[][] data = new String[countTableSize()][10];
        //SortTeamsByID();
        int i = 0;
        for (TeamRV team : RVTeams) {
            for (RoundPoints rp : team.allRoundPoints) {

                data[i][0] = Integer.toString(team.getTeamID());
                data[i][1] = team.Name;
                data[i][2] = Integer.toString(team.allRoundPoints.indexOf(rp) + 1);
                data[i][3] = Integer.toString(rp.getLevel(0));
                data[i][4] = Integer.toString(rp.getLevel(1));
                data[i][5] = Integer.toString(rp.getLevel(2));
                data[i][6] = Integer.toString(rp.getLevel(3));
                data[i][7] = Integer.toString(rp.getLevel(4));
                data[i][8] = Integer.toString(rp.getWeight());
                data[i][9] = Integer.toString(rp.getSum());
                i++;
            }
        }

        return data;
    }

    /**
     * Funkcia vytvorí pavúkový turnajový systém. Vezme 8 resp ak je málo tímov
     * tak 4 najlepšie tímy a vytvorí postupového pavúka.
     *
     * @param bestTeams list najlepsich timov, pripadne zoradeny list vsetkych
     * timov
     * @return list turnajovych dvojic, ktore proti sebe budu hrat
     */
    public List<RVTournamentPair> CreateSpider(List<TeamRV> bestTeams) {
        List<RVTournamentPair> spider = new LinkedList<>();
        RVTournamentPair tournament1 = new RVTournamentPair(null, 1);
        spider.add(tournament1);

        if (bestTeams.size() < 8) {
            if (bestTeams.size() < 4) {
                JOptionPane.showMessageDialog(null, "Málo tímov na vytvorenie pavúka", "RBA", JOptionPane.INFORMATION_MESSAGE);
            } else {
                spider.add(new RVTournamentPair(bestTeams.get(0), bestTeams.get(3), tournament1, 2));
                spider.add(new RVTournamentPair(bestTeams.get(1), bestTeams.get(2), tournament1, 2));
                JOptionPane.showMessageDialog(null, "Vytvorený pavúk pre najlepšie tímy v počte tímov: 4", "RBA", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {

            RVTournamentPair tournament2 = new RVTournamentPair(tournament1, 2);
            RVTournamentPair tournament3 = new RVTournamentPair(tournament1, 2);
            spider.add(tournament2);
            spider.add(tournament3);

            spider.add(new RVTournamentPair(bestTeams.get(0), bestTeams.get(7), tournament2, 3));
            spider.add(new RVTournamentPair(bestTeams.get(1), bestTeams.get(6), tournament3, 3));
            spider.add(new RVTournamentPair(bestTeams.get(2), bestTeams.get(5), tournament3, 3));
            spider.add(new RVTournamentPair(bestTeams.get(3), bestTeams.get(4), tournament2, 3));
            JOptionPane.showMessageDialog(null, "Vytvorený pavúk pre najlepšie tímy v počte tímov: 8", "RBA", JOptionPane.INFORMATION_MESSAGE);
        }

        return spider;
    }

    /**
     * Nacita turnaju zo suboru
     */
    public void LoadTournaments() {
        tournamentSpider = RBAHodnotiaciSoftver.LoadTeams("RVtournaments");
        tournamentSpider.stream().forEach(tp -> {
            selectTournamentPair.addItem(tp);
        });
    }

    /**
     * Ulozi turnaje do suboru
     */
    public void SaveTournaments() {
        RBAHodnotiaciSoftver.SaveTeams(tournamentSpider, "RVtournaments.res");
        SaveTeams();
    }

    public void SaveTeams() {
        RBAHodnotiaciSoftver.SaveTeams(RVTeams, "RV.res");
    }

    /**
     * Vynuluje spinnery spinnery
     *
     * @param spinners
     */
    public void ClearSpinners(List<JSpinner> spinners) {
        for (JSpinner s : spinners) {
            s.setValue(0);
        }
    }

    /**
     * Ulozi vysledky zo spinnerov do teamu.
     *
     * @param spinners
     * @param team
     */
    public void SaveTeamResults(List<JSpinner> spinners, TeamRV team) {
        if (team != null) {
            int[] Points = new int[5];

            for (int i = 0; i < 4; i++) {
                Points[i] = (int) spinners.get(i).getValue();
            }
            Points[4] = 0;
            team.allRoundPoints.add(new RoundPoints(Points, (int) spinners.get(4).getValue(), team));
            ClearSpinners(spinners);
            SaveTeams();
        } else {
            JOptionPane.showMessageDialog(this, "Nebol vybraný žiaden tím", "RBA", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Creates new form RBARobotickaVyzva
     */
    public RBARobotickaVyzva() {
        initComponents();

        CreateRVMenu();
        Container cont = this.getContentPane();
        cont.setLayout(new GridLayout(9, 5, 20, 20));

        // Combobox na vyberanie typu zapasu
        JLabel selectTypeLabel = new JLabel("Typ zápasu:");
        cont.add(selectTypeLabel);
        selectType = new JComboBox();
        selectType.addItem(TournamentMode.BASIC);
        selectType.addItem(TournamentMode.SPIDER);
        selectType.addActionListener(e -> ChangeTournamentMode((TournamentMode) selectType.getSelectedItem()));
        cont.add(selectType);

        // Combobox na vyberanie zapadsu ak sa hra pavuk
        selectTournamentPair = new JComboBox();
        selectTournamentPair.setVisible(false);

        cont.add(selectTournamentPair);

        AddEmptyCell(cont, 2);
        JLabel team1Name = new JLabel();
        cont.add(team1Name);
        AddEmptyCell(cont, 3);
        JLabel team2Name = new JLabel();
        cont.add(team2Name);

        selectTournamentPair.addActionListener(e -> {
            if (this.currTournamentMode == TournamentMode.SPIDER && selectTournamentPair.getItemCount() > 0) {
                team1Name.setText("" + ((RVTournamentPair) selectTournamentPair.getSelectedItem()).team1);
                team2Name.setText("" + ((RVTournamentPair) selectTournamentPair.getSelectedItem()).team2);
            } else {
                team1Name.setText("");
                team2Name.setText("");
            }
        });

        //Vyberace timov
        team1Chooser = new JComboBox();
        cont.add(team1Chooser);
        AddEmptyCell(cont, 2);
        team1Chooser.addActionListener(e -> {
            ClearSpinners(Team1Spinners);
        });
        team2Chooser = new JComboBox();
        cont.add(team2Chooser);
        AddEmptyCell(cont, 1);
        team2Chooser.addActionListener(e -> {
            ClearSpinners(Team2Spinners);
        });

        Team1Spinners = new LinkedList<>();
        Team2Spinners = new LinkedList<>();

        JLabel team1Score = new JLabel();
        JLabel team2Score = new JLabel();

        // Hodnotenia levelov
        for (int i = 1; i < 6; i++) {
            JLabel t1label = new JLabel("Level " + i);
            JSpinner t1spin = new JSpinner();
            SpinnerModel sm1;
            if (i == 5) {
                sm1 = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
            } else {
                sm1 = new SpinnerNumberModel(0, 0, 5, 1);
            }
            t1spin.setModel(sm1);
            cont.add(t1label);
            cont.add(t1spin);
            t1spin.addChangeListener(e -> {
                team1Score.setText("SPOLU: " + Integer.toString(CountResults(Team1Spinners, Team2Spinners)));
            });
            Team1Spinners.add(t1spin);

            AddEmptyCell(cont, 1);
            JLabel t2label = new JLabel("Level " + i);
            SpinnerModel sm2;
            if (i == 5) {
                sm2 = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
            } else {
                sm2 = new SpinnerNumberModel(0, 0, 5, 1);
            }
            JSpinner t2spin = new JSpinner();
            t2spin.setModel(sm2);
            cont.add(t2label);
            cont.add(t2spin);
            t2spin.addChangeListener(e -> {
                team2Score.setText("SPOLU: " + Integer.toString(CountResults(Team2Spinners, Team1Spinners)));
            });
            Team2Spinners.add(t2spin);
        }

        saveTeam1 = new JButton("Ulož výsledky");
        saveTeam1.addActionListener(e -> {
            SaveTeamResults(Team1Spinners, (TeamRV) team1Chooser.getSelectedItem());
        });
        cont.add(saveTeam1);
        cont.add(team1Score);
        saveTournament = new JButton("Ulož turnaj");
        saveTournament.setVisible(false);
        saveTournament.addActionListener(e -> {
            setTournamentPairResults((RVTournamentPair) selectTournamentPair.getSelectedItem());
        });
        cont.add(saveTournament);
        saveTeam2 = new JButton("Ulož výsledky");
        saveTeam2.addActionListener(e -> {
            SaveTeamResults(Team2Spinners, (TeamRV) team2Chooser.getSelectedItem());
        });
        cont.add(saveTeam2);
        cont.add(team2Score);

    }

    /**
     * Zapíše výsledky turnajoveho stretnutia a zariadi aby lepsi tím postupil
     * do lepšej skupiny
     *
     * @param tp
     */
    public void setTournamentPairResults(RVTournamentPair tp) {
if (tp!=null){
        tp.team2.tournamentRoundPoints[tp.getSpiderLevel()] = CountResults(Team2Spinners, Team1Spinners);
        tp.team1.tournamentRoundPoints[tp.getSpiderLevel()] = CountResults(Team1Spinners, Team2Spinners);
        try {
            tp.moveToGroupForWinner();
            SaveTournaments();

        } catch (NoResultsFoundException ex) {
            JOptionPane.showMessageDialog(null, "Nepodarilo sa ulozit vysledky :", "RBA", JOptionPane.WARNING_MESSAGE);
        }
} else{
    JOptionPane.showMessageDialog(null, "Nebol vybraný duel", "RBA", JOptionPane.WARNING_MESSAGE);
}
    }

    /**
     * Vytvára horný MenuBar
     */
    public void CreateRVMenu() {
        JMenuBar mb = new JMenuBar();

        JMenu rv = new JMenu("Robotická výzva");
        JMenuItem createSpider = new JMenuItem("Vytvoriť pavúka");
        createSpider.addActionListener(e -> {
            SortTeamsByBestResult();
            tournamentSpider = CreateSpider(RVTeams);
            RBAHodnotiaciSoftver.SaveTeams(tournamentSpider, "tournamentSpider.res");
            selectTournamentPair.removeAllItems();
            tournamentSpider.stream().forEach(tp -> {
                selectTournamentPair.addItem(tp);
            });
        });
        JMenuItem loadTeams = new JMenuItem("Načítať tímy");
        loadTeams.addActionListener(e -> {
            LoadTeams();
        });

        JMenuItem finalRes = new JMenuItem("Ulož finálne výsledky");
        finalRes.addActionListener(e -> {
            SortTeamsByBestResult();
            RBAHodnotiaciSoftver.SaveTeams(RVTeams, "RVQualifFINALRESULTS.res");
            SortTeamsByTierAndNumberOfPoints();
            RBAHodnotiaciSoftver.SaveTeams(RVTeams, "RVFINALRESULTS.res");
        });

        JMenuItem loadSpider = new JMenuItem("Načítať pavúka");
        loadSpider.addActionListener(e -> {
            tournamentSpider = RBAHodnotiaciSoftver.LoadTeams("tournamentSpider.res");
            selectTournamentPair.removeAllItems();

        });

        JMenuItem grantPoints = new JMenuItem("Prideľ body za level 5");
        grantPoints.addActionListener(e -> {
            grantPointsForLevel5();
        });

        JMenuItem overview = new JMenuItem("Prehľad bodov");
        overview.addActionListener((ActionEvent e) -> {
            Overview FormOverview = new Overview();

            Object[] header = {"Číslo tímu", "Názov tímu", "Poradie jazdy", "Level 1", "Level 2", "Level 3", "Level 4", "Level 5", "Závažie [g]", "Súčet bodov"};
            int[] discols = {0, 1, 2, 9};
            FormOverview.InitTable(WrapData(), header, discols);

            FormOverview.InitSaveBTN();
            FormOverview.setVisible(true);
        });
        rv.add(overview);
        rv.add(grantPoints);
        rv.add(loadTeams);
        rv.add(loadSpider);
        rv.add(createSpider);
        rv.add(finalRes);
        mb.add(rv);
        mb.add(RBAHodnotiaciSoftver.CreateContextSwitchingMenu());
        this.setJMenuBar(mb);
    }

    private enum TournamentMode {
        BASIC, SPIDER

    }

    TournamentMode currTournamentMode = TournamentMode.BASIC;

    /**
     * Zmena herného módu
     *
     * @param newMode nový herný mód
     */
    private void ChangeTournamentMode(TournamentMode newMode) {
        currTournamentMode = newMode;
        switch (newMode) {
            case BASIC:
                team1Chooser.setVisible(true);
                team2Chooser.setVisible(true);
                saveTournament.setVisible(false);
                saveTeam1.setVisible(true);
                saveTeam2.setVisible(true);
                selectTournamentPair.setVisible(false);
                break;
            case SPIDER:
                team1Chooser.setVisible(false);
                team2Chooser.setVisible(false);
                saveTournament.setVisible(true);
                saveTeam1.setVisible(false);
                saveTeam2.setVisible(false);
                selectTournamentPair.setVisible(true);
                break;

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RBA - Robotická výzva");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 818, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 406, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
   // public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
     /*   try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RBARobotickaVyzva.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RBARobotickaVyzva.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RBARobotickaVyzva.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RBARobotickaVyzva.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
       /* java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RBARobotickaVyzva().setVisible(true);
            }
        });
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
