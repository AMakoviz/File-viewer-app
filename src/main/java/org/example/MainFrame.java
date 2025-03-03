package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;



public class MainFrame extends JFrame {
    private JList dirList;
    private JLabel back;
    private JTextArea textRight;
    private JSplitPane splitPane;
    private ArrayList<String> dirCash;
    private JPanel textPanel = new JPanel();
    private JPanel left = new JPanel();
    private JFrame frame;
    private JScrollPane filesScrollPane;
    private JScrollPane textScrollPane;



    public JSplitPane getSplitPane() {
        return splitPane;
    }
    public MainFrame() {

        dirCash = splitPath();
        File file = new File(System.getProperty("user.dir"));
        String[] dir = file.list();

        frame = new JFrame(System.getProperty("user.dir"));

        textRight = initText();
        dirList = initList(dir);

        left.add(initList(dir));

        filesScrollPane = new JScrollPane(dirList);
        filesScrollPane.setPreferredSize(new Dimension(400,500));
        left.add(filesScrollPane);
        left.setPreferredSize(new Dimension(200, 500));


        textPanel.setLayout(new BorderLayout());
        textScrollPane = new JScrollPane(textRight);
        textScrollPane.setPreferredSize(new Dimension(300,460));

        textPanel.add(textScrollPane, BorderLayout.CENTER);





        BoxLayout layout = new BoxLayout(left, BoxLayout.Y_AXIS);
        left.setLayout(layout);





        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, textPanel);


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(splitPane);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JTextArea initText(){
       textRight = new JTextArea();
        textRight.setEditable(false);
        textRight.setLineWrap(true);
        textRight.setWrapStyleWord(true);

        return textRight;

    }


    private void initTitle(Frame frame){
        frame.setTitle(toFullPath(dirCash));

    }

    private JList initList(String [] dir){
        DefaultListModel<String> modelFile = new DefaultListModel<>();
        modelFile.addElement("...");

        if (dir != null) {
            for (String item : dir) {
                modelFile.addElement(item);
            }
        }
        dirList = new JList(modelFile);
        dirList.addMouseListener( new MouseListener());
        dirList.addKeyListener(new KeyListener());

        return dirList;
    }


    public String toFullPath(List<String> dirCash) {
        String listPart = "";
        for (String str : dirCash){
            listPart = listPart + str+"\\";
        }
        return listPart;
    }

    public ArrayList<String> splitPath(){
        String currentDir = System.getProperty("user.dir");
        dirCash = new ArrayList<>();
        int i =0;
        while (!currentDir.isEmpty()){
            int end = currentDir.indexOf("\\");
            if (currentDir.contains("\\")){
                String cat = currentDir.substring(0, end);
                currentDir = currentDir.replace(currentDir.substring(0,end+1), "");
                dirCash.add(i, cat);
            } else {
                dirCash.add(i, currentDir);
                currentDir = currentDir.replace(currentDir,"");
            }
            i++;
        }
        return dirCash;
    }
    private void back(){
        DefaultListModel modelFile = new DefaultListModel();
        modelFile.addElement("...");
        List<String> cat = new ArrayList<>();
        List<String> file = new ArrayList<>();
        if (dirCash.size()>1){
            dirCash.remove(dirCash.size()-1);
            initTitle(frame);
            String fullPath = toFullPath(dirCash);
            File backDir = new File(fullPath);
            String [] rootStr = backDir.list();
            for (String i: rootStr){
                File checkFile = new File (backDir.getPath(), i);
                if (!checkFile.isHidden()){
                    if (checkFile.isDirectory()){
                        cat.add(i);
                    } else {
                        file.add(i);
                    }
                }

            }
            cat.sort(String::compareToIgnoreCase);
            file.sort(String::compareToIgnoreCase);
            for (String i: cat){
                modelFile.addElement(i);
            }
            for (String i:file){
                modelFile.addElement(i);
            }
            dirList.setModel(modelFile);

        }


    }
    private void getText(File selectedFile){
        
        try {
            textRight.setText(Files.readAllLines(selectedFile.toPath()).toString());
            textRight.setCaretPosition(0);

        } catch (FileNotFoundException er){
            throw new RuntimeException(er + "Невозможно открыть указанный файл");
        }
        catch (IOException er) {
            throw new RuntimeException(er + "Невозможно открыть указанный файл");
        }
    }
    private void goInCat(File selectedFile,String selectedObject ) {
        DefaultListModel modelFile = new DefaultListModel();
        modelFile.addElement("...");
        List<String> cat = new ArrayList<>();
        List<String> file = new ArrayList<>();

        if (selectedFile.isDirectory()) {
            textRight.setText("");
            String[] rootStr = selectedFile.list();
            for (String i : rootStr) {
                File checkFile = new File(selectedFile.getPath(), i);
                if (!checkFile.isHidden()) {
                    if (checkFile.isDirectory()) {
                        cat.add(i);
                    } else {
                        file.add(i);
                    }
                }
            }
            cat.sort(String::compareToIgnoreCase);
            file.sort(String::compareToIgnoreCase);
            for (String i : cat) {
                modelFile.addElement(i);
            }
            for (String i : file) {
                modelFile.addElement(i);
            }
            dirCash.add(selectedObject);
            initTitle(frame);
            dirList.setModel(modelFile);

        } else getText(selectedFile);
    }

    private class MouseListener extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e){

            if (e.getClickCount() == 2) {
                String selectedObject = dirList.getSelectedValue().toString();
                File selectedFile;
                String fullPath = toFullPath(dirCash);
                if (dirCash.size() > 1) {
                    selectedFile = new File(fullPath, selectedObject);
                } else {
                    selectedFile = new File(fullPath + selectedObject);
                }

                if (selectedObject.equals("...")) {
                    back();
                } else if (selectedFile.isDirectory()|| selectedFile.isFile()){
                    goInCat(selectedFile,selectedObject);
                }
            }
        }
    }


    private class KeyListener extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER && !dirList.isSelectionEmpty()){
                String selectedObject = dirList.getSelectedValue().toString();
                File selectedFile;
                String fullPath = toFullPath(dirCash);
                if (dirCash.size() > 1) {
                    selectedFile = new File(fullPath, selectedObject);
                } else {
                    selectedFile = new File(fullPath + selectedObject);
                }

                if (selectedObject.equals("...")) {
                    back();
                } else if (selectedFile.isDirectory()|| selectedFile.isFile()){
                    goInCat(selectedFile,selectedObject);
                }
            }
//
        }
    }
}
