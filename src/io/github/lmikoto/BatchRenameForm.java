package io.github.lmikoto;

import javax.swing.*;
import java.awt.event.*;

public class BatchRenameForm extends JDialog {

    private RenameBean renameBean;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textToFind;
    private JTextField replaceWith;
    private JTabbedPane tabbedPane;
    private JPanel replace;
    private JCheckBox searchInCommentsAndStrings;
    private JCheckBox searchForTextOccurrences;


    public BatchRenameForm(RenameBean renameBean){
        this();
        this.renameBean = renameBean;
    }

    public BatchRenameForm() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        setBeanValue();
        dispose();
    }

    private void setBeanValue(){
       renameBean.setReplaceWith(replaceWith.getText());
       renameBean.setTextToFind(textToFind.getText());
       renameBean.setSearchForTextOccurrences(searchForTextOccurrences.isSelected());
       renameBean.setSearchInCommentsAndStrings(searchInCommentsAndStrings.isSelected());
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        BatchRenameForm dialog = new BatchRenameForm();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
