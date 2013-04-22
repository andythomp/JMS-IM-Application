package edu.carleton.comp4104.assignment3.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Transfer extends Remote {
    
    public static final String SERVICE_NAME = "TransferEngine";
    
    public void transfer(String fileName, byte[] bytes) throws RemoteException;
    public void initializeTransfer(String fileName, int fileSize) throws RemoteException;
    public void writeToDisc(String fileName) throws RemoteException;
    
}
