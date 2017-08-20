/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.trade;

import hungarian.Hungarian;
import java.util.ArrayList;

/**
 *
 * @author brock
 */
public class TradeController {
    private final ArrayList<Buyer> buyers = new ArrayList<>();
    private final ArrayList<Seller> sellers = new ArrayList<>();

    public TradeController() {

    }

    /**
     * go through list of buyers and sellers
     * and match together
     */
    public void processTrades() {
        Hungarian h = new Hungarian(sellers, buyers);
        h.compute();

        for (Seller i : sellers) {
            i.sell();
        }

        buyers.clear();
        sellers.clear();
    }

    /**
     * Adds a sell offer to the list of trades to process
     * @param offer
     */
    public void add(Seller offer) {
        sellers.add(offer);
    }

    /**
     * Adds a buy request to the list of trades to process
     * @param request
     */
    public void add(Buyer request) {
       buyers.add(request);
    }

    /**
     * for testing
     */

    public void printTrades() {
        for (int i = 0; i < sellers.size(); i ++) {
            System.out.println(sellers.get(i));
        }
        for (int i = 0; i < buyers.size(); i ++) {
            System.out.println(buyers.get(i));
        }
    }

    public static void main(String[] args) {
        TradeController tCon = new TradeController();
        tCon.add(new Buyer(null, 1, 1, "food"));
        tCon.add(new Seller(null, 1, 1, "food"));

        tCon.processTrades();

        tCon.printTrades();
    }
}
