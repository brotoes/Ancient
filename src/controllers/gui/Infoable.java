/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;

/**
 * Implemented by classes that can display an info panel
 * @author brock
 */
public interface Infoable {
    /**
     * called by Screen Controller to get the panel to be displayed
     * @param nifty
     * @param screen
     * @param elem
     * @return
     */
    public Element showInfoPanel(Nifty nifty, Screen screen, Element elem);

    /**
     * When any button on the info panel is clicked, this is called
     * @param args
     */
    public void infoClick(String... args);

    /**
     * removes the info panel
     */
    public void removeInfoPanel();
}
