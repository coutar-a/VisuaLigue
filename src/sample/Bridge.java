package sample;

import javafx.stage.Stage;

/**
 * Created by WINDO on 03/12/2016.
 */
public class Bridge {

    private static Bridge instance = new Bridge();
    public FrontController fc;
    public Stage ps;
    public Main main;
    public String importPath = null;
    public boolean hasImportedOnce = false;

    // Constructor
    private Bridge() {

    }

    public static Bridge getInstance()
    {
        return instance;
    }

}
