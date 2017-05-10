/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.kovacspe.rbahodnotiacisoftver;

import javax.swing.JButton;

/**
 * Tlacidlo timu ked sa jazdi pretek v kategorii Racing
 * Obsluhuje pripocitavanie kol pre svoj dany tim
 * @author Peter
 */
public class TeamRCBTN extends JButton{
    private final RacingGroup group;
    private final RBARacing form;
    private final TeamRC team;
    

    public TeamRCBTN(String label, RacingGroup group, TeamRC team,RBARacing form){
        super(label);
        this.group=group;
        this.form=form;
        this.team=team;
        this.addActionListener(e -> {
            if(this.group.isRunning){
                this.group.finishRound(team);
                this.form.refreshTable();
            }
            });
    }
}
