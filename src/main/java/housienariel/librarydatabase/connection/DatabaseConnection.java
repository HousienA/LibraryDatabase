package housienariel.librarydatabase.connection;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class DatabaseConnection {
    private static final String URI = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "mongodbVSCodePlaygroundDB";
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static MongoDatabase getConnection() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(URI);
            database = mongoClient.getDatabase(DATABASE_NAME);
            System.out.println("MongoDB connected successfully!");
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
