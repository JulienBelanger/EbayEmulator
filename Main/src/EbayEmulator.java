import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import java.util.Optional;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.Types;
import java.sql.SQLException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class EbayEmulator extends Application 
{
    Scene register, login, acheter, vendre, admin;
    Stage primaryStage;
    Statement stmt;
    boolean flopping = true;
    String this_username;
    Produit produit;
    Estimation estimation;
    
    @Override
    public void start(Stage pStage) throws Exception 
    {
        // DB CONNECTION
        stmt = databaseConnection("org.postgresql.Driver", "jdbc:postgresql:", "//localhost:5432/ebay", "myusr", "Fawkes05");

        // CREATE TABLES IF NECESSARY OR UPDATE IDS
        init(stmt);

        // GUI CONSTRUCTION
        this.primaryStage = pStage;

        primaryStage.setTitle("EbayEmulator");

        //LOGIN SCENE
        // Create the login form grid pane
        GridPane loginPane = createFormPane("Login");
        // Add UI controls to the login form grid pane
        addLoginUIControls(loginPane);
        // Create a scene with registration form grid pane as the root node
        login = new Scene(loginPane, 800, 500);

        // REGISTER SCENE
        // Create the registration form grid pane
        GridPane regPane = createFormPane("Registration");
        // Add UI controls to the registration form grid pane
        addRegisterUIControls(regPane);
        // Create a scene with registration form grid pane as the root node
        register = new Scene(regPane, 800, 500);

        //ACHETER SCENE
        GridPane achPane = createFormPane("Acheter");
        addAcheterUIControls(achPane);
        acheter = new Scene(achPane, 800, 500);

        //VENDRE SCENE
        GridPane vendPane = createFormPane("Vendre");
        addVendreUIControls(vendPane);
        vendre = new Scene(vendPane, 800, 500);


        //ADMIN PAGE
        GridPane adminPane = createFormPane("Admin");
        addAdminUIControls(adminPane);
        admin = new Scene(adminPane, 800, 500);


        // Set the login scene in primary stage
        primaryStage.setScene(login);

        primaryStage.show();
    }

    // Quicky to connect to a db
    private static Statement databaseConnection(String driver, String jdbc, String url, String usr, String password) throws Exception
    {
        //Database connection...
        Connection c = null;
        try {
            Class.forName(driver);
            c = DriverManager.getConnection(jdbc+url, usr, password);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        return c.createStatement();
    }

    private void init(Statement stmt)
    {
         // TABLE BUILD        
        if(!Table.exists(stmt, "utilisateur"))
        {
            Utilisateur.createTable(stmt);
        }else
        {
            Utilisateur.setId(Utilisateur.getdbId(stmt));   
        }
        if(!Table.exists(stmt, "compte"))
        {
            Compte.createTable(stmt);
        }else
        {
            Compte.setId(Compte.getdbId(stmt));   
        }
        if(!Table.exists(stmt, "produit"))
        {
            try
            {
                String sql = "select Count(to_regtype('categories'));";
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                    if(rs.getInt("count")==1)
                    {
                        Table.dropType(stmt, "categories");

                    }

                }
            }catch(Exception e){
                e.printStackTrace(); 
                System.err.println(e.getClass().getName()+": "+e.getMessage());
                System.exit(0);}

            Produit.createTable(stmt);
        }else
        {
            Produit.setId(Produit.getdbId(stmt));   
        }
        if(!Table.exists(stmt, "offre"))
        {   
            Offre.createTable(stmt);
        }else
        {
            Offre.setId(Offre.getdbId(stmt));   
        }
        if(!Table.exists(stmt, "estimation"))
        {
            Estimation.createTable(stmt);
        }else
        {
            Estimation.setId(Estimation.getdbId(stmt));   
        }
        if(!Table.exists(stmt, "paiement"))
        {
            Paiement.createTable(stmt);
        }else
        {
            Paiement.setId(Offre.getdbId(stmt));   
        }
    }

    // Meta props for all pages (font, size, header style, etc.)
    private GridPane createFormPane(String header) 
    {
        // Instantiate a new Grid Pane
        GridPane gridPane = new GridPane();

        // Position the pane at the center of the screen, both vertically and horizontally
        //gridPane.setAlignment(Pos.CENTER);

        // Set a padding of 20px on each side
        gridPane.setPadding(new Insets(40, 40, 40, 40));

        // Set the horizontal gap between columns
        gridPane.setHgap(10);

        // Set the vertical gap between rows
        gridPane.setVgap(10);

        // columnOneConstraints will be applied to all the nodes placed in column one.
        //ColumnConstraints columnOneConstraints = new ColumnConstraints(100, 100, Double.MAX_VALUE);
        //columnOneConstraints.setHalignment(HPos.RIGHT);

        // columnTwoConstraints will be applied to all the nodes placed in column two.
        //ColumnConstraints columnTwoConstrains = new ColumnConstraints(200,200, Double.MAX_VALUE);
        //columnTwoConstrains.setHgrow(Priority.ALWAYS);

        //gridPane.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstrains);

        // Add Header
        Label headerLabel = new Label(header);
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gridPane.add(headerLabel, 0,0,2,1);
        GridPane.setHalignment(headerLabel, HPos.CENTER);
        GridPane.setMargin(headerLabel, new Insets(20, 0,20,0));

        return gridPane;
    }

    private void addAdminUIControls(GridPane gridPane) 
    {

        // Utilisateurs ayant effectué plus de ventes que la moyenne
        Label pdvm = new Label("Utilisateurs ayant effectué plus de ventes que la moyenne");
        gridPane.add(pdvm, 0, 1);
        
        // Utilisateurs ayant effectué plus de ventes que la moyenne
        Text pdvmField = new Text();
        gridPane.add(pdvmField, 0, 2);

        // call to up function straight up
        String ret = Utilisateur.UserBMoy(stmt);
        // TODO: Put in textfield maybe

        // Distribution du nombre d’hésitation(s) avant d’accepter une offre
        Label dnh = new Label("Distribution du nombre d’hésitation(s) avant d’accepter une offre");
        gridPane.add(dnh, 1, 1);
        
        // Distribution du nombre d’hésitation(s) avant d’accepter une offre
        Text dnhField = new Text();
        dnhField.setVisible(false);
        gridPane.add(dnhField, 1, 2);

        String dance = Compte.dance(stmt);

        // Détecteur de bot (captcha at register)        
        Label ddb = new Label("Détecteur de bot");
        gridPane.add(ddb, 2, 1);
        
        // Détecteur de bot (captcha at register)
        Button ddbB = new Button("kill bots");
        ddbB.setPrefHeight(40);
        ddbB.setDefaultButton(true);
        ddbB.setPrefWidth(100);
        gridPane.add(ddbB, 2, 1, 2, 1);
        GridPane.setHalignment(ddbB, HPos.CENTER);
        GridPane.setMargin(ddbB, new Insets(20, 0,20,0));

    }

    // Page spec for Login
    private void addLoginUIControls(GridPane gridPane) 
    {
        // Add Username label
        Label usernameLabel = new Label("Username : ");
        gridPane.add(usernameLabel, 0, 1);

        // Add Username Text Field
        TextField usernameField = new TextField();
        usernameField.setPrefHeight(40);
        gridPane.add(usernameField, 1, 1);

        // Add Password Label
        Label passwordLabel = new Label("Password : ");
        gridPane.add(passwordLabel, 0, 2);

        // Add Password Field
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefHeight(40);
        gridPane.add(passwordField, 1, 2);

        // Add Submit Button
        Button submitButton = new Button("Submit");
        submitButton.setPrefHeight(40);
        submitButton.setDefaultButton(true);
        submitButton.setPrefWidth(100);
        gridPane.add(submitButton, 0, 5, 2, 1);
        GridPane.setHalignment(submitButton, HPos.CENTER);
        GridPane.setMargin(submitButton, new Insets(20, 0,20,0));

        // Add Register Button
        Button registerButton = new Button("Register");
        registerButton.setPrefHeight(40);
        registerButton.setDefaultButton(true);
        registerButton.setPrefWidth(100);
        gridPane.add(registerButton, 2, 5, 2, 1);
        GridPane.setHalignment(registerButton, HPos.CENTER);
        GridPane.setMargin(registerButton, new Insets(20, 0,20,0));
        registerButton.setOnAction(e -> primaryStage.setScene(register));

         // Add admin Button
        Button adminB = new Button("Admin");
        adminB.setPrefHeight(40);
        adminB.setDefaultButton(true);
        adminB.setPrefWidth(100);
        gridPane.add(adminB, 4, 5, 2, 1);
        GridPane.setHalignment(adminB, HPos.CENTER);
        GridPane.setMargin(adminB, new Insets(20, 0,20,0));
        adminB.setOnAction(e -> primaryStage.setScene(admin));


        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            
                if(usernameField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Error!", "Please enter a username");
                    return;
                }

                if(passwordField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Error!", "Please enter a password");
                    return;
                }

                else if(Compte.exists(stmt, usernameField.getText(), passwordField.getText())){
                    this_username = usernameField.getText();
                    showAlert(Alert.AlertType.CONFIRMATION, gridPane.getScene().getWindow(), "Login Successful!", "Welcome " + Utilisateur.getName(stmt, usernameField.getText()));
                    primaryStage.setScene(vendre);
                }

                else {
                    showAlert(Alert.AlertType.CONFIRMATION, gridPane.getScene().getWindow(), "Login Failed", "Mauvais nom d'utilisateur ou mot de passe incorrect");
                }
            }
        });
    }

    // Page spec for Register
    private void addRegisterUIControls(GridPane gridPane) 
    {

        // Add Surname Label
        Label prenom = new Label("Prénom : ");
        gridPane.add(prenom, 0,1);

        // Add Surname Text Field
        TextField prenomField = new TextField();
        prenomField.setPrefHeight(40);
        gridPane.add(prenomField, 1,1);


        // Add Name Label
        Label nom = new Label("Nom : ");
        gridPane.add(nom, 0, 2);

        // Add Name Text Field
        TextField nomField = new TextField();
        nomField.setPrefHeight(40);
        gridPane.add(nomField, 1, 2);

        // Add Username label
        Label usernameLabel = new Label("Username : ");
        gridPane.add(usernameLabel, 0, 3);

        // Add Username Text Field
        TextField usernameField = new TextField();
        usernameField.setPrefHeight(40);
        gridPane.add(usernameField, 1, 3);

        // Add Password Label
        Label passwordLabel = new Label("Password : ");
        gridPane.add(passwordLabel, 0, 4);

        // Add Password Field
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefHeight(40);
        gridPane.add(passwordField, 1, 4);

        // Add Submit Button
        Button submitButton = new Button("Submit");
        submitButton.setPrefHeight(40);
        submitButton.setDefaultButton(true);
        submitButton.setPrefWidth(100);
        gridPane.add(submitButton, 0, 5, 2, 1);
        GridPane.setHalignment(submitButton, HPos.CENTER);
        GridPane.setMargin(submitButton, new Insets(20, 0,20,0));

        // Add Login Button
        Button loginButton = new Button("Login");
        loginButton.setPrefHeight(40);
        loginButton.setDefaultButton(true);
        loginButton.setPrefWidth(100);
        gridPane.add(loginButton, 2, 5, 2, 1);
        GridPane.setHalignment(loginButton, HPos.CENTER);
        GridPane.setMargin(loginButton, new Insets(20, 0,20,0));

        loginButton.setOnAction(e -> primaryStage.setScene(login));


        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(prenomField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Error!", "Please enter your surname");
                    return;
                }
                if(nomField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Error!", "Please enter your name");
                    return;
                }
                if(usernameField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Error!", "Please enter a username");
                    return;
                }
                if(passwordField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Error!", "Please enter a password");
                    return;
                }
                else if(!Compte.exists(stmt, usernameField.getText())){
                    
                    // new user
                    Utilisateur user = new Utilisateur(stmt, nomField.getText(), prenomField.getText(), usernameField.getText(), passwordField.getText());
                    user.insert();

                    showAlert(Alert.AlertType.CONFIRMATION, gridPane.getScene().getWindow(), "Registration Successful!", "Welcome " + prenomField.getText());    
                    
                    // go to scene vendre
                    primaryStage.setScene(login);
                }
                else{
                    showAlert(Alert.AlertType.CONFIRMATION, gridPane.getScene().getWindow(), "Registration Failed", "username already used");
                }
            }
        });
    }

    // Page spec for Acheter
    private void addAcheterUIControls(GridPane gridPane) 
    {

        // Add keyword filter
        Label motclef = new Label("Mot clef : ");
        gridPane.add(motclef, 0, 1);
        TextField search = new TextField();
        search.setPrefHeight(40);
        gridPane.add(search, 1, 1);

        // add category filter
        Label cat = new Label("Catégorie : ");
        gridPane.add(cat, 0,2);
        ComboBox<String> catCombo = new ComboBox<String>();
        catCombo.getItems().addAll(
            "CARS",
            "HEALTH",
            "CLOTHING",
            "ELECTRONICS",
            "ART",
            "PETS",
            "APPLIANCES");
        gridPane.add(catCombo, 1, 2);

        // Add showing results
        Text results = new Text();
        results.setVisible(false);
        gridPane.add(results, 1, 3);

        // add result menu
        Label resultLabel = new Label("Result : ");
        gridPane.add(resultLabel, 2,1);
        resultLabel.setVisible(false);
        ComboBox<String> resultCombo = new ComboBox<String>();        
        gridPane.add(resultCombo, 3, 1);
        resultCombo.setVisible(false);

        // TODO : add money making sql queries under the drop menu format

        // add seller prenom, nom filter
        Label seller = new Label("Afficheur : ");
        gridPane.add(seller, 0, 3);
        TextField prenomField = new TextField("Prénom");
        prenomField.setPrefHeight(40);
        gridPane.add(prenomField, 1, 3);
        TextField nomField = new TextField("Nom");
        nomField.setPrefHeight(40);
        gridPane.add(nomField, 2, 3);


        // add price range
        Label priceRange = new Label("Price Range : ");
        gridPane.add(priceRange, 0, 4);
        TextField lowerField = new TextField();
        lowerField.setPrefHeight(40);
        gridPane.add(lowerField, 1, 4);
        TextField higherField = new TextField();
        higherField.setPrefHeight(40);
        gridPane.add(higherField, 2, 4);

        // Add Submit Button
        Button submitButton = new Button("Search");
        submitButton.setPrefHeight(40);
        submitButton.setDefaultButton(true);
        submitButton.setPrefWidth(100);
        gridPane.add(submitButton, 0, 5, 2, 1);
        GridPane.setHalignment(submitButton, HPos.CENTER);
        GridPane.setMargin(submitButton, new Insets(20, 0,20,0));

        // Add Vendre Button
        Button vendreButton = new Button("Vendre");
        vendreButton.setPrefHeight(40);
        vendreButton.setDefaultButton(true);
        vendreButton.setPrefWidth(100);
        gridPane.add(vendreButton, 5, 6, 2, 1);
        GridPane.setHalignment(vendreButton, HPos.CENTER);
        GridPane.setMargin(vendreButton, new Insets(20, 0,20,0));

        vendreButton.setOnAction(e -> primaryStage.setScene(vendre));

         // Add Offre Button and IdField and TextField
        Label of = new Label("Offre : $");
        of.setVisible(false);
        gridPane.add(of, 2, 5);
        TextField ofField = new TextField();
        ofField.setPrefHeight(40);
        gridPane.add(ofField, 3, 5);
        ofField.setVisible(false);
        Button offreButton = new Button("Offre");
        offreButton.setVisible(false);
        offreButton.setPrefHeight(40);
        offreButton.setDefaultButton(true);
        offreButton.setPrefWidth(100);
        gridPane.add(offreButton, 4, 5, 2, 1);
        

         submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
    
                //ux
                resultCombo.getSelectionModel().clearSelection();
                resultCombo.getItems().clear();
                resultCombo.setVisible(true);

                ofField.setVisible(true);
                offreButton.setVisible(true);
                    
                // logic
                String additional_condition = " And ";
                if(!search.getText().trim().isEmpty())
                {
                    additional_condition = additional_condition + " (titre like '%" + search.getText() + "%' OR description like '%" + search.getText() + "%') AND "; 
                }
                if(!catCombo.getSelectionModel().isEmpty())
                {
                    additional_condition = additional_condition + " (categorie = '" + catCombo.getValue() + "') AND ";
                }
                if(!prenomField.getText().equals("Prénom") && !prenomField.getText().trim().isEmpty()){
                    System.out.println("sucs");
                    additional_condition = additional_condition + " (prenom = '" + prenomField.getText() +"') AND ";
                }
                if(nomField.getText().equals("Nom") && !nomField.getText().trim().isEmpty()){
                    additional_condition = additional_condition + " (nom = '" + nomField.getText() +"') AND ";
                }
                if(!lowerField.getText().trim().isEmpty()){
                    additional_condition = additional_condition + " cast(produit.price as numeric(32,2)) >= " + lowerField.getText() + " AND ";
                }
                if(!higherField.getText().trim().isEmpty()){
                    additional_condition = additional_condition + " (cast(produit.price as numeric(32,2)) <= " + higherField.getText() + ") AND ";
                }
                additional_condition = additional_condition +" true ;";

                // get conditional
                Produit.buyerLook(stmt, resultCombo, additional_condition);
                //ux
                resultLabel.setVisible(true);
                resultCombo.setVisible(true);
            }
        });  


        offreButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!resultCombo.getSelectionModel().isEmpty() && !ofField.getText().trim().isEmpty()){
                    Pattern pattern = Pattern.compile("id:(\\d+),*");
                    Matcher match = pattern.matcher(resultCombo.getValue());
                    if(match.find() )
                    {
                        Offre this_of;
                        int of_prd_id = Integer.parseInt(match.group(1));
                        this_of = new Offre(Compte.getId(stmt, this_username), of_prd_id, new BigDecimal(ofField.getText()), stmt);
                        this_of.insert();
                        System.out.println("prodprice: " + Produit.getPrice(stmt, of_prd_id));
                        System.out.println("offeredprice: " + this_of.price);
                        int comp = this_of.price.compareTo(Produit.getPrice(stmt, of_prd_id));
                        if(comp >= 0)
                        {
                            this_of.accept();
                            results.setText("Offer accepted");
                            offreButton.setVisible(false);
                            ofField.setVisible(false);
                            resultLabel.setVisible(false);
                            results.setVisible(true);
                            resultCombo.setVisible(false);
                        }
                        else
                        {
                            results.setText("Offer refused");
                            results.setVisible(true);
                        }

                    }
                }
            }
        });

    }
    // Page spec for Vendre
    private void addVendreUIControls(GridPane gridPane) 
    {
        // Add Catégorie Label
        Label cat = new Label("Catégorie : ");
        gridPane.add(cat, 0,1);

        ComboBox<String> catCombo = new ComboBox<String>();

        // Add catégorie Combo Field
        catCombo.getItems().addAll(
            "CARS",
            "HEALTH",
            "CLOTHING",
            "ELECTRONICS",
            "ART",
            "PETS",
            "APPLIANCES");
        gridPane.add(catCombo, 1, 1);

        // Add catégorie Text Field
        Text catField = new Text();
        catField.setVisible(false);
        gridPane.add(catField, 1, 1);

        // Add Titre Label
        Label titre = new Label("Titre : ");
        gridPane.add(titre, 0, 2);

        // Add Titre Text Field
        TextField titreField = new TextField();
        titreField.setPrefHeight(40);
        gridPane.add(titreField, 1, 2);

        // Add Description label
        Label desLabel = new Label("Description : ");
        gridPane.add(desLabel, 0, 3);

        // Add Description Text Field
        TextField desField = new TextField();
        desField.setPrefHeight(40);
        gridPane.add(desField, 1, 3);

        // Add Price label
        Label priceLabel = new Label("Prix : $");
        gridPane.add(priceLabel, 0, 4);

        // Add price Text Field
        TextField priceField = new TextField();
        priceField.setPrefHeight(40);
        gridPane.add(priceField, 1, 4);

        // Add Estimation prix as Text and setVisible(false) by default
        Label priceEstimateLabel = new Label("Estimation : $");
        priceEstimateLabel.setVisible(false);
        gridPane.add(priceEstimateLabel, 2, 4);

        Text priceEstimate = new Text();
        priceEstimate.setVisible(false);
        gridPane.add(priceEstimate, 3, 4);
        
        // Add Accepter Button and setVisible(false) by default
        Button acceptButton = new Button("Accepter");
        acceptButton.setVisible(false);
        acceptButton.setPrefHeight(40);
        acceptButton.setDefaultButton(true);
        acceptButton.setPrefWidth(100);
        gridPane.add(acceptButton, 4, 4, 2, 1);
        GridPane.setHalignment(acceptButton, HPos.CENTER);
        GridPane.setMargin(acceptButton, new Insets(20, 0,20,0));        

        // Add Create Button
        Button createButton = new Button("Créer");
        createButton.setPrefHeight(40);
        createButton.setDefaultButton(true);
        createButton.setPrefWidth(100);
        gridPane.add(createButton, 0, 5, 2, 1);
        GridPane.setHalignment(createButton, HPos.CENTER);
        GridPane.setMargin(createButton, new Insets(20, 0,20,0));

        // Add Submit Button
        Button submitButton = new Button("Évaluer");
        submitButton.setVisible(false);
        submitButton.setPrefHeight(40);
        submitButton.setDefaultButton(true);
        submitButton.setPrefWidth(100);
        gridPane.add(submitButton, 0, 5, 2, 1);
        GridPane.setHalignment(submitButton, HPos.CENTER);
        GridPane.setMargin(submitButton, new Insets(20, 0,20,0));

        // Add Acheter Button
        Button acheterButton = new Button("Acheter");
        acheterButton.setPrefHeight(40);
        acheterButton.setDefaultButton(true);
        acheterButton.setPrefWidth(100);
        gridPane.add(acheterButton, 7, 5, 2, 1);
        GridPane.setHalignment(acheterButton, HPos.CENTER);
        GridPane.setMargin(acheterButton, new Insets(20, 0,20,0));
        acheterButton.setOnAction(e -> primaryStage.setScene(acheter));

        createButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(catCombo.getValue() == null) {
                    showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Error!", "Choisissez une catégorie");
                    return;
                }
                if(titreField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Error!", "Donnez un titre à votre produit");
                    return;
                }

                if(priceField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Warning!", "Donnez un prix à votre produit");
                    return;
                }

                if(desField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Error!", "Êtes-vous certain de ne pas vouloir donner de description à votre produit?");
                }

                //UX
                createButton.setVisible(false);
                submitButton.setVisible(true);
                catCombo.setVisible(false);
                catField.setVisible(true);
                catField.setText(catCombo.getValue());
                titreField.setEditable(false);
                desField.setEditable(false);
                priceField.setEditable(false);

                //Produit object creation and insert
                Produit.Categories ctg = null;
                switch (catCombo.getValue()) 
                {
                    case "CARS":
                        ctg = Produit.Categories.CARS;
                        break;
                    case "HEALTH":
                        ctg = Produit.Categories.HEALTH;
                        break;
                    case "CLOTHING":
                        ctg = Produit.Categories.CLOTHING;
                        break;
                    case "ELECTRONICS":
                        ctg = Produit.Categories.ELECTRONICS;
                        break;
                    case "ART":
                        ctg = Produit.Categories.ART;
                        break;
                    case "PETS":
                        ctg = Produit.Categories.PETS;
                        break;
                    case "APPLIANCES":
                        ctg = Produit.Categories.APPLIANCES;
                        break;
                }
                produit = new Produit(Compte.getId(stmt, this_username), ctg, titreField.getText(), desField.getText(), new BigDecimal(priceField.getText()), stmt);
                produit.insert();
                System.out.println("Produit Created successfully");    
            }
        });

        // When evaluate is triggered
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                TextInputDialog dialog = new TextInputDialog();
                
                dialog.setTitle("Expert");
                dialog.setHeaderText("Donnez un prix juste pour le produit : ");
                dialog.setContentText("Prix : $");
                 
                Optional<String> result = dialog.showAndWait();
                
                result.ifPresent(price -> {
                    
                    // UX
                    priceEstimate.setText(price);
                    priceEstimateLabel.setVisible(true);
                    priceEstimate.setVisible(true);
                    acceptButton.setVisible(true);
                });

                estimation = new Estimation(produit.id, new BigDecimal(priceEstimate.getText()), stmt);
                estimation.insert();
                System.out.println("Estimation inserted");
            }
        });


        acceptButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                
                //UX
                catField.setVisible(false);
                catCombo.setValue(null);
                titreField.clear();
                desField.clear();
                priceField.clear();
                priceEstimateLabel.setVisible(false);
                priceEstimate.setVisible(false);
                acceptButton.setVisible(false);

                createButton.setVisible(true);
                submitButton.setVisible(false);
                catCombo.setVisible(true);
                titreField.setEditable(true);
                desField.setEditable(true);
                priceField.setEditable(true);

                Estimation.accept(stmt, produit.id);

            }
        });
    }

    // Popup alert (readonly)
    private void showAlert(Alert.AlertType alertType, Window owner, String title, String message) 
    {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }

    // Boiler plate for launching app
    public static void main(String[] args) 
    {
        launch(args);
    }
}