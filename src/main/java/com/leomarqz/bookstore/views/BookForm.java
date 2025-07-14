package com.leomarqz.bookstore.views;

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
    private DefaultTableModel tableModel;

    @Autowired
    public BookForm(BookService bookService){
        this.bookService = bookService;

        start();
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
}
