package dao;

import model.NetworkLineItem;
import model.Transaction;

import java.util.HashMap;
import java.util.List;

public interface TransactionDAO {
    public void insertTransaction(Transaction t);

    public int insertTransaction(List<NetworkLineItem> items, int userId);

    public List<Transaction> getUserTransactions(int userId);
}

//Tablas maestras --> modelan entidades
//Tablas detalle o transaccionales --> tablas donde tenemos insertadas tranascciones y demas
//      cosas hechas por los maestros, son las que tienen foreignkeys a las tablas maestros
//Tablas cabecera --> Serian como enums
//Tablas encabezado --> 

//TABLAS
//User  --> Transaction --> ProductInTransaction <-- Product
//User
//    -id
//    -username
//    -password
//    -role
//
//Transaction
//    -id
//    -date
//    -user_id
//    -total
//
//Product
//      -id
//      -name
//      -price
//      -amountOfDownloads
//      -album
//      -artist
//      -year
//      -duration
//      -path de mp3
//      -path de la imagen
//ProductInTransaction
//    -id
//    -id_transaction
//    -id_product
//    -quantity
//    -price


