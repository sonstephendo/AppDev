package com.project.javafx.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.project.javafx.ulti.mongoDBUtil.MongoDBHandler;
import org.bson.Document;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractRepository<T, ID> extends MongoDBHandler implements GenericRepository<T, ID> {

    private final Class<T> clazz;
    private String collectionName;
    private String filePath;
    Set<T> objects = new HashSet<>();

    public AbstractRepository(Class<T> clazz, String collectionName, String filePath) {
        this.clazz = clazz;
        this.collectionName = collectionName;
        this.filePath = filePath;
    }

    protected Gson gsonCreator() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    public MongoCollection<Document> getCollection() {
        return mongoLoadCollectionTo(DB_NAME, collectionName);
    }

    protected abstract ID getID(T e);

    protected void getObjectCollection(Document query, Class<? extends T> aClass) {
        FindIterable<Document> cursor = getCollection().find(query);
        for (Document doc : cursor) {
            T o = gsonCreator().fromJson(doc.toJson(), aClass);
            objects.add(o);
        }
    }

    // đồng bộ = database với object
    public void getObjectCollection() {
        objects.clear();
        Document query = new Document();
        getObjectCollection(query, clazz);
    }

    protected void getObjectFromFile(Class<? extends T> aClass) {
        // read JSON file data as String

    }

    public void getObjectFromFile() {
        objects.clear();
        String fileData;
        try {
            fileData = new String(Files.readAllBytes(Paths.get(filePath)));
            // parse json string to object
            objects.addAll(gsonCreator().fromJson(fileData, setListType()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract Type setListType();

    public void saveObjectToFile() {
        // create JSON String from Object
        try {
            OutputStream stream = new FileOutputStream(new File(filePath));
            String json = gsonCreator().toJson(objects);
            stream.write(json.getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public T findById(ID id) {
        for (T obj : objects) {
            ID objID = getID(obj);
            if (objID.equals(id)) return obj;
        }
        return null;
    }

    @Override
    public Set<T> findAll() {
        return objects;
    }

    @Override
    public boolean existsById(ID id) {
        for (T obj : objects) {
            ID objID = getID(obj);
            if (objID.equals(id)) return true;
        }
        return false;
    }

    private void updateObject(T newObj, Document queryFindOld) {
        Gson gson = gsonCreator();
        Document first = getCollection().find(queryFindOld).first();

        String json2 = gson.toJson(newObj);
        Document doc2 = gson.fromJson(json2, Document.class);
        getCollection().replaceOne(first, doc2);
    }

    protected abstract Document findOldQuery(ID id);

    @Override
    public boolean update(T entity) {
        ID id = getID(entity);
        if (objects.contains(entity)) {
            updateObject(entity, findOldQuery(id));
            return true;
        }
        return false;
    }

    private void insertObject(T obj) {
        Gson gson = gsonCreator();
        String json = gson.toJson(obj);
        Document doc = gson.fromJson(json, Document.class);
        getCollection().insertOne(doc);
    }

    @Override
    public boolean save(T entity) {
        if (!existsById(getID(entity))) {
            objects.add(entity);
            insertObject(entity);
            System.out.println("Saved successfully");
            return true;

        }
        System.out.println("Saved failed");
        return false;
    }

    private void deleteObject(T obj) {
        Gson gson = gsonCreator();
        String json = gson.toJson(obj);
        Document doc = gson.fromJson(json, Document.class);
        getCollection().deleteOne(doc);
    }

    @Override
    public boolean delete(T entity) {
        if (objects.contains(entity)) {
            objects.remove(entity);
            deleteObject(entity);
            System.out.println("Delete successfully");
            return true;
        }
        System.out.println("Saved failed");
        return false;
    }

    @Override
    public boolean deleteByID(ID id) {
        for (T obj : objects) {
            ID objID = getID(obj);
            if (objID.equals(id)) {
                return delete(obj);
            }
        }
        return false;
    }
}
