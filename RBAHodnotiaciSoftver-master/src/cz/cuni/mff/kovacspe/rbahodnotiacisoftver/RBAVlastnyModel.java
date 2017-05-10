/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.kovacspe.rbahodnotiacisoftver;

import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * Form pre hodnotenie kategorie Vlasny model
 * @author Peter
 */
public class RBAVlastnyModel extends javax.swing.JFrame {

    /**
     * Creates new form RBAVlastnyModel
     */
    public List<TeamVM> VMTeams;

    /**
     * Utriedi list tímov podľa počtu bodov.
     */
    void SortTeamsByNumberOfPoints() {
        Comparator<TeamVM> cmprtr = new Comparator<TeamVM>() {

            public int compare(TeamVM t1, TeamVM t2) {
                return t2.getSum() - t1.getSum();
            }
        };
        VMTeams.sort(cmprtr);
    }

    /**
     * Utriedi list tímov podľa Tier a potom podla počtu bodov.
     */
    void SortTeamsByTierAndNumberOfPoints() {
        Comparator<TeamVM> cmprtr = new Comparator<TeamVM>() {

            @Override
            public int compare(TeamVM t1, TeamVM t2) {
                if (t2.Tier.equals(t1.Tier)) {
                    return t2.getSum() - t1.getSum();
                } else {
                    return t1.Tier - t2.Tier;
                }
            }
        };
        VMTeams.sort(cmprtr);
    }

    /**
     * Načíta tímy zo súboru VM.res
     */
    void LoadTeams() {
        TeamsComboBox.removeAllItems();
        VMTeams = RBAHodnotiaciSoftver.LoadTeams("VM.res");
        VMTeams.forEach(t -> {TeamsComboBox.addItem(t);});

    }

    /**
     * Vracia súčet bodov v jednotlivých hodnotiacich okruhoch.
     *
     * @return
     */
    private int CountVMPoints() {
        return jSlider1.getValue() + jSlider2.getValue() + jSlider3.getValue() + jSlider4.getValue() + Integer.parseInt(jTextField1.getText());
    }

    /**
     * Pripravuje tímy na zobrazenie v tabuľke. Preloží všetky atribúty do
     * stringu a uloží do dvojrozmerného poľa.
     *
     * @return
     */
    private String[][] WrapData() {
        String[][] data = new String[VMTeams.size()][9];
        SortTeamsByNumberOfPoints();
        VMTeams.forEach(team -> {
            int i = VMTeams.indexOf(team);
            data[i][0] = Integer.toString(team.getTeamID());
            data[i][1] = team.Name;
            data[i][2] = Integer.toString(team.SoftwarePoints);
            data[i][3] = Integer.toString(team.ConstructionPoints);
            data[i][4] = Integer.toString(team.CreativityPoints);
            data[i][5] = Integer.toString(team.PresentationPoints);
            data[i][6] = Integer.toString(team.BonusPoints);
            data[i][7] = Integer.toString(team.getSum());
            data[i][8] = Integer.toString(team.Tier);

        });

        return data;
    }

    /**
     *
     * @return Vracia menu pre sekciu Vlastný model
     */
    JMenuBar CreateMenu() {
        JMenu menu = new JMenu("Správa tímov");
        JMenuBar menubar = new JMenuBar();
        menubar.add(menu);
        JMenuItem loadteam = new JMenuItem("Načítaj tímy");
        JMenuItem saveteam = new JMenuItem("Ulož tímy");
        JMenuItem overview = new JMenuItem("Prehľad bodov");
        JMenuItem export = new JMenuItem("Exportuj vysledky");
        loadteam.addActionListener(e -> {
            LoadTeams();
        });
        saveteam.addActionListener(e -> {
            SerializeTeams();
        });
        export.addActionListener(e -> {
            SortTeamsByTierAndNumberOfPoints();
            SerializeTeams("FINALRESULTS");
        });

        overview.addActionListener((ActionEvent e) -> {
            Overview FormOverview = new Overview();

            Object[] header = {"Číslo tímu", "Názov tímu", "Softvér", "Konštrukcia", "Kreativita", "Prezentácia", "Bonus", "Suma", "Umiestnenie"};
            int[] discols = {0, 1};
            FormOverview.InitTable(WrapData(), header, discols);

            FormOverview.InitSaveBTN();
            FormOverview.setVisible(true);
        });
        menu.add(loadteam);
        menu.add(saveteam);
        menu.add(overview);
        menu.add(export);
        menubar.add(RBAHodnotiaciSoftver.CreateContextSwitchingMenu());
        return menubar;
    }

