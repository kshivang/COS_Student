package org.invincible.cosstudent.misc;

/**
 * Created by kshivang on 02/10/16.
 */

public class MenuItem{
    private String name;
    private Integer price = 0;
    private String menu;
    private Integer qty = 1;

//    MenuItem(String name, Integer price, String menu, Integer qty) {
//        setName(name);
//        if (price == null) price = 0;
//        setPrice(price);
//        setMenu(menu);
//        if (qty == null) qty = 1;
//        setQty(qty);
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        if (qty == null || qty == 0) {
            qty = 1;
        }
        this.qty = qty;
    }
}
