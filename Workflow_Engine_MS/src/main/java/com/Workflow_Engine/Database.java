package com.Workflow_Engine;

import com.google.gson.JsonElement;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import static com.mongodb.client.model.Filters.eq;


public class Database {
    CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
    CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider)); //Todo

    MongoDatabase database;
    MongoCollection<Configdata> configdataMongoCollection;
    MongoCollection<Simulationresults> simulationresultsMongoCollection;
    MongoCollection<Configdata> configCollection;
    MongoCollection<AnalyserStati> analyserStatiMongoCollection;

    String uri = "mongodb://root:password@localhost:27017/?maxPoolSize=20&w=majority";

    public Database(){
        try (MongoClient mongoClient = MongoClients.create(this.uri)) {
            this.database = mongoClient.getDatabase("sample_pojos").withCodecRegistry(pojoCodecRegistry);
            this.configCollection = this.database.getCollection("configs", Configdata.class);
            this.simulationresultsMongoCollection = this.database.getCollection("simulationresults", Simulationresults.class);
            this.analyserStatiMongoCollection = this.database.getCollection("analyserstati", AnalyserStati.class);
        }
    }


    public void save_new_workflow(Configdata data){
        this.configCollection.insertOne(data);
        this.analyserStatiMongoCollection.insertOne(new AnalyserStati(data.id));
        this.simulationresultsMongoCollection.insertOne(new Simulationresults(data.id));
    }

    public AnalyserStati getAnalyserStatiByID(String id) {
        return this.analyserStatiMongoCollection.find(eq("id", id)).first();
    }


    public void update_simulationresuls(String id, Map<String, Double> simulationresults){
        List<Bson> updates = new LinkedList<>();
        simulationresults.forEach((k,v) -> updates.add(Updates.set(k, v))); //todo check if the typeconverting works as intended
        Bson update = Updates.combine(updates);
        this.simulationresultsMongoCollection.updateOne(eq("id", id), update);
    }


    public void update_analyserstatus(String id, String name, String status) {
        Bson update = Updates.set(name, status);
        this.analyserStatiMongoCollection.updateOne(eq("id", id), update);
    }
}
