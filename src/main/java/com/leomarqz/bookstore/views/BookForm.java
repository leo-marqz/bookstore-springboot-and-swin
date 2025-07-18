package com.leomarqz.bookstore.views;

import com.leomarqz.bookstore.models.Book;
import com.leomarqz.bookstore.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
    private DefaultTableModel tableModel;

    @Autowired
    public BookForm(BookService bookService){
        this.bookService = bookService;

        start();

        btnSave.addActionListener((event)->{
            //TODO
            System.out.println("Button Save clicked");
            addNewBook();
        });

    }

    private void start(){
        setContentPane(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Book Form");
        setVisible(true);
        setSize(900, 700);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        this.tableModel = new DefaultTableModel(0, 5);

        String[] headers = {"Id", "Title", "Author", "Price", "Stock"};

        this.tableModel.setColumnIdentifiers(headers);
        this.table = new JTable(tableModel);

        // Ajustar ancho de columna "Id"
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(0).setMinWidth(30);

        loadBooks();
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
