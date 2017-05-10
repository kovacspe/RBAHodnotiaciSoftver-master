/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.kovacspe.rbahodnotiacisoftver;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.scene.control.ComboBox;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 * Form na hodnotenie kategorie Racing
 *
 * @author Peter
 */
public class RBARacing extends javax.swing.JFrame {

    public List<TeamRC> RCTeams;
    public List<RacingGroup> Groups;
    private JPanel BTNcont;
    JComboBox chooseGroupComboBox;
    private JPanel tablePanel;
    private JTable table;
    Overview RacingTable;

    /**
     * Usporiada list RCTeams podla kvalifikacneho casu
     */
    private void SortTeamsByQualificationTime() {
        Comparator<TeamRC> cmprtr = new Comparator<TeamRC>() {
            @Override
            public int compare(TeamRC t, TeamRC t1) {
                try {
                    return t.getBestQualificationTime() - t1.getBestQualificationTime();
                } catch (TimeNotSetException ex) {
                    if (!t.isTimeInitialized()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            }

        };
        RCTeams.sort(cmprtr);
    }

    /**
     * Usporiada list RCTeams podla ID timu
     */
    private void SortTeamsByID() {
        Comparator<TeamRC> cmprtr = new Comparator<TeamRC>() {
            @Override
            public int compare(TeamRC t, TeamRC t1) {
                return t.getTeamID() - t1.getTeamID();
            }

        };
        RCTeams.sort(cmprtr);
    }

    /**
     * Vytvori horny MenuBar
     *
     * @return
     */
    private JMenuBar CreateRCMenuBar() {
        JMenuBar mb = new JMenuBar();
        JMenu teamManager = new JMenu("Správa súťaže");

        JMenuItem qualification = new JMenuItem("Hodnotenie kvalifikácie");
        qualification.addActionListener(e -> OpenQualificationTable());
        teamManager.add(qualification);

        JMenuItem makeGroups = new JMenuItem("Vytvoriť skupiny na pretek");
        makeGroups.addActionListener(e -> {
            try {
                int groupsize = Integer.parseInt(
                        (String) JOptionPane.showInputDialog(this, "Zadajte maximálnu kapacitu skupiny", "Kapacita skupiny", JOptionPane.PLAIN_MESSAGE)
                );
                CreateRacingGroups(groupsize);
                CreateAndShowRacingGUI();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Nepodarilo sa vytvoriť skupiny. Chyba pri zadávaní veľkosti skupiny", "Chyba", JOptionPane.ERROR_MESSAGE);
            }
        });
        teamManager.add(makeGroups);

        JMenuItem loadGroups = new JMenuItem("Načítať skupiny na pretek");
        loadGroups.addActionListener(e -> CreateAndShowRacingGUI());
        teamManager.add(loadGroups);

        JMenuItem export = new JMenuItem("Exportovať výsledky pretekov");
        export.addActionListener(e -> RBAHodnotiaciSoftver.SaveTeams(Groups, "RCFINALRESULTS.res"));
        teamManager.add(export);
        mb.add(teamManager);
        mb.add(RBAHodnotiaciSoftver.CreateContextSwitchingMenu());
        return mb;
    }

    /**
     * Vytvori skupiny na pretek. Snazi sa naplnit kazdu skupinu na plnu
     * kapacitu
     *
     * @param groupSize kapacita skupiny
     */
    private void CreateRacingGroups(int groupSize) {
        SortTeamsByQualificationTime();

        RBAHodnotiaciSoftver.SaveTeams(RCTeams, "RCQualifFINALRESULTS.res");
        int i = 0;
        RacingGroup betterGroup = null;
        List<TeamRC> tmp = new ArrayList<>();
        List<RacingGroup> groups = new ArrayList<>();
        while (i < RCTeams.size()) {
            RCTeams.get(i).RaceNumber = i % groupSize;
            RCTeams.get(i).nativeGroup = "Group" + ((char) (65 + (i / groupSize)));
            tmp.add(RCTeams.get(i));
            if (i % groupSize == groupSize - 1) {
                RacingGroup rg = new RacingGroup("Group" + ((char) (65 + (i / groupSize))), tmp, 5);
                rg.betterGroup = betterGroup;

                betterGroup = rg;
                groups.add(rg);
                tmp = new ArrayList<>();
            }
            i++;
        }
        if ((i - 1) % groupSize != groupSize - 1) {
            RacingGroup rg = new RacingGroup("Group" + ((char) (65 + (i / groupSize))), tmp, 5);
            rg.betterGroup = betterGroup;

            betterGroup = rg;
            groups.add(rg);
            tmp = new ArrayList<>();
        }
        RBAHodnotiaciSoftver.SaveTeams(groups, "RacingGroups.dat");
    }

    /**
     * Nacita skupiny na pretek
     */
    private void LoadRacingGroups() {
        Groups = RBAHodnotiaciSoftver.LoadTeams("RacingGroups.dat");
    }

    /**
     * Refreshuje malu aj velku tabulku
     */
    public void refreshTable() {
        try {
            RacingGroup currRacingGroup = ((RacingGroup) chooseGroupComboBox.getSelectedItem());
            RacingTable.UpdateTable(currRacingGroup.WrapGroupData());
            UpdateTable(currRacingGroup.WrapGroupData());
        } catch (NullPointerException ex) {

        }
    }

    /**
     * Vytvori tlacidla a tabulku pre novu pretekarsku skupinu
     *
     * @param g
     */
    private void setGroup(RacingGroup g) {
        try {
            
            BTNcont.removeAll();
            tablePanel.removeAll();

            BTNcont.setLayout(new GridLayout(g.readTeams().size(), 1));
            for (TeamRC t : g.readTeams()) {
                TeamRCBTN b = new TeamRCBTN(t.toStringInRace(), g, t, this);
                BTNcont.add(b);
            }
            String header2[] = {"Pozícia", "Názov tímu", "Kolo", "Stav"};
            
            table = new JTable(g.WrapGroupData(), header2);
            tablePanel.setLayout(new BorderLayout());
            tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
            tablePanel.add(table, BorderLayout.CENTER);
            tablePanel.setVisible(true);
            tablePanel.repaint();
            BTNcont.repaint();
            this.pack();
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Skupina neexistuje", "RBA", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Spusti pretek alebo ukonci pretek. Pred pretekom resetuje vysledky timov
     * a otvori tabulku, ktora sa da umiestnit na dataprojektor
     */
    private void StartRace() {
        try {
            RacingGroup currRacingGroup = ((RacingGroup) chooseGroupComboBox.getSelectedItem());
            if (currRacingGroup.isFinal == false) {
                if (!currRacingGroup.isRunning) {
                    currRacingGroup.ResetResults();
                    currRacingGroup.run();
                    RacingTable = new Overview();
                    String[] header = {"Pozícia", "Názov tímu", "Kolo", "Stav"};
                    RacingTable.InitTable(currRacingGroup.WrapGroupData(), header);
                    RacingTable.setVisible(true);

                } else {
                    BTNcont.removeAll();
                    BTNcont.repaint();

                    currRacingGroup.SaveFinalResults();
                }
            } else {
                int answ = JOptionPane.showConfirmDialog(this, "Pretek už prebehol. Prajete si zresetovať jeho výsledky?", "RBA", JOptionPane.YES_NO_OPTION);
                if (answ == 0) {
                    currRacingGroup.isFinal = false;
                    currRacingGroup.ResetResults();
                    UpdateTable(currRacingGroup.WrapGroupData());

                }
            }

        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Nebola zvolena validna skupina", "RBA", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Vytvori GUI pre pretek
     */
    public void CreateAndShowRacingGUI() {
        //this.CreateRCMenuBar();
        Container cont = this.getContentPane();
        cont.removeAll();
        LoadRacingGroups();
        cont.setLayout(new BorderLayout());

        JPanel control = new JPanel();
        control.setLayout(new GridLayout(1, 2));

        chooseGroupComboBox = new JComboBox();
        Groups.forEach(g -> chooseGroupComboBox.addItem(g));
        chooseGroupComboBox.addActionListener(e -> setGroup((RacingGroup) chooseGroupComboBox.getSelectedItem()));
        control.add(chooseGroupComboBox);
        JButton BTNStart = new JButton("Start");
        BTNStart.addActionListener(e -> {
            StartRace();
        });

        control.add(BTNStart);

        cont.add(control, BorderLayout.NORTH);

        JPanel groupStuff = new JPanel();
        groupStuff.setLayout(new GridLayout(1, 2));
        BTNcont = new JPanel();
        //BTNcont.ad
        groupStuff.add(BTNcont);
        tablePanel = new JPanel();
        groupStuff.add(tablePanel);
        cont.add(groupStuff);
        this.pack();

        //this.
    }

    /**
     * Zabali kvalifikacne vysledky do formatu tabulky
     *
     * @return
     */
    private String[][] WrapRCQualificationData() {
        SortTeamsByID();
        String[][] table = new String[RCTeams.size()][4];
        for (TeamRC t : RCTeams) {
            int i = RCTeams.indexOf(t);
            table[i][0] = Integer.toString(t.getTeamID());
            table[i][1] = t.Name;
            table[i][2] = t.getTimeConvertedToString();
            table[i][3] = t.note;
        }
        return table;
    }

    /**
     * Otvori tabulku v ktorej sa daju upravovat casy kvalifikacnych jazd
     */
    private void OpenQualificationTable() {
        if (!RCTeams.isEmpty()) {
            Overview over = new Overview();
            over.setTitle("Racing - kvalifikácia");
            String[] header = {"Team ID", "Názov tímu", "Najlepšia jazda", "Poznámka"};
            int[] discols = {0, 1};
            over.InitTable(WrapRCQualificationData(), header, discols);
            over.InitSaveBTN();
            over.setVisible(true);
            System.out.println("Tabulka hodnotenia bz mala byt otvorena");
        }
    }

    void LoadTeams() {
        RCTeams = RBAHodnotiaciSoftver.LoadTeams("RC.res");

    }

    void SaveTeams() {
        RBAHodnotiaciSoftver.SaveTeams(RCTeams, "RC.res");
    }

    /**
     * Rehreshuje malu tabulku, pri preteku.
     *
     * @param data data tabulky
     */
    public void UpdateTable(String[][] data) {

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                table.setValueAt(data[i][j], i, j);
            }
        }

    }

    /**
     * Creates new form NewJFrame
     */
    public RBARacing() {
        initComponents();
        this.setJMenuBar(CreateRCMenuBar());

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
        setTitle("Racing");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
