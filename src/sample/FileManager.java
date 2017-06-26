package sample;
import java.io.*;

/**
 * Created by Charles on 2016-10-11.
 */
public class FileManager {

    private static FileManager instance = new FileManager();

    // Constructor
    private FileManager() {

    }

    public static FileManager getInstance()
    {
        return instance;
    }

    /*
     * Serialize the data given in param
     * @backend: Object to serialize
     */
    public void saveData(BackEnd backEnd) {
        this.actualSavingProcess(backEnd, null);
    }

    /*
     * Serialize data and save the file to a given location
     * @backend: Object to serialize
     * @sPath: Path where to save the file
     */
    public void saveData(BackEnd backEnd, String sPath) {
        this.actualSavingProcess(backEnd, sPath);
    }

    public void saveThumbnails(BackEnd backEnd) {

    }

    private void actualSavingProcess(BackEnd backEnd, String sPath)
    {
        FileOutputStream fileOut;
        backEnd.helpSerializable();
        try {
            if (sPath == null) {
                fileOut = new FileOutputStream("./data/backend.ser");
                System.out.println("save = ./data/backend.ser");
            }
            else {
                fileOut = new FileOutputStream(sPath);
                System.out.println("save = " + sPath);
            }
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(backEnd);
            out.close();
            fileOut.close();
            //System.out.printf("Serialized data is saved in ./data/backend.ser");
        } catch(IOException i) {
            i.printStackTrace();
        }
    }

    private BackEnd actualLoadingOfData(String sPath)
    {
        FileInputStream fileIn;
        BackEnd backEnd = null;
        try {
            if (sPath == null) {
                fileIn = new FileInputStream("./data/backend.ser");
                System.out.println("load = ./data/backend.ser");
            }
            else {
                fileIn = new FileInputStream(sPath);
                System.out.println("load = " + sPath);
            }
            ObjectInputStream in = new ObjectInputStream(fileIn);
            backEnd = (BackEnd) in.readObject();  //ici ca throw InvalidClassException
            in.close();
            fileIn.close();
        }catch(IOException i) {
            i.printStackTrace();
            return null;
        }catch(ClassNotFoundException c) {
            c.printStackTrace();
            return null;
        }

        backEnd.helpDeserializable();
        return backEnd;
    }

    /*
     * Restores the Backend from the saved file
     */
    public BackEnd loadData() {
        BackEnd backEnd = this.actualLoadingOfData(null); //return null check dans appel
        return backEnd;
    }

    public BackEnd loadData(String sPath) {
        BackEnd backEnd = this.actualLoadingOfData(sPath);
        return backEnd;
    }

}
