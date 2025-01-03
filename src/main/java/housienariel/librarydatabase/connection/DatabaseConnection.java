package housienariel.librarydatabase.connection;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class DatabaseConnection {
    private static final String URI = "mongodb+srv://ahvp:<db_password>@cluster0.k9qvo.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static MongoDatabase getConnection() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(URI);
            database = mongoClient.getDatabase("library");
            System.out.println("Connected to MongoDB Atlas successfully!");
        }
        return database;
    }

    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDB connection closed successfully!");
        }
    }

    public static void main(String[] args) {
        try {
            MongoDatabase db = getConnection();
            System.out.println("Connected to database: " + db.getName());
            db.getCollection("books").find().forEach((Document doc) -> {
                System.out.println(doc.toJson());
            });
            closeConnection();
        } catch (Exception e) {
            System.err.println("MongoDB connection failed!");
            e.printStackTrace();
        }
    }
}
