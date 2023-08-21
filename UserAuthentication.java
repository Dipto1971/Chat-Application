import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.util.UUID;

public class UserAuthentication {
    public static String userID;

    private static final String DB_NAME = "Chatapp";
    private static final String USER_COLLECTION_NAME = "USERS";


    public static String getUserId() {
        return userID;
    }

    public static boolean login(String email, String password) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017"); // Connect to local MongoDB server
        MongoDatabase database = mongoClient.getDatabase(DB_NAME);
        MongoCollection<Document> userCollection = database.getCollection(USER_COLLECTION_NAME);

        Document userDoc = userCollection.find(Filters.eq("email", email)).first();

        if (userDoc == null) {
            mongoClient.close();
            return false; // User not found
        } else {
            String savedPassword = userDoc.getString("password");

            if (savedPassword.equals(password)) { 
                Document query = new Document("ID", userID);
                Document document = collection.find(query).first();
                String name = document.getString("name");
                System.out.println("Name: " + name);
                mongoClient.close();
                return true; // Login successful
            } else {
                mongoClient.close();
                return false; // Incorrect password
            }
        }
    }

    // public static String giveName(String userId){
    //     MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017"); // Connect to local MongoDB server
    //     MongoDatabase database = mongoClient.getDatabase(DB_NAME);
    //     MongoCollection<Document> collection = database.getCollection(USER_COLLECTION_NAME);
    //     return name;
    // }

    public static boolean createUser(String name, String email, String password) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017"); // Connect to local MongoDB server
        MongoDatabase database = mongoClient.getDatabase(DB_NAME);
        MongoCollection<Document> userCollection = database.getCollection(USER_COLLECTION_NAME);

        Document userDoc = userCollection.find(Filters.eq("email", email)).first();

        if (userDoc == null) {
            UUID userId = UUID.randomUUID();
            String userIdStr = userId.toString();
            userID=userIdStr;
            Document newUser = new Document("name", name)
                     .append("ID", userIdStr)
                    .append("email", email)
                    .append("password", password);
            userCollection.insertOne(newUser);

            mongoClient.close();
            return true; // User created successfully
        } else {
            mongoClient.close();
            return false; // User already exists
        }
    }
   public static void main(String[] args) {
    UserAuthentication hello=new UserAuthentication();
    boolean hi=hello.createUser("hello","hello@example.com", "password122323");
    hello.login("hello@example.com", "password122323");
    System.out.println(hi);


   }
}