    /**
     * Inicializuje listeners na všetkých komponentách okrem menu.
     */
    public void initListeners() {
        jSlider1.addChangeListener(e -> {
            jLabel5.setText(String.valueOf(jSlider1.getValue()));
        });
        jSlider2.addChangeListener(e -> {
            jLabel6.setText(String.valueOf(jSlider2.getValue()));
        });
        jSlider3.addChangeListener(e -> {
            jLabel7.setText(String.valueOf(jSlider3.getValue()));
        });
        jSlider4.addChangeListener(e -> {
            jLabel8.setText(String.valueOf(jSlider4.getValue()));
        });

        jSlider1.addChangeListener(e -> {
            jLabel10.setText("Spolu: " + String.valueOf(CountVMPoints()));
        });
        jSlider2.addChangeListener(e -> {
            jLabel10.setText("Spolu: " + String.valueOf(CountVMPoints()));
        });
        jSlider3.addChangeListener(e -> {
            jLabel10.setText("Spolu: " + String.valueOf(CountVMPoints()));
        });
        jSlider4.addChangeListener(e -> {
            jLabel10.setText("Spolu: " + String.valueOf(CountVMPoints()));
        });
        jTextField1.addActionListener(e -> {
            jLabel10.setText("Spolu: " + String.valueOf(CountVMPoints()));
        });

        jButton1.addActionListener(e -> {
            SaveTeamPoints((TeamVM)TeamsComboBox.getSelectedItem());
            SerializeTeams();
        });

        TeamsComboBox.addActionListener(e -> {
            if (TeamsComboBox.getItemCount() > 0) {
                SetSliders((TeamVM)TeamsComboBox.getSelectedItem());
            }
        });
        this.setJMenuBar(CreateMenu());
    }

    /**
     * Spúšťa sa po zmene hodnoteného tímu. Nahodí aktuálne skóre do polí a
     * posuvníkov.
     *
     * @param team Tím, ktorý sa zobrazuje.
     */
    void SetSliders(TeamVM team) {
        try {
            jSlider1.setValue(team.SoftwarePoints);
            jSlider2.setValue(team.ConstructionPoints);
            jSlider3.setValue(team.CreativityPoints);
            jSlider4.setValue(team.PresentationPoints);
            jTextField1.setText(team.BonusPoints + "");
            jLabel10.setText("Spolu: " + String.valueOf(CountVMPoints()));
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "Tím neexistuje", "RBA", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ukladá nové body tímu po stlačení ukladacie tlačidla.
     *
     * @param team - Tím, ktorému sa menia body.
     */
    void SaveTeamPoints(TeamVM team) {
        try{
        team.SoftwarePoints = jSlider1.getValue();
        team.ConstructionPoints = jSlider2.getValue();
        team.CreativityPoints = jSlider3.getValue();
        team.PresentationPoints = jSlider4.getValue();
        team.BonusPoints = Integer.parseInt(jTextField1.getText());
        team.havePoints = true;
        SortTeamsByNumberOfPoints();
        } catch(NullPointerException ex){
            JOptionPane.showMessageDialog(null, "Tím neexistuje", "RBA", JOptionPane.ERROR_MESSAGE);
        } catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(null, "V textovom poli musí byť číslo", "RBA", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Zavolá SerializeTeams bez špeciálneho označenia.
     */
    void SerializeTeams() {
        SerializeTeams("");
    }

    /**
     * Serializuje list, v ktorom sú uložené tímy účastniace sa Vlastného
     * modelu. Vytvára súbor VM<label>.res, kde <label> je zadaný parameter.
     *
     * @param label - Špeciálne označenie súboru.
     */
    void SerializeTeams(String label) {
        RBAHodnotiaciSoftver.SaveTeams(VMTeams, "VM" + label + ".res");
    }

    public RBAVlastnyModel() {
        initComponents();
        initListeners();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        jSlider2 = new javax.swing.JSlider();
        jSlider3 = new javax.swing.JSlider();
        jSlider4 = new javax.swing.JSlider();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        TeamsComboBox = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RBA - Vlastný model");

        jButton1.setText("Ohodnoť");

        jLabel1.setText("Softvér");

        jLabel2.setText("Konštrukcia");

        jLabel3.setText("Kreativita a funkčnosť");

        jLabel4.setText("Prezentácia");

        jSlider1.setMaximum(5);

        jSlider2.setMaximum(5);
        jSlider2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider2StateChanged(evt);
            }
        });

        jSlider3.setMaximum(5);

        jSlider4.setMaximum(5);

        jLabel5.setText("jLabel5");

        jLabel6.setText("jLabel6");

        jLabel7.setText("jLabel7");

        jLabel8.setText("jLabel8");

        jLabel9.setText("Bonusové body");

        jTextField1.setText("0");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel10.setText("jLabel10");

        TeamsComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        TeamsComboBox.setName(""); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(TeamsComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
                        .addComponent(jLabel9)))
                .addGap(65, 65, 65)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSlider2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSlider3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSlider4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField1))
                .addGap(46, 46, 46)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(258, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(TeamsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jLabel1)
                                            .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel2))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(29, 29, 29)
                                                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                    .addComponent(jSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel3))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(29, 29, 29)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE))))
                            .addComponent(jSlider3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addComponent(jLabel4))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addComponent(jSlider4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(101, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jSlider2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider2StateChanged

    }//GEN-LAST:event_jSlider2StateChanged

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<Object> TeamsComboBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider2;
    private javax.swing.JSlider jSlider3;
    private javax.swing.JSlider jSlider4;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
