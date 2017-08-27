/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.trade;

import ancient.resources.Resource;
import hungarian.Hungarian;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author brock
 */
public class TradeController {
    private final HashMap<Resource, List<Buyer>> buyers = new HashMap<>();
    private final HashMap<Resource, List<Seller>> sellers = new HashMap<>();


    public TradeController() {}

    /**
     * go through list of buyers and sellers
     * and match together
     */
    public void processTrades() {
        for (Resource resource : sellers.keySet()) {
            /* check if there are both buyers and sellers for that resource */
            if (!buyers.containsKey(resource)) {
                continue;
            }
            List<Seller> sellerList = sellers.get(resource);
            List<Buyer> buyerList = buyers.get(resource);

            if (sellerList.isEmpty() || buyerList.isEmpty()) {
                continue;
            }

            Hungarian h = new Hungarian(sellerList, buyerList);
            h.compute();

            for (Seller i : sellerList) {
                i.sell();
            }

            buyerList.clear();
            sellerList.clear();
        }
    }

    /**
     * Adds a sell offer to the list of trades to process
     * @param seller
     */
    public void add(Seller seller) {
        if (!sellers.containsKey(seller.getResource())) {
            sellers.put(seller.getResource(), new ArrayList<>());
        }
        List<Seller> list = sellers.get(seller.getResource());

        if (list.indexOf(seller) < 0) {
            list.add(seller);
        }
    }

    /**
     * Adds a buy request to the list of trades to process
     * @param buyer
     */
    public void add(Buyer buyer) {
        if (!buyers.containsKey(buyer.getResource())) {
            buyers.put(buyer.getResource(), new ArrayList<>());
        }
        List<Buyer> list = buyers.get(buyer.getResource());

        if (list.indexOf(buyer) < 0) {
            list.add(buyer);
        }
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
}
