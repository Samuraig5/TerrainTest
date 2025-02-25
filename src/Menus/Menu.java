package Menus;

import javax.swing.*;

abstract class Menu {
    protected JFrame frame;

    public Menu(JFrame frame) {
        this.frame = frame;
        showMenu();
    }

    // Abstract method to display the menu.
    public abstract void showMenu();
}
