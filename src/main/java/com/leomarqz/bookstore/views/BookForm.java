package com.leomarqz.bookstore.views;

import com.leomarqz.bookstore.models.Book;
import com.leomarqz.bookstore.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Locale;

@Component
public class BookForm extends JFrame
{
    private BookService bookService;
    private JPanel panel;
    private JTable table;
    private JTextField textTitle;
    private JTextField textAuthor;
    private JTextField textPrice;
    private JTextField textStock;
    private JButton btnSave;
    private JButton btnModify;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnReload;
    private DefaultTableModel tableModel;
    private JTextField textIdHidden; // Optional, if you want to keep track of the selected book ID

    @Autowired
    public BookForm(BookService bookService){
        this.bookService = bookService;

        start();

        setResizable(false);

        btnSave.addActionListener((event)->{
            //TODO
            System.out.println("Button Save clicked");
            addNewBook();
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                selectTableRow();
            }
        });
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });
        btnModify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyBook();
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBook();
            }
        });
        btnReload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadBooks();
            }
        });


    }

    private void start(){
        setContentPane(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setTitle("Book Form");
        setVisible(true);

        setSize(800, 550);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;

        setLocation(x, y);

        if (btnSave != null) btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (btnModify != null) btnModify.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (btnDelete != null) btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (btnClear != null) btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if( btnReload != null) btnReload.setCursor(new Cursor(Cursor.HAND_CURSOR));

    }

    private void createUIComponents() {

        this.textIdHidden = new JTextField();
        this.textIdHidden.setVisible(false); // Hide the ID field if not needed in the UI

        this.tableModel = new DefaultTableModel(0, 5){
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make the table cells non-editable
                return false;
            }
        };

        String[] headers = {"Id", "Title", "Author", "Price", "Stock"};

        this.tableModel.setColumnIdentifiers(headers);
        this.table = new JTable(tableModel);
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ajustar ancho de columna "Id"
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(0).setMinWidth(30);



        loadBooks();
    }

    private void deleteBook(){
        String idText = textIdHidden.getText();
        if(idText.isEmpty()){
            JOptionPane.showMessageDialog(this, "No book selected for deletion.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            bookService.deleteBook(id);
            JOptionPane.showMessageDialog(this, "Book deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadBooks();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifyBook(){
        String idText = textIdHidden.getText();
        if(idText.isEmpty()){
            JOptionPane.showMessageDialog(this, "No book selected for modification.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        var result = validator();

        if(!result) return;

        var book = new Book();

        try{
            book.setId(Integer.parseInt(textIdHidden.getText()));
        }catch (Exception e){
            textIdHidden.requestFocusInWindow();
            JOptionPane.showMessageDialog(this, "Invalid ID format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        book.setTitle(textTitle.getText());
        book.setAuthor(textAuthor.getText());

        try{
            book.setPrice( Double.parseDouble(textPrice.getText()) );
        }catch (Exception e){
            textPrice.requestFocusInWindow();
            JOptionPane.showMessageDialog(this, "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            book.setStock(Integer.parseInt(textStock.getText()));
        }catch (Exception e) {
            textStock.requestFocusInWindow();
            JOptionPane.showMessageDialog(this, "Invalid stock format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try{
            bookService.updateBook(book);
            JOptionPane.showMessageDialog(this, "Book updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadBooks();
        }catch (Exception e){
            JOptionPane.showMessageDialog(this, "Error updating book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    private void selectTableRow(){
        var row = table.getSelectedRow();
        if(row != -1){
            var id = table.getValueAt(row, 0).toString();
            var title = table.getValueAt(row, 1).toString();
            var author = table.getValueAt(row, 2).toString();
            var price = table.getValueAt(row, 3).toString();
            var stock = table.getValueAt(row, 4).toString();

            textIdHidden.setText(id);
            textTitle.setText(title);
            textAuthor.setText(author);
            textPrice.setText(price.replace("$", ""));
            textStock.setText(stock);

            // Optionally, you can set the ID in a hidden field or variable if needed
            // e.g., selectedBookId = Integer.parseInt(id.toString());
        }
    }

    private void loadBooks(){
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

        tableModel.setRowCount(0); // Clear existing rows
        bookService.listBooks().forEach((book) -> {
            Object[] row = {
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                currencyFormat.format(book.getPrice()),
                book.getStock()
            };
            tableModel.addRow(row);
        });
        table.setModel(tableModel);
    }

    private void addNewBook(){
        String title = textTitle.getText();
        String author = textAuthor.getText();
        String priceText = textPrice.getText();
        String stockText = textStock.getText();

        var result = validator();

        if(!result) return;

        var book = new Book();

        book.setTitle( title );
        book.setAuthor( author );

        try{
            book.setPrice( Double.parseDouble(priceText) );
        }catch (Exception e){
            textPrice.requestFocusInWindow();
            JOptionPane.showMessageDialog(this, "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            book.setStock(Integer.parseInt(stockText));
        }catch (Exception e) {
            textStock.requestFocusInWindow();
            JOptionPane.showMessageDialog(this, "Invalid stock format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try{
            bookService.createBook(book);

            JOptionPane.showMessageDialog(this, "Book saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            clearFields();

            loadBooks();

        }catch (Exception e){
            JOptionPane.showMessageDialog(this, "Error saving book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

    }

    private boolean validator(){
        if(this.textTitle.getText().isEmpty()){
            this.textTitle.requestFocusInWindow();
            JOptionPane.showMessageDialog(this, "Title is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(this.textAuthor.getText().isEmpty()){
            this.textAuthor.requestFocusInWindow();
            JOptionPane.showMessageDialog(this, "Author is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(this.textPrice.getText().isEmpty()){
            this.textPrice.requestFocusInWindow();
            JOptionPane.showMessageDialog(this, "Price is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(this.textStock.getText().isEmpty()){
            this.textStock.requestFocusInWindow();
            JOptionPane.showMessageDialog(this, "Stock is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void clearFields() {
        textTitle.setText("");
        textAuthor.setText("");
        textPrice.setText("");
        textStock.setText("");
    }
}
